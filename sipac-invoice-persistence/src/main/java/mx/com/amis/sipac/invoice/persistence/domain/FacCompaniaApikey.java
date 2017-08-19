package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the FAC_COMPANIA_APIKEY database table.
 * 
 */
@Entity
@Table(name="FAC_COMPANIA_APIKEY")
@NamedQuery(name="FacCompaniaApikey.findAll", query="SELECT f FROM FacCompaniaApikey f")
public class FacCompaniaApikey implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_COMPANIA_APIKEY")
	private Long idCompaniaApikey;

	@Column(name="ACTIVO")
	private Boolean activo;

	@Column(name="APIKEY")
	private String apikey;

	@Column(name="FECHA_REGISTRO")
	private Timestamp fechaRegistro;

	//bi-directional many-to-one association to Compania
	@ManyToOne
	@JoinColumn(name="COMPANIA_ID")
	private Compania compania;

	//bi-directional many-to-one association to FacUsuario
	@ManyToOne
	@JoinColumn(name="ID_USUARIO_REGISTRO")
	private FacUsuario facUsuario;

	public FacCompaniaApikey() {
	}

	public Long getIdCompaniaApikey() {
		return this.idCompaniaApikey;
	}

	public void setIdCompaniaApikey(Long idCompaniaApikey) {
		this.idCompaniaApikey = idCompaniaApikey;
	}

	public Boolean getActivo() {
		return this.activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}

	public String getApikey() {
		return this.apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public Timestamp getFechaRegistro() {
		return this.fechaRegistro;
	}

	public void setFechaRegistro(Timestamp fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public Compania getCompania() {
		return this.compania;
	}

	public void setCompania(Compania compania) {
		this.compania = compania;
	}

	public FacUsuario getFacUsuario() {
		return this.facUsuario;
	}

	public void setFacUsuario(FacUsuario facUsuario) {
		this.facUsuario = facUsuario;
	}

}