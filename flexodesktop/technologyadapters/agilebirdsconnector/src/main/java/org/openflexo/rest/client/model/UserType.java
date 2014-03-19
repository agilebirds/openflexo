
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="userType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ADMIN"/>
 *     &lt;enumeration value="CLIENT_ADMIN"/>
 *     &lt;enumeration value="DEFAULT_USER"/>
 *     &lt;enumeration value="WORKER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "userType")
@XmlEnum
public enum UserType {

    ADMIN,
    CLIENT_ADMIN,
    DEFAULT_USER,
    WORKER;

    public String value() {
        return name();
    }

    public static UserType fromValue(String v) {
        return valueOf(v);
    }

}
