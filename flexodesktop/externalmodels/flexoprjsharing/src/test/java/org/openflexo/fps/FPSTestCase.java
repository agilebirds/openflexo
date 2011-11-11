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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.rm.FlexoProcessResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.wkf.WKFElementType;
import org.openflexo.foundation.wkf.action.AddSubProcess;
import org.openflexo.foundation.wkf.action.DropWKFElement;
import org.openflexo.foundation.wkf.action.OpenOperationLevel;
import org.openflexo.foundation.wkf.action.SetAndOpenOperationComponent;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;

public abstract class FPSTestCase extends FlexoTestCase {

	protected static final String TEST_SUB_PROCESS = "TestSubProcess";
	protected static final String TEST_SUB_PROCESS_2 = "TestSubProcess2";
	protected static final String TEST_SUB_PROCESS_3 = "TestSubProcess3";
	protected static final String TEST_SUB_PROCESS_4 = "TestSubProcess4";
	protected static final String TEST_OPERATION_NODE_1 = "TestOperation1";
	protected static final String TEST_OPERATION_NODE_2 = "TestOperation2";
	protected static final String TEST_OPERATION_NODE_3 = "TestOperation3";
	protected static final String TEST_OPERATION_NODE_4 = "TestOperation4";
	protected static final String OPERATION_COMPONENT_1 = "Operation1";
	protected static final String OPERATION_COMPONENT_2 = "Operation2";
	protected static final String OPERATION_COMPONENT_3 = "Operation3";
	protected static final String TAB_COMPONENT1 = "Tab1";
	protected static final String TAB_COMPONENT2 = "Tab2";

	public FPSTestCase(String name) {
		super(name);
	}

	protected static final Logger logger = Logger.getLogger(FPSTestCase.class.getPackage().getName());

