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
// Generated on: 2007.11.06 at 03:26:51 PM CET 
//

package org.xmlsoap.schemas.ws._2004._03.business_process;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for tCatch complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tCatch">
 *   &lt;complexContent>
 *     &lt;extension base="{http://schemas.xmlsoap.org/ws/2004/03/business-process/}tActivityContainer">
 *       &lt;attribute name="faultName" type="{http://schemas.xmlsoap.org/ws/2004/03/business-process/}QName" />
 *       &lt;attribute name="faultVariable" type="{http://schemas.xmlsoap.org/ws/2004/03/business-process/}BPELVariableName" />
 *       &lt;attribute name="faultMessageType" type="{http://schemas.xmlsoap.org/ws/2004/03/business-process/}QName" />
 *       &lt;attribute name="faultElement" type="{http://schemas.xmlsoap.org/ws/2004/03/business-process/}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCatch")
public class TCatch extends TActivityContainer {

	@XmlAttribute(namespace = "http://schemas.xmlsoap.org/ws/2004/03/business-process/")
	protected String faultName;
	@XmlAttribute(namespace = "http://schemas.xmlsoap.org/ws/2004/03/business-process/")
	protected String faultVariable;
	@XmlAttribute(namespace = "http://schemas.xmlsoap.org/ws/2004/03/business-process/")
	protected String faultMessageType;
	@XmlAttribute(namespace = "http://schemas.xmlsoap.org/ws/2004/03/business-process/")
	protected String faultElement;

	/**
	 * Gets the value of the faultName property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFaultName() {
		return faultName;
	}

	/**
	 * Sets the value of the faultName property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFaultName(String value) {
		this.faultName = value;
	}

	/**
	 * Gets the value of the faultVariable property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFaultVariable() {
		return faultVariable;
	}

	/**
	 * Sets the value of the faultVariable property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFaultVariable(String value) {
		this.faultVariable = value;
	}

	/**
	 * Gets the value of the faultMessageType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFaultMessageType() {
		return faultMessageType;
	}

	/**
	 * Sets the value of the faultMessageType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFaultMessageType(String value) {
		this.faultMessageType = value;
	}

	/**
	 * Gets the value of the faultElement property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFaultElement() {
		return faultElement;
	}

	/**
	 * Sets the value of the faultElement property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFaultElement(String value) {
		this.faultElement = value;
	}

}
