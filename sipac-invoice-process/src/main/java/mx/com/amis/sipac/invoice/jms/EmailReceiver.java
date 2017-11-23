package mx.com.amis.sipac.invoice.jms;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.service.MailService;
import mx.com.amis.sipac.utils.GsonHelper;

public class EmailReceiver {
	private static final Logger logger = LoggerFactory.getLogger(EmailReceiver.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	@Autowired 
	private MailService mailService;

//	@Autowired(required = true)
//	private EmailQueueSender emailSender;
	
	@Value("${local.vault.location}")
    private String vaultPath;

	@KafkaListener(topics = "${kafka.topic.invoice.email}")
	public void receive(String message) {
		if (message == null) {
			return;
		}
		logger.debug("received email message: " + message);
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		OrderToInvoice order = GsonHelper.customGson.fromJson(message, OrderToInvoice.class);
		order.setStartSendEmailDate(ts);
		try {
			mailService.sendEmail(order);
		} catch (Exception e) {
		  order.setError(order.getError() + " +++ " + e.getMessage());
		  e.printStackTrace();
			//emailSender.send(order);
		}
		ts = new Timestamp(System.currentTimeMillis());
		order.setEndDate(ts);
		
		appendTimesLog(order);
		
		latch.countDown();
	}
	
	private void appendTimesLog(OrderToInvoice order) {
	  String file = vaultPath + "/timesLog.csv";
	  logger.debug("Logging times at " + file);
	  Writer output = null;
	  try {
	    output = new BufferedWriter(new FileWriter(file, true));
	    output.append(order.getSiniestroId() + "," 
	        + order.getFolio() + "," 
	        + order.getEstatus() + ","
	        + EstatusFacturacionEnum.values()[order.getInvoiceStatus() - 1].name() + ","
	        + order.getStartDate() + ","
	        + order.getQueueDate() + ","
	        + order.getStartReachcoreDate() + ","
	        + order.getEndReachCoreDate() + ","
	        + order.getStartSendEmailDate() + ","
	        + order.getEndDate() + ","
	        + (order.getError() != null ? order.getError() : " ") + " \n");
	  }catch (Exception e) {
	    e.printStackTrace();
	  } finally {
	    if (output != null) {
	      try {
	        output.close();
	      } catch (Exception e) { }
	    }
	  }
	}
}
