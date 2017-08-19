package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the OFICINAS database table.
 * 
 */
@Entity
@Table(name="OFICINAS")
@NamedQuery(name="Oficina.findAll", query="SELECT o FROM Oficina o")
public class Oficina implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OficinaPK id;

	@Column(name="COLONIA")
	private String colonia;

	@Column(name="CP")
	private String cp;

	@Column(name="DESCRIPCION")
	private String descripcion;

	@Column(name="DIRECCION")
	private String direccion;

	//bi-directional many-to-one association to FacUsuario
	@OneToMany(mappedBy="oficina")
	private List<FacUsuario> facUsuarios;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="CIA_ID", insertable=false, updatable=false)
	private Compania compania;

	public Oficina() {
	}

	public OficinaPK getId() {
		return this.id;
	}

	public void setId(OficinaPK id) {
		this.id = id;
	}

	public String getColonia() {
		return this.colonia;
	}

	public void setColonia(String colonia) {
		this.colonia = colonia;
	}

	public String getCp() {
		return this.cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public List<FacUsuario> getFacUsuarios() {
		return this.facUsuarios;
	}

	public void setFacUsuarios(List<FacUsuario> facUsuarios) {
		this.facUsuarios = facUsuarios;
	}

	public FacUsuario addFacUsuario(FacUsuario facUsuario) {
		getFacUsuarios().add(facUsuario);
		facUsuario.setOficina(this);

		return facUsuario;
	}

	public FacUsuario removeFacUsuario(FacUsuario facUsuario) {
		getFacUsuarios().remove(facUsuario);
		facUsuario.setOficina(null);

		return facUsuario;
	}

	public Compania getCompania() {
		return this.compania;
	}

	public void setCompania(Compania compania) {
		this.compania = compania;
	}

}