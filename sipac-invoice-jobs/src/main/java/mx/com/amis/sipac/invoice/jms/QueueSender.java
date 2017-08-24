package mx.com.amis.sipac.invoice.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.google.gson.Gson;

import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class QueueSender {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueueSender.class);

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;
  
  @Value("${kafka.topic.ordersToInvoice}")
  private String topic;

  public void send(OrderToInvoice order) {
    String message = new Gson().toJson(order);
    LOGGER.debug("message to send: " + message);
    LOGGER.debug("kafkaTemplate: " + kafkaTemplate);
    
    // the KafkaTemplate provides asynchronous send methods returning a Future
    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

    // register a callback with the listener to receive the result of the send asynchronously
    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
      @Override
      public void onSuccess(SendResult<String, String> result) {
        LOGGER.info("sent message='{}' with offset={}", message,
            result.getRecordMetadata().offset());
      }
      @Override
      public void onFailure(Throwable ex) {
        LOGGER.error("unable to send message='{}'", message, ex);
      }
    });
  }
}
