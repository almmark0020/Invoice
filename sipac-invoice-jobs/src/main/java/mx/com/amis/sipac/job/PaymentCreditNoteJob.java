package mx.com.amis.sipac.job;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.jms.Sender;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;

@Service("paymentCreditNoteJob")
public class PaymentCreditNoteJob {
  private static final Logger logger = LoggerFactory.getLogger(PaymentCreditNoteJob.class);
  
  @Value("${kafka.topic.invoice.creditNote}")
  private String topic;
  
  @Autowired(required = true)
  private InvoiceOrdersRepository repository;
  
  @Autowired(required = true)
  private Sender sender;

  public void process() {
    logger.debug("Start quartz process...");
    Timestamp ts = new Timestamp(System.currentTimeMillis());
    List<OrderToInvoice> orders = repository.getRefundOrdersToInvoice();
    for(OrderToInvoice order : orders) {
      order.setStartDate(ts);
      sender.send(order, topic);
    }
    logger.debug("End quartz process.");
  }
}
