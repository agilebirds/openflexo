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
package org.openflexo.technologyadapter.emf;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFModelRepository;
import org.openflexo.technologyadapter.emf.model.EMFTechnologyContextManager;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFClass;
import org.openflexo.technologyadapter.emf.viewpoint.editionaction.AddEMFInstance;

/**
 * This class defines and implements the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclarePatternRoles({
/** Instances */
@DeclarePatternRole(IndividualPatternRole.class),
/** Classes */
@DeclarePatternRole(ClassPatternRole.class),
/** Data properties */
@DeclarePatternRole(DataPropertyPatternRole.class),
/** Object properties */
@DeclarePatternRole(ObjectPropertyPatternRole.class) })
@DeclareEditionActions({
/** Add instance */
@DeclareEditionAction(AddEMFInstance.class),
/** Add class */
@DeclareEditionAction(AddEMFClass.class),
/** Add class */
@DeclareEditionAction(DeleteAction.class) })
public class EMFTechnologyAdapter extends TechnologyAdapter<EMFModel, EMFMetaModel> {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyAdapter.class.getPackage().getName());

	public EMFTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	@Override
	public EMFModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new EMFModelSlot(viewPoint, this);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO: also check that file is valid and maps a valid XSD schema
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".ecore");
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO implement this
		return null;
	}

	/**
	 * Retrieve and return URI for supplied model file
	 * 
	 * @param aModelFile
	 * @param rc
	 *            TODO
	 * @return
	 */
	@Override
	public String retrieveModelURI(File aModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(java.io.File,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO: also check that file is valid and maps a valid XML model conform to supplied meta-model
		// TODO implement this
		return false;
	}

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public EMFMetaModelResource retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		logger.warning("Not implemented yet");
		EMFMetaModelResource emfModelResource = null;

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;
		emfContextManager.registerMetaModel(emfModelResource);
		return emfModelResource;
	}

	/**
	 * Instantiate new model resource stored in supplied model file
	 * 
	 * @param aMetaModelFile
	 * 
	 * @return
	 */
	@Override
	public EMFModelResource retrieveModelResource(File aModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		logger.warning("Not implemented yet");
		EMFModelResource emfModelResource = new EMFModelResource((FlexoProjectBuilder) null);

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;
		emfContextManager.registerModel(emfModelResource);
		return emfModelResource;
	}

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	@Override
	public EMFModel createEmptyModel(FlexoProject project, EMFMetaModel metaModel,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		// TODO implement this
		// See code in XSD/XML connector
		return null;
	}

	@Override
	public TechnologyContextManager<EMFModel, EMFMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EMFModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFModelRepository(this, resourceCenter);
	}

	@Override
	public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFMetaModelRepository(this, resourceCenter);
	}

}
