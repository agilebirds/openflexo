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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.DropPartialComponent;
import org.openflexo.foundation.ie.action.MakePartialComponent;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.TopComponentReusableWidget;
import org.openflexo.foundation.rm.FlexoResourceManager.BackwardSynchronizationHook;
import org.openflexo.foundation.rm.FlexoXMLStorageResource.SaveXMLResourceException;
import org.openflexo.foundation.utils.ProjectInitializerException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.ActionPetriGraph;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddStatus;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public class TestRM extends FlexoTestCase {

	public TestRM() {
		super(TEST_RM);
	}

	public static final Logger logger = Logger.getLogger(TestRM.class.getPackage().getName());

	private static final String TEST_RM = "TestRM";

	private static final String TEST_SUB_PROCESS = "TestSubProcess";
	private static final String TEST_OPERATION_NODE = "TestOperation";
	private static final String TEST_OPERATION_COMPONENT = "TestOperation";
	private static final String TEST_OPERATION_COMPONENT2 = "TestOperation2";
	private static final String TEST_PARTIAL_COMPONENT = "TestPartialComponent";
	private static final String TEST_STATUS = "TestANewStatus";

	private static FlexoEditor _editor;
	private static FlexoProject _project;
	private static File _projectDirectory;
	private static String _projectIdentifier;
	private static DebugBackwardSynchronizationHook _bsHook;

	private static FlexoRMResource _rmResource;
	private static FlexoWorkflowResource _wkfResource;
	private static FlexoDMResource _dmResource;
	private static FlexoDKVResource _dkvResource;
	private static FlexoComponentLibraryResource _clResource;
	private static FlexoNavigationMenuResource _menuResource;
	private static FlexoProcessResource _rootProcessResource;
	private static FlexoProcessResource _subProcessResource;
	private static FlexoEOModelResource _eoPrototypesResource;
	private static FlexoEOModelResource _executionModelResource;
	private static SubProcessNode _subProcessNode;
	private static OperationNode _operationNode;
	private static FlexoOperationComponentResource _testOperationComponentResource;
	private static IEOperationComponent _testOperationComponent;
	private static FlexoOperationComponentResource _testOperationComponentResource2;
	private static IEOperationComponent _testOperationComponent2;
	private static IEBlocWidget _bloc2;
	private static FlexoReusableComponentResource _partialComponentResource;

	/**
	 * Overrides setUp
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		log("test0CreateProject");
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		try {
			File tempFile = File.createTempFile(TEST_RM, "");
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
		_bsHook = new DebugBackwardSynchronizationHook();
		FlexoResourceManager.setBackwardSynchronizationHook(_bsHook);
	}

	/**
	 * Check that resources ans dependancies were correctely built
	 */
	public void test1CheckResources() {
		log("test1CheckResources");
		assertNotNull(_rmResource = _project.getFlexoRMResource());
		assertNotNull(_wkfResource = _project.getFlexoWorkflowResource());
		assertNotNull(_dmResource = _project.getFlexoDMResource());
		assertNotNull(_dkvResource = _project.getFlexoDKVResource());
		assertNotNull(_clResource = _project.getFlexoComponentLibraryResource());
		assertNotNull(_menuResource = _project.getFlexoNavigationMenuResource());
		assertNotNull(_rootProcessResource = _project.getFlexoProcessResource(_projectIdentifier));
		assertNotNull(_executionModelResource = _project.getEOModelResource(FlexoExecutionModelRepository.EXECUTION_MODEL_DIR.getName()));
		assertNotNull(_eoPrototypesResource = _project.getEOModelResource(EOPrototypeRepository.EOPROTOTYPE_REPOSITORY_DIR.getName()));

		for (FlexoResource<? extends FlexoResourceData> resource : _project) {
			if (resource != _rmResource && !(resource instanceof FlexoMemoryResource)) {
				assertSynchonized(resource, _rmResource);
			}
		}
		assertSynchonized(_dmResource, _executionModelResource);
		assertSynchonized(_dmResource, _eoPrototypesResource);

		assertSynchonized(_wkfResource, _rootProcessResource);

		assertDepends(_rootProcessResource, _dmResource);
		assertNotDepends(_rootProcessResource, _clResource);

		logger.info("Resources are WELL created and DEPENDANCIES checked");

		for (FlexoResource resource : _project.getResources().values()) {
			if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource) resource);
			}
		}

		logger.info("Resources are marked as NOT MODIFIED");

	}

	/**
	 * Add a new sub-process and check resource dependancies
	 */
	public void test2CreateSubProcessAndCheckResources() {
		log("test2CreateSubProcessAndCheckResources");
		AddSubProcess action = AddSubProcess.actionType.makeNewAction(_rootProcessResource.getFlexoProcess(), null, _editor);
		action.setParentProcess(_rootProcessResource.getFlexoProcess());
		action.setNewProcessName(TEST_SUB_PROCESS);
		action.doAction();
		logger.info("SubProcess " + action.getNewProcess().getName() + " successfully created");
		_subProcessResource = _project.getFlexoProcessResource(TEST_SUB_PROCESS);
		assertNotNull(_subProcessResource);
		assertSynchonized(_subProcessResource, _rmResource);
		assertSynchonized(_subProcessResource, _wkfResource);
		assertDepends(_subProcessResource, _dmResource);
		assertNotDepends(_subProcessResource, _clResource);
		for (FlexoResource<? extends FlexoResourceData> resource : _project) {
			if (resource == _rmResource) {
				assertModified(_rmResource);
			} else if (resource == _dmResource) {
				assertModified(_dmResource);
			} else if (resource == _wkfResource) {
				assertModified(_wkfResource);
			} else if (resource == _rootProcessResource) {
				assertModified(_rootProcessResource);
			} else if (resource == _subProcessResource) {
				assertModified(_subProcessResource);
			} else if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource) resource);
			}
		}
	}

	/**
	 * Add a new sub-process node, check dependancies
	 */
	public void test3CreateSubProcessNodeAndCheckResources() {
		log("test3CreateSubProcessNodeAndCheckResources");
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(_rootProcessResource.getFlexoProcess().getActivityPetriGraph(),
				null, _editor);
		action.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		action.setParameter(DropWKFElement.SUB_PROCESS, _subProcessResource.getFlexoProcess());
		action.setLocation(100, 100);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		_subProcessNode = (SubProcessNode) action.getObject();
		logger.info("SubProcessNode " + _subProcessNode.getName() + " successfully created");
		assertDepends(_rootProcessResource, _subProcessResource);
		saveProject();
	}

	/**
	 * Open operation level, drop a new operation, and associate it a new operation component
	 */
	public void test4CreateOperationAndCheckResources() {
		log("test4CreateOperationAndCheckResources");
		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(_subProcessNode, null, _editor);
		openOperationLevel.doAction();
		DropWKFElement dropOperation = DropWKFElement.actionType.makeNewAction(_subProcessNode.getOperationPetriGraph(), null, _editor);
		dropOperation.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation.setLocation(100, 100);
		dropOperation.doAction();
		assertTrue(dropOperation.hasActionExecutionSucceeded());
		_operationNode = (OperationNode) dropOperation.getObject();
		_operationNode.setName(TEST_OPERATION_NODE);
		logger.info("OperationNode " + _operationNode.getName() + " successfully created");
		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(_operationNode, null,
				_editor);
		setOperationComponent.setNewComponentName(TEST_OPERATION_COMPONENT);
		setOperationComponent.doAction();
		assertTrue(setOperationComponent.hasActionExecutionSucceeded());
		_testOperationComponentResource = _project.getFlexoOperationComponentResource(TEST_OPERATION_COMPONENT);
		assertNotNull(_testOperationComponentResource);
		assertSynchonized(_testOperationComponentResource, _rmResource);
		assertSynchonized(_testOperationComponentResource, _clResource);
		assertDepends(_testOperationComponentResource, _dmResource);
		assertDepends(_rootProcessResource, _testOperationComponentResource);
		saveProject();
	}

	/**
	 * Edit this new component by adding 3 blocks
	 */
	public void test5HandlesComponentsAndCheckResources() {
		log("test5HandlesComponentsAndCheckResources");
		_testOperationComponent = _testOperationComponentResource.getIEOperationComponent();
		assertNotNull(_testOperationComponent);

		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_testOperationComponent, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_testOperationComponent, 1, _editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		_bloc2 = (IEBlocWidget) dropBloc2.getDroppedWidget();
		assertNotNull(_bloc2);
		_bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_testOperationComponent, 1, _editor);
		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc3 = (IEBlocWidget) dropBloc3.getDroppedWidget();
		assertNotNull(bloc3);
		bloc3.setTitle("Bloc3");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(_bloc2, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);

		// Drop a label in the table, at cell (0,0) at position 0
		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table, WidgetType.LABEL, 0, 0, 0, _editor);
		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());

		// Drop a TextField in the table, at cell (0,1) at position 0
		DropIEElement dropTF = DropIEElement.insertWidgetInTable(table, WidgetType.TEXTFIELD, 0, 1, 0, _editor);
		assertTrue(dropTF.doAction().hasActionExecutionSucceeded());

		assertModified(_testOperationComponentResource);

		// Save project
		saveProject();
	}

	/**
	 * Creates a new operation component
	 */
	public void test6CreateOperationComponent2() {
		log("test6CreateOperationComponent2");
		AddComponent addComponent = AddComponent.actionType.makeNewAction(_project.getFlexoComponentLibrary(), null, _editor);
		addComponent.setNewComponentName(TEST_OPERATION_COMPONENT2);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_testOperationComponentResource2 = _project.getFlexoOperationComponentResource(TEST_OPERATION_COMPONENT2);
		assertNotNull(_testOperationComponentResource2);
		assertSynchonized(_testOperationComponentResource2, _rmResource);
		assertSynchonized(_testOperationComponentResource2, _clResource);
		assertDepends(_testOperationComponentResource2, _dmResource);

		_testOperationComponent2 = _testOperationComponentResource2.getIEOperationComponent();
		assertNotNull(_testOperationComponent2);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_testOperationComponent2, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("NewBloc");

		saveProject();
	}

	/**
	 * Sets bloc2 in operation component 1 to be partial, Check resource dependancies
	 */
	public void test7MakePartialComponent() {
		log("test7MakePartialComponent");
		_bloc2.setTitle("Reusable");
		MakePartialComponent makePartial = MakePartialComponent.actionType.makeNewAction(_bloc2, null, _editor);
		makePartial.setNewComponentName(TEST_PARTIAL_COMPONENT);
		assertTrue(makePartial.doAction().hasActionExecutionSucceeded());

		_partialComponentResource = _project.getFlexoReusableComponentResource(TEST_PARTIAL_COMPONENT);
		assertNotNull(_partialComponentResource);
		assertSynchonized(_partialComponentResource, _rmResource);
		assertSynchonized(_partialComponentResource, _clResource);
		assertDepends(_partialComponentResource, _dmResource);
		assertDepends(_testOperationComponentResource, _partialComponentResource);
		saveProject();
	}

	/**
	 * Use partial component in operation component 2 Check resource dependancies
	 */
	public void test8UsePartialComponentInOperation2() {
		log("test8UsePartialComponentInOperation2");
		DropPartialComponent dropPartialComponent = DropPartialComponent.actionType.makeNewAction(_testOperationComponent2, null, _editor);
		ReusableComponentDefinition partialComponent = _partialComponentResource.getComponentDefinition();
		dropPartialComponent.setPartialComponent(partialComponent);
		assertTrue(dropPartialComponent.doAction().hasActionExecutionSucceeded());
		assertDepends(_testOperationComponentResource, _partialComponentResource);
		assertDepends(_testOperationComponentResource2, _partialComponentResource);
		saveProject();
	}

	/**
	 * Reload project, check dependancies Assert all resources are not modified
	 */
	public void test9ReloadProjectAndCheckDependancies() {
		log("test9ReloadProjectAndCheckDependancies");
		reloadProject(true);
		for (FlexoStorageResource<? extends StorageResourceData> resource : _project.getStorageResources()) {
			logger.info("Resource" + resource + " lastModifiedOn: "
					+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(resource.getDiskLastModifiedDate()));
		}
		// Save RM for eventual back-synchro to be saved
		saveProject();
		log("Done. Now check that no other back-synchro");
		// Let eventual dependancies back-synchronize together
		reloadProject(true); // This time, all must be not modified
		for (FlexoStorageResource<? extends StorageResourceData> resource : _project.getStorageResources()) {
			assertNotModified(resource);
		}

	}

	/**
	 * Reload project again, check that there is no back synchronization to be performed
	 */
	public void test10ReloadProjectAgain() {
		log("test10ReloadProjectAgain");
		reloadProject(true);
		_bsHook.assertNoBackSynchronization();
	}

	/**
	 * Reload project again, but not the components yet, only load processes Then load components
	 */
	public void test11ReloadRootProcessOnly() {
		log("test11ReloadRootProcessOnly");
		reloadProject(false);
		_bsHook.assertNoBackSynchronization();
		assertNotLoaded(_testOperationComponentResource);
		assertNotLoaded(_testOperationComponentResource2);
		assertNotLoaded(_partialComponentResource);

		for (FlexoResource dr : _testOperationComponentResource.getDependantResources()) {
			logger.info("depends of " + dr + " dr.isLoaded()=" + ((FlexoStorageResource) dr).isLoaded());
		}

		// WE now load OperationComponent
		_testOperationComponent = _testOperationComponentResource.getIEOperationComponent();
		assertLoaded(_testOperationComponentResource);
		assertNotLoaded(_testOperationComponentResource2);
		assertNotLoaded(_partialComponentResource);

		// Now, we want to access to ReusableComponentInstance
		// This should automatically load reusable
		TopComponentReusableWidget reusable = (TopComponentReusableWidget) _testOperationComponent.getRootSequence().get(2);
		reusable.getReusableComponentInstance().getWOComponent();
		assertLoaded(_testOperationComponentResource);
		assertNotLoaded(_testOperationComponentResource2);
		assertLoaded(_partialComponentResource);

		// Access to OperationComponent2
		_testOperationComponent2 = _testOperationComponentResource2.getIEOperationComponent();
		assertLoaded(_testOperationComponentResource);
		assertLoaded(_testOperationComponentResource2);
		assertLoaded(_partialComponentResource);
	}

	/**
	 * Touch partial component Check that back-synchro are called and performed
	 */
	public void test12TestBackSynchro() {
		log("test12TestBackSynchro");

		reloadProject(false);

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		// We simulate here a 'touch' on the partial component
		try {
			_partialComponentResource.loadResourceData();
			// To simulate a touch on a data, we must trigger a setChanged(), otherwise the lastKnownMemoryUpdate will not be updated!
			_partialComponentResource.getResourceData().setChanged();
			_partialComponentResource.saveResourceData();
			// And also, save the project so that the lastKnownMemoryUpdate of the partial component is properly serialized
			_project.getFlexoResource().saveResourceData();
		} catch (SaveXMLResourceException e) {
			e.printStackTrace();
			fail();
		} catch (SaveResourcePermissionDeniedException e) {
			e.printStackTrace();
			fail();
		} catch (FlexoException e) {
			e.printStackTrace();
			fail();
		}

		// And we reload the project
		reloadProject(false);

		// testOperationComponent is used in an OperationNode while testOperationComponent2 is not. Since we know that all processes are
		// loaded at startup, the RM mechanism should see that the operation component is not up-to-date compared with the partial component
		// and should therefore force the loading of the component so that it gets back-synched with its partial component (and so it gets
		// up-to-date). This is why we assert that it is loaded.
		assertLoaded(_testOperationComponentResource);
		// testOperationComponent2 is not used by any process and should therefore not be loaded!
		assertNotLoaded(_testOperationComponentResource2);
		assertLoaded(_partialComponentResource);

		_bsHook.assertBackSynchronizationCount(2);
		_bsHook.assertBackSynchronizationHasBeenPerformed(_testOperationComponentResource, _partialComponentResource);
		_bsHook.assertBackSynchronizationHasBeenPerformed(_rootProcessResource, _testOperationComponentResource);
		saveProject();

	}

	/**
	 * Load project again Check that there is no more back-synchronization to be performed
	 */
	public void test13ReloadRootProcessNoBackSynchro() {
		log("test13ReloadRootProcessNoBackSynchro");
		reloadProject(false);
		_bsHook.assertNoBackSynchronization();
		assertNotLoaded(_testOperationComponentResource);
		assertNotLoaded(_testOperationComponentResource2);
		assertNotLoaded(_partialComponentResource);
	}

	/**
	 * Load project again Check that validation passes and does not throw exceptions
	 */
	public void test14ValidateProject() {
		log("test14ValidateProject");
		reloadProject(true);
		ValidationReport dmReport = _project.getDataModel().validate();
		assertEquals(0, dmReport.getErrorNb());
		ValidationReport ieReport = _project.getFlexoComponentLibrary().validate();
		System.out.println(ieReport.errorAsString());
		assertEquals(2, ieReport.getErrorNb());
		ValidationReport dkvReport = _project.getDKVModel().validate();
		assertEquals(0, dkvReport.getErrorNb());
		ValidationReport wkfReport = _project.getFlexoWorkflow().validate();
		assertEquals(1, wkfReport.getErrorNb());
	}

	/**
	 * Load project again Check that repair action works and does not throws any exceptions
	 */
	public void test15RepairProject() {
		log("test15RepairProject");
		reloadProject(true);
		ValidationReport report = _project.validate();
		if (report.getErrorNb() > 0) {
			System.err.println(report.reportAsString());
		}
		assertEquals(0, report.getErrorNb());
	}

	public void test16Serialization() {
		AddStatus addStatus = AddStatus.actionType.makeNewAction(_rootProcessResource.getFlexoProcess(), null, _editor);
		addStatus.setNewStatusName(TEST_STATUS);
		addStatus.doAction();
		assertTrue(addStatus.hasActionExecutionSucceeded());
		_subProcessNode.setNewStatus(addStatus.getNewStatus());
		_operationNode.setNewStatus(addStatus.getNewStatus());
		new ActionPetriGraph(_operationNode);
		_operationNode.getAllActionNodes().firstElement().setNewStatus(addStatus.getNewStatus());
		saveProject();
		reloadProject(true);
		Status status = null;
		Iterator<Status> i = _project.getGlobalStatus().values().iterator();
		while (status == null && i.hasNext()) {
			Status s = i.next();
			if (TEST_STATUS.equals(s.getName())) {
				status = s;
			}
		}
		assertNotNull(status);
		assertEquals(status, _subProcessNode.getNewStatus());
		assertEquals(status, _operationNode.getNewStatus());
		// Although the next line may seem a little weak, it tests that deserialization process does not modify order!
		assertEquals(status, _operationNode.getAllActionNodes().firstElement().getNewStatus());
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		resetVariables();
		_bsHook = null;
		_editor = null;
		_projectDirectory = null;
		_projectIdentifier = null;
	}

	private void resetVariables() {
		_project = null;
		_bloc2 = null;
		_clResource = null;
		_dkvResource = null;
		_dmResource = null;
		_eoPrototypesResource = null;
		_executionModelResource = null;
		_menuResource = null;
		_operationNode = null;
		_partialComponentResource = null;
		_rmResource = null;
		_rootProcessResource = null;
		_subProcessNode = null;
		_subProcessResource = null;
		_testOperationComponentResource = null;
		_testOperationComponent = null;
		_testOperationComponentResource2 = null;
		_testOperationComponent2 = null;
		_wkfResource = null;
	}

	private void reloadProject(boolean fullLoading) {
		_bsHook.clear();
		if (_project != null) {
			_project.close();
		}
		resetVariables();

		try {
			assertNotNull(_editor = FlexoResourceManager.initializeExistingProject(_projectDirectory, EDITOR_FACTORY, null));
			_project = _editor.getProject();
		} catch (ProjectInitializerException e) {
			e.printStackTrace();
			fail();
		} catch (ProjectLoadingCancelledException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(_rmResource = _project.getFlexoRMResource());
		assertNotNull(_wkfResource = _project.getFlexoWorkflowResource());
		assertNotNull(_dmResource = _project.getFlexoDMResource());
		assertNotNull(_dkvResource = _project.getFlexoDKVResource());
		assertNotNull(_clResource = _project.getFlexoComponentLibraryResource());
		assertNotNull(_menuResource = _project.getFlexoNavigationMenuResource());
		assertNotNull(_rootProcessResource = _project.getFlexoProcessResource(_projectIdentifier));
		assertNotNull(_executionModelResource = _project.getEOModelResource(FlexoExecutionModelRepository.EXECUTION_MODEL_DIR.getName()));
		assertNotNull(_eoPrototypesResource = _project.getEOModelResource(EOPrototypeRepository.EOPROTOTYPE_REPOSITORY_DIR.getName()));
		assertNotNull(_subProcessResource = _project.getFlexoProcessResource(TEST_SUB_PROCESS));
		if (fullLoading) {
			assertNotNull(_subProcessNode = _rootProcessResource.getFlexoProcess().getActivityPetriGraph()
					.getSubProcessNodeNamed(TEST_SUB_PROCESS));
		}
		if (fullLoading) {
			assertNotNull(_operationNode = _rootProcessResource.getFlexoProcess().getActivityPetriGraph()
					.getOperationNodeNamed(TEST_OPERATION_NODE));
		}
		assertNotNull(_testOperationComponentResource = _project.getFlexoOperationComponentResource(TEST_OPERATION_COMPONENT));
		assertNotNull(_testOperationComponentResource2 = _project.getFlexoOperationComponentResource(TEST_OPERATION_COMPONENT2));
		if (fullLoading) {
			assertNotNull(_testOperationComponent = _testOperationComponentResource.getIEOperationComponent());
		}
		if (fullLoading) {
			assertNotNull(_testOperationComponent2 = _testOperationComponentResource2.getIEOperationComponent());
		}
		assertNotNull(_partialComponentResource = _project.getFlexoReusableComponentResource(TEST_PARTIAL_COMPONENT));

		assertSynchonized(_dmResource, _executionModelResource);
		assertSynchonized(_dmResource, _eoPrototypesResource);
		assertSynchonized(_wkfResource, _rootProcessResource);
		assertDepends(_rootProcessResource, _dmResource);
		assertNotDepends(_rootProcessResource, _clResource);

		assertSynchonized(_subProcessResource, _rmResource);
		assertSynchonized(_subProcessResource, _wkfResource);
		assertDepends(_subProcessResource, _dmResource);
		assertNotDepends(_subProcessResource, _clResource);

		assertSynchonized(_testOperationComponentResource, _rmResource);
		assertSynchonized(_testOperationComponentResource, _clResource);
		assertDepends(_testOperationComponentResource, _dmResource);

		assertDepends(_rootProcessResource, _testOperationComponentResource);

		assertSynchonized(_testOperationComponentResource2, _rmResource);
		assertSynchonized(_testOperationComponentResource2, _clResource);
		assertDepends(_testOperationComponentResource2, _dmResource);

		assertSynchonized(_partialComponentResource, _rmResource);
		assertSynchonized(_partialComponentResource, _clResource);
		assertDepends(_partialComponentResource, _dmResource);

		assertDepends(_testOperationComponentResource, _partialComponentResource);
		assertDepends(_testOperationComponentResource2, _partialComponentResource);

		if (_bsHook.getBackSynchronizationCount() == 0) {
			assertStorageResourcesAreNotModified();
		}

	}

	private void assertStorageResourcesAreNotModified() {
		for (FlexoStorageResource<? extends StorageResourceData> resource : _project.getStorageResources()) {
			assertNotModified(resource);
		}
	}

	private void saveProject() {
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
		assertStorageResourcesAreNotModified();
	}

	private class DebugBackwardSynchronizationHook implements BackwardSynchronizationHook {
		private class BackSynchroEntry {
			protected FlexoResource resource1;
			protected FlexoResource resource2;

			protected BackSynchroEntry(FlexoResource aResource1, FlexoResource aResource2) {
				resource1 = aResource1;
				resource2 = aResource2;
			}

			protected boolean backSynchroConcerns(FlexoResource aResource1, FlexoResource aResource2) {
				return resource1 == aResource1 && resource2 == aResource2;
			}
		}

		private Vector<BackSynchroEntry> entries;

		protected DebugBackwardSynchronizationHook() {
			entries = new Vector<BackSynchroEntry>();
			clear();
		}

		protected void clear() {
			entries.clear();
		}

		@Override
		public void notifyBackwardSynchronization(FlexoResource resource1, FlexoResource resource2) {
			System.out.println("Resource " + resource1 + " is to be back-synchronized with " + resource2);
			entries.add(new BackSynchroEntry(resource1, resource2));
		}

		protected void assertBackSynchronizationHasBeenPerformed(FlexoResource aResource1, FlexoResource aResource2) {
			for (BackSynchroEntry entry : entries) {
				if (entry.backSynchroConcerns(aResource1, aResource2)) {
					return;
				}
			}
			fail("RESOURCE synchonization problem: " + aResource1 + " MUST have been back-synchronized with " + aResource2);
		}

		protected void assertNoBackSynchronization() {
			assertBackSynchronizationCount(0);
		}

		protected void assertBackSynchronizationCount(int count) {
			assertEquals(getBackSynchronizationCount(), count);
		}

		protected int getBackSynchronizationCount() {
			return entries.size();
		}
	}

}
