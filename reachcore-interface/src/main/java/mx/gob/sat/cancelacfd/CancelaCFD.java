
package mx.gob.sat.cancelacfd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cancelacion" type="{http://cancelacfd.sat.gob.mx}Cancelacion" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cancelacion"
})
@XmlRootElement(name = "CancelaCFD")
public class CancelaCFD {

    @XmlElement(name = "Cancelacion")
    protected Cancelacion cancelacion;

    /**
     * Gets the value of the cancelacion property.
     * 
     * @return
     *     possible object is
     *     {@link Cancelacion }
     *     
     */
    public Cancelacion getCancelacion() {
        return cancelacion;
    }

    /**
     * Sets the value of the cancelacion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Cancelacion }
     *     
     */
    public void setCancelacion(Cancelacion value) {
        this.cancelacion = value;
    }

}
