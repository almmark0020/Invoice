package mx.com.amis.sipac.invoice.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mx.com.amis.sipac.invoice.persistence.domain.FacConfiguracion;


public interface FacConfiguracionRepository extends BaseRepository<FacConfiguracion, Integer> {
  /**
   * Searchs for a FacConfiguracion entry with the given clave
   * @param clave
   * @return
   */
  @Query(value = "select o from FacConfiguracion o where clave = :clave")
  public FacConfiguracion getByClave(@Param("clave") final String clave);
}
