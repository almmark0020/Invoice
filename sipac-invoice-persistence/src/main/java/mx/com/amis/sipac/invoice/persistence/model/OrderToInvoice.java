package mx.com.amis.sipac.invoice.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class OrderToInvoice implements Serializable {
	private static final long serialVersionUID = -6447715092117918570L;

	@Id
	private String id;

	private Long invoiceOrderId;
	private Integer siniestroId;
	private String folio;
	private String siniestroDeudor;
	private String siniestroAcreedor;
	private String polizaDeudor;
	private String polizaAcreedor;
	private String tipoOrden;
	private Integer ciaDeudora;
	private Integer ciaAcreedora;
	private Double monto;
	private String estatus;
	private Timestamp fechaEstatus;
	private String rfcDeudora;
	private String razonSocialDeudora;
	private String regimeFiscalDeudora;
	private String rfcAcreedora;
	private String razonSocialAcreedora;
	private String regimeFiscalAcreedora;
	private String cp;
	private String apiKey;
	private String siniestroCorrecto;

	@Transient
	private int invoiceStatus;
	@Transient
	private byte[] pdf;
	@Transient
	private byte[] xml;
	@Transient
	private byte[] acuseSAT;
	@Transient
	private String error;

	@Transient
	private Timestamp startDate;
	@Transient
	private Timestamp endDate;
	@Transient
	private Timestamp queueDate;
	@Transient
	private Timestamp startReachcoreDate;
	@Transient
	private Timestamp endReachCoreDate;
	@Transient
	private Timestamp startSendEmailDate;
	@Transient
	private Timestamp endSendEmailDate;
	
	@Transient
	private String filesName;
	
	public long getTotalTime() {
	  return endDate.getTime() - startDate.getTime();
	}
	public long getTotalProcessTime() {
	  return endDate.getTime() - startReachcoreDate.getTime();
	}
	public long getReachcoreTime() {
      return endReachCoreDate.getTime() - startReachcoreDate.getTime();
    }
	public long getEmailSendTime() {
      return endDate.getTime() - startSendEmailDate.getTime();
    }

	// for productivity report
	private String tipoCaptura;
	private Timestamp fechaRegistro;
	private Timestamp fechaAceptacion;
	private Timestamp fechaPago;
	private Double costo;
	private Timestamp fechaExpedicion;
	private Timestamp fechaSiniestro;
	private Integer estado;
	private Integer municipio;
	private Double sancion;
	private String contraparte;
	private Integer dias;
	private Integer motivo;
	private String observacionesAcreedor;
	private String observacionesDeudor;
	private String observacionesComite;
	private Integer circunstanciaDeudor;
	private Integer circunstanciaAcreedor;
	private String capturado;
	private String modificado;
	private Integer tipoTransporteDeudor;
	private Integer tipoTransporteAcreedor;
	private Timestamp fechaConfirmacionPago;
	private Timestamp fechaPrimerRechazo;

	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getInvoiceStatus() {
		return invoiceStatus;
	}
	public void setInvoiceStatus(int invoiceStatus) {
		this.invoiceStatus = invoiceStatus;
	}
	public byte[] getPdf() {
		return pdf;
	}
	public void setPdf(byte[] pdf) {
		this.pdf = pdf;
	}
	public byte[] getXml() {
		return xml;
	}
	public void setXml(byte[] xml) {
		this.xml = xml;
	}
	public byte[] getAcuseSAT() {
		return acuseSAT;
	}
	public void setAcuseSAT(byte[] acuseSAT) {
		this.acuseSAT = acuseSAT;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getInvoiceOrderId() {
		return invoiceOrderId;
	}
	public void setInvoiceOrderId(Long invoiceOrderId) {
		this.invoiceOrderId = invoiceOrderId;
	}
	public Integer getSiniestroId() {
		return siniestroId;
	}
	public void setSiniestroId(Integer siniestroId) {
		this.siniestroId = siniestroId;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	}
	public String getSiniestroDeudor() {
		return siniestroDeudor;
	}
	public void setSiniestroDeudor(String siniestroDeudor) {
		this.siniestroDeudor = siniestroDeudor;
	}
	public String getSiniestroAcreedor() {
		return siniestroAcreedor;
	}
	public void setSiniestroAcreedor(String siniestroAcreedor) {
		this.siniestroAcreedor = siniestroAcreedor;
	}
	public String getPolizaDeudor() {
		return polizaDeudor;
	}
	public void setPolizaDeudor(String polizaDeudor) {
		this.polizaDeudor = polizaDeudor;
	}
	public String getPolizaAcreedor() {
		return polizaAcreedor;
	}
	public void setPolizaAcreedor(String polizaAcreedor) {
		this.polizaAcreedor = polizaAcreedor;
	}
	public String getTipoOrden() {
		return tipoOrden;
	}
	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}
	public Integer getCiaDeudora() {
		return ciaDeudora;
	}
	public void setCiaDeudora(Integer ciaDeudora) {
		this.ciaDeudora = ciaDeudora;
	}
	public Integer getCiaAcreedora() {
		return ciaAcreedora;
	}
	public void setCiaAcreedora(Integer ciaAcreedora) {
		this.ciaAcreedora = ciaAcreedora;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public Timestamp getFechaEstatus() {
		return fechaEstatus;
	}
	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}
	public String getRfcDeudora() {
		return rfcDeudora;
	}
	public void setRfcDeudora(String rfcDeudora) {
		this.rfcDeudora = rfcDeudora;
	}
	public String getRazonSocialDeudora() {
		return razonSocialDeudora;
	}
	public void setRazonSocialDeudora(String razonSocialDeudora) {
		this.razonSocialDeudora = razonSocialDeudora;
	}
	public String getRegimeFiscalDeudora() {
		return regimeFiscalDeudora;
	}
	public void setRegimeFiscalDeudora(String regimeFiscalDeudora) {
		this.regimeFiscalDeudora = regimeFiscalDeudora;
	}
	public String getRfcAcreedora() {
		return rfcAcreedora;
	}
	public void setRfcAcreedora(String rfcAcreedora) {
		this.rfcAcreedora = rfcAcreedora;
	}
	public String getRazonSocialAcreedora() {
		return razonSocialAcreedora;
	}
	public void setRazonSocialAcreedora(String razonSocialAcreedora) {
		this.razonSocialAcreedora = razonSocialAcreedora;
	}
	public String getRegimeFiscalAcreedora() {
		return regimeFiscalAcreedora;
	}
	public void setRegimeFiscalAcreedora(String regimeFiscalAcreedora) {
		this.regimeFiscalAcreedora = regimeFiscalAcreedora;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getApiKey() {
		return apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public String getSiniestroCorrecto() {
		return siniestroCorrecto;
	}
	public void setSiniestroCorrecto(String siniestroCorrecto) {
		this.siniestroCorrecto = siniestroCorrecto;
	}
	public Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}
	public Timestamp getEndDate() {
		return endDate;
	}
	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}
	public Timestamp getStartReachcoreDate() {
		return startReachcoreDate;
	}
	public void setStartReachcoreDate(Timestamp startReachcoreDate) {
		this.startReachcoreDate = startReachcoreDate;
	}
	public Timestamp getEndReachCoreDate() {
		return endReachCoreDate;
	}
	public void setEndReachCoreDate(Timestamp endReachCoreDate) {
		this.endReachCoreDate = endReachCoreDate;
	}
	public Timestamp getStartSendEmailDate() {
		return startSendEmailDate;
	}
	public void setStartSendEmailDate(Timestamp startSendEmailDate) {
		this.startSendEmailDate = startSendEmailDate;
	}
	public Timestamp getEndSendEmailDate() {
		return endSendEmailDate;
	}
	public void setEndSendEmailDate(Timestamp endSendEmailDate) {
		this.endSendEmailDate = endSendEmailDate;
	}
	public Timestamp getQueueDate() {
		return queueDate;
	}
	public void setQueueDate(Timestamp queueDate) {
		this.queueDate = queueDate;
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
	public String getFilesName() {
		return filesName;
	}
	public void setFilesName(String filesName) {
		this.filesName = filesName;
	}
}
