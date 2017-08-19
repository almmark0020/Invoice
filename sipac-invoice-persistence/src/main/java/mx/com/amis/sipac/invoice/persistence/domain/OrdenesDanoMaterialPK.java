package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ORDENES_DANO_MATERIAL database table.
 * 
 */
@Embeddable
public class OrdenesDanoMaterialPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="SINIESTRO_ID", insertable=false, updatable=false)
	private int siniestroId;

	@Column(name="FOLIO_ORDEN")
	private String folioOrden;

	public OrdenesDanoMaterialPK() {
	}
	public int getSiniestroId() {
		return this.siniestroId;
	}
	public void setSiniestroId(int siniestroId) {
		this.siniestroId = siniestroId;
	}
	public String getFolioOrden() {
		return this.folioOrden;
	}
	public void setFolioOrden(String folioOrden) {
		this.folioOrden = folioOrden;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OrdenesDanoMaterialPK)) {
			return false;
		}
		OrdenesDanoMaterialPK castOther = (OrdenesDanoMaterialPK)other;
		return 
			(this.siniestroId == castOther.siniestroId)
			&& this.folioOrden.equals(castOther.folioOrden);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.siniestroId;
		hash = hash * prime + this.folioOrden.hashCode();
		
		return hash;
	}
}