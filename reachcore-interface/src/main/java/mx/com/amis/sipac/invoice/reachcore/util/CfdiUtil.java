package mx.com.amis.sipac.invoice.reachcore.util;

import java.io.StringWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import mx.gob.sat.cfdi.serializer.Comprobante;
import mx.gob.sat.retencionpago.serializer.Retenciones;

public class CfdiUtil {
  
  public static String formatCfdiToString(Comprobante compr) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.cfdi.serializer");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(compr, sw);
    return normalizeUriNamespace(sw.toString(), "cfdi");
//    String cfdi = sw.toString();
//    cfdi = cfdi.replace("xmlns=", "xmlns:cfdi=");
//    cfdi = cfdi.replace("<", "<cfdi:");
//    cfdi = cfdi.replace("<cfdi:?", "<?");
//    cfdi = cfdi.replace("<cfdi:/", "</cfdi:");
//    return cfdi;
  }
  
  public static String formatCfdiToString(Retenciones compr) throws JAXBException {
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.retencionpago.serializer");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(compr, sw);
    return normalizeUriNamespace(sw.toString(), "retenciones");
//    String cfdi = sw.toString();
//    cfdi = cfdi.replace("xmlns=", "xmlns:retenciones=");
//    cfdi = cfdi.replace("<", "<retenciones:");
//    cfdi = cfdi.replace("<retenciones:?", "<?");
//    cfdi = cfdi.replace("<retenciones:/", "</retenciones:");
//    return cfdi;
  }
  
  private static String normalizeUriNamespace(String xml, String uri) {
    xml = xml.replace("xmlns=", "xmlns:" + uri+ "=");
    xml = xml.replace("<", "<" + uri + ":");
    xml = xml.replace("<" + uri + ":?", "<?");
    xml = xml.replace("<" + uri + ":/", "</" + uri + ":");
    return xml;
  }

  public static XMLGregorianCalendar getXMLGregorianCalendar(Date date) {
    GregorianCalendar c = new GregorianCalendar();
    c.setTime(date);
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    } catch (DatatypeConfigurationException e) {
      e.printStackTrace();
      return null;
    }
  }
  
  public static XMLGregorianCalendar getXMLGregorianCalendar() {
    return getXMLGregorianCalendar(new Date());
  }
}
