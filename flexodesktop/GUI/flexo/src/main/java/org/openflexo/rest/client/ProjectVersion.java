
package org.openflexo.rest.client;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="docFileUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docGenerationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="docUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docxfileuuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isIntermediate" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isMergeSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isMerged" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="isProtoValidationSuccessful" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="mergeFromProjectVersion" type="{http://www.agilebirds.com/openflexo}ProjectVersion" minOccurs="0"/>
 *         &lt;element name="prjModelVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="prjVersionId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="prjfileuuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="project" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="projectRevision" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="protoGenerationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="protoLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoPassword" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tocRepositories" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="unmergedVersion" type="{http://www.agilebirds.com/openflexo}ProjectVersion" minOccurs="0"/>
 *         &lt;element name="uploadedWarName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "docFileUuid",
    "docGenerationDate",
    "docUrl",
    "docxfileuuid",
    "isIntermediate",
    "isMergeSuccessful",
    "isMerged",
    "isProtoValidationSuccessful",
    "mergeFromProjectVersion",
    "prjModelVersion",
    "prjVersionId",
    "prjfileuuid",
    "project",
    "projectRevision",
    "protoGenerationDate",
    "protoLogin",
    "protoPassword",
    "protoUrl",
    "tocRepositories",
    "unmergedVersion",
    "uploadedWarName",
    "versionNumber"
})
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
    protected String docFileUuid;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar docGenerationDate;
    protected String docUrl;
    protected String docxfileuuid;
    protected Boolean isIntermediate;
    protected Boolean isMergeSuccessful;
    protected Boolean isMerged;
    protected Boolean isProtoValidationSuccessful;
    protected ProjectVersion mergeFromProjectVersion;
    protected String prjModelVersion;
    protected Integer prjVersionId;
    protected String prjfileuuid;
    protected Integer project;
    protected BigInteger projectRevision;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar protoGenerationDate;
    protected String protoLogin;
    protected String protoPassword;
    protected String protoUrl;
    protected String tocRepositories;
    protected ProjectVersion unmergedVersion;
    protected String uploadedWarName;
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
     * Gets the value of the docFileUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocFileUuid() {
        return docFileUuid;
    }

    /**
     * Sets the value of the docFileUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocFileUuid(String value) {
        this.docFileUuid = value;
    }

    /**
     * Gets the value of the docGenerationDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDocGenerationDate() {
        return docGenerationDate;
    }

    /**
     * Sets the value of the docGenerationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDocGenerationDate(XMLGregorianCalendar value) {
        this.docGenerationDate = value;
    }

    /**
     * Gets the value of the docUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocUrl() {
        return docUrl;
    }

    /**
     * Sets the value of the docUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocUrl(String value) {
        this.docUrl = value;
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
     *     {@link ProjectVersion }
     *     
     */
    public ProjectVersion getMergeFromProjectVersion() {
        return mergeFromProjectVersion;
    }

    /**
     * Sets the value of the mergeFromProjectVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectVersion }
     *     
     */
    public void setMergeFromProjectVersion(ProjectVersion value) {
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
     * Gets the value of the prjVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrjVersionId() {
        return prjVersionId;
    }

    /**
     * Sets the value of the prjVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrjVersionId(Integer value) {
        this.prjVersionId = value;
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
     * Gets the value of the tocRepositories property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTocRepositories() {
        return tocRepositories;
    }

    /**
     * Sets the value of the tocRepositories property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTocRepositories(String value) {
        this.tocRepositories = value;
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
