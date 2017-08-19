package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the FAC_ROL database table.
 * 
 */
@Entity
@Table(name="FAC_ROL")
@NamedQuery(name="FacRol.findAll", query="SELECT f FROM FacRol f")
public class FacRol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_ROL")
	private Integer idRol;

	@Column(name="ACTIVO")
	private Boolean activo;

	@Column(name="DESCRIPCION")
	private String descripcion;

	//bi-directional many-to-one association to FacUsuario
	@OneToMany(mappedBy="facRol")
	private List<FacUsuario> facUsuarios;

	public FacRol() {
	}

	public Integer getIdRol() {
		return this.idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<FacUsuario> getFacUsuarios() {
		return this.facUsuarios;
	}

	public void setFacUsuarios(List<FacUsuario> facUsuarios) {
		this.facUsuarios = facUsuarios;
	}

	public FacUsuario addFacUsuario(FacUsuario facUsuario) {
		getFacUsuarios().add(facUsuario);
		facUsuario.setFacRol(this);

		return facUsuario;
	}

	public FacUsuario removeFacUsuario(FacUsuario facUsuario) {
		getFacUsuarios().remove(facUsuario);
		facUsuario.setFacRol(null);

		return facUsuario;
	}

}