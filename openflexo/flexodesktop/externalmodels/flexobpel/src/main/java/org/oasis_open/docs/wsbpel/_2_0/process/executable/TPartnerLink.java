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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tPartnerLink complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tPartnerLink">
 *   &lt;complexContent>
 *     &lt;extension base="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tExtensibleElements">
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="partnerLinkType" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="myRole" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="partnerRole" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="initializePartnerRole" type="{http://docs.oasis-open.org/wsbpel/2.0/process/executable}tBoolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPartnerLink")
public class TPartnerLink
    extends TExtensibleElements
{

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(required = true)
    protected QName partnerLinkType;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String myRole;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String partnerRole;
    @XmlAttribute
    protected TBoolean initializePartnerRole;

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
     * Gets the value of the partnerLinkType property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getPartnerLinkType() {
        return partnerLinkType;
    }

    /**
     * Sets the value of the partnerLinkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setPartnerLinkType(QName value) {
        this.partnerLinkType = value;
    }

    /**
     * Gets the value of the myRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyRole() {
        return myRole;
    }

    /**
     * Sets the value of the myRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyRole(String value) {
        this.myRole = value;
    }

    /**
     * Gets the value of the partnerRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerRole() {
        return partnerRole;
    }

    /**
     * Sets the value of the partnerRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerRole(String value) {
        this.partnerRole = value;
    }

    /**
     * Gets the value of the initializePartnerRole property.
     * 
     * @return
     *     possible object is
     *     {@link TBoolean }
     *     
     */
    public TBoolean getInitializePartnerRole() {
        return initializePartnerRole;
    }

    /**
     * Sets the value of the initializePartnerRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link TBoolean }
     *     
     */
    public void setInitializePartnerRole(TBoolean value) {
        this.initializePartnerRole = value;
    }

}
