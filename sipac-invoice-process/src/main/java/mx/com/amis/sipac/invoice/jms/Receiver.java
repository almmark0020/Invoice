package mx.com.amis.sipac.invoice.jms;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.google.gson.Gson;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;

import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.domain.FacEstatusFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.EmailToNotify;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;
import mx.com.amis.sipac.invoice.reachcore.facade.ReachCoreFacade;
import mx.com.amis.sipac.invoice.reachcore.util.CfdiUtil;
import mx.com.amis.sipac.service.MailService;
import mx.gob.sat.cfdi.serializer.v33.CMetodoPago;
import mx.gob.sat.cfdi.serializer.v33.CMoneda;
import mx.gob.sat.cfdi.serializer.v33.CTipoDeComprobante;
import mx.gob.sat.cfdi.serializer.v33.CUsoCFDI;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Complemento;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Emisor;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Receptor;
import mx.gob.sat.cfdi.serializer.v33.Pagos;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago.DoctoRelacionado;

public class Receiver {
  private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

  private CountDownLatch latch = new CountDownLatch(1);

  public CountDownLatch getLatch() {
    return latch;
  }

  @Value("${reachcore.emision.url}")
  private String reachCoreEmitUrl;

  @Value("${reachcore.cancelacion.url}")
  private String reachCoreCancelUrl;

  @Value("${reachcore.recuperacion.url}")
  private String reachCoreRetrieveUrl;
  
  @Value("${local.vault.location}")
  private String vaultPath;

  @Autowired private InvoiceOrdersRepository repository;
  @Autowired private MailService mailService;

