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
package org.openflexo.ie.view.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import junit.framework.Assert;

import org.openflexo.Flexo;
import org.openflexo.FlexoModuleTestCase;
import org.openflexo.GeneralPreferences;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.MakePartialComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.ie.IEModule;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoadingException;
import org.openflexo.module.ProjectLoader;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public class TestIEController extends FlexoModuleTestCase {

	protected static final Logger logger = Logger.getLogger(TestIEController.class.getPackage().getName());

	private FlexoEditor _editor;
	private FlexoProject _project;
	private File _projectDirectory;
	private String _projectIdentifier;
	private FlexoComponentLibrary _cl;
	private FlexoComponentFolder _cf;
	private IEOperationComponent _oc;
	private IEOperationComponent _targetComponent;
	private static final String TEST_REUSABLE_COMPONENT_VIEW = "TestReusableComponentView";
	private static final String TEST_COMPONENT = "TestComponent";
	private static final String PARTIAL_COMPONENT = "PartialTestComponent";
	private static final String TEST_TARGET_COMPONENT = "TestTargetComponent";
	private static final String TEST_COMPONENT_FOLDER = "TestFolder";

	public TestIEController(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new empty project in a temp directory
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public void test0CreateProject() throws InterruptedException, InvocationTargetException {
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		try {
			File tempFile = File.createTempFile(TEST_REUSABLE_COMPONENT_VIEW, "");
			_projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: " + _projectDirectory.getAbsolutePath());
		_projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length() - 4);
		logger.info("Project identifier: " + _projectIdentifier);
		_editor = FlexoResourceManager.initializeNewProject(_projectDirectory, EDITOR_FACTORY, null);
		_project = _editor.getProject();
		logger.info("Project has been SUCCESSFULLY created");

		_cl = _project.getFlexoComponentLibrary();
		AddComponentFolder addComponentFolder = AddComponentFolder.actionType.makeNewAction(_cl, null);
		addComponentFolder.setNewFolderName(TEST_COMPONENT_FOLDER);
		addComponentFolder.doAction();
		assertTrue(addComponentFolder.hasActionExecutionSucceeded());
		_cf = _cl.getRootFolder().getFlexoComponentFolderWithName(TEST_COMPONENT_FOLDER);
		assertNotNull(_cf);
		_cl = _project.getFlexoComponentLibrary();

		AddComponent addComponent = AddComponent.actionType.makeNewAction(_cf, null);
		addComponent.setNewComponentName(TEST_COMPONENT);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_oc = _project.getOperationComponent(TEST_COMPONENT);
		assertNotNull(_oc);

		AddComponent targetComponent = AddComponent.actionType.makeNewAction(_cf, null);
		targetComponent.setNewComponentName(TEST_TARGET_COMPONENT);
		targetComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		targetComponent.doAction();
		assertTrue(targetComponent.hasActionExecutionSucceeded());
		_targetComponent = _project.getOperationComponent(TEST_TARGET_COMPONENT);
		assertNotNull(_targetComponent);

		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_oc, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_oc, 1, _editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc2 = (IEBlocWidget) dropBloc2.getDroppedWidget();
		assertNotNull(bloc2);
		bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_oc, 1, _editor);
		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc3 = (IEBlocWidget) dropBloc3.getDroppedWidget();
		assertNotNull(bloc3);
		bloc3.setTitle("Bloc3");

		// Drop a table in the bloc1
		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc1, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);

		// Drop a table in the bloc2
		DropIEElement dropTable2 = DropIEElement.createTableInBloc(bloc2, _editor);
		assertTrue(dropTable2.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table2 = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table2);

		// Save project
		saveProject();

		// reuse block1

		MakePartialComponent makePartial = MakePartialComponent.actionType.makeNewAction(bloc1, null);
		makePartial.setNewComponentName(PARTIAL_COMPONENT);
		makePartial.setNewComponentFolder(_cf);

		assertTrue(makePartial.doAction().hasActionExecutionSucceeded());
		assertEquals(table.getWOComponent(), bloc1.getWOComponent());
		assertEquals(table.getWOComponent(), makePartial.getComponent());
		// Save project
		saveProject();
		ReusableComponentDefinition newComponent = (ReusableComponentDefinition) _cl.getComponentNamed(PARTIAL_COMPONENT);
		assertNotNull(newComponent);
		DropPartialComponent dropPartial = DropPartialComponent.actionType.makeNewAction(_targetComponent, null);
		dropPartial.setPartialComponent(newComponent);
		assertTrue(dropPartial.doAction().hasActionExecutionSucceeded());
		saveProject();

		// DropPartialComponent dropPartial2 = DropPartialComponent.actionType.makeNewAction(table2.getTDAt(0, 0).getSequenceWidget(),null);
		// dropPartial2.setPartialComponent(newComponent);
		// dropPartial2.setIndex(0);
		// assertTrue(dropPartial2.doAction().hasActionExecutionSucceeded());

		// assertNotSame(dropPartial.getComponentInstance(), dropPartial2.getComponentInstance());
		saveProject();

		_project.close();
		_project = null;

		_editor = FlexoResourceManager.initializeNewProject(_projectDirectory, INTERACTIVE_EDITOR_FACTORY, null);
		_project = _editor.getProject();

		initModuleLoader(_projectDirectory, _project);
		IEModule ie = null;
		try {
			ie = (IEModule) getModuleLoader().switchToModule(Module.IE_MODULE, _project);
		} catch (ModuleLoadingException e) {
			Assert.fail("Fail to load IE module." + e.getMessage());
			e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
		}

		ie.focusOn();
		assertTrue(getModuleLoader().isActive(Module.IE_MODULE));
		assertNotNull(ie);
		IEController ctrl = ie.getIEController();
		assertNotNull(ctrl);
		IESelectionManager selectionManager = ctrl.getIESelectionManager();
		assertNotNull(selectionManager);
		assertTrue(selectionManager.getSelectionListenersCount() == 4);
		ctrl.setSelectedComponent(_oc.getComponentDefinition().getDummyComponentInstance());
		assertTrue(selectionManager.getSelectionListenersCount() == 6);
		ctrl.setSelectedComponent(null);
		assertTrue(selectionManager.getSelectionListenersCount() == 4);
		ctrl.setSelectedComponent(_oc.getComponentDefinition().getDummyComponentInstance());
		assertTrue(selectionManager.getSelectionListenersCount() == 6);
		DropPartialComponent dropPartial2 = DropPartialComponent.actionType.makeNewAction(_oc, null);
		dropPartial2.setPartialComponent(newComponent);
		assertTrue(dropPartial2.doAction().hasActionExecutionSucceeded());
		assertTrue(selectionManager.getSelectionListenersCount() == 7);
		dropPartial2.getDroppedWidget().delete();
		assertTrue(selectionManager.getSelectionListenersCount() == 6);
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ProjectLoader.instance().closeCurrentProject();
			}
		});
		FileUtils.deleteDir(_projectDirectory);
	}

	/**
	 * @param projectDirectory
	 * @param project
	 */
	private void initModuleLoader(File projectDirectory, FlexoProject project) {
		getModuleLoader().setAllowsDocSubmission(false);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Init Module loader...");
		}
		if (GeneralPreferences.getFavoriteModuleName() == null) {
			GeneralPreferences.setFavoriteModuleName(Module.IE_MODULE.getName());
		}
		Flexo.setFileNameToOpen(projectDirectory.getAbsolutePath());
		if (ExternalModuleDelegater.getModuleLoader() == null) {
			fail("Module loader is not there. Screenshots cannot be generated");
		} else {
			try {
				if (ExternalModuleDelegater.getModuleLoader().getIEModuleInstance(project) == null) {
					fail("IE Module not on the classpath. Component screenshots cannot be generated");
				}
			} catch (ModuleLoadingException e) {
				Assert.fail("Fail to load IE module." + e.getMessage());
			}
		}
	}

	/**
	 * Save the project
	 * 
	 */
	private void saveProject() {
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}
}
