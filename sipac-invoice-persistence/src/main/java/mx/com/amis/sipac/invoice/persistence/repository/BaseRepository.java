package mx.com.amis.sipac.invoice.persistence.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Interface JPA para entidades genericas.
 * 
 * @author Edgar Uriel
 *
 * @param <T>
 * @param <KeyType>
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Obtener todas las entidades del mismo tipo desde la base de datos.
     * 
     * @return List<T>
     */
    List<T> findAll();

}
