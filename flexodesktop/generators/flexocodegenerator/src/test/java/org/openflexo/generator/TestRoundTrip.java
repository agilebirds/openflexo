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
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.Merge;
import org.openflexo.diff.merge.MergeChange.MergeChangeAction;
import org.openflexo.diff.merge.MergeChange.MergeChangeSource;
import org.openflexo.diff.merge.MergeChange.MergeChangeType;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateFile.TemplateFileContentEditor;
import org.openflexo.foundation.cg.templates.action.AddCustomTemplateRepository;
import org.openflexo.foundation.cg.templates.action.EditCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.RedefineCustomTemplateFile;
import org.openflexo.foundation.cg.templates.action.SaveCustomTemplateFile;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.ie.IETabComponent;
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
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoMemoryResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.generator.action.EditGeneratedFile;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.action.MarkAsMerged;
import org.openflexo.generator.action.SaveGeneratedFile;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.generator.file.AbstractCGFile;
import org.openflexo.generator.rm.BuildPropertiesResource;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public class TestRoundTrip extends CGTestCase {

	private static final String BUILDPROPERTIES_VM = "build.properties.vm";

	public TestRoundTrip(String arg0) {
		super(arg0);
	}

	protected static final Logger logger = Logger.getLogger(TestRoundTrip.class.getPackage().getName());

	private static final String TEST_RT = "TestRoundTrip";

	static BuildPropertiesResource _buildPropertiesResource;

	@Override
	protected void reloadGeneratedResources() {
		super.reloadGeneratedResources();
		_buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE, codeRepository.getName()
				+ ".BUILD_PROPERTIES");
	}

	@Override
	protected void reloadProject(boolean fullLoading) {
		super.reloadProject(fullLoading);
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		log("test0CreateProject");
		ToolBox.setPlatform();
		FlexoLoggingManager.forceInitialize();
		try {
			File tempFile = File.createTempFile(TEST_RT, "");
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

		for (FlexoResource resource : _project.getResources().values()) {
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
        defineStatusColumn(action.getNewProcess());
		assertTrue(action.hasActionExecutionSucceeded());
		logger.info("SubProcess " + action.getNewProcess().getName() + " successfully created");
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
		assertTrue(openOperationLevel.hasActionExecutionSucceeded());
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
		assertTrue(openOperationLevel.hasActionExecutionSucceeded());
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
		// Save RM for eventual back-synchro to be saved
		saveProject();
		logger.info("Done. Now check that no other back-synchro");
		// Let eventual dependancies back-synchronize together
		reloadProject(true); // This time, all must be not modified
		for (FlexoResource resource : _project.getResources().values()) {
			if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource) resource);
			}
		}
		File directory = new File(_projectDirectory.getParentFile(), "GeneratedCodeFor" + _project.getProjectName());
		directory.mkdirs();

		createDefaultGCRepository();
		codeRepository.setTargetType(CodeType.PROTOTYPE);

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

		// Try to fix errors (GPO: this is not required anymore, prefix is always set on root folder unless done otherwise explicitly)
		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();
		// rootFolder.setComponentPrefix("TST");
		// To fix errors we need another process and operation on which we will bind the menu
		AddSubProcess process = AddSubProcess.actionType.makeNewAction(_project.getFlexoWorkflow(), null, _editor);
		process.setNewProcessName("Process context free");
		process.doAction();
		assertTrue(process.hasActionExecutionSucceeded());
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
		assertTrue(openOperationLevel.hasActionExecutionSucceeded());
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
		associateTabWithOperations();
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
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());
		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();
		assertTrue(writeToDisk.hasActionExecutionSucceeded());
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
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());
		logger.info("Code generation is now synchronized");
		checkThatAllFilesAreUpToDate();
		// Except(GenerationStatus.GenerationModified,buildPropertiesResource.getCGFile(),appConfProdResource.getCGFile());

	}

	/**
	 * Perform some optimitic dependancy checking while editing components
	 */
	public void test2CheckDependancyCheckingWithComponents() {
		log("test2CheckDependancyCheckingWithComponents");

		_operationComponent1 = _operationComponentResource1.getIEOperationComponent();
		assertNotNull(_operationComponent1);

		// On component 1, insert a new bloc at index 3, name it Bloc4
		DropIEElement dropBloc4 = DropIEElement.createBlocInComponent(_operationComponent1, 0, _editor);
		assertTrue(dropBloc4.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc4 = (IEBlocWidget) dropBloc4.getDroppedWidget();
		assertNotNull(bloc4);

		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(), cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(), operationComponent1ScreenshotCopyOfCopy.getCGFile());
		// Generate content
		GenerateSourceCode generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();

		checkThatAllFilesAreUpToDate();

		bloc4.setTitle("Bloc4");

		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(), cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(), operationComponent1ScreenshotCopyOfCopy.getCGFile());
		generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());
		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();

		// And save project
		saveProject();

		IETabComponent tab2 = _tab2ComponentResource.getTabComponent();
		assertNotNull(tab2);

		// Insert a new bloc at index 0, name it "BlocInTab2"
		DropIEElement dropBlocInTab2 = DropIEElement.createBlocInComponent(tab2, 0, _editor);
		assertTrue(dropBlocInTab2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc = (IEBlocWidget) dropBlocInTab2.getDroppedWidget();
		assertNotNull(bloc);
		bloc.setTitle("BlocInTab2");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);
		// Drop a label in the table, at cell (0,0) at position 0
		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table, WidgetType.LABEL, 0, 0, 0, _editor);
		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());

		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(), operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(), cstJavaResource.getCGFile(), rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(), operationComponent1ScreenshotCopyOfCopy.getCGFile(),
				operationComponent3ScreenshotCopyOfCopy.getCGFile(), tabComponent2ScreenshotCopyOfCopy.getCGFile());

		generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());

		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();

		checkThatAllFilesAreUpToDate();

		// And save project
		saveProject();

		logger.info("Before modifying");
		logger.info("_tab2ComponentResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(_tab2ComponentResource.getLastUpdate()));
		logger.info("tabComponent2JavaResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(tabComponent2JavaResource.getLastUpdate()));

		// Now we change again bloc name
		bloc.setTitle("BlocInTab2");

		// Naturally those resources are changed
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(), operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(), cstJavaResource.getCGFile(), rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(), operationComponent1ScreenshotCopyOfCopy.getCGFile(),
				operationComponent3ScreenshotCopyOfCopy.getCGFile(), tabComponent2ScreenshotCopyOfCopy.getCGFile());

		logger.info("After modifying");
		logger.info("_tab2ComponentResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(_tab2ComponentResource.getLastUpdate()));
		logger.info("tabComponent2JavaResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(tabComponent2JavaResource.getLastUpdate()));

		log("OK, trying to save and reload project");

		// But now we don't write it on disk, just save project
		saveProject();

		// And reload it
		reloadProject(true);
		reloadGeneratedResources();
		// And resynchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		logger.info("After code synchro");
		logger.info("_tab2ComponentResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(_tab2ComponentResource.getLastUpdate()));
		logger.info("tabComponent2JavaResource update="
				+ new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(tabComponent2JavaResource.getLastUpdate()));

		// The same resources must be in the 'modified' state except for the ones that have been cleared because the backsynchronization
		// mechanism has not caused an update in resource dependancy tree (The
		// We test here the persistance of 'Modified' status
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(), operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(), tabComponent2ScreenshotCopyOfCopy.getCGFile());

		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();
		assertTrue(writeToDisk.hasActionExecutionSucceeded());
		// And save project
		saveProject();

	}

	/**
	 * Check that templates dynamic dependancies scheme is working
	 */
	public void test3CheckTemplateDependancies() {
		log("test3CheckTemplateDependancies");
		CGTemplate labelHTMLTemplate = null;
		try {
			labelHTMLTemplate = projectGenerator.getTemplateLocator().templateWithName("Label.html.vm");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(labelHTMLTemplate.isApplicationTemplate());

		checkDependingOnTemplate(labelHTMLTemplate, tabComponent2JavaResource.getCGFile(), tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile());

		AddCustomTemplateRepository addCustomTemplateRepository = AddCustomTemplateRepository.actionType.makeNewAction(_project
				.getGeneratedCode().getTemplates(), null, _editor);
		addCustomTemplateRepository.setNewCustomTemplatesRepositoryName("MyCustomTemplates");
		addCustomTemplateRepository.setRepositoryType(TemplateRepositoryType.Code);
		addCustomTemplateRepository.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(_project, "MyCustomTemplates"));
		assertTrue(addCustomTemplateRepository.doAction().hasActionExecutionSucceeded());

		RedefineCustomTemplateFile redefineTemplate = RedefineCustomTemplateFile.actionType.makeNewAction(labelHTMLTemplate, null, _editor);
		redefineTemplate.setRepository(addCustomTemplateRepository.getNewCustomTemplatesRepository());
		redefineTemplate.setTarget(CodeType.PROTOTYPE);
		assertTrue(redefineTemplate.doAction().hasActionExecutionSucceeded());

		CGTemplateFile newLabelHTMLTemplate = redefineTemplate.getNewTemplateFile();

		codeRepository.setPreferredTemplateRepository(addCustomTemplateRepository.getNewCustomTemplatesRepository());

		// Resynchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());
		CGTemplate newTabelHTMLTemplate2 = null;
		try {
			newTabelHTMLTemplate2 = projectGenerator.getTemplateLocator().templateWithName("Label.html.vm");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(newTabelHTMLTemplate2.isCustomTemplate());
		assertTrue(newTabelHTMLTemplate2 == newLabelHTMLTemplate);

		// Suppose that this template has changed
		newLabelHTMLTemplate.setChanged();

		// In this case, all those file must be marked as modified
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(), tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile());

		checkDependingOnTemplate(newLabelHTMLTemplate, tabComponent2JavaResource.getCGFile(), tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();
		assertTrue(writeToDisk.hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		final String ADDED_STRING = "DenisEstBete";
		final String oldContent = newTabelHTMLTemplate2.getContent();
		final String newContent = oldContent + ADDED_STRING;

		EditCustomTemplateFile editTemplate = EditCustomTemplateFile.actionType.makeNewAction(newLabelHTMLTemplate, null, _editor);
		editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
			@Override
			public String getEditedContent() {
				return newContent;
			}

			@Override
			public void setEditedContent(String content) {
			}
		});
		editTemplate.doAction();
		assertTrue(editTemplate.hasActionExecutionSucceeded());
		SaveCustomTemplateFile saveTemplate = SaveCustomTemplateFile.actionType.makeNewAction(newLabelHTMLTemplate, null, _editor);
		saveTemplate.doAction();
		assertTrue(saveTemplate.hasActionExecutionSucceeded());
		checkDependingOnTemplate(newLabelHTMLTemplate, tabComponent2JavaResource.getCGFile(), tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(), operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(), operationComponent1WOResource.getCGFile());
		waitForVelocityRefresh();
		// Generate required files
		log("Generate required file");
		logger.info("Generate required file");
		generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// Write generated files to disk
		log("Write required file");
		logger.info("Write required file");
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		try {
			File tab2_html = new File(tabComponent2WOResource.getFile(), TAB_COMPONENT2 + ".html");
			logger.info("file: " + tab2_html.getAbsolutePath());
			logger.info("content: " + FileUtils.fileContents(tab2_html));
			logger.info("index=" + FileUtils.fileContents(tab2_html).indexOf(ADDED_STRING));
			assertTrue(FileUtils.fileContents(tab2_html).indexOf(ADDED_STRING) > 0);
			File operation1_html = new File(operationComponent1WOResource.getFile(), OPERATION_COMPONENT_1 + ".html");
			assertTrue(FileUtils.fileContents(operation1_html).indexOf(ADDED_STRING) > 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Check that custom template edition is working
	 */
	public void test4TestEditCustomTemplate() {
		log("test4TestEditCustomTemplate");

		_buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE, codeRepository.getName()
				+ ".BUILD_PROPERTIES");

		CGTemplate buildPropertiesTemplate = null;
		try {
			buildPropertiesTemplate = projectGenerator.getTemplateLocator().templateWithName(BUILDPROPERTIES_VM);
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(buildPropertiesTemplate.isApplicationTemplate());

		RedefineCustomTemplateFile redefineTemplate = RedefineCustomTemplateFile.actionType.makeNewAction(buildPropertiesTemplate, null,
				_editor);
		redefineTemplate.setRepository(codeRepository.getPreferredTemplateRepository());
		redefineTemplate.setTarget(CodeType.PROTOTYPE);
		assertTrue(redefineTemplate.doAction().hasActionExecutionSucceeded());

		final CGTemplateFile newBuildPropertiesTemplate = redefineTemplate.getNewTemplateFile();

		// Resynchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(
				codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());
		// Suppose that this template has changed
		// Modify first line
		try {
			Thread.sleep(100);
			// This sleep is here so that the lastUpdate date of the template will be at least 100 ms after the last memory generation
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		final String ADDED_ON_FIRST_LINE = "Added on first line";

		EditCustomTemplateFile editTemplate = EditCustomTemplateFile.actionType.makeNewAction(newBuildPropertiesTemplate, null, _editor);
		editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
			@Override
			public String getEditedContent() {
				return ADDED_ON_FIRST_LINE + newBuildPropertiesTemplate.getContent();
			}

			@Override
			public void setEditedContent(String content) {
			}
		});
		editTemplate.doAction();
		assertTrue(editTemplate.hasActionExecutionSucceeded());
		SaveCustomTemplateFile saveTemplate = SaveCustomTemplateFile.actionType.makeNewAction(newBuildPropertiesTemplate, null, _editor);
		saveTemplate.doAction();
		assertTrue(saveTemplate.hasActionExecutionSucceeded());
		// In this case, DefaultApplication.conf must be marked as modified
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, _buildPropertiesResource.getCGFile());

		// Check that file is to regenerate and generator also to run again
		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());
		waitForVelocityRefresh();
		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// And depending on template
		checkDependingOnTemplate(newBuildPropertiesTemplate, _buildPropertiesResource.getCGFile());

		// Check that file is to regenerate but generator has run and memory generation is up-to-date
		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified);
		assertFalse(_buildPropertiesResource.getGenerator().needsGeneration());

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		writeToDisk.doAction();
		assertTrue(writeToDisk.hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		try {
			assertTrue(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()).indexOf(ADDED_ON_FIRST_LINE) == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Check that edition of file inside Flexo is working
	 */
	public void test5TestEditGeneratedFileInFlexo() {
		log("test5TestEditGeneratedFileInFlexo");

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		final String ADDED_ON_FIRST_LINE_BY_FLEXO = "ADDED_ON_FIRST_LINE_BY_FLEXO";

		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.UpToDate);

		// Edit file by prepending string on first line
		EditGeneratedFile editFile = EditGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		editFile.setFileContentEditor(new CGFile.FileContentEditor() {
			@Override
			public String getEditedContentForKey(String contentKey) {
				return ADDED_ON_FIRST_LINE_BY_FLEXO
						+ _buildPropertiesResource.getGeneratedResourceData().getContent(ContentSource.CONTENT_ON_DISK);
			}

			@Override
			public void setEditedContent(CGFile file) {
			}
		});
		assertTrue(editFile.doAction().hasActionExecutionSucceeded());

		// logger.info("applicationConfResource.getDiskLastModifiedDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getDiskLastModifiedDate()));
		// logger.info("applicationConfResource.getLastAcceptingDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getLastAcceptingDate()));

		// Save file
		SaveGeneratedFile saveFile = SaveGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(saveFile.doAction().hasActionExecutionSucceeded());

		// Status of file must be DiskModified
		// logger.info("applicationConfResource.getDiskLastModifiedDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getDiskLastModifiedDate()));
		// logger.info("applicationConfResource.getLastAcceptingDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getLastAcceptingDate()));
		// logger.info("applicationConfResource.getGenerationStatus()="+applicationConfResource.getGenerationStatus());

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.DiskModified);

		// Check if this was successfully written on disk
		try {
			assertTrue(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()).indexOf(ADDED_ON_FIRST_LINE_BY_FLEXO) == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		// Save file
		AcceptDiskUpdate acceptDiskUpdate = AcceptDiskUpdate.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(acceptDiskUpdate.doAction().hasActionExecutionSucceeded());

		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.UpToDate);
	}

	/**
	 * Check that edition of file outside Flexo is working (third-party application)
	 */
	public void test6TestEditGeneratedFileOutsideFlexo() {
		log("test6TestEditGeneratedFileOutsideFlexo");

		final String ADDED_ON_FIRST_LINE_OUTSIDE_FLEXO = "ADDED_ON_FIRST_LINE_OUTSIDE_FLEXO";

		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.UpToDate);

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		try {
			String contentOnDisk = FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile());
			contentOnDisk = ADDED_ON_FIRST_LINE_OUTSIDE_FLEXO + contentOnDisk;
			FileUtils.saveToFile(_buildPropertiesResource.getResourceFile().getFile(), contentOnDisk);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		// And now we must wait for some time (mimimal is FlexoResourceManager.RESOURCE_CHECKING_DELAY)
		logger.info("Waiting " + (FlexoResourceManager.RESOURCE_CHECKING_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoResourceManager.RESOURCE_CHECKING_DELAY + 3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		// Status of file must be DiskModified

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.DiskModified);

		// Check if this was successfully read

		assertTrue(_buildPropertiesResource.getGeneratedResourceData().getContent(ContentSource.CONTENT_ON_DISK)
				.indexOf(ADDED_ON_FIRST_LINE_OUTSIDE_FLEXO) == 0);

		// Accept file
		AcceptDiskUpdate acceptDiskUpdate = AcceptDiskUpdate.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(acceptDiskUpdate.doAction().hasActionExecutionSucceeded());

		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.UpToDate);

		// We will now check that the only difference between last generated and last accepted version is the first line

		DiffReport diffReport = ComputeDiff.diff(
				_buildPropertiesResource.getGeneratedResourceData().getContent(ContentSource.LAST_GENERATED), _buildPropertiesResource
						.getGeneratedResourceData().getContent(ContentSource.LAST_ACCEPTED));

		assertEquals(diffReport.getChanges().size(), 1);
		assertEquals(diffReport.getChanges().firstElement().getFirst0(), 0);
		assertEquals(diffReport.getChanges().firstElement().getLast0(), 0);
		assertEquals(diffReport.getChanges().firstElement().getFirst1(), 0);
		assertEquals(diffReport.getChanges().firstElement().getLast1(), 0);
	}

	/**
	 * Check that automatic merging when there is no conflict is working
	 */
	public void test7TestMergeWithoutConflict() {
		log("test7TestMergeWithoutConflict");

		_buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE, codeRepository.getName()
				+ ".BUILD_PROPERTIES");

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		CGTemplate newBuildPropertiesTemplate = null;

		// Retrieve custom template
		// And modify this template by prepending line nb on line 3 to 5
		try {
			final CGTemplate editedBuildPropertiesTemplate = projectGenerator.getTemplateLocator().templateWithName(BUILDPROPERTIES_VM);
			EditCustomTemplateFile editTemplate = EditCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
				@Override
				public String getEditedContent() {
					return tagStringWithLineNb(editedBuildPropertiesTemplate.getContent(), "*", 3, 5);
				}

				@Override
				public void setEditedContent(String content) {
				}
			});
			editTemplate.doAction();
			assertTrue(editTemplate.hasActionExecutionSucceeded());
			SaveCustomTemplateFile saveTemplate = SaveCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			saveTemplate.doAction();
			assertTrue(saveTemplate.hasActionExecutionSucceeded());
			newBuildPropertiesTemplate = editedBuildPropertiesTemplate;
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		// In this case, DefaultApplication.conf must be marked as modified
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, _buildPropertiesResource.getCGFile());

		// And depending on template
		checkDependingOnTemplate(newBuildPropertiesTemplate, _buildPropertiesResource.getCGFile());

		// Check that file is to regenerate and generator also to run again
		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());

		// Edit file by prepending line nb on line 8 to 11
		EditGeneratedFile editFile = EditGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		editFile.setFileContentEditor(new CGFile.FileContentEditor() {
			@Override
			public String getEditedContentForKey(String contentKey) {
				return tagStringWithLineNb(_buildPropertiesResource.getGeneratedResourceData().getContent(ContentSource.CONTENT_ON_DISK),
						"#", 8, 11);
			}

			@Override
			public void setEditedContent(CGFile file) {
			}
		});
		assertTrue(editFile.doAction().hasActionExecutionSucceeded());
		// logger.info("applicationConfResource.getDiskLastModifiedDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getDiskLastModifiedDate()));
		// logger.info("applicationConfResource.getLastAcceptingDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getLastAcceptingDate()));

		// Save file
		SaveGeneratedFile saveFile = SaveGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(saveFile.doAction().hasActionExecutionSucceeded());
		try {
			Thread.currentThread().sleep(4000);
			System.out.println(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Status of file must be ConflictingUnMerged
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.ConflictingUnMerged);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.ConflictingUnMerged);
		assertFalse(_buildPropertiesResource.getGenerator().needsGeneration());

		// Check that this is not possible to write a file
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDisk.getFilesToWrite().size(), 0);

		// Try to do it anyway
		Vector<AbstractCGFile> writeThis = new Vector<AbstractCGFile>();
		writeThis.add(_buildPropertiesResource.getCGFile());
		writeToDisk.setFilesToWrite(writeThis);
		assertFalse(writeToDisk.doAction().hasActionExecutionSucceeded());
		// It's already been said: it's a conflict, it won't work
		// assertTrue(writeToDisk.getThrownException() instanceof UnresolvedConflictException);
		// Now, look at that conflict
		// No changes are conflicting in generation merge
		Merge generationMerge = _buildPropertiesResource.getGeneratedResourceData().getGenerationMerge();
		assertFalse(generationMerge.isReallyConflicting());
		// No changes are conflicting in result file merge
		Merge resultFileMerge = _buildPropertiesResource.getGeneratedResourceData().getResultFileMerge();
		assertFalse(_buildPropertiesResource.getGeneratedResourceData().getResultFileMerge().isReallyConflicting());

		// Look if declared changes are conform
		assertEquals(generationMerge.getChanges().size(), 2);
		assertChange(generationMerge.getChanges().get(0), MergeChangeSource.Right, MergeChangeType.Modification, 0, 0, 0, 0, 0, 0);
		assertChange(generationMerge.getChanges().get(1), MergeChangeSource.Left, MergeChangeType.Modification, 3, 4, 3, 4, 3, 4);
		assertEquals(resultFileMerge.getChanges().size(), 2);
		assertChange(resultFileMerge.getChanges().get(0), MergeChangeSource.Left, MergeChangeType.Modification, 3, 4, 3, 4, 3, 4);
		assertChange(resultFileMerge.getChanges().get(1), MergeChangeSource.Right, MergeChangeType.Modification, 8, 10, 8, 10, 8, 10);

		// Mark as merged
		MarkAsMerged markAsMerged = MarkAsMerged.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(markAsMerged.doAction().hasActionExecutionSucceeded());

		// Check that this is now possible to write a file
		WriteModifiedGeneratedFiles writeToDiskNow = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDiskNow.getFilesToWrite().size(), 1);
		assertTrue(writeToDiskNow.doAction().hasActionExecutionSucceeded());

		// Check that this has been successfully written
		try {
			DiffSource diffSource = new DiffSource(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()));
			assertTrue(diffSource.tokenValueAt(3).indexOf("*3") == 0);
			assertTrue(diffSource.tokenValueAt(4).indexOf("*4") == 0);
			System.out.println(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()));
			System.out.println(diffSource.tokenValueAt(8));
			assertTrue(diffSource.tokenValueAt(8).indexOf("#8") == 0);
			assertTrue(diffSource.tokenValueAt(9).indexOf("#9") == 0);
			assertTrue(diffSource.tokenValueAt(10).indexOf("#10") == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		checkThatAllFilesAreUpToDate();

	}

	/**
	 * Check that automatic merging when there a conflict is working
	 */
	public void test8TestMergeWithGenerationConflict() {
		log("test8TestMergeWithGenerationConflict");

		_buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE, codeRepository.getName()
				+ ".BUILD_PROPERTIES");

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		CGTemplate newBuildPropertiesTemplate = null;

		// Retrieve custom template
		// And modify this template by prepending line nb on line 8 and 9
		try {
			final CGTemplate editedBuildPropertiesTemplate = projectGenerator.getTemplateLocator().templateWithName(BUILDPROPERTIES_VM);
			EditCustomTemplateFile editTemplate = EditCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
				@Override
				public String getEditedContent() {
					return tagStringWithLineNb(editedBuildPropertiesTemplate.getContent(), "*", 8, 10);
				}

				@Override
				public void setEditedContent(String content) {
				}
			});
			editTemplate.doAction();
			SaveCustomTemplateFile saveTemplate = SaveCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			saveTemplate.doAction();
			newBuildPropertiesTemplate = editedBuildPropertiesTemplate;
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		// In this case, DefaultApplication.conf must be marked as modified
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, _buildPropertiesResource.getCGFile());

		// And depending on template
		checkDependingOnTemplate(newBuildPropertiesTemplate, _buildPropertiesResource.getCGFile());

		// Check that file is to regenerate and generator also to run again
		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// Status of file must be ConflictingUnMerged : this is a generation conflict !!!
		// Because we have modified the generation in an area that was scheduled to
		// Be changed in the generation (diff between last generated and last accepted)
		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.ConflictingUnMerged);
		assertFalse(_buildPropertiesResource.getGenerator().needsGeneration());

		// Check that this is not possible to write a file
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDisk.getFilesToWrite().size(), 0);

		// Try to do it anyway
		Vector<AbstractCGFile> writeThis = new Vector<AbstractCGFile>();
		writeThis.add(_buildPropertiesResource.getCGFile());
		writeToDisk.setFilesToWrite(writeThis);
		assertFalse(writeToDisk.doAction().hasActionExecutionSucceeded());
		// It's already been said: it's a conflict, it won't work
		// assertTrue(writeToDisk.getThrownException() instanceof UnresolvedConflictException);

		// Now, look at that conflict
		// One change is conflicting in generation merge
		Merge generationMerge = _buildPropertiesResource.getGeneratedResourceData().getGenerationMerge();
		assertTrue(generationMerge.isReallyConflicting());
		// No changes are conflicting in result file merge
		Merge resultFileMerge = _buildPropertiesResource.getGeneratedResourceData().getResultFileMerge();
		assertFalse(_buildPropertiesResource.getGeneratedResourceData().getResultFileMerge().isReallyConflicting());

		// Look if declared changes are conform
		assertEquals(generationMerge.getChanges().size(), 2);
		assertChange(generationMerge.getChanges().get(0), MergeChangeSource.Right, MergeChangeType.Modification, 0, 0, 0, 0, 0, 0);
		assertChange(generationMerge.getChanges().get(1), MergeChangeSource.Conflict, MergeChangeType.Modification, 8, 10, 8, 10, 8, 10);
		assertEquals(resultFileMerge.getChanges().size(), 1);
		assertChange(resultFileMerge.getChanges().get(0), MergeChangeSource.Left, MergeChangeType.Modification, 8, 10, 8, 10, 8, 10);

		// Select both, right first
		generationMerge.getChanges().get(1).setMergeChangeAction(MergeChangeAction.ChooseBothRightFirst);
		// Result file merge must have changed also
		assertEquals(resultFileMerge.getChanges().size(), 1);
		assertChange(resultFileMerge.getChanges().get(0), MergeChangeSource.Left, MergeChangeType.Addition, 11, 13, 11, 10, 11, 10);

		// Mark as merged
		MarkAsMerged markAsMerged = MarkAsMerged.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(markAsMerged.doAction().hasActionExecutionSucceeded());

		// Check that this is now possible to write a file
		WriteModifiedGeneratedFiles writeToDiskNow = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDiskNow.getFilesToWrite().size(), 1);
		assertTrue(writeToDiskNow.doAction().hasActionExecutionSucceeded());

		// Check that this has been successfully written
		try {
			DiffSource diffSource = new DiffSource(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()));
			assertTrue(diffSource.tokenValueAt(3).indexOf("*3") == 0);
			assertTrue(diffSource.tokenValueAt(4).indexOf("*4") == 0);
			assertTrue(diffSource.tokenValueAt(8).indexOf("#8") == 0);
			assertTrue(diffSource.tokenValueAt(9).indexOf("#9") == 0);
			assertTrue(diffSource.tokenValueAt(10).indexOf("#10") == 0);
			assertTrue(diffSource.tokenValueAt(11).indexOf("*8") == 0);
			assertTrue(diffSource.tokenValueAt(12).indexOf("*9") == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		checkThatAllFilesAreUpToDate();

	}

	/**
	 * Check that automatic merging when there a conflict is working
	 */
	public void test9TestMergeWithConflict() {
		log("test9TestMergeWithConflict");

		_buildPropertiesResource = (BuildPropertiesResource) _project.resourceForKey(ResourceType.TEXT_FILE, codeRepository.getName()
				+ ".BUILD_PROPERTIES");

		// And now we must wait for some time (mimimal is FlexoFileResource.ACCEPTABLE_FS_DELAY)
		logger.info("Waiting " + (FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000) + " ms");
		try {
			Thread.sleep(FlexoFileResource.ACCEPTABLE_FS_DELAY + 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("OK, it should be ok now");

		CGTemplate newBuildPropertiesTemplate = null;

		// Retrieve custom template
		// And modify this template by prepending line nb on line 5 and 6
		try {
			final CGTemplate editedBuildPropertiesTemplate = projectGenerator.getTemplateLocator().templateWithName(BUILDPROPERTIES_VM);
			EditCustomTemplateFile editTemplate = EditCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
				@Override
				public String getEditedContent() {
					return tagStringWithLineNb(editedBuildPropertiesTemplate.getContent(), "*", 5, 7);
				}

				@Override
				public void setEditedContent(String content) {
				}
			});
			editTemplate.doAction();
			SaveCustomTemplateFile saveTemplate = SaveCustomTemplateFile.actionType.makeNewAction(
					(CGTemplateFile) editedBuildPropertiesTemplate, null, _editor);
			saveTemplate.doAction();
			assertTrue(saveTemplate.hasActionExecutionSucceeded());
			newBuildPropertiesTemplate = editedBuildPropertiesTemplate;
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		// In this case, DefaultApplication.conf must be marked as modified
		checkThatAllFilesAreUpToDateExcept(GenerationStatus.GenerationModified, _buildPropertiesResource.getCGFile());

		// And depending on template
		checkDependingOnTemplate(newBuildPropertiesTemplate, _buildPropertiesResource.getCGFile());

		// Check that file is to regenerate and generator also to run again
		assertTrue(_buildPropertiesResource.getCGFile().getGenerationStatus() == GenerationStatus.GenerationModified);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());

		// Edit file by prepending line nb on line 6 and 7
		EditGeneratedFile editFile = EditGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		editFile.setFileContentEditor(new CGFile.FileContentEditor() {
			@Override
			public String getEditedContentForKey(String contentKey) {
				return tagStringWithLineNb(_buildPropertiesResource.getGeneratedResourceData().getContent(ContentSource.CONTENT_ON_DISK),
						"#", 6, 8);
			}

			@Override
			public void setEditedContent(CGFile file) {
			}
		});
		assertTrue(editFile.doAction().hasActionExecutionSucceeded());

		// logger.info("applicationConfResource.getDiskLastModifiedDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getDiskLastModifiedDate()));
		// logger.info("applicationConfResource.getLastAcceptingDate()="+(new
		// SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(applicationConfResource.getLastAcceptingDate()));

		// Save file
		SaveGeneratedFile saveFile = SaveGeneratedFile.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(saveFile.doAction().hasActionExecutionSucceeded());

		// Status of file must be ConflictingUnMerged

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.ConflictingUnMerged);
		assertTrue(_buildPropertiesResource.getGenerator().needsGeneration());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository, null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		assertEquals(_buildPropertiesResource.getCGFile().getGenerationStatus(), GenerationStatus.ConflictingUnMerged);
		assertFalse(_buildPropertiesResource.getGenerator().needsGeneration());

		// Check that this is not possible to write a file
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDisk.getFilesToWrite().size(), 0);

		// Try to do it anyway
		Vector<AbstractCGFile> writeThis = new Vector<AbstractCGFile>();
		writeThis.add(_buildPropertiesResource.getCGFile());
		writeToDisk.setFilesToWrite(writeThis);
		assertFalse(writeToDisk.doAction().hasActionExecutionSucceeded());
		// It's already been said: it's a conflict, it won't work
		// assertTrue(writeToDisk.getThrownException() instanceof UnresolvedConflictException);

		// Now, look at that conflict
		// No changes are conflicting in generation merge
		Merge generationMerge = _buildPropertiesResource.getGeneratedResourceData().getGenerationMerge();
		assertFalse(generationMerge.isReallyConflicting());
		// No changes are conflicting in result file merge
		Merge resultFileMerge = _buildPropertiesResource.getGeneratedResourceData().getResultFileMerge();
		assertTrue(_buildPropertiesResource.getGeneratedResourceData().getResultFileMerge().isReallyConflicting());

		// Look if declared changes are conform
		assertEquals(generationMerge.getChanges().size(), 3);
		assertChange(generationMerge.getChanges().get(0), MergeChangeSource.Right, MergeChangeType.Modification, 0, 0, 0, 0, 0, 0);
		assertChange(generationMerge.getChanges().get(1), MergeChangeSource.Left, MergeChangeType.Modification, 5, 6, 5, 6, 5, 6);
		assertChange(generationMerge.getChanges().get(2), MergeChangeSource.Right, MergeChangeType.Addition, 8, 7, 8, 7, 8, 10);
		assertEquals(resultFileMerge.getChanges().size(), 1);
		assertChange(resultFileMerge.getChanges().get(0), MergeChangeSource.Conflict, MergeChangeType.Modification, 5, 7, 5, 7, 5, 7);

		// Manually resolve conflict
		resultFileMerge.getChanges().get(0).setMergeChangeAction(MergeChangeAction.ChooseLeft);
		String leftString = resultFileMerge.getChanges().get(0).getMergeChangeResult().merge;
		String[] leftVersion = new DiffSource(leftString).getSignificativeTokens();
		/*for (int i=0; i<leftVersion.length; i++) {
			System.out.println("leftVersion["+i+"]="+leftVersion[i]);
		}*/
		resultFileMerge.getChanges().get(0).setMergeChangeAction(MergeChangeAction.ChooseRight);
		String rightString = resultFileMerge.getChanges().get(0).getMergeChangeResult().merge;
		String[] rightVersion = new DiffSource(rightString).getSignificativeTokens();
		/*for (int i=0; i<rightVersion.length; i++) {
			System.out.println("rightVersion["+i+"]="+rightVersion[i]);
		}*/
		String customHandEdition = leftVersion[0] + "\n" + leftVersion[1] + "\n" + rightVersion[1] + "\n" + rightVersion[2] + "\n";
		// System.out.println("customHandEdition="+customHandEdition);
		resultFileMerge.getChanges().get(0).setCustomHandEdition(customHandEdition);
		resultFileMerge.getChanges().get(0).setMergeChangeAction(MergeChangeAction.CustomEditing);

		// Mark as merged
		MarkAsMerged markAsMerged = MarkAsMerged.actionType.makeNewAction(_buildPropertiesResource.getCGFile(), null, _editor);
		assertTrue(markAsMerged.doAction().hasActionExecutionSucceeded());

		// Check that this is now possible to write a file
		WriteModifiedGeneratedFiles writeToDiskNow = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository, null, _editor);
		assertEquals(writeToDiskNow.getFilesToWrite().size(), 1);
		assertTrue(writeToDiskNow.doAction().hasActionExecutionSucceeded());

		// Check that this has been successfully written
		try {
			// System.out.println("fileContents="+FileUtils.fileContents(applicationConfResource.getResourceFile().getFile()));
			DiffSource diffSource = new DiffSource(FileUtils.fileContents(_buildPropertiesResource.getResourceFile().getFile()));
			assertTrue(diffSource.tokenValueAt(3).indexOf("*3") == 0);
			assertTrue(diffSource.tokenValueAt(4).indexOf("*4") == 0);
			assertTrue(diffSource.tokenValueAt(5).indexOf("*5") == 0);
			assertTrue(diffSource.tokenValueAt(6).indexOf("*6") == 0);
			assertTrue(diffSource.tokenValueAt(7).indexOf("#6") == 0);
			assertTrue(diffSource.tokenValueAt(8).indexOf("#7") == 0);
			assertTrue(diffSource.tokenValueAt(9).indexOf("#8") == 0);
			assertTrue(diffSource.tokenValueAt(10).indexOf("#9") == 0);
			assertTrue(diffSource.tokenValueAt(11).indexOf("#10") == 0);
			assertTrue(diffSource.tokenValueAt(12).indexOf("*8") == 0);
			assertTrue(diffSource.tokenValueAt(13).indexOf("*9") == 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}

		checkThatAllFilesAreUpToDate();

		saveProject();
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		resetVariables();
		buildPropertiesResource = null;
	}

}
