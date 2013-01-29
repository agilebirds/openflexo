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
package org.openflexo.foundation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.VirtualModel.VirtualModelBuilder;
import org.openflexo.foundation.xml.FlexoXMLSerializable;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * 
 * Abstract implementation of a FlexoObject which is serializable as an XMLFile
 * 
 * @author sylvain
 * 
 */
public abstract class XMLSerializableFlexoObject extends FlexoObject implements FlexoXMLSerializable {

	private static final Logger logger = Logger.getLogger(XMLSerializableFlexoObject.class.getPackage().getName());

	private boolean isSerializing = false;
	private boolean isDeserializing = false;

	/**
	 * Property that indicates wheter the current object is currently copied to create a new clone
	 */
	private boolean isBeingCloned = false;

	@Override
	public abstract XMLMapping getXMLMapping();

	@Override
	public final VirtualModel.VirtualModelBuilder instanciateNewBuilder() {
		return (VirtualModel.VirtualModelBuilder) getXMLResourceData().getFlexoXMLFileResource().instanciateNewBuilder();
	}

	@Override
	public FlexoXMLSerializable cloneUsingXMLMapping() {
		logger.info("Not implemented yet");
		return null;
	}

	@Override
	public void initializeSerialization() {
		isSerializing = true;
	}

	@Override
	public void finalizeSerialization() {
		isSerializing = false;
	}

	@Override
	public void initializeDeserialization(Object builder) {
		isDeserializing = true;
	}

	@Override
	public void finalizeDeserialization(Object builder) {
		isDeserializing = false;
	}

	@Override
	public boolean isSerializing() {
		return isSerializing;
	}

	@Override
	public boolean isDeserializing() {
		return isDeserializing;
	}

	@Override
	public void initializeCloning() {
		isBeingCloned = true;
	}

	@Override
	public void finalizeCloning() {
		isBeingCloned = false;
	}

	/**
	 * Property that indicates wheter the current object is currently copied to create a new clone
	 */
	@Override
	public boolean isBeingCloned() {
		if (getXMLResourceData() == this) {
			return isBeingCloned;
		} else {
			if (getXMLResourceData() != null) {
				return getXMLResourceData().isBeingCloned();
			} else {
				return isBeingCloned;
			}
		}
	}

	@Override
	public abstract XMLStorageResourceData<?> getXMLResourceData();

	public String getXMLRepresentation() {
		try {
			initializeSerialization();
			String returned = XMLCoder.encodeObjectWithMapping(this, getXMLMapping(), getStringEncoder(), null);
			finalizeSerialization();
			return returned;
		} catch (Exception e) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
			}
			e.printStackTrace();
			return null;
		}
	}

}
