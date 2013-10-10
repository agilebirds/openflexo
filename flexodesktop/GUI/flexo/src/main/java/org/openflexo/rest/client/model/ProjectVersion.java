
package org.openflexo.rest.client.model;

import java.math.BigInteger;
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
 * <p>Java class for ProjectVersion complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProjectVersion">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.agilebirds.com/openflexo}ModelObject">
 *       &lt;sequence>
 *         &lt;element name="applicationGenerationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="applicationUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="consistencyReport" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="creator" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docxfileuuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isIntermediate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isMergeSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isMerged" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isProtoValidationSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="mergeFromProjectVersion" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="prjModelVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prjfileuuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="project" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="projectRevision" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="protoGenerationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="protoLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tocEntries" type="{http://www.agilebirds.com/openflexo}TocEntryDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="unmergedVersion" type="{http://www.agilebirds.com/openflexo}ProjectVersion" minOccurs="0"/>
 *         &lt;element name="uploadedWarName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="versionID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="versionNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProjectVersion", propOrder = {
    "applicationGenerationDate",
    "applicationUrl",
    "comment",
    "consistencyReport",
    "creationDate",
    "creator",
    "docxfileuuid",
    "isIntermediate",
    "isMergeSuccessful",
    "isMerged",
    "isProtoValidationSuccessful",
    "mergeFromProjectVersion",
    "prjModelVersion",
    "prjfileuuid",
    "project",
    "projectRevision",
    "protoGenerationDate",
    "protoLogin",
    "protoPassword",
    "protoUrl",
    "tocEntries",
    "unmergedVersion",
    "uploadedWarName",
    "versionID",
    "versionNumber"
})
@XmlRootElement(name = "ProjectVersion")
public class ProjectVersion
    extends ModelObject
{

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar applicationGenerationDate;
    protected String applicationUrl;
    protected String comment;
    protected String consistencyReport;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String creator;
    protected String docxfileuuid;
    protected Boolean isIntermediate;
    protected Boolean isMergeSuccessful;
    protected Boolean isMerged;
    protected Boolean isProtoValidationSuccessful;
    protected Integer mergeFromProjectVersion;
    protected String prjModelVersion;
    protected String prjfileuuid;
    protected Integer project;
    protected BigInteger projectRevision;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar protoGenerationDate;
    protected String protoLogin;
    protected String protoPassword;
    protected String protoUrl;
    @XmlElement(nillable = true)
    protected List<TocEntryDefinition> tocEntries;
    protected ProjectVersion unmergedVersion;
    protected String uploadedWarName;
    protected Integer versionID;
    protected String versionNumber;

    /**
     * Gets the value of the applicationGenerationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getApplicationGenerationDate() {
        return applicationGenerationDate;
    }

    /**
     * Sets the value of the applicationGenerationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setApplicationGenerationDate(XMLGregorianCalendar value) {
        this.applicationGenerationDate = value;
    }

    /**
     * Gets the value of the applicationUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationUrl() {
        return applicationUrl;
    }

    /**
     * Sets the value of the applicationUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApplicationUrl(String value) {
        this.applicationUrl = value;
    }

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the consistencyReport property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsistencyReport() {
        return consistencyReport;
    }

    /**
     * Sets the value of the consistencyReport property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsistencyReport(String value) {
        this.consistencyReport = value;
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
     * Gets the value of the creator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Sets the value of the creator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreator(String value) {
        this.creator = value;
    }

    /**
     * Gets the value of the docxfileuuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocxfileuuid() {
        return docxfileuuid;
    }

    /**
     * Sets the value of the docxfileuuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocxfileuuid(String value) {
        this.docxfileuuid = value;
    }

    /**
     * Gets the value of the isIntermediate property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsIntermediate() {
        return isIntermediate;
    }

    /**
     * Sets the value of the isIntermediate property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsIntermediate(Boolean value) {
        this.isIntermediate = value;
    }

    /**
     * Gets the value of the isMergeSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsMergeSuccessful() {
        return isMergeSuccessful;
    }

    /**
     * Sets the value of the isMergeSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsMergeSuccessful(Boolean value) {
        this.isMergeSuccessful = value;
    }

    /**
     * Gets the value of the isMerged property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsMerged() {
        return isMerged;
    }

    /**
     * Sets the value of the isMerged property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsMerged(Boolean value) {
        this.isMerged = value;
    }

    /**
     * Gets the value of the isProtoValidationSuccessful property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsProtoValidationSuccessful() {
        return isProtoValidationSuccessful;
    }

    /**
     * Sets the value of the isProtoValidationSuccessful property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsProtoValidationSuccessful(Boolean value) {
        this.isProtoValidationSuccessful = value;
    }

    /**
     * Gets the value of the mergeFromProjectVersion property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMergeFromProjectVersion() {
        return mergeFromProjectVersion;
    }

    /**
     * Sets the value of the mergeFromProjectVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMergeFromProjectVersion(Integer value) {
        this.mergeFromProjectVersion = value;
    }

    /**
     * Gets the value of the prjModelVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrjModelVersion() {
        return prjModelVersion;
    }

    /**
     * Sets the value of the prjModelVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrjModelVersion(String value) {
        this.prjModelVersion = value;
    }

    /**
     * Gets the value of the prjfileuuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrjfileuuid() {
        return prjfileuuid;
    }

    /**
     * Sets the value of the prjfileuuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrjfileuuid(String value) {
        this.prjfileuuid = value;
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
     * Gets the value of the projectRevision property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getProjectRevision() {
        return projectRevision;
    }

    /**
     * Sets the value of the projectRevision property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setProjectRevision(BigInteger value) {
        this.projectRevision = value;
    }

    /**
     * Gets the value of the protoGenerationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProtoGenerationDate() {
        return protoGenerationDate;
    }

    /**
     * Sets the value of the protoGenerationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProtoGenerationDate(XMLGregorianCalendar value) {
        this.protoGenerationDate = value;
    }

    /**
     * Gets the value of the protoLogin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoLogin() {
        return protoLogin;
    }

    /**
     * Sets the value of the protoLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoLogin(String value) {
        this.protoLogin = value;
    }

    /**
     * Gets the value of the protoPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoPassword() {
        return protoPassword;
    }

    /**
     * Sets the value of the protoPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoPassword(String value) {
        this.protoPassword = value;
    }

    /**
     * Gets the value of the protoUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoUrl() {
        return protoUrl;
    }

    /**
     * Sets the value of the protoUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoUrl(String value) {
        this.protoUrl = value;
    }

    /**
     * Gets the value of the tocEntries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tocEntries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTocEntries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TocEntryDefinition }
     * 
     * 
     */
    public List<TocEntryDefinition> getTocEntries() {
        if (tocEntries == null) {
            tocEntries = new ArrayList<TocEntryDefinition>();
        }
        return this.tocEntries;
    }

    /**
     * Gets the value of the unmergedVersion property.
     * 
     * @return
     *     possible object is
     *     {@link ProjectVersion }
     *     
     */
    public ProjectVersion getUnmergedVersion() {
        return unmergedVersion;
    }

    /**
     * Sets the value of the unmergedVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectVersion }
     *     
     */
    public void setUnmergedVersion(ProjectVersion value) {
        this.unmergedVersion = value;
    }

    /**
     * Gets the value of the uploadedWarName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUploadedWarName() {
        return uploadedWarName;
    }

    /**
     * Sets the value of the uploadedWarName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUploadedWarName(String value) {
        this.uploadedWarName = value;
    }

    /**
     * Gets the value of the versionID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getVersionID() {
        return versionID;
    }

    /**
     * Sets the value of the versionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setVersionID(Integer value) {
        this.versionID = value;
    }

    /**
     * Gets the value of the versionNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * Sets the value of the versionNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionNumber(String value) {
        this.versionNumber = value;
    }

}
