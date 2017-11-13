package mx.com.amis.sipac.invoice.persistence.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.domain.EstatusEnum;
import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.EmailToNotify;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class InvoiceOrdersRepository {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceOrdersRepository.class);

  @PersistenceContext
  EntityManager em;

  @Autowired
  FacOrdenFacturadaRepository facOrdenFacturadaRepository;

  public List<OrderToInvoice> getAcceptedOrdersToInvoice() {
    return getOrdersToInvoice(getAcceptedStatus());
  }

  public List<OrderToInvoice> getOrdersAutAcceptanceToInvoice() {
    return getOrdersToInvoice(getAutAcceptedStatus());
  }

  public List<OrderToInvoice> getPaidOrdersToInvoice() {
    return getOrdersToInvoice(getPaidStatus(), EstatusFacturacionEnum.COMPLEMENTO);
  }

  public List<OrderToInvoice> getCancelledOrdersToInvoice() {
    return getOrdersToInvoice(getCancelledStatus(), EstatusFacturacionEnum.CANCELACION);
  }
  
  public List<OrderToInvoice> getRefundOrdersToInvoice() {
    return getOrdersToInvoice(getPaidStatus(), EstatusFacturacionEnum.NOTA_CREDITO);
  }
  
  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getOrdersToInvoice(List<String> statuses) {
    logger.debug("getOrdersToInvoice...");
    String queryString = "select distinct "
        + " 0 as \"invoiceOrderId\", "
        + " CONVERT(VARCHAR(20), ord.SINIESTRO_ID, 365) || ord.FOLIO_ORDEN || ord.ESTATUS_ID as id,"
        + getCommonFields()
        + " from FAC_ORDEN_A_FACTURAR ord "
        + " where ord.ORIGEN != 'R' "
        + " and ord.ESTATUS_ID in (" + getStatusString(statuses) + ") ";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }
  
  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getOrdersToInvoice(List<String> statuses, EstatusFacturacionEnum estatus) {
    logger.debug("getOrdersToInvoice...");
    String queryString = "select distinct "
        + " fac.ID_ORDEN_FACTURADA as \"invoiceOrderId\","
        + " mov.UUID as id,"
        + getCommonFields()
        + " from FAC_ORDEN_A_FACTURAR ord "
        + " join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = ord.SINIESTRO_ID"
        + "     and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = ord.TIPO_ORDEN and fac.CIA_DEUDORA = ord.CIA_DEUDORA "
        + " join FAC_MOVIMIENTO_FACTURACION mov on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and mov.ID_ESTATUS_FACTURACION = " 
        + (estatus == EstatusFacturacionEnum.NOTA_CREDITO ? EstatusFacturacionEnum.COMPLEMENTO.getEstatusId() : EstatusFacturacionEnum.FACTURA.getEstatusId())
        + " left join FAC_MOVIMIENTO_ERROR err "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and err.CODIGO_ERROR in (" + getDelayedStatus() + ") and err.ID_ESTATUS_FACTURACION = " + estatus.getEstatusId()
        +"      and REINTENTO = 0 "
        + " where ord.ORIGEN " + (estatus == EstatusFacturacionEnum.NOTA_CREDITO ? " = " : " != ") + " 'R' "
        + " and err.ID_MOVIMIENTO_ERROR = null "
        + " and ord.ESTATUS_ID in (" + getStatusString(statuses) + ") ";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }

  private String getDelayedStatus() {
    return "'0', '101', '100', '102', '205', '208', '306', '307', '308', '316', '317', '318', '319', '202'";
  }

  private String getCommonFields() {
    String fields = " ord.SINIESTRO_ID as \"siniestroId\","
        + " ord.SINIESTRO_DEUDOR as \"siniestroDeudor\","
        + " ord.SINIESTRO_ACREEDOR as \"siniestroAcreedor\","
        + " ord.SINIESTRO_CORRECTO as \"siniestroCorrecto\","
        + " ord.POLIZA_DEUDOR as \"polizaDeudor\","
        + " ord.POLIZA_ACREEDOR as \"polizaAcreedor\","
        + " ord.FOLIO_ORDEN as \"folio\","
        + " ord.ESTATUS_ID as \"estatus\" ,"
        + " ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " ord.CIA_DEUDORA as \"ciaDeudora\", "
        + " ord.RFC_DEUDORA as \"rfcDeudora\","
        + " ord.RAZON_SOCIAL_DEUDORA as \"razonSocialDeudora\","
        + " ord.REGIMEN_FISCAL_DEUDORA as \"regimeFiscalDeudora\","
        + " ord.CIA_ACREEDORA as \"ciaAcreedora\","
        + " ord.RFC_ACREEDORA as \"rfcAcreedora\","
        + " ord.RAZON_SOCIAL_ACREEDORA as \"razonSocialAcreedora\","
        + " ord.REGIMEN_FISCAL_ACREEDORA as \"regimeFiscalAcreedora\","
        + " ord.CP_ACREEDORA as \"cp\","
        + " ord.MONTO as \"monto\","
        + " ord.TIPO_ORDEN as \"tipoOrden\", "
        + " '' as \"apiKey\" ";
    return fields;
  }

  @SuppressWarnings("unchecked")
  public List<EmailToNotify> getEmails(long companiaAcr, long companiaDeu) {
    String queryString = "select ID_USUARIO as \"id\", "
        + " NOMBRE_USUARIO as \"username\", "
        + " CORREO_ELECTRONICO as \"email\" "
        + " from FAC_USUARIO"
        + " where ID_COMPANIA in (" + companiaAcr + ", " + companiaDeu + ") "
        + " and NOTIFICAR_EMAIL = 1 and ACTIVO = 1 ";
    Query q = em.createNativeQuery (queryString, EmailToNotify.class);
    return q.getResultList();
  }

  //	@SuppressWarnings("unchecked")
  //	public List<EmailToNotify> getEmails(long companiaAcr, long companiaDeu) {
  //		String queryString = "select USUARIO_ID as \"id\", "
  //				+ " LOGIN_USUARIO as \"username\", "
  //				+ " EMAIL as \"email\" "
  //				+ " from USUARIOS"
  //				+ " where CIA_ID in (" + companiaAcr + ", " + companiaDeu + ") "
  //				+ " and NOTIFICAR_EMAIL = 1 and ESTATUS = 'A' ";
  //		Query q = em.createNativeQuery (queryString, EmailToNotify.class);
  //		return q.getResultList();
  //	}

  private List<String> getAcceptedStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.ACEPTACION_MANUAL.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA_POR_MATCH.getEstatusId());
    statusList.add(EstatusEnum.CONFIRMACION_DE_ACEPTACION_MANUAL.getEstatusId());
    statusList.add(EstatusEnum.CONFIRMACION_ACEPTACION_POR_COMITE.getEstatusId());
    return statusList;
  }

  private List<String> getAutAcceptedStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA.getEstatusId());
    statusList.add(EstatusEnum.CONFIRMACION_DE_ACEPTACION_AUTOMATICA.getEstatusId());
    return statusList;
  }

  private List<String> getPaidStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.CONFIRMACION_DE_PAGO.getEstatusId());
    return statusList;
  }

  private List<String> getCancelledStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.CANCELACION_ACEPTACION_PAGO.getEstatusId());
    return statusList;
  }

  private String getStatusString(List<String> statusList) {
    String queryString = "";
    boolean first = true;
    for(String status : statusList) {
      if (!first) {
        queryString += " ,";
      }
      queryString += "'" + status + "'";
      first = false;
    }
    return queryString;
  }

  @Transactional
  public FacOrdenFacturada registerInvoicedOrder(FacOrdenFacturada order) {
    FacOrdenFacturada exist = facOrdenFacturadaRepository.getByFolioAndIdSiniestroAndTipoOrden(order.getFolio(), order.getIdSiniestro(), order.getTipoOrden());
    if (exist != null) {
      return exist;
    } else {
      return em.merge(order);
    }
  }

  @Transactional
  public FacMovimientoFacturacion registerInvoiceMovement(OrderToInvoice order, FacMovimientoFacturacion movement) {
    String queryString = "delete from FAC_ORDEN_A_FACTURAR "
        + " where SINIESTRO_ID = " + order.getSiniestroId()
        + " and FOLIO_ORDEN = '" + order.getFolio() + "' "
        + " and ESTATUS_ID = '" + order.getEstatus() + "' ";
    Query q = em.createNativeQuery(queryString);
    q.executeUpdate();
    return em.merge(movement);
  }

  @Transactional
  public FacMovimientoError registerInvoiceMovement(FacMovimientoError movement) {
    return em.merge(movement);
  }

  @Transactional
  public void resetErrors() {
    String queryString = "update FAC_MOVIMIENTO_ERROR set REINTENTO = 1 where ID_MOVIMIENTO_ERROR in ("
        + " select ID_MOVIMIENTO_ERROR from FAC_MOVIMIENTO_ERROR err"
        + " join FAC_ORDEN_FACTURADA ord on err.ID_ORDEN_FACTURADA = ord.ID_ORDEN_FACTURADA"
        + " left join FAC_MOVIMIENTO_FACTURACION mov on mov.ID_ORDEN_FACTURADA = err.ID_ORDEN_FACTURADA"
        + " and mov.ID_ESTATUS_FACTURACION = err.ID_ESTATUS_FACTURACION"
        + " where mov.ID_MOVIMIENTO_FACTURACION = null and CODIGO_ERROR in ( " + getDelayedStatus() + ") )";
    Query q = em.createNativeQuery(queryString);
    q.executeUpdate();
  }

  @SuppressWarnings("unchecked")
  public boolean isAlreadyInvoiced(OrderToInvoice order, EstatusFacturacionEnum status) {
    String queryString = "select " + 
        " mov.ID_MOVIMIENTO_FACTURACION " + 
        " from FAC_ORDEN_FACTURADA fac  " + 
        " join FAC_MOVIMIENTO_FACTURACION mov " +
        " on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA " +
        " and mov.ID_ESTATUS_FACTURACION = " + status.getEstatusId() + 
        " where fac.ID_SINIESTRO = " + order.getSiniestroId() + 
        " and fac.FOLIO = '" + order.getFolio() + "' " + 
        " and fac.TIPO_ORDEN = '" + order.getTipoOrden() +"' " +
        " and fac.CIA_DEUDORA = " + order.getCiaDeudora();
    Query q = em.createNativeQuery(queryString);
    List<Object[]> result = q.getResultList();
    return result != null && !result.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public boolean isAlreadyOnError(OrderToInvoice order, EstatusFacturacionEnum status) {
    String queryString = "select " + 
        " mov.ID_MOVIMIENTO_ERROR " + 
        " from FAC_ORDEN_FACTURADA fac  " + 
        " join FAC_MOVIMIENTO_ERROR mov " +
        " on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA " +
        " and mov.ID_ESTATUS_FACTURACION = " + status.getEstatusId() + 
        " where fac.ID_SINIESTRO = " + order.getSiniestroId() + 
        " and fac.FOLIO = '" + order.getFolio() + "' " + 
        " and fac.TIPO_ORDEN = '" + order.getTipoOrden() +"' " +
        " and fac.CIA_DEUDORA = " + order.getCiaDeudora();
    Query q = em.createNativeQuery(queryString);
    List<Object[]> result = q.getResultList();
    return result != null && !result.isEmpty();
  }

  public String getApiKey(OrderToInvoice order) {
    String result = "";
    String queryString = "select APIKEY from FAC_COMPANIA_APIKEY "
        + " where COMPANIA_ID = " + order.getCiaAcreedora() + " and ACTIVO = 1";
    Query q = em.createNativeQuery(queryString);
    try {
      result = (String) q.getSingleResult();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }
}
