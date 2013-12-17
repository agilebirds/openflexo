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
package org.openflexo.foundation.rm;

import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.resource.FlexoXMLFileResource;
import org.openflexo.foundation.resource.ResourceData;
import org.openflexo.xmlcode.XMLMapping;
import org.openflexo.xmlcode.XMLSerializable;

/**
 * This interface is implemented by all classes which represents data related to a given XML resource
 * 
 * @author sguerin
 * 
 */
public interface XMLStorageResourceData<RD extends XMLStorageResourceData<RD>> extends StorageResourceData<RD>, ResourceData<RD>,
		XMLSerializable {

	// TODO: replace with getResource()
	public FlexoXMLFileResource<RD> getFlexoXMLFileResource();

	public boolean isSerializing();

	public boolean isDeserializing();

	@Override
	public FlexoProject getProject();

	public XMLMapping getXMLMapping();

	public void initializeCloning();

	public void finalizeCloning();

	public boolean isBeingCloned();

	// public void saveToFile(File xmlFile);
}
