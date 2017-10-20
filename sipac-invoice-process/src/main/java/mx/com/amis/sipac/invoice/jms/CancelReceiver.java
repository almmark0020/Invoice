package mx.com.amis.sipac.invoice.jms;

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
import mx.com.amis.sipac.service.MailService;

public class CancelReceiver {
  private static final Logger logger = LoggerFactory.getLogger(CancelReceiver.class);

  private CountDownLatch latch = new CountDownLatch(1);

  public CountDownLatch getLatch() {
    return latch;
  }
  
  @Value("${reachcore.cancelacion.url}")
  private String reachCoreEmitUrl;
  
  @Autowired private InvoiceOrdersRepository repository;
  @Autowired private MailService mailService;

  @KafkaListener(topics = "${kafka.topic.invoice.cancel}")
  public void receive(String message) {
    logger.info("received message='{}'", message);
    CancelacionFiscalResponse resp = null;
    OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
    List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora(), order.getCiaDeudora());
    try {
      resp = processOrder(order);
      if (resp != null && !resp.isError()) {
        buildInvoiceMovement(order, EstatusFacturacionEnum.CANCELACION);
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

  private CancelacionFiscalResponse processOrder(OrderToInvoice order) throws Exception {
    //String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
    CancelacionFiscalResponse resp = facade.cancelInvoice(order.getRfcAcreedora(), order.getId());
    return resp;
  }
  
  private FacMovimientoFacturacion buildInvoiceMovement(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
    FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
    mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
    mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
    mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
    mov.setUuid(order.getId());
    mov.setCfdiXml("X");
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
