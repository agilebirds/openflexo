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
package org.openflexo.foundation.dm;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.dm.action.ImportJARFileRepository;
import org.openflexo.foundation.ie.TestCreateComponent;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;

public class TestLoadJar extends FlexoDMTestCase {

	protected static final Logger logger = Logger.getLogger(TestCreateComponent.class.getPackage().getName());
	private static final String TEST_LOADJAR = "TestLoadJar";
	private static final String TEST_JAR_REPOSITORY_NAME = "TestJarRepositoryName";

	public TestLoadJar() {
		super("TestLoadJar");
	}

	/**
	 * Overrides setUp
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void test0CreateProject() {
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		createProject(TEST_LOADJAR);
		assertNotNull(_project.getDataModel().getEntityNamed("be.denali.core.woapp.WDLComponent"));
	}

	/**
	 * Creates a new component folder in the component library
	 */
	public void test1CreateJarRepository() {
		File jarFile = getResource("dummy.jar");
		assertNotNull(jarFile);
		ImportJARFileRepository importJarFileRepository = ImportJARFileRepository.actionType.makeNewAction(_project.getDataModel(), null,
				_editor);
		importJarFileRepository.setNewRepositoryName(TEST_JAR_REPOSITORY_NAME);
		importJarFileRepository.setJarFile(jarFile);
		importJarFileRepository.setRepositoryType(CreateDMRepository.EXTERNAL_REPOSITORY);
		importJarFileRepository.setProject(_project);
		importJarFileRepository.doAction();
		assertTrue(importJarFileRepository.hasActionExecutionSucceeded());
		assertNotNull(_project.getDataModel().getExternalRepository(_project.getJarResource("dummy.jar")));
		saveProject();
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
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
