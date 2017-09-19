package mx.com.amis.sipac.invoice.persistence.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.domain.EstatusEnum;
import mx.com.amis.sipac.invoice.persistence.domain.EstatusFacturacionEnum;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.domain.OrderTypeEnum;
import mx.com.amis.sipac.invoice.persistence.model.EmailToNotify;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class InvoiceOrdersRepository {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceOrdersRepository.class);

  @PersistenceContext
  EntityManager em;

  public List<OrderToInvoice> getAcceptedOrdersToInvoice() {
    List<OrderToInvoice> orders = new ArrayList<OrderToInvoice>();
    orders.addAll(getOrdersToInvoice(OrderTypeEnum.DANOS_MATERIALES.getTypeId()));
    //orders.addAll(getOrdersToInvoice(OrderTypeEnum.GASTOS_MEDICOS.getTypeId()));
    return orders;
  }

  public List<OrderToInvoice> getPaidOrdersToInvoice() {
    List<OrderToInvoice> orders = new ArrayList<OrderToInvoice>();
    orders.addAll(getPaidOrders(OrderTypeEnum.DANOS_MATERIALES.getTypeId()));
    //orders.addAll(getPaidOrders(OrderTypeEnum.GASTOS_MEDICOS.getTypeId()));
    return orders;
  }

  public List<OrderToInvoice> getRefundOrdersToInvoice() {
    List<OrderToInvoice> orders = new ArrayList<OrderToInvoice>();
    orders.addAll(getRefundOrders(OrderTypeEnum.DANOS_MATERIALES.getTypeId()));
    //orders.addAll(getRefundOrders(OrderTypeEnum.GASTOS_MEDICOS.getTypeId()));
    return orders;
  }

  public List<OrderToInvoice> getCancelledOrdersToInvoice() {
    List<OrderToInvoice> orders = new ArrayList<OrderToInvoice>();
    orders.addAll(getCancelledOrders(OrderTypeEnum.DANOS_MATERIALES.getTypeId()));
    //orders.addAll(getCancelledOrders(OrderTypeEnum.GASTOS_MEDICOS.getTypeId()));
    return orders;
  }

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getOrdersToInvoice(String orderType) {
    logger.debug("getOrdersToInvoice...");
    String queryString = "select 0 as \"invoiceOrderId\", "
        + " (CONVERT(VARCHAR, sin.SINIESTRO_ID) + ord.FOLIO_ORDEN + 'D') as id,"
        + " sin.SINIESTRO_ID as \"siniestroId\","
        + " sin.SINIESTRO_DEUDOR as \"siniestroDeudor\","
        + " ord.SINIESTRO_ACREEDOR as \"siniestroAcreedor\","
        + " sin.POLIZA as \"polizaDeudor\","
        + " ord.POLIZA_ACREEDOR as \"polizaAcreedor\","
        + " ord.FOLIO_ORDEN as \"folio\","
        + " ord.ESTATUS_ID as \"estatus\" , ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " sin.CIA_DEUDORA as \"ciaDeudora\", cia_deu.RFC as \"rfcDeudora\","
        + " cia_deu.RAZON_SOCIAL as \"razonSocialDeudora\", cia_deu.REGIMEN_FISCAL as \"regimeFiscalDeudora\","
        + " ord.CIA_ACREEDORA as \"ciaAcreedora\", cia_acc.RFC as \"rfcAcreedora\","
        + " cia_acc.RAZON_SOCIAL as \"razonSocialAcreedora\", cia_acc.REGIMEN_FISCAL as \"regimeFiscalAcreedora\","
        + " cia_acc.LUGAR_EXPEDICION_CP as \"cp\","
        + " (select VALOR from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID "
        + " and INICIO_VIGENCIA = (select MAX(INICIO_VIGENCIA) from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID)) as \"monto\","
        + " '" + orderType + "' as \"tipoOrden\","
        + " api.APIKEY as \"apiKey\""
        + " from " + getTableByOrderType(orderType) + " ord"
        + " join SINIESTROS sin on ord.SINIESTRO_ID = sin.SINIESTRO_ID"
        + " join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID"
        + " join COMPANIAS cia_acc on ord.CIA_ACREEDORA = cia_acc.CIA_ID"
        + " join FAC_COMPANIA_APIKEY api on api.COMPANIA_ID = cia_acc.CIA_ID and api.ACTIVO = 1"
        + " join TIPO_VEHICULO tv on ord.TIPO_ID = tv.TIPO_ID"
        + " join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID"
        + " left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID"
        + "     and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = '" + orderType + "' and fac.CIA_DEUDORA = sin.CIA_DEUDORA "
        + " left join FAC_MOVIMIENTO_FACTURACION mov "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and mov.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.FACTURA.getEstatusId()
        + " left join FAC_MOVIMIENTO_ERROR err "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and err.CODIGO_ERROR in (" + getDelayedStatus() + ") "
        + "     and err.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.FACTURA.getEstatusId()
        +"      and REINTENTO = 0 "
        //+ " where fac.ID_ORDEN_FACTURADA = null"
        + " where mov.ID_MOVIMIENTO_FACTURACION = null "
        + " and err.ID_MOVIMIENTO_ERROR = null "
        + " and ord.FECHA_ESTATUS >= CAST(convert(varchar, getdate(), 101) as DATE) "
        + " and ord.ESTATUS_ID in (" + getStatusString(this.getAcceptedStatus()) + ") "
        + " and ord.ORIGEN != 'R' ";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }
  
  private String getDelayedStatus() {
    return "'101', '100', '102', '205', '208', '306', '307', '308', '316', '317', '318', '319'";
  }

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getPaidOrders(String orderType) {
    String queryString = "select"
        + " fac.ID_ORDEN_FACTURADA as \"invoiceOrderId\","
        + " (select UUID "
        + "     from FAC_MOVIMIENTO_FACTURACION "
        + "     where ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.FACTURA.getEstatusId() + ") as id,"
        + " sin.SINIESTRO_ID as \"siniestroId\","
        + " sin.SINIESTRO_DEUDOR as \"siniestroDeudor\","
        + " ord.SINIESTRO_ACREEDOR as \"siniestroAcreedor\","
        + " sin.POLIZA as \"polizaDeudor\","
        + " ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " ord.POLIZA_ACREEDOR as \"polizaAcreedor\","
        + " ord.FOLIO_ORDEN as \"folio\","
        + " ord.ESTATUS_ID as \"estatus\" , ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " sin.CIA_DEUDORA as \"ciaDeudora\", cia_deu.RFC as \"rfcDeudora\","
        + " cia_deu.RAZON_SOCIAL as \"razonSocialDeudora\", cia_deu.REGIMEN_FISCAL as \"regimeFiscalDeudora\","
        + " ord.CIA_ACREEDORA as \"ciaAcreedora\", cia_acc.RFC as \"rfcAcreedora\","
        + " cia_acc.RAZON_SOCIAL as \"razonSocialAcreedora\", cia_acc.REGIMEN_FISCAL as \"regimeFiscalAcreedora\","
        + " cia_acc.LUGAR_EXPEDICION_CP as \"cp\","
        + " (select VALOR from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID "
        + " and INICIO_VIGENCIA = (select MAX(INICIO_VIGENCIA) from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID)) as \"monto\","
        + " '" + orderType + "' as \"tipoOrden\","
        + " api.APIKEY as \"apiKey\""
        + " from " + getTableByOrderType(orderType) + " ord"
        + " join SINIESTROS sin on ord.SINIESTRO_ID = sin.SINIESTRO_ID"
        + " join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID"
        + " join COMPANIAS cia_acc on ord.CIA_ACREEDORA = cia_acc.CIA_ID"
        + " join FAC_COMPANIA_APIKEY api on api.COMPANIA_ID = cia_acc.CIA_ID and api.ACTIVO = 1"
        + " join TIPO_VEHICULO tv on ord.TIPO_ID = tv.TIPO_ID"
        + " join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID"
        + " left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID"
        + "     and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = '" + orderType + "' and fac.CIA_DEUDORA = sin.CIA_DEUDORA "
        + " left join FAC_MOVIMIENTO_FACTURACION mov on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and mov.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.COMPLEMENTO.getEstatusId()
        + " left join FAC_MOVIMIENTO_ERROR err "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and err.CODIGO_ERROR in (" + getDelayedStatus() + ") and err.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.COMPLEMENTO.getEstatusId()
        +"      and REINTENTO = 0 "
        + " where fac.ID_ORDEN_FACTURADA != null"
        + " and mov.ID_MOVIMIENTO_FACTURACION = null"
        + " and err.ID_MOVIMIENTO_ERROR = null "
        + " and ord.FECHA_ESTATUS >= CAST(convert(varchar, getdate(), 101) as DATE) "
        + " and ord.ESTATUS_ID in (" + getStatusString(this.getPaidStatus()) + ")"
        + " and ord.ORIGEN != 'R' ";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getCancelledOrders(String orderType) {
    String queryString = "select"
        + " fac.ID_ORDEN_FACTURADA as \"invoiceOrderId\","
        + " (select UUID "
        + "     from FAC_MOVIMIENTO_FACTURACION "
        + "     where ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.FACTURA.getEstatusId() + ") as id,"
        + " sin.SINIESTRO_ID as \"siniestroId\","
        + " sin.SINIESTRO_DEUDOR as \"siniestroDeudor\","
        + " ord.SINIESTRO_ACREEDOR as \"siniestroAcreedor\","
        + " sin.POLIZA as \"polizaDeudor\","
        + " ord.POLIZA_ACREEDOR as \"polizaAcreedor\","
        + " ord.FOLIO_ORDEN as \"folio\","
        + " ord.ESTATUS_ID as \"estatus\" , ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " sin.CIA_DEUDORA as \"ciaDeudora\", cia_deu.RFC as \"rfcDeudora\","
        + " cia_deu.RAZON_SOCIAL as \"razonSocialDeudora\", cia_deu.REGIMEN_FISCAL as \"regimeFiscalDeudora\","
        + " ord.CIA_ACREEDORA as \"ciaAcreedora\", cia_acc.RFC as \"rfcAcreedora\","
        + " cia_acc.RAZON_SOCIAL as \"razonSocialAcreedora\", cia_acc.REGIMEN_FISCAL as \"regimeFiscalAcreedora\","
        + " (select VALOR from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID "
        + " and INICIO_VIGENCIA = (select MAX(INICIO_VIGENCIA) from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID)) as \"monto\","
        + " cia_acc.LUGAR_EXPEDICION_CP as \"cp\","
        + " '" + orderType + "' as \"tipoOrden\","
        + " api.APIKEY as \"apiKey\""
        + " from " + getTableByOrderType(orderType) + " ord"
        + " join SINIESTROS sin on ord.SINIESTRO_ID = sin.SINIESTRO_ID"
        + " join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID"
        + " join COMPANIAS cia_acc on ord.CIA_ACREEDORA = cia_acc.CIA_ID"
        + " join FAC_COMPANIA_APIKEY api on api.COMPANIA_ID = cia_acc.CIA_ID and api.ACTIVO = 1"
        + " join TIPO_VEHICULO tv on ord.TIPO_ID = tv.TIPO_ID"
        + " join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID"
        + " left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID"
        + "     and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = '" + orderType + "' and fac.CIA_DEUDORA = sin.CIA_DEUDORA "
        + " left join FAC_MOVIMIENTO_FACTURACION mov on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and mov.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.CANCELACION.getEstatusId()
        + " left join FAC_MOVIMIENTO_ERROR err "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and err.CODIGO_ERROR in (" + getDelayedStatus() + ") and err.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.CANCELACION.getEstatusId()
        +"      and REINTENTO = 0 "
        + " where fac.ID_ORDEN_FACTURADA != null "
        + " and mov.ID_MOVIMIENTO_FACTURACION = null "
        + " and err.ID_MOVIMIENTO_ERROR = null "
        + " and ord.FECHA_ESTATUS >= CAST(convert(varchar, getdate(), 101) as DATE) "
        + " and ord.ESTATUS_ID in (" + getStatusString(this.getCancelledStatus()) + ")"
        + " and ord.ORIGEN != 'R' ";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getRefundOrders(String orderType) {
    String queryString = "select " + 
        " fac.ID_ORDEN_FACTURADA as \"invoiceOrderId\", " + 
        " (select UUID  " + 
        "             from FAC_MOVIMIENTO_FACTURACION  " + 
        "             where ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA  " + 
        "         and ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.FACTURA.getEstatusId() + ") as id," + 
        "         sin.SINIESTRO_ID as \"siniestroId\", " + 
        "         ord.FOLIO_ORDEN as \"folio\", " 
        + " sin.SINIESTRO_ID as \"siniestroId\","
        + " sin.SINIESTRO_DEUDOR as \"siniestroDeudor\","
        + " ord.SINIESTRO_ACREEDOR as \"siniestroAcreedor\","
        + " sin.POLIZA as \"polizaDeudor\","
        + " ord.POLIZA_ACREEDOR as \"polizaAcreedor\"," +
        "         ord.ESTATUS_ID as \"estatus\" , ord.FECHA_ESTATUS as \"fechaEstatus\", " + 
        "         sin.CIA_DEUDORA as \"ciaDeudora\", cia_deu.RFC as \"rfcDeudora\", " + 
        "         cia_deu.RAZON_SOCIAL as \"razonSocialDeudora\", cia_deu.REGIMEN_FISCAL as \"regimeFiscalDeudora\", " + 
        "         sinNuevo.CIA_DEUDORA as \"ciaAcreedora\", cia_acc.RFC as \"rfcAcreedora\", " + 
        "         cia_acc.RAZON_SOCIAL as \"razonSocialAcreedora\", cia_acc.REGIMEN_FISCAL as \"regimeFiscalAcreedora\", " + 
        "         (select VALOR from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID  " + 
        "         and INICIO_VIGENCIA = (select MAX(INICIO_VIGENCIA) from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID)) as \"monto\", " +
        " cia_acc.LUGAR_EXPEDICION_CP as \"cp\"," +
        "         'D' as \"tipoOrden\", " + 
        "         api.APIKEY as \"apiKey\" " + 
        " from " + getTableByOrderType(orderType) + " ord" +
        "         join SINIESTROS sin on sin.SINIESTRO_DEUDOR = ord.SINIESTRO_ACREEDOR and sin.CIA_DEUDORA = ord.CIA_ACREEDORA  " + 
        "         join SINIESTROS sinNuevo on ord.SINIESTRO_ID = sinNuevo.SINIESTRO_ID " + 
        "         join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID " + 
        "         join COMPANIAS cia_acc on sinNuevo.CIA_DEUDORA = cia_acc.CIA_ID " + 
        "         join FAC_COMPANIA_APIKEY api on api.COMPANIA_ID = cia_acc.CIA_ID and api.ACTIVO = 1 " + 
        "         join TIPO_VEHICULO tv on ord.TIPO_ID = tv.TIPO_ID " + 
        "         join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID " + 
        "         left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID " + 
        "         and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = 'D' and fac.CIA_DEUDORA = sin.CIA_DEUDORA  " + 
        "         left join FAC_MOVIMIENTO_FACTURACION mov on mov.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA" +
        "  and mov.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.NOTA_CREDITO.getEstatusId()  
        + " left join FAC_MOVIMIENTO_ERROR err "
        + "     on err.ID_ORDEN_FACTURADA = fac.ID_ORDEN_FACTURADA "
        + "     and err.CODIGO_ERROR in (" + getDelayedStatus() + ") and err.ID_ESTATUS_FACTURACION = " + EstatusFacturacionEnum.NOTA_CREDITO.getEstatusId() 
        +"      and REINTENTO = 0 " +
        "         where fac.ID_ORDEN_FACTURADA != null and " + 
        "         mov.ID_MOVIMIENTO_FACTURACION = null " + 
        " and err.ID_MOVIMIENTO_ERROR = null " +
        "         and ord.FECHA_ESTATUS >= CAST(convert(varchar, getdate(), 101) as DATE)  " + 
        " and ord.ESTATUS_ID in (" + getStatusString(this.getPaidStatus()) + ")" +
        "         and ord.ORIGEN = 'R'";  

    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }

  private String getTableByOrderType(String orderType) {
    return orderType == OrderTypeEnum.DANOS_MATERIALES.getTypeId() ? "ORDENES_DANO_MATERIAL" : "ORDENES_GASTO_MEDICO";
  }

  @SuppressWarnings("unchecked")
  public List<EmailToNotify> getEmails(long companiaId) {
    String queryString = "select ID_USUARIO as \"id\", "
        + " NOMBRE_USUARIO as \"username\", "
        + " CORREO_ELECTRONICO as \"email\" "
        + " from FAC_USUARIO"
        + " where NOTIFICAR_EMAIL = 1 and ID_COMPANIA = " + companiaId + " and ACTIVO = 1 ";
    Query q = em.createNativeQuery (queryString, EmailToNotify.class);
    return q.getResultList();
  }

  private List<String> getAcceptedStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.ACEPTACION_MANUAL_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_COMITE_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_MANUAL.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA_POR_MATCH.getEstatusId());
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
    return em.merge(order);
  }

  @Transactional
  public FacMovimientoFacturacion registerInvoiceMovement(FacMovimientoFacturacion movement) {
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
}
