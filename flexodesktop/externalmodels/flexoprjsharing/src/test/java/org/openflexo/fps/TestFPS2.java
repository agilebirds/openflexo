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
package org.openflexo.fps;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


import junit.framework.AssertionFailedError;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.fps.CVSConsole;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.CVSStatus;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.CVSRefresh;
import org.openflexo.fps.action.CheckoutProject;
import org.openflexo.fps.action.ShareProject;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.ToolBox;

public class TestFPS2 extends FPSTestCase  {

	protected static final Logger logger = Logger.getLogger(TestFPS2.class.getPackage().getName());

	protected static FlexoEditor _initialProjectEditor;
	protected static FlexoProject _initialProject;

	protected static FlexoEditor _checkoutedProjectEditor;
	protected static FlexoProject _checkoutedProject;
	protected static String _projectName;
	protected static String _projectIdentifier;
	
	protected static CVSRepositoryList _repositories;
	protected static CVSRepository _workingCVSRepository;
	
	protected static SharedProject _project1;
	protected static SharedProject _project2;

	public TestFPS2(String name) {
		super(name);
	}

	private static final String TEST_FPS = "TestFPS";

	static {
	      ToolBox.setPlatform();
	      FlexoLoggingManager.forceInitialize();
	      CVSConsole.logger.setLevel(Level.FINE);
	}
	
	private static FlexoEditor EDITOR = new DefaultFlexoEditor(null);
	
 	/**
	 * Creates a new project in a temp directory
	 */
	public void test0CreateProject()
	{
		log("test0CreateProject");
		_initialProjectEditor = createFPSProject(TEST_FPS);
		_initialProject = _initialProjectEditor.getProject();
		_projectName = _initialProject.getProjectName()+".prj";
		_projectIdentifier = _initialProject.getProjectName();
	}

	/**
	 * Initialize CVS environment by initializing a new CVS repository
	 */
	public void test1InitCVS()
	{
		log("test1InitCVS");
		_repositories = new CVSRepositoryList();
		FileResource cvsRepFile = new FileResource("src/test/resources/TestCVSRepository.cvs");
		_workingCVSRepository = new CVSRepository(cvsRepFile);
		_repositories.addToCVSRepositories(_workingCVSRepository);
		CVSRefresh refreshRepository = CVSRefresh.actionType.makeNewAction(_workingCVSRepository,null, EDITOR);
		assertTrue(refreshRepository.doAction().hasActionExecutionSucceeded());
	}

	/**
	 * Share this project
	 */
	public void test2ShareProjectAs()
	{
		log("test2ShareProjectAs");
		
		ShareProject shareProject = ShareProject.actionType.makeNewAction(_repositories,null, EDITOR);
		shareProject.setProjectDirectory(_initialProject.getProjectDirectory());
		shareProject.setRepository(_workingCVSRepository);
		shareProject.setModuleName("Module"+_initialProject.getProjectName()+File.separator+"SubModule"+File.separator+"Renamed"+_initialProject.getProjectName()+".prj");
		shareProject.setCvsIgnorize(true);
		assertTrue(shareProject.doAction().hasActionExecutionSucceeded());
		_project1 = shareProject.getProject();
	}

	/**
	 * Now checkout in a new location, with a new name
	 */
	public void test3CheckoutProjectAs()
	{		
		log("test3CheckoutProjectAs");
		File checkoutDirectory = new File(
				_initialProject.getProjectDirectory().getParentFile(),
				"TestCheckout");
		if (!checkoutDirectory.exists()) checkoutDirectory.mkdirs();

		// Retrieve module
		CVSModule moduleToCheckout = _project1.getCVSModule();
		logger.info ("Trying to checkout "+moduleToCheckout.getFullQualifiedModuleName()+" in "+checkoutDirectory);
		assertNotNull(moduleToCheckout);
		
		// Perform the checkout
		CheckoutProject checkoutProject = CheckoutProject.actionType.makeNewAction(moduleToCheckout, null, EDITOR);
		checkoutProject.setLocalDirectory(checkoutDirectory);
		checkoutProject.setLocalName("Checkouted"+_initialProject.getProjectName()+".prj");
		assertTrue(checkoutProject.doAction().hasActionExecutionSucceeded());
		_project2 = checkoutProject.getCheckoutedProject();
		
		assertAllFilesAreUpToDateOrCVSIgnored(_project2);
		assertEquals(_project2.getCVSFile(".cvsrepository").getStatus(), CVSStatus.CVSIgnored);
		
		// Test that checkouted project is still readable by Flexo
		try {
			assertNotNull(_checkoutedProjectEditor = FlexoResourceManager.initializeExistingProject(_project2.getModuleDirectory(),EDITOR_FACTORY,null));
			_checkoutedProject = _checkoutedProjectEditor.getProject();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}
		// The last test must call this to stop RM checking
		_checkoutedProject.close();
	}

	private void assertAllFilesAreUpToDateOrCVSIgnored(SharedProject prj)
	{
		assertAllFilesAreUpToDateOrCVSIgnoredExcept(prj,new CVSFile[0]);
	}
	
	private void assertAllFilesAreUpToDateOrCVSIgnoredExcept(SharedProject prj, CVSFile... files)
	{
		String errors = "";
		boolean hasErrors = false;
		for (CVSFile f : prj.getAllCVSFiles()) {
			//logger.info("File "+f.getFile()+" status="+f.getStatus());
			boolean isException = false;
			for (CVSFile f2 : files) {
				if (f == f2) isException=true;
			}
			try {
			if (!isException) assertTrue(f.getStatus() == CVSStatus.CVSIgnored || f.getStatus() == CVSStatus.UpToDate);
			}
			catch (AssertionFailedError e) {
				hasErrors = true;
				errors += "Found file "+f.getFile()+" status="+f.getStatus()+" expected CVSIgnored or UpToDate\n";
			}
		}
		if (hasErrors) {
			logger.warning("Status errors\n"+errors);
			fail();
		}
	}
	
}
