package mx.com.amis.sipac.invoice.persistence.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the OFICINAS database table.
 * 
 */
@Embeddable
public class OficinaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CIA_ID", insertable=false, updatable=false)
	private int ciaId;

	@Column(name="OFICINA_ID", insertable=false, updatable=false)
	private int oficinaId;

	public OficinaPK() {
	}
	public int getCiaId() {
		return this.ciaId;
	}
	public void setCiaId(int ciaId) {
		this.ciaId = ciaId;
	}
	public int getOficinaId() {
		return this.oficinaId;
	}
	public void setOficinaId(int oficinaId) {
		this.oficinaId = oficinaId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OficinaPK)) {
			return false;
		}
		OficinaPK castOther = (OficinaPK)other;
		return 
			(this.ciaId == castOther.ciaId)
			&& (this.oficinaId == castOther.oficinaId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.ciaId;
		hash = hash * prime + this.oficinaId;
		
		return hash;
	}
}