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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.view.action.CreateView;
import org.openflexo.foundation.view.diagram.action.CreateDiagram;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.viewpoint.ViewPoint;

/**
 * Test instanciation of City Mapping View with 2 EMF
 * 
 * @author gbesancon
 */
public class TestEMFCityMappingView extends FlexoTestCase {

	/**
	 * Follow the link.
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		instanciateTestServiceManager();
	}

	public void testEMFCityMapping() {
		// CreateProject
		FlexoEditor editor = createProject("TestCreateView");
		FlexoProject project = editor.getProject();
		assertNotNull(project.getViewLibrary());

		// Load CityMapping ViewPoint
		ViewPoint cityMappingVP = loadViewPoint("http://www.thalesgroup.com/openflexo/emf/CityMapping");
		assertNotNull(cityMappingVP);
		System.out.println("Found view point in " + cityMappingVP.getResource().getFile());

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
		createDiagram.setDiagramSpecification(cityMappingVP.getDefaultDiagramSpecification());
		createDiagram.doAction();
		System.out.println("exception thrown=" + createDiagram.getThrownException());
		assertTrue(createDiagram.hasActionExecutionSucceeded());
		Diagram newDiagram = createDiagram.getNewDiagram();
		System.out.println("New diagram " + newDiagram + " created in " + newDiagram.getResource().getFile());
		assertNotNull(newDiagram);
		assertEquals(createDiagram.getNewVirtualModelInstanceName(), newDiagram.getName());
		assertEquals(createDiagram.getNewVirtualModelInstanceTitle(), newDiagram.getTitle());
		assertEquals(createDiagram.getDiagramSpecification(), cityMappingVP.getDefaultDiagramSpecification());
		assertTrue(newDiagram.getResource().getFile().exists());
		assertEquals(project, newDiagram.getResource().getProject());
		assertEquals(project, newDiagram.getProject());

		// Populate Diagram
		DropSchemeAction dropSchemeAction = DropSchemeAction.actionType.makeNewAction(view, null, editor);
		dropSchemeAction.doAction();
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
