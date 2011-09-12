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


import junit.framework.Test;
import junit.framework.TestSuite;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoExceptionHandler;
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
import org.openflexo.foundation.rm.FlexoMemoryResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.FlexoResourceManager.BackwardSynchronizationHook;
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
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.action.SynchronizeRepositoryCodeGeneration;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.ToolBox;

public class TestCG extends CGTestCase {

    public TestCG()
    {
		super(TEST_CG);
	}
    public TestCG(String testName)
    {
		super(testName);
	}
     protected static final Logger logger = Logger.getLogger(TestCG.class.getPackage().getName());

    private static final String TEST_CG = "TestCG";
	private static DebugBackwardSynchronizationHook _bsHook;
	public static Test suite() {
	     final TestSuite suite = new TestSuite("TestSuite for TestCG");
	     suite.addTest(new TestCG("test0CreateProject")); 
	     suite.addTest(new TestCG("test1CheckResources"));
	     suite.addTest(new TestCG("test2CreateSubProcessAndCheckResources"));
	     suite.addTest(new TestCG("test3CreateSubProcessNodeAndCheckResources"));
	     suite.addTest(new TestCG("test4CreateOperationAndCheckResources"));
	     suite.addTest(new TestCG("test5EditOperationComponent1"));
	     suite.addTest(new TestCG("test6CreateOperationComponent2"));
	     suite.addTest(new TestCG("test7CreateOperationComponent3"));
	     suite.addTest(new TestCG("test8CreateSubProcessNodeAndCheckResources"));
	     suite.addTest(new TestCG("test9InitializeCodeGeneration"));
	     suite.addTest(new TestCG("test10ValidateProject"));
	     suite.addTest(new TestCG("test11SynchronizeCodeGeneration"));
	     suite.addTest(new TestCG("test12CheckAllGeneratedResourcesDependancies"));
	     suite.addTest(new TestCG("test13CheckGeneratedResourcesStatus"));
	     suite.addTest(new TestCG("test14CheckOptimisticDependancyCheckingWithDataModel"));
	     suite.addTest(new TestCG("test15CheckOptimisticDependancyCheckingWithProcesses"));
	     suite.addTest(new TestCG("test16CheckOptimisticDependancyCheckingWithComponents"));
	     suite.addTest(new TestCG("test17CheckResourceDynamicDependancyRebuilding"));
	     suite.addTest(new TestCG("test18CheckTemplateDependancies"));
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
	public void test0CreateProject()
	{
		log("test0CreateProject");
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
		_editor = (DefaultFlexoEditor)FlexoResourceManager.initializeNewProject(_projectDirectory,EDITOR_FACTORY,null);
		_project = _editor.getProject();
		logger.info("Project has been SUCCESSFULLY created");
		_bsHook = new DebugBackwardSynchronizationHook();
		FlexoResourceManager.setBackwardSynchronizationHook(_bsHook);
	}

	/**
	 * Check that resources ans dependancies were correctely built
	 */
	public void test1CheckResources()
	{
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

		for (FlexoResource resource : _project.getResources().values()) {
			if (resource != _rmResource && !(resource instanceof FlexoMemoryResource)) assertSynchonized(resource, _rmResource);
		}
		assertSynchonized (_dmResource,_executionModelResource);
		assertSynchonized (_dmResource,_eoPrototypesResource);

		assertSynchonized (_wkfResource,_rootProcessResource);

		assertDepends (_rootProcessResource,_dmResource);
		assertNotDepends (_rootProcessResource,_clResource);

		logger.info("Resources are WELL created and DEPENDANCIES checked");

		for (FlexoResource resource : _project.getResources().values()) {
			if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource)resource);
			}
		}

		logger.info("Resources are marked as NOT MODIFIED");

	}

	/**
	 * Add a new sub-process and check resource dependancies
	 */
	public void test2CreateSubProcessAndCheckResources()
	{
		log("test2CreateSubProcessAndCheckResources");
		AddSubProcess action = AddSubProcess.actionType.makeNewAction(_rootProcessResource.getFlexoProcess(), null, _editor);
		action.setParentProcess(_rootProcessResource.getFlexoProcess());
		action.setNewProcessName(TEST_SUB_PROCESS);
		action.doAction();
		logger.info("SubProcess "+action.getNewProcess().getName()+" successfully created");
		_subProcessResource = _project.getFlexoProcessResource(TEST_SUB_PROCESS);
		assertNotNull(_subProcessResource);
		assertSynchonized(_subProcessResource,_rmResource);
		assertSynchonized(_subProcessResource,_wkfResource);
		assertDepends(_subProcessResource,_dmResource);
		assertNotDepends(_subProcessResource,_clResource);
		for (FlexoResource resource : _project.getResources().values()) {
			if (resource == _rmResource) {
				assertModified(_rmResource);
			}
			else if (resource == _dmResource) {
				assertModified(_dmResource);
			}
			else if (resource == _wkfResource) {
				assertModified(_wkfResource);
			}
			else if (resource == _rootProcessResource) {
				assertModified(_rootProcessResource);
			}
			else if (resource == _subProcessResource) {
				assertModified(_subProcessResource);
			}
			else if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource)resource);
			}
		}
	}

	/**
	 * Add a new sub-process node, check dependancies
	 */
	public void test3CreateSubProcessNodeAndCheckResources()
	{
		log("test3CreateSubProcessNodeAndCheckResources");
		DropWKFElement action = DropWKFElement.actionType.makeNewAction(_rootProcessResource.getFlexoProcess().getActivityPetriGraph(), null, _editor);
		action.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		action.setParameter(DropWKFElement.SUB_PROCESS, _subProcessResource.getFlexoProcess());
		action.setLocation(100,100);
		action.doAction();
		assertTrue(action.hasActionExecutionSucceeded());
		_subProcessNode = (SubProcessNode)action.getObject();
		_subProcessNode.setName(TEST_SUB_PROCESS_NODE);
		logger.info("SubProcessNode "+_subProcessNode.getName()+" successfully created");
		assertDepends(_rootProcessResource,_subProcessResource);
		saveProject();
	}

	/**
	 * Open operation level, drop a new operation, and associate it a new operation component
	 */
	public void test4CreateOperationAndCheckResources()
	{
		log("test4CreateOperationAndCheckResources");
		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(_subProcessNode, null, _editor);
		openOperationLevel.doAction();
		DropWKFElement dropOperation = DropWKFElement.actionType.makeNewAction(_subProcessNode.getOperationPetriGraph(), null, _editor);
		dropOperation.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation.setLocation(100,100);
		dropOperation.doAction();
		assertTrue(dropOperation.hasActionExecutionSucceeded());
		_operationNode = (OperationNode)dropOperation.getObject();
		_operationNode.setName(TEST_OPERATION_NODE_1);
		logger.info("OperationNode "+_operationNode.getName()+" successfully created");
		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(_operationNode, null, _editor);
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
	public void test5EditOperationComponent1()
	{
		log("test5EditOperationComponent1");
		_operationComponent1 = _operationComponentResource1.getIEOperationComponent();
		assertNotNull(_operationComponent1);

		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_operationComponent1, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget)dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_operationComponent1, 1, _editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		_bloc2 = (IEBlocWidget)dropBloc2.getDroppedWidget();
		assertNotNull(_bloc2);
		_bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_operationComponent1, 1, _editor);
		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc3 = (IEBlocWidget)dropBloc3.getDroppedWidget();
		assertNotNull(bloc3);
		bloc3.setTitle("Bloc3");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(_bloc2, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget)dropTable.getDroppedWidget();
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
		IESequenceTab tabs = (IESequenceTab)dropTabs.getDroppedWidget();

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
	public void test6CreateOperationComponent2()
	{
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
		IEBlocWidget bloc1 = (IEBlocWidget)dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent2, 1, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab)dropTabs.getDroppedWidget();

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
	public void test7CreateOperationComponent3()
	{
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
		IEBlocWidget bloc1 = (IEBlocWidget)dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent3, 1, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab)dropTabs.getDroppedWidget();

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
	public void test8CreateSubProcessNodeAndCheckResources()
	{
		log("test8CreateSubProcessNodeAndCheckResources");
		DropWKFElement addActivity = DropWKFElement.actionType.makeNewAction(_subProcessResource.getFlexoProcess().getActivityPetriGraph(), null, _editor);
		addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
		addActivity.setLocation(100,100);
		addActivity.doAction();
		assertTrue(addActivity.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode)addActivity.getObject();
		activityNode.setName(TEST_ACTIVITY_IN_SUB_PROCESS);
		logger.info("ActivityNode "+activityNode.getName()+" successfully created");

		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
		openOperationLevel.doAction();

		DropWKFElement dropOperation2 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation2.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation2.setLocation(10,50);
		dropOperation2.doAction();
		assertTrue(dropOperation2.hasActionExecutionSucceeded());
		OperationNode operationNode2 = (OperationNode)dropOperation2.getObject();
		operationNode2.setName(TEST_OPERATION_NODE_2);
		logger.info("OperationNode "+operationNode2.getName()+" successfully created");

		SetAndOpenOperationComponent setOperationComponent2
		= SetAndOpenOperationComponent.actionType.makeNewAction(operationNode2, null, _editor);
		setOperationComponent2.setNewComponentName(OPERATION_COMPONENT_2);
		setOperationComponent2.doAction();
		assertTrue(setOperationComponent2.hasActionExecutionSucceeded());
		DropWKFElement dropOperation3 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
		dropOperation3.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation3.setLocation(100,50);
		dropOperation3.doAction();
		assertTrue(dropOperation3.hasActionExecutionSucceeded());
		OperationNode operationNode3 = (OperationNode)dropOperation3.getObject();
		operationNode3.setName(TEST_OPERATION_NODE_3);
		logger.info("OperationNode "+operationNode3.getName()+" successfully created");

		SetAndOpenOperationComponent setOperationComponent3
		= SetAndOpenOperationComponent.actionType.makeNewAction(operationNode3, null, _editor);
		setOperationComponent3.setNewComponentName(OPERATION_COMPONENT_3);
		setOperationComponent3.doAction();
		assertTrue(setOperationComponent3.hasActionExecutionSucceeded());
		saveProject();
	}


	/**
	 * Reload project, Initialize code generation
	 */
	public void test9InitializeCodeGeneration()
	{
		log("test9InitializeCodeGeneration");
		reloadProject(true);
		// Save RM for eventual back-synchro to be saved
		saveProject();
		logger.info("Done. Now check that no other back-synchro");
		// Let eventual dependancies back-synchronize together
		reloadProject(true); // This time, all must be not modified
		for (FlexoResource resource : _project.getResources().values()) {
			if (resource instanceof FlexoStorageResource) {
				assertNotModified((FlexoStorageResource)resource);
			}
		}
		File directory = new File(_projectDirectory.getParentFile(),"GeneratedCodeFor"+_project.getProjectName());
		directory.mkdirs();

		createDefaultGCRepository();
		codeRepository.setTargetType(CodeType.PROTOTYPE);
	}


	/**
	 * Reload project, Initialize code generation
	 */
	public void test10ValidateProject()
	{
		log("test10ValidateProject");

		_editor.registerExceptionHandlerFor(ValidateProject.actionType,new FlexoExceptionHandler<ValidateProject>() {
			@Override
            public boolean handleException(FlexoException exception, ValidateProject action) {
				if (action.getIeValidationReport() != null && action.getIeValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from IE:\n"+action.getIeValidationReport().reportAsString());
				}
				if (action.getWkfValidationReport() != null && action.getWkfValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from WKF:\n"+action.getWkfValidationReport().reportAsString());
				}
				if (action.getDkvValidationReport() != null && action.getDkvValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from DKV:\n"+action.getDkvValidationReport().reportAsString());
				}
				if (action.getDmValidationReport() != null && action.getDmValidationReport().getErrorNb() > 0) {
					logger.info("Errors reported from DM:\n"+action.getDmValidationReport().reportAsString());
				}
				return true;
			}
		});

		ValidateProject validateProject = ValidateProject.actionType.makeNewAction(codeRepository, null, _editor);
		validateProject.doAction();

		// First project is not valid
		assertFalse(validateProject.isProjectValid());

		// First project is not valid
		/*try {
		assertFalse(validateProject.isProjectValid());
		}
		catch (AssertionFailedError e) {
		}
		fail();*/


		// Try to fix errors (GPO: this is not required anymore, prefix is always set on root folder unless done otherwise explicitly)
		FlexoComponentFolder rootFolder = _project.getFlexoComponentLibrary().getRootFolder();
		//rootFolder.setComponentPrefix("TST");

		// To fix errors we need another process and operation on which we will bind the menu
		AddSubProcess process = AddSubProcess.actionType.makeNewAction(_project.getFlexoWorkflow(), null, _editor);
		process.setNewProcessName("Process context free");
		process.doAction();
		DropWKFElement addActivity = DropWKFElement.actionType.makeNewAction(process.getNewProcess().getActivityPetriGraph(), null, _editor);
        addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
        addActivity.setLocation(100,100);
        addActivity.doAction();
        assertTrue(addActivity.hasActionExecutionSucceeded());
        ActivityNode activityNode = (ActivityNode)addActivity.getObject();
        logger.info("ActivityNode "+activityNode.getName()+" successfully created");

        OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(activityNode, null, _editor);
        openOperationLevel.doAction();

        DropWKFElement dropOperation2 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, _editor);
        dropOperation2.setElementType(WKFElementType.NORMAL_OPERATION);
        dropOperation2.setLocation(10,50);
        dropOperation2.doAction();
        assertTrue(dropOperation2.hasActionExecutionSucceeded());

        OperationNode operationNodeForMenu = (OperationNode)dropOperation2.getObject();
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

        logger.info("OperationNode "+operationNodeForMenu.getName()+" successfully created");
		_project.getFlexoNavigationMenu().getRootMenu().setProcess(operationNodeForMenu.getProcess());
		_project.getFlexoNavigationMenu().getRootMenu().setOperation(operationNodeForMenu);

		//now we have to define tabs for operations
		associateTabWithOperations();
		// Project should be without errors now
		validateProject = ValidateProject.actionType.makeNewAction(codeRepository, null, _editor);
		validateProject.doAction();
		System.out.println(validateProject.readableValidationErrors());
		assertTrue(validateProject.isProjectValid());

		saveProject();

	}

	
	/**
	 * Reload project, Initialize code generation
	 */
	public void test11SynchronizeCodeGeneration()
	{
		log("test11SynchronizeCodeGeneration");

		// Synchronize code generation
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null, _editor);
		// Do it even if validation failed
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		if (!synchronizeCodeGeneration.hasActionExecutionSucceeded())
            fail("Synchronization action failed. Action execution status: "+synchronizeCodeGeneration.getExecutionStatus().name());
		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();
        if (!writeToDisk.hasActionExecutionSucceeded())
            fail("Writing to disk has failed. Action execution status: "+writeToDisk.getExecutionStatus().name());
		saveProject();

	}

	/**
	 * Reload project, check all dependancies
	 */
	public void test12CheckAllGeneratedResourcesDependancies()
	{
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
	public void test13CheckGeneratedResourcesStatus()
	{
		log("test13CheckGeneratedResourcesStatus");

		logger.info("Synchronize code generation again");
		// Synchronize code generation again
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		logger.info("Code generation is now synchronized");

		//after a synchronize : all files must normally be up-to-date
		//but : build.properties and Application.conf.PROD depends on the war name (this is really a deploiement parameter, and it IS NOT a model attribute)
		//so we can admit that those files needs to be regenerate since the code repository has changed.
		checkThatAllFilesAreUpToDate();
		//Except(GenerationStatus.GenerationModified,buildPropertiesResource.getCGFile(),appConfProdResource.getCGFile());

	}

	/**
	 * Reload project, perform some optimistic dependancy checking woth data model
	 */
	public void test14CheckOptimisticDependancyCheckingWithDataModel()
	{
		log("test14CheckOptimisticDependancyCheckingWithDataModel");

		// Now we force the DataModel to be changed
		// Normally most of resources depending on it should be marked as changed
		// But because of optimist dependancy checking none will be affected

		logger.info("'Touch' DataModel");

		_project.getDataModel().setChanged();

		checkThatAllFilesAreUpToDate();

		saveProject();

		// Now we force component entity for Operation2 to be changed
		_operationComponent2.getComponentDMEntity().setChanged();

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent2JavaResource.getCGFile(),
				operationComponent2APIResource.getCGFile(),
				operationComponent2WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile());

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());

		// And save project
		saveProject();

		logger.info("'Touch' DMEntity for Tab1");

		// Now we force component entity for Tab1 to be changed
		_tab1.getComponentDMEntity().setChanged();

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent2JavaResource.getCGFile(),
				operationComponent2APIResource.getCGFile(),
				operationComponent2WOResource.getCGFile(),
				tabComponent1JavaResource.getCGFile(),
				tabComponent1APIResource.getCGFile(), 
				tabComponent1WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile());

		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();

		// And save project
		saveProject();

	}

	/**
	 * Perform some optimitic dependancy while editing processes
	 */
	public void test15CheckOptimisticDependancyCheckingWithProcesses()
	{
		log("test15CheckOptimisticDependancyCheckingWithProcesses");

		// Now we force the SubProcess to be changed
		// Normally most of resources depending on it should be marked as changed
		// But because of optimist dependancy checking none will be affected

		logger.info("'Touch' SubProcess "); 
		_subProcessResource.getFlexoProcess().setChanged();

		//try {
		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(),
				subProcessScreenshotCopyOfCopy.getCGFile(),
				activityInSubProcessScreenshotCopyOfCopy.getCGFile(),
				operationNode2ScreenshotCopyOfCopy.getCGFile(),
				operationNode3ScreenshotCopyOfCopy.getCGFile());
		
		/*}
		catch (AssertionFailedError hop) {
			saveProject();
			System.exit(0);
		}*/

		saveProject();

		// Now we force Operation2 to be changed
		OperationNode operationNode2 = _subProcessResource.getFlexoProcess().getActivityPetriGraph().getOperationNodeNamed(TEST_OPERATION_NODE_2);
		operationNode2.setChanged();

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent2JavaResource.getCGFile(),
				operationComponent2APIResource.getCGFile(),
				operationComponent2WOResource.getCGFile(),
				tabComponent1JavaResource.getCGFile(),
				tabComponent1APIResource.getCGFile(),
				tabComponent1WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(),
				subProcessScreenshotCopyOfCopy.getCGFile(),
				activityInSubProcessScreenshotCopyOfCopy.getCGFile(),
				operationNode2ScreenshotCopyOfCopy.getCGFile(),
				operationNode3ScreenshotCopyOfCopy.getCGFile());
		// Run generators for required files
		GenerateSourceCode generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());
		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());

		// And save project
		saveProject();

		// Now we force Operation1 to be changed
		_operationNode.setChanged();

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				tabComponent1JavaResource.getCGFile(),
				tabComponent1APIResource.getCGFile(),
				tabComponent1WOResource.getCGFile(),
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				rootProcessScreenshotCopyOfCopy.getCGFile(),
				subProcessNodeScreenshotInRootProcessCopyOfCopy.getCGFile(),
				operationNode1ScreenshotCopyOfCopy.getCGFile());

		assertTrue(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent1JavaResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent2JavaResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertTrue(tabComponent2WOResource.getCGFile().needsMemoryGeneration());
		assertTrue(cstJavaResource.getCGFile().needsMemoryGeneration());

		// Run generators for required files
		generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());

		assertFalse(operationComponent1JavaResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(operationComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent1JavaResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent1APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent1WOResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent2JavaResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent2APIResource.getCGFile().needsMemoryGeneration());
		assertFalse(tabComponent2WOResource.getCGFile().needsMemoryGeneration());

		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());

		// And save project
		saveProject();

	}

	/**
	 * Perform some optimitic dependancy checking while editing components
	 * Btw, if you find some errors in here or you modify some stuffs here, please report them also in TestRoundtrip.test2
	 */
	public void test16CheckOptimisticDependancyCheckingWithComponents()
	{
		log("test16CheckOptimisticDependancyCheckingWithComponents");

		_operationComponent1 = _operationComponentResource1.getIEOperationComponent();
		assertNotNull(_operationComponent1);

		// On component 1, insert a new bloc at index 3, name it Bloc4
		DropIEElement dropBloc4 = DropIEElement.createBlocInComponent(_operationComponent1, 0, _editor);
		assertTrue(dropBloc4.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc4 = (IEBlocWidget)dropBloc4.getDroppedWidget();
		assertNotNull(bloc4);

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile());
		// Generate content
		GenerateSourceCode generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();

		checkThatAllFilesAreUpToDate();

		bloc4.setTitle("Bloc4");

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile());
		generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());
		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();

		// And save project
		saveProject();


		IETabComponent tab2 = _tab2ComponentResource.getTabComponent();
		assertNotNull(tab2);

		// Insert a new bloc at index 0, name it "BlocInTab2"
		DropIEElement dropBlocInTab2 = DropIEElement.createBlocInComponent(tab2, 0, _editor);
		assertTrue(dropBlocInTab2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc = (IEBlocWidget)dropBlocInTab2.getDroppedWidget();
		assertNotNull(bloc);
		bloc.setTitle("BlocInTab2");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget)dropTable.getDroppedWidget();
		assertNotNull(table);
		// Drop a label in the table, at cell (0,0) at position 0
		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table, WidgetType.LABEL, 0, 0, 0, _editor);
		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());

		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(),
				operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile(),
				operationComponent3ScreenshotCopyOfCopy.getCGFile(),
				tabComponent2ScreenshotCopyOfCopy.getCGFile());

		generateRequiredCode = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequiredCode.doAction().hasActionExecutionSucceeded());
		
		// Write generated files to disk
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();

		checkThatAllFilesAreUpToDate();

		// And save project
		saveProject();

		logger.info("Before modifying");
 		logger.info("_tab2ComponentResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(_tab2ComponentResource.getLastUpdate()));
 		logger.info("tabComponent2JavaResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(tabComponent2JavaResource.getLastUpdate()));

 		// Now we change again bloc name
		bloc.setTitle("BlocInTab2");

		// Naturally those resources are changed
		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(),
				operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(),
				cstJavaResource.getCGFile(),
				rootProcessJSCopy.getCGFile(),
				subProcessJSCopy.getCGFile(),
				operationComponent1ScreenshotCopyOfCopy.getCGFile(),
				operationComponent3ScreenshotCopyOfCopy.getCGFile(),
				tabComponent2ScreenshotCopyOfCopy.getCGFile());

		logger.info("After modifying");
 		logger.info("_tab2ComponentResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(_tab2ComponentResource.getLastUpdate()));
 		logger.info("tabComponent2JavaResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(tabComponent2JavaResource.getLastUpdate()));

		log ("OK, trying to save and reload project");


		// But now we don't write it on disk, just save project
		saveProject();

		// And reload it
		reloadProject(true);
		reloadGeneratedResources();
		// And resynchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

		logger.info("After code synchro");
		logger.info("_tab2ComponentResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(_tab2ComponentResource.getLastUpdate()));
 		logger.info("tabComponent2JavaResource update="+(new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(tabComponent2JavaResource.getLastUpdate()));

		// The same resources must be in the 'modified' state except for the ones that have been cleared because the backsynchronization
		// mechanism has not caused an update in resource dependancy tree (The
		// We test here the persistance of 'Modified' status
		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile(),
				operationComponent3JavaResource.getCGFile(),
				operationComponent3APIResource.getCGFile(),
				operationComponent3WOResource.getCGFile(),
				tabComponent2ScreenshotCopyOfCopy.getCGFile());
	}

	/**
	 * Check that dynamic dependancies rebuilding is working
	 */
	public void test17CheckResourceDynamicDependancyRebuilding()
	{
		log("test17CheckResourceDynamicDependancyRebuilding");

		// Add operation TEST_OPERATION_NODE_4 in root process using OperationComponent2

		DropWKFElement dropOperation = DropWKFElement.actionType.makeNewAction(_subProcessNode.getOperationPetriGraph(), null, _editor);
		dropOperation.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation.setLocation(100,50);
		dropOperation.doAction();
		assertTrue(dropOperation.hasActionExecutionSucceeded());
		OperationNode operationNode4 = (OperationNode)dropOperation.getObject();
		operationNode4.setName(TEST_OPERATION_NODE_4);
		logger.info("OperationNode "+operationNode4.getName()+" successfully created");

		SetAndOpenOperationComponent setOperationComponent
		= SetAndOpenOperationComponent.actionType.makeNewAction(operationNode4, null, _editor);
		setOperationComponent.setNewComponentName(OPERATION_COMPONENT_2);
		setOperationComponent.doAction();
		assertTrue(setOperationComponent.hasActionExecutionSucceeded());

		assertNotDepends(operationComponent2JavaResource, _rootProcessResource);
		assertNotDepends(operationComponent2APIResource, _rootProcessResource);
		assertNotDepends(operationComponent2WOResource, _rootProcessResource);

		//associate a tab with operationNode4
		operationNode4.setTabComponent(_tab1);
		// And resynchronize code generation
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();
		assertTrue(synchronizeCodeGeneration.hasActionExecutionSucceeded());
		// Now, as Operation2 is used in root process, it should depend on it
		assertDepends(operationComponent2JavaResource, _rootProcessResource);
		assertDepends(operationComponent2APIResource, _rootProcessResource);
		assertDepends(operationComponent2WOResource, _rootProcessResource);

		// Write generated files to disk
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();

		saveProject();

	}

	/**
	 * Check that dynamic dependancies rebuilding is working
	 */
	public void test18CheckTemplateDependancies()
	{
		log("test18CheckTemplateDependancies");
		CGTemplate labelHTMLTemplate = null;
		try {
			labelHTMLTemplate = projectGenerator.getTemplateLocator().templateWithName("Label.html.vm");
		} catch (TemplateNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		assertTrue(labelHTMLTemplate.isApplicationTemplate());

		checkDependingOnTemplate(labelHTMLTemplate,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile());

		AddCustomTemplateRepository addCustomTemplateRepository
		= AddCustomTemplateRepository.actionType.makeNewAction(_project.getGeneratedCode().getTemplates(), null, _editor);
		addCustomTemplateRepository.setNewCustomTemplatesRepositoryName("MyCustomTemplates");
		addCustomTemplateRepository.setNewCustomTemplatesRepositoryDirectory(new FlexoProjectFile(_project,"MyCustomTemplates"));
		addCustomTemplateRepository.setRepositoryType(TemplateRepositoryType.Code);
		assertTrue(addCustomTemplateRepository.doAction().hasActionExecutionSucceeded());

		RedefineCustomTemplateFile redefineTemplate
		= RedefineCustomTemplateFile.actionType.makeNewAction(labelHTMLTemplate, null, _editor);
		redefineTemplate.setRepository(addCustomTemplateRepository.getNewCustomTemplatesRepository());
		redefineTemplate.setTarget(CodeType.PROTOTYPE);
		assertTrue(redefineTemplate.doAction().hasActionExecutionSucceeded());

		CGTemplateFile newLabelHTMLTemplate = redefineTemplate.getNewTemplateFile();

		codeRepository.setPreferredTemplateRepository(addCustomTemplateRepository.getNewCustomTemplatesRepository());

		// Resynchronize code generation
		codeRepository.connect();
		SynchronizeRepositoryCodeGeneration synchronizeCodeGeneration = SynchronizeRepositoryCodeGeneration.actionType.makeNewAction(codeRepository, null, _editor);
		synchronizeCodeGeneration.setContinueAfterValidation(true);
		synchronizeCodeGeneration.doAction();

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
		checkThatAllFilesAreUpToDateExcept(
				GenerationStatus.GenerationModified,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile());

		checkDependingOnTemplate(newLabelHTMLTemplate,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile());

		// Generate required files
		logger.info("Generate required file");
		GenerateSourceCode generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// Write generated files to disk
		logger.info("Write required file");
		WriteModifiedGeneratedFiles writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		writeToDisk.doAction();
		logger.info("Write required file DONE");

		checkThatAllFilesAreUpToDate();

		final String ADDED_STRING = "DenisEstBete";

		EditCustomTemplateFile editTemplate
		= EditCustomTemplateFile.actionType.makeNewAction(newLabelHTMLTemplate, null, _editor);
		editTemplate.setTemplateFileContentEditor(new TemplateFileContentEditor() {
			public String getEditedContent() {
				return "<VALUE>"+ADDED_STRING;
			}
			public void setEditedContent(String content) { }
		});
		editTemplate.doAction();

		SaveCustomTemplateFile saveTemplate
		= SaveCustomTemplateFile.actionType.makeNewAction(newLabelHTMLTemplate, null, _editor);
		saveTemplate.doAction();

		checkDependingOnTemplate(newLabelHTMLTemplate,
				tabComponent2JavaResource.getCGFile(),
				tabComponent2APIResource.getCGFile(),
				tabComponent2WOResource.getCGFile(),
				operationComponent1JavaResource.getCGFile(),
				operationComponent1APIResource.getCGFile(),
				operationComponent1WOResource.getCGFile());

		waitForVelocityRefresh();
		
		// Generate required files
		logger.info("Generate required file");
		generateRequired = GenerateSourceCode.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(generateRequired.doAction().hasActionExecutionSucceeded());
		logger.info("Generate required file DONE");

		// Write generated files to disk
		logger.info("Write required file");
		writeToDisk = WriteModifiedGeneratedFiles.actionType.makeNewAction(codeRepository,null, _editor);
		assertTrue(writeToDisk.doAction().hasActionExecutionSucceeded());
		logger.info("Write required file DONE");

		try {
			File tab2_html = new File(tabComponent2WOResource.getFile(),TAB_COMPONENT2+".html");
			assertTrue(FileUtils.fileContents(tab2_html).indexOf(ADDED_STRING) > 0);
			File operation1_html = new File(operationComponent1WOResource.getFile(),OPERATION_COMPONENT_1+".html");
			assertTrue(FileUtils.fileContents(operation1_html).indexOf(ADDED_STRING) > 0);
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		if (_bsHook!=null)
		    _bsHook.clear();
		FlexoResourceManager.setBackwardSynchronizationHook(null);
		resetVariables();
	}
	private class DebugBackwardSynchronizationHook implements BackwardSynchronizationHook
	{
		private class BackSynchroEntry
		{
			protected FlexoResource resource1;
			protected FlexoResource resource2;
			protected BackSynchroEntry(FlexoResource aResource1, FlexoResource aResource2) {
				resource1 = aResource1;
				resource2 = aResource2;
			}
			protected boolean backSynchroConcerns(FlexoResource aResource1, FlexoResource aResource2) {
				return ((resource1 == aResource1) && (resource2 == aResource2));
			}
		}

		private Vector<BackSynchroEntry> entries;

		protected DebugBackwardSynchronizationHook()
		{
			entries = new Vector<BackSynchroEntry>();
			clear();
		}

		protected void clear()
		{
			entries.clear();
		}

		public void notifyBackwardSynchronization(FlexoResource resource1, FlexoResource resource2)
		{
			System.out.println("Resource "+resource1+" is to be back-synchronized with "+resource2);
			//(new Exception()).printStackTrace();
			entries.add(new BackSynchroEntry(resource1,resource2));
		}

		protected void assertBackSynchronizationHasBeenPerformed(FlexoResource aResource1, FlexoResource aResource2)
		{
			for (BackSynchroEntry entry : entries) {
				if (entry.backSynchroConcerns(aResource1, aResource2)) return;
			}
			fail("RESOURCE synchonization problem: "+aResource1+" MUST have been back-synchronized with "+aResource2);
		}

		protected void assertNoBackSynchronization()
		{
			assertBackSynchronizationCount(0);
		}

		protected void assertBackSynchronizationCount(int count)
		{
			assertEquals(getBackSynchronizationCount(), count);
		}

		protected int getBackSynchronizationCount()
		{
			return entries.size();
		}
	}


}