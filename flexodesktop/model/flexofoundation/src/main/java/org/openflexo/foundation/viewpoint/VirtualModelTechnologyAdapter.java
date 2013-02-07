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
package org.openflexo.foundation.viewpoint;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.view.VirtualModelInstance;

/**
 * This class defines and implements the Openflexo built-in virtual model technology adapter
 * 
 * @author sylvain
 * 
 */
public class VirtualModelTechnologyAdapter<VMI extends VirtualModelInstance<VMI, VM>, VM extends VirtualModel<VM>> extends
		TechnologyAdapter<VMI, VM> {

	private static final Logger logger = Logger.getLogger(VirtualModelTechnologyAdapter.class.getPackage().getName());

	public VirtualModelTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Openflexo built-in virtual model technology adapter";
	}

	@Override
	public VirtualModelModelSlot<VMI, VM> createNewModelSlot(ViewPoint viewPoint) {
		return new VirtualModelModelSlot<VMI, VM>(viewPoint, this);
	}

	@Override
	public VirtualModelModelSlot<VMI, VM> createNewModelSlot(VirtualModel<?> virtualModel) {
		return new VirtualModelModelSlot<VMI, VM>(virtualModel, this);
	}

	/**
	 * Not applicable here
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<VMI, VM> technologyContextManager) {
		return false;
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<VMI, VM> technologyContextManager) {
		return null;
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<VM> metaModelResource,
			TechnologyContextManager<VMI, VM> technologyContextManager) {
		return false;
	}

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	@Override
	public FlexoResource<VMI> createEmptyModel(FlexoProject project, String filename, String modelUri, FlexoResource<VM> metaModel,
			TechnologyContextManager<VMI, VM> technologyContextManager) {
		logger.info("Add view");
		logger.warning("Not implemented yet");
		return null;
		/*if (StringUtils.isNotEmpty(newViewTitle) && StringUtils.isEmpty(newViewName)) {
			newViewName = JavaUtils.getClassName(newViewTitle);
		}

		if (StringUtils.isNotEmpty(newViewName) && StringUtils.isEmpty(newViewTitle)) {
			newViewTitle = newViewName;
		}

		if (getFolder() == null) {
			throw new InvalidParameterException("folder is undefined");
		}
		if (StringUtils.isEmpty(newViewName)) {
			throw new InvalidParameterException("view name is undefined");
		}

		int index = 1;
		String baseName = newViewName;
		while (getProject().getShemaLibrary().getShemaNamed(newViewName) != null) {
			newViewName = baseName + index;
			index++;
		}

		_newShema = new ViewDefinition(newViewName, getFolder().getShemaLibrary(), getFolder(), getProject(), true);
		_newShema.setTitle(newViewTitle);
		if (useViewPoint) {
			_newShema.setViewPoint(viewpoint);
		}
		logger.info("Added view " + _newShema + " for project " + _newShema.getProject());
		// Creates the resource here
		_newShema.getShemaResource();*/
	}

	@Override
	public FlexoResource<VMI> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<VM> metaModelResource, TechnologyContextManager<VMI, VM> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// no specific binding factory for this technology
		return null;
	}

	@Override
	public FlexoResource<VM> retrieveMetaModelResource(File aMetaModelFile, TechnologyContextManager<VMI, VM> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String retrieveModelURI(File aModelFile, FlexoResource<VM> metaModelResource,
			TechnologyContextManager<VMI, VM> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoResource<VMI> retrieveModelResource(File aModelFile, FlexoResource<VM> metaModelResource,
			TechnologyContextManager<VMI, VM> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends VM>> MetaModelRepository<R, VMI, VM, ? extends TechnologyAdapter<VMI, VM>> createMetaModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends VMI>> ModelRepository<R, VMI, VM, ? extends TechnologyAdapter<VMI, VM>> createModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyContextManager<VMI, VM> createTechnologyContextManager(FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}
}
