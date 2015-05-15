
package com.bardsoftware.papeeria.sufler.struct.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * AUTO GENERATED, DO NOT EDIT
 */

/**
 * <p>Java class for ListRecordsType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="ListRecordsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="record" type="{http://www.openarchives.org/OAI/2.0/}recordType" maxOccurs="unbounded"/>
 *         &lt;element name="resumptionToken" type="{http://www.openarchives.org/OAI/2.0/}resumptionTokenType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListRecordsType", namespace = "http://www.openarchives.org/OAI/2.0/", propOrder = {
        "record",
        "resumptionToken"
})
public class ListRecordsType {

    @XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/", required = true)
    protected List<RecordType> record;
    @XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/")
    protected ResumptionTokenType resumptionToken;

    /**
     * Gets the value of the record property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the record property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRecord().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RecordType }
     */
    public List<RecordType> getRecord() {
        if (record == null) {
            record = new ArrayList<RecordType>();
        }
        return this.record;
    }

    /**
     * Gets the value of the resumptionToken property.
     *
     * @return possible object is
     * {@link ResumptionTokenType }
     */
    public ResumptionTokenType getResumptionToken() {
        return resumptionToken;
    }

    /**
     * Sets the value of the resumptionToken property.
     *
     * @param value allowed object is
     *              {@link ResumptionTokenType }
     */
    public void setResumptionToken(ResumptionTokenType value) {
        this.resumptionToken = value;
    }

}
