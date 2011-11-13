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

public class PPMProcess extends PPMObject implements java.io.Serializable {
	private byte[] screenshoot;

	private org.openflexo.ws.client.PPMWebService.PPMProcess parentProcess;

	private org.openflexo.ws.client.PPMWebService.PPMProcess[] subProcesses;

	public PPMProcess() {
	}

	public PPMProcess(java.lang.String businessDescription, java.lang.String generalDescription, java.lang.String name,
			org.openflexo.ws.client.PPMWebService.PPMProcess parentProcess, byte[] screenshoot,
			org.openflexo.ws.client.PPMWebService.PPMProcess[] subProcesses, java.lang.String technicalDescription, java.lang.String uri,
			java.lang.String userManualDescription, java.lang.String versionUri) {
		this.businessDescription = businessDescription;
		this.generalDescription = generalDescription;
		this.name = name;
		this.parentProcess = parentProcess;
		this.screenshoot = screenshoot;
		this.subProcesses = subProcesses;
		this.technicalDescription = technicalDescription;
		this.uri = uri;
		this.userManualDescription = userManualDescription;
		this.versionUri = versionUri;
	}

	/**
	 * Gets the parentProcess value for this PPMProcess.
	 * 
	 * @return parentProcess
	 */
	public org.openflexo.ws.client.PPMWebService.PPMProcess getParentProcess() {
		return parentProcess;
	}

	/**
	 * Sets the parentProcess value for this PPMProcess.
	 * 
	 * @param parentProcess
	 */
	public void setParentProcess(org.openflexo.ws.client.PPMWebService.PPMProcess parentProcess) {
		this.parentProcess = parentProcess;
	}

	/**
	 * Gets the screenshoot value for this PPMProcess.
	 * 
	 * @return screenshoot
	 */
	public byte[] getScreenshoot() {
		return screenshoot;
	}

	/**
	 * Sets the screenshoot value for this PPMProcess.
	 * 
	 * @param screenshoot
	 */
	public void setScreenshoot(byte[] screenshoot) {
		this.screenshoot = screenshoot;
	}

	/**
	 * Gets the subProcesses value for this PPMProcess.
	 * 
	 * @return subProcesses
	 */
	public org.openflexo.ws.client.PPMWebService.PPMProcess[] getSubProcesses() {
		return subProcesses;
	}

	/**
	 * Sets the subProcesses value for this PPMProcess.
	 * 
	 * @param subProcesses
	 */
	public void setSubProcesses(org.openflexo.ws.client.PPMWebService.PPMProcess[] subProcesses) {
		this.subProcesses = subProcesses;
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof PPMProcess))
			return false;
		PPMProcess other = (PPMProcess) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.businessDescription == null && other.getBusinessDescription() == null) || (this.businessDescription != null && this.businessDescription
						.equals(other.getBusinessDescription())))
				&& ((this.generalDescription == null && other.getGeneralDescription() == null) || (this.generalDescription != null && this.generalDescription
						.equals(other.getGeneralDescription())))
				&& ((this.name == null && other.getName() == null) || (this.name != null && this.name.equals(other.getName())))
				&& ((this.parentProcess == null && other.getParentProcess() == null) || (this.parentProcess != null && this.parentProcess
						.equals(other.getParentProcess())))
				&& ((this.screenshoot == null && other.getScreenshoot() == null) || (this.screenshoot != null && java.util.Arrays.equals(
						this.screenshoot, other.getScreenshoot())))
				&& ((this.subProcesses == null && other.getSubProcesses() == null) || (this.subProcesses != null && java.util.Arrays
						.equals(this.subProcesses, other.getSubProcesses())))
				&& ((this.technicalDescription == null && other.getTechnicalDescription() == null) || (this.technicalDescription != null && this.technicalDescription
						.equals(other.getTechnicalDescription())))
				&& ((this.uri == null && other.getUri() == null) || (this.uri != null && this.uri.equals(other.getUri())))
				&& ((this.userManualDescription == null && other.getUserManualDescription() == null) || (this.userManualDescription != null && this.userManualDescription
						.equals(other.getUserManualDescription())))
				&& ((this.versionUri == null && other.getVersionUri() == null) || (this.versionUri != null && this.versionUri.equals(other
						.getVersionUri())));
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
		if (getParentProcess() != null) {
			_hashCode += getParentProcess().hashCode();
		}
		if (getScreenshoot() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getScreenshoot()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getScreenshoot(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		if (getSubProcesses() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getSubProcesses()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getSubProcesses(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
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
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(PPMProcess.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMProcess"));
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
		elemField.setFieldName("parentProcess");
		elemField.setXmlName(new javax.xml.namespace.QName("", "parentProcess"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMProcess"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("screenshoot");
		elemField.setXmlName(new javax.xml.namespace.QName("", "screenshoot"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("subProcesses");
		elemField.setXmlName(new javax.xml.namespace.QName("", "subProcesses"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.flexoserver.com/ppm", "PPMProcess"));
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
