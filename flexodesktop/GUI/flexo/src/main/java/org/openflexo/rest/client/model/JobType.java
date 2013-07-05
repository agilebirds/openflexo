
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JobType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PROJECT_MERGER"/>
 *     &lt;enumeration value="PROTOTYPE_BUILDER"/>
 *     &lt;enumeration value="DOC_BUILDER"/>
 *     &lt;enumeration value="DOC_REINJECTER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JobType")
@XmlEnum
public enum JobType {

    PROJECT_MERGER,
    PROTOTYPE_BUILDER,
    DOC_BUILDER,
    DOC_REINJECTER;

    public String value() {
        return name();
    }

    public static JobType fromValue(String v) {
        return valueOf(v);
    }

}
