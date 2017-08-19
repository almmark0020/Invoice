package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FAC_ORDEN_FACTURADA database table.
 * 
 */
@Entity
@Table(name="FAC_ORDEN_FACTURADA")
@NamedQuery(name="FacOrdenFacturada.findAll", query="SELECT f FROM FacOrdenFacturada f")
public class FacOrdenFacturada implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_ORDEN_FACTURADA")
	private Long idOrdenFacturada;

	@Column(name="FOLIO")
	private String folio;

	@Column(name="ID_SINIESTRO")
	private Integer idSiniestro;

	@Column(name="TIPO_ORDEN")
	private String tipoOrden;

	//bi-directional many-to-one association to FacMovimientoError
	@OneToMany(mappedBy="facOrdenFacturada")
	private List<FacMovimientoError> facMovimientoErrors;

	//bi-directional many-to-one association to FacMovimientoFacturacion
	@OneToMany(mappedBy="facOrdenFacturada")
	private List<FacMovimientoFacturacion> facMovimientoFacturacions;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="CIA_ACREEDORA")
	private Compania compania1;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="CIA_DEUDORA")
	private Compania compania2;

	public FacOrdenFacturada() {
	}

	public Long getIdOrdenFacturada() {
		return this.idOrdenFacturada;
	}

	public void setIdOrdenFacturada(Long idOrdenFacturada) {
		this.idOrdenFacturada = idOrdenFacturada;
	}

	public String getFolio() {
		return this.folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public Integer getIdSiniestro() {
		return this.idSiniestro;
	}

	public void setIdSiniestro(Integer idSiniestro) {
		this.idSiniestro = idSiniestro;
	}

	public String getTipoOrden() {
		return this.tipoOrden;
	}

	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}

	public List<FacMovimientoError> getFacMovimientoErrors() {
		return this.facMovimientoErrors;
	}

	public void setFacMovimientoErrors(List<FacMovimientoError> facMovimientoErrors) {
		this.facMovimientoErrors = facMovimientoErrors;
	}

	public FacMovimientoError addFacMovimientoError(FacMovimientoError facMovimientoError) {
		getFacMovimientoErrors().add(facMovimientoError);
		facMovimientoError.setFacOrdenFacturada(this);

		return facMovimientoError;
	}

	public FacMovimientoError removeFacMovimientoError(FacMovimientoError facMovimientoError) {
		getFacMovimientoErrors().remove(facMovimientoError);
		facMovimientoError.setFacOrdenFacturada(null);

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
		facMovimientoFacturacion.setFacOrdenFacturada(this);

		return facMovimientoFacturacion;
	}

	public FacMovimientoFacturacion removeFacMovimientoFacturacion(FacMovimientoFacturacion facMovimientoFacturacion) {
		getFacMovimientoFacturacions().remove(facMovimientoFacturacion);
		facMovimientoFacturacion.setFacOrdenFacturada(null);

		return facMovimientoFacturacion;
	}

	public Compania getCompania1() {
		return this.compania1;
	}

	public void setCompania1(Compania compania1) {
		this.compania1 = compania1;
	}

	public Compania getCompania2() {
		return this.compania2;
	}

	public void setCompania2(Compania compania2) {
		this.compania2 = compania2;
	}

}