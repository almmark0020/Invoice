package mx.com.amis.sipac.job;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.jms.QueueSender;
import mx.com.amis.sipac.invoice.persistence.domain.Compania;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;

@Service("jobsRegManageOrders")
public class JobsRegManageOrders {
  private static final Logger logger = LoggerFactory.getLogger(QueueSender.class);
  
  @Autowired(required = true)
  private InvoiceOrdersRepository repository;

  public void procesaRegistrado() {
    logger.debug("Start quartz process...");
    List<OrderToInvoice> orders = repository.getOrdersToInvoice();
    QueueSender sender = new QueueSender();
    for(OrderToInvoice order : orders) {
      registerInvoiceOrder(order);
      sender.send(order);
    }
    logger.debug("End quartz process.");
  }
  
  private void registerInvoiceOrder(OrderToInvoice orderToInv) {
    FacOrdenFacturada order = new FacOrdenFacturada();
    order.setFolio(orderToInv.getFolio());
    order.setIdSiniestro(orderToInv.getSiniestroId());
    order.setTipoOrden(orderToInv.getTipoOrden());
    order.setCompania1(new Compania(orderToInv.getCiaAcreedora()));
    order.setCompania2(new Compania(orderToInv.getCiaDeudora()));
    this.repository.registerInvoicedOrder(order);
  }
}
