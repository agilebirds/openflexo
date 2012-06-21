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
package org.openflexo.generator;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponent.ComponentType;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.rm.FlexoMemoryResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoResourceManager.BackwardSynchronizationHook;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.rm.OperationComponentJavaFileResource;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.ToolBox;

public class TestCGRepositoryDeletion extends CGTestCase {

	public TestCGRepositoryDeletion() {
		super(TEST_CG);
	}

	public TestCGRepositoryDeletion(String testName) {
		super(testName);
	}

	protected static final Logger logger = Logger.getLogger(TestCG.class.getPackage().getName());

	private static final String TEST_CG = "TestCGRepositoryDeletion";
	private static DebugBackwardSynchronizationHook _bsHook;

	public static Test suite() {
		final TestSuite suite = new TestSuite("TestSuite for TestCGRepositoryDeletion");
		suite.addTest(new TestCGRepositoryDeletion("test0CreateProject"));
		suite.addTest(new TestCGRepositoryDeletion("test1CheckResources"));
		suite.addTest(new TestCGRepositoryDeletion("test2CreateSubProcessAndCheckResources"));
		suite.addTest(new TestCGRepositoryDeletion("test3CreateSubProcessNodeAndCheckResources"));
		suite.addTest(new TestCGRepositoryDeletion("test4CreateOperationAndCheckResources"));
		suite.addTest(new TestCGRepositoryDeletion("test5EditOperationComponent1"));
		suite.addTest(new TestCGRepositoryDeletion("test6CreateOperationComponent2"));
		suite.addTest(new TestCGRepositoryDeletion("test7CreateOperationComponent3"));
		suite.addTest(new TestCGRepositoryDeletion("test8CreateSubProcessNodeAndCheckResources"));
		suite.addTest(new TestCGRepositoryDeletion("test9InitializeCodeGeneration"));
		suite.addTest(new TestCGRepositoryDeletion("test10ValidateProject"));
		suite.addTest(new TestCGRepositoryDeletion("test11SynchronizeCodeGeneration"));
		suite.addTest(new TestCGRepositoryDeletion("test12CheckAllGeneratedResourcesDependancies"));
		suite.addTest(new TestCGRepositoryDeletion("test13CheckGeneratedResourcesStatus"));
		suite.addTest(new TestCGRepositoryDeletion("test14Delete"));
		return suite;
	}

	/*
	public static Test suite() {
	     return new OrderedTestSuite(TestCG.class, new String[]{
	    	 "test0CreateProject", 
	    	 "test1CheckResources",
	    	 "test2CreateSubProcessAndCheckResources",
	    	 "test3CreateSubProcessNodeAndCheckResources",
	    	 "test4CreateOperationAndCheckResources",
	    	 "test5EditOperationComponent1",
	    	 "test6CreateOperationComponent2",
	    	 "test7CreateOperationComponent3",
	    	 "test8CreateSubProcessNodeAndCheckResources",
	    	 "test9InitializeCodeGeneration",
	    	 "test10ValidateProject",
	    	 "test11SynchronizeCodeGeneration",
	    	 "test12CheckAllGeneratedResourcesDependancies",
	    	 "test13CheckGeneratedResourcesStatus",
	    	 "test14CheckOptimisticDependancyCheckingWithDataModel",
	    	 "test15CheckOptimisticDependancyCheckingWithProcesses",
	    	 "test16CheckOptimisticDependancyCheckingWithComponents",
	    	 "test17CheckResourceDynamicDependancyRebuilding",
	    	 "test18CheckTemplateDependancies"});
	 }
	 */
	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		log("test0CreateProject");
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		try {
			File tempFile = File.createTempFile(TEST_CG, "");
			_projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: " + _projectDirectory.getAbsolutePath());
		_projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length() - 4);
		logger.info("Project identifier: " + _projectIdentifier);
		_editor = (DefaultFlexoEditor) FlexoResourceManager.initializeNewProject(_projectDirectory, EDITOR_FACTORY, null);
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
		defineStatusColumn(_rootProcessResource.getFlexoProcess());
		defineStatusColumn(action.getNewProcess());
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

