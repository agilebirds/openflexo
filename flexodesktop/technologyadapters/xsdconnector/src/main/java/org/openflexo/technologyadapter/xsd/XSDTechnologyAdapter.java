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
package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.DeclareEditionAction;
import org.openflexo.foundation.technologyadapter.DeclareEditionActions;
import org.openflexo.foundation.technologyadapter.DeclarePatternRole;
import org.openflexo.foundation.technologyadapter.DeclarePatternRoles;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DeleteAction;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.rm.XMLModelResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

/**
 * This class defines and implements the XSD/XML technology adapter
 * 
 * @author sylvain, luka
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
@DeclareEditionAction(AddIndividual.class),
/** Add class */
@DeclareEditionAction(AddClass.class),
/** Add class */
@DeclareEditionAction(DeleteAction.class) })
public class XSDTechnologyAdapter extends TechnologyAdapter<XMLModel, XSDMetaModel, XSDModelSlot> {

	protected static final Logger logger = Logger.getLogger(XSDTechnologyAdapter.class.getPackage().getName());

	public XSDTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "XSD/XML technology adapter";
	}

	@Override
	protected XSDModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new XSDModelSlot(viewPoint, this);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, FlexoResourceCenter rc) {
		// TODO: also check that file is valid and maps a valid XSD schema
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".xsd");
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, FlexoResourceCenter rc) {
		return XSOntology.findOntologyURI(aMetaModelFile);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource, FlexoResourceCenter rc) {
		// TODO: also check that file is valid and maps a valid XML model conform to supplied meta-model
		return aModelFile.getName().endsWith(".xml");
	}

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public XSDMetaModelResource retrieveMetaModelResource(File aMetaModelFile, FlexoResourceCenter rc) {
		// TODO to be implemented
		return null;
	}

	/**
	 * Instantiate new model resource stored in supplied model file
	 * @param aMetaModelFile
	 * 
	 * @return
	 */
	@Override
	public XMLModelResource retrieveModelResource(File aModelFile, FlexoResourceCenter rc) {
		// TODO to be implemented
		return null;
	}

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	@Override
	public XMLModel createEmptyModel(FlexoProject project, XSDMetaModel metaModel) {

		// TODO: meta model not handled here !

		/*if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewXMLModel(), project=" + project);
		}
		logger.info("-------------> Create XMLModel for " + project.getProjectName());
		File owlFile = ProjectRestructuration.getExpectedProjectOntologyFile(project, project.getProjectName());
		FlexoProjectFile ontologyFile = new FlexoProjectFile(owlFile, project);

		XMLModel newProjectOntology = createProjectOntology(project.getURI(), owlFile, project.getProjectOntologyLibrary());
		project.getProjectOntologyLibrary().registerOntology(newProjectOntology);

		FlexoXMLModelResource ontologyRes;
		try {
			ontologyRes = new FlexoXMLModelResource(project, newProjectOntology, ontologyFile);
		} catch (InvalidFileNameException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This should not happen: invalid file " + ontologyFile);
			}
			return null;
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("This should not happen: DuplicateResourceException for " + ontologyFile);
			}
			return null;
		}
		try {
			project.registerResource(ontologyRes);
		} catch (Exception e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		try {
			ontologyRes.saveResourceData();
		} catch (Exception e1) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newProjectOntology;*/

		return null;
	}

	@Override
	public XMLModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new XMLModelRepository(this, resourceCenter);
	}

	@Override
	public XSDMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new XSDMetaModelRepository(this, resourceCenter);
	}
}
