package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;


/**
 * The persistent class for the FAC_PERIODO_EXTEMPORANEO database table.
 * 
 */
@Entity
@Table(name="FAC_PERIODO_EXTEMPORANEO")
@NamedQuery(name="FacPeriodoExtemporaneo.findAll", query="SELECT f FROM FacPeriodoExtemporaneo f")
public class FacPeriodoExtemporaneo implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID_PERIODO_EXTEMPORANEO")
  private Long idPeriodoExtemporaneo;

  @Column(name="ACTIVO")
  private Boolean activo;

  @Column(name="NUMERO_DIAS")
  private Integer numeroDias;

  @Column(name="PERIODO")
  private Integer periodo;

  @Column(name="FECHA_REGISTRO")
  private Timestamp fechaRegistro;

  //bi-directional many-to-one association to FacUsuario
  @ManyToOne
  @JoinColumn(name="ID_USUARIO_REGISTRO")
  private FacUsuario facUsuario;

  public FacPeriodoExtemporaneo() {
  }

  public Long getIdPeriodoExtemporaneo() {
    return this.idPeriodoExtemporaneo;
  }

  public void setIdPeriodoExtemporaneo(Long idPeriodoExtemporaneo) {
    this.idPeriodoExtemporaneo = idPeriodoExtemporaneo;
  }

  public Boolean getActivo() {
    return this.activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public Integer getNumeroDias() {
    return this.numeroDias;
  }

  public void setNumeroDias(Integer numeroDias) {
    this.numeroDias = numeroDias;
  }

  public Integer getPeriodo() {
    return this.periodo;
  }

  public void setPeriodo(Integer periodo) {
    this.periodo = periodo;
  }

  public Timestamp getFechaRegistro() {
    return fechaRegistro;
  }

  public void setFechaRegistro(Timestamp fechaRegistro) {
    this.fechaRegistro = fechaRegistro;
  }

  public FacUsuario getFacUsuario() {
    return this.facUsuario;
  }

  public void setFacUsuario(FacUsuario facUsuario) {
    this.facUsuario = facUsuario;
  }

}