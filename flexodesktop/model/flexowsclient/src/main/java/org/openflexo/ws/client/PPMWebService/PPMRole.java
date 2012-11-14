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

import java.util.StringTokenizer;

public class PPMRole extends PPMObject implements java.io.Serializable {
	private java.lang.String rgbColor;

	public PPMRole() {
	}

	public PPMRole(java.lang.String businessDescription, java.lang.String generalDescription, java.lang.String name,
			java.lang.String rgbColor, java.lang.String technicalDescription, java.lang.String uri, java.lang.String userManualDescription,
			java.lang.String versionUri) {
		this.businessDescription = businessDescription;
		this.generalDescription = generalDescription;
		this.name = name;
		this.rgbColor = rgbColor;
		this.technicalDescription = technicalDescription;
		this.uri = uri;
		this.userManualDescription = userManualDescription;
		this.versionUri = versionUri;
	}

	/**
	 * Gets the rgbColor value for this PPMRole.
	 * 
	 * @return rgbColor
	 */
	public java.lang.String getRgbColor() {
		return rgbColor;
	}

	/**
	 * Sets the rgbColor value for this PPMRole.
	 * 
	 * @param rgbColor
	 */
	public void setRgbColor(java.lang.String rgbColor) {
		this.rgbColor = rgbColor;
	}

	public int[] getRgb() {
		if (getRgbColor() == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(getRgbColor(), ",");
		int[] rgb = new int[3];
		rgb[0] = Integer.parseInt(st.nextToken());
		rgb[1] = Integer.parseInt(st.nextToken());
		rgb[2] = Integer.parseInt(st.nextToken());
		return rgb;
	}

	public int getRed() {
		int[] rgb = getRgb();
		if (rgb == null) {
			return -1;
		}
		return rgb[0];
	}

	public int getGreen() {
		int[] rgb = getRgb();
		if (rgb == null) {
			return -1;
		}
		return rgb[1];
	}

	public int getBlue() {
		int[] rgb = getRgb();
		if (rgb == null) {
			return -1;
		}
		return rgb[2];
	}

	public void setRgbColor(int red, int green, int blue) {
		setRgbColor(String.valueOf(red) + "," + String.valueOf(green) + "," + String.valueOf(blue));
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof PPMRole)) {
			return false;
		}
		PPMRole other = (PPMRole) obj;
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (__equalsCalc != null) {
			return __equalsCalc == obj;
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& (this.businessDescription == null && other.getBusinessDescription() == null || this.businessDescription != null
						&& this.businessDescription.equals(other.getBusinessDescription()))
				&& (this.generalDescription == null && other.getGeneralDescription() == null || this.generalDescription != null
						&& this.generalDescription.equals(other.getGeneralDescription()))
				&& (this.name == null && other.getName() == null || this.name != null && this.name.equals(other.getName()))
				&& (this.rgbColor == null && other.getRgbColor() == null || this.rgbColor != null
						&& this.rgbColor.equals(other.getRgbColor()))
				&& (this.technicalDescription == null && other.getTechnicalDescription() == null || this.technicalDescription != null
						&& this.technicalDescription.equals(other.getTechnicalDescription()))
				&& (this.uri == null && other.getUri() == null || this.uri != null && this.uri.equals(other.getUri()))
				&& (this.userManualDescription == null && other.getUserManualDescription() == null || this.userManualDescription != null
						&& this.userManualDescription.equals(other.getUserManualDescription()))
				&& (this.versionUri == null && other.getVersionUri() == null || this.versionUri != null
						&& this.versionUri.equals(other.getVersionUri()));
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
		if (getBusinessDescription() != null) {
			_hashCode += getBusinessDescription().hashCode();
		}
		if (getGeneralDescription() != null) {
			_hashCode += getGeneralDescription().hashCode();
		}
		if (getName() != null) {
			_hashCode += getName().hashCode();
		}
		if (getRgbColor() != null) {
			_hashCode += getRgbColor().hashCode();
		}
		if (getTechnicalDescription() != null) {
			_hashCode += getTechnicalDescription().hashCode();
		}
		if (getUri() != null) {
			_hashCode += getUri().hashCode();
		}
		if (getUserManualDescription() != null) {
			_hashCode += getUserManualDescription().hashCode();
		}
		if (getVersionUri() != null) {
			_hashCode += getVersionUri().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(PPMRole.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMRole"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("businessDescription");
		elemField.setXmlName(new javax.xml.namespace.QName("", "businessDescription"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("generalDescription");
		elemField.setXmlName(new javax.xml.namespace.QName("", "generalDescription"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("rgbColor");
		elemField.setXmlName(new javax.xml.namespace.QName("", "rgbColor"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("technicalDescription");
		elemField.setXmlName(new javax.xml.namespace.QName("", "technicalDescription"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("uri");
		elemField.setXmlName(new javax.xml.namespace.QName("", "uri"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("userManualDescription");
		elemField.setXmlName(new javax.xml.namespace.QName("", "userManualDescription"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("versionUri");
		elemField.setXmlName(new javax.xml.namespace.QName("", "versionUri"));
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

}
