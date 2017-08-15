
package com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransactionDetailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionDetailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FolioFiscal" type="{http://microsoft.com/wsdl/types/}guid"/>
 *         &lt;element name="Cancelado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResultMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionDetailResponse", propOrder = {
    "folioFiscal",
    "cancelado",
    "resultCode",
    "resultMessage"
})
public class TransactionDetailResponse {

    @XmlElement(name = "FolioFiscal", required = true)
    protected String folioFiscal;
    @XmlElement(name = "Cancelado")
    protected boolean cancelado;
    @XmlElement(name = "ResultCode")
    protected String resultCode;
    @XmlElement(name = "ResultMessage")
    protected String resultMessage;

    /**
     * Gets the value of the folioFiscal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolioFiscal() {
        return folioFiscal;
    }

    /**
     * Sets the value of the folioFiscal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolioFiscal(String value) {
        this.folioFiscal = value;
    }

    /**
     * Gets the value of the cancelado property.
     * 
     */
    public boolean isCancelado() {
        return cancelado;
    }

    /**
     * Sets the value of the cancelado property.
     * 
     */
    public void setCancelado(boolean value) {
        this.cancelado = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the resultMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultMessage() {
        return resultMessage;
    }

    /**
     * Sets the value of the resultMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResultMessage(String value) {
        this.resultMessage = value;
    }

    @Override
    public String toString() {
      return "TransactionDetailResponse [folioFiscal=" + folioFiscal + ", cancelado=" + cancelado + ", resultCode="
          + resultCode + ", resultMessage=" + resultMessage + "]";
    }

}
