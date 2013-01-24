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

import java.util.logging.Logger;

import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.xml.FlexoWSLibraryBuilder;

public class FlexoWSLibraryResource extends FlexoXMLStorageResource<FlexoWSLibrary> {
	protected static final Logger logger = Logger.getLogger(FlexoWSLibraryResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public FlexoWSLibraryResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoWSLibraryResource(FlexoProject project) {
		super(project, project.getServiceManager());
	}

	/**
	 * @param aProject
	 * @throws InvalidFileNameException
	 */
	public FlexoWSLibraryResource(FlexoProject aProject, FlexoWSLibrary wsLib, FlexoProjectFile wsFile) throws InvalidFileNameException {
		this(aProject);
		wsLib.setFlexoResource(this);
		_resourceData = wsLib;
		setResourceFile(wsFile);
	}

	/**
	 * Overrides getResourceDataClass
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#getResourceDataClass()
	 */
	@Override
	public Class getResourceDataClass() {
		return FlexoWSLibrary.class;
	}

	/**
	 * Overrides instanciateNewBuilder
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#instanciateNewBuilder()
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoWSLibraryBuilder builder = new FlexoWSLibraryBuilder(this);
		builder.wsLibrary = _resourceData;
		return builder;
	}

	/**
	 * Overrides hasBuilder
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#hasBuilder()
	 */
	@Override
	public boolean hasBuilder() {
		return true;
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.WS_LIBRARY;
	}

	public FlexoWSLibrary getWSLibrary() {
		return getResourceData();
	}

	/**
	 * Overrides getName
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#getName()
	 */
	@Override
	public String getName() {
		return getProject().getProjectName();
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
	}

	@Override
	protected boolean isDuplicateSerializationIdentifierRepairable() {
		return false;
	}

	@Override
	protected boolean repairDuplicateSerializationIdentifier() {
		return false;
	}

}
