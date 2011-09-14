/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.02.08 at 10:43:57 AM CET 
//


package org.oasis_open.docs.wsbpel._2_0.process.executable;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * 
 * 				XSD Authors: The child element correlations needs to be a Local Element Declaration, 
 * 				because there is another correlations element defined for the non-invoke activities.
 * 			
 * 
 * <p>Java class for tInvoke complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tInvoke">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tActivity">
 *       &lt;sequence>
 *         &lt;element name="correlations" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tCorrelationsWithPattern" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}catch" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}catchAll" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}compensationHandler" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}toParts" minOccurs="0"/>
 *         &lt;element ref="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}fromParts" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="partnerLink" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="portType" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="operation" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="inputVariable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *       &lt;attribute name="outputVariable" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}BPELVariableName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tInvoke", propOrder = {
    "correlations",
    "_catch",
    "catchAll",
    "compensationHandler",
    "toParts",
    "fromParts"
})
public class TInvoke
    extends TActivity
{

    protected TCorrelationsWithPattern correlations;
    @XmlElement(name = "catch")
    protected List<TCatch> _catch;
    protected TActivityContainer catchAll;
    protected TActivityContainer compensationHandler;
    protected TToParts toParts;
    protected TFromParts fromParts;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String partnerLink;
    @XmlAttribute
    protected QName portType;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String operation;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String inputVariable;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String outputVariable;

    /**
     * Gets the value of the correlations property.
     * 
     * @return
     *     possible object is
     *     {@link TCorrelationsWithPattern }
     *     
     */
    public TCorrelationsWithPattern getCorrelations() {
        return correlations;
    }

    /**
     * Sets the value of the correlations property.
     * 
     * @param value
     *     allowed object is
     *     {@link TCorrelationsWithPattern }
     *     
     */
    public void setCorrelations(TCorrelationsWithPattern value) {
        this.correlations = value;
    }

    /**
     * Gets the value of the catch property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the catch property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCatch().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TCatch }
     * 
     * 
     */
    public List<TCatch> getCatch() {
        if (_catch == null) {
            _catch = new ArrayList<TCatch>();
        }
        return this._catch;
    }

    /**
     * Gets the value of the catchAll property.
     * 
     * @return
     *     possible object is
     *     {@link TActivityContainer }
     *     
     */
    public TActivityContainer getCatchAll() {
        return catchAll;
    }

    /**
     * Sets the value of the catchAll property.
     * 
     * @param value
     *     allowed object is
     *     {@link TActivityContainer }
     *     
     */
    public void setCatchAll(TActivityContainer value) {
        this.catchAll = value;
    }

    /**
     * Gets the value of the compensationHandler property.
     * 
     * @return
     *     possible object is
     *     {@link TActivityContainer }
     *     
     */
    public TActivityContainer getCompensationHandler() {
        return compensationHandler;
    }

    /**
     * Sets the value of the compensationHandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link TActivityContainer }
     *     
     */
    public void setCompensationHandler(TActivityContainer value) {
        this.compensationHandler = value;
    }

    /**
     * Gets the value of the toParts property.
     * 
     * @return
     *     possible object is
     *     {@link TToParts }
     *     
     */
    public TToParts getToParts() {
        return toParts;
    }

    /**
     * Sets the value of the toParts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TToParts }
     *     
     */
    public void setToParts(TToParts value) {
        this.toParts = value;
    }

    /**
     * Gets the value of the fromParts property.
     * 
     * @return
     *     possible object is
     *     {@link TFromParts }
     *     
     */
    public TFromParts getFromParts() {
        return fromParts;
    }

    /**
     * Sets the value of the fromParts property.
     * 
     * @param value
     *     allowed object is
     *     {@link TFromParts }
     *     
     */
    public void setFromParts(TFromParts value) {
        this.fromParts = value;
    }

    /**
     * Gets the value of the partnerLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerLink() {
        return partnerLink;
    }

    /**
     * Sets the value of the partnerLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerLink(String value) {
        this.partnerLink = value;
    }

    /**
     * Gets the value of the portType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPortType() {
        return portType;
    }

    /**
     * Sets the value of the portType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPortType(QName value) {
        this.portType = value;
    }

    /**
     * Gets the value of the operation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Sets the value of the operation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperation(String value) {
        this.operation = value;
    }

    /**
     * Gets the value of the inputVariable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputVariable() {
        return inputVariable;
    }

    /**
     * Sets the value of the inputVariable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputVariable(String value) {
        this.inputVariable = value;
    }

    /**
     * Gets the value of the outputVariable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputVariable() {
        return outputVariable;
    }

    /**
     * Sets the value of the outputVariable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputVariable(String value) {
        this.outputVariable = value;
    }

}
