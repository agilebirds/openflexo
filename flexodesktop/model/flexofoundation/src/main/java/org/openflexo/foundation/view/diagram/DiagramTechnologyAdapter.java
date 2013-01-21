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
package org.openflexo.foundation.view.diagram;

import java.io.File;
import java.util.logging.Logger;

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
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.viewpoint.ViewPoint;

/**
 * This class defines and implements the Openflexo built-in diagram technology adapter
 * 
 * @author sylvain
 * 
 */
public class DiagramTechnologyAdapter extends TechnologyAdapter<View, DiagramMetaModel> {

	private static final Logger logger = Logger.getLogger(DiagramTechnologyAdapter.class.getPackage().getName());

	public DiagramTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "Openflexo built-in diagram technology adapter";
	}

	@Override
	public DiagramModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new DiagramModelSlot(viewPoint, this);
	}

	/**
	 * Not applicable here
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		return false;
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		return DiagramMetaModel.INSTANCE.getURI();
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<DiagramMetaModel> metaModelResource,
			TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
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
	public FlexoResource<View> createEmptyModel(FlexoProject project, FlexoResource<DiagramMetaModel> metaModel,
			TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		logger.info("Add shema");
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
			throw new InvalidParameterException("shema name is undefined");
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
	public FlexoResource<DiagramMetaModel> retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public String retrieveModelURI(File aModelFile, FlexoResource<DiagramMetaModel> metaModelResource,
			TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public FlexoResource<View> retrieveModelResource(File aModelFile, FlexoResource<DiagramMetaModel> metaModelResource,
			TechnologyContextManager<View, DiagramMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends View>> ModelRepository<R, View, DiagramMetaModel, ? extends TechnologyAdapter<View, DiagramMetaModel>> createModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <R extends FlexoResource<? extends DiagramMetaModel>> MetaModelRepository<R, View, DiagramMetaModel, ? extends TechnologyAdapter<View, DiagramMetaModel>> createMetaModelRepository(
			FlexoResourceCenter resourceCenter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyContextManager<View, DiagramMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// no specific binding factory for this technology
		return null;
	}

}
