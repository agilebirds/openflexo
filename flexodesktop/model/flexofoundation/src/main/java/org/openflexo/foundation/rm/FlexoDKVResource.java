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

import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;

/**
 * @author gpolet
 * 
 */
public class FlexoDKVResource extends FlexoXMLStorageResource<DKVModel> {

	/**
	 * @param builder
	 */
	public FlexoDKVResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	public FlexoDKVResource(FlexoProject project) {
		super(project, project.getServiceManager());
	}

	/**
	 * @param aProject
	 * @throws InvalidFileNameException
	 */
	public FlexoDKVResource(FlexoProject aProject, DKVModel dkvModel, FlexoProjectFile dlFile) throws InvalidFileNameException {
		this(aProject);
		dkvModel.setFlexoResource(this);
		_resourceData = dkvModel;
		setResourceFile(dlFile);
	}

	/**
	 * Overrides getResourceDataClass
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#getResourceDataClass()
	 */
	@Override
	public Class getResourceDataClass() {
		return DKVModel.class;
	}

	/**
	 * Overrides instanciateNewBuilder
	 * 
	 * @see org.openflexo.foundation.rm.FlexoXMLStorageResource#instanciateNewBuilder()
	 */
	@Override
	public Object instanciateNewBuilder() {
		FlexoDKVModelBuilder builder = new FlexoDKVModelBuilder(this);
		builder.dkvModel = _resourceData;
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
		return ResourceType.DKV_MODEL;
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
