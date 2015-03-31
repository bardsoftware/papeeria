
package com.bardsoftware.papeeria.sufler.struct.oai.dc;

import com.bardsoftware.papeeria.sufler.struct.oai.MetadataType;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for oai_dcType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="oai_dcType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}title"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}creator"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}subject"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}description"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}publisher"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}contributor"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}date"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}type"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}format"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}identifier"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}source"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}language"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}relation"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}coverage"/>
 *         &lt;element ref="{http://purl.org/dc/elements/1.1/}rights"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oai_dcType", namespace = "http://www.openarchives.org/OAI/2.0/oai_dc/", propOrder = {
        "titleOrCreatorOrSubject"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
public class OaiDcType extends MetadataType {

    @XmlElementRefs({
            @XmlElementRef(name = "format", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "identifier", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "rights", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "type", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "title", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "language", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "relation", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "description", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "publisher", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "subject", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "coverage", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "source", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "date", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "creator", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false),
            @XmlElementRef(name = "contributor", namespace = "http://purl.org/dc/elements/1.1/", type = JAXBElement.class, required = false)
    })
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    protected List<JAXBElement<ElementType>> titleOrCreatorOrSubject;

    /**
     * Gets the value of the titleOrCreatorOrSubject property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the titleOrCreatorOrSubject property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitleOrCreatorOrSubject().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     * {@link JAXBElement }{@code <}{@link ElementType }{@code >}
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    public List<JAXBElement<ElementType>> getTitleOrCreatorOrSubject() {
        if (titleOrCreatorOrSubject == null) {
            titleOrCreatorOrSubject = new ArrayList<JAXBElement<ElementType>>();
        }
        return this.titleOrCreatorOrSubject;
    }

}
