
package com.reachcore.services.api.ws.timbre_fiscal.cancelacion._2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfTransactionDetailResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfTransactionDetailResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TransactionDetailResponse" type="{urn:reachcore.com:services:api:ws:timbre-fiscal:cancelacion:2.0}TransactionDetailResponse" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfTransactionDetailResponse", propOrder = {
    "transactionDetailResponse"
})
public class ArrayOfTransactionDetailResponse {

    @XmlElement(name = "TransactionDetailResponse", nillable = true)
    protected List<TransactionDetailResponse> transactionDetailResponse;

    /**
     * Gets the value of the transactionDetailResponse property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transactionDetailResponse property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionDetailResponse().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionDetailResponse }
     * 
     * 
     */
    public List<TransactionDetailResponse> getTransactionDetailResponse() {
        if (transactionDetailResponse == null) {
            transactionDetailResponse = new ArrayList<TransactionDetailResponse>();
        }
        return this.transactionDetailResponse;
    }

    @Override
    public String toString() {
      return "ArrayOfTransactionDetailResponse [transactionDetailResponse=" + transactionDetailResponse + "]";
    }

}
