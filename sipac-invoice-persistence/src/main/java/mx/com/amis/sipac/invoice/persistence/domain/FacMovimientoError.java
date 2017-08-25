package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;


/**
 * The persistent class for the FAC_MOVIMIENTO_ERROR database table.
 * 
 */
@Entity
@Table(name="FAC_MOVIMIENTO_ERROR")
@NamedQuery(name="FacMovimientoError.findAll", query="SELECT f FROM FacMovimientoError f")
public class FacMovimientoError implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID_MOVIMIENTO_ERROR")
  private Long idMovimientoError;

  @Column(name="MENSAJE_ERROR")
  private String mensajeError;

  @Column(name="FECHA_MOVIMIENTO")
  private Timestamp fechaMovimiento;

  //bi-directional many-to-one association to FacEstatusFacturacion
  @ManyToOne
  @JoinColumn(name="ID_ESTATUS_FACTURACION")
  private FacEstatusFacturacion facEstatusFacturacion;

  //bi-directional many-to-one association to FacOrdenFacturada
  @ManyToOne
  @JoinColumn(name="ID_ORDEN_FACTURADA")
  private FacOrdenFacturada facOrdenFacturada;

  public FacMovimientoError() {
  }

  public Long getIdMovimientoError() {
    return this.idMovimientoError;
  }

  public void setIdMovimientoError(Long idMovimientoError) {
    this.idMovimientoError = idMovimientoError;
  }

  public String getMensajeError() {
    return this.mensajeError;
  }

  public void setMensajeError(String mensajeError) {
    this.mensajeError = mensajeError;
  }

  public FacEstatusFacturacion getFacEstatusFacturacion() {
    return this.facEstatusFacturacion;
  }

  public void setFacEstatusFacturacion(FacEstatusFacturacion facEstatusFacturacion) {
    this.facEstatusFacturacion = facEstatusFacturacion;
  }

  public FacOrdenFacturada getFacOrdenFacturada() {
    return this.facOrdenFacturada;
  }

  public void setFacOrdenFacturada(FacOrdenFacturada facOrdenFacturada) {
    this.facOrdenFacturada = facOrdenFacturada;
  }

  public Timestamp getFechaMovimiento() {
    return fechaMovimiento;
  }

  public void setFechaMovimiento(Timestamp fechaMovimiento) {
    this.fechaMovimiento = fechaMovimiento;
  }

}