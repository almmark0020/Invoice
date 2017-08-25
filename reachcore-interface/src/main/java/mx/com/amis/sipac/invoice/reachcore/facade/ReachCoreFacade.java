package mx.com.amis.sipac.invoice.reachcore.facade;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tempuri.Cancelacion;
import org.tempuri.Emision;
import org.tempuri.ICancelacion;
import org.tempuri.IEmision;

import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteRequest;
import com.reachcore.services.api.ws.pacservices._6.EmitirComprobanteResponse;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.ArrayOfGuid;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalRequest;
import com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2.CancelacionFiscalResponse;

import mx.com.amis.sipac.invoice.reachcore.domain.FileResponse;
import mx.com.amis.sipac.invoice.reachcore.util.CfdiUtil;
import mx.com.amis.sipac.invoice.reachcore.util.NetClientGet;
import mx.gob.sat.cfdi.serializer.v33.Comprobante;
import mx.gob.sat.retencionpago.serializer.Retenciones;

/**
 * A set of methods to access reachCore web services 
 * @author marco.antonio@prutech.com.mx
 *  
 *  URLs:
 *  emit invoices:
 *  Test: https://oat.reachcore.com/api/ws/6.0/pacservices/Emision.svc/basic?wsdl
 *  Producción:  https://go.reachcore.com/api/ws/6.0/pacservices/Emision.svc/basic?wsdl
 *  
 *  cancel invoices:
 *  Test: https://oat.reachcore.com/api/ws/timbre-fiscal/Cancelacion.svc/basic?wsdl
 *  Producción  https://go.reachcore.com/api/ws/timbre-fiscal/Cancelacion.svc/basic?wsdl
 */
public class ReachCoreFacade {
  private static final Logger logger = LoggerFactory.getLogger(ReachCoreFacade.class);

  /**
   * The reachCore service url address
   */
  private String url;

  /**
   * The key to authenticate over reachCore services
   */
  private String apiKey;

  public ReachCoreFacade(String url, String apiKey) {
    this.url = url;
    this.apiKey = apiKey;
  }

  /**
   * Performs a request to reachCore for generating an invoice
   * @param compr An object with the information about the invoice
   * @return
   * @throws Exception
   */
  public EmitirComprobanteResponse emitInvoice(Comprobante compr) throws Exception {
    Emision emision = new Emision(new URL(url));
    IEmision cliente = emision.getBasicHttpBindingIEmision();
    EmitirComprobanteRequest req = new EmitirComprobanteRequest();
    String cfdiString = CfdiUtil.formatCfdiToString(compr);
    logger.debug("xml: " + cfdiString);
    System.out.println("xml: " + cfdiString);
    req.setComprobante(cfdiString);
    return cliente.emitirComprobante(req, apiKey);
  }

  /**
   * Performs a request to reachCore for generating a notice of payment
   * @param ret An object with information about the notice of payment 
   * @return
   * @throws Exception
   */
  public EmitirComprobanteResponse emitNoticeOfPayment(Retenciones ret) throws Exception {
    Emision emision = new Emision(new URL(url));
    IEmision cliente = emision.getBasicHttpBindingIEmision();
    EmitirComprobanteRequest req = new EmitirComprobanteRequest();
    String cfdiString = CfdiUtil.formatCfdiToString(ret);
    logger.debug("xml: " + cfdiString);
    req.setComprobante(cfdiString);
    return cliente.emitirComprobante(req, apiKey);
  }

  /**
   * Performs a request to reachCore for canceling a set of invoices
   * @param rfc The tax id related to the set on invoices to cancel
   * @param invoiceIds A list of invoices id to cancel
   * @return
   * @throws Exception
   */
  public CancelacionFiscalResponse cancelInvoice(String rfc, List<String> invoiceIds) throws Exception {
    Cancelacion canc = new Cancelacion(new URL(url));
    ICancelacion clienteCan = canc.getBasicHttpBindingICancelacion();
    CancelacionFiscalRequest reqCan = new CancelacionFiscalRequest();
    reqCan.setRFC(rfc);
    ArrayOfGuid folios = new ArrayOfGuid();
    for(String invoiceId : invoiceIds) {
      folios.getGuid().add(invoiceId);
    }
    reqCan.setFolios(folios);
    return clienteCan.cancelar(reqCan, apiKey);
  }

  /**
   * Performs a request to reachCore for canceling an invoice
   * @param rfc The tax id related to the invoice to cancel
   * @param invoiceId The invoice id to be cancelled
   * @return
   * @throws Exception
   */
  public CancelacionFiscalResponse cancelInvoice(String rfc, String invoiceId) throws Exception {
    List<String> invoiceIds = new ArrayList<String>();
    invoiceIds.add(invoiceId);
    return cancelInvoice(rfc, invoiceIds);
  }

  /**
   * Retrieves a pdf file from reachcore's vault
   * @param uuid The invoice id to be requested
   * @return 
   * @throws Exception
   */
  public FileResponse getPdf(String uuid) throws Exception {
    return NetClientGet.getPdf(url, apiKey, uuid);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}