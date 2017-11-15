package mx.com.amis.sipac.invoice.jms;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

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

	@Autowired(required = true)
	private EmailQueueSender emailSender;

	@KafkaListener(topics = "${kafka.topic.invoice.email}")
	public void receive(String message) {
		if (message == null) {
			return;
		}
		logger.debug("received email message: " + message);
		OrderToInvoice order = GsonHelper.customGson.fromJson(message, OrderToInvoice.class);
		try {
			mailService.sendEmail(order);
		} catch (Exception e) {
			emailSender.send(order);
		}
		latch.countDown();
	}
}
