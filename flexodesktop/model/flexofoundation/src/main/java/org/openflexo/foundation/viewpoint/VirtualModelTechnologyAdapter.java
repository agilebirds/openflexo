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
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;

/**
 * This class defines and implements the Openflexo built-in virtual model technology adapter
 * 
 * @author sylvain
 * 
 */
public class VirtualModelTechnologyAdapter extends TechnologyAdapter {

	private static final Logger logger = Logger.getLogger(VirtualModelTechnologyAdapter.class.getPackage().getName());

	public VirtualModelTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Openflexo virtual model";
	}

	@Override
	public VirtualModelModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new VirtualModelModelSlot(viewPoint, this);
	}

	@Override
	public VirtualModelModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new VirtualModelModelSlot(virtualModel, this);
	}

	/**
	 * Not applicable here
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager technologyContextManager) {
		return false;
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager technologyContextManager) {
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
	public boolean isValidModelFile(File aModelFile, FlexoMetaModelResource<?, ?> metaModelResource,
			TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isValidModelFile(File aModelFile, TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FlexoModelResource<?, ?> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoMetaModelResource<?, ?> metaModelResource, TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoModelResource<?, ?> createEmptyModel(FlexoProject project, String filename, String modelUri,
			FlexoMetaModelResource<?, ?> metaModelResource, TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// no specific binding factory for this technology
		return null;
	}

	@Override
	public FlexoMetaModelResource<?, ?> retrieveMetaModelResource(File aMetaModelFile, TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String retrieveModelURI(File aModelFile, FlexoMetaModelResource<?, ?> metaModelResource,
			TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoModelResource<?, ?> retrieveModelResource(File aModelFile, TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlexoModelResource<?, ?> retrieveModelResource(File aModelFile, FlexoMetaModelResource<?, ?> metaModelResource,
			TechnologyContextManager technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoModelResource<?, ?>> ModelRepository<R, ?, ?, ? extends TechnologyAdapter> createModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoMetaModelResource<?, ?>> MetaModelRepository<R, ?, ?, ? extends TechnologyAdapter> createMetaModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedMetaModelExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedModelExtension(FlexoMetaModelResource<?, ?> metaModelResource) {
		// TODO Auto-generated method stub
		return null;
	}

}
