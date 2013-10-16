
package org.openflexo.rest.client.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Document complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Document">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.agilebirds.com/openflexo}ModelObject">
 *       &lt;sequence>
 *         &lt;element name="author" type="{http://www.agilebirds.com/openflexo}User" minOccurs="0"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="docType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="documentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="documentId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="generationDocFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="generationDocType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="project" type="{http://www.agilebirds.com/openflexo}Project" minOccurs="0"/>
 *         &lt;element name="projectVersion" type="{http://www.agilebirds.com/openflexo}ProjectVersion" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tocTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Document", propOrder = {
    "author",
    "creationDate",
    "docType",
    "docUuid",
    "documentDate",
    "documentId",
    "generationDocFormat",
    "generationDocType",
    "project",
    "projectVersion",
    "title",
    "tocTitle"
})
@XmlRootElement(name = "Document")
public class Document
    extends ModelObject
{

    protected User author;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String docType;
    protected String docUuid;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar documentDate;
    protected Integer documentId;
    protected String generationDocFormat;
    protected String generationDocType;
    protected Project project;
    protected ProjectVersion projectVersion;
    protected String title;
    protected String tocTitle;

    /**
     * Gets the value of the author property.
     * 
     * @return
     *     possible object is
     *     {@link User }
     *     
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Sets the value of the author property.
     * 
     * @param value
     *     allowed object is
     *     {@link User }
     *     
     */
    public void setAuthor(User value) {
        this.author = value;
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
     * Gets the value of the docType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets the value of the docType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocType(String value) {
        this.docType = value;
    }

    /**
     * Gets the value of the docUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocUuid() {
        return docUuid;
    }

    /**
     * Sets the value of the docUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocUuid(String value) {
        this.docUuid = value;
    }

    /**
     * Gets the value of the documentDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDocumentDate() {
        return documentDate;
    }

    /**
     * Sets the value of the documentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDocumentDate(XMLGregorianCalendar value) {
        this.documentDate = value;
    }

    /**
     * Gets the value of the documentId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDocumentId() {
        return documentId;
    }

    /**
     * Sets the value of the documentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDocumentId(Integer value) {
        this.documentId = value;
    }

    /**
     * Gets the value of the generationDocFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerationDocFormat() {
        return generationDocFormat;
    }

    /**
     * Sets the value of the generationDocFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerationDocFormat(String value) {
        this.generationDocFormat = value;
    }

    /**
     * Gets the value of the generationDocType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerationDocType() {
        return generationDocType;
    }

    /**
     * Sets the value of the generationDocType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerationDocType(String value) {
        this.generationDocType = value;
    }

    /**
     * Gets the value of the project property.
     * 
     * @return
     *     possible object is
     *     {@link Project }
     *     
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets the value of the project property.
     * 
     * @param value
     *     allowed object is
     *     {@link Project }
     *     
     */
    public void setProject(Project value) {
        this.project = value;
    }

    /**
     * Gets the value of the projectVersion property.
     * 
     * @return
     *     possible object is
     *     {@link ProjectVersion }
     *     
     */
    public ProjectVersion getProjectVersion() {
        return projectVersion;
    }

    /**
     * Sets the value of the projectVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectVersion }
     *     
     */
    public void setProjectVersion(ProjectVersion value) {
        this.projectVersion = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the tocTitle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTocTitle() {
        return tocTitle;
    }

    /**
     * Sets the value of the tocTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTocTitle(String value) {
        this.tocTitle = value;
    }

}
