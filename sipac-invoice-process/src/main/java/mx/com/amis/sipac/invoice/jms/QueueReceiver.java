package mx.com.amis.sipac.invoice.jms;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;

import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.domain.FacEstatusFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;
import mx.com.amis.sipac.invoice.reachcore.facade.ReachCoreFacade;
import mx.com.amis.sipac.invoice.reachcore.util.CfdiUtil;
import mx.gob.sat.cfdi.serializer.v33.CMoneda;
import mx.gob.sat.cfdi.serializer.v33.CTipoDeComprobante;
import mx.gob.sat.cfdi.serializer.v33.CUsoCFDI;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Emisor;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Receptor;

@Service
public class QueueReceiver {
  private static final Logger logger = LoggerFactory.getLogger(QueueReceiver.class);

  private CountDownLatch latch = new CountDownLatch(1);

  public CountDownLatch getLatch() {
    return latch;
  }
  
  @Value("${reachcore.emision.url}")
  private String reachCoreEmitUrl;
  
  @Autowired
  private InvoiceOrdersRepository repository;

  @KafkaListener(topics = "${kafka.topic.invoice}")
  public void receive(String message) {
    logger.info("received message='{}'", message);
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    try {
      processOrder(order);
      EmitirComprobanteResponse resp = processOrder(order);
      buildInvoiceMovement(order, resp, EstatusFacturacionEnum.FACTURA);
    } catch (Exception e) {
      e.printStackTrace();
      buildInvoiceError(order, EstatusFacturacionEnum.FACTURA);
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

    // TODO get correct values
    compr.setTipoDeComprobante(CTipoDeComprobante.T);
    compr.setMoneda(CMoneda.MXN);
    compr.setLugarExpedicion("08400");

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
    receptor.setUsoCFDI(CUsoCFDI.D_01); // TODO get correct value
    compr.setReceptor(receptor);

    Concepto concepto = new Concepto();
    concepto.setCantidad(new BigDecimal(1));
    concepto.setValorUnitario(new BigDecimal(order.getMonto()));
    concepto.setImporte(new BigDecimal(order.getMonto()));
    // TODO get correct values
    concepto.setUnidad("Unidad");
    concepto.setDescripcion("Descripcion");
    concepto.setClaveProdServ("01010101");
    concepto.setClaveUnidad("H87");

    Conceptos conceptos = new Conceptos();
    conceptos.getConcepto().add(concepto);
    compr.setConceptos(conceptos);

    //    Impuestos impuestos = new Impuestos();
    //    compr.setImpuestos(impuestos);

    return compr;
  }
  
  private FacMovimientoFacturacion buildInvoiceMovement(OrderToInvoice order, EmitirComprobanteResponse resp, EstatusFacturacionEnum status) {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setUuid(resp.getTransactionId());
    mov.setCfdiXml(resp.getResult());
    mov = repository.registerInvoiceMovement(mov);
    return mov;
  }
  
  private FacMovimientoError buildInvoiceError(OrderToInvoice order, EstatusFacturacionEnum status) {
    FacMovimientoError mov = new FacMovimientoError();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov = repository.registerInvoiceMovement(mov);
    return mov;
  }
}
