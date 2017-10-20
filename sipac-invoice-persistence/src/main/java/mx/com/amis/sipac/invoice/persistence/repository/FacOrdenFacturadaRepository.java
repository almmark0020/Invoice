package mx.com.amis.sipac.invoice.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.com.amis.sipac.invoice.persistence.domain.FacOrdenFacturada;

public interface FacOrdenFacturadaRepository extends BaseRepository<FacOrdenFacturada, Long> {
	/**
	 * Searchs for a FacConfiguracion entry with the given clave
	 * @param clave
	 * @return
	 */
	@Query(value = "select o from FacOrdenFacturada o where folio = :folio and idSiniestro = :idSiniestro and tipoOrden = :tipoOrden")
	public FacOrdenFacturada getByFolioAndIdSiniestroAndTipoOrden (
			@Param("folio") final String folio,
			@Param("idSiniestro") final Integer idSiniestro,
			@Param("tipoOrden") final String tipoOrden);
}