	public void test3CreateSubProcessNodeAndCheckResources() {
		log("test3CreateSubProcessNodeAndCheckResources");
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(_rootProcessResource.getFlexoProcess().getActivityPetriGraph(),
				null, _editor);
		action.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		action.setParameter(DropWKFElement.SUB_PROCESS, _subProcessResource.getFlexoProcess());
		action.setLocation(100, 100);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		defineStatusColumn(action.getProcess());
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
		_operationNode.setName(TEST_OPERATION_NODE_1);
		logger.info("OperationNode " + _operationNode.getName() + " successfully created");
		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(_operationNode, null,
				_editor);
		setOperationComponent.setNewComponentName(OPERATION_COMPONENT_1);
		setOperationComponent.doAction();
		assertTrue(setOperationComponent.hasActionExecutionSucceeded());
		_operationComponentResource1 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_1);
		assertNotNull(_operationComponentResource1);
		assertSynchonized(_operationComponentResource1, _rmResource);
		assertSynchonized(_operationComponentResource1, _clResource);
		assertDepends(_operationComponentResource1, _dmResource);
		assertDepends(_rootProcessResource, _operationComponentResource1);
		saveProject();
	}

	/**
	 * Edit this new component by adding 3 blocks
	 */
	public void test5EditOperationComponent1() {
		log("test5EditOperationComponent1");
		_operationComponent1 = _operationComponentResource1.getIEOperationComponent();
		assertNotNull(_operationComponent1);

		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_operationComponent1, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_operationComponent1, 1, _editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		_bloc2 = (IEBlocWidget) dropBloc2.getDroppedWidget();
		assertNotNull(_bloc2);
		_bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_operationComponent1, 1, _editor);
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

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent1, 3, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab) dropTabs.getDroppedWidget();

		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();

		// Put Tab1 inside
		AddTab addTab1 = AddTab.actionType.makeNewAction(tabs, null, _editor);
		addTab1.setFolder(rootFolder);
		addTab1.setTabTitle("Tab1Title");
		addTab1.setTabName(TAB_COMPONENT1);
		addTab1.setTabContainer(tabs);
		assertTrue(addTab1.doAction().hasActionExecutionSucceeded());
		_tab1 = addTab1.getTabDef();

		// Put Tab2 inside
		AddTab addTab2 = AddTab.actionType.makeNewAction(tabs, null, _editor);
		addTab2.setFolder(rootFolder);
		addTab2.setTabTitle("Tab2Title");
		addTab2.setTabName(TAB_COMPONENT2);
		addTab2.setTabContainer(tabs);
		assertTrue(addTab2.doAction().hasActionExecutionSucceeded());
		_tab2 = addTab2.getTabDef();

		assertModified(_operationComponentResource1);

		// Save project
		saveProject();
	}

	/**
	 * Creates a new operation component 2
	 */
	public void test6CreateOperationComponent2() {
		log("test6CreateOperationComponent2");
		AddComponent addComponent = AddComponent.actionType.makeNewAction(_project.getFlexoComponentLibrary(), null, _editor);
		addComponent.setNewComponentName(OPERATION_COMPONENT_2);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_operationComponentResource2 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_2);
		assertNotNull(_operationComponentResource2);
		assertSynchonized(_operationComponentResource2, _rmResource);
		assertSynchonized(_operationComponentResource2, _clResource);
		assertDepends(_operationComponentResource2, _dmResource);

		_operationComponent2 = _operationComponentResource2.getIEOperationComponent();
		assertNotNull(_operationComponent2);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_operationComponent2, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent2, 1, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab) dropTabs.getDroppedWidget();

		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();

		// Put Tab1 inside
		AddTab addTab1 = AddTab.actionType.makeNewAction(tabs, null, _editor);
		addTab1.setFolder(rootFolder);
		addTab1.setTabTitle("Tab1Title");
		addTab1.setTabDef(_tab1);
		addTab1.setTabContainer(tabs);
		assertTrue(addTab1.doAction().hasActionExecutionSucceeded());

		saveProject();
	}

	/**
	 * Creates a new operation component 3
	 */
	public void test7CreateOperationComponent3() {
		log("test7CreateOperationComponent3");
		AddComponent addComponent = AddComponent.actionType.makeNewAction(_project.getFlexoComponentLibrary(), null, _editor);
		addComponent.setNewComponentName(OPERATION_COMPONENT_3);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_operationComponentResource3 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_3);
		assertNotNull(_operationComponentResource3);
		assertSynchonized(_operationComponentResource3, _rmResource);
		assertSynchonized(_operationComponentResource3, _clResource);
		assertDepends(_operationComponentResource3, _dmResource);

		_operationComponent3 = _operationComponentResource3.getIEOperationComponent();
		assertNotNull(_operationComponent3);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_operationComponent3, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent3, 1, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab) dropTabs.getDroppedWidget();

		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();

		// Put Tab2 inside
		AddTab addTab2 = AddTab.actionType.makeNewAction(tabs, null, _editor);
		addTab2.setFolder(rootFolder);
		addTab2.setTabTitle("Tab2Title");
		addTab2.setTabDef(_tab2);
		addTab2.setTabContainer(tabs);
		assertTrue(addTab2.doAction().hasActionExecutionSucceeded());

		saveProject();
	}

	/**
	 * Add a new sub-process node, check dependancies
	 */
	public void test8CreateSubProcessNodeAndCheckResources() {
		log("test8CreateSubProcessNodeAndCheckResources");
		DropWKFElement addActivity = DropWKFElement.actionType.makeNewAction(_subProcessResource.getFlexoProcess().getActivityPetriGraph(),
				null, _editor);
		addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
		addActivity.setLocation(100, 100);
		addActivity.doAction();
		assertTrue(addActivity.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode) addActivity.getObject();
		activityNode.setName(TEST_ACTIVITY_IN_SUB_PROCESS);
		logger.info("ActivityNode " + activityNode.getName() + " successfully created");

		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
		openOperationLevel.doAction();

		DropWKFElement dropOperation2 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation2.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation2.setLocation(10, 50);
		dropOperation2.doAction();
		assertTrue(dropOperation2.hasActionExecutionSucceeded());
		OperationNode operationNode2 = (OperationNode) dropOperation2.getObject();
		operationNode2.setName(TEST_OPERATION_NODE_2);
		logger.info("OperationNode " + operationNode2.getName() + " successfully created");

		SetAndOpenOperationComponent setOperationComponent2 = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode2, null,
				_editor);
		setOperationComponent2.setNewComponentName(OPERATION_COMPONENT_2);
		setOperationComponent2.doAction();
		assertTrue(setOperationComponent2.hasActionExecutionSucceeded());

		DropWKFElement dropOperation3 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation3.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation3.setLocation(100, 50);
		dropOperation3.doAction();
		assertTrue(dropOperation3.hasActionExecutionSucceeded());
		OperationNode operationNode3 = (OperationNode) dropOperation3.getObject();
		operationNode3.setName(TEST_OPERATION_NODE_3);
		logger.info("OperationNode " + operationNode3.getName() + " successfully created");

		SetAndOpenOperationComponent setOperationComponent3 = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode3, null,
				_editor);
		setOperationComponent3.setNewComponentName(OPERATION_COMPONENT_3);
		setOperationComponent3.doAction();
		assertTrue(setOperationComponent3.hasActionExecutionSucceeded());

		saveProject();
	}

	/**
	 * Reload project, Initialize code generation
	 */
	public void test9InitializeCodeGeneration() {
		log("test9InitializeCodeGeneration");
		reloadProject(true);
		// Save RM for eventual back-synchro to be saved
		saveProject();
		logger.info("Done. Now check that no other back-synchro");
		// Let eventual dependancies back-synchronize together
		reloadProject(true); // This time, all must be not modified
		for (FlexoStorageResource<? extends StorageResourceData> resource : _project.getStorageResources()) {
			assertNotModified(resource);
		}
		File directory = new File(_projectDirectory.getParentFile(), "GeneratedCodeFor" + _project.getProjectName());
		directory.mkdirs();
		createDefaultGCRepository();
		codeRepository.setTargetType(CodeType.PROTOTYPE);

	}

	/**
	 * Reload project, Initialize code generation
	 */
	public void test10ValidateProject() {
		log("test10ValidateProject");

		validateProject(codeRepository, false);

		// To fix errors we need another process and operation on which we will bind the menu
		AddSubProcess process = AddSubProcess.actionType.makeNewAction(_project.getFlexoWorkflow(), null, _editor);
		process.setNewProcessName("Process context free");
		process.doAction();
		defineStatusColumn(process.getNewProcess());
		DropWKFElement addActivity = DropWKFElement.actionType
				.makeNewAction(process.getNewProcess().getActivityPetriGraph(), null, _editor);
		addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
		addActivity.setLocation(100, 100);
		addActivity.doAction();
		assertTrue(addActivity.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode) addActivity.getObject();
		logger.info("ActivityNode " + activityNode.getName() + " successfully created");

		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
		openOperationLevel.doAction();

		DropWKFElement dropOperation2 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation2.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation2.setLocation(10, 50);
		dropOperation2.doAction();
		assertTrue(dropOperation2.hasActionExecutionSucceeded());

		OperationNode operationNodeForMenu = (OperationNode) dropOperation2.getObject();
		operationNodeForMenu.setName("OperationNodeForMenu");

		// We also need to set a screen on the operation node
		AddComponent addComponent = AddComponent.actionType.makeNewAction(_project.getFlexoComponentLibrary(), null, _editor);
		addComponent.setComponentType(ComponentType.OPERATION_COMPONENT);
		addComponent.setNewComponentName("DummyComponentForMenu");
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());

		try {
			operationNodeForMenu.setOperationComponent((OperationComponentDefinition) addComponent.getNewComponent());
		} catch (OperationAssociatedWithComponentSuccessfully e) {
			// Normal
		}

		logger.info("OperationNode " + operationNodeForMenu.getName() + " successfully created");
		_project.getFlexoNavigationMenu().getRootMenu().setProcess(operationNodeForMenu.getProcess());
		_project.getFlexoNavigationMenu().getRootMenu().setOperation(operationNodeForMenu);

		// now we have to define tabs for operations
		associateTabWithOperations();
		// Project should be without errors now
		assertProjectIsValid(codeRepository);

	}

	/**
	 * Reload project, Initialize code generation
	 */
	public void test11SynchronizeCodeGeneration() {
		log("test11SynchronizeCodeGeneration");

		// Synchronize code generation
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		if (!synchronizeCodeGeneration.hasActionExecutionSucceeded()) {
			fail("Synchronization action failed. Action execution status: " + synchronizeCodeGeneration.getExecutionStatus().name());
		}
		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();
		if (!writeToDisk.hasActionExecutionSucceeded()) {
			fail("Writing to disk has failed. Action execution status: " + writeToDisk.getExecutionStatus().name());
		}
		saveProject();

	}

	/**
	 * Reload project, check all dependancies
	 */
	public void test12CheckAllGeneratedResourcesDependancies() {
		log("test12CheckAllGeneratedResourcesDependancies");

		reloadProject(true);
		reloadGeneratedResources();

		assertDepends(operationComponent1JavaResource, _dmResource);
		assertDepends(operationComponent1JavaResource, _operationComponentResource1);
		assertDepends(operationComponent1JavaResource, _tab1ComponentResource);
		assertDepends(operationComponent1JavaResource, _tab2ComponentResource);
		assertDepends(operationComponent1JavaResource, _rootProcessResource);
		assertDepends(operationComponent1APIResource, _dmResource);
		assertDepends(operationComponent1APIResource, _operationComponentResource1);
		assertDepends(operationComponent1APIResource, _tab1ComponentResource);
		assertDepends(operationComponent1APIResource, _tab2ComponentResource);
		assertDepends(operationComponent1APIResource, _rootProcessResource);
		assertDepends(operationComponent1WOResource, _dmResource);
		assertDepends(operationComponent1WOResource, _operationComponentResource1);
		assertDepends(operationComponent1WOResource, _tab1ComponentResource);
		assertDepends(operationComponent1WOResource, _tab2ComponentResource);
		assertDepends(operationComponent1WOResource, _rootProcessResource);

		assertDepends(operationComponent2JavaResource, _dmResource);
		assertDepends(operationComponent2JavaResource, _operationComponentResource2);
		assertDepends(operationComponent2JavaResource, _tab1ComponentResource);
		assertDepends(operationComponent2JavaResource, _subProcessResource);
		assertDepends(operationComponent2APIResource, _dmResource);
		assertDepends(operationComponent2APIResource, _operationComponentResource2);
		assertDepends(operationComponent2APIResource, _tab1ComponentResource);
		assertDepends(operationComponent2APIResource, _subProcessResource);
		assertDepends(operationComponent2WOResource, _dmResource);
		assertDepends(operationComponent2WOResource, _operationComponentResource2);
		assertDepends(operationComponent2WOResource, _tab1ComponentResource);
		assertDepends(operationComponent2WOResource, _subProcessResource);

		assertDepends(operationComponent3JavaResource, _dmResource);
		assertDepends(operationComponent3JavaResource, _operationComponentResource3);
		assertDepends(operationComponent3JavaResource, _tab2ComponentResource);
		assertDepends(operationComponent3JavaResource, _subProcessResource);
		assertDepends(operationComponent3APIResource, _dmResource);
		assertDepends(operationComponent3APIResource, _operationComponentResource3);
		assertDepends(operationComponent3APIResource, _tab2ComponentResource);
		assertDepends(operationComponent3APIResource, _subProcessResource);
		assertDepends(operationComponent3WOResource, _dmResource);
		assertDepends(operationComponent3WOResource, _operationComponentResource3);
		assertDepends(operationComponent3WOResource, _tab2ComponentResource);
		assertDepends(operationComponent3WOResource, _subProcessResource);

		assertDepends(tabComponent1JavaResource, _dmResource);
		assertDepends(tabComponent1JavaResource, _tab1ComponentResource);
		assertDepends(tabComponent1JavaResource, _rootProcessResource);
		assertDepends(tabComponent1JavaResource, _subProcessResource);
		assertDepends(tabComponent1APIResource, _dmResource);
		assertDepends(tabComponent1APIResource, _tab1ComponentResource);
		assertDepends(tabComponent1APIResource, _rootProcessResource);
		assertDepends(tabComponent1APIResource, _subProcessResource);
		assertDepends(tabComponent1WOResource, _dmResource);
		assertDepends(tabComponent1WOResource, _tab1ComponentResource);
		assertDepends(tabComponent1WOResource, _rootProcessResource);
		assertDepends(tabComponent1WOResource, _subProcessResource);

		assertDepends(tabComponent2JavaResource, _dmResource);
		assertDepends(tabComponent2JavaResource, _tab2ComponentResource);
		assertDepends(tabComponent2JavaResource, _rootProcessResource);
		assertDepends(tabComponent2JavaResource, _subProcessResource);
		assertDepends(tabComponent2APIResource, _dmResource);
		assertDepends(tabComponent2APIResource, _tab2ComponentResource);
		assertDepends(tabComponent2APIResource, _rootProcessResource);
		assertDepends(tabComponent2APIResource, _subProcessResource);
		assertDepends(tabComponent2WOResource, _dmResource);
		assertDepends(tabComponent2WOResource, _tab2ComponentResource);
		assertDepends(tabComponent2WOResource, _rootProcessResource);
		assertDepends(tabComponent2WOResource, _subProcessResource);
	}

	/**
	 * Reload project, check all resources status
	 */
	public void test13CheckGeneratedResourcesStatus() {
		log("test13CheckGeneratedResourcesStatus");

		logger.info("Synchronize code generation again");
		// Synchronize code generation again
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		logger.info("Code generation is now synchronized");

		// after a synchronize : all files must normally be up-to-date
		// but : build.properties and Application.conf.PROD depends on the war name (this is really a deploiement parameter, and it IS NOT a
		// model attribute)
		// so we can admit that those files needs to be regenerate since the code repository has changed.
		checkThatAllFilesAreUpToDate();
		// Except(GenerationStatus.GenerationModified,buildPropertiesResource.getCGFile(),appConfProdResource.getCGFile());

	}

	public void test14Delete() {
		codeRepository.delete(true);
		assertTrue("The deleted repository is not supposed to still in the generated code.", _project.getGeneratedCode()
				.getGeneratedRepositories().size() == 0);
		saveProject();
		OperationComponentJavaFileResource generatedFile = (OperationComponentJavaFileResource) _project.resourceForKey(
				ResourceType.JAVA_FILE, codeRepository.getName() + "." + OPERATION_COMPONENT_1);
		assertNull("A generated resource of the deleted repository is supposed to be null after deletion: " + generatedFile, generatedFile);
		assertFalse("The physical directory with Generated code data is supposed to be physically deleted after a delete and save",
				new File(_projectDirectory, "GeneratedCode/GeneratedCode").exists());

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
			// (new Exception()).printStackTrace();
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