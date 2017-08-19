package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FAC_USUARIO database table.
 * 
 */
@Entity
@Table(name="FAC_USUARIO")
@NamedQuery(name="FacUsuario.findAll", query="SELECT f FROM FacUsuario f")
public class FacUsuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_USUARIO")
	private Integer idUsuario;

	@Column(name="ACTIVO")
	private Boolean activo;

	@Column(name="APELLIDO_MATERNO")
	private String apellidoMaterno;

	@Column(name="APELLIDO_PATERNO")
	private String apellidoPaterno;

	@Column(name="CORREO_ELECTRONICO")
	private String correoElectronico;

	@Column(name="NOMBRE")
	private String nombre;

	@Column(name="NOMBRE_USUARIO")
	private String nombreUsuario;

	@Column(name="NOTIFICAR_EMAIL")
	private Boolean notificarEmail;

	@Column(name="PASSWORD")
	private String password;

	@Column(name="TELEFONO")
	private String telefono;

	//bi-directional many-to-one association to FacCompaniaApikey
	@OneToMany(mappedBy="facUsuario")
	private List<FacCompaniaApikey> facCompaniaApikeys;

	//bi-directional many-to-one association to FacPeriodoExtemporaneo
	@OneToMany(mappedBy="facUsuario")
	private List<FacPeriodoExtemporaneo> facPeriodoExtemporaneos;

	//bi-directional many-to-one association to Oficina
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="ID_COMPANIA", referencedColumnName="CIA_ID"),
		@JoinColumn(name="OFICINA_ID", referencedColumnName="OFICINA_ID")
		})
	private Oficina oficina;

	//bi-directional many-to-one association to FacRol
	@ManyToOne
	@JoinColumn(name="ID_ROL")
	private FacRol facRol;

	public FacUsuario() {
	}

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getApellidoMaterno() {
		return this.apellidoMaterno;
	}

	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	public String getApellidoPaterno() {
		return this.apellidoPaterno;
	}

	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	public String getCorreoElectronico() {
		return this.correoElectronico;
	}

	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombreUsuario() {
		return this.nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public Boolean getNotificarEmail() {
		return this.notificarEmail;
	}

	public void setNotificarEmail(Boolean notificarEmail) {
		this.notificarEmail = notificarEmail;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public List<FacCompaniaApikey> getFacCompaniaApikeys() {
		return this.facCompaniaApikeys;
	}

	public void setFacCompaniaApikeys(List<FacCompaniaApikey> facCompaniaApikeys) {
		this.facCompaniaApikeys = facCompaniaApikeys;
	}

	public FacCompaniaApikey addFacCompaniaApikey(FacCompaniaApikey facCompaniaApikey) {
		getFacCompaniaApikeys().add(facCompaniaApikey);
		facCompaniaApikey.setFacUsuario(this);

		return facCompaniaApikey;
	}

	public FacCompaniaApikey removeFacCompaniaApikey(FacCompaniaApikey facCompaniaApikey) {
		getFacCompaniaApikeys().remove(facCompaniaApikey);
		facCompaniaApikey.setFacUsuario(null);

		return facCompaniaApikey;
	}

	public List<FacPeriodoExtemporaneo> getFacPeriodoExtemporaneos() {
		return this.facPeriodoExtemporaneos;
	}

	public void setFacPeriodoExtemporaneos(List<FacPeriodoExtemporaneo> facPeriodoExtemporaneos) {
		this.facPeriodoExtemporaneos = facPeriodoExtemporaneos;
	}

	public FacPeriodoExtemporaneo addFacPeriodoExtemporaneo(FacPeriodoExtemporaneo facPeriodoExtemporaneo) {
		getFacPeriodoExtemporaneos().add(facPeriodoExtemporaneo);
		facPeriodoExtemporaneo.setFacUsuario(this);

		return facPeriodoExtemporaneo;
	}

	public FacPeriodoExtemporaneo removeFacPeriodoExtemporaneo(FacPeriodoExtemporaneo facPeriodoExtemporaneo) {
		getFacPeriodoExtemporaneos().remove(facPeriodoExtemporaneo);
		facPeriodoExtemporaneo.setFacUsuario(null);

		return facPeriodoExtemporaneo;
	}

	public Oficina getOficina() {
		return this.oficina;
	}

	public void setOficina(Oficina oficina) {
		this.oficina = oficina;
	}

	public FacRol getFacRol() {
		return this.facRol;
	}

	public void setFacRol(FacRol facRol) {
		this.facRol = facRol;
	}

}