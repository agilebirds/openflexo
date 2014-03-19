
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for JobStatus.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="JobStatus">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="TODO"/>
 *     &lt;enumeration value="STARTING"/>
 *     &lt;enumeration value="IN_PROGRESS"/>
 *     &lt;enumeration value="DONE"/>
 *     &lt;enumeration value="MANUAL_MERGE_SUCCESS"/>
 *     &lt;enumeration value="FAILED"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "JobStatus")
@XmlEnum
public enum JobStatus {

    TODO,
    STARTING,
    IN_PROGRESS,
    DONE,
    MANUAL_MERGE_SUCCESS,
    FAILED;

    public String value() {
        return name();
    }

    public static JobStatus fromValue(String v) {
        return valueOf(v);
    }

}
