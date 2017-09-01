package mx.com.amis.sipac.invoice.jms;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.google.gson.Gson;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;

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
import mx.gob.sat.cfdi.serializer.v33.CMoneda;
import mx.gob.sat.cfdi.serializer.v33.CTipoDeComprobante;
import mx.gob.sat.cfdi.serializer.v33.CUsoCFDI;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Emisor;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Receptor;

public class ComplementReceiver {
  private static final Logger logger = LoggerFactory.getLogger(ComplementReceiver.class);

  private CountDownLatch latch = new CountDownLatch(1);

  public CountDownLatch getLatch() {
    return latch;
  }
  
  @Value("${reachcore.emision.url}")
  private String reachCoreEmitUrl;
  
  @Autowired private InvoiceOrdersRepository repository;
  @Autowired private MailService mailService;

  @KafkaListener(topics = "${kafka.topic.invoice.complement}")
  public void receive(String message) {
    logger.info("received message='{}'", message);
    EmitirComprobanteResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora());
    try {
      resp = processOrder(order);
      if (resp != null && resp.getError() == null) {
        buildInvoiceMovement(order, resp, EstatusFacturacionEnum.COMPLEMENTO);
        this.mailService.send(emails, "SIPAC: Factura emitida", mailService.builEmailBody(order));
      } else {
        buildInvoiceError(resp.getError().toString(), order, EstatusFacturacionEnum.COMPLEMENTO);
        this.mailService.send(emails, "SIPAC: Factura emitida", mailService.builEmailBody(order, resp.getError().toString()));
      }
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(e.getMessage(), order, EstatusFacturacionEnum.COMPLEMENTO);
      // sendErrorEmail
    }
    latch.countDown();
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
  
  private FacMovimientoFacturacion buildInvoiceMovement(OrderToInvoice order, EmitirComprobanteResponse resp, EstatusFacturacionEnum status) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
    mov.setUuid(compr.getComplemento().get(0).getUUID());
    mov.setCfdiXml(resp.getResult());
    mov = repository.registerInvoiceMovement(mov);
    return mov;
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
