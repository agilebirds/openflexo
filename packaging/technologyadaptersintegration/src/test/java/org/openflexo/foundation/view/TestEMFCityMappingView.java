/** Copyright (c) 2013, THALES SYSTEMES AEROPORTES - All Rights Reserved
 * Author : Gilles Besançon
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
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or 
 * combining it with eclipse EMF (or a modified version of that library), 
 * containing parts covered by the terms of EPL 1.0, the licensors of this 
 * Program grant you additional permission to convey the resulting work.
 *
 * Contributors :
 *
 */
package org.openflexo.foundation.view;

import java.io.File;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.FlexoOntologyModelSlotInstanceConfiguration;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.view.action.CreateView;
import org.openflexo.foundation.view.action.CreateVirtualModelInstance;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;

/**
 * Test instanciation of City Mapping View with 2 EMF
 * 
 * @author gbesancon
 */
public class TestEMFCityMappingView extends FlexoTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;
	private static ViewPoint cityMappingVP;
	private static RepositoryFolder<ViewResource> viewFolder;
	private static View view;

	/**
	 * Instantiate test resource center
	 */
	public void test0InstantiateResourceCenter() {

		log("test0InstantiateResourceCenter()");

		// TODO: create a project where all those tests don't need a manual import of projects
		// TODO: copy all test VP in tmp dir and work with those VP instead of polling GIT workspace
		instanciateTestServiceManager();
	}

	public void test1CreateProject() {
		editor = createProject("TestCreateView");
		project = editor.getProject();

		assertNotNull(project.getViewLibrary());
	}

	private ViewPoint loadViewPoint(String viewPointURI) {

		log("Testing ViewPoint loading: " + viewPointURI);

		System.out.println("resourceCenter=" + resourceCenter);
		System.out.println("resourceCenter.getViewPointRepository()=" + resourceCenter.getViewPointRepository());

		ViewPointResource vpRes = resourceCenter.getViewPointRepository().getResource(viewPointURI);

		assertNotNull(vpRes);
		assertFalse(vpRes.isLoaded());

		ViewPoint vp = vpRes.getViewPoint();
		assertTrue(vpRes.isLoaded());

		return vp;

	}

	public void test2LoadCityMappingViewPoint() {
		cityMappingVP = loadViewPoint("http://www.openflexo.org/ViewPoints/Tests/CityMapping");
		assertNotNull(cityMappingVP);
		System.out.println("Found view point in " + cityMappingVP.getResource().getFile());
	}

	public void test3CreateViewFolder() {
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType.makeNewAction(project.getViewLibrary().getRootFolder(),
				null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(viewFolder.getFile().exists());
	}

	public void test4CreateView() {
		CreateView addView = CreateView.actionType.makeNewAction(viewFolder, null, editor);
		addView.newViewName = "TestNewView";
		addView.newViewTitle = "A nice title for a new view";
		addView.viewpointResource = cityMappingVP.getResource();
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + newView.getResource().getFile());
		assertNotNull(newView);
		assertEquals(addView.newViewName, newView.getName());
		assertEquals(addView.newViewTitle, newView.getTitle());
		assertEquals(addView.viewpointResource.getViewPoint(), cityMappingVP);
		assertTrue(newView.getResource().getFile().exists());
	}

	public void test5ReloadProject() {
		editor = reloadProject(project.getProjectDirectory());
		project = editor.getProject();
		assertNotNull(project.getViewLibrary());
		assertEquals(1, project.getViewLibrary().getRootFolder().getChildren().size());
		viewFolder = project.getViewLibrary().getRootFolder().getChildren().get(0);
		assertEquals(1, viewFolder.getResources().size());
		ViewResource viewRes = viewFolder.getResources().get(0);
		assertEquals(viewRes, project.getViewLibrary().getResource(viewRes.getURI()));
		assertNotNull(viewRes);
		assertFalse(viewRes.isLoaded());
		view = viewRes.getView();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project, view.getResource().getProject());
		assertEquals(project, view.getProject());
	}

	public void test6CreateVirtualModelInstance() {
		System.out.println("Create virtual model instance, view=" + view + " editor=" + editor);

		CreateVirtualModelInstance createVirtualModelInstance = CreateVirtualModelInstance.actionType.makeNewAction(view, null, editor);
		createVirtualModelInstance.setNewVirtualModelInstanceName("TestNewVirtualModel");
		createVirtualModelInstance.setNewVirtualModelInstanceTitle("A nice title for a new virtual model instance");

		VirtualModel<?> conceptualModel = cityMappingVP.getVirtualModelNamed("ConceptualModel");
		assertNotNull(conceptualModel);

		createVirtualModelInstance.setVirtualModel(conceptualModel);

		ModelSlot<?, ?> emfModelSlot = conceptualModel.getModelSlots().get(0);
		FlexoOntologyModelSlotInstanceConfiguration emfModelSlotConfiguration = (FlexoOntologyModelSlotInstanceConfiguration) createVirtualModelInstance
				.getModelSlotInstanceConfiguration(emfModelSlot);
		emfModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.SelectExistingModel);
		// File modelFile = new FileResource("src/test/resources/TestResourceCenter/EMF/Model/example1/my.example1");
		File modelFile = new File(((FileSystemBasedResourceCenter) resourceCenter).getRootDirectory(),
				"TestResourceCenter/EMF/Model/example1/my.example1");
		System.out.println("Searching " + modelFile.getAbsolutePath());
		assertTrue(modelFile.exists());
		System.out.println("Searching " + modelFile.toURI().toString());
		FlexoModelResource<?, ?> modelResource = project.getServiceManager().getInformationSpace().getModel(modelFile.toURI().toString());
		assertNotNull(modelResource);
		emfModelSlotConfiguration.setModelResource(modelResource);
		assertTrue(emfModelSlotConfiguration.isValidConfiguration());

		ModelSlot<?, ?> ontologySlot = conceptualModel.getModelSlots().get(1);
		FlexoOntologyModelSlotInstanceConfiguration ontologyModelSlotConfiguration = (FlexoOntologyModelSlotInstanceConfiguration) createVirtualModelInstance
				.getModelSlotInstanceConfiguration(ontologySlot);
		ontologyModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		ontologyModelSlotConfiguration.setModelUri("http://MyCityOntology");
		ontologyModelSlotConfiguration.setRelativePath("");
		ontologyModelSlotConfiguration.setFilename("MyCityOntology.owl");
		assertTrue(ontologyModelSlotConfiguration.isValidConfiguration());

		createVirtualModelInstance.doAction();
		System.out.println("exception thrown=" + createVirtualModelInstance.getThrownException());
		// createDiagram.getThrownException().printStackTrace();
		assertTrue(createVirtualModelInstance.hasActionExecutionSucceeded());
		VirtualModelInstance newVirtualModelInstance = createVirtualModelInstance.getNewVirtualModelInstance();
		System.out.println("New VirtualModelInstance " + newVirtualModelInstance + " created in "
				+ newVirtualModelInstance.getResource().getFile());
		assertNotNull(newVirtualModelInstance);
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceName(), newVirtualModelInstance.getName());
		assertEquals(createVirtualModelInstance.getNewVirtualModelInstanceTitle(), newVirtualModelInstance.getTitle());
		assertEquals(createVirtualModelInstance.getVirtualModel(), cityMappingVP.getVirtualModelNamed("ConceptualModel"));
		assertTrue(newVirtualModelInstance.getResource().getFile().exists());
		assertEquals(project, newVirtualModelInstance.getResource().getProject());
		assertEquals(project, newVirtualModelInstance.getProject());
	}
}
