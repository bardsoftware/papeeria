
package com.bardsoftware.papeeria.sufler.struct.oai;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for verbType.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;simpleType name="verbType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Identify"/>
 *     &lt;enumeration value="ListMetadataFormats"/>
 *     &lt;enumeration value="ListSets"/>
 *     &lt;enumeration value="GetRecord"/>
 *     &lt;enumeration value="ListIdentifiers"/>
 *     &lt;enumeration value="ListRecords"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 */
@XmlType(name = "verbType", namespace = "http://www.openarchives.org/OAI/2.0/")
@XmlEnum
public enum VerbType {

    @XmlEnumValue("Identify")
    IDENTIFY("Identify"),
    @XmlEnumValue("ListMetadataFormats")
    LIST_METADATA_FORMATS("ListMetadataFormats"),
    @XmlEnumValue("ListSets")
    LIST_SETS("ListSets"),
    @XmlEnumValue("GetRecord")
    GET_RECORD("GetRecord"),
    @XmlEnumValue("ListIdentifiers")
    LIST_IDENTIFIERS("ListIdentifiers"),
    @XmlEnumValue("ListRecords")
    LIST_RECORDS("ListRecords");
    private final String value;

    VerbType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VerbType fromValue(String v) {
        for (VerbType c : VerbType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
