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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.OpenflexoRunTimeTestCase;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TypeAwareModelSlotInstanceConfiguration;
import org.openflexo.foundation.view.action.CreateView;
import org.openflexo.foundation.view.action.ModelSlotInstanceConfiguration.DefaultModelSlotInstanceConfigurationOption;
import org.openflexo.foundation.view.rm.ViewResource;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.rm.ViewPointResource;

/**
 * Test instanciation of City Mapping View with 2 EMF
 * 
 * @author gbesancon
 */
public class TestEMFCityMappingView extends OpenflexoRunTimeTestCase {

	/**
	 * Follow the link.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@BeforeClass
	protected static void setUpClass() throws Exception {
		instanciateTestServiceManager();
	}

	/**
	 * Test creating Diagram and model from scratch.
	 */
	@Test
	public void testEMFCityMapping() {
		// CreateProject
		FlexoEditor editor = createProject("TestCreateView");
		FlexoProject project = editor.getProject();
		assertNotNull(project.getViewLibrary());

		// Load CityMapping ViewPoint
		ViewPoint cityMappingViewPoint = loadViewPoint("http://www.thalesgroup.com/openflexo/emf/CityMapping");
		assertNotNull(cityMappingViewPoint);
		System.out.println("Found view point in " + cityMappingViewPoint.getResource().getFile());

		// Create View Folder
		AddRepositoryFolder addRepositoryFolder = AddRepositoryFolder.actionType.makeNewAction(project.getViewLibrary().getRootFolder(),
				null, editor);
		addRepositoryFolder.setNewFolderName("NewViewFolder");
		addRepositoryFolder.doAction();
		assertTrue(addRepositoryFolder.hasActionExecutionSucceeded());
		RepositoryFolder<ViewResource> viewFolder = addRepositoryFolder.getNewFolder();
		assertTrue(viewFolder.getFile().exists());

		// Create View
		CreateView addView = CreateView.actionType.makeNewAction(viewFolder, null, editor);
		addView.newViewName = "TestNewView";
		addView.newViewTitle = "A nice title for a new view";
		addView.viewpointResource = cityMappingViewPoint.getResource();
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + newView.getResource().getFile());
		assertNotNull(newView);
		assertEquals(addView.newViewName, newView.getName());
		assertEquals(addView.newViewTitle, newView.getTitle());
		assertEquals(addView.viewpointResource.getViewPoint(), cityMappingViewPoint);
		assertTrue(newView.getResource().getFile().exists());

		// Reload Project
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
		View view = viewRes.getView();
		assertTrue(viewRes.isLoaded());
		assertNotNull(view);
		assertEquals(project, view.getResource().getProject());
		assertEquals(project, view.getProject());

