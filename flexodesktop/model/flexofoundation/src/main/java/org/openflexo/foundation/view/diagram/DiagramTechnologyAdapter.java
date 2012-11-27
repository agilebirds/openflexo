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

import org.openflexo.foundation.ontology.OntologyLibrary;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.view.diagram.model.View;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddConnector;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.AddShape;
import org.openflexo.foundation.view.diagram.viewpoint.editionaction.GraphicalAction;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;

/**
 * This class defines and implements the Openflexo built-in diagram technology adapter
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
/** Add diagram */
@DeclareEditionAction(AddDiagram.class),
/** Add instance */
@DeclareEditionAction(AddShape.class),
/** Add class */
@DeclareEditionAction(AddConnector.class),
/** Add class */
@DeclareEditionAction(GraphicalAction.class),
/** Add class */
@DeclareEditionAction(DeleteAction.class) })
public class DiagramTechnologyAdapter extends TechnologyAdapter<View, DiagramMetaModel, DiagramModelSlot> {

	private static final Logger logger = Logger.getLogger(DiagramTechnologyAdapter.class.getPackage().getName());

	public DiagramTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	@Override
	protected DiagramModelSlot createNewModelSlot() {
		// TODO implement this
		return null;
	}

	/**
	 * Not applicable here
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile) {
		return false;
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile) {
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
	public boolean isValidModelFile(File aModelFile, DiagramMetaModel metaModel) {
		// TODO: also check that file is valid and maps a valid diagram
		return true;
	}

	/**
	 * Instantiate new meta model stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public DiagramMetaModel loadMetaModel(File aMetaModelFile, OntologyLibrary library) {
		return DiagramMetaModel.INSTANCE;
	}

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	@Override
	public View createNewModel(FlexoProject project, DiagramMetaModel metaModel) {
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

}
