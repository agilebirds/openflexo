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
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;

import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.fps.CVSConsole;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.CVSStatus;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.SharedProject;
import org.openflexo.fps.action.CVSRefresh;
import org.openflexo.fps.action.CheckoutProject;
import org.openflexo.fps.action.CommitFiles;
import org.openflexo.fps.action.MarkAsMergedFiles;
import org.openflexo.fps.action.OpenSharedProject;
import org.openflexo.fps.action.RefreshProject;
import org.openflexo.fps.action.ShareProject;
import org.openflexo.fps.action.SynchronizeWithRepository;
import org.openflexo.fps.action.UpdateFiles;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public class TestFPS extends FPSTestCase {

	protected static final Logger logger = Logger.getLogger(TestFPS.class.getPackage().getName());

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

	public TestFPS(String name) {
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
	public void test0CreateProject() {
		log("test0CreateProject");
		_initialProjectEditor = createFPSProject(TEST_FPS);
		_initialProject = _initialProjectEditor.getProject();
		_projectName = _initialProject.getProjectName() + ".prj";
		_projectIdentifier = _initialProject.getProjectName();
	}

	/**
	 * Initialize CVS environment by initializing a new CVS repository
	 */
	public void test1InitCVS() {
		log("test1InitCVS");
		_repositories = new CVSRepositoryList();
		FileResource cvsRepFile = new FileResource("src/test/resources/TestCVSRepository.cvs");
		_workingCVSRepository = new CVSRepository(cvsRepFile);
		_repositories.addToCVSRepositories(_workingCVSRepository);
		CVSRefresh refreshRepository = CVSRefresh.actionType.makeNewAction(_workingCVSRepository, null, EDITOR);
		assertTrue(refreshRepository.doAction().hasActionExecutionSucceeded());
		assertFalse(refreshRepository.hasReceivedTimeout());
		assertTrue("All modules could not be explored", refreshRepository.getExplorableFailed().size() == 0);
		for (CVSModule m : _workingCVSRepository.getCVSModules()) {
			logger.info("Found module " + m.getModuleName());
		}
	}

	/**
	 * Share this project
	 */
	public void test2ShareProject() {
		log("test2ShareProject");

		File testFile1 = new File(_initialProject.getProjectDirectory(), "removeThisFileLater");
		File testFile2 = new File(_initialProject.getProjectDirectory(), "removeThisFileLater2");
		File testFile3 = new File(_initialProject.getProjectDirectory(), "removeThisFileLater3");
		try {
			FileUtils.saveToFile(testFile1, "foo");
			FileUtils.saveToFile(testFile2, "foo");
			FileUtils.saveToFile(testFile3, "foo");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		ShareProject shareProject = ShareProject.actionType.makeNewAction(_repositories, null, EDITOR);
		shareProject.setProjectDirectory(_initialProject.getProjectDirectory());
		shareProject.setRepository(_workingCVSRepository);
		shareProject.setCvsIgnorize(true);
		assertTrue(shareProject.doAction().hasActionExecutionSucceeded());
		_project1 = shareProject.getProject();
	}

	/**
	 * Now checkout in a new location
	 */
	public void test3CheckoutProject() {
		log("test3CheckoutProject");
		File checkoutDirectory = null;
		try {
			File tempFile = File.createTempFile("tempFile", "");
			checkoutDirectory = new File(tempFile.getParentFile(), "Checkout");
			tempFile.delete();
			if (!checkoutDirectory.exists()) {
				checkoutDirectory.mkdirs();
			}
		} catch (IOException e) {
			fail();
		}

		// Retrieve module
		CVSModule moduleToCheckout = _workingCVSRepository.getModuleNamed(_projectName);
		assertNotNull(moduleToCheckout);

		// Perform the checkout
		CheckoutProject checkoutProject = CheckoutProject.actionType.makeNewAction(moduleToCheckout, null, EDITOR);
		checkoutProject.setLocalDirectory(checkoutDirectory);
		assertTrue(checkoutProject.doAction().hasActionExecutionSucceeded());
		_project2 = checkoutProject.getCheckoutedProject();

		assertAllFilesAreUpToDateOrCVSIgnored(_project2);
		assertEquals(_project2.getCVSFile(".cvsrepository").getStatus(), CVSStatus.CVSIgnored);

	}

	private void assertAllFilesAreUpToDateOrCVSIgnored(SharedProject prj) {
		assertAllFilesAreUpToDateOrCVSIgnoredExcept(prj, new CVSFile[0]);
	}

	private void assertAllFilesAreUpToDateOrCVSIgnoredExcept(SharedProject prj, CVSFile... files) {
		String errors = "";
		boolean hasErrors = false;
		for (CVSFile f : prj.getAllCVSFiles()) {
			// logger.info("File "+f.getFile()+" status="+f.getStatus());
			boolean isException = false;
			for (CVSFile f2 : files) {
				if (f == f2)
					isException = true;
			}
			try {
				if (!isException)
					assertTrue(f.getStatus() == CVSStatus.CVSIgnored || f.getStatus() == CVSStatus.UpToDate);
			} catch (AssertionFailedError e) {
				hasErrors = true;
				errors += "Found file " + f.getFile() + " status=" + f.getStatus() + " expected CVSIgnored or UpToDate\n";
			}
		}
		if (hasErrors) {
			logger.warning("Status errors\n" + errors);
			fail();
		}
	}

	/**
	 * Perform some modifs on checkouted project
	 */
	public void test4PerformSomeModifs() {
		log("test4PerformSomeModifs");

		// Test that checkouted project is still readable by Flexo
		try {
			assertNotNull(_checkoutedProjectEditor = FlexoResourceManager.initializeExistingProject(_project2.getModuleDirectory(),
					EDITOR_FACTORY, null));
			_checkoutedProject = _checkoutedProjectEditor.getProject();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}

		FlexoProcess rootProcess = _checkoutedProject.getFlexoWorkflow().getRootFlexoProcess();
		AbstractActivityNode node = rootProcess.getAbstractActivityNodeNamed(TEST_SUB_PROCESS);
		// node.setLocation(new Point(node.getLocation().x+50,node.getLocation().y+100));
		node.setX(node.getX("bpe") + 50, "bpe");
		node.setY(node.getY("bpe") + 100, "bpe");

		AddSubProcess action = AddSubProcess.actionType.makeNewAction(rootProcess, null, EDITOR);
		action.setParentProcess(rootProcess);
		action.setNewProcessName(TEST_SUB_PROCESS_2);
		action.doAction();
		FlexoProcess subProcess2 = action.getNewProcess();
		logger.info("SubProcess " + subProcess2.getName() + " successfully created");

		DropWKFElement dropSubProcessNode = DropWKFElement.actionType.makeNewAction(rootProcess.getActivityPetriGraph(), null, EDITOR);
		dropSubProcessNode.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		dropSubProcessNode.setParameter(DropWKFElement.SUB_PROCESS, subProcess2);
		dropSubProcessNode.setLocation(300, 150);
		dropSubProcessNode.doAction();
		assertTrue(dropSubProcessNode.hasActionExecutionSucceeded());
		SubProcessNode subProcessNode2 = (SubProcessNode) dropSubProcessNode.getObject();
		logger.info("SubProcessNode " + subProcessNode2.getName() + " successfully created");

		saveProject(_checkoutedProject);

		File testFile = new File(_checkoutedProject.getProjectDirectory(), "removeThisFileLater");
		assertTrue(testFile.exists());
		testFile.delete();
	}

	/**
	 * Check local synchronization
	 */
	public void test5CheckLocalSynchronization() {
		log("test5CheckLocalSynchronization");

		SynchronizeWithRepository syncAction = SynchronizeWithRepository.actionType.makeNewAction(_project2, null, EDITOR);
		assertTrue(syncAction.doAction().hasActionExecutionSucceeded());

		assertAllFilesAreUpToDateOrCVSIgnoredExcept(_project2, _project2.getCVSFile(_projectIdentifier + ".rmxml"),
				_project2.getCVSFile("removeThisFileLater"),
				_project2.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm"),
				_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf"),
				_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml"),
				_project2.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_2 + ".xml"),
				_project2.getCVSFile("Documentation" + File.separator + _projectIdentifier + ".toc"));

		assertEquals(_project2.getCVSFile(_projectIdentifier + ".rmxml").getStatus(), CVSStatus.LocallyModified);
		assertEquals(_project2.getCVSFile("removeThisFileLater").getStatus(), CVSStatus.LocallyRemoved);
		assertEquals(_project2.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm").getStatus(), CVSStatus.LocallyModified);
		assertEquals(_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf").getStatus(), CVSStatus.LocallyModified);
		assertEquals(_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml").getStatus(), CVSStatus.LocallyModified);
		assertEquals(_project2.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_2 + ".xml").getStatus(), CVSStatus.LocallyAdded);

	}

	/**
	 * Commit modified files
	 */
	public void test6CommitFiles() {
		log("test6CommitFiles");

		Vector<FPSObject> filesToCommit = new Vector<FPSObject>();
		filesToCommit.add(_project2.getCVSFile(_projectIdentifier + ".rmxml"));
		filesToCommit.add(_project2.getCVSFile("removeThisFileLater"));
		filesToCommit.add(_project2.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm"));
		filesToCommit.add(_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf"));
		filesToCommit.add(_project2.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml"));
		filesToCommit.add(_project2.getCVSFile("Documentation" + File.separator + _projectIdentifier + ".toc"));
		filesToCommit.add(_project2.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_2 + ".xml"));

		CommitFiles commitAction = CommitFiles.actionType.makeNewAction(null, filesToCommit, EDITOR);
		commitAction.setCommitMessage("Test commit");
		assertTrue(commitAction.doAction().hasActionExecutionSucceeded());

		assertAllFilesAreUpToDateOrCVSIgnored(_project2);

	}

	/**
	 * Commit modified files
	 */
	public void test7ResynchronizeFirstProject() {
		log("test7Update");

		File project1Directory = _project1.getModuleDirectory();

		// Forget first project
		_project1 = null;

		// And reload it
		OpenSharedProject openSharedProject = OpenSharedProject.actionType.makeNewAction(_repositories, null, EDITOR);
		openSharedProject.setProjectDirectory(project1Directory);
		assertTrue(openSharedProject.doAction().hasActionExecutionSucceeded());
		_project1 = openSharedProject.getNewProject();

		// Without CVS synchronization, not changes because no local changes
		assertAllFilesAreUpToDateOrCVSIgnored(_project1);

		// Now perform a synchronization with repository
		SynchronizeWithRepository syncAction = SynchronizeWithRepository.actionType.makeNewAction(_project1, null, EDITOR);
		assertTrue(syncAction.doAction().hasActionExecutionSucceeded());

		// See changes performed in step 4 as remote changes (since they were committed)
		assertAllFilesAreUpToDateOrCVSIgnoredExcept(_project1, _project1.getCVSFile(_projectIdentifier + ".rmxml"),
				_project1.getCVSFile("removeThisFileLater"),
				_project1.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm"),
				_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf"),
				_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml"),
				_project1.getCVSFile("Documentation" + File.separator + _projectIdentifier + ".toc"),
				_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_2 + ".xml"));

		assertEquals(_project1.getCVSFile(_projectIdentifier + ".rmxml").getStatus(), CVSStatus.RemotelyModified);
		assertEquals(_project1.getCVSFile("removeThisFileLater").getStatus(), CVSStatus.RemotelyRemoved);
		assertEquals(_project1.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm").getStatus(),
				CVSStatus.RemotelyModified);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf").getStatus(),
				CVSStatus.RemotelyModified);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml").getStatus(),
				CVSStatus.RemotelyModified);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_2 + ".xml").getStatus(), CVSStatus.RemotelyAdded);

	}

	/**
	 * Update modified files
	 */
	public void test8UpdateFiles() {
		log("test8UpdateFiles");

		UpdateFiles updateAction = UpdateFiles.actionType.makeNewAction(_project1, null, EDITOR);
		assertTrue(updateAction.doAction().hasActionExecutionSucceeded());

		assertAllFilesAreUpToDateOrCVSIgnored(_project1);

		// Test that checkouted project is still readable by Flexo
		try {
			assertNotNull(_initialProjectEditor = FlexoResourceManager.initializeExistingProject(_project1.getModuleDirectory(),
					EDITOR_FACTORY, null));
			_initialProject = _initialProjectEditor.getProject();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}

	}

	/**
	 * Perform some concurrent modifications
	 */
	public void test9PerformSomeConcurrentModifications() {
		log("test9PerformSomeConcurrentModifications");

		// Some modifs on checkouted project

		FlexoProcess rootProcess = _checkoutedProject.getFlexoWorkflow().getRootFlexoProcess();
		AbstractActivityNode node = rootProcess.getAbstractActivityNodeNamed(TEST_SUB_PROCESS);
		// node.setLocation(new Point(node.getLocation().x+50,node.getLocation().y+100));
		node.setX(node.getX("bpe") + 50, "bpe");
		node.setY(node.getY("bpe") + 100, "bpe");

		AddSubProcess action = AddSubProcess.actionType.makeNewAction(rootProcess, null, EDITOR);
		action.setParentProcess(rootProcess);
		action.setNewProcessName(TEST_SUB_PROCESS_3);
		action.doAction();
		FlexoProcess subProcess3 = action.getNewProcess();
		logger.info("SubProcess " + subProcess3.getName() + " successfully created");

		DropWKFElement dropSubProcessNode = DropWKFElement.actionType.makeNewAction(rootProcess.getActivityPetriGraph(), null, EDITOR);
		dropSubProcessNode.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		dropSubProcessNode.setParameter(DropWKFElement.SUB_PROCESS, subProcess3);
		dropSubProcessNode.setLocation(300, 150);
		dropSubProcessNode.doAction();
		assertTrue(dropSubProcessNode.hasActionExecutionSucceeded());
		SubProcessNode subProcessNode2 = (SubProcessNode) dropSubProcessNode.getObject();
		logger.info("SubProcessNode " + subProcessNode2.getName() + " successfully created");

		ComponentDefinition component1 = _checkoutedProject.getFlexoComponentLibrary().getComponentNamed(OPERATION_COMPONENT_1);
		// Insert a new bloc at index 3, name it Bloc1
		DropIEElement dropBloc4 = DropIEElement.createBlocInComponent(component1.getWOComponent(), 3, _checkoutedProjectEditor);
		assertTrue(dropBloc4.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc4 = (IEBlocWidget) dropBloc4.getDroppedWidget();
		assertNotNull(bloc4);
		bloc4.setTitle("Bloc4");

		saveProject(_checkoutedProject);

		File testFile2 = new File(_checkoutedProject.getProjectDirectory(), "removeThisFileLater2");
		assertTrue(testFile2.exists());
		testFile2.delete();

		// Refresh

		RefreshProject refreshProject = RefreshProject.actionType.makeNewAction(_project2, null, EDITOR);
		assertTrue(refreshProject.doAction().hasActionExecutionSucceeded());

		// Commit those modifs

		CommitFiles commitAction = CommitFiles.actionType.makeNewAction(_project2, null, EDITOR);
		commitAction.setCommitMessage("Test commit 2");
		assertTrue(commitAction.doAction().hasActionExecutionSucceeded());

		assertAllFilesAreUpToDateOrCVSIgnored(_project2);

		// Some modifs on initial project

		FlexoProcess rootProcess2 = _initialProject.getFlexoWorkflow().getRootFlexoProcess();
		AbstractActivityNode node2 = rootProcess2.getAbstractActivityNodeNamed(TEST_SUB_PROCESS);
		// node2.setLocation(new Point(node2.getLocation().x+23,node2.getLocation().y+67));
		node2.setX(node.getX("bpe") + 23, "bpe");
		node2.setY(node.getY("bpe") + 67, "bpe");

		AddSubProcess addSubProcess = AddSubProcess.actionType.makeNewAction(rootProcess2, null, EDITOR);
		addSubProcess.setParentProcess(rootProcess2);
		addSubProcess.setNewProcessName(TEST_SUB_PROCESS_4);
		addSubProcess.doAction();
		FlexoProcess subProcess4 = addSubProcess.getNewProcess();
		logger.info("SubProcess " + subProcess4.getName() + " successfully created");

		DropWKFElement dropSubProcessNode4 = DropWKFElement.actionType.makeNewAction(rootProcess2.getActivityPetriGraph(), null, EDITOR);
		dropSubProcessNode4.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		dropSubProcessNode4.setParameter(DropWKFElement.SUB_PROCESS, subProcess4);
		dropSubProcessNode4.setLocation(300, 150);
		dropSubProcessNode4.doAction();
		assertTrue(dropSubProcessNode4.hasActionExecutionSucceeded());
		SubProcessNode subProcessNode4 = (SubProcessNode) dropSubProcessNode4.getObject();
		logger.info("SubProcessNode " + subProcessNode4.getName() + " successfully created");

		ComponentDefinition component2 = _initialProject.getFlexoComponentLibrary().getComponentNamed(OPERATION_COMPONENT_2);
		// Insert a new bloc at index 0, name it Bloc5
		DropIEElement dropBloc5 = DropIEElement.createBlocInComponent(component2.getWOComponent(), 0, _initialProjectEditor);
		assertTrue(dropBloc5.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc5 = (IEBlocWidget) dropBloc5.getDroppedWidget();
		assertNotNull(bloc5);
		bloc5.setTitle("Bloc5");

		saveProject(_initialProject);

		File testFile3 = new File(_initialProject.getProjectDirectory(), "removeThisFileLater3");
		assertTrue(testFile3.exists());
		testFile3.delete();

		SynchronizeWithRepository syncAction = SynchronizeWithRepository.actionType.makeNewAction(_project1, null, EDITOR);
		assertTrue(syncAction.doAction().hasActionExecutionSucceeded());

		assertAllFilesAreUpToDateOrCVSIgnoredExcept(_project1, _project1.getCVSFile(_projectIdentifier + ".rmxml"),
				_project1.getCVSFile(_projectIdentifier + ".wolib"),
				_project1.getCVSFile("Components" + File.separator + OPERATION_COMPONENT_1 + ".woxml"),
				_project1.getCVSFile("Components" + File.separator + OPERATION_COMPONENT_2 + ".woxml"),
				_project1.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm"),
				_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf"),
				_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml"),
				_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_3 + ".xml"),
				_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_4 + ".xml"),
				_project1.getCVSFile("removeThisFileLater3"), _project1.getCVSFile("removeThisFileLater2"));

		assertEquals(_project1.getCVSFile(_projectIdentifier + ".rmxml").getStatus(), CVSStatus.Conflicting);
		assertEquals(_project1.getCVSFile(_projectIdentifier + ".wolib").getStatus(), CVSStatus.UpToDate);
		assertEquals(_project1.getCVSFile("Components" + File.separator + OPERATION_COMPONENT_1 + ".woxml").getStatus(),
				CVSStatus.RemotelyModified);
		assertEquals(_project1.getCVSFile("Components" + File.separator + OPERATION_COMPONENT_2 + ".woxml").getStatus(),
				CVSStatus.LocallyModified);
		assertEquals(_project1.getCVSFile("DataModel" + File.separator + _projectIdentifier + ".dm").getStatus(), CVSStatus.Conflicting);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".wkf").getStatus(), CVSStatus.Conflicting);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + _projectIdentifier + ".xml").getStatus(), CVSStatus.Conflicting);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_3 + ".xml").getStatus(), CVSStatus.RemotelyAdded);
		assertEquals(_project1.getCVSFile("Workflow" + File.separator + TEST_SUB_PROCESS_4 + ".xml").getStatus(), CVSStatus.LocallyAdded);
		assertEquals(_project1.getCVSFile("removeThisFileLater3").getStatus(), CVSStatus.LocallyRemoved);
		assertEquals(_project1.getCVSFile("removeThisFileLater2").getStatus(), CVSStatus.RemotelyRemoved);

	}

	/**
	 * Perform required merges
	 */
	public void test10PerformMerges() {
		log("test10PerformMerges");

		CVSFile woLib = _project1.getCVSFile(_projectIdentifier + ".wolib");
		assertTrue(woLib.getMerge().isResolved());
		MarkAsMergedFiles mergeWOLib = MarkAsMergedFiles.actionType.makeNewAction(woLib, null, EDITOR);
		assertTrue(mergeWOLib.doAction().hasActionExecutionSucceeded());
		assertEquals(woLib.getStatus(), CVSStatus.UpToDate);
		_checkoutedProject.close();
	}

}
