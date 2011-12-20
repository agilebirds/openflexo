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
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dm.DuplicateClassNameException;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.action.IEDelete;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoMemoryResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceData;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.StorageResourceData;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.rm.FlexoCopyOfFlexoResource;
import org.openflexo.generator.rm.ProjectTextFileResource;
import org.openflexo.generator.rm.TabComponentAPIFileResource;
import org.openflexo.generator.rm.TabComponentJavaFileResource;
import org.openflexo.generator.rm.TabComponentWOFileResource;
import org.openflexo.generator.utils.DefaultApplicationConfGenerator;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestCG2 extends CGTestCase {

	public TestCG2(String arg0) {
		super(arg0);
	}

	protected static final Logger logger = Logger.getLogger(TestCG2.class.getPackage().getName());

	private static final String TEST_CG2 = "TestCG2";

	private static ProjectTextFileResource applicationConfResource;

	@Override
	protected void reloadGeneratedResources() {
		super.reloadGeneratedResources();
		applicationConfResource = (ProjectTextFileResource) _project.resourceForKey(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(codeRepository, DefaultApplicationConfGenerator.IDENTIFIER));
	}


    public static Test suite() {
		final TestSuite suite = new TestSuite("TestSuite for TestCG2");
		suite.addTest(new TestCG2("test0CreateProject"));
		suite.addTest(new TestCG2("test1InitializeCodeGeneration"));
		suite.addTest(new TestCG2("test2TestRenamingComponentInsideSynchronization"));
		suite.addTest(new TestCG2("test3TestRenamingComponentOutsideSynchronization"));
		suite.addTest(new TestCG2("test4TestRemovingComponentInsideSynchronization"));
		suite.addTest(new TestCG2("test5TestRemovingComponentOutsideSynchronization"));
		return suite;
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		log("test0CreateProject");
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		try {
			File tempFile = File.createTempFile(TEST_CG2, "");
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

		defineStatusColumn(_rootProcessResource.getFlexoProcess());
		AddSubProcess action = AddSubProcess.actionType.makeNewAction(_rootProcessResource.getFlexoProcess(), null, _editor);
		action.setParentProcess(_rootProcessResource.getFlexoProcess());
		action.setNewProcessName(TEST_SUB_PROCESS);
		action.doAction();
		logger.info("SubProcess " + action.getNewProcess().getName() + " successfully created");
		defineStatusColumn(action.getNewProcess());
		_subProcessResource = _project.getFlexoProcessResource(TEST_SUB_PROCESS);
		assertNotNull(_subProcessResource);
		assertSynchonized(_subProcessResource, _rmResource);
		assertSynchonized(_subProcessResource, _wkfResource);
		assertDepends(_subProcessResource, _dmResource);
		assertNotDepends(_subProcessResource, _clResource);
		for (FlexoResource resource : _project.getResources().values()) {
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

		DropWKFElement dropSubProcessNode = DropWKFElement.actionType.makeNewAction(_rootProcessResource.getFlexoProcess()
				.getActivityPetriGraph(), null, _editor);
		dropSubProcessNode.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		dropSubProcessNode.setParameter(DropWKFElement.SUB_PROCESS, _subProcessResource.getFlexoProcess());
		dropSubProcessNode.setLocation(100, 100);
		dropSubProcessNode.doAction();
		assertTrue(dropSubProcessNode.hasActionExecutionSucceeded());
		_subProcessNode = (SubProcessNode) dropSubProcessNode.getObject();
		_subProcessNode.setName(TEST_SUB_PROCESS_NODE);
		logger.info("SubProcessNode " + _subProcessNode.getName() + " successfully created");
		assertDepends(_rootProcessResource, _subProcessResource);
		saveProject();

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
		DropIEElement dropBloc1InOp2 = DropIEElement.createBlocInComponent(_operationComponent2, 0, _editor);
		assertTrue(dropBloc1InOp2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1InOp2 = (IEBlocWidget) dropBloc1InOp2.getDroppedWidget();
		assertNotNull(bloc1InOp2);
		bloc1InOp2.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabsInOp2 = DropIEElement.createTabsInComponent(_operationComponent2, 1, _editor);
		assertTrue(dropTabsInOp2.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabsInOp2 = (IESequenceTab) dropTabsInOp2.getDroppedWidget();

		// Put Tab1 inside
		AddTab addTab1InTabs = AddTab.actionType.makeNewAction(tabsInOp2, null, _editor);
		addTab1InTabs.setFolder(rootFolder);
		addTab1InTabs.setTabTitle("Tab1Title");
		addTab1InTabs.setTabDef(_tab1);
		addTab1InTabs.setTabContainer(tabsInOp2);
		assertTrue(addTab1InTabs.doAction().hasActionExecutionSucceeded());

		AddComponent addComponent3 = AddComponent.actionType.makeNewAction(_project.getFlexoComponentLibrary(), null, _editor);
		addComponent3.setNewComponentName(OPERATION_COMPONENT_3);
		addComponent3.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent3.doAction();
		assertTrue(addComponent3.hasActionExecutionSucceeded());
		_operationComponentResource3 = _project.getFlexoOperationComponentResource(OPERATION_COMPONENT_3);
		assertNotNull(_operationComponentResource3);
		assertSynchonized(_operationComponentResource3, _rmResource);
		assertSynchonized(_operationComponentResource3, _clResource);
		assertDepends(_operationComponentResource3, _dmResource);

		_operationComponent3 = _operationComponentResource3.getIEOperationComponent();
		assertNotNull(_operationComponent3);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1InOp3 = DropIEElement.createBlocInComponent(_operationComponent3, 0, _editor);
		assertTrue(dropBloc1InOp3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1InOp3 = (IEBlocWidget) dropBloc1InOp3.getDroppedWidget();
		assertNotNull(bloc1InOp3);
		bloc1InOp3.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabsInOp3 = DropIEElement.createTabsInComponent(_operationComponent3, 1, _editor);
		assertTrue(dropTabsInOp3.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabsInOp3 = (IESequenceTab) dropTabsInOp3.getDroppedWidget();

		// Put Tab2 inside
		AddTab addTab2InOp3 = AddTab.actionType.makeNewAction(tabsInOp3, null, _editor);
		addTab2InOp3.setFolder(rootFolder);
		addTab2InOp3.setTabTitle("Tab2Title");
		addTab2InOp3.setTabDef(_tab2);
		addTab2InOp3.setTabContainer(tabsInOp3);
		assertTrue(addTab2InOp3.doAction().hasActionExecutionSucceeded());

		DropWKFElement addActivity = DropWKFElement.actionType.makeNewAction(_subProcessResource.getFlexoProcess().getActivityPetriGraph(),
				null, _editor);
		addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
		addActivity.setLocation(100, 100);
		addActivity.doAction();
		assertTrue(addActivity.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode) addActivity.getObject();
		activityNode.setName(TEST_ACTIVITY_IN_SUB_PROCESS);
		logger.info("ActivityNode " + activityNode.getName() + " successfully created");

		OpenOperationLevel openOperationLevelInSubProcess = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
		openOperationLevelInSubProcess.doAction();

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
	public void test1InitializeCodeGeneration() {
		log("test1InitializeCodeGeneration");
		reloadProject(true);
		associateTabWithOperations();
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

		// Now sets the factory

		_editor.registerExceptionHandlerFor(ValidateProject.actionType, new FlexoExceptionHandler<ValidateProject>() {
			@Override
			public boolean handleException(FlexoException exception, ValidateProject action) {
				if (action.getIeValidationReport() != null && action.getIeValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from IE:\n" + action.getIeValidationReport().reportAsString());
				}
				if (action.getWkfValidationReport() != null && action.getWkfValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from WKF:\n" + action.getWkfValidationReport().reportAsString());
				}
				if (action.getDkvValidationReport() != null && action.getDkvValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from DKV:\n" + action.getDkvValidationReport().reportAsString());
				}
				if (action.getDmValidationReport() != null && action.getDmValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from DM:\n" + action.getDmValidationReport().reportAsString());
				}
				return true;
			}
		});

		ValidateProject validateProject = ValidateProject.actionType.makeNewAction(codeRepository, null, _editor);
		validateProject.doAction();

		// First project is not valid
		assertFalse(validateProject.isProjectValid());

		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();

		// Try to fix errors (GPO: this is not required anymore, prefix is always set on root folder unless done otherwise explicitly)
		// FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();
		// rootFolder.setComponentPrefix("TST");
		_project.getFlexoNavigationMenu().getRootMenu().setProcess(_operationNode.getProcess());
		_project.getFlexoNavigationMenu().getRootMenu().setOperation(_operationNode);

		// Project should be without errors now
		validateProject = ValidateProject.actionType.makeNewAction(codeRepository, null, _editor);
		validateProject.doAction();
		assertTrue(validateProject.isProjectValid());

		saveProject();

		// Synchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();

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

		saveProject();

		reloadProject(true);
		reloadGeneratedResources();

		logger.info("Synchronize code generation again");

		// Synchronize code generation again
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGenerationAgain = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		synchronizeCodeGenerationAgain.setContinueAfterValidation(true);
		synchronizeCodeGenerationAgain.doAction();

		logger.info("Code generation is now synchronized");

		checkThatAllFilesAreUpToDate();
		// Except(GenerationStatus.GenerationModified,buildPropertiesResource.getCGFile(),appConfProdResource.getCGFile());

	}

	private static final String NEW_TAB1_NAME = "Tab1WasRenamed";

	private static final String NEW_TAB2_NAME = "Tab2WasRenamed";

	public void test2TestRenamingComponentInsideSynchronization() {
		log("test2TestRenamingComponentInsideSynchronization");

		checkThatAllFilesAreUpToDate();
		// Except(GenerationStatus.GenerationModified,buildPropertiesResource.getCGFile(),appConfProdResource.getCGFile());

		try {
			_tab1.setName(NEW_TAB1_NAME);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			fail();
		} catch (DuplicateClassNameException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		}

		TabComponentAPIFileResource renamedComponentAPIResource = (TabComponentAPIFileResource) _project.resourceForKey(
				ResourceType.API_FILE, codeRepository.getName() + "." + NEW_TAB1_NAME);
		TabComponentWOFileResource renamedComponentWOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + NEW_TAB1_NAME);
		TabComponentJavaFileResource renamedComponentJavaResource = (TabComponentJavaFileResource) _project.resourceForKey(
				ResourceType.JAVA_FILE, codeRepository.getName() + "." + NEW_TAB1_NAME);

		assertNotNull(renamedComponentAPIResource);
		assertNotNull(renamedComponentWOResource);
		assertNotNull(renamedComponentJavaResource);

		checkThatAllFilesAreUpToDateExcept(operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent2APIResource.getCGFile(),
				operationComponent2WOResource.getCGFile(),
				operationComponent2JavaResource.getCGFile(),
				operationComponent2ScreenshotCopyOfCopy.getCGFile(),
				tabComponent1APIResource.getCGFile(),
				tabComponent1WOResource.getCGFile(),
				tabComponent1JavaResource.getCGFile(),
				tabComponent1ScreenshotCopyOfCopy.getCGFile(),
				renamedComponentAPIResource.getCGFile(),
				renamedComponentWOResource.getCGFile(),
				renamedComponentJavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), // tab2 is also set as "needs generation" the WO tab2 depends of OperationNode
				tabComponent2WOResource.getCGFile(), tabComponent2JavaResource.getCGFile(), tabComponent2ScreenshotCopyOfCopy.getCGFile(),
				classpathTextResource.getCGFile(), daJavaResource.getCGFile(), cstJavaResource.getCGFile(),
				headerFooterAPIResource.getCGFile(), headerFooterJavaResource.getCGFile(), headerFooterWOResource.getCGFile(),
				rootProcessJSCopy.getCGFile(), subProcessJSCopy.getCGFile(), rootProcessScreenshotCopyOfCopy.getCGFile(),
				subProcessNodeScreenshotInRootProcessCopyOfCopy.getCGFile(), operationNode1ScreenshotCopyOfCopy.getCGFile(),
				subProcessScreenshotCopyOfCopy.getCGFile(), activityInSubProcessScreenshotCopyOfCopy.getCGFile(),
				operationNode2ScreenshotCopyOfCopy.getCGFile(), operationNode3ScreenshotCopyOfCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile(), operationComponent2ScreenshotCopyOfCopy.getCGFile(),
				workflowComponentInstanceResource.getCGFile());

		assertEquals(operationComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(operationComponent2APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent2WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent2JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(tabComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);

		assertEquals(renamedComponentAPIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);
		assertEquals(renamedComponentWOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);
		assertEquals(renamedComponentJavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);

		assertTrue(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());

		assertTrue(operationComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent2WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent2JavaResource.getCGFile().needsMemoryGeneration());

		// The 3 next lines are commented because the generation is immediately performed when we discover that the
		// tab has been renamed. We, however, leave those 3 lines so that nobody ever tries again to add them before
		// and get stuck here because he doesn't understand why we haven't put them before.
		// assertTrue(renamedComponentAPIResource.getCGFile().needsMemoryGeneration());
		// assertTrue(renamedComponentWOResource.getCGFile().needsMemoryGeneration());
		// assertTrue(renamedComponentJavaResource.getCGFile().needsMemoryGeneration());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		assertFalse(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());

		assertFalse(operationComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent2WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent2JavaResource.getCGFile().needsMemoryGeneration());

		assertFalse(renamedComponentAPIResource.getCGFile().needsMemoryGeneration());
		assertFalse(renamedComponentWOResource.getCGFile().needsMemoryGeneration());
		assertFalse(renamedComponentJavaResource.getCGFile().needsMemoryGeneration());

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		saveProject();
	}

	public void test3TestRenamingComponentOutsideSynchronization() {
		log("test3TestRenamingComponentOutsideSynchronization");
		reloadProject(true, NEW_TAB1_NAME, TAB_COMPONENT2);
		reloadGeneratedResources(NEW_TAB1_NAME, TAB_COMPONENT2);

		try {
			_tab2.setName(NEW_TAB2_NAME);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			fail();
		} catch (DuplicateClassNameException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidNameException e) {
			e.printStackTrace();
			fail();
		}

		TabComponentAPIFileResource renamedComponentAPIResource = (TabComponentAPIFileResource) _project.resourceForKey(
				ResourceType.API_FILE, codeRepository.getName() + "." + NEW_TAB2_NAME);
		TabComponentWOFileResource renamedComponentWOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + NEW_TAB2_NAME);
		TabComponentJavaFileResource renamedComponentJavaResource = (TabComponentJavaFileResource) _project.resourceForKey(
				ResourceType.JAVA_FILE, codeRepository.getName() + "." + NEW_TAB2_NAME);

		assertNull(renamedComponentAPIResource);
		assertNull(renamedComponentWOResource);
		assertNull(renamedComponentJavaResource);

		// Synchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		renamedComponentAPIResource = (TabComponentAPIFileResource) _project.resourceForKey(ResourceType.API_FILE, codeRepository.getName()
				+ "." + NEW_TAB2_NAME);
		renamedComponentWOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE, codeRepository.getName()
				+ "." + NEW_TAB2_NAME);
		renamedComponentJavaResource = (TabComponentJavaFileResource) _project.resourceForKey(ResourceType.JAVA_FILE,
				codeRepository.getName() + "." + NEW_TAB2_NAME);
		FlexoCopyOfFlexoResource copyOfCopyOfScreenshotOfRenamedTab = getCopyOfReaderScreenshotResourceForObject(_tab2);
		assertNotNull(renamedComponentAPIResource);
		assertNotNull(renamedComponentWOResource);
		assertNotNull(renamedComponentJavaResource);

		checkThatAllFilesAreUpToDateExcept(
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(),
				tabComponent1APIResource.getCGFile(),// tab1 is also set as "needs generation" the WO tab2 depends of OperationNode
				tabComponent1WOResource.getCGFile(), tabComponent1JavaResource.getCGFile(), tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(), tabComponent2JavaResource.getCGFile(), renamedComponentAPIResource.getCGFile(),
				renamedComponentWOResource.getCGFile(), renamedComponentJavaResource.getCGFile(),
				tabComponent2ScreenshotCopyOfCopy.getCGFile(), copyOfCopyOfScreenshotOfRenamedTab.getCGFile(),
				classpathTextResource.getCGFile(), daJavaResource.getCGFile(), buildPropertiesResource.getCGFile(),
				appConfProdResource.getCGFile(), cstJavaResource.getCGFile(), headerFooterAPIResource.getCGFile(),
				headerFooterJavaResource.getCGFile(), headerFooterWOResource.getCGFile(), rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(), subProcessScreenshotCopyOfCopy.getCGFile(),
				activityInSubProcessScreenshotCopyOfCopy.getCGFile(), operationNode2ScreenshotCopyOfCopy.getCGFile(),
				operationNode3ScreenshotCopyOfCopy.getCGFile(), operationComponent1ScreenshotCopyOfCopy.getCGFile(),
				operationComponent3ScreenshotCopyOfCopy.getCGFile());

		assertEquals(operationComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(operationComponent3APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent3WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent3JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(tabComponent2APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent2WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent2JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);

		assertEquals(renamedComponentAPIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);
		assertEquals(renamedComponentWOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);
		assertEquals(renamedComponentJavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationAdded);

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		generateRequired.doAction();
		logger.info("Generate required file DONE");
		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		saveProject();

		checkThatAllFilesAreUpToDate();

	}

	public void test4TestRemovingComponentInsideSynchronization() {
		log("test4TestRemovingComponentInsideSynchronization");

		checkThatAllFilesAreUpToDate();

		logger.info("Will delete tab1");
		IEDelete deleteTab1 = IEDelete.actionType.makeNewAction(_tab1, null, _editor);
		assertTrue(deleteTab1.doAction().hasActionExecutionSucceeded());
		logger.info("Delete tab1 DONE");
		TabComponentAPIFileResource renamedComponentAPIResource = (TabComponentAPIFileResource) _project.resourceForKey(
				ResourceType.API_FILE, codeRepository.getName() + "." + NEW_TAB2_NAME);
		TabComponentWOFileResource renamedComponentWOResource = (TabComponentWOFileResource) _project.resourceForKey(ResourceType.WO_FILE,
				codeRepository.getName() + "." + NEW_TAB2_NAME);
		TabComponentJavaFileResource renamedComponentJavaResource = (TabComponentJavaFileResource) _project.resourceForKey(
				ResourceType.JAVA_FILE, codeRepository.getName() + "." + NEW_TAB2_NAME);

		checkThatAllFilesAreUpToDateExcept(operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(), operationComponent2APIResource.getCGFile(),
				operationComponent2WOResource.getCGFile(), operationComponent2JavaResource.getCGFile(),
				tabComponent1APIResource.getCGFile(), tabComponent1WOResource.getCGFile(), tabComponent1JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), tabComponent2WOResource.getCGFile(), tabComponent2JavaResource.getCGFile(),
				headerFooterJavaResource.getCGFile(), headerFooterWOResource.getCGFile(), headerFooterAPIResource.getCGFile(),
				classpathTextResource.getCGFile(), daJavaResource.getCGFile(), cstJavaResource.getCGFile(),
				renamedComponentAPIResource.getCGFile(), renamedComponentWOResource.getCGFile(), renamedComponentJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(), subProcessJSCopy.getCGFile(), rootProcessScreenshotCopyOfCopy.getCGFile(),
				subProcessNodeScreenshotInRootProcessCopyOfCopy.getCGFile(), subProcessScreenshotCopyOfCopy.getCGFile(),
				activityInSubProcessScreenshotCopyOfCopy.getCGFile(), operationNode1ScreenshotCopyOfCopy.getCGFile(),
				operationNode2ScreenshotCopyOfCopy.getCGFile(), operationNode3ScreenshotCopyOfCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile(), operationComponent2ScreenshotCopyOfCopy.getCGFile());

		assertEquals(operationComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(operationComponent2APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent2WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent2JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(tabComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);

		assertTrue(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());

		assertTrue(operationComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent2WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent2JavaResource.getCGFile().needsMemoryGeneration());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		assertFalse(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());

		assertFalse(operationComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent2WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent2JavaResource.getCGFile().needsMemoryGeneration());

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		saveProject();

		assertFalse(tabComponent1APIResource.getFile().exists());
		assertFalse(tabComponent1WOResource.getFile().exists());
		assertFalse(tabComponent1JavaResource.getFile().exists());

	}

	public void test5TestRemovingComponentOutsideSynchronization() {
		log("test5TestRemovingComponentOutsideSynchronization");
		reloadProject(true, null, NEW_TAB2_NAME);
		reloadGeneratedResources(null, NEW_TAB2_NAME);

		logger.info("Will delete tab2");
		IEDelete deleteTab2 = IEDelete.actionType.makeNewAction(_tab2, null, _editor);
		assertTrue(deleteTab2.doAction().hasActionExecutionSucceeded());
		logger.info("Delete tab2 DONE");
		// Synchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		System.out.println(synchronizeCodeGeneration.getValidationErrorAsString());
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());

		// TODO : decommenter ce qui suit et passer le test.

		// le problème est causé par la suppression d'un composant qui n'efface
		// pas le screenshoot associé... il reste une ref dans le repository
		// du moins c'est ce qui me semble.

		// ce problème est apparu en résolvant un probleme de dependance des screenshootResource.
		// voir le code de "ScreenshootResource.rebuildDependencies()" qui est a present
		// un peu particulier dans les cas ComponentDefinition :
		// AVANT : on ajoutait la ComponentLibrary dans les dependentResources (ce qui me semble faut)
		// MAINTENANT : on ajoute la ComponentResource correspondant a la ComponentDef dans les dependantResources (ce qui me semble
		// correct)
		// CONCLUSION : les screenshotResource ont des dependances correctes, et cela fait apparaitre un probleme lors du delete d'un
		// composant...

		// checkThatAllFilesAreUpToDateExcept(
		// operationComponent1APIResource.getCGFile(),
		// operationComponent1WOResource.getCGFile(),
		// operationComponent1JavaResource.getCGFile(),
		// operationComponent3APIResource.getCGFile(),
		// operationComponent3WOResource.getCGFile(),
		// operationComponent3JavaResource.getCGFile(),
		// tabComponent2APIResource.getCGFile(),
		// tabComponent2WOResource.getCGFile(),
		// tabComponent2JavaResource.getCGFile(),
		// headerFooterJavaResource.getCGFile(),
		// headerFooterWOResource.getCGFile(),
		// headerFooterAPIResource.getCGFile(),
		// classpathTextResource.getCGFile(),
		// daJavaResource.getCGFile(),
		// buildPropertiesResource.getCGFile(),
		// appConfProdResource.getCGFile(),
		// cstJavaResource.getCGFile(),
		// rootProcessJSCopy.getCGFile(),
		// subProcessJSCopy.getCGFile(),
		// subProcessScreenshotCopyOfCopy.getCGFile(),
		// activityInSubProcessScreenshotCopyOfCopy.getCGFile(),
		// operationNode2ScreenshotCopyOfCopy.getCGFile(),
		// operationNode3ScreenshotCopyOfCopy.getCGFile(),
		// operationComponent1ScreenshotCopyOfCopy.getCGFile(),
		// operationComponent3ScreenshotCopyOfCopy.getCGFile(),
		// tabComponent2ScreenshotCopyOfCopy.getCGFile()
		// );

		assertEquals(operationComponent1APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent1JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(operationComponent3APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent3WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);
		assertEquals(operationComponent3JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationModified);

		assertEquals(tabComponent2APIResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent2WOResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent2JavaResource.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);
		assertEquals(tabComponent2ScreenshotCopyOfCopy.getCGFile().getGenerationStatus(), GenerationStatus.GenerationRemoved);

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		saveProject();

		assertFalse(tabComponent2APIResource.getFile().exists());
		assertFalse(tabComponent2WOResource.getFile().exists());
		assertFalse(tabComponent2JavaResource.getFile().exists());
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		resetVariables();
	}

}
