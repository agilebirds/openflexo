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
package org.openflexo.dm.view.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.FlexoModuleTestCase;
import org.openflexo.GeneralPreferences;
import org.openflexo.dm.DMModule;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.module.external.ExternalModuleDelegater;
import org.openflexo.toolbox.ToolBox;

public class TestDMController extends FlexoModuleTestCase {

	@Override
	protected void setUp() throws Exception {

	}

	@Override
	protected void tearDown() throws Exception {
	}

	protected static final Logger logger = Logger.getLogger(TestDMController.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;
	private static File _projectDirectory;
	private static String _projectIdentifier;
	private static final String TEST_REUSABLE_COMPONENT_VIEW = "TestDMController";

	public TestDMController(String arg0) {
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
		saveProject();

		_project.close();
		_project = null;

		_editor = FlexoResourceManager.initializeNewProject(_projectDirectory, INTERACTIVE_EDITOR_FACTORY, null);
		_project = _editor.getProject();

		initModuleLoader(_projectDirectory, _project);
		ModuleLoader.switchToModule(Module.DM_MODULE);

		DMModule ie = (DMModule) Module.DM_MODULE.getInstance();
		ie.focusOn();
		assertTrue(ModuleLoader.isActive(Module.DM_MODULE));
		assertNotNull(ie);
		DMController ctrl = ie.getDMController();
		assertNotNull(ctrl);
		DMSelectionManager selectionManager = ctrl.getDMSelectionManager();
		assertNotNull(selectionManager);
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ModuleLoader.closeCurrentProject();
			}
		});

	}

	/**
	 * @param projectDirectory
	 * @param flexoProject
	 */
	private void initModuleLoader(File projectDirectory, FlexoProject project) {
		ModuleLoader.setAllowsDocSubmission(false);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Init Module loader...");
		}
		if (GeneralPreferences.getFavoriteModuleName() == null) {
			GeneralPreferences.setFavoriteModuleName(Module.WKF_MODULE.getName());
		}
		ModuleLoader.fileNameToOpen = projectDirectory.getAbsolutePath();
		ModuleLoader.initializeModules(UserType.getUserTypeNamed("DEVELOPPER"), false);
		ModuleLoader.setProject(project);
		if (ExternalModuleDelegater.getModuleLoader() == null) {
			fail("Module loader is not there. Screenshots cannot be generated");
		} else if (ExternalModuleDelegater.getModuleLoader().getDMModuleInstance() == null) {
			fail("WKF Module not on the classpath. Component screenshots cannot be generated");
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
