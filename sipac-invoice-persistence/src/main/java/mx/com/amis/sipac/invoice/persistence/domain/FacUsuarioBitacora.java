package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the FAC_USUARIO database table.
 * 
 */
@Entity
@Table(name="FAC_USUARIO_BITACORA")
@NamedQuery(name="FacUsuarioBitacora.findAll", query="SELECT f FROM FacUsuarioBitacora f")
public class FacUsuarioBitacora implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="ID_USUARIO_BITACORA")
  private Integer idUsuarioBitacora;

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
  
  @Column(name="FECHA_MODIFICACION")
  private Timestamp fechaModificacion;
  
  @ManyToOne
  @JoinColumn(name="ID_USUARIO")
  private FacUsuario facUsuario;
  
  @ManyToOne
  @JoinColumn(name="ID_USUARIO_REGISTRO")
  private FacUsuario usuarioRegistro;

  public FacUsuarioBitacora() {
  }

  public Integer getIdUsuarioBitacora() {
    return idUsuarioBitacora;
  }

  public void setIdUsuarioBitacora(Integer idUsuarioBitacora) {
    this.idUsuarioBitacora = idUsuarioBitacora;
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

  public FacUsuario getFacUsuario() {
    return facUsuario;
  }

  public void setFacUsuario(FacUsuario facUsuario) {
    this.facUsuario = facUsuario;
  }

  public Timestamp getFechaModificacion() {
    return fechaModificacion;
  }

  public void setFechaModificacion(Timestamp fechaModificacion) {
    this.fechaModificacion = fechaModificacion;
  }

  public FacUsuario getUsuarioRegistro() {
    return usuarioRegistro;
  }

  public void setUsuarioRegistro(FacUsuario usuarioRegistro) {
    this.usuarioRegistro = usuarioRegistro;
  }
}