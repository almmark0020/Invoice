package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the ESTATUS database table.
 * 
 */
@Entity
@Table(name="ESTATUS")
@NamedQuery(name="Estatus.findAll", query="SELECT e FROM Estatus e")
public class Estatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ESTATUS_ID")
	private String estatusId;

	@Column(name="DESCRIPCION")
	private String descripcion;

	//bi-directional many-to-one association to OrdenesDanoMaterial
	@OneToMany(mappedBy="estatus")
	private List<OrdenesDanoMaterial> ordenesDanoMaterials;

	//bi-directional many-to-one association to OrdenesGastoMedico
	@OneToMany(mappedBy="estatus")
	private List<OrdenesGastoMedico> ordenesGastoMedicos;

	public Estatus() {
	}

	public String getEstatusId() {
		return this.estatusId;
	}

	public void setEstatusId(String estatusId) {
		this.estatusId = estatusId;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<OrdenesDanoMaterial> getOrdenesDanoMaterials() {
		return this.ordenesDanoMaterials;
	}

	public void setOrdenesDanoMaterials(List<OrdenesDanoMaterial> ordenesDanoMaterials) {
		this.ordenesDanoMaterials = ordenesDanoMaterials;
	}

	public OrdenesDanoMaterial addOrdenesDanoMaterial(OrdenesDanoMaterial ordenesDanoMaterial) {
		getOrdenesDanoMaterials().add(ordenesDanoMaterial);
		ordenesDanoMaterial.setEstatus(this);

		return ordenesDanoMaterial;
	}

	public OrdenesDanoMaterial removeOrdenesDanoMaterial(OrdenesDanoMaterial ordenesDanoMaterial) {
		getOrdenesDanoMaterials().remove(ordenesDanoMaterial);
		ordenesDanoMaterial.setEstatus(null);

		return ordenesDanoMaterial;
	}

	public List<OrdenesGastoMedico> getOrdenesGastoMedicos() {
		return this.ordenesGastoMedicos;
	}

	public void setOrdenesGastoMedicos(List<OrdenesGastoMedico> ordenesGastoMedicos) {
		this.ordenesGastoMedicos = ordenesGastoMedicos;
	}

	public OrdenesGastoMedico addOrdenesGastoMedico(OrdenesGastoMedico ordenesGastoMedico) {
		getOrdenesGastoMedicos().add(ordenesGastoMedico);
		ordenesGastoMedico.setEstatus(this);

		return ordenesGastoMedico;
	}

	public OrdenesGastoMedico removeOrdenesGastoMedico(OrdenesGastoMedico ordenesGastoMedico) {
		getOrdenesGastoMedicos().remove(ordenesGastoMedico);
		ordenesGastoMedico.setEstatus(null);

		return ordenesGastoMedico;
	}

}