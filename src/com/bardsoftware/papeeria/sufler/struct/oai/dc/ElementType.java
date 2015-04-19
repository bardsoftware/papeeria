
package com.bardsoftware.papeeria.sufler.struct.oai.dc;

import javax.annotation.Generated;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * AUTO GENERATED, DO NOT EDIT
 */

/**
 * <p>Java class for elementType complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="elementType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute ref="{http://www.w3.org/XML/1998/namespace}lang"/>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "elementType", namespace = "http://purl.org/dc/elements/1.1/", propOrder = {
        "value"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
public class ElementType {

    @XmlValue
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    protected String value;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "language")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    protected String lang;

    /**
     * Gets the value of the value property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the lang property.
     *
     * @return possible object is
     *         {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the lang property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2015-03-16T01:41:30+04:00", comments = "JAXB RI v2.2.4-2")
    public void setLang(String value) {
        this.lang = value;
    }

}
