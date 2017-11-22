package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

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

	@Column(name="FECHA_ESTATUS_SIPAC")
	private Timestamp fechaEstatusSipac;

	@Column(name="ESTATUS_SIPAC")
	private String estatusSipac;

	@Column(name="TIPO_CAPTURA")
	private String tipoCaptura;
	
	@Column(name="FECHA_REGISTRO")
	private Timestamp fechaRegistro;
	
	@Column(name="FECHA_ACEPTACION")
	private Timestamp fechaAceptacion;
	
	@Column(name="FECHA_PAGO")
	private Timestamp fechaPago;
	
	@Column(name="COSTO")
	private Double costo;
	
	@Column(name="FECHA_EXPEDICION")
	private Timestamp fechaExpedicion;
	
	@Column(name="FECHA_SINIESTRO")
	private Timestamp fechaSiniestro;
	
	@Column(name="ESTADO")
	private Integer estado;
	
	@Column(name="MUNICIPIO")
	private Integer municipio;
	
	@Column(name="SANCION")
	private Double sancion;
	
	@Column(name="CONTRAPARTE")
	private String contraparte;
	
	@Column(name="DIAS")
	private Integer dias;
	
	@Column(name="MOTIVO")
	private Integer motivo;
	
	@Column(name="OBSERVACIONES_ACREEDOR")
	private String observacionesAcreedor;
	
	@Column(name="OBSERVACIONES_DEUDOR")
	private String observacionesDeudor;
	
	@Column(name="OBSERVACIONES_COMITE")
	private String observacionesComite;
	
	@Column(name="CIRCUNSTANCIA_DEUDOR")
	private Integer circunstanciaDeudor;
	
	@Column(name="CIRCUNSTANCIA_ACREEDOR")
	private Integer circunstanciaAcreedor;
	
	@Column(name="CAPTURADO")
	private String capturado;
	
	@Column(name="MODIFICADO")
	private String modificado;
	
	@Column(name="TIPO_TRANSPORTE_DEUDOR")
	private Integer tipoTransporteDeudor;
	
	@Column(name="TIPO_TRANSPORTE_ACREEDOR")
	private Integer tipoTransporteAcreedor;
	
	@Column(name="FECHA_CONFIRMACION_PAGO")
	private Timestamp fechaConfirmacionPago;
	
	@Column(name="FECHA_PRIMER_RECHAZO")
	private Timestamp fechaPrimerRechazo;

	public FacOrdenFacturada() {
	}

	public FacOrdenFacturada(Long idOrdenFacturada) {
		this.idOrdenFacturada = idOrdenFacturada;
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

	public Timestamp getFechaEstatusSipac() {
		return fechaEstatusSipac;
	}

	public void setFechaEstatusSipac(Timestamp fechaEstatusSipac) {
		this.fechaEstatusSipac = fechaEstatusSipac;
	}

	public String getEstatusSipac() {
		return estatusSipac;
	}

	public void setEstatusSipac(String estatusSipac) {
		this.estatusSipac = estatusSipac;
	}

	public String getTipoCaptura() {
		return tipoCaptura;
	}

	public void setTipoCaptura(String tipoCaptura) {
		this.tipoCaptura = tipoCaptura;
	}

	public Timestamp getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Timestamp getFechaAceptacion() {
		return fechaAceptacion;
	}

	public void setFechaAceptacion(Timestamp fechaAceptacion) {
		this.fechaAceptacion = fechaAceptacion;
	}

	public Timestamp getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(Timestamp fechaPago) {
		this.fechaPago = fechaPago;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Timestamp getFechaExpedicion() {
		return fechaExpedicion;
	}

	public void setFechaExpedicion(Timestamp fechaExpedicion) {
		this.fechaExpedicion = fechaExpedicion;
	}

	public Timestamp getFechaSiniestro() {
		return fechaSiniestro;
	}

	public void setFechaSiniestro(Timestamp fechaSiniestro) {
		this.fechaSiniestro = fechaSiniestro;
	}

	public Integer getEstado() {
		return estado;
	}

	public void setEstado(Integer estado) {
		this.estado = estado;
	}

	public Integer getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Integer municipio) {
		this.municipio = municipio;
	}

	public Double getSancion() {
		return sancion;
	}

	public void setSancion(Double sancion) {
		this.sancion = sancion;
	}

	public String getContraparte() {
		return contraparte;
	}

	public void setContraparte(String contraparte) {
		this.contraparte = contraparte;
	}

	public Integer getDias() {
		return dias;
	}

	public void setDias(Integer dias) {
		this.dias = dias;
	}

	public Integer getMotivo() {
		return motivo;
	}

	public void setMotivo(Integer motivo) {
		this.motivo = motivo;
	}

	public String getObservacionesAcreedor() {
		return observacionesAcreedor;
	}

	public void setObservacionesAcreedor(String observacionesAcreedor) {
		this.observacionesAcreedor = observacionesAcreedor;
	}

	public String getObservacionesDeudor() {
		return observacionesDeudor;
	}

	public void setObservacionesDeudor(String observacionesDeudor) {
		this.observacionesDeudor = observacionesDeudor;
	}

	public String getObservacionesComite() {
		return observacionesComite;
	}

	public void setObservacionesComite(String observacionesComite) {
		this.observacionesComite = observacionesComite;
	}

	public Integer getCircunstanciaDeudor() {
		return circunstanciaDeudor;
	}

	public void setCircunstanciaDeudor(Integer circunstanciaDeudor) {
		this.circunstanciaDeudor = circunstanciaDeudor;
	}

	public Integer getCircunstanciaAcreedor() {
		return circunstanciaAcreedor;
	}

	public void setCircunstanciaAcreedor(Integer circunstanciaAcreedor) {
		this.circunstanciaAcreedor = circunstanciaAcreedor;
	}

	public String getCapturado() {
		return capturado;
	}

	public void setCapturado(String capturado) {
		this.capturado = capturado;
	}

	public String getModificado() {
		return modificado;
	}

	public void setModificado(String modificado) {
		this.modificado = modificado;
	}

	public Integer getTipoTransporteDeudor() {
		return tipoTransporteDeudor;
	}

	public void setTipoTransporteDeudor(Integer tipoTransporteDeudor) {
		this.tipoTransporteDeudor = tipoTransporteDeudor;
	}

	public Integer getTipoTransporteAcreedor() {
		return tipoTransporteAcreedor;
	}

	public void setTipoTransporteAcreedor(Integer tipoTransporteAcreedor) {
		this.tipoTransporteAcreedor = tipoTransporteAcreedor;
	}

	public Timestamp getFechaConfirmacionPago() {
		return fechaConfirmacionPago;
	}

	public void setFechaConfirmacionPago(Timestamp fechaConfirmacionPago) {
		this.fechaConfirmacionPago = fechaConfirmacionPago;
	}

	public Timestamp getFechaPrimerRechazo() {
		return fechaPrimerRechazo;
	}

	public void setFechaPrimerRechazo(Timestamp fechaPrimerRechazo) {
		this.fechaPrimerRechazo = fechaPrimerRechazo;
	}

}