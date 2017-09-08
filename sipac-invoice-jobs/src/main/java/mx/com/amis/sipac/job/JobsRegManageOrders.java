package mx.com.amis.sipac.job;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.jms.Sender;
import mx.com.amis.sipac.invoice.persistence.domain.Compania;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;
import mx.com.amis.sipac.invoice.persistence.repository.InvoiceOrdersRepository;

@Service("jobsRegManageOrders")
public class JobsRegManageOrders {
	private static final Logger logger = LoggerFactory.getLogger(JobsRegManageOrders.class);

	@Value("${kafka.topic.invoice}")
	private String topic;

	@Autowired(required = true)
	private InvoiceOrdersRepository repository;

	@Autowired(required = true)
	private Sender sender;

	public void procesaRegistrado() {
		logger.debug("Start quartz process...");
		List<OrderToInvoice> orders = repository.getAcceptedOrdersToInvoice();
		for(OrderToInvoice order : orders) {
			Long id = registerInvoiceOrder(order);
			order.setInvoiceOrderId(id);
			sender.send(order, topic);
		}
		//    sendMock();
		logger.debug("End quartz process.");
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
		order = repository.registerInvoicedOrder(order);
		return order.getIdOrdenFacturada();
	}

	@SuppressWarnings("unused")
	private void sendMock() {
		OrderToInvoice order = buildMockOrder();
		Long id = registerInvoiceOrder(order);
		order.setInvoiceOrderId(id);
		sender.send(order, "sipac.invoice.creditNote");
	}

	private OrderToInvoice buildMockOrder() {
		OrderToInvoice order = new OrderToInvoice();
		order.setApiKey("h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy");
		order.setId("6FBD51B3-1014-40E6-988E-3C9E60575235");
		order.setCiaAcreedora(1);
		order.setCp("08400");
		order.setCiaDeudora(2);
		order.setEstatus("1");
		order.setFechaEstatus(new Timestamp(new Date().getTime()));
		order.setFolio("TEST00");
		order.setMonto(1000d);
		order.setRazonSocialAcreedora("ACCEM SERVICIOS EMPRESARIALES SC");
		order.setRazonSocialDeudora("MARCO ANTONIO LOPEZ VARGAS");
		order.setRegimeFiscalAcreedora("601");
		order.setRegimeFiscalDeudora("601");
		order.setRfcAcreedora("AAA010101AAA");
		order.setRfcDeudora("LOVM840920DI9");
		order.setSiniestroId(1);
		order.setTipoOrden("D");
		return order;
	}
}
