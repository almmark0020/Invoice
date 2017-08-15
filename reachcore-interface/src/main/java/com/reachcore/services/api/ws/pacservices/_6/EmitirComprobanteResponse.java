
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
 *         &lt;element name="TransactionId" type="{http://microsoft.com/wsdl/types/}guid"/>
 *         &lt;element name="Error" type="{urn:reachcore.com:services:api:ws:pacservices:6.0}ErrorMessageCode" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TransactionProperties" type="{urn:reachcore.com:services:api:ws:pacservices:6.0}ArrayOfTransactionProperty" minOccurs="0"/>
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
    "transactionId",
    "error",
    "result",
    "transactionProperties"
})
@XmlRootElement(name = "EmitirComprobanteResponse")
public class EmitirComprobanteResponse {

    @XmlElement(name = "TransactionId", required = true)
    protected String transactionId;
    @XmlElement(name = "Error")
    protected ErrorMessageCode error;
    @XmlElement(name = "Result")
    protected String result;
    @XmlElement(name = "TransactionProperties")
    protected ArrayOfTransactionProperty transactionProperties;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorMessageCode }
     *     
     */
    public ErrorMessageCode getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorMessageCode }
     *     
     */
    public void setError(ErrorMessageCode value) {
        this.error = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResult(String value) {
        this.result = value;
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

    @Override
    public String toString() {
      return "EmitirComprobanteResponse [transactionId=" + transactionId + ", error=" + error + ", result=" + result
          + ", transactionProperties=" + transactionProperties + "]";
    }

}
