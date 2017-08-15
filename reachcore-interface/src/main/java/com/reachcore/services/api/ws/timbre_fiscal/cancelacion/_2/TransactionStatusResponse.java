
package com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2;

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
 *         &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Finished" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="FoliosCancelados" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Folios" type="{urn:reachcore.com:services:api:ws:timbre-fiscal:cancelacion:2.0}ArrayOfTransactionDetailResponse" minOccurs="0"/>
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
    "error",
    "errorMessage",
    "finished",
    "foliosCancelados",
    "folios"
})
@XmlRootElement(name = "TransactionStatusResponse")
public class TransactionStatusResponse {

    @XmlElement(name = "Error")
    protected boolean error;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;
    @XmlElement(name = "Finished")
    protected boolean finished;
    @XmlElement(name = "FoliosCancelados")
    protected int foliosCancelados;
    @XmlElement(name = "Folios")
    protected ArrayOfTransactionDetailResponse folios;

    /**
     * Gets the value of the error property.
     * 
     */
    public boolean isError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     */
    public void setError(boolean value) {
        this.error = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Gets the value of the finished property.
     * 
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the value of the finished property.
     * 
     */
    public void setFinished(boolean value) {
        this.finished = value;
    }

    /**
     * Gets the value of the foliosCancelados property.
     * 
     */
    public int getFoliosCancelados() {
        return foliosCancelados;
    }

    /**
     * Sets the value of the foliosCancelados property.
     * 
     */
    public void setFoliosCancelados(int value) {
        this.foliosCancelados = value;
    }

    /**
     * Gets the value of the folios property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfTransactionDetailResponse }
     *     
     */
    public ArrayOfTransactionDetailResponse getFolios() {
        return folios;
    }

    /**
     * Sets the value of the folios property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfTransactionDetailResponse }
     *     
     */
    public void setFolios(ArrayOfTransactionDetailResponse value) {
        this.folios = value;
    }

}
