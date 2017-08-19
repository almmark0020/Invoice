package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FAC_ESTATUS_FACTURACION database table.
 * 
 */
@Entity
@Table(name="FAC_ESTATUS_FACTURACION")
@NamedQuery(name="FacEstatusFacturacion.findAll", query="SELECT f FROM FacEstatusFacturacion f")
public class FacEstatusFacturacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_ESTATUS_FACTURACION")
	private Integer idEstatusFacturacion;

	@Column(name="ACTIVO")
	private Boolean activo;

	@Column(name="DESCRIPCION")
	private String descripcion;

	//bi-directional many-to-one association to FacMovimientoError
	@OneToMany(mappedBy="facEstatusFacturacion")
	private List<FacMovimientoError> facMovimientoErrors;

	//bi-directional many-to-one association to FacMovimientoFacturacion
	@OneToMany(mappedBy="facEstatusFacturacion")
	private List<FacMovimientoFacturacion> facMovimientoFacturacions;

	public FacEstatusFacturacion() {
	}

	public Integer getIdEstatusFacturacion() {
		return this.idEstatusFacturacion;
	}

	public void setIdEstatusFacturacion(Integer idEstatusFacturacion) {
		this.idEstatusFacturacion = idEstatusFacturacion;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<FacMovimientoError> getFacMovimientoErrors() {
		return this.facMovimientoErrors;
	}

	public void setFacMovimientoErrors(List<FacMovimientoError> facMovimientoErrors) {
		this.facMovimientoErrors = facMovimientoErrors;
	}

	public FacMovimientoError addFacMovimientoError(FacMovimientoError facMovimientoError) {
		getFacMovimientoErrors().add(facMovimientoError);
		facMovimientoError.setFacEstatusFacturacion(this);

		return facMovimientoError;
	}

	public FacMovimientoError removeFacMovimientoError(FacMovimientoError facMovimientoError) {
		getFacMovimientoErrors().remove(facMovimientoError);
		facMovimientoError.setFacEstatusFacturacion(null);

		return facMovimientoError;
	}

	public List<FacMovimientoFacturacion> getFacMovimientoFacturacions() {
		return this.facMovimientoFacturacions;
	}

	public void setFacMovimientoFacturacions(List<FacMovimientoFacturacion> facMovimientoFacturacions) {
		this.facMovimientoFacturacions = facMovimientoFacturacions;
	}

	public FacMovimientoFacturacion addFacMovimientoFacturacion(FacMovimientoFacturacion facMovimientoFacturacion) {
		getFacMovimientoFacturacions().add(facMovimientoFacturacion);
		facMovimientoFacturacion.setFacEstatusFacturacion(this);

		return facMovimientoFacturacion;
	}

	public FacMovimientoFacturacion removeFacMovimientoFacturacion(FacMovimientoFacturacion facMovimientoFacturacion) {
		getFacMovimientoFacturacions().remove(facMovimientoFacturacion);
		facMovimientoFacturacion.setFacEstatusFacturacion(null);

		return facMovimientoFacturacion;
	}

}