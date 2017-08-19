package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FAC_CONFIGURACION database table.
 * 
 */
@Entity
@Table(name="FAC_CONFIGURACION")
@NamedQuery(name="FacConfiguracion.findAll", query="SELECT f FROM FacConfiguracion f")
public class FacConfiguracion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_CONFIGURACION")
	private Integer idConfiguracion;

	@Column(name="CLAVE")
	private String clave;

	@Column(name="DESCRIPCION")
	private String descripcion;

	@Column(name="VALOR")
	private String valor;

	public FacConfiguracion() {
	}

	public Integer getIdConfiguracion() {
		return this.idConfiguracion;
	}

	public void setIdConfiguracion(Integer idConfiguracion) {
		this.idConfiguracion = idConfiguracion;
	}

	public String getClave() {
		return this.clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}