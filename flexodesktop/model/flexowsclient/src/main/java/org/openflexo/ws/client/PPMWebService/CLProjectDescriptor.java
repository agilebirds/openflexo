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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CLProjectDescriptor implements java.io.Serializable {
	private java.lang.String currentEditSessionOwner;

	private java.lang.String currentLastVersion;

	private java.util.Calendar currentLastVersionCreationDate;

	private java.lang.String lastUploader;

	private int projectID;

	private java.lang.String projectName;

	private boolean uploadAllowed;

	public CLProjectDescriptor() {
	}

	public CLProjectDescriptor(java.lang.String currentEditSessionOwner, java.lang.String currentLastVersion,
			java.util.Calendar currentLastVersionCreationDate, java.lang.String lastUploader, int projectID, java.lang.String projectName,
			boolean uploadAllowed) {
		this.currentEditSessionOwner = currentEditSessionOwner;
		this.currentLastVersion = currentLastVersion;
		this.currentLastVersionCreationDate = currentLastVersionCreationDate;
		this.lastUploader = lastUploader;
		this.projectID = projectID;
		this.projectName = projectName;
		this.uploadAllowed = uploadAllowed;
	}

	/**
	 * Gets the currentEditSessionOwner value for this CLProjectDescriptor.
	 * 
	 * @return currentEditSessionOwner
	 */
	public java.lang.String getCurrentEditSessionOwner() {
		return currentEditSessionOwner;
	}

	/**
	 * Sets the currentEditSessionOwner value for this CLProjectDescriptor.
	 * 
	 * @param currentEditSessionOwner
	 */
	public void setCurrentEditSessionOwner(java.lang.String currentEditSessionOwner) {
		this.currentEditSessionOwner = currentEditSessionOwner;
	}

	/**
	 * Gets the currentLastVersion value for this CLProjectDescriptor.
	 * 
	 * @return currentLastVersion
	 */
	public java.lang.String getCurrentLastVersion() {
		return currentLastVersion;
	}

	/**
	 * Sets the currentLastVersion value for this CLProjectDescriptor.
	 * 
	 * @param currentLastVersion
	 */
	public void setCurrentLastVersion(java.lang.String currentLastVersion) {
		this.currentLastVersion = currentLastVersion;
	}

	/**
	 * Gets the currentLastVersionCreationDate value for this CLProjectDescriptor.
	 * 
	 * @return currentLastVersionCreationDate
	 */
	public java.util.Calendar getCurrentLastVersionCreationDate() {
		return currentLastVersionCreationDate;
	}

	/**
	 * Sets the currentLastVersionCreationDate value for this CLProjectDescriptor.
	 * 
	 * @param currentLastVersionCreationDate
	 */
	public void setCurrentLastVersionCreationDate(java.util.Calendar currentLastVersionCreationDate) {
		this.currentLastVersionCreationDate = currentLastVersionCreationDate;
	}

	/**
	 * Gets the lastUploader value for this CLProjectDescriptor.
	 * 
	 * @return lastUploader
	 */
	public java.lang.String getLastUploader() {
		return lastUploader;
	}

	/**
	 * Sets the lastUploader value for this CLProjectDescriptor.
	 * 
	 * @param lastUploader
	 */
	public void setLastUploader(java.lang.String lastUploader) {
		this.lastUploader = lastUploader;
	}

	/**
	 * Gets the projectID value for this CLProjectDescriptor.
	 * 
	 * @return projectID
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * Sets the projectID value for this CLProjectDescriptor.
	 * 
	 * @param projectID
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	/**
	 * Gets the projectName value for this CLProjectDescriptor.
	 * 
	 * @return projectName
	 */
	public java.lang.String getProjectName() {
		return projectName;
	}

	/**
	 * Sets the projectName value for this CLProjectDescriptor.
	 * 
	 * @param projectName
	 */
	public void setProjectName(java.lang.String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Gets the uploadAllowed value for this CLProjectDescriptor.
	 * 
	 * @return uploadAllowed
	 */
	public boolean isUploadAllowed() {
		return uploadAllowed;
	}

	/**
	 * Sets the uploadAllowed value for this CLProjectDescriptor.
	 * 
	 * @param uploadAllowed
	 */
	public void setUploadAllowed(boolean uploadAllowed) {
		this.uploadAllowed = uploadAllowed;
	}

	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

	@Override
	public String toString() {
		return getProjectName() + " | owner : " + (currentEditSessionOwner == null ? "-" : currentEditSessionOwner)
				+ " | current version : " + (currentLastVersion == null ? "-" : currentLastVersion) + " by : "
				+ (lastUploader == null ? "-" : lastUploader) + " on : "
				+ (currentLastVersionCreationDate == null ? "-" : dateFormatter.format((currentLastVersionCreationDate).getTime()));
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CLProjectDescriptor))
			return false;
		CLProjectDescriptor other = (CLProjectDescriptor) obj;
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
				&& ((this.currentEditSessionOwner == null && other.getCurrentEditSessionOwner() == null) || (this.currentEditSessionOwner != null && this.currentEditSessionOwner
						.equals(other.getCurrentEditSessionOwner())))
				&& ((this.currentLastVersion == null && other.getCurrentLastVersion() == null) || (this.currentLastVersion != null && this.currentLastVersion
						.equals(other.getCurrentLastVersion())))
				&& ((this.currentLastVersionCreationDate == null && other.getCurrentLastVersionCreationDate() == null) || (this.currentLastVersionCreationDate != null && this.currentLastVersionCreationDate
						.equals(other.getCurrentLastVersionCreationDate())))
				&& ((this.lastUploader == null && other.getLastUploader() == null) || (this.lastUploader != null && this.lastUploader
						.equals(other.getLastUploader())))
				&& this.projectID == other.getProjectID()
				&& ((this.projectName == null && other.getProjectName() == null) || (this.projectName != null && this.projectName
						.equals(other.getProjectName()))) && this.uploadAllowed == other.isUploadAllowed();
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
		if (getCurrentEditSessionOwner() != null) {
			_hashCode += getCurrentEditSessionOwner().hashCode();
		}
		if (getCurrentLastVersion() != null) {
			_hashCode += getCurrentLastVersion().hashCode();
		}
		if (getCurrentLastVersionCreationDate() != null) {
			_hashCode += getCurrentLastVersionCreationDate().hashCode();
		}
		if (getLastUploader() != null) {
			_hashCode += getLastUploader().hashCode();
		}
		_hashCode += getProjectID();
		if (getProjectName() != null) {
			_hashCode += getProjectName().hashCode();
		}
		_hashCode += (isUploadAllowed() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(CLProjectDescriptor.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://bean.ws.flexo.denali.be", "CLProjectDescriptor"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("currentEditSessionOwner");
		elemField.setXmlName(new javax.xml.namespace.QName("", "currentEditSessionOwner"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("currentLastVersion");
		elemField.setXmlName(new javax.xml.namespace.QName("", "currentLastVersion"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("currentLastVersionCreationDate");
		elemField.setXmlName(new javax.xml.namespace.QName("", "currentLastVersionCreationDate"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("lastUploader");
		elemField.setXmlName(new javax.xml.namespace.QName("", "lastUploader"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("projectID");
		elemField.setXmlName(new javax.xml.namespace.QName("", "projectID"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("projectName");
		elemField.setXmlName(new javax.xml.namespace.QName("", "projectName"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("uploadAllowed");
		elemField.setXmlName(new javax.xml.namespace.QName("", "uploadAllowed"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		elemField.setNillable(false);
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
