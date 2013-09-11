
package org.openflexo.rest.client.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Project complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Project">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.agilebirds.com/openflexo}ModelObject">
 *       &lt;sequence>
 *         &lt;element name="allowConcurrency" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="availableDocToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="availableProtoToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="availableServiceToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="clientAccount" type="{http://www.agilebirds.com/openflexo}Account" minOccurs="0"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dlpmProjectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="dlpmReleaseId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docTargets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docTypes" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="effectiveEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="isActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="isFlexoProject" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="lastAccessDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processModelingProgress" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="projectId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="projectManager" type="{http://www.agilebirds.com/openflexo}User" minOccurs="0"/>
 *         &lt;element name="projectRepositoryUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="projectUri" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoDefaultLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoDefaultPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Project", propOrder = {
    "allowConcurrency",
    "availableDocToken",
    "availableProtoToken",
    "availableServiceToken",
    "clientAccount",
    "creationDate",
    "description",
    "dlpmProjectId",
    "dlpmReleaseId",
    "docTargets",
    "docTypes",
    "effectiveEndDate",
    "endDate",
    "isActive",
    "isFlexoProject",
    "lastAccessDate",
    "name",
    "processModelingProgress",
    "projectId",
    "projectManager",
    "projectRepositoryUuid",
    "projectUri",
    "protoDefaultLogin",
    "protoDefaultPassword",
    "startDate"
})
@XmlRootElement(name = "Project")
public class Project
    extends ModelObject
{

    protected Boolean allowConcurrency;
    protected Integer availableDocToken;
    protected Integer availableProtoToken;
    protected Integer availableServiceToken;
    protected Account clientAccount;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String description;
    protected Integer dlpmProjectId;
    protected String dlpmReleaseId;
    protected String docTargets;
    @XmlElement(nillable = true)
    protected List<String> docTypes;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar effectiveEndDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    protected boolean isActive;
    protected Boolean isFlexoProject;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastAccessDate;
    protected String name;
    protected Integer processModelingProgress;
    protected Integer projectId;
    protected User projectManager;
    protected String projectRepositoryUuid;
    protected String projectUri;
    protected String protoDefaultLogin;
    protected String protoDefaultPassword;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;

    /**
     * Gets the value of the allowConcurrency property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllowConcurrency() {
        return allowConcurrency;
    }

    /**
     * Sets the value of the allowConcurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllowConcurrency(Boolean value) {
        this.allowConcurrency = value;
    }

    /**
     * Gets the value of the availableDocToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAvailableDocToken() {
        return availableDocToken;
    }

    /**
     * Sets the value of the availableDocToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAvailableDocToken(Integer value) {
        this.availableDocToken = value;
    }

    /**
     * Gets the value of the availableProtoToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAvailableProtoToken() {
        return availableProtoToken;
    }

    /**
     * Sets the value of the availableProtoToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAvailableProtoToken(Integer value) {
        this.availableProtoToken = value;
    }

    /**
     * Gets the value of the availableServiceToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAvailableServiceToken() {
        return availableServiceToken;
    }

    /**
     * Sets the value of the availableServiceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAvailableServiceToken(Integer value) {
        this.availableServiceToken = value;
    }

    /**
     * Gets the value of the clientAccount property.
     * 
     * @return
     *     possible object is
     *     {@link Account }
     *     
     */
    public Account getClientAccount() {
        return clientAccount;
    }

    /**
     * Sets the value of the clientAccount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Account }
     *     
     */
    public void setClientAccount(Account value) {
        this.clientAccount = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDate(XMLGregorianCalendar value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the dlpmProjectId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDlpmProjectId() {
        return dlpmProjectId;
    }

    /**
     * Sets the value of the dlpmProjectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDlpmProjectId(Integer value) {
        this.dlpmProjectId = value;
    }

    /**
     * Gets the value of the dlpmReleaseId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDlpmReleaseId() {
        return dlpmReleaseId;
    }

    /**
     * Sets the value of the dlpmReleaseId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDlpmReleaseId(String value) {
        this.dlpmReleaseId = value;
    }

    /**
     * Gets the value of the docTargets property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocTargets() {
        return docTargets;
    }

    /**
     * Sets the value of the docTargets property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocTargets(String value) {
        this.docTargets = value;
    }

    /**
     * Gets the value of the docTypes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the docTypes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocTypes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDocTypes() {
        if (docTypes == null) {
            docTypes = new ArrayList<String>();
        }
        return this.docTypes;
    }

    /**
     * Gets the value of the effectiveEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEffectiveEndDate() {
        return effectiveEndDate;
    }

    /**
     * Sets the value of the effectiveEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEffectiveEndDate(XMLGregorianCalendar value) {
        this.effectiveEndDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the isFlexoProject property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsFlexoProject() {
        return isFlexoProject;
    }

    /**
     * Sets the value of the isFlexoProject property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsFlexoProject(Boolean value) {
        this.isFlexoProject = value;
    }

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
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the processModelingProgress property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProcessModelingProgress() {
        return processModelingProgress;
    }

    /**
     * Sets the value of the processModelingProgress property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProcessModelingProgress(Integer value) {
        this.processModelingProgress = value;
    }

    /**
     * Gets the value of the projectId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * Sets the value of the projectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProjectId(Integer value) {
        this.projectId = value;
    }

    /**
     * Gets the value of the projectManager property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getProjectManager() {
        return projectManager;
    }

    /**
     * Sets the value of the projectManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setProjectManager(User value) {
        this.projectManager = value;
    }

    /**
     * Gets the value of the projectRepositoryUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectRepositoryUuid() {
        return projectRepositoryUuid;
    }

    /**
     * Sets the value of the projectRepositoryUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectRepositoryUuid(String value) {
        this.projectRepositoryUuid = value;
    }

    /**
     * Gets the value of the projectUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProjectUri() {
        return projectUri;
    }

    /**
     * Sets the value of the projectUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProjectUri(String value) {
        this.projectUri = value;
    }

    /**
     * Gets the value of the protoDefaultLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoDefaultLogin() {
        return protoDefaultLogin;
    }

    /**
     * Sets the value of the protoDefaultLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoDefaultLogin(String value) {
        this.protoDefaultLogin = value;
    }

    /**
     * Gets the value of the protoDefaultPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoDefaultPassword() {
        return protoDefaultPassword;
    }

    /**
     * Sets the value of the protoDefaultPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoDefaultPassword(String value) {
        this.protoDefaultPassword = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

}
