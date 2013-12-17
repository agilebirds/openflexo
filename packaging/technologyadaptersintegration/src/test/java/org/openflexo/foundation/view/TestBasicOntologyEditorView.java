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
package org.openflexo.foundation.view;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.action.AddRepositoryFolder;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.ViewPointResource;
import org.openflexo.foundation.rm.ViewResource;
import org.openflexo.foundation.view.action.CreateView;
import org.openflexo.foundation.view.diagram.action.CreateDiagram;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.viewpoint.ViewPoint;

public class TestBasicOntologyEditorView extends FlexoTestCase {

	public static FlexoProject project;
	private static FlexoEditor editor;
	private static ViewPoint basicOntologyEditor;
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

	public void test2LoadBasicOntologyEditorViewPoint() {
		basicOntologyEditor = loadViewPoint("http://www.agilebirds.com/openflexo/ViewPoints/Basic/BasicOntology.owl");
		assertNotNull(basicOntologyEditor);
		System.out.println("Found view point in " + basicOntologyEditor.getResource().getFile());
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
		addView.viewpointResource = basicOntologyEditor.getResource();
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + newView.getResource().getFile());
		assertNotNull(newView);
		assertEquals(addView.newViewName, newView.getName());
		assertEquals(addView.newViewTitle, newView.getTitle());
		assertEquals(addView.viewpointResource.getViewPoint(), basicOntologyEditor);
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

	/*public void test6CreateVirtualModel() {
		CreateVirtualModelInstance createVirtualModelInstance = CreateVirtualModelInstance.actionType.makeNewAction(view, null, editor);
		createVirtualModelInstance.newVirtualModelInstanceName = "TestNewVirtualModel";
		createVirtualModelInstance.newVirtualModelInstanceTitle = "A nice title for a new virtual model instance";
		createVirtualModelInstance.virtualModel = basicOntologyEditor;
		addView.doAction();
		assertTrue(addView.hasActionExecutionSucceeded());
		View newView = addView.getNewView();
		System.out.println("New view " + newView + " created in " + newView.getResource().getFile());
		assertNotNull(newView);
		assertEquals(addView.newViewName, newView.getName());
		assertEquals(addView.newViewTitle, newView.getTitle());
		assertEquals(addView.viewpoint, basicOntologyEditor);
		assertTrue(newView.getResource().getFile().exists());
	}*/

	public void test6CreateDiagram() {
		System.out.println("Create diagram, view=" + view + " editor=" + editor);
		System.out.println("editor project = " + editor.getProject());
		System.out.println("view project = " + view.getProject());
		CreateDiagram createDiagram = CreateDiagram.actionType.makeNewAction(view, null, editor);
		createDiagram.setNewVirtualModelInstanceName("TestNewDiagram");
		createDiagram.setNewVirtualModelInstanceTitle("A nice title for a new diagram");
		createDiagram.setDiagramSpecification(basicOntologyEditor.getDefaultDiagramSpecification());
		createDiagram.doAction();
		System.out.println("exception thrown=" + createDiagram.getThrownException());
		// createDiagram.getThrownException().printStackTrace();
		assertTrue(createDiagram.hasActionExecutionSucceeded());
		Diagram newDiagram = createDiagram.getNewDiagram();
		System.out.println("New diagram " + newDiagram + " created in " + newDiagram.getResource().getFile());
		assertNotNull(newDiagram);
		assertEquals(createDiagram.getNewVirtualModelInstanceName(), newDiagram.getName());
		assertEquals(createDiagram.getNewVirtualModelInstanceTitle(), newDiagram.getTitle());
		assertEquals(createDiagram.getDiagramSpecification(), basicOntologyEditor.getDefaultDiagramSpecification());
		assertTrue(newDiagram.getResource().getFile().exists());
		assertEquals(project, newDiagram.getResource().getProject());
		assertEquals(project, newDiagram.getProject());
	}
}
