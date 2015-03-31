
package com.bardsoftware.papeeria.sufler.struct.oai;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListMetadataFormatsType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="ListMetadataFormatsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="metadataFormat" type="{http://www.openarchives.org/OAI/2.0/}metadataFormatType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListMetadataFormatsType", namespace = "http://www.openarchives.org/OAI/2.0/", propOrder = {
        "metadataFormat"
})
public class ListMetadataFormatsType {

    @XmlElement(namespace = "http://www.openarchives.org/OAI/2.0/", required = true)
    protected List<MetadataFormatType> metadataFormat;

    /**
     * Gets the value of the metadataFormat property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the metadataFormat property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMetadataFormat().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link MetadataFormatType }
     */
    public List<MetadataFormatType> getMetadataFormat() {
        if (metadataFormat == null) {
            metadataFormat = new ArrayList<MetadataFormatType>();
        }
        return this.metadataFormat;
    }

}
