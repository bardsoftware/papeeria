
package com.bardsoftware.papeeria.sufler.struct.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * AUTO GENERATED, DO NOT EDIT
 */

/**
 * <p>Java class for GetRecordType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="GetRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="record" type="{http://www.openarchives.org/OAI/2.0/}recordType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetRecordType", namespace = "http://www.openarchives.org/OAI/2.0/", propOrder = {
        "record"
})
public class GetRecordType {

    @XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/", required = true)
    protected RecordType record;

    /**
     * Gets the value of the record property.
     *
     * @return possible object is
     * {@link RecordType }
     */
    public RecordType getRecord() {
        return record;
    }

    /**
     * Sets the value of the record property.
     *
     * @param value allowed object is
     *              {@link RecordType }
     */
    public void setRecord(RecordType value) {
        this.record = value;
    }

}
