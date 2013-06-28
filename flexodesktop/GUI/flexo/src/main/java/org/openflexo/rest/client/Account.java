
package org.openflexo.rest.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for Account complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Account">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.agilebirds.com/openflexo}ModelObject">
 *       &lt;sequence>
 *         &lt;element name="accountName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="accountRepositoryUuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="availableDocToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="availableProtoToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="availableServiceToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="canDownloadFlexoRelease" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="clientAccountId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="clientAdmin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractEndDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="contractIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="contractStartDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="docIncidentImpact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docIncidentUrgency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docTargets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="docTokenPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="isActive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="lastTrimestrialIncrementDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="maxActiveProjectCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maxActiveUserCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maxDocToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maxProtoToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maxRunningPrototype" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="maxServiceToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mergeIncidentImpact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mergeIncidentUrgency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mergeMaster" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoIncidentImpact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoIncidentUrgency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="protoTokenPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="serviceTokenPrice" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="trimestrialDocToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="trimestrialProtoToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="trimestrialServiceToken" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Account", propOrder = {
    "accountName",
    "accountRepositoryUuid",
    "availableDocToken",
    "availableProtoToken",
    "availableServiceToken",
    "canDownloadFlexoRelease",
    "clientAccountId",
    "clientAdmin",
    "contractEndDate",
    "contractIdentifier",
    "contractStartDate",
    "creationDate",
    "docIncidentImpact",
    "docIncidentUrgency",
    "docTargets",
    "docTokenPrice",
    "isActive",
    "lastTrimestrialIncrementDate",
    "maxActiveProjectCount",
    "maxActiveUserCount",
    "maxDocToken",
    "maxProtoToken",
    "maxRunningPrototype",
    "maxServiceToken",
    "mergeIncidentImpact",
    "mergeIncidentUrgency",
    "mergeMaster",
    "protoIncidentImpact",
    "protoIncidentUrgency",
    "protoTokenPrice",
    "serviceTokenPrice",
    "trimestrialDocToken",
    "trimestrialProtoToken",
    "trimestrialServiceToken"
})
public class Account
    extends ModelObject
{

    protected String accountName;
    protected String accountRepositoryUuid;
    protected Integer availableDocToken;
    protected Integer availableProtoToken;
    protected Integer availableServiceToken;
    protected Boolean canDownloadFlexoRelease;
    protected Integer clientAccountId;
    protected String clientAdmin;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar contractEndDate;
    protected String contractIdentifier;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar contractStartDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDate;
    protected String docIncidentImpact;
    protected String docIncidentUrgency;
    protected String docTargets;
    protected Double docTokenPrice;
    protected Boolean isActive;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastTrimestrialIncrementDate;
    protected Integer maxActiveProjectCount;
    protected Integer maxActiveUserCount;
    protected Integer maxDocToken;
    protected Integer maxProtoToken;
    protected Integer maxRunningPrototype;
    protected Integer maxServiceToken;
    protected String mergeIncidentImpact;
    protected String mergeIncidentUrgency;
    protected String mergeMaster;
    protected String protoIncidentImpact;
    protected String protoIncidentUrgency;
    protected Double protoTokenPrice;
    protected Double serviceTokenPrice;
    protected Integer trimestrialDocToken;
    protected Integer trimestrialProtoToken;
    protected Integer trimestrialServiceToken;

    /**
     * Gets the value of the accountName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the value of the accountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountName(String value) {
        this.accountName = value;
    }

    /**
     * Gets the value of the accountRepositoryUuid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountRepositoryUuid() {
        return accountRepositoryUuid;
    }

    /**
     * Sets the value of the accountRepositoryUuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountRepositoryUuid(String value) {
        this.accountRepositoryUuid = value;
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
     * Gets the value of the canDownloadFlexoRelease property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCanDownloadFlexoRelease() {
        return canDownloadFlexoRelease;
    }

    /**
     * Sets the value of the canDownloadFlexoRelease property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCanDownloadFlexoRelease(Boolean value) {
        this.canDownloadFlexoRelease = value;
    }

    /**
     * Gets the value of the clientAccountId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getClientAccountId() {
        return clientAccountId;
    }

    /**
     * Sets the value of the clientAccountId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setClientAccountId(Integer value) {
        this.clientAccountId = value;
    }

    /**
     * Gets the value of the clientAdmin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientAdmin() {
        return clientAdmin;
    }

    /**
     * Sets the value of the clientAdmin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientAdmin(String value) {
        this.clientAdmin = value;
    }

    /**
     * Gets the value of the contractEndDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getContractEndDate() {
        return contractEndDate;
    }

    /**
     * Sets the value of the contractEndDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setContractEndDate(XMLGregorianCalendar value) {
        this.contractEndDate = value;
    }

    /**
     * Gets the value of the contractIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContractIdentifier() {
        return contractIdentifier;
    }

    /**
     * Sets the value of the contractIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContractIdentifier(String value) {
        this.contractIdentifier = value;
    }

    /**
     * Gets the value of the contractStartDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getContractStartDate() {
        return contractStartDate;
    }

    /**
     * Sets the value of the contractStartDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setContractStartDate(XMLGregorianCalendar value) {
        this.contractStartDate = value;
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
     * Gets the value of the docIncidentImpact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocIncidentImpact() {
        return docIncidentImpact;
    }

    /**
     * Sets the value of the docIncidentImpact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocIncidentImpact(String value) {
        this.docIncidentImpact = value;
    }

    /**
     * Gets the value of the docIncidentUrgency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocIncidentUrgency() {
        return docIncidentUrgency;
    }

    /**
     * Sets the value of the docIncidentUrgency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocIncidentUrgency(String value) {
        this.docIncidentUrgency = value;
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
     * Gets the value of the docTokenPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDocTokenPrice() {
        return docTokenPrice;
    }

    /**
     * Sets the value of the docTokenPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDocTokenPrice(Double value) {
        this.docTokenPrice = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsActive(Boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the lastTrimestrialIncrementDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastTrimestrialIncrementDate() {
        return lastTrimestrialIncrementDate;
    }

    /**
     * Sets the value of the lastTrimestrialIncrementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastTrimestrialIncrementDate(XMLGregorianCalendar value) {
        this.lastTrimestrialIncrementDate = value;
    }

    /**
     * Gets the value of the maxActiveProjectCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxActiveProjectCount() {
        return maxActiveProjectCount;
    }

    /**
     * Sets the value of the maxActiveProjectCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxActiveProjectCount(Integer value) {
        this.maxActiveProjectCount = value;
    }

    /**
     * Gets the value of the maxActiveUserCount property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxActiveUserCount() {
        return maxActiveUserCount;
    }

    /**
     * Sets the value of the maxActiveUserCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxActiveUserCount(Integer value) {
        this.maxActiveUserCount = value;
    }

    /**
     * Gets the value of the maxDocToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxDocToken() {
        return maxDocToken;
    }

    /**
     * Sets the value of the maxDocToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxDocToken(Integer value) {
        this.maxDocToken = value;
    }

    /**
     * Gets the value of the maxProtoToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxProtoToken() {
        return maxProtoToken;
    }

    /**
     * Sets the value of the maxProtoToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxProtoToken(Integer value) {
        this.maxProtoToken = value;
    }

    /**
     * Gets the value of the maxRunningPrototype property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxRunningPrototype() {
        return maxRunningPrototype;
    }

    /**
     * Sets the value of the maxRunningPrototype property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxRunningPrototype(Integer value) {
        this.maxRunningPrototype = value;
    }

    /**
     * Gets the value of the maxServiceToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxServiceToken() {
        return maxServiceToken;
    }

    /**
     * Sets the value of the maxServiceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxServiceToken(Integer value) {
        this.maxServiceToken = value;
    }

    /**
     * Gets the value of the mergeIncidentImpact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeIncidentImpact() {
        return mergeIncidentImpact;
    }

    /**
     * Sets the value of the mergeIncidentImpact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeIncidentImpact(String value) {
        this.mergeIncidentImpact = value;
    }

    /**
     * Gets the value of the mergeIncidentUrgency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeIncidentUrgency() {
        return mergeIncidentUrgency;
    }

    /**
     * Sets the value of the mergeIncidentUrgency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeIncidentUrgency(String value) {
        this.mergeIncidentUrgency = value;
    }

    /**
     * Gets the value of the mergeMaster property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMergeMaster() {
        return mergeMaster;
    }

    /**
     * Sets the value of the mergeMaster property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMergeMaster(String value) {
        this.mergeMaster = value;
    }

    /**
     * Gets the value of the protoIncidentImpact property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoIncidentImpact() {
        return protoIncidentImpact;
    }

    /**
     * Sets the value of the protoIncidentImpact property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoIncidentImpact(String value) {
        this.protoIncidentImpact = value;
    }

    /**
     * Gets the value of the protoIncidentUrgency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtoIncidentUrgency() {
        return protoIncidentUrgency;
    }

    /**
     * Sets the value of the protoIncidentUrgency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtoIncidentUrgency(String value) {
        this.protoIncidentUrgency = value;
    }

    /**
     * Gets the value of the protoTokenPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getProtoTokenPrice() {
        return protoTokenPrice;
    }

    /**
     * Sets the value of the protoTokenPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setProtoTokenPrice(Double value) {
        this.protoTokenPrice = value;
    }

    /**
     * Gets the value of the serviceTokenPrice property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getServiceTokenPrice() {
        return serviceTokenPrice;
    }

    /**
     * Sets the value of the serviceTokenPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setServiceTokenPrice(Double value) {
        this.serviceTokenPrice = value;
    }

    /**
     * Gets the value of the trimestrialDocToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTrimestrialDocToken() {
        return trimestrialDocToken;
    }

    /**
     * Sets the value of the trimestrialDocToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTrimestrialDocToken(Integer value) {
        this.trimestrialDocToken = value;
    }

    /**
     * Gets the value of the trimestrialProtoToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTrimestrialProtoToken() {
        return trimestrialProtoToken;
    }

    /**
     * Sets the value of the trimestrialProtoToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTrimestrialProtoToken(Integer value) {
        this.trimestrialProtoToken = value;
    }

    /**
     * Gets the value of the trimestrialServiceToken property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTrimestrialServiceToken() {
        return trimestrialServiceToken;
    }

    /**
     * Sets the value of the trimestrialServiceToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTrimestrialServiceToken(Integer value) {
        this.trimestrialServiceToken = value;
    }

}
