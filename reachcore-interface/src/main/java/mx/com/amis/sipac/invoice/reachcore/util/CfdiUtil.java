package mx.com.amis.sipac.invoice.reachcore.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import mx.gob.sat.cfdi.serializer.v33.CTipoDeComprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.CfdiRelacionados.CfdiRelacionado;
import mx.gob.sat.cfdi.serializer.v33.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.v33.Pagos;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago;
import mx.gob.sat.cfdi.serializer.v33.Pagos.Pago.DoctoRelacionado;
import mx.gob.sat.retencionpago.serializer.Retenciones;

public class CfdiUtil {

  public static Comprobante getComprobanteFromXml(String xml) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.cfdi.serializer.v33");
    Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
    StringReader reader = new StringReader(xml);

    //    JAXBElement<Comprobante> unmarshalledObject = 
    //        (JAXBElement<Comprobante>)jaxbUnmarshaller.unmarshal(reader);

    Comprobante comprobante = (Comprobante) jaxbUnmarshaller.unmarshal(reader);
    return comprobante;
  }

  public static String formatCfdiToString(Comprobante compr) throws JAXBException {
    /*for (Complemento compl : compr.getComplemento()) {
      if(compl.getPagos() != null) {
        Pagos pagos = compl.getPagos();
        String paymt = formatCfdiToString(pagos);
        compl.getAny().clear();
        compl.getAny().add(paymt);
        break;
      }
    }*/
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.cfdi.serializer.v33");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(compr, sw);
    return normalizeUriNamespace(sw.toString(), "cfdi");
  }

  public static String formatCfdiToString(Pagos paymt) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.cfdi.serializer.v33");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(paymt, sw);
    return normalizeUriNamespace(sw.toString(), "pago10");
  }

  public static String formatCfdiToString(Retenciones compr) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.retencionpago.serializer");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(compr, sw);
    return normalizeUriNamespace(sw.toString(), "retenciones");
  }

  private static String normalizeUriNamespace(String xml, String uri) {
    xml = xml.replace("xmlns=", "xmlns:" + uri+ "=");
    xml = xml.replace("<", "<" + uri + ":");
    xml = xml.replace("<" + uri + ":?", "<?");
    xml = xml.replace("<" + uri + ":/", "</" + uri + ":");

    if (uri.equals("cfdi")) {
      xml = xml.replace("<cfdi:Comprobante", "<cfdi:Comprobante xmlns:pago10=\"http://www.sat.gob.mx/Pagos\" ");
      xml = xml.replace("cfdi:Pagos", "pago10:Pagos");
      xml = xml.replace("cfdi:Pago", "pago10:Pago");
      xml = xml.replace("cfdi:DoctoRelacionado", "pago10:DoctoRelacionado");
    }
    return xml;
  }

  public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date));
    } catch (DatatypeConfigurationException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static XMLGregorianCalendar getXMLGregorianCalendar() {
    //    Calendar cal = Calendar.getInstance();
    //    cal.add(Calendar.DATE, -2);
    //    return getXMLGregorianCalendar(cal.getTime());
    return getXMLGregorianCalendar(new Date());
  }

  public static String format(Comprobante compr) {
    String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
        + " <cfdi:Comprobante xmlns:pago10=\"http://www.sat.gob.mx/Pagos\"  Version=\"3.3\" "
        + "     xmlns:cfdi=\"http://www.sat.gob.mx/cfd/3\" xmlns:ns2=\"http://www.sat.gob.mx/TimbreFiscalDigital\" "
        + "     Fecha=\"" + compr.getFecha() + "\" ";
    if (compr.getTipoDeComprobante() != CTipoDeComprobante.P) {
      xmlString += "     FormaPago=\"" + compr.getFormaPago() + "\" ";
    }
    if (compr.getTipoDeComprobante() == CTipoDeComprobante.I) {
        xmlString += "     MetodoPago=\"" + compr.getMetodoPago() + "\" ";
    }
    xmlString += "     SubTotal=\"" + compr.getSubTotal() + "\" "
        + "     Moneda=\"" + compr.getMoneda().value() + "\" "
        + "     Total=\"" + compr.getTotal() + "\" "
        + "     TipoDeComprobante=\"" + compr.getTipoDeComprobante().value() + "\" "
        + "     LugarExpedicion=\"" + compr.getLugarExpedicion() + "\">";

    if (compr.getTipoDeComprobante() == CTipoDeComprobante.E) {
      CfdiRelacionados relatedInvoices = compr.getCfdiRelacionados();
      CfdiRelacionado relatedInvoice = relatedInvoices.getCfdiRelacionado().get(0);
      xmlString += " <cfdi:CfdiRelacionados TipoRelacion=\"" + relatedInvoices.getTipoRelacion() + "\"> "
          + " <cfdi:CfdiRelacionado UUID=\"" + relatedInvoice.getUUID() + "\"/> "
          + " </cfdi:CfdiRelacionados>";
    }

    xmlString += " <cfdi:Emisor Rfc=\"" + compr.getEmisor().getRfc() + "\" "
        + "     Nombre=\"" + compr.getEmisor().getNombre() + "\" "
        + "     RegimenFiscal=\"" + compr.getEmisor().getRegimenFiscal() + "\"/>"
        + " <cfdi:Receptor Rfc=\"" + compr.getReceptor().getRfc()+ "\" "
        + "     Nombre=\"" + compr.getReceptor().getNombre()+ "\" "
        + "     UsoCFDI=\"" + compr.getReceptor().getUsoCFDI().value()+ "\"/>";

    xmlString += " <cfdi:Conceptos>";
    for (Concepto conc : compr.getConceptos().getConcepto()) {
      xmlString += "<cfdi:Concepto "
          + "         ClaveProdServ=\"" + conc.getClaveProdServ() + "\" "
          + "         Cantidad=\"" + conc.getCantidad()+ "\" "
          + "         ClaveUnidad=\"" + conc.getClaveUnidad() + "\" ";
      if (compr.getTipoDeComprobante() != CTipoDeComprobante.P) {
        xmlString += "         Unidad=\"" + conc.getUnidad() + "\" ";
      }
      xmlString += "         Descripcion=\"" + conc.getDescripcion() + "\" "
          + "         ValorUnitario=\"" + conc.getValorUnitario() + "\" "
          + "         Importe=\"" + conc.getImporte() + "\"/>";
    }
    xmlString += " </cfdi:Conceptos>";

    if (compr.getTipoDeComprobante() == CTipoDeComprobante.P) {
      Pagos pagos = (Pagos) compr.getComplemento().get(0).getAny().get(0);
      Pago pago = pagos.getPago().get(0);
      DoctoRelacionado doc = pago.getDoctoRelacionado().get(0);
      xmlString += "<cfdi:Complemento><pago10:Pagos Version=\"1.0\">"
          + " <pago10:Pago "
          + "   FechaPago=\"" + pago.getFechaPago() + "\" "
          + "   FormaDePagoP=\"" + pago.getFormaDePagoP() + "\" "
          + "   MonedaP=\"" + pago.getMonedaP().value() + "\" "
          + "   Monto=\"" + pago.getMonto() + "\">"
          + " <pago10:DoctoRelacionado "
          + "   IdDocumento=\"" + doc.getIdDocumento() + "\" "
          + "   MonedaDR=\"" + doc.getMonedaDR().value() + "\" "
          + "   MetodoDePagoDR=\"" + doc.getMetodoDePagoDR().value() + "\" "
          + "   ImpSaldoAnt=\"" + doc.getImpSaldoAnt() + "\" "
          + "   ImpPagado=\"" + doc.getImpPagado() + "\" "
          + "	NumParcialidad=\"" + doc.getNumParcialidad() + "\" "
          + "   ImpSaldoInsoluto=\"" + doc.getImpSaldoInsoluto() + "\" />"
          + " </pago10:Pago>"
          + " </pago10:Pagos></cfdi:Complemento>";
    }

    xmlString += "<cfdi:Addenda><ad:orden xmlns:ad=\"http://www.sipac.com.mx\">"
        + "   <ad:datos>"
        + "       <ad:folioDua>" + compr.getFolioDua() + "</ad:folioDua>";
    if (compr.getSiniestroCorrecto() != null && !compr.getSiniestroCorrecto().trim().equals("")) {
      xmlString += "       <ad:siniestroCorrecto>" + compr.getSiniestroCorrecto() + "</ad:siniestroCorrecto>";
    } else {
    	xmlString += "       <ad:siniestroCorrecto>" + compr.getSiniestroDeudor() + "</ad:siniestroCorrecto>";
    }
    xmlString += "       <ad:siniestroDeudor>" + compr.getSiniestroDeudor() + "</ad:siniestroDeudor>"
        + "       <ad:siniestroAcreedor>" + compr.getSiniestroAcreedor() + "</ad:siniestroAcreedor>"
        + "       <ad:polizaDeudora>" + compr.getPolizaDeudor() + "</ad:polizaDeudora>"
        + "       <ad:polizaAcreedora>" + compr.getPolizaAcreedor() + "</ad:polizaAcreedora>"
        + "   </ad:datos>"
        + "</ad:orden></cfdi:Addenda>"
        + " </cfdi:Comprobante>";

    return xmlString;
  }
}
