
package com.bardsoftware.papeeria.sufler.struct.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;

/**
 * AUTO GENERATED, DO NOT EDIT
 */

/**
 * Metadata must be expressed in XML that complies
 * with another XML Schema (namespace=#other). Metadata must be
 * explicitly qualified in the response.
 * <p>
 * <p>Java class for metadataType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="metadataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadataType", namespace = "http://www.openarchives.org/OAI/2.0/", propOrder = {
        "any"
})
public class MetadataType {

    @XmlAnyElement(lax = true)
    protected Object any;

    /**
     * Gets the value of the any property.
     *
     * @return possible object is
     * {@link Object }
     */
    public Object getAny() {
        return any;
    }

    /**
     * Sets the value of the any property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setAny(Object value) {
        this.any = value;
    }

}
