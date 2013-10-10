
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for UserProject complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserProject">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.agilebirds.com/openflexo}ModelObject">
 *       &lt;sequence>
 *         &lt;element name="lastAccessDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="needEmailNotification" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="project" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="userProjectPK" type="{http://www.agilebirds.com/openflexo}UserProjectPK" minOccurs="0"/>
 *         &lt;element name="userRole" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserProject", propOrder = {
    "lastAccessDate",
    "needEmailNotification",
    "project",
    "user",
    "userProjectPK",
    "userRole"
})
@XmlRootElement(name = "UserProject")
public class UserProject
    extends ModelObject
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastAccessDate;
    protected Boolean needEmailNotification;
    protected Integer project;
    protected String user;
    protected UserProjectPK userProjectPK;
    protected String userRole;

    /**
     * Gets the value of the lastAccessDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastAccessDate() {
        return lastAccessDate;
    }

    /**
     * Sets the value of the lastAccessDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastAccessDate(XMLGregorianCalendar value) {
        this.lastAccessDate = value;
    }

    /**
     * Gets the value of the needEmailNotification property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNeedEmailNotification() {
        return needEmailNotification;
    }

    /**
     * Sets the value of the needEmailNotification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNeedEmailNotification(Boolean value) {
        this.needEmailNotification = value;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProject(Integer value) {
        this.project = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the userProjectPK property.
     * 
     * @return
     *     possible object is
     *     {@link UserProjectPK }
     *     
     */
    public UserProjectPK getUserProjectPK() {
        return userProjectPK;
    }

    /**
     * Sets the value of the userProjectPK property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserProjectPK }
     *     
     */
    public void setUserProjectPK(UserProjectPK value) {
        this.userProjectPK = value;
    }

    /**
     * Gets the value of the userRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserRole() {
        return userRole;
    }

    /**
     * Sets the value of the userRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserRole(String value) {
        this.userRole = value;
    }

}
