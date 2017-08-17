package mx.com.amis.sipac.invoice.reachcore;

import java.math.BigDecimal;

import org.junit.Test;

import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;

import mx.com.amis.sipac.invoice.reachcore.domain.FileResponse;
import mx.com.amis.sipac.invoice.reachcore.facade.ReachCoreFacade;
import mx.com.amis.sipac.invoice.reachcore.util.CfdiUtil;
import mx.gob.sat.cfdi.serializer.Comprobante;
import mx.gob.sat.cfdi.serializer.Comprobante.Conceptos;
import mx.gob.sat.cfdi.serializer.Comprobante.Conceptos.Concepto;
import mx.gob.sat.cfdi.serializer.Comprobante.Emisor;
import mx.gob.sat.cfdi.serializer.Comprobante.Emisor.RegimenFiscal;
import mx.gob.sat.cfdi.serializer.Comprobante.Impuestos;
import mx.gob.sat.cfdi.serializer.Comprobante.Receptor;
import mx.gob.sat.cfdi.serializer.TUbicacion;
import mx.gob.sat.cfdi.serializer.TUbicacionFiscal;

public class ReachCoreFacadeTest {

  @Test
  public void getPdf() throws Exception {
    String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    String url = "https://oat.reachcore.com/api/rest/Timbre/Get";
    ReachCoreFacade facade = new ReachCoreFacade(url, apiKey);
    FileResponse response = facade.getPdf("F9123206-D86C-49EE-80F3-C288CC948631");
    System.out.println("response: " + response);
  }
  
  @Test
  public void emit() throws Exception {
    String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    String emitUrl = "https://oat.reachcore.com/api/ws/6.0/pacservices/Emision.svc/basic?wsdl";
    ReachCoreFacade facade = new ReachCoreFacade(emitUrl, apiKey);
    Comprobante compr = buildMockComprobante();
    EmitirComprobanteResponse resp = facade.emitInvoice(compr);
    System.out.println("resp: " + resp);
  }

  @Test
  public void cancel() throws Exception {
    String apiKey = "h4kxqr4tdzyfdyga4ezbbnjphabjt8etruqqm6xeqxgucqbt5ne7f3j5gzguun8qerhr56c8tadienvy";
    String cancelUrl = "https://oat.reachcore.com/api/ws/timbre-fiscal/Cancelacion.svc/basic?wsdl";
    ReachCoreFacade facade = new ReachCoreFacade(cancelUrl, apiKey);
    facade.setUrl(cancelUrl);
    CancelacionFiscalResponse respCan = facade.cancelInvoice("LOVM840920AA9", "F9123206-D86C-49EE-80F3-C288CC948631");
    System.out.println("respCancelacion: " + respCan);
  }
  
  private Comprobante buildMockComprobante() {
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
}