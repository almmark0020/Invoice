
package com.reachcore.services.api.ws.pacservices._6;

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
 *         &lt;element name="Comprobante" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CustomData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionProperties" type="{urn:reachcore.com:services:api:ws:pacservices:6.0}ArrayOfTransactionProperty" minOccurs="0"/>
 *         &lt;element name="Domicilios" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "comprobante",
    "customData",
    "transactionProperties",
    "domicilios"
})
@XmlRootElement(name = "EmitirComprobanteRequest")
public class EmitirComprobanteRequest {

    @XmlElement(name = "Comprobante")
    protected String comprobante;
    @XmlElement(name = "CustomData")
    protected String customData;
    @XmlElement(name = "TransactionProperties")
    protected ArrayOfTransactionProperty transactionProperties;
    @XmlElement(name = "Domicilios")
    protected String domicilios;

    /**
     * Gets the value of the comprobante property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComprobante() {
        return comprobante;
    }

    /**
     * Sets the value of the comprobante property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComprobante(String value) {
        this.comprobante = value;
    }

    /**
     * Gets the value of the customData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomData() {
        return customData;
    }

    /**
     * Sets the value of the customData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomData(String value) {
        this.customData = value;
    }

    /**
     * Gets the value of the transactionProperties property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionProperty }
     *     
     */
    public ArrayOfTransactionProperty getTransactionProperties() {
        return transactionProperties;
    }

    /**
     * Sets the value of the transactionProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionProperty }
     *     
     */
    public void setTransactionProperties(ArrayOfTransactionProperty value) {
        this.transactionProperties = value;
    }

    /**
     * Gets the value of the domicilios property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomicilios() {
        return domicilios;
    }

    /**
     * Sets the value of the domicilios property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomicilios(String value) {
        this.domicilios = value;
    }

}
