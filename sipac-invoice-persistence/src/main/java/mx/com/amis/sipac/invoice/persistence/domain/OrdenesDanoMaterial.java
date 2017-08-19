package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the ORDENES_DANO_MATERIAL database table.
 * 
 */
@Entity
@Table(name="ORDENES_DANO_MATERIAL")
@NamedQuery(name="OrdenesDanoMaterial.findAll", query="SELECT o FROM OrdenesDanoMaterial o")
public class OrdenesDanoMaterial implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OrdenesDanoMaterialPK id;

	@Column(name="AJUSTADOR_ACREEDOR")
	private String ajustadorAcreedor;

	@Column(name="APELLIDO_MATERNO")
	private String apellidoMaterno;

	@Column(name="APELLIDO_PATERNO")
	private String apellidoPaterno;

	@Column(name="CAUSA_CANC_ID")
	private int causaCancId;

	@Column(name="CIRCUNSTANCIA_ID_ACR")
	private short circunstanciaIdAcr;

	@Column(name="CLAVE_AJUSTADOR")
	private String claveAjustador;

	@Column(name="COBERTURA")
	private String cobertura;

	@Column(name="COLOR_ID")
	private int colorId;

	@Column(name="CONSEC")
	private int consec;

	@Column(name="DANOS_PREEXIST")
	private boolean danosPreexist;

	@Column(name="DESCR_PREEXIST")
	private String descrPreexist;

	@Column(name="DIAS_TRANSC")
	private int diasTransc;

	@Column(name="DIAS_X_VENC")
	private int diasXVenc;

	@Column(name="ESTATUS_STP")
	private int estatusStp;

	@Column(name="FECHA_CAPTURA")
	private Timestamp fechaCaptura;

	@Column(name="FECHA_DICTAMEN")
	private Timestamp fechaDictamen;

	@Column(name="FECHA_ESTATUS")
	private Timestamp fechaEstatus;

	@Column(name="FECHA_EXP")
	private Timestamp fechaExp;

	@Column(name="FECHA_INICIO_JUICIO")
	private Timestamp fechaInicioJuicio;

	@Column(name="IMPORTE_SALDO")
	private BigDecimal importeSaldo;

	@Column(name="INCISO")
	private String inciso;

	@Column(name="MODELO")
	private BigDecimal modelo;

	@Column(name="MOTIVO_ID")
	private int motivoId;

	@Column(name="MOTIVO_OTRO")
	private String motivoOtro;

	@Column(name="NOMBRE_AFECTADO")
	private String nombreAfectado;

	@Column(name="OBS_COMITE")
	private String obsComite;

	@Column(name="OBSERVACIONES")
	private String observaciones;

	@Column(name="OBSERVACIONES_STP")
	private String observacionesStp;

	@Column(name="ORIGEN")
	private String origen;

	@Column(name="PERS_AFECTADO")
	private String persAfectado;

	@Column(name="PLACA")
	private String placa;

	@Column(name="POLIZA_ACREEDOR")
	private String polizaAcreedor;

	@Column(name="PROCEDENCIA")
	private String procedencia;

	@Column(name="PROCEDENCIA_GEST")
	private String procedenciaGest;

	@Column(name="RESULTADO_CESVI")
	private short resultadoCesvi;

	@Column(name="SERIE")
	private String serie;

	@Column(name="SINIESTRO_ACREEDOR")
	private String siniestroAcreedor;

	@Column(name="SINIESTRO_CORRECTO")
	private String siniestroCorrecto;

	@Column(name="TIPO_CAPTURA")
	private String tipoCaptura;

	@Column(name="TIPO_ID")
	private int tipoId;

	@Column(name="USO_ID")
	private short usoId;

	@Column(name="USUARIO_ID")
	private int usuarioId;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="CIA_ACREEDORA")
	private Compania compania;

	//bi-directional many-to-one association to Estatus
	@ManyToOne
	@JoinColumn(name="ESTATUS_ID")
	private Estatus estatus;

	public OrdenesDanoMaterial() {
	}

	public OrdenesDanoMaterialPK getId() {
		return this.id;
	}

	public void setId(OrdenesDanoMaterialPK id) {
		this.id = id;
	}

	public String getAjustadorAcreedor() {
		return this.ajustadorAcreedor;
	}

	public void setAjustadorAcreedor(String ajustadorAcreedor) {
		this.ajustadorAcreedor = ajustadorAcreedor;
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

	public int getCausaCancId() {
		return this.causaCancId;
	}

	public void setCausaCancId(int causaCancId) {
		this.causaCancId = causaCancId;
	}

	public short getCircunstanciaIdAcr() {
		return this.circunstanciaIdAcr;
	}

	public void setCircunstanciaIdAcr(short circunstanciaIdAcr) {
		this.circunstanciaIdAcr = circunstanciaIdAcr;
	}

	public String getClaveAjustador() {
		return this.claveAjustador;
	}

	public void setClaveAjustador(String claveAjustador) {
		this.claveAjustador = claveAjustador;
	}

	public String getCobertura() {
		return this.cobertura;
	}

	public void setCobertura(String cobertura) {
		this.cobertura = cobertura;
	}

	public int getColorId() {
		return this.colorId;
	}

	public void setColorId(int colorId) {
		this.colorId = colorId;
	}

	public int getConsec() {
		return this.consec;
	}

	public void setConsec(int consec) {
		this.consec = consec;
	}

	public boolean getDanosPreexist() {
		return this.danosPreexist;
	}

	public void setDanosPreexist(boolean danosPreexist) {
		this.danosPreexist = danosPreexist;
	}

	public String getDescrPreexist() {
		return this.descrPreexist;
	}

	public void setDescrPreexist(String descrPreexist) {
		this.descrPreexist = descrPreexist;
	}

	public int getDiasTransc() {
		return this.diasTransc;
	}

	public void setDiasTransc(int diasTransc) {
		this.diasTransc = diasTransc;
	}

	public int getDiasXVenc() {
		return this.diasXVenc;
	}

	public void setDiasXVenc(int diasXVenc) {
		this.diasXVenc = diasXVenc;
	}

	public int getEstatusStp() {
		return this.estatusStp;
	}

	public void setEstatusStp(int estatusStp) {
		this.estatusStp = estatusStp;
	}

	public Timestamp getFechaCaptura() {
		return this.fechaCaptura;
	}

	public void setFechaCaptura(Timestamp fechaCaptura) {
		this.fechaCaptura = fechaCaptura;
	}

	public Timestamp getFechaDictamen() {
		return this.fechaDictamen;
	}

	public void setFechaDictamen(Timestamp fechaDictamen) {
		this.fechaDictamen = fechaDictamen;
	}

	public Timestamp getFechaEstatus() {
		return this.fechaEstatus;
	}

	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public Timestamp getFechaExp() {
		return this.fechaExp;
	}

	public void setFechaExp(Timestamp fechaExp) {
		this.fechaExp = fechaExp;
	}

	public Timestamp getFechaInicioJuicio() {
		return this.fechaInicioJuicio;
	}

	public void setFechaInicioJuicio(Timestamp fechaInicioJuicio) {
		this.fechaInicioJuicio = fechaInicioJuicio;
	}

	public BigDecimal getImporteSaldo() {
		return this.importeSaldo;
	}

	public void setImporteSaldo(BigDecimal importeSaldo) {
		this.importeSaldo = importeSaldo;
	}

	public String getInciso() {
		return this.inciso;
	}

	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public BigDecimal getModelo() {
		return this.modelo;
	}

	public void setModelo(BigDecimal modelo) {
		this.modelo = modelo;
	}

	public int getMotivoId() {
		return this.motivoId;
	}

	public void setMotivoId(int motivoId) {
		this.motivoId = motivoId;
	}

	public String getMotivoOtro() {
		return this.motivoOtro;
	}

	public void setMotivoOtro(String motivoOtro) {
		this.motivoOtro = motivoOtro;
	}

	public String getNombreAfectado() {
		return this.nombreAfectado;
	}

	public void setNombreAfectado(String nombreAfectado) {
		this.nombreAfectado = nombreAfectado;
	}

	public String getObsComite() {
		return this.obsComite;
	}

	public void setObsComite(String obsComite) {
		this.obsComite = obsComite;
	}

	public String getObservaciones() {
		return this.observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getObservacionesStp() {
		return this.observacionesStp;
	}

	public void setObservacionesStp(String observacionesStp) {
		this.observacionesStp = observacionesStp;
	}

	public String getOrigen() {
		return this.origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getPersAfectado() {
		return this.persAfectado;
	}

	public void setPersAfectado(String persAfectado) {
		this.persAfectado = persAfectado;
	}

	public String getPlaca() {
		return this.placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getPolizaAcreedor() {
		return this.polizaAcreedor;
	}

	public void setPolizaAcreedor(String polizaAcreedor) {
		this.polizaAcreedor = polizaAcreedor;
	}

	public String getProcedencia() {
		return this.procedencia;
	}

	public void setProcedencia(String procedencia) {
		this.procedencia = procedencia;
	}

	public String getProcedenciaGest() {
		return this.procedenciaGest;
	}

	public void setProcedenciaGest(String procedenciaGest) {
		this.procedenciaGest = procedenciaGest;
	}

	public short getResultadoCesvi() {
		return this.resultadoCesvi;
	}

	public void setResultadoCesvi(short resultadoCesvi) {
		this.resultadoCesvi = resultadoCesvi;
	}

	public String getSerie() {
		return this.serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getSiniestroAcreedor() {
		return this.siniestroAcreedor;
	}

	public void setSiniestroAcreedor(String siniestroAcreedor) {
		this.siniestroAcreedor = siniestroAcreedor;
	}

	public String getSiniestroCorrecto() {
		return this.siniestroCorrecto;
	}

	public void setSiniestroCorrecto(String siniestroCorrecto) {
		this.siniestroCorrecto = siniestroCorrecto;
	}

	public String getTipoCaptura() {
		return this.tipoCaptura;
	}

	public void setTipoCaptura(String tipoCaptura) {
		this.tipoCaptura = tipoCaptura;
	}

	public int getTipoId() {
		return this.tipoId;
	}

	public void setTipoId(int tipoId) {
		this.tipoId = tipoId;
	}

	public short getUsoId() {
		return this.usoId;
	}

	public void setUsoId(short usoId) {
		this.usoId = usoId;
	}

	public int getUsuarioId() {
		return this.usuarioId;
	}

	public void setUsuarioId(int usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Compania getCompania() {
		return this.compania;
	}

	public void setCompania(Compania compania) {
		this.compania = compania;
	}

	public Estatus getEstatus() {
		return this.estatus;
	}

	public void setEstatus(Estatus estatus) {
		this.estatus = estatus;
	}

}