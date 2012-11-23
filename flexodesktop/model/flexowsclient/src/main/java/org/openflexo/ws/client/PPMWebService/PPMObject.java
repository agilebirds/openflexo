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

@Deprecated
public class PPMObject {

	protected java.lang.String name;
	protected java.lang.String uri;
	protected java.lang.String versionUri;
	protected java.lang.String generalDescription;
	protected java.lang.String businessDescription;
	protected java.lang.String technicalDescription;
	protected java.lang.String userManualDescription;;

	/**
	 * Gets the businessDescription value for this PPMProcess.
	 * 
	 * @return businessDescription
	 */
	public java.lang.String getBusinessDescription() {
		return businessDescription;
	}

	/**
	 * Sets the businessDescription value for this PPMProcess.
	 * 
	 * @param businessDescription
	 */
	public void setBusinessDescription(java.lang.String businessDescription) {
		this.businessDescription = businessDescription;
	}

	/**
	 * Gets the generalDescription value for this PPMProcess.
	 * 
	 * @return generalDescription
	 */
	public java.lang.String getGeneralDescription() {
		return generalDescription;
	}

	/**
	 * Sets the generalDescription value for this PPMProcess.
	 * 
	 * @param generalDescription
	 */
	public void setGeneralDescription(java.lang.String generalDescription) {
		this.generalDescription = generalDescription;
	}

	/**
	 * Gets the name value for this PPMProcess.
	 * 
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name value for this PPMProcess.
	 * 
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Gets the technicalDescription value for this PPMProcess.
	 * 
	 * @return technicalDescription
	 */
	public java.lang.String getTechnicalDescription() {
		return technicalDescription;
	}

	/**
	 * Sets the technicalDescription value for this PPMProcess.
	 * 
	 * @param technicalDescription
	 */
	public void setTechnicalDescription(java.lang.String technicalDescription) {
		this.technicalDescription = technicalDescription;
	}

	/**
	 * Gets the uri value for this PPMProcess.
	 * 
	 * @return uri
	 */
	public java.lang.String getUri() {
		return uri;
	}

	/**
	 * Sets the uri value for this PPMProcess.
	 * 
	 * @param uri
	 */
	public void setUri(java.lang.String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the userManualDescription value for this PPMProcess.
	 * 
	 * @return userManualDescription
	 */
	public java.lang.String getUserManualDescription() {
		return userManualDescription;
	}

	/**
	 * Sets the userManualDescription value for this PPMProcess.
	 * 
	 * @param userManualDescription
	 */
	public void setUserManualDescription(java.lang.String userManualDescription) {
		this.userManualDescription = userManualDescription;
	}

	/**
	 * Gets the versionUri value for this PPMProcess.
	 * 
	 * @return versionUri
	 */
	public java.lang.String getVersionUri() {
		return versionUri;
	}

	/**
	 * Sets the versionUri value for this PPMProcess.
	 * 
	 * @param versionUri
	 */
	public void setVersionUri(java.lang.String versionUri) {
		this.versionUri = versionUri;
	}

}
