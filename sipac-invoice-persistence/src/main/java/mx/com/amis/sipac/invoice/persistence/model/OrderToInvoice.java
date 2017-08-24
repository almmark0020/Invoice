package mx.com.amis.sipac.invoice.persistence.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class OrderToInvoice implements Serializable {
  private static final long serialVersionUID = -6447715092117918570L;
  
  private Integer siniestroId;
  private String folio;
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
  private String apiKey;
  
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
  public String getApiKey() {
    return apiKey;
  }
  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
