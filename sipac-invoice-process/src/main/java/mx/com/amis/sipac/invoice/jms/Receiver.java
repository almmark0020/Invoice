package mx.com.amis.sipac.invoice.jms;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;

import com.google.gson.Gson;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;

import mx.com.amis.sipac.invoice.persistence.domain.Compania;
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
import mx.gob.sat.cfdi.serializer.v33.CMetodoPago;
import mx.gob.sat.cfdi.serializer.v33.CMoneda;
import mx.gob.sat.cfdi.serializer.v33.CTipoDeComprobante;
import mx.gob.sat.cfdi.serializer.v33.CUsoCFDI;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Complemento;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Emisor;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Receptor;
import mx.gob.sat.cfdi.serializer.v33.Pagos;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago.DoctoRelacionado;

public class Receiver {
	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

	private CountDownLatch latch = new CountDownLatch(1);

	public CountDownLatch getLatch() {
		return latch;
	}

	@Value("${reachcore.emision.url}")
	private String reachCoreEmitUrl;

	@Value("${reachcore.cancelacion.url}")
	private String reachCoreCancelUrl;

	@Value("${reachcore.recuperacion.url}")
	private String reachCoreRetrieveUrl;

	@Value("${local.vault.location}")
	private String vaultPath;

	@Value("${inv.doc.claveProducto}")
	private String cveProducto;

	@Value("${inv.doc.descripcion}")
	private String invDescription;

	@Autowired private InvoiceOrdersRepository repository;
	@Autowired private MailService mailService;

	@KafkaListener(topics = "${kafka.topic.invoice}")
	public void receive(String message) {
		processMessage(message, EstatusFacturacionEnum.FACTURA);
	}

	@KafkaListener(topics = "${kafka.topic.invoice.complement}")
	public void receiveComplement(String message) {
		processMessage(message, EstatusFacturacionEnum.COMPLEMENTO);
	}

	@KafkaListener(topics = "${kafka.topic.invoice.creditNote}")
	public void receiveCreditNote(String message) {
		processMessage(message, EstatusFacturacionEnum.NOTA_CREDITO);
	}

	@KafkaListener(topics = "${kafka.topic.invoice.cancel}")
	public void receiveCancel(String message) {
		logger.info("received message='{}'", message);
		if (message == null || message.equals("null")) {
			return;
		}
		CancelacionFiscalResponse resp = null;
		OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
		if (repository.isAlreadyInvoiced(order, EstatusFacturacionEnum.CANCELACION)) {
			logger.info("This order was already processed.");
			return;
		}
		List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora(), order.getCiaDeudora());
		try {
			resp = processCancelOrder(order);
			if (resp != null && !resp.isError()) {
				FacMovimientoFacturacion mov = buildCancelInvoiceMovement(order, EstatusFacturacionEnum.CANCELACION);
				mailService.sendMessageWithAttachment(emails, getSubject(order, EstatusFacturacionEnum.CANCELACION), mailService.builEmailBody(order), mov.getXml(), mov.getPdf());
			} else {
				buildInvoiceError(ReachCoreFacade.getErrorCode(resp), ReachCoreFacade.getErrorMessage(resp), order, EstatusFacturacionEnum.CANCELACION);
				this.mailService.send(emails, "ERROR: " + getSubject(order, EstatusFacturacionEnum.CANCELACION), mailService.builEmailBody(order, ReachCoreFacade.getErrorCode(resp) + ReachCoreFacade.getErrorMessage(resp) + "<br/>" + resp.getErrorMessage()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			//buildInvoiceError("0", e.getMessage(), order, EstatusFacturacionEnum.CANCELACION);
			buildInvoiceError("0", "Ocurrió un error en el proceso de facturación. Por favor contacte al administrador: " + e.getMessage(), order, EstatusFacturacionEnum.CANCELACION);
		}
		latch.countDown();
	}

	private void processMessage(String message, EstatusFacturacionEnum status) {
		logger.info("received message='{}'", message);
		if (message == null || message.equals("null")) {
			return;
		}
		EmitirComprobanteResponse resp = null;
		OrderToInvoice order = new Gson().fromJson(message, OrderToInvoice.class);
		if (repository.isAlreadyInvoiced(order, status)) {
			logger.info("This order was already processed.");
			return;
		}
		if (status == EstatusFacturacionEnum.FACTURA) {
			Long id = registerInvoiceOrder(order);
			order.setInvoiceOrderId(id);
			String apiKey = repository.getApiKey(order);
			logger.debug("apikey: " + apiKey);
			order.setApiKey(apiKey);
		} else if (status == EstatusFacturacionEnum.NOTA_CREDITO) {
			logger.info("This order was already processed with errors.");
			if (repository.isAlreadyOnError(order, status)) {
				return;
			}
		}
		List<EmailToNotify> emails = repository.getEmails(order.getCiaAcreedora(), order.getCiaDeudora());
		try {
			resp = process(order, status);
			if (resp != null && !ReachCoreFacade.hasErrors(resp)) {
				FacMovimientoFacturacion mov = buildInvoiceMovement(order, resp, status);
				logger.debug("before send email..." + mov.getXml());
				mailService.sendMessageWithAttachment(emails, getSubject(order, status), mailService.builEmailBody(order), mov.getXml(), mov.getPdf());
			} else {
				buildInvoiceError(ReachCoreFacade.getErrorCode(resp), ReachCoreFacade.getErrorMessage(resp), order, status);
				mailService.send(emails, "ERROR " + getSubject(order, status), mailService.builEmailBody(order, ReachCoreFacade.getErrorCode(resp) + ReachCoreFacade.getErrorMessage(resp) + "<br/>" + resp.getError().toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			//			buildInvoiceError("0", e.getMessage(), order, status);
			buildInvoiceError("0", "Ocurrió un error en el proceso de facturación. Por favor contacte al administrador: " + e.getMessage(), order, status);
			// sendErrorEmail
		}
		latch.countDown();
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

	private String getSubject(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
		String subject = "";
		switch(status){
		case FACTURA:
			subject = "Facturación SIPAC: Factura - ";
			break;
		case COMPLEMENTO:
			subject = "Facturación SIPAC: Complemento de Pago - ";
			break;
		case NOTA_CREDITO:
			subject = "Facturación SIPAC: Nota de Crédito - ";
			break;
		case CANCELACION:
			subject = "Facturación SIPAC: Factura Cancelada - ";
			break;
		default:
			return "";
		}
		return subject + "FOLIO: " + order.getFolio() 
		+ ", SINIESTRO DEUDOR: " + order.getSiniestroDeudor() 
		+ ", SINIESTRO ACREEDOR: " + order.getSiniestroAcreedor() 
		+ ", CIA DEUDORA: " + order.getCiaDeudora() 
		+ ", CIA ACREEDORA: " + order.getCiaAcreedora();
	}

	private EmitirComprobanteResponse process(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
		switch(status){
		case FACTURA:
			return processOrder(order);
		case COMPLEMENTO:
			return processComplementOrder(order);
		case NOTA_CREDITO:
			return processCreditNoteOrder(order);
		case EN_PROCESO:
		case CANCELACION:
			return null;
		}
		return null;
	}

	private CancelacionFiscalResponse processCancelOrder(OrderToInvoice order) throws Exception {
		//String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreCancelUrl, order.getApiKey());
		CancelacionFiscalResponse resp = facade.cancelInvoice(order.getRfcAcreedora(), order.getId());
		return resp;
	}

	private FacMovimientoFacturacion buildCancelInvoiceMovement(OrderToInvoice order, EstatusFacturacionEnum status) throws Exception {
		FacMovimientoFacturacion mov = new FacMovimientoFacturacion();
		mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
		mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
		mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
		mov.setUuid(order.getId());
		mov = repository.registerInvoiceMovement(mov);
		mov.setPdf(this.retrievePdf(order.getApiKey(), order.getId()));
		mov.setXml(this.retrieveXml(order.getApiKey(), order.getId()));
		return mov;
	}

	private EmitirComprobanteResponse processOrder(OrderToInvoice order) throws Exception {
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
		Comprobante compr = buildComprobante(order);
		return facade.emitInvoice(compr);
	}

	private EmitirComprobanteResponse processComplementOrder(OrderToInvoice order) throws Exception {
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
		Comprobante compr = this.buildPaymentComplement(order);
		return facade.emitInvoice(compr);
	}

	private EmitirComprobanteResponse processCreditNoteOrder(OrderToInvoice order) throws Exception {
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreEmitUrl, order.getApiKey());
		Comprobante compr = this.buildCreditNote(order);
		return facade.emitInvoice(compr);
	}

	private Comprobante buildComprobante(OrderToInvoice order) {
		Comprobante compr =  new Comprobante();
		compr.setVersion("3.3");

		compr.setTipoDeComprobante(CTipoDeComprobante.I);
		compr.setMoneda(CMoneda.MXN);
		compr.setLugarExpedicion(order.getCp());
		compr.setFormaPago("03");

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
		receptor.setUsoCFDI(CUsoCFDI.G_03);
		compr.setReceptor(receptor);

		Concepto concepto = new Concepto();
		concepto.setCantidad(new BigDecimal(1));
		concepto.setValorUnitario(new BigDecimal(order.getMonto()));
		concepto.setImporte(new BigDecimal(order.getMonto()));
		concepto.setUnidad("Unidad de Servicio");
		//    concepto.setDescripcion("Folio: " + order.getFolio() 
		//    + " - SIN DEUDOR: "  + order.getSiniestroDeudor() 
		//    + " - POLIZA DEUDOR: " + order.getPolizaDeudor()
		//    + " - SIN ACREEDOR: " + order.getSiniestroAcreedor()
		//    + " - POLIZA ACREEDOR: " + order.getPolizaAcreedor());
		concepto.setDescripcion(getDescription(order));
		concepto.setClaveProdServ(cveProducto);
		concepto.setClaveUnidad("C62"); // E48

		Conceptos conceptos = new Conceptos();
		conceptos.getConcepto().add(concepto);
		compr.setConceptos(conceptos);

		//    Impuestos impuestos = new Impuestos();
		//    compr.setImpuestos(impuestos);

		return compr;
	}

//	private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	private String getDescription(OrderToInvoice order) {
		String desc = "INDEMNIZACION DE LA RECUPERACION DE SINIESTROS MODALIDAD SIPAC PERCEPCION DE LA INDEMNIZACION DE LA RECUPRACION ASOCIADA AL : ";
		desc += ", Siniestro Acreedor: " + order.getPolizaAcreedor();
		desc += ", Poliza Acreedor: " + order.getPolizaAcreedor();
		if (order.getSiniestroCorrecto() != null) {
			desc += ", Siniestro Correcto: " + order.getSiniestroCorrecto();
		} else {
			desc += ", Siniestro Deudor: " + order.getSiniestroDeudor();
		}
		desc += ", Poliza Deudor: " + order.getPolizaDeudor();
		desc += ", Actividad no objeto de IVA";

		//		invDescription = invDescription.replaceAll("%sin%", order.getSiniestroAcreedor())
		//				.replaceAll("%folio%", order.getFolio())
		//				.replaceAll("%sinr%", order.getSiniestroDeudor())
		//				.replaceAll("%polr%", order.getPolizaDeudor())
		//				.replaceAll("%pola%", order.getPolizaAcreedor())
		//				.replaceAll("%sina%", order.getSiniestroAcreedor())
		//				.replaceAll("%fec%", format.format(order.getFechaEstatus()));
		//		if (order.getSiniestroCorrecto() != null) {
		//			invDescription.replaceAll("%sinc%", order.getSiniestroCorrecto());
		//		}
		//		return invDescription;

		return desc;
	}

	private Comprobante buildPaymentComplement(OrderToInvoice order) {
		/*
    <pago10:Pagos Version="1.0">
    <pago10:Pago FechaPago="2017-08-30T16:34:26" FormaDePagoP="01" MonedaP="MXN" Monto="1285.45">
      <pago10:DoctoRelacionado IdDocumento="2F1275E0-1088-4B73-B250-718136D615C9" MonedaDR="MXN" MetodoDePagoDR="PUE" ImpSaldoAnt="1246.91" ImpPagado="1246.91" ImpSaldoInsoluto="0.00" />
    </pago10:Pago>
  </pago10:Pagos>
		 */
		Comprobante compr = buildComprobante(order);
		compr.setMoneda(CMoneda.XXX);
		compr.setTipoDeComprobante(CTipoDeComprobante.P);
		compr.setTotal(new BigDecimal(0));
		compr.setSubTotal(new BigDecimal(0));
		compr.getConceptos().getConcepto().get(0).setImporte(new BigDecimal(0));
		compr.getConceptos().getConcepto().get(0).setValorUnitario(new BigDecimal(0));
		compr.getConceptos().getConcepto().get(0).setClaveProdServ("84111506");
		compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
		compr.getConceptos().getConcepto().get(0).setUnidad(null);
		//		compr.getConceptos().getConcepto().get(0).setDescripcion(getDescription(order));
		compr.getConceptos().getConcepto().get(0).setDescripcion("Pago");
		compr.getReceptor().setUsoCFDI(CUsoCFDI.P_01);
		compr.setFormaPago(null);

		Pagos compl = new Pagos();
		compl.setVersion("1.0");
		Pago paymt = new Pago();
		paymt.setFechaPago(CfdiUtil.getXMLGregorianCalendar());
		paymt.setFormaDePagoP("01");
		paymt.setMonedaP(CMoneda.MXN);
		paymt.setMonto(new BigDecimal(order.getMonto()));
		DoctoRelacionado doc = new DoctoRelacionado();
		doc.setIdDocumento(order.getId());
		doc.setMonedaDR(CMoneda.MXN);
		doc.setMetodoDePagoDR(CMetodoPago.PUE);
		doc.setImpSaldoAnt(new BigDecimal(order.getMonto()));
		doc.setImpPagado(new BigDecimal(order.getMonto()));
		doc.setImpSaldoInsoluto(new BigDecimal(0));
		paymt.getDoctoRelacionado().add(doc);
		compl.getPago().add(paymt);

		Complemento complemento = new Complemento();
		complemento.getAny().add(compl);
		compr.getComplemento().add(complemento);

		return compr;
	}

	private Comprobante buildCreditNote(OrderToInvoice order) {
		Comprobante compr = buildComprobante(order);
		compr.setTipoDeComprobante(CTipoDeComprobante.E);
		//		compr.getConceptos().getConcepto().get(0).setClaveProdServ(cveProducto);
		//		compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
		compr.getConceptos().getConcepto().get(0).setUnidad(null);
		//		compr.getConceptos().getConcepto().get(0).setDescripcion(getDescription(order));
		CfdiRelacionados relatedInvoices = new CfdiRelacionados();
		relatedInvoices.setTipoRelacion("01");
		CfdiRelacionado relatedInvoice = new CfdiRelacionado();
		relatedInvoice.setUUID(order.getId());
		relatedInvoices.getCfdiRelacionado().add(relatedInvoice);
		compr.setCfdiRelacionados(relatedInvoices);
		return compr;
	}

	private FacMovimientoFacturacion buildInvoiceMovement(OrderToInvoice order, EmitirComprobanteResponse resp, EstatusFacturacionEnum status) throws Exception {
		FacMovimientoFacturacion mov = new FacMovimientoFacturacion();

		Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
		byte[] pdf = retrievePdf(order.getApiKey(), compr.getUUID());
		//byte[] xml = retrieveXml(order.getApiKey(), compr.getUUID());

		String fileId = createVaultFiles(compr.getUUID(), resp.getResult().getBytes(), pdf);
		mov.setCfdiXml(fileId + ".xml");
		mov.setCfdiPdf(fileId + ".pdf");

		mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
		mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
		mov.setFechaMovimiento(new Timestamp(new Date().getTime()));

		mov.setUuid(compr.getUUID());
		mov = repository.registerInvoiceMovement(mov);

		mov.setPdf(pdf);
		mov.setXml(resp.getResult().getBytes());
		return mov;
	}

	private byte[] retrievePdf(String apiKey, String uuid) throws Exception {
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreRetrieveUrl, apiKey);
		return facade.getPdf(uuid).getContents();
	}

	private byte[] retrieveXml(String apiKey, String uuid) throws Exception {
		ReachCoreFacade facade = new ReachCoreFacade(reachCoreRetrieveUrl, apiKey);
		return facade.getXml(uuid).getContents();
	}

	private String createVaultFiles(String uuid, byte[] xml, byte[] pdf) {
		String fileId = vaultPath + File.separatorChar + uuid;
		try {
			FileUtils.writeByteArrayToFile(new File(fileId + ".xml"), xml);
			FileUtils.writeByteArrayToFile(new File(fileId + ".pdf"), pdf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileId;
	}

	private FacMovimientoError buildInvoiceError(String errorCode, String errorMsg, OrderToInvoice order, EstatusFacturacionEnum status) {
		FacMovimientoError mov = new FacMovimientoError();
		mov.setFacOrdenFacturada(new FacOrdenFacturada(order.getInvoiceOrderId()));
		mov.setFacEstatusFacturacion(new FacEstatusFacturacion(status.getEstatusId()));
		mov.setFechaMovimiento(new Timestamp(new Date().getTime()));
		mov.setCodigoError(errorCode);
		mov.setMensajeError(errorMsg);
		mov = repository.registerInvoiceMovement(mov);
		return mov;
	}
}
