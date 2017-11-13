package mx.com.amis.sipac.service;

import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.model.EmailToNotify;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class MailService {
	private static final Logger logger = LoggerFactory.getLogger(MailService.class);

	@Autowired private JavaMailSender mailSender;

	@Value("${send.from.email}")
	private String fromEmail;

	@Value("${default.dest.email}")
	private String defaultEmail;
	
	@Value("${emulate.email}")
    private String emulateEmail;

	public String builEmailBody(OrderToInvoice order) {
		return builEmailBody(order, null);
	}

	public String builEmailBody(OrderToInvoice order, String errorMsg) {

		StringBuffer sb = new StringBuffer();
		if (errorMsg == null) {
			//sb.append("<h3>Factura Generada</h3>");
		} else {
			sb.append("<h3>Error al generar la factura: " + errorMsg + " </h3>");
		}
		sb.append("<table><tr>"
				+ "<th>Folio</th>"
				+ "<th>Siniestro Deudor</th>"
				+ "<th>Póliza Deudor</th>"
				+ "<th>Siniestro Acreedor</th>"
				+ "<th>Póliza Acreedor</th>"
				+ "<th>Fecha</th>"
				+ "<th>Monto</th>"
				+ "</tr>");
		sb.append("<tr><td>");
		sb.append(order.getFolio());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getSiniestroDeudor());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getPolizaDeudor());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getSiniestroAcreedor());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getPolizaAcreedor());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getFechaEstatus());
		sb.append("</td>");
		sb.append("<td>");
		sb.append(order.getMonto());
		sb.append("</td></tr>");
		sb.append("</table>");

		logger.debug(sb.toString());
		return sb.toString();
	}

	public String send(List<EmailToNotify> toEmail, String subject, String msgTxt) throws MessagingException {
	  if (emulateEmail.equals("true")) {
	    logger.debug("emulated email sent...");
	    return "OK";
	  }
		logger.debug("Starting Send...");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(getEmails(toEmail));
		helper.setFrom(this.fromEmail);
		helper.setSubject(subject);
		helper.setText(msgTxt, true);
		mailSender.send(message);
		for (int i = 0; i < 3; i++) {
			try {
				logger.debug("trying to send email [" + i + "]...");
				this.mailSender.send(message);
				logger.debug("Finish Send...");
				return "OK";
			} catch (Exception ex) {
				logger.debug("Error sending message: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		logger.debug("Finished Send...");
		return "OK";
	}
	
	public void sendMessageWithAttachment(
        List<EmailToNotify> toEmail, String subject, String text, byte[] xml, byte[] pdf) throws MessagingException {
	  sendMessageWithAttachment(toEmail, subject, text, xml, pdf, null);
	}

	public void sendMessageWithAttachment(
			List<EmailToNotify> toEmail, String subject, String text, byte[] xml, byte[] pdf, byte[] acuseSAT) throws MessagingException {
	  if (emulateEmail.equals("true")) {
        logger.debug("emulated email sent...");
        return;
      }
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(getEmails(toEmail));
		helper.setFrom(this.fromEmail);
		helper.setSubject(subject);
		helper.setText(text, true);

		logger.debug("xml to attach: " + xml);

		helper.addAttachment("PDF.pdf", new ByteArrayResource(pdf));
		helper.addAttachment("XML.xml", new ByteArrayResource(xml));
		if (acuseSAT != null) {
		  helper.addAttachment("AcuseSAT.xml", new ByteArrayResource(acuseSAT));
		}

		for (int i = 0; i < 5; i++) {
			logger.debug("trying to send email [" + i + "]...");
			try {
				mailSender.send(message);
				logger.debug("Finish Send...");
				return;
			} catch (Exception ex) {
				logger.debug("Error sending message: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

	private String[] getEmails(List<EmailToNotify> emailsToNotify) {
		List<String> emails = new ArrayList<String>();
		if (emailsToNotify == null || emailsToNotify.isEmpty()) {
			emails.add(this.defaultEmail);
		} else {
			for(EmailToNotify email : emailsToNotify) {
				if (email == null) {
					continue;
				}
				String[] addresses = email.getEmail().split(",");
				for (String address : addresses) {
					emails.add(address);
				}
			}
		}
		return emails.toArray(new String[emails.size()]);
	}
}
