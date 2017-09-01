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

import mx.gob.sat.cfdi.serializer.v33.Comprobante;
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
    JAXBContext context = JAXBContext.newInstance("mx.gob.sat.cfdi.serializer.v33");
    Marshaller marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    StringWriter sw = new StringWriter();
    marshaller.marshal(compr, sw);
    return normalizeUriNamespace(sw.toString(), "cfdi");
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
    return getXMLGregorianCalendar(new Date());
  }
}
