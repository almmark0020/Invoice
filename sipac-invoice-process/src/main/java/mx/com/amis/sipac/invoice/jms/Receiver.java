package mx.com.amis.sipac.invoice.jms;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.google.gson.Gson;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;

import mx.com.amis.sipac.invoice.persistence.domain.Compania;
import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.domain.FacEstatusFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
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

  @Value("${inv.doc.claveProducto}")
  private String cveProducto;

  @Value("${inv.doc.descripcion}")
  private String invDescription;

  @Autowired 
  private MailService mailService;

  @Autowired private InvoiceOrdersRepository repository;
  //	@Autowired private MailService mailService;

//  @Autowired(required = true)
//  private EmailQueueSender emailSender;

  @KafkaListener(topics = "${kafka.topic.invoice}")
  public void receive(String message) {
    processMessage(message, EstatusFacturacionEnum.FACTURA);
  }

  @KafkaListener(topics = "${kafka.topic.invoice.complement}")
  public void receiveComplement(String message) {
    processMessage(message, EstatusFacturacionEnum.COMPLEMENTO);
  }

  @KafkaListener(topics = "${kafka.topic.invoice.creditNote}")
  public void receiveCreditNote(String message) {
    processMessage(message, EstatusFacturacionEnum.NOTA_CREDITO);
  }

  @KafkaListener(topics = "${kafka.topic.invoice.automaticAcceptance}")
  public void receiveAutomaticAcceptance(String message) {
    processMessage(message, EstatusFacturacionEnum.FACTURA);
  }

  @KafkaListener(topics = "${kafka.topic.invoice.cancel}")
  public void receiveCancel(String message) {
    OrderToInvoice order = buildOrder(message, EstatusFacturacionEnum.CANCELACION);
    if (order == null) {
      return;
    }
    try {
      CancelacionFiscalResponse resp = processCancelOrder(order);
      logger.debug("Reachcore response: " + resp);
      Timestamp ts = new Timestamp(System.currentTimeMillis());
      order.setEndReachCoreDate(ts);
      if (resp != null && !resp.isError()) {
        order = buildCancelInvoiceMovement(order, EstatusFacturacionEnum.CANCELACION, resp.getAcuseSAT());
        //				emailSender.send(order);
        mailService.sendEmail(order);
      } else {
        buildInvoiceError(ReachCoreFacade.getErrorCode(resp), ReachCoreFacade.getErrorMessage(resp), order, EstatusFacturacionEnum.CANCELACION);
        order.setError(ReachCoreFacade.getErrorCode(resp) + ReachCoreFacade.getErrorMessage(resp) + "<br/>" + resp.getErrorMessage());
        notifyError(order);
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError("0", "Ocurrió un error en el proceso de facturación. Por favor contacte al administrador: " + e.getMessage(), order, EstatusFacturacionEnum.CANCELACION);
    }
    latch.countDown();
  }

  private void processMessage(String message, EstatusFacturacionEnum status) {
    OrderToInvoice order = buildOrder(message, status);
    if (order == null) {
      return;
    }
    try {
      EmitirComprobanteResponse resp = process(order, status);
      logger.debug("Reachcore response: " + resp);
      Timestamp ts = new Timestamp(System.currentTimeMillis());
      order.setEndReachCoreDate(ts);
      if (resp != null && !ReachCoreFacade.hasErrors(resp)) {
        order = buildInvoiceMovement(order, resp, status);
        //				emailSender.send(order);
        mailService.sendEmail(order);
      } else {
        buildInvoiceError(ReachCoreFacade.getErrorCode(resp), ReachCoreFacade.getErrorMessage(resp), order, status);
        order.setError(ReachCoreFacade.getErrorCode(resp) + ReachCoreFacade.getErrorMessage(resp) + "<br/>" + resp.getError().toString());
        notifyError(order);
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError("0", "Ocurrió un error en el proceso de facturación. Por favor contacte al administrador: " + e.getMessage(), order, status);
    }
    latch.countDown();
  }

  private void notifyError(OrderToInvoice order) throws InterruptedException {
    int retryAttempts = repository.getErrorRetryAttempts(order);
    if (retryAttempts >= 10) {
      //emailSender.send(order);
      mailService.sendEmail(order);
    }
  }

  private OrderToInvoice buildOrder(String message, EstatusFacturacionEnum status) {
    logger.info("received message='{}'", message);
    if (message == null || message.equals("null")) {
      return null;
    }
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    order.setStartReachcoreDate(ts);
    order.setInvoiceStatus(status.getEstatusId());
    String apiKey = repository.getApiKey(order);
    logger.debug("apikey: " + apiKey);
    order.setApiKey(apiKey);
    if (repository.isAlreadyInvoiced(order, status)) {
      logger.info("This order was already processed.");
      return null;
    }
    if (status == EstatusFacturacionEnum.FACTURA) {
      Long id = registerInvoiceOrder(order);
      order.setInvoiceOrderId(id);
    }
    int retryAttempts = repository.getErrorRetryAttempts(order);
    if (retryAttempts >= 10) {
      logger.info("This order has been reached the limits of retry attempts to be processed.");
      return null;
    }
    return order;
  }

  private Long registerInvoiceOrder(OrderToInvoice orderToInv) {
    FacOrdenFacturada order = new FacOrdenFacturada();
    order.setFolio(orderToInv.getFolio());
    order.setIdSiniestro(orderToInv.getSiniestroId());
    order.setTipoOrden(orderToInv.getTipoOrden());
    order.setCompania1(new Compania(orderToInv.getCiaAcreedora()));
    order.setCompania2(new Compania(orderToInv.getCiaDeudora()));
    order.setFechaEstatusSipac(orderToInv.getFechaEstatus());
    order.setEstatusSipac(orderToInv.getEstatus());

    order.setSiniestroAcreedor(orderToInv.getSiniestroAcreedor());
    order.setSiniestroDeudor(orderToInv.getSiniestroDeudor());
    order.setSiniestroCorrecto(orderToInv.getSiniestroCorrecto());
    order.setPolizaAcreedor(orderToInv.getPolizaAcreedor());
    order.setPolizaDeudor(orderToInv.getPolizaDeudor());
    order.setMonto(orderToInv.getMonto());
    order.setRfcAcreedora(orderToInv.getRfcAcreedora());
    order.setRfcDeudora(orderToInv.getRfcDeudora());
    order.setRazonsocialAcreedora(orderToInv.getRazonSocialAcreedora());
    order.setRazonSocialDeudora(orderToInv.getRazonSocialDeudora());
    order.setRegimenfiscalAcreedora(orderToInv.getRegimeFiscalAcreedora());
    order.setRegimenFiscalDeudora(orderToInv.getRegimeFiscalDeudora());
    order.setCpAcreedora(orderToInv.getCp());
    order.setNombreCiaAcreedora(orderToInv.getRazonSocialAcreedora());
    order.setNombreCiaDeudora(orderToInv.getRazonSocialDeudora());

    order.setCapturado(orderToInv.getCapturado());
    order.setCircunstanciaAcreedor(orderToInv.getCircunstanciaAcreedor());
    order.setCircunstanciaDeudor(orderToInv.getCircunstanciaDeudor());
    order.setContraparte(orderToInv.getContraparte());
    order.setCosto(orderToInv.getCosto());
    order.setDias(orderToInv.getDias());
    order.setEstado(orderToInv.getEstado());
    order.setFechaAceptacion(orderToInv.getFechaAceptacion());
    order.setFechaConfirmacionPago(orderToInv.getFechaConfirmacionPago());
    order.setFechaExpedicion(orderToInv.getFechaExpedicion());
    order.setFechaPago(orderToInv.getFechaPago());
    order.setFechaPrimerRechazo(orderToInv.getFechaPrimerRechazo());
    order.setFechaRegistro(orderToInv.getFechaRegistro());
    order.setFechaSiniestro(orderToInv.getFechaSiniestro());
    order.setModificado(orderToInv.getModificado());
    order.setMotivo(orderToInv.getMotivo());
    order.setMunicipio(orderToInv.getMunicipio());
    order.setObservacionesAcreedor(orderToInv.getObservacionesAcreedor());
    order.setObservacionesComite(orderToInv.getObservacionesComite());
    order.setObservacionesDeudor(orderToInv.getObservacionesDeudor());
    order.setSancion(orderToInv.getSancion());
    order.setTipoCaptura(orderToInv.getTipoCaptura());
    order.setTipoTransporteAcreedor(orderToInv.getTipoTransporteAcreedor());
    order.setTipoTransporteDeudor(orderToInv.getTipoTransporteDeudor());

    order = repository.registerInvoicedOrder(order);
    return order.getIdOrdenFacturada();
  }

  private EmitirComprobanteResponse process(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
    switch(status){
    case FACTURA:
      return processOrder(order);
    case COMPLEMENTO:
      return processComplementOrder(order);
    case NOTA_CREDITO:
      return processCreditNoteOrder(order);
    case EN_PROCESO:
    case CANCELACION:
      return null;
    }
    return null;
  }

  private CancelacionFiscalResponse processCancelOrder(OrderToInvoice order) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreCancelUrl, order.getApiKey());
    CancelacionFiscalResponse resp = facade.cancelInvoice(order.getRfcAcreedora(), order.getId());
    return resp;
  }

  private EmitirComprobanteResponse processOrder(OrderToInvoice order) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = buildComprobante(order);
    return facade.emitInvoice(compr);
  }

  private EmitirComprobanteResponse processComplementOrder(OrderToInvoice order) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = this.buildPaymentComplement(order);
    return facade.emitInvoice(compr);
  }

  private EmitirComprobanteResponse processCreditNoteOrder(OrderToInvoice order) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    Comprobante compr = this.buildCreditNote(order);
    return facade.emitInvoice(compr);
  }

  private Comprobante buildComprobante(OrderToInvoice order) {
    Comprobante compr =  new Comprobante();
    compr.setVersion("3.3");

    compr.setTipoDeComprobante(CTipoDeComprobante.I);
    compr.setMoneda(CMoneda.MXN);
    compr.setLugarExpedicion(order.getCp());
    compr.setFormaPago("03");

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
    receptor.setUsoCFDI(CUsoCFDI.G_03);
    compr.setReceptor(receptor);

    Concepto concepto = new Concepto();
    concepto.setCantidad(new BigDecimal(1));
    concepto.setValorUnitario(new BigDecimal(order.getMonto()));
    concepto.setImporte(new BigDecimal(order.getMonto()));
    concepto.setUnidad("Unidad de Servicio");
    concepto.setDescripcion(getDescription(order));
    concepto.setClaveProdServ(cveProducto);
    concepto.setClaveUnidad("C62"); // E48

    Conceptos conceptos = new Conceptos();
    conceptos.getConcepto().add(concepto);
    compr.setConceptos(conceptos);
    return compr;
  }

  private String getDescription(OrderToInvoice order) {
    String desc = "INDEMNIZACION DE LA RECUPERACION DE SINIESTROS MODALIDAD SIPAC PERCEPCION DE LA INDEMNIZACION DE LA RECUPRACION ASOCIADA AL : ";
    desc += "Siniestro Acreedor: " + order.getSiniestroAcreedor();
    desc += ", Poliza Acreedor: " + order.getPolizaAcreedor();
    if (!StringUtils.isEmpty(order.getSiniestroCorrecto())) {
      desc += ", Siniestro Correcto: " + order.getSiniestroCorrecto();
    } else {
      desc += ", Siniestro Deudor: " + order.getSiniestroDeudor();
    }
    desc += ", Poliza Deudor: " + order.getPolizaDeudor();
    desc += ", Actividad no objeto de IVA";
    return desc;
  }

  private Comprobante buildPaymentComplement(OrderToInvoice order) {
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
    compr.getReceptor().setUsoCFDI(CUsoCFDI.P_01);
    compr.setFormaPago(null);

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
    compr.getConceptos().getConcepto().get(0).setUnidad(null);
    CfdiRelacionados relatedInvoices = new CfdiRelacionados();
    relatedInvoices.setTipoRelacion("01");
    CfdiRelacionado relatedInvoice = new CfdiRelacionado();
    relatedInvoice.setUUID(order.getId());
    relatedInvoices.getCfdiRelacionado().add(relatedInvoice);
    compr.setCfdiRelacionados(relatedInvoices);
    return compr;
  }

  private OrderToInvoice buildInvoiceMovement(OrderToInvoice order, EmitirComprobanteResponse resp, EstatusFacturacionEnum status) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();

    Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
    byte[] pdf = retrievePdf(order.getApiKey(), compr.getUUID());

    String fileId = createVaultFiles(compr.getUUID(), resp.getResult().getBytes(), pdf);
    mov.setCfdiXml(fileId + ".xml");
    mov.setCfdiPdf(fileId + ".pdf");

    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));

    mov.setUuid(compr.getUUID());
    mov = repository.registerInvoiceMovement(order, mov);

    order.setPdf(pdf);
    order.setXml(resp.getResult().getBytes());
    return order;
  }

  private OrderToInvoice buildCancelInvoiceMovement(OrderToInvoice order, EstatusFacturacionEnum status, String acuseSAT) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setUuid(order.getId());
    mov = repository.registerInvoiceMovement(order, mov);
    order.setPdf(this.retrievePdf(order.getApiKey(), order.getId()));
    order.setXml(this.retrieveXml(order.getApiKey(), order.getId()));
    if (acuseSAT != null) {
      byte[] acuse = acuseSAT.getBytes(StandardCharsets.UTF_8);
      createVaultFiles(order.getId(), acuse);
      order.setAcuseSAT(acuse);
    }
    return order;
  }

  private FacMovimientoError buildInvoiceError(String errorCode, String errorMsg, OrderToInvoice order, EstatusFacturacionEnum status) {
    FacMovimientoError mov = new FacMovimientoError();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setCodigoError(errorCode);
    mov.setMensajeError(errorMsg);
    mov = repository.registerInvoiceMovement(mov);
    return mov;
  }

  private byte[] retrievePdf(String apiKey, String uuid) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreRetrieveUrl, apiKey);
    return facade.getPdf(uuid).getContents();
  }

  private byte[] retrieveXml(String apiKey, String uuid) throws Exception {
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreRetrieveUrl, apiKey);
    return facade.getXml(uuid).getContents();
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

  private String createVaultFiles(String uuid, byte[] acuseSAT) {
    String fileId = vaultPath + File.separatorChar + uuid;
    try {
      FileUtils.writeByteArrayToFile(new File(fileId + "-acuseSAT.xml"), acuseSAT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileId;
  }
}