	protected static void savePrj(FlexoProject project) {
		try {
			project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}

	/**
	 * Creates a new project in a temp directory
	 */
	public static FlexoEditor createFPSProject(String projectName) {
		FlexoProcessResource _rootProcessResource;
		FlexoProcessResource _subProcessResource;
		SubProcessNode _subProcessNode;
		OperationNode _operationNode;
		FlexoOperationComponentResource _operationComponentResource1;
		IEOperationComponent _operationComponent1;
		IEBlocWidget _bloc2;
		TabComponentDefinition _tab1;
		TabComponentDefinition _tab2;
		FlexoOperationComponentResource _operationComponentResource2;
		IEOperationComponent _operationComponent2;
		FlexoOperationComponentResource _operationComponentResource3;
		IEOperationComponent _operationComponent3;

		FlexoEditor editor;
		File projectDirectory = null;
		String projectIdentifier;

		log("test0CreateProject");
		try {
			File tempFile = File.createTempFile(projectName, "");
			projectDirectory = new File(tempFile.getParentFile(), tempFile.getName() + ".prj");
			tempFile.delete();
		} catch (IOException e) {
			fail();
		}
		logger.info("Project directory: " + projectDirectory.getAbsolutePath());
		projectIdentifier = projectDirectory.getName().substring(0, projectDirectory.getName().length() - 4);
		logger.info("Project identifier: " + projectIdentifier);
		editor = FlexoResourceManager.initializeNewProject(projectDirectory, EDITOR_FACTORY, null);
		FlexoProject project = editor.getProject();
		logger.info("Project has been SUCCESSFULLY created");

		assertNotNull(_rootProcessResource = project.getFlexoProcessResource(projectIdentifier));

		AddSubProcess action = AddSubProcess.actionType.makeNewAction(_rootProcessResource.getFlexoProcess(), null, editor);
		action.setParentProcess(_rootProcessResource.getFlexoProcess());
		action.setNewProcessName(TEST_SUB_PROCESS);
		action.doAction();
		logger.info("SubProcess " + action.getNewProcess().getName() + " successfully created");
		_subProcessResource = project.getFlexoProcessResource(TEST_SUB_PROCESS);
		assertNotNull(_subProcessResource);

		DropWKFElement dropSubProcessNode = DropWKFElement.actionType.makeNewAction(_rootProcessResource.getFlexoProcess()
				.getActivityPetriGraph(), null, editor);
		dropSubProcessNode.setElementType(WKFElementType.MULTIPLE_INSTANCE_PARALLEL_SUB_PROCESS_NODE);
		dropSubProcessNode.setParameter(DropWKFElement.SUB_PROCESS, _subProcessResource.getFlexoProcess());
		dropSubProcessNode.setLocation(100, 100);
		dropSubProcessNode.doAction();
		assertTrue(dropSubProcessNode.hasActionExecutionSucceeded());
		_subProcessNode = (SubProcessNode) dropSubProcessNode.getObject();
		logger.info("SubProcessNode " + _subProcessNode.getName() + " successfully created");
		savePrj(project);

		OpenOperationLevel openOperationLevel = OpenOperationLevel.actionType.makeNewAction(_subProcessNode, null, editor);
		openOperationLevel.doAction();
		DropWKFElement dropOperation = DropWKFElement.actionType.makeNewAction(_subProcessNode.getOperationPetriGraph(), null, editor);
		dropOperation.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation.setLocation(100, 100);
		dropOperation.doAction();
		assertTrue(dropOperation.hasActionExecutionSucceeded());
		_operationNode = (OperationNode) dropOperation.getObject();
		_operationNode.setName(TEST_OPERATION_NODE_1);
		logger.info("OperationNode " + _operationNode.getName() + " successfully created");
		SetAndOpenOperationComponent setOperationComponent = SetAndOpenOperationComponent.actionType.makeNewAction(_operationNode, null,
				editor);
		setOperationComponent.setNewComponentName(OPERATION_COMPONENT_1);
		setOperationComponent.doAction();
		assertTrue(setOperationComponent.hasActionExecutionSucceeded());
		_operationComponentResource1 = project.getFlexoOperationComponentResource(OPERATION_COMPONENT_1);
		assertNotNull(_operationComponentResource1);
		savePrj(project);

		_operationComponent1 = _operationComponentResource1.getIEOperationComponent();
		assertNotNull(_operationComponent1);

		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_operationComponent1, 0, editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_operationComponent1, 1, editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		_bloc2 = (IEBlocWidget) dropBloc2.getDroppedWidget();
		assertNotNull(_bloc2);
		_bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_operationComponent1, 1, editor);
		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc3 = (IEBlocWidget) dropBloc3.getDroppedWidget();
		assertNotNull(bloc3);
		bloc3.setTitle("Bloc3");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(_bloc2, editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);

		// Drop a label in the table, at cell (0,0) at position 0
		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table, WidgetType.LABEL, 0, 0, 0, editor);
		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());

		// Drop a TextField in the table, at cell (0,1) at position 0
		DropIEElement dropTF = DropIEElement.insertWidgetInTable(table, WidgetType.TEXTFIELD, 0, 1, 0, editor);
		assertTrue(dropTF.doAction().hasActionExecutionSucceeded());

		// Now, drop a TabsContainer
		DropIEElement dropTabs = DropIEElement.createTabsInComponent(_operationComponent1, 3, editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabs = (IESequenceTab) dropTabs.getDroppedWidget();

		FlexoComponentFolder rootFolder = project.getFlexoComponentLibrary().getRootFolder();

		// Put Tab1 inside
		AddTab addTab1 = AddTab.actionType.makeNewAction(tabs, null, editor);
		addTab1.setFolder(rootFolder);
		addTab1.setTabTitle("Tab1Title");
		addTab1.setTabName(TAB_COMPONENT1);
		addTab1.setTabContainer(tabs);
		assertTrue(addTab1.doAction().hasActionExecutionSucceeded());
		_tab1 = addTab1.getTabDef();

		// Put Tab2 inside
		AddTab addTab2 = AddTab.actionType.makeNewAction(tabs, null, editor);
		addTab2.setFolder(rootFolder);
		addTab2.setTabTitle("Tab2Title");
		addTab2.setTabName(TAB_COMPONENT2);
		addTab2.setTabContainer(tabs);
		assertTrue(addTab2.doAction().hasActionExecutionSucceeded());
		_tab2 = addTab2.getTabDef();

		AddComponent addComponent = AddComponent.actionType.makeNewAction(project.getFlexoComponentLibrary(), null, editor);
		addComponent.setNewComponentName(OPERATION_COMPONENT_2);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_operationComponentResource2 = project.getFlexoOperationComponentResource(OPERATION_COMPONENT_2);
		assertNotNull(_operationComponentResource2);

		_operationComponent2 = _operationComponentResource2.getIEOperationComponent();
		assertNotNull(_operationComponent2);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1InOp2 = DropIEElement.createBlocInComponent(_operationComponent2, 0, editor);
		assertTrue(dropBloc1InOp2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1InOp2 = (IEBlocWidget) dropBloc1InOp2.getDroppedWidget();
		assertNotNull(bloc1InOp2);
		bloc1InOp2.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabsInOp2 = DropIEElement.createTabsInComponent(_operationComponent2, 1, editor);
		assertTrue(dropTabsInOp2.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabsInOp2 = (IESequenceTab) dropTabsInOp2.getDroppedWidget();

		// Put Tab1 inside
		AddTab addTab1InTabs = AddTab.actionType.makeNewAction(tabsInOp2, null, editor);
		addTab1InTabs.setFolder(rootFolder);
		addTab1InTabs.setTabTitle("Tab1Title");
		addTab1InTabs.setTabDef(_tab1);
		addTab1InTabs.setTabContainer(tabsInOp2);
		assertTrue(addTab1InTabs.doAction().hasActionExecutionSucceeded());

		AddComponent addComponent3 = AddComponent.actionType.makeNewAction(project.getFlexoComponentLibrary(), null, editor);
		addComponent3.setNewComponentName(OPERATION_COMPONENT_3);
		addComponent3.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent3.doAction();
		assertTrue(addComponent3.hasActionExecutionSucceeded());
		_operationComponentResource3 = project.getFlexoOperationComponentResource(OPERATION_COMPONENT_3);
		assertNotNull(_operationComponentResource3);

		_operationComponent3 = _operationComponentResource3.getIEOperationComponent();
		assertNotNull(_operationComponent3);
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1InOp3 = DropIEElement.createBlocInComponent(_operationComponent3, 0, editor);
		assertTrue(dropBloc1InOp3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1InOp3 = (IEBlocWidget) dropBloc1InOp3.getDroppedWidget();
		assertNotNull(bloc1InOp3);
		bloc1InOp3.setTitle("NewBloc");

		// Now, drop a TabsContainer
		DropIEElement dropTabsInOp3 = DropIEElement.createTabsInComponent(_operationComponent3, 1, editor);
		assertTrue(dropTabsInOp3.doAction().hasActionExecutionSucceeded());
		IESequenceTab tabsInOp3 = (IESequenceTab) dropTabsInOp3.getDroppedWidget();

		// Put Tab2 inside
		AddTab addTab2InOp3 = AddTab.actionType.makeNewAction(tabsInOp3, null, editor);
		addTab2InOp3.setFolder(rootFolder);
		addTab2InOp3.setTabTitle("Tab2Title");
		addTab2InOp3.setTabDef(_tab2);
		addTab2InOp3.setTabContainer(tabsInOp3);
		assertTrue(addTab2InOp3.doAction().hasActionExecutionSucceeded());

		DropWKFElement addActivity = DropWKFElement.actionType.makeNewAction(_subProcessResource.getFlexoProcess().getActivityPetriGraph(),
				null, editor);
		addActivity.setElementType(WKFElementType.NORMAL_ACTIVITY);
		addActivity.setLocation(100, 100);
		addActivity.doAction();
		assertTrue(addActivity.hasActionExecutionSucceeded());
		ActivityNode activityNode = (ActivityNode) addActivity.getObject();
		logger.info("ActivityNode " + activityNode.getName() + " successfully created");

		OpenOperationLevel openOperationLevelInSubProcess = OpenOperationLevel.actionType.makeNewAction(activityNode, null, editor);
		openOperationLevelInSubProcess.doAction();

		DropWKFElement dropOperation2 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, editor);
		dropOperation2.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation2.setLocation(10, 50);
		dropOperation2.doAction();
		assertTrue(dropOperation2.hasActionExecutionSucceeded());
		OperationNode operationNode2 = (OperationNode) dropOperation2.getObject();
		operationNode2.setName(TEST_OPERATION_NODE_2);
		logger.info("OperationNode " + operationNode2.getName() + " successfully created");

		SetAndOpenOperationComponent setOperationComponent2 = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode2, null,
				editor);
		setOperationComponent2.setNewComponentName(OPERATION_COMPONENT_2);
		setOperationComponent2.doAction();
		assertTrue(setOperationComponent2.hasActionExecutionSucceeded());

		DropWKFElement dropOperation3 = DropWKFElement.actionType.makeNewAction(activityNode.getOperationPetriGraph(), null, editor);
		dropOperation3.setElementType(WKFElementType.NORMAL_OPERATION);
		dropOperation3.setLocation(100, 50);
		dropOperation3.doAction();
		assertTrue(dropOperation3.hasActionExecutionSucceeded());
		OperationNode operationNode3 = (OperationNode) dropOperation3.getObject();
		operationNode3.setName(TEST_OPERATION_NODE_3);
		logger.info("OperationNode " + operationNode3.getName() + " successfully created");

		SetAndOpenOperationComponent setOperationComponent3 = SetAndOpenOperationComponent.actionType.makeNewAction(operationNode3, null,
				editor);
		setOperationComponent3.setNewComponentName(OPERATION_COMPONENT_3);
		setOperationComponent3.doAction();
		assertTrue(setOperationComponent3.hasActionExecutionSucceeded());

		savePrj(project);

		return editor;
	}

}
