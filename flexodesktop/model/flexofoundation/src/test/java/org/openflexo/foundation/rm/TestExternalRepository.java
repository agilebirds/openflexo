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
package org.openflexo.foundation.rm;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;

public class TestExternalRepository extends FlexoTestCase {

	public TestExternalRepository(String arg0) {
		super(arg0);
	}

	protected static final Logger logger = Logger.getLogger(TestExternalRepository.class.getPackage().getName());

	private static final String TEST_ER = "TestExternalRepository";

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		logger.info("test0CreateProject");
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		createProject(TEST_ER);
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test1CreateExternalRepository() {
		logger.info("test1CreateExternalRepository");
		_project.setDirectoryForRepositoryName("repository1", new File(System.getProperty("user.home")));
		_project.setDirectoryForRepositoryName("repository2", new File(System.getProperty("user.home") + "/Test"));
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test2CreateResource() {
		logger.info("test2CreateResource : not maintained");
		// try {
		// CustomInspectorsResource res = new CustomInspectorsResource(_project,new FlexoProjectFile("repository1:temp"));
		// _project.registerResource(res);
		// CustomTemplatesResource res2 = new CustomTemplatesResource(_project,"Templates",new
		// FlexoProjectFile(_project,_project.getExternalRepositoryWithKey("repository2"),"SomeTemplates"));
		// _project.registerResource(res2);
		// logger.info("res.getFile()"+res.getFile().getAbsolutePath());
		// logger.info("res2.getFile()"+res2.getFile().getAbsolutePath());
		// } catch (InvalidFileNameException e) {
		// fail();
		// } catch (DuplicateResourceException e) {
		// fail();
		// }
		// try {
		// _project.save();
		// } catch (SaveResourceException e) {
		// fail("Cannot save project");
		// }

	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test3Reload() {
		logger.info("test3Reload : not maintained");
		// try {
		// if (_project!=null)
		// _project.close();
		// assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(_projectDirectory,EDITOR_FACTORY));
		// _project = _editor.getProject();
		// } catch (ProjectInitializerException e) {
		// e.printStackTrace();
		// fail();
		// } catch (ProjectLoadingCancelledException e) {
		// e.printStackTrace();
		// fail();
		// }
		// CustomInspectorsResource res = _project.getCustomInspectorsResource();
		// CustomTemplatesResource res2 = _project.getCustomTemplatesResources().firstElement();
		// logger.info("res.getFile()"+res.getFile().getAbsolutePath());
		// logger.info("res2.getFile()"+res2.getFile().getAbsolutePath());
		// _project.close();
		// FileUtils.deleteDir(_project.getProjectDirectory());
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_project = null;
		_editor = null;
		_projectDirectory = null;
		_projectIdentifier = null;
	}

}