  @KafkaListener(topics = "${kafka.topic.invoice}")
  public void receive(String message) {
    logger.info("received message='{}'", message);
    EmitirComprobanteResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora());
    try {
      resp = processOrder(order);
      if (resp != null && resp.getError() == null) {
        FacMovimientoFacturacion mov = buildInvoiceMovement(order, resp, EstatusFacturacionEnum.FACTURA);
        //this.mailService.send(emails, "SIPAC: Factura emitida", mailService.builEmailBody(order));
        logger.debug("before send email...");
        mailService.sendMessageWithAttachment(emails, "SIPAC: Factura Emitida", mailService.builEmailBody(order), mov.getCfdiXml().getBytes(), mov.getPdf());
      } else {
        buildInvoiceError(resp.getError().toString(), order, EstatusFacturacionEnum.FACTURA);
        mailService.send(emails, "SIPAC: Factura emitida", mailService.builEmailBody(order, resp.getError().toString()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(e.getMessage(), order, EstatusFacturacionEnum.FACTURA);
      // sendErrorEmail
    }
    latch.countDown();
  }

  @KafkaListener(topics = "${kafka.topic.invoice.complement}")
  public void receiveComplement(String message) {
    logger.info("received message complemento='{}'", message);
    EmitirComprobanteResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora());
    try {
      resp = processComplementOrder(order);
      if (resp != null && resp.getError() == null) {
        FacMovimientoFacturacion mov = buildInvoiceMovement(order, resp, EstatusFacturacionEnum.COMPLEMENTO);
        //this.mailService.send(emails, "SIPAC: Factura emitida", mailService.builEmailBody(order));
        logger.debug("before send email...");
        mailService.sendMessageWithAttachment(emails, "SIPAC: Complemento de Pago Registrado", mailService.builEmailBody(order), mov.getCfdiXml().getBytes(), mov.getPdf());
      } else {
        buildInvoiceError(resp.getError().toString(), order, EstatusFacturacionEnum.COMPLEMENTO);
        mailService.send(emails, "SIPAC: Error al registrar Complemento de Pago", mailService.builEmailBody(order, resp.getError().toString()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(e.getMessage(), order, EstatusFacturacionEnum.COMPLEMENTO);
      // sendErrorEmail
    }
    latch.countDown();
  }
  
  @KafkaListener(topics = "${kafka.topic.invoice.creditNote}")
  public void receiveCreditNote(String message) {
    logger.info("received message complemento='{}'", message);
    EmitirComprobanteResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora());
    try {
      resp = processCreditNoteOrder(order);
      if (resp != null && resp.getError() == null) {
        FacMovimientoFacturacion mov = buildInvoiceMovement(order, resp, EstatusFacturacionEnum.NOTA_CREDITO);
        logger.debug("before send email...");
        mailService.sendMessageWithAttachment(emails, "SIPAC: Nota deCrédito Registrada", mailService.builEmailBody(order), mov.getCfdiXml().getBytes(), mov.getPdf());
      } else {
        buildInvoiceError(resp.getError().toString(), order, EstatusFacturacionEnum.NOTA_CREDITO);
        mailService.send(emails, "SIPAC: Error al registrar Nota deCrédito", mailService.builEmailBody(order, resp.getError().toString()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(e.getMessage(), order, EstatusFacturacionEnum.NOTA_CREDITO);
      // sendErrorEmail
    }
    latch.countDown();
  }

  @KafkaListener(topics = "${kafka.topic.invoice.cancel}")
  public void receiveCancel(String message) {
    logger.info("received message='{}'", message);
    CancelacionFiscalResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora());
    try {
      resp = processCancelOrder(order);
      if (resp != null && !resp.isError()) {
        buildCancelInvoiceMovement(order, EstatusFacturacionEnum.CANCELACION);
        this.mailService.send(emails, "SIPAC: Factura cancelada", mailService.builEmailBody(order));
      } else {
        buildInvoiceError(resp.getErrorMessage(), order, EstatusFacturacionEnum.CANCELACION);
        this.mailService.send(emails, "SIPAC: Factura cancelada", mailService.builEmailBody(order, resp.getErrorMessage()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(e.getMessage(), order, EstatusFacturacionEnum.CANCELACION);
      // sendErrorEmail
    }
    latch.countDown();
  }

  private CancelacionFiscalResponse processCancelOrder(OrderToInvoice order) throws Exception {
    //String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreCancelUrl, order.getApiKey());
    CancelacionFiscalResponse resp = facade.cancelInvoice(order.getRfcAcreedora(), order.getId());
    return resp;
  }

  private FacMovimientoFacturacion buildCancelInvoiceMovement(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setUuid(order.getId());
    mov.setCfdiXml("X");
    mov = repository.registerInvoiceMovement(mov);
    return mov;
  }

  private EmitirComprobanteResponse processOrder(OrderToInvoice order) throws Exception {
    //String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = buildComprobante(order);
    EmitirComprobanteResponse resp;
    resp = facade.emitInvoice(compr);
    System.out.println("resp: " + resp);
    return resp;
  }

  private EmitirComprobanteResponse processComplementOrder(OrderToInvoice order) throws Exception {
    //String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = this.buildPaymentComplement(order);
    EmitirComprobanteResponse resp;
    resp = facade.emitInvoice(compr);
    System.out.println("resp: " + resp);
    return resp;
  }
  
  private EmitirComprobanteResponse processCreditNoteOrder(OrderToInvoice order) throws Exception {
    //String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = this.buildCreditNote(order);
    EmitirComprobanteResponse resp;
    resp = facade.emitInvoice(compr);
    System.out.println("resp: " + resp);
    return resp;
  }

  private Comprobante buildComprobante(OrderToInvoice order) {
    Comprobante compr =  new Comprobante();
    compr.setVersion("3.3");

    compr.setTipoDeComprobante(CTipoDeComprobante.I);
    compr.setMoneda(CMoneda.MXN);
    compr.setLugarExpedicion(order.getCp());

    compr.setFecha(CfdiUtil.getXMLGregorianCalendar());
    compr.setSubTotal(new BigDecimal(order.getMonto()));
    compr.setTotal(new BigDecimal(order.getMonto()));

    Emisor emisor = new Emisor();
    emisor.setRfc(order.getRfcAcreedora());
    emisor.setNombre(order.getRazonSocialAcreedora());
    emisor.setRegimenFiscal(order.getRegimeFiscalAcreedora());
    compr.setEmisor(emisor);

    Receptor receptor = new Receptor();
    receptor.setRfc(order.getRfcDeudora());
    receptor.setNombre(order.getRazonSocialDeudora());
    receptor.setUsoCFDI(CUsoCFDI.P_01);
    compr.setReceptor(receptor);

    Concepto concepto = new Concepto();
    concepto.setCantidad(new BigDecimal(1));
    concepto.setValorUnitario(new BigDecimal(order.getMonto()));
    concepto.setImporte(new BigDecimal(order.getMonto()));
    concepto.setUnidad("Unidad de Servicio");
    concepto.setDescripcion("Folio: " + order.getFolio() 
    + " - SIN DEUDOR: "  + order.getSiniestroDeudor() 
    + " - POLIZA DEUDOR: " + order.getPolizaDeudor()
    + " - SIN ACREEDOR: " + order.getSiniestroAcreedor()
    + " - POLIZA ACREEDOR: " + order.getPolizaAcreedor());
    concepto.setClaveProdServ("84131503");
    concepto.setClaveUnidad("E48");

    Conceptos conceptos = new Conceptos();
    conceptos.getConcepto().add(concepto);
    compr.setConceptos(conceptos);

    //    Impuestos impuestos = new Impuestos();
    //    compr.setImpuestos(impuestos);

    return compr;
  }

  private Comprobante buildPaymentComplement(OrderToInvoice order) {
    /*
    <pago10:Pagos Version="1.0">
    <pago10:Pago FechaPago="2017-08-30T16:34:26" FormaDePagoP="01" MonedaP="MXN" Monto="1285.45">
      <pago10:DoctoRelacionado IdDocumento="2F1275E0-1088-4B73-B250-718136D615C9" MonedaDR="MXN" MetodoDePagoDR="PUE" ImpSaldoAnt="1246.91" ImpPagado="1246.91" ImpSaldoInsoluto="0.00" />
    </pago10:Pago>
  </pago10:Pagos>
     */
    Comprobante compr = buildComprobante(order);
    compr.setMoneda(CMoneda.XXX);
    compr.setTipoDeComprobante(CTipoDeComprobante.P);
    compr.setTotal(new BigDecimal(0));
    compr.setSubTotal(new BigDecimal(0));
    compr.getConceptos().getConcepto().get(0).setImporte(new BigDecimal(0));
    compr.getConceptos().getConcepto().get(0).setValorUnitario(new BigDecimal(0));
    compr.getConceptos().getConcepto().get(0).setClaveProdServ("84111506");
    compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
    compr.getConceptos().getConcepto().get(0).setUnidad(null);
    compr.getConceptos().getConcepto().get(0).setDescripcion("Pago");

    Pagos compl = new Pagos();
    compl.setVersion("1.0");
    Pago paymt = new Pago();
    paymt.setFechaPago(CfdiUtil.getXMLGregorianCalendar());
    paymt.setFormaDePagoP("01");
    paymt.setMonedaP(CMoneda.MXN);
    paymt.setMonto(new BigDecimal(order.getMonto()));
    DoctoRelacionado doc = new DoctoRelacionado();
    doc.setIdDocumento(order.getId());
    doc.setMonedaDR(CMoneda.MXN);
    doc.setMetodoDePagoDR(CMetodoPago.PUE);
    doc.setImpSaldoAnt(new BigDecimal(order.getMonto()));
    doc.setImpPagado(new BigDecimal(order.getMonto()));
    doc.setImpSaldoInsoluto(new BigDecimal(0));
    paymt.getDoctoRelacionado().add(doc);
    compl.getPago().add(paymt);

    Complemento complemento = new Complemento();
    complemento.getAny().add(compl);
    compr.getComplemento().add(complemento);

    return compr;
  }
  
  private Comprobante buildCreditNote(OrderToInvoice order) {
    Comprobante compr = buildComprobante(order);
    compr.setTipoDeComprobante(CTipoDeComprobante.E);
    compr.getConceptos().getConcepto().get(0).setClaveProdServ("84111506");
    compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
    compr.getConceptos().getConcepto().get(0).setUnidad(null);
    CfdiRelacionados relatedInvoices = new CfdiRelacionados();
    relatedInvoices.setTipoRelacion("01");
    CfdiRelacionado relatedInvoice = new CfdiRelacionado();
    relatedInvoice.setUUID(order.getId());
    relatedInvoices.getCfdiRelacionado().add(relatedInvoice);
    compr.setCfdiRelacionados(relatedInvoices);
    return compr;
  }

  private FacMovimientoFacturacion buildInvoiceMovement(OrderToInvoice order, EmitirComprobanteResponse resp, EstatusFacturacionEnum status) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();

    Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreRetrieveUrl, order.getApiKey());
    logger.debug("before getting pdf..." + reachCoreRetrieveUrl);
    logger.debug("UUID: " + compr.getUUID());
    byte[] pdf = facade.getPdf(compr.getUUID()).getContents();
    logger.debug("pdf: " + pdf);
    mov.setPdf(pdf);
    
    String fileId = createVaultFiles(compr.getUUID(), resp.getResult().getBytes(), pdf);
    mov.setCfdiXml(fileId + ".xml");
    mov.setCfdiPdf(fileId + ".pdf");

    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));

    mov.setUuid(compr.getUUID());
    mov = repository.registerInvoiceMovement(mov);
    mov.setPdf(pdf);
    return mov;
  }
  
  private String createVaultFiles(String uuid, byte[] xml, byte[] pdf) {
    String fileId = vaultPath + File.separatorChar + uuid;
    try {
      FileUtils.writeByteArrayToFile(new File(fileId + ".xml"), xml);
      FileUtils.writeByteArrayToFile(new File(fileId + ".pdf"), pdf);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileId;
  }

  private FacMovimientoError buildInvoiceError(String errorMsg, OrderToInvoice order, EstatusFacturacionEnum status) {
    FacMovimientoError mov = new FacMovimientoError();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setMensajeError(errorMsg);
    mov = repository.registerInvoiceMovement(mov);
    return mov;
  }
}
