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
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoResourceCenter;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.LocalResourceCenterImplementation;
import org.openflexo.foundation.dkv.TestPopulateDKV;
import org.openflexo.foundation.ontology.FlexoOntology.OntologyNotFoundException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.toolbox.FileUtils;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public class TestOntologiesDeprecated extends FlexoTestCase {

	protected static final Logger logger = Logger.getLogger(TestPopulateDKV.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;

	private static File _resourceCenterDirectory;
	private static FlexoResourceCenter _resourceCenter;

	public TestOntologiesDeprecated(String name) {
		super(name);
	}

	/**
	 * Instanciate new ResourceCenter
	 */
	public void test0InstanciateNewResourceCenter() {
		log("test0InstanciateNewResourceCenter()");

		try {
			File tempFile = File.createTempFile("LocalResourceCenter", "");
			_resourceCenterDirectory = new File(tempFile.getParentFile(), tempFile.getName());
			_resourceCenterDirectory.mkdirs();
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}

		_resourceCenter = LocalResourceCenterImplementation.instanciateNewLocalResourceCenterImplementation(_resourceCenterDirectory);

	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test1CreateProject() {
		log("test1CreateProject()");

		_editor = createProject("TestOntology", _resourceCenter);
		_project = _editor.getProject();
	}

	/**
	 * Creates a new empty project in a temp directory, create ontology, reload project
	 */
	public void test2CreateOntology() {
		log("test2CreateOntology()");

		logger.info("Hop" + _project.getProjectOntology());

		saveProject(_project);

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();

		logger.info("Hop" + _project.getProjectOntology());

		FlexoOntology flexoConceptsOntology = _project.getProjectOntologyLibrary().getFlexoConceptOntology();
		flexoConceptsOntology.describe();

	}

	/**
	 * Import ontologies
	 */
	public void test3ImportOntologies() {
		log("test3ImportOntologies()");

		FlexoOntology basicOrganizationTreeOntology = _project.getProjectOntologyLibrary().getOntology(
				"http://www.agilebirds.com/openflexo/ontologies/OrganizationTree/BasicOrganizationTree.owl");
		assertNotNull(basicOrganizationTreeOntology);

		try {
			_project.getProjectOntology().importOntology(basicOrganizationTreeOntology);
		} catch (OntologyNotFoundException e) {
			fail(e.getMessage());
		}

		saveProject(_project);
	}

	/**
	 * Load calcs, import calc
	 */
	/*public void test4loadCalcsAndImportSomeCalc() {
		log("test4loadCalcsAndImportSomeCalc()");

		logger.info("CalcLibrary: " + _resourceCenter.retrieveViewPointLibrary());
		logger.info("All calcs: " + _resourceCenter.retrieveViewPointLibrary().getViewPoints());

		ViewPoint basicOrganizationTreeEditorCalc = _resourceCenter.retrieveViewPointLibrary().getOntologyCalc(
				"http://www.agilebirds.com/openflexo/ViewPoints/Tests/BasicOrganizationTreeEditor.owl");
		logger.info("Le calc: " + basicOrganizationTreeEditorCalc);
		basicOrganizationTreeEditorCalc.loadWhenUnloaded();

		ViewPoint thesaurusEditorCalc = _resourceCenter.retrieveViewPointLibrary().getOntologyCalc(
				"http://www.agilebirds.com/openflexo/ViewPoints/ThesaurusEditor.owl");
		logger.info("Le calc: " + thesaurusEditorCalc);
		thesaurusEditorCalc.loadWhenUnloaded();

		try {
			_project.getProjectOntology().importOntology(basicOrganizationTreeEditorCalc.getViewpointOntology());
		} catch (OntologyNotFoundException e) {
			fail(e.getMessage());
		}

		saveProject(_project);

	}*/

	/**
	 * Edit ontology and reload project
	 */
	public void test4ManuallyEditOntology() {
		log("test5ManuallyEditOntology()");

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

		OntModel ontModel = _project.getProjectOntology().getOntModel();

		OntClass fooClass = ontModel.createClass(_project.getProjectOntology().getOntologyURI() + "#" + "foo");
		OntClass foo2Class = ontModel.createClass(_project.getProjectOntology().getOntologyURI() + "#" + "foo2");
		foo2Class.addComment("Test de commentaire", "FR");
		foo2Class.addComment("Comment test", "EN");
		foo2Class.addSuperClass(fooClass);

		FlexoProcess process = _project.getWorkflow().getRootFlexoProcess();
		OntClass flexoModelObject = ontModel.getOntClass(FLEXO_MODEL_OBJECT);
		ObjectProperty linkedToModelProperty = ontModel.getObjectProperty(LINKED_TO_MODEL_PROPERTY);
		DatatypeProperty classNameProperty = ontModel.getDatatypeProperty(CLASS_NAME_PROPERTY);
		DatatypeProperty flexoIDProperty = ontModel.getDatatypeProperty(FLEXO_ID_PROPERTY);
		DatatypeProperty resourceNameProperty = ontModel.getDatatypeProperty(RESOURCE_NAME_PROPERTY);

		Individual myRootFlexoProcess = ontModel.createIndividual(_project.getProjectOntology().getURI() + "#MyRootProcess",
				flexoModelObject);
		myRootFlexoProcess.addProperty(classNameProperty, process.getClass().getName());
		myRootFlexoProcess.addProperty(flexoIDProperty, process.getSerializationIdentifier());
		myRootFlexoProcess.addProperty(resourceNameProperty, process.getFlexoResource().getFullyQualifiedName());

		OntClass botCompany = ontModel.getOntClass(BOT_COMPANY);
		DatatypeProperty companyNameProperty = ontModel.getDatatypeProperty(COMPANY_NAME);
		Individual agileBirdsCompany = ontModel.createIndividual(_project.getProjectOntology().getURI() + "#AgileBirds", botCompany);
		agileBirdsCompany.addProperty(companyNameProperty, "Agile Birds S.A.");

		agileBirdsCompany.addProperty(linkedToModelProperty, myRootFlexoProcess);

		_project.getProjectOntology().setChanged();
		saveProject(_project);

		_project.getProjectOntology().describe();

		logger.info("Reload project");

		_editor = reloadProject(_project.getProjectDirectory());
		if (_project != null) {
			_project.close();
		}
		_project = _editor.getProject();

		_project.getProjectOntology().describe();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_project = null;
		_editor = null;
		_resourceCenter = null;
		_resourceCenterDirectory = null;
	}

}