		// CreateDiagram
		System.out.println("Create diagram, view=" + view + " editor=" + editor);
		System.out.println("editor project = " + editor.getProject());
		System.out.println("view project = " + view.getProject());
		CreateDiagram createDiagram = CreateDiagram.actionType.makeNewAction(view, null, editor);
		createDiagram.setNewVirtualModelInstanceName("TestNewDiagram");
		createDiagram.setNewVirtualModelInstanceTitle("A nice title for a new diagram");
		createDiagram.setDiagramSpecification(cityMappingViewPoint.getDefaultDiagramSpecification());
		// Populate modelSlots
		assertEquals(3, cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlots().size());
		// Model Slot Diagram
		ModelSlot diagramModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("diagram");
		assertNotNull(diagramModelSlot);
		DiagramModelSlotInstanceConfiguration diagramModelSlotConfiguration = (DiagramModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(diagramModelSlot);
		diagramModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.Autoconfigure);
		assertTrue(diagramModelSlotConfiguration.isValidConfiguration());
		// Model Slot city1
		ModelSlot city1ModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("city1");
		assertNotNull(city1ModelSlot);
		TypeAwareModelSlotInstanceConfiguration city1ModelSlotConfiguration = (TypeAwareModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(city1ModelSlot);
		city1ModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		city1ModelSlotConfiguration.setModelUri("http://www.thalesgroup.com/openflexo/emf/CityMapping/myCity1");
		city1ModelSlotConfiguration.setRelativePath("/");
		city1ModelSlotConfiguration.setFilename("city.city1");
		assertTrue(city1ModelSlotConfiguration.isValidConfiguration());
		// Model Slot city2
		ModelSlot city2ModelSlot = cityMappingViewPoint.getDefaultDiagramSpecification().getModelSlot("city2");
		assertNotNull(city2ModelSlot);
		TypeAwareModelSlotInstanceConfiguration city2ModelSlotConfiguration = (TypeAwareModelSlotInstanceConfiguration) createDiagram
				.getModelSlotInstanceConfiguration(city2ModelSlot);
		city2ModelSlotConfiguration.setOption(DefaultModelSlotInstanceConfigurationOption.CreatePrivateNewModel);
		city2ModelSlotConfiguration.setModelUri("http://www.thalesgroup.com/openflexo/emf/CityMapping/myCity2");
		city2ModelSlotConfiguration.setRelativePath("/");
		city2ModelSlotConfiguration.setFilename("city.city2");
		assertTrue(city2ModelSlotConfiguration.isValidConfiguration());
		// Do Action CreateDiagram.
		createDiagram.doAction();
		System.out.println("exception thrown=" + createDiagram.getThrownException());
		assertTrue(createDiagram.hasActionExecutionSucceeded());
		Diagram newDiagram = createDiagram.getNewDiagram();
		System.out.println("New diagram " + newDiagram + " created in " + newDiagram.getResource().getFile());
		assertNotNull(newDiagram);
		assertEquals(createDiagram.getNewVirtualModelInstanceName(), newDiagram.getName());
		assertEquals(createDiagram.getNewVirtualModelInstanceTitle(), newDiagram.getTitle());
		assertEquals(createDiagram.getDiagramSpecification(), cityMappingViewPoint.getDefaultDiagramSpecification());
		assertTrue(newDiagram.getResource().getFile().exists());
		assertEquals(project, newDiagram.getResource().getProject());
		assertEquals(project, newDiagram.getProject());

		// Test ModelSlotInstance well created and initialized
		assertEquals(3, newDiagram.getModelSlotInstances().size());
		ModelSlotInstance<?, ?> diagramModelSlotInstance = newDiagram.getModelSlotInstance("diagram");
		assertNotNull(diagramModelSlotInstance);
		TypeAwareModelSlotInstance<?, ?, ?> city1ModelSlotInstance = (TypeAwareModelSlotInstance<?, ?, ?>) newDiagram
				.getModelSlotInstance("city1");
		assertNotNull(city1ModelSlotInstance);
		TypeAwareModelSlotInstance<?, ?, ?> city2ModelSlotInstance = (TypeAwareModelSlotInstance<?, ?, ?>) newDiagram
				.getModelSlotInstance("city2");
		assertNotNull(city2ModelSlotInstance);
		// System.out.println("DiagramModel=" + diagramModelSlotInstance.getModelURI());
		System.out.println("City1Model=" + city1ModelSlotInstance.getModelURI());
		System.out.println("City2Model=" + city2ModelSlotInstance.getModelURI());
		assertNotNull(city1ModelSlotInstance.getModelURI());
		assertNotNull(city2ModelSlotInstance.getModelURI());
		assertNotNull(city1ModelSlotInstance.getModel());
		assertNotNull(city2ModelSlotInstance.getModel());

		// Populate Diagram
		DiagramSpecification diagramSpecification = cityMappingViewPoint.getDefaultDiagramSpecification();
		assertEquals(6, diagramSpecification.getEditionPatterns().size());
		EditionPattern cityEditionPattern = diagramSpecification.getEditionPattern("City");
		Vector<DropScheme> cityDropSchemes = cityEditionPattern.getDropSchemes();
		assertEquals(1, cityDropSchemes.size());

		DropSchemeAction cityDropSchemeAction = DropSchemeAction.actionType.makeNewAction(createDiagram.getNewDiagram().getRootPane(),
				null, editor);
		DropScheme cityDropScheme = cityDropSchemes.get(0);
		cityDropSchemeAction.setDropScheme(cityDropScheme);
		EditionSchemeParameter cityNameParameter = cityDropScheme.getParameter("name");
		assertNotNull(cityNameParameter);
		cityDropSchemeAction.setParameterValue(cityNameParameter, "Brest");
		EditionSchemeParameter cityCity1UriParameter = cityDropScheme.getParameter("city1Uri");
		assertNotNull(cityCity1UriParameter);
		cityDropSchemeAction.setParameterValue(cityCity1UriParameter, "city1");
		EditionSchemeParameter cityCity2UriParameter = cityDropScheme.getParameter("city2Uri");
		assertNotNull(cityCity2UriParameter);
		cityDropSchemeAction.setParameterValue(cityCity2UriParameter, "city2");
		assertEquals("Brest", (String) cityDropSchemeAction.getParameterValue(cityNameParameter));
		assertEquals("city1", (String) cityDropSchemeAction.getParameterValue(cityCity1UriParameter));
		assertEquals("city2", (String) cityDropSchemeAction.getParameterValue(cityCity2UriParameter));
		cityDropSchemeAction.doAction();
	}

	private ViewPoint loadViewPoint(String viewPointURI) {
		log("Testing ViewPoint loading: " + viewPointURI);
		System.out.println("resourceCenter=" + resourceCenter);
		System.out.println("resourceCenter.getViewPointRepository()=" + resourceCenter.getViewPointRepository());
		ViewPointResource viewPointResource = resourceCenter.getViewPointRepository().getResource(viewPointURI);
		assertNotNull(viewPointResource);
		assertFalse(viewPointResource.isLoaded());
		ViewPoint viewPoint = viewPointResource.getViewPoint();
		assertTrue(viewPointResource.isLoaded());
		return viewPoint;
	}
}
