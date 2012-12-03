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

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.model.EMFModel;
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
/** Delete */
@DeclareEditionAction(DeleteAction.class) })
public class EMFTechnologyAdapter extends TechnologyAdapter<EMFModel, EMFMetaModel, EMFModelSlot> {

	/**
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public EMFTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getName()
	 */
	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot()
	 */
	@Override
	protected EMFModelSlot createNewModelSlot() {
		// TODO implement this
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidMetaModelFile(java.io.File)
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile) {
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".ecore");
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelURI(java.io.File)
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile) {
		// TODO implement this
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(java.io.File,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, EMFMetaModel metaModel) {
		// TODO: also check that file is valid and maps a valid XML model conform to supplied meta-model
		// TODO implement this
		return false;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#loadMetaModel(java.io.File,
	 *      org.openflexo.foundation.ontology.OntologyLibrary)
	 */
	@Override
	public EMFMetaModel loadMetaModel(File aMetaModelFile, OntologyLibrary library) {
		// TODO implement this
		return null;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModel(org.openflexo.foundation.rm.FlexoProject,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public EMFModel createNewModel(FlexoProject project, EMFMetaModel metaModel) {
		// TODO implement this
		// See code in XSD/XML connector
		return null;
	}

}
