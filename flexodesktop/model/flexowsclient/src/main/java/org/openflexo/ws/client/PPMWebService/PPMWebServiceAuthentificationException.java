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

package org.openflexo.ws.client.PPMWebService;

public class PPMWebServiceAuthentificationException extends org.apache.axis.AxisFault implements java.io.Serializable {
	private java.lang.Object cause1;

	private java.lang.String message1;

	public PPMWebServiceAuthentificationException() {
	}

	public PPMWebServiceAuthentificationException(java.lang.Object cause1, java.lang.String message1) {
		this.cause1 = cause1;
		this.message1 = message1;
	}

	/**
	 * Gets the cause1 value for this PPMWebServiceAuthentificationException.
	 * 
	 * @return cause1
	 */
	public java.lang.Object getCause1() {
		return cause1;
	}

	/**
	 * Sets the cause1 value for this PPMWebServiceAuthentificationException.
	 * 
	 * @param cause1
	 */
	public void setCause1(java.lang.Object cause1) {
		this.cause1 = cause1;
	}

	/**
	 * Gets the message1 value for this PPMWebServiceAuthentificationException.
	 * 
	 * @return message1
	 */
	public java.lang.String getMessage1() {
		return message1;
	}

	/**
	 * Sets the message1 value for this PPMWebServiceAuthentificationException.
	 * 
	 * @param message1
	 */
	public void setMessage1(java.lang.String message1) {
		this.message1 = message1;
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof PPMWebServiceAuthentificationException)) {
			return false;
		}
		PPMWebServiceAuthentificationException other = (PPMWebServiceAuthentificationException) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.cause1 == null && other.getCause1() == null) || (this.cause1 != null && this.cause1.equals(other.getCause1())))
				&& ((this.message1 == null && other.getMessage1() == null) || (this.message1 != null && this.message1.equals(other
						.getMessage1())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	@Override
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getCause1() != null) {
			_hashCode += getCause1().hashCode();
		}
		if (getMessage1() != null) {
			_hashCode += getMessage1().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			PPMWebServiceAuthentificationException.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMWebServiceAuthentificationException"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("cause1");
		elemField.setXmlName(new javax.xml.namespace.QName("", "cause"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("message1");
		elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static org.apache.axis.encoding.Serializer getSerializer(java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Writes the exception data to the faultDetails
	 */
	@Override
	public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context)
			throws java.io.IOException {
		context.serialize(qname, null, this);
	}
}
