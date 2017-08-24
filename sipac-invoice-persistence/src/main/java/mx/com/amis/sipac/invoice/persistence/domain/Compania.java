package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the COMPANIAS database table.
 * 
 */
@Entity
@Table(name="COMPANIAS")
@NamedQuery(name="Compania.findAll", query="SELECT c FROM Compania c")
public class Compania implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  @Column(name="CIA_ID")
  private Integer ciaId;

  @Column(name="ESTATUS")
  private String estatus;

  @Column(name="NOMBRE")
  private String nombre;

  @Column(name="NOMBRE_CORTO")
  private String nombreCorto;

  //bi-directional many-to-one association to FacCompaniaApikey
  @OneToMany(mappedBy="compania")
  private List<FacCompaniaApikey> facCompaniaApikeys;

  //bi-directional many-to-one association to FacOrdenFacturada
  @OneToMany(mappedBy="compania1")
  private List<FacOrdenFacturada> facOrdenFacturadas1;

  //bi-directional many-to-one association to FacOrdenFacturada
  @OneToMany(mappedBy="compania2")
  private List<FacOrdenFacturada> facOrdenFacturadas2;

  //bi-directional many-to-one association to Oficina
  @OneToMany(mappedBy="compania")
  private List<Oficina> oficinas;

  //bi-directional many-to-one association to OrdenesDanoMaterial
  @OneToMany(mappedBy="compania")
  private List<OrdenesDanoMaterial> ordenesDanoMaterials;

  //bi-directional many-to-one association to OrdenesGastoMedico
  @OneToMany(mappedBy="compania")
  private List<OrdenesGastoMedico> ordenesGastoMedicos;

  public Compania() {
  }

  public Compania(Integer ciaId) {
    this.ciaId = ciaId;
  }

  public Integer getCiaId() {
    return this.ciaId;
  }

  public void setCiaId(Integer ciaId) {
    this.ciaId = ciaId;
  }

  public String getEstatus() {
    return this.estatus;
  }

  public void setEstatus(String estatus) {
    this.estatus = estatus;
  }

  public String getNombre() {
    return this.nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombreCorto() {
    return this.nombreCorto;
  }

  public void setNombreCorto(String nombreCorto) {
    this.nombreCorto = nombreCorto;
  }

  public List<FacCompaniaApikey> getFacCompaniaApikeys() {
    return this.facCompaniaApikeys;
  }

  public void setFacCompaniaApikeys(List<FacCompaniaApikey> facCompaniaApikeys) {
    this.facCompaniaApikeys = facCompaniaApikeys;
  }

  public FacCompaniaApikey addFacCompaniaApikey(FacCompaniaApikey facCompaniaApikey) {
    getFacCompaniaApikeys().add(facCompaniaApikey);
    facCompaniaApikey.setCompania(this);

    return facCompaniaApikey;
  }

  public FacCompaniaApikey removeFacCompaniaApikey(FacCompaniaApikey facCompaniaApikey) {
    getFacCompaniaApikeys().remove(facCompaniaApikey);
    facCompaniaApikey.setCompania(null);

    return facCompaniaApikey;
  }

  public List<FacOrdenFacturada> getFacOrdenFacturadas1() {
    return this.facOrdenFacturadas1;
  }

  public void setFacOrdenFacturadas1(List<FacOrdenFacturada> facOrdenFacturadas1) {
    this.facOrdenFacturadas1 = facOrdenFacturadas1;
  }

  public FacOrdenFacturada addFacOrdenFacturadas1(FacOrdenFacturada facOrdenFacturadas1) {
    getFacOrdenFacturadas1().add(facOrdenFacturadas1);
    facOrdenFacturadas1.setCompania1(this);

    return facOrdenFacturadas1;
  }

  public FacOrdenFacturada removeFacOrdenFacturadas1(FacOrdenFacturada facOrdenFacturadas1) {
    getFacOrdenFacturadas1().remove(facOrdenFacturadas1);
    facOrdenFacturadas1.setCompania1(null);

    return facOrdenFacturadas1;
  }

  public List<FacOrdenFacturada> getFacOrdenFacturadas2() {
    return this.facOrdenFacturadas2;
  }

  public void setFacOrdenFacturadas2(List<FacOrdenFacturada> facOrdenFacturadas2) {
    this.facOrdenFacturadas2 = facOrdenFacturadas2;
  }

  public FacOrdenFacturada addFacOrdenFacturadas2(FacOrdenFacturada facOrdenFacturadas2) {
    getFacOrdenFacturadas2().add(facOrdenFacturadas2);
    facOrdenFacturadas2.setCompania2(this);

    return facOrdenFacturadas2;
  }

  public FacOrdenFacturada removeFacOrdenFacturadas2(FacOrdenFacturada facOrdenFacturadas2) {
    getFacOrdenFacturadas2().remove(facOrdenFacturadas2);
    facOrdenFacturadas2.setCompania2(null);

    return facOrdenFacturadas2;
  }

  public List<Oficina> getOficinas() {
    return this.oficinas;
  }

  public void setOficinas(List<Oficina> oficinas) {
    this.oficinas = oficinas;
  }

  public Oficina addOficina(Oficina oficina) {
    getOficinas().add(oficina);
    oficina.setCompania(this);

    return oficina;
  }

  public Oficina removeOficina(Oficina oficina) {
    getOficinas().remove(oficina);
    oficina.setCompania(null);

    return oficina;
  }

  public List<OrdenesDanoMaterial> getOrdenesDanoMaterials() {
    return this.ordenesDanoMaterials;
  }

  public void setOrdenesDanoMaterials(List<OrdenesDanoMaterial> ordenesDanoMaterials) {
    this.ordenesDanoMaterials = ordenesDanoMaterials;
  }

  public OrdenesDanoMaterial addOrdenesDanoMaterial(OrdenesDanoMaterial ordenesDanoMaterial) {
    getOrdenesDanoMaterials().add(ordenesDanoMaterial);
    ordenesDanoMaterial.setCompania(this);

    return ordenesDanoMaterial;
  }

  public OrdenesDanoMaterial removeOrdenesDanoMaterial(OrdenesDanoMaterial ordenesDanoMaterial) {
    getOrdenesDanoMaterials().remove(ordenesDanoMaterial);
    ordenesDanoMaterial.setCompania(null);

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
    ordenesGastoMedico.setCompania(this);

    return ordenesGastoMedico;
  }

  public OrdenesGastoMedico removeOrdenesGastoMedico(OrdenesGastoMedico ordenesGastoMedico) {
    getOrdenesGastoMedicos().remove(ordenesGastoMedico);
    ordenesGastoMedico.setCompania(null);

    return ordenesGastoMedico;
  }

}