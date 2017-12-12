package mx.com.amis.sipac.invoice.reachcore;

import java.io.File;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.TransactionDetailResponse;

import mx.com.amis.sipac.invoice.reachcore.domain.FileResponse;
import mx.com.amis.sipac.invoice.reachcore.facade.ReachCoreFacade;
import mx.com.amis.sipac.invoice.reachcore.util.CfdiUtil;
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

public class ReachCoreFacadeTest {

	//  @Test
	public void getPdf() throws Exception {
		//h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy
		String apiKey = "okok";
		String url = "https://oat.reachcore.com/api/rest/Timbre/Get";
		ReachCoreFacade facade = new ReachCoreFacade(url, apiKey);
		FileResponse response = facade.getPdf("27A59B95-7288-4EDB-BEFC-B65458C742F2");
		FileUtils.writeByteArrayToFile(new File("/home/almmark0020/test/27A59B95-7288-4EDB-BEFC-B65458C742F2.pdf"), response.getContents());
		System.out.println("response: " + response);
	}

	@Test
	public void emit() throws Exception {
		// h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy
		String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
		String emitUrl = "https://oat.reachcore.com/api/ws/6.0/pacservices/Emision.svc/basic?wsdl";
		ReachCoreFacade facade = new ReachCoreFacade(emitUrl, apiKey);
		Comprobante compr = buildMockComprobante33();
		System.out.println("comprobante XML: " + CfdiUtil.format(compr));
		EmitirComprobanteResponse resp = facade.emitInvoice(compr);
		System.out.println("resp: " + resp);
		
		String uuid = processResponse(resp);
		
		if (uuid != null) {
			System.out.println("Emiting payment compliment...");
			compr = buildMockPaymentComplement(uuid);
			System.out.println("comprobante XML: " + CfdiUtil.format(compr));
			resp = facade.emitInvoice(compr);
			System.out.println("resp: " + resp);
			processResponse(resp);
			
			System.out.println("Emiting credit note...");
			compr = buildMockCreditNote(uuid);
			System.out.println("+++++++++++++++++++++++++++++");
            System.out.println("comprobante XML: " + CfdiUtil.formatCfdiToString(compr));
            System.out.println("+++++++++++++++++++++++++++++");
            System.out.println("comprobante XML: " + CfdiUtil.format(compr));
            System.out.println("+++++++++++++++++++++++++++++");
			resp = facade.emitInvoice(compr);
			System.out.println("resp: " + resp);
			processResponse(resp);
		}
	}

	private String processResponse(EmitirComprobanteResponse resp) throws JAXBException {
		if(ReachCoreFacade.hasErrors(resp)) {
			System.out.println("error code: " + ReachCoreFacade.getErrorCode(resp));
			System.out.println("error message: " + ReachCoreFacade.getErrorMessage(resp));
		} else {
			Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
			for (Complemento compl : compr.getComplemento()) {
				System.out.println("UUID: " + compl.getUUID());
				for (Object any : compl.getAny()) {
					System.out.println("compl: " + any.toString());
				}
				return compl.getUUID();
			}
		}
		return null;
	}

//	  @Test
	public void emitString() throws Exception {
		String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
		String emitUrl = "https://oat.reachcore.com/api/ws/6.0/pacservices/Emision.svc/basic?wsdl";
		ReachCoreFacade facade = new ReachCoreFacade(emitUrl, apiKey);
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
		    + " <cfdi:Comprobante xmlns:pago10=\"http://www.sat.gob.mx/Pagos\"  Version=\"3.3\" Fecha=\"2017-11-26T19:13:42\" FormaPago=\"03\" SubTotal=\"10\" Moneda=\"MXN\" Total=\"10\" TipoDeComprobante=\"I\" LugarExpedicion=\"08400\" xmlns:cfdi=\"http://www.sat.gob.mx/cfd/3\" xmlns:ns2=\"http://www.sat.gob.mx/TimbreFiscalDigital\">"
		    + " <cfdi:Emisor Rfc=\"AAA010101AAA\" Nombre=\"ACCEM SERVICIOS EMPRESARIALES SC\" RegimenFiscal=\"601\"/>"
		    + " <cfdi:Receptor Rfc=\"LOVM840920DI9\" Nombre=\"MARCO ANTONIO LOPEZ VARGAS\" UsoCFDI=\"G03\"/>"
		    + " <cfdi:Conceptos>"
		    + " <cfdi:Concepto ClaveProdServ=\"01010101\" Cantidad=\"1\" ClaveUnidad=\"C62\" Unidad=\"Unidad\" Descripcion=\"ok\" ValorUnitario=\"10\" Importe=\"10\"/>"
		    + " </cfdi:Conceptos>"
		    + " </cfdi:Comprobante>";
		System.out.println("xmlString: " + xmlString);
		EmitirComprobanteResponse resp = facade.emitInvoice(xmlString);
		System.out.println("resp: " + resp);

		if(ReachCoreFacade.hasErrors(resp)) {
			System.out.println("error code: " + ReachCoreFacade.getErrorCode(resp));
			System.out.println("error message: " + ReachCoreFacade.getErrorMessage(resp));
		} else {

			Comprobante compr = CfdiUtil.getComprobanteFromXml(resp.getResult());
			for (Complemento compl : compr.getComplemento()) {
				System.out.println("UUID: " + compl.getUUID());
				for (Object any : compl.getAny()) {
					System.out.println("compl: " + any.toString());
				}
			}
		}
	}



	//  @Test
	public void cancel() throws Exception {
		String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
		String cancelUrl = "https://oat.reachcore.com/api/ws/timbre-fiscal/Cancelacion.svc/basic?wsdl";
		ReachCoreFacade facade = new ReachCoreFacade(cancelUrl, apiKey);
		facade.setUrl(cancelUrl);
		CancelacionFiscalResponse respCan = facade.cancelInvoice("LOVM840920AA9", "F9123206-D86C-49EE-80F3-C288CC948631");
		System.out.println("respCancelacion: " + respCan);
		if (ReachCoreFacade.hasErrors(respCan)) {
			System.out.println("error code: " + ReachCoreFacade.getErrorCode(respCan));
			System.out.println("error message: " + ReachCoreFacade.getErrorMessage(respCan));
		}
		if (respCan.getFolios() != null && respCan.getFolios().getTransactionDetailResponse() != null) {
			for (TransactionDetailResponse tr : respCan.getFolios().getTransactionDetailResponse()) {
				System.out.println("transaction: " + tr);
			}
		}
	}

	private Comprobante buildMockComprobante33() {
		Comprobante compr =  new Comprobante();
		compr.setVersion("3.3");
		compr.setTipoDeComprobante(CTipoDeComprobante.I);
		compr.setMoneda(CMoneda.MXN);

		compr.setFecha(CfdiUtil.getXMLGregorianCalendar());
		compr.setSubTotal(new BigDecimal(10));
		compr.setTotal(new BigDecimal(10));
		compr.setLugarExpedicion("08400");
		compr.setFormaPago("03");

		Emisor emisor = new Emisor();
		emisor.setRfc("AAA010101AAA");
		emisor.setNombre("ACCEM SERVICIOS EMPRESARIALES SC");
		emisor.setRegimenFiscal("601");
		//    RegimenFiscal regimen = new RegimenFiscal();
		//    regimen.setRegimen("Regimen Actividad Empresarial");
		//emisor.getRegimenFiscal().add(regimen);
		//    TUbicacionFiscal domicilio = new TUbicacionFiscal();
		//    domicilio.setCalle("Calle Emisor Trial");
		//    domicilio.setMunicipio("Deleg/Mpio Emisor Trial");
		//    domicilio.setEstado("Distrito Federal Emisor Trial");
		//    domicilio.setPais("Mexico Emisor Trial");
		//    domicilio.setCodigoPostal("09876");
		//    emisor.setDomicilioFiscal(domicilio);
		compr.setEmisor(emisor);

		Receptor receptor = new Receptor();
		receptor.setRfc("LOVM840920DI9");
		receptor.setNombre("MARCO ANTONIO LOPEZ VARGAS");
		//    TUbicacion dom = new TUbicacion();
		//    dom.setCalle("Calle Emisor Trial");
		//    dom.setMunicipio("Deleg/Mpio Emisor Trial");
		//    dom.setEstado("Distrito Federal Emisor Trial");
		//    dom.setPais("Mexico Emisor Trial");
		//    dom.setCodigoPostal("09876");
		//    receptor.setDomicilio(dom);
		receptor.setUsoCFDI(CUsoCFDI.G_03);
		compr.setReceptor(receptor);

		Concepto concepto = new Concepto();
		concepto.setCantidad(new BigDecimal(1));
		concepto.setUnidad("Unidad");
		concepto.setDescripcion("okokiÖn");
		concepto.setValorUnitario(new BigDecimal(10));
		concepto.setImporte(new BigDecimal(10));
		concepto.setClaveProdServ("01010101");
		concepto.setClaveUnidad("C62");

		Conceptos conceptos = new Conceptos();
		conceptos.getConcepto().add(concepto);
		compr.setConceptos(conceptos);

		//    Impuestos impuestos = new Impuestos();
		//    compr.setImpuestos(impuestos);

		return compr;
	}

	private Comprobante buildMockPaymentComplement(String uuid) {
		/*
  <pago10:Pagos Version="1.0">
  <pago10:Pago FechaPago="2017-08-30T16:34:26" FormaDePagoP="01" MonedaP="MXN" Monto="1285.45">
    <pago10:DoctoRelacionado IdDocumento="2F1275E0-1088-4B73-B250-718136D615C9" MonedaDR="MXN" MetodoDePagoDR="PUE" ImpSaldoAnt="1246.91" ImpPagado="1246.91" ImpSaldoInsoluto="0.00" />
  </pago10:Pago>
</pago10:Pagos>
		 */
		Comprobante compr = buildMockComprobante33();
		compr.setMoneda(CMoneda.XXX);
		compr.setTipoDeComprobante(CTipoDeComprobante.P);
		compr.setTotal(new BigDecimal(0));
		compr.setSubTotal(new BigDecimal(0));
		compr.setFormaPago(null);
		compr.getConceptos().getConcepto().get(0).setImporte(new BigDecimal(0));
		compr.getConceptos().getConcepto().get(0).setValorUnitario(new BigDecimal(0));
		compr.getConceptos().getConcepto().get(0).setClaveProdServ("84111506");
		compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
		compr.getConceptos().getConcepto().get(0).setUnidad(null);
		//		compr.getConceptos().getConcepto().get(0).setDescripcion(getDescription(order));
		compr.getConceptos().getConcepto().get(0).setDescripcion("Pago");
		compr.getReceptor().setUsoCFDI(CUsoCFDI.P_01);

		Pagos compl = new Pagos();
		compl.setVersion("1.0");
		Pago paymt = new Pago();
		paymt.setFechaPago(CfdiUtil.getXMLGregorianCalendar());
		paymt.setFormaDePagoP("03");
		paymt.setMonedaP(CMoneda.MXN);
		paymt.setMonto(new BigDecimal(1));
		DoctoRelacionado doc = new DoctoRelacionado();
		doc.setIdDocumento(uuid);
		doc.setMonedaDR(CMoneda.MXN);
		doc.setMetodoDePagoDR(CMetodoPago.PUE);
		doc.setImpSaldoAnt(new BigDecimal(1));
		doc.setImpPagado(new BigDecimal(1));
		doc.setImpSaldoInsoluto(new BigDecimal(0));
		paymt.getDoctoRelacionado().add(doc);
		compl.getPago().add(paymt);

		Complemento complemento = new Complemento();
		complemento.getAny().add(compl);
		compr.getComplemento().add(complemento);

		return compr;
	}
	
	private Comprobante buildMockCreditNote(String uuid) {
		Comprobante compr = buildMockComprobante33();
		compr.setTipoDeComprobante(CTipoDeComprobante.E);
		//compr.getConceptos().getConcepto().get(0).setClaveProdServ("01010101");
//		compr.getConceptos().getConcepto().get(0).setClaveUnidad("ACT");
		compr.getConceptos().getConcepto().get(0).setUnidad(null);
		//compr.getConceptos().getConcepto().get(0).setDescripcion("Test");
		CfdiRelacionados relatedInvoices = new CfdiRelacionados();
		relatedInvoices.setTipoRelacion("01");
		CfdiRelacionado relatedInvoice = new CfdiRelacionado();
		relatedInvoice.setUUID(uuid);
		relatedInvoices.getCfdiRelacionado().add(relatedInvoice);
		compr.setCfdiRelacionados(relatedInvoices);
		return compr;
	}

	/*
  private mx.gob.sat.cfdi.serializer.v32.Comprobante buildMockComprobante32() {
    Comprobante compr =  new Comprobante();
    compr.setVersion("3.2");

    compr.setFecha(CfdiUtil.getXMLGregorianCalendar());
    compr.setFormaDePago("Pago en una sola Exhibicion");
    compr.setSubTotal(new BigDecimal(0));
    compr.setTotal(new BigDecimal(0));
    compr.setTipoDeComprobante("ingreso");
    compr.setMetodoDePago("Efectivo");
    compr.setLugarExpedicion("México D.F.");

    Emisor emisor = new Emisor();
    emisor.setRfc("LOVM820913AA9");
    emisor.setNombre("TEST");
    RegimenFiscal regimen = new RegimenFiscal();
    regimen.setRegimen("Regimen Actividad Empresarial");
    emisor.getRegimenFiscal().add(regimen);
    TUbicacionFiscal domicilio = new TUbicacionFiscal();
    domicilio.setCalle("Calle Emisor Trial");
    domicilio.setMunicipio("Deleg/Mpio Emisor Trial");
    domicilio.setEstado("Distrito Federal Emisor Trial");
    domicilio.setPais("Mexico Emisor Trial");
    domicilio.setCodigoPostal("09876");
    emisor.setDomicilioFiscal(domicilio);
    compr.setEmisor(emisor);

    Receptor receptor = new Receptor();
    receptor.setRfc("XAXX010101RC5");
    receptor.setNombre("TEST RECEPTOR");
    TUbicacion dom = new TUbicacion();
    dom.setCalle("Calle Emisor Trial");
    dom.setMunicipio("Deleg/Mpio Emisor Trial");
    dom.setEstado("Distrito Federal Emisor Trial");
    dom.setPais("Mexico Emisor Trial");
    dom.setCodigoPostal("09876");
    receptor.setDomicilio(dom);
    compr.setReceptor(receptor);

    Concepto concepto = new Concepto();
    concepto.setCantidad(new BigDecimal(1));
    concepto.setUnidad("Unidad");
    concepto.setDescripcion("Descripcion");
    concepto.setValorUnitario(new BigDecimal(0));
    concepto.setImporte(new BigDecimal(0));
    Conceptos conceptos = new Conceptos();
    conceptos.getConcepto().add(concepto);
    compr.setConceptos(conceptos);

    Impuestos impuestos = new Impuestos();
    compr.setImpuestos(impuestos);

    return compr;
  }
	 */
}