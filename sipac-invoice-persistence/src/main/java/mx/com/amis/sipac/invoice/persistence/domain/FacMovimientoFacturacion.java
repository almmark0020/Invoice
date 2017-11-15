package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the FAC_MOVIMIENTO_FACTURACION database table.
 * 
 */
@Entity
@Table(name="FAC_MOVIMIENTO_FACTURACION")
@NamedQuery(name="FacMovimientoFacturacion.findAll", query="SELECT f FROM FacMovimientoFacturacion f")
public class FacMovimientoFacturacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ID_MOVIMIENTO_FACTURACION")
	private Long idMovimientoFacturacion;

	@Column(name="CFDI_XML")
	private String cfdiXml;

	@Column(name="CFDI_PDF")
	private String cfdiPdf;

	@Column(name="FECHA_MOVIMIENTO")
	private Timestamp fechaMovimiento;

	@Column(name="UUID")
	private String uuid;

	//bi-directional many-to-one association to FacEstatusFacturacion
	@ManyToOne
	@JoinColumn(name="ID_ESTATUS_FACTURACION")
	private FacEstatusFacturacion facEstatusFacturacion;

	//bi-directional many-to-one association to FacOrdenFacturada
	@ManyToOne
	@JoinColumn(name="ID_ORDEN_FACTURADA")
	private FacOrdenFacturada facOrdenFacturada;

	public FacMovimientoFacturacion() {
	}

	public Long getIdMovimientoFacturacion() {
		return this.idMovimientoFacturacion;
	}

	public void setIdMovimientoFacturacion(Long idMovimientoFacturacion) {
		this.idMovimientoFacturacion = idMovimientoFacturacion;
	}

	public String getCfdiXml() {
		return this.cfdiXml;
	}

	public void setCfdiXml(String cfdiXml) {
		this.cfdiXml = cfdiXml;
	}

	public Timestamp getFechaMovimiento() {
		return this.fechaMovimiento;
	}

	public void setFechaMovimiento(Timestamp fechaMovimiento) {
		this.fechaMovimiento = fechaMovimiento;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getCfdiPdf() {
		return cfdiPdf;
	}

	public void setCfdiPdf(String cfdiPdf) {
		this.cfdiPdf = cfdiPdf;
	}

}