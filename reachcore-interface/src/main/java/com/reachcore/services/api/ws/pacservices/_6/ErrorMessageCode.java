
package com.reachcore.services.api.ws.pacservices._6;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorMessageCode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ErrorMessageCode">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:reachcore.com:services:api:ws:pacservices:6.0}MessageCode">
 *       &lt;sequence>
 *         &lt;element name="Target" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Details" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InnerErrors" type="{urn:reachcore.com:services:api:ws:pacservices:6.0}ArrayOfErrorMessageCode" minOccurs="0"/>
 *         &lt;element name="MoreInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ErrorMessageCode", propOrder = {
    "target",
    "details",
    "innerErrors",
    "moreInfo"
})
public class ErrorMessageCode
    extends MessageCode
{

    @XmlElement(name = "Target")
    protected String target;
    @XmlElement(name = "Details")
    protected String details;
    @XmlElement(name = "InnerErrors")
    protected ArrayOfErrorMessageCode innerErrors;
    @XmlElement(name = "MoreInfo")
    protected String moreInfo;

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the details property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetails() {
        return details;
    }

    /**
     * Sets the value of the details property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetails(String value) {
        this.details = value;
    }

    /**
     * Gets the value of the innerErrors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfErrorMessageCode }
     *     
     */
    public ArrayOfErrorMessageCode getInnerErrors() {
        return innerErrors;
    }

    /**
     * Sets the value of the innerErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfErrorMessageCode }
     *     
     */
    public void setInnerErrors(ArrayOfErrorMessageCode value) {
        this.innerErrors = value;
    }

    /**
     * Gets the value of the moreInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoreInfo() {
        return moreInfo;
    }

    /**
     * Sets the value of the moreInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoreInfo(String value) {
        this.moreInfo = value;
    }

    @Override
    public String toString() {
      return "ErrorMessageCode [target=" + target + ", details=" + details + ", innerErrors=" + innerErrors
          + ", moreInfo=" + moreInfo + "]";
    }

}
