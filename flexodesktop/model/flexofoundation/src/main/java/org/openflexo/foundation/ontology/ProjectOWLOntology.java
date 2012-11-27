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
package org.openflexo.foundation.ontology;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.owl.OWLOntology;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectOntologyResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.localization.FlexoLocalization;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ProjectOWLOntology extends OWLOntology implements ProjectOntology {

	private static final Logger logger = Logger.getLogger(FlexoOntology.class.getPackage().getName());

	private FlexoProjectOntologyResource _resource;

	/**
	 * Creates and returns a newly created data model with resource management (creates the resource)
	 * 
	 * @return a newly created DMModel
	 */
	public static ProjectOWLOntology createNewProjectOntology(FlexoProject project) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("createNewProjectOntology(), project=" + project);
		}
		logger.info("-------------> Create ontology for " + project.getProjectName());
		File owlFile = ProjectRestructuration.getExpectedProjectOntologyFile(project, project.getProjectName());
		FlexoProjectFile ontologyFile = new FlexoProjectFile(owlFile, project);

		ProjectOWLOntology newProjectOntology = createProjectOntology(project.getURI(), owlFile, project.getProjectOntologyLibrary());
		project.getProjectOntologyLibrary().registerOntology(newProjectOntology);

		FlexoProjectOntologyResource ontologyRes;
		try {
			ontologyRes = new FlexoProjectOntologyResource(project, newProjectOntology, ontologyFile);
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
		// newDMModel.initializeDefaultRepositories(dmRes);
		try {
			// dmRes.saveResourceData();
			project.registerResource(ontologyRes);
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		try {
			ontologyRes.saveResourceData();
		} catch (Exception e1) {
			// Warns about the exception
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Exception raised: " + e1.getClass().getName() + ". See console for details.");
			}
			e1.printStackTrace();
		}

		return newProjectOntology;
	}

	private static ProjectOWLOntology createProjectOntology(String anURI, File owlFile, ProjectOntologyLibrary ontologyLibrary) {
		ProjectOWLOntology returned = new ProjectOWLOntology(anURI, owlFile, ontologyLibrary);

		Model base = ModelFactory.createDefaultModel();
		returned.ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ontologyLibrary, base);
		returned.ontModel.createOntology(anURI);
		returned.ontModel.setDynamicImports(true);
		returned.isLoaded = true;
		return returned;
	}

	public ProjectOWLOntology(String anURI, File owlFile, ProjectOntologyLibrary ontologyLibrary) {
		super(anURI, owlFile, ontologyLibrary);
	}

	@Override
	public boolean getIsReadOnly() {
		return false;
	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey("project_ontology");
	}

	@Override
	public FlexoProjectOntologyResource getFlexoResource() {
		return _resource;
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException {
		_resource = (FlexoProjectOntologyResource) resource;
	}

	@Override
	public void save() throws SaveResourceException {
		getFlexoResource().saveResourceData();
	}

	@Override
	public String getClassNameKey() {
		return "project_ontology";
	}

	@Override
	public String getDescription() {
		return FlexoLocalization.localizedForKey("project_ontology");
	}

	@Override
	public String getInspectorName() {
		return Inspectors.VE.PROJECT_ONTOLOGY_INSPECTOR;
	}

	@Override
	public String getDisplayableDescription() {
		return "Ontology of project " + getProject().getName();
	}

	private void pourNePasOublier() {
		String FLEXO_CONCEPTS_URI = "http://www.agilebirds.com/openflexo/ontologies/FlexoConceptsOntology.owl";
		String FLEXO_MODEL_OBJECT = FLEXO_CONCEPTS_URI + "#FlexoModelObject";
		String LINKED_TO_MODEL_PROPERTY = FLEXO_CONCEPTS_URI + "#linkedToModel";
		String CLASS_NAME_PROPERTY = FLEXO_CONCEPTS_URI + "#className";
		String FLEXO_ID_PROPERTY = FLEXO_CONCEPTS_URI + "#flexoID";
		String RESOURCE_NAME_PROPERTY = FLEXO_CONCEPTS_URI + "#resourceName";

		String BOT_URI = "http://www.agilebirds.com/openflexo/ontologies/OrganizationTree/BasicOrganizationTree.owl";
		String COMPANY_NAME = BOT_URI + "#companyName";

		String BOT_EDITOR_URI = "http://www.agilebirds.com/openflexo/ViewPoints/Tests/BasicOrganizationTreeEditor.owl";
		String BOT_COMPANY = BOT_EDITOR_URI + "#BOTCompany";

		OntModel ontModel = getProject().getProjectOntology().getOntModel();

		OntClass fooClass = ontModel.createClass(getProject().getProjectOntology().getOntologyURI() + "#" + "foo");
		OntClass foo2Class = ontModel.createClass(getProject().getProjectOntology().getOntologyURI() + "#" + "foo2");
		foo2Class.addComment("Test de commentaire", "FR");
		foo2Class.addComment("Comment test", "EN");
		foo2Class.addSuperClass(fooClass);

		FlexoProcess process = getProject().getWorkflow().getRootFlexoProcess();
		OntClass flexoModelObject = ontModel.getOntClass(FLEXO_MODEL_OBJECT);
		ObjectProperty linkedToModelProperty = ontModel.getObjectProperty(LINKED_TO_MODEL_PROPERTY);
		DatatypeProperty classNameProperty = ontModel.getDatatypeProperty(CLASS_NAME_PROPERTY);
		DatatypeProperty flexoIDProperty = ontModel.getDatatypeProperty(FLEXO_ID_PROPERTY);
		DatatypeProperty resourceNameProperty = ontModel.getDatatypeProperty(RESOURCE_NAME_PROPERTY);

		Individual myRootFlexoProcess = ontModel.createIndividual(getProject().getProjectOntology().getURI() + "#MyRootProcess",
				flexoModelObject);
		myRootFlexoProcess.addProperty(classNameProperty, process.getClass().getName());
		myRootFlexoProcess.addProperty(flexoIDProperty, process.getSerializationIdentifier());
		myRootFlexoProcess.addProperty(resourceNameProperty, process.getFlexoResource().getFullyQualifiedName());

		OntClass botCompany = ontModel.getOntClass(BOT_COMPANY);
		DatatypeProperty companyNameProperty = ontModel.getDatatypeProperty(COMPANY_NAME);
		Individual agileBirdsCompany = ontModel.createIndividual(getProject().getProjectOntology().getURI() + "#AgileBirds", botCompany);
		agileBirdsCompany.addProperty(companyNameProperty, "Agile Birds S.A.");

		agileBirdsCompany.addProperty(linkedToModelProperty, myRootFlexoProcess);
	}

}
