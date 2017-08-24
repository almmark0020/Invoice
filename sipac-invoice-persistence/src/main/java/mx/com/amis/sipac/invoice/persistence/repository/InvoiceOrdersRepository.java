package mx.com.amis.sipac.invoice.persistence.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.com.amis.sipac.invoice.persistence.model.OrderToInvoice;

@Service
public class InvoiceOrdersRepository {
  private static final Logger logger = LoggerFactory.getLogger(InvoiceOrdersRepository.class);

  @PersistenceContext
  EntityManager em;

  @SuppressWarnings("unchecked")
  public List<OrderToInvoice> getOrdersToInvoice() {
    logger.debug("getOrdersToInvoice...");
    Query q = em.createNativeQuery (
        "select "
            + " sin.SINIESTRO_ID as \"siniestroId\","
            + " ord.FOLIO_ORDEN,"
            + " ord.ESTATUS_ID, ord.FECHA_ESTATUS,"
            + " sin.CIA_DEUDORA, cia_deu.RFC, cia_deu.RAZON_SOCIAL, cia_deu.REGIMEN_FISCAL,"
            + " ord.CIA_ACREEDORA, cia_acc.RFC, cia_acc.RAZON_SOCIAL, cia_acc.REGIMEN_FISCAL,"
            + " ord.IMPORTE_SALDO,"
            + " 'D' as TIPO_ORDEN"
            + " from ORDENES_DANO_MATERIAL ord"
            + " join SINIESTROS sin on ord.SINIESTRO_ID = sin.SINIESTRO_ID"
            + " join COMPANIAS cia_deu on sin.CIA_DEUDORA = cia_deu.CIA_ID"
            + " join COMPANIAS cia_acc on ord.CIA_ACREEDORA = cia_acc.CIA_ID"
            + " left join FAC_ORDEN_FACTURADA fac on fac.ID_SINIESTRO = sin.SINIESTRO_ID"
            + " and fac.FOLIO = ord.FOLIO_ORDEN and fac.TIPO_ORDEN = 'D' and fac.CIA_DEUDORA = sin.CIA_DEUDORA "
            + " where fac.ID_ORDEN_FACTURADA = null",
            OrderToInvoice.class);
    return q.getResultList();
  }
}
