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
package org.openflexo.foundation.cg;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;



import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.ToolBox;

public class TestCGFoundation extends FlexoTestCase {

    public TestCGFoundation(String arg0) {
		super(arg0);
	}

    protected static final Logger logger = Logger.getLogger(TestCGFoundation.class.getPackage().getName());

    private static final String TEST_CG = "TestCG";

 	private static FlexoEditor _editor;
 	private static FlexoProject _project;
	private static File _projectDirectory;
	private static String _projectIdentifier;

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject()
	{
		logger.info("test0CreateProject");
       ToolBox.setPlatform();
       FlexoLoggingManager.forceInitialize();
		try {
			File tempFile = File.createTempFile(TEST_CG, "");
			_projectDirectory = new File (tempFile.getParentFile(),tempFile.getName()+".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: "+_projectDirectory.getAbsolutePath());
		_projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length()-4);
		logger.info("Project identifier: "+_projectIdentifier);
		_editor = FlexoResourceManager.initializeNewProject(_projectDirectory,EDITOR_FACTORY,null);
		_project = _editor.getProject();
		logger.info("Project has been SUCCESSFULLY created");
	}


	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test1CreateExternalRepository()
	{
		logger.info("test1CreateExternalRepository");
		AddGeneratedCodeRepository action = AddGeneratedCodeRepository.actionType.makeNewAction(_project.getGeneratedCode(), null, _editor);
		action.setNewGeneratedCodeRepositoryName("GeneratedCode");
		action.setNewGeneratedCodeRepositoryDirectory(new File("/tmp/TestGeneratedCode"));
		action.doAction();
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
 	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test2Reload()
	{
		logger.info("test2Reload");
		try {
		    if (_project!=null)
		        _project.close();
			assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(_projectDirectory,EDITOR_FACTORY,null));
			_project = _editor.getProject();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}
		logger.info("_project.getGeneratedCode()="+_project.getGeneratedCode());
		_project.close();
 	}
}
