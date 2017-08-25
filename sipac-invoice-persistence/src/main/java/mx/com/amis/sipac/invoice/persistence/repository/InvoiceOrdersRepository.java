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
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoError;
import mx.com.amis.sipac.invoice.persistence.domain.FacMovimientoFacturacion;
import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;
import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class InvoiceOrdersRepository {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceOrdersRepository.class);

  @PersistenceContext
  EntityManager em;
  
  public List<OrderToInvoice> getAcceptedOrdersToInvoice() {
    return getOrdersToInvoice(getAcceptedStatus());
  }

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getOrdersToInvoice(List<String> statusList) {
    logger.debug("getOrdersToInvoice...");
    String queryString = "select "
        + " (CONVERT(VARCHAR, sin.SINIESTRO_ID) + ord.FOLIO_ORDEN + 'D') as id,"
        + " sin.SINIESTRO_ID as \"siniestroId\","
        + " ord.FOLIO_ORDEN as \"folio\","
        + " ord.ESTATUS_ID as \"estatus\" , ord.FECHA_ESTATUS as \"fechaEstatus\","
        + " sin.CIA_DEUDORA as \"ciaDeudora\", cia_deu.RFC as \"rfcDeudora\","
        + " cia_deu.RAZON_SOCIAL as \"razonSocialDeudora\", cia_deu.REGIMEN_FISCAL as \"regimeFiscalDeudora\","
        + " ord.CIA_ACREEDORA as \"ciaAcreedora\", cia_acc.RFC as \"rfcAcreedora\","
        + " cia_acc.RAZON_SOCIAL as \"razonSocialAcreedora\", cia_acc.REGIMEN_FISCAL as \"regimeFiscalAcreedora\","
        + " (select VALOR from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID "
        + " and INICIO_VIGENCIA = (select MAX(INICIO_VIGENCIA) from CPS where TIPO_TRANS_GM = mar.TIPO_TRANS_ID)) as \"monto\","
        + " 'D' as \"tipoOrden\","
        + " api.APIKEY as \"apiKey\""
        + " from ORDENES_DANO_MATERIAL ord"
        + " join SINIESTROS sin on ord.SINIESTRO_ID = sin.SINIESTRO_ID"
        + " join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID"
        + " join COMPANIAS cia_acc on ord.CIA_ACREEDORA = cia_acc.CIA_ID"
        + " join FAC_COMPANIA_APIKEY api on api.COMPANIA_ID = cia_acc.CIA_ID"
        + " join TIPO_VEHICULO tv on ord.TIPO_ID = tv.TIPO_ID"
        + " join MARCAS mar on mar.MARCA_ID = tv.MARCA_ID"
        + " left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID"
        + " and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = 'D' and fac.CIA_DEUDORA = sin.CIA_DEUDORA "
        + " where fac.ID_ORDEN_FACTURADA = null"
        + " and ord.ESTATUS_ID in (";
    boolean first = true;
    for(String status : statusList) {
      if (!first) {
        queryString += " ,";
      }
      queryString += "'" + status + "'";
      first = false;
    }
    queryString += ")";
    Query q = em.createNativeQuery (queryString, OrderToInvoice.class);
    return q.getResultList();
  }
  
  private List<String> getAcceptedStatus() {
    List<String> statusList = new ArrayList<String>();
    statusList.add(EstatusEnum.ACEPTACION_MANUAL_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_AUTOMATICA_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACION_COMITE_CONTROVERSIA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACIÓN_MANUAL.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACIÓN_AUTOMÁTICA.getEstatusId());
    statusList.add(EstatusEnum.ACEPTACIÓN_AUTOMÁTICA_POR_MATCH.getEstatusId());
    return statusList;
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
}
