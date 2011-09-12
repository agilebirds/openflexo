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
package org.openflexo.foundation.ie;
import java.io.File;
//import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;

//import org.openflexo.foundation.ie.action.DropIEElement;
//import org.openflexo.foundation.ie.action.DropPartialComponent;
//import org.openflexo.foundation.ie.action.MakePartialComponent;
//import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
//import org.openflexo.foundation.ie.cl.action.AddComponent;
//import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
//import org.openflexo.foundation.ie.util.WidgetType;
//import org.openflexo.foundation.ie.widget.IEBlocWidget;
//import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
//import org.openflexo.foundation.ie.widget.IELabelWidget;
//import org.openflexo.foundation.ie.widget.IEWidget;
//import org.openflexo.foundation.ie.widget.ITableRow;
//import org.openflexo.foundation.rm.FlexoResourceManager;


public class TestReusableComponent extends FlexoIETestCase{

	protected static final Logger logger = Logger.getLogger(TestReusableComponent.class.getPackage().getName());

	private static FlexoEditor _editor;
	private static FlexoProject _project;
	private static File _projectDirectory;
	private static String _projectIdentifier;
	private static FlexoComponentLibrary _cl;
	private static FlexoComponentFolder _cf;
	private static IEOperationComponent _oc;
	private static IEOperationComponent _targetComponent;
	private static final String TEST_REUSABLE_COMPONENT = "TestReusableComponent";
	private static final String TEST_COMPONENT = "TestComponent";
	private static final String PARTIAL_COMPONENT = "PartialTestComponent";
	private static final String PARTIAL_COMPONENT_2 = "Table";
	private static final String PARTIAL_COMPONENT_3 = "Label";
	private static final String PARTIAL_COMPONENT_4 = "Row";
	private static final String TEST_TARGET_COMPONENT = "TestTargetComponent";
    private static final String TEST_COMPONENT_FOLDER = "TestFolder";

    public TestReusableComponent(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject()
	{
		logger.info("TestReusableComponent not maintained");
//        ToolBox.setPlatform();
//        FlexoLoggingManager.forceInitialize();
//		try {
//			File tempFile = File.createTempFile(TEST_REUSABLE_COMPONENT, "");
//			_projectDirectory = new File (tempFile.getParentFile(),tempFile.getName()+".prj");
//			tempFile.delete();
//		} catch (IOException e) {
//			fail();
//		}
//		logger.info("Project directory: "+_projectDirectory.getAbsolutePath());
//		_projectIdentifier = _projectDirectory.getName().substring(0, _projectDirectory.getName().length()-4);
//		logger.info("Project identifier: "+_projectIdentifier);
//		_editor = FlexoResourceManager.initializeNewProject(_projectDirectory,EDITOR_FACTORY);
//		_project = _editor.getProject();
//		logger.info("Project has been SUCCESSFULLY created");
//
//		_cl = _project.getFlexoComponentLibrary();
//		AddComponentFolder addComponentFolder = AddComponentFolder.actionType.makeNewAction(_cl, null, _editor);
//		addComponentFolder.setNewFolderName(TEST_COMPONENT_FOLDER);
//		addComponentFolder.doAction();
//		assertTrue(addComponentFolder.hasActionExecutionSucceeded());
//		_cf = _cl.getRootFolder().getFlexoComponentFolderWithName(TEST_COMPONENT_FOLDER);
//		assertNotNull(_cf);
//		_cl = _project.getFlexoComponentLibrary();
//
//		AddComponent addComponent = AddComponent.actionType.makeNewAction(_cf, null, _editor);
//		addComponent.setNewComponentName(TEST_COMPONENT);
//		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
//		addComponent.doAction();
//		assertTrue(addComponent.hasActionExecutionSucceeded());
//		_oc = _project.getOperationComponent(TEST_COMPONENT);
//		assertNotNull(_oc);
//
//
//
//
//		AddComponent targetComponent = AddComponent.actionType.makeNewAction(_cf, null, _editor);
//		targetComponent.setNewComponentName(TEST_TARGET_COMPONENT);
//		targetComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
//		targetComponent.doAction();
//		assertTrue(targetComponent.hasActionExecutionSucceeded());
//		_targetComponent = _project.getOperationComponent(TEST_TARGET_COMPONENT);
//		assertNotNull(_targetComponent);
//
//
////		 Insert a new bloc at index 0, name it Bloc1
//		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_oc, 0,_editor);
//		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
//		IEBlocWidget bloc1 = (IEBlocWidget)dropBloc1.getDroppedWidget();
//		assertNotNull(bloc1);
//		bloc1.setTitle("Bloc1");
//
//		// Insert a new bloc at index 1, name it Bloc2
//		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_oc, 1,_editor);
//		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
//		IEBlocWidget bloc2 = (IEBlocWidget)dropBloc2.getDroppedWidget();
//		assertNotNull(bloc2);
//		bloc2.setTitle("Bloc2");
//
//		// Insert a new bloc at index 1, name it Bloc3
//		// This bloc is therefore placed between Bloc1 and Bloc2
//		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_oc, 1,_editor);
//		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
//		IEBlocWidget bloc3 = (IEBlocWidget)dropBloc3.getDroppedWidget();
//		assertNotNull(bloc3);
//		bloc3.setTitle("Bloc3");
//
//		DropIEElement dropBloc4 = DropIEElement.createBlocInComponent(_oc, 1,_editor);
//		assertTrue(dropBloc4.doAction().hasActionExecutionSucceeded());
//		IEBlocWidget bloc4 = (IEBlocWidget)dropBloc4.getDroppedWidget();
//		assertNotNull(bloc4);
//		bloc4.setTitle("Bloc4");
//
//
//		// Drop a table in the bloc1
//		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc1,_editor);
//		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table = (IEHTMLTableWidget)dropTable.getDroppedWidget();
//		assertNotNull(table);
//		table.assertIsValid();
//
////		 Drop a table in the bloc2
//		DropIEElement dropTable2 = DropIEElement.createTableInBloc(bloc2,_editor);
//		assertTrue(dropTable2.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table2 = (IEHTMLTableWidget)dropTable2.getDroppedWidget();
//		assertNotNull(table2);
//		table2.assertIsValid();
//
////		 Drop a table in the bloc3
//		DropIEElement dropTable3 = DropIEElement.createTableInBloc(bloc3,_editor);
//		assertTrue(dropTable3.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table3 = (IEHTMLTableWidget)dropTable3.getDroppedWidget();
//		assertNotNull(table3);
//		table3.assertIsValid();
//
////		 Drop a table in the bloc4
//		DropIEElement dropTable4 = DropIEElement.createTableInBloc(bloc4,_editor);
//		assertTrue(dropTable4.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table4 = (IEHTMLTableWidget)dropTable4.getDroppedWidget();
//		assertNotNull(table4);
//
//		ITableRow row = table4.getTR(2);
//		assertNotNull(row);
//		table4.assertIsValid();
//
////		 Drop a label in the table3, at cell (0,0) at position 0
//		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table3, WidgetType.LABEL, 0, 0, 0,_editor);
//		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());
//		IELabelWidget label = (IELabelWidget)dropLabel.getDroppedWidget();
//		assertNotNull(label);
//		table3.assertIsValid();
//		// Save project
//		saveProject();
//
//		//reuse block1
//		MakePartialComponent makePartial = MakePartialComponent.actionType.makeNewAction(bloc1, null, _editor);
//		makePartial.setNewComponentName(PARTIAL_COMPONENT);
//		makePartial.setNewComponentFolder(_cf);
//		assertTrue(makePartial.doAction().hasActionExecutionSucceeded());
//		assertEquals(table.getWOComponent(), bloc1.getWOComponent());
//		assertEquals(table.getWOComponent(), makePartial.getComponent());
//		table.assertIsValid();
//		// reuse table2
//		MakePartialComponent makePartial2 = MakePartialComponent.actionType.makeNewAction(table2, null, _editor);
//		makePartial2.setNewComponentName(PARTIAL_COMPONENT_2);
//		makePartial2.setNewComponentFolder(_cf);
//		assertTrue(makePartial2.doAction().hasActionExecutionSucceeded());
//		assertEquals(table2.getWOComponent(), makePartial2.getComponent());
//		table2.assertIsValid();
//		// reuse label
//		MakePartialComponent makePartial3 = MakePartialComponent.actionType.makeNewAction(label, null, _editor);
//		makePartial3.setNewComponentName(PARTIAL_COMPONENT_3);
//		makePartial3.setNewComponentFolder(_cf);
//		assertTrue(makePartial3.doAction().hasActionExecutionSucceeded());
//		assertEquals(label.getWOComponent(), makePartial3.getComponent());
//		table.assertIsValid();
//
//		// reuse row
//		MakePartialComponent makePartial4 = MakePartialComponent.actionType.makeNewAction((IEWidget)row, null, _editor);
//		makePartial4.setNewComponentName(PARTIAL_COMPONENT_4);
//		makePartial4.setNewComponentFolder(_cf);
//		assertTrue(makePartial4.doAction().hasActionExecutionSucceeded());
//		assertEquals(((IEWidget)row).getWOComponent(), makePartial4.getComponent());
//		table4.assertIsValid();
//
//		// Save project
//		saveProject();
//		ReusableComponentDefinition bloc1Component = (ReusableComponentDefinition)_cl.getComponentNamed(PARTIAL_COMPONENT);
//		assertNotNull(bloc1Component);
//		ReusableComponentDefinition tableComponent = (ReusableComponentDefinition)_cl.getComponentNamed(PARTIAL_COMPONENT_2);
//		assertNotNull(tableComponent);
//		assertEquals(1,((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().size());
//		assertTrue(((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().get(0) instanceof IEHTMLTableWidget);
//		((IEHTMLTableWidget)((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().get(0)).assertIsValid();
//
//		ReusableComponentDefinition labelComponent = (ReusableComponentDefinition)_cl.getComponentNamed(PARTIAL_COMPONENT_3);
//		assertNotNull(labelComponent);
//		ReusableComponentDefinition rowComponent = (ReusableComponentDefinition)_cl.getComponentNamed(PARTIAL_COMPONENT_4);
//		assertNotNull(rowComponent);
//
//		// now that we have created partial components, let's drop them somewhere
//
//		// 1. drop a bloc in the top level
//		DropPartialComponent dropPartial = DropPartialComponent.actionType.makeNewAction(_targetComponent, null, _editor);
//		dropPartial.setPartialComponent(bloc1Component);
//		assertTrue(dropPartial.doAction().hasActionExecutionSucceeded());
//		saveProject();
//
//
//		// 2. drop a table in a bloc
//		// (need to create a new bloc to drop in)
//		DropIEElement dropBloc5 = DropIEElement.createBlocInComponent(_oc, 1,_editor);
//		assertTrue(dropBloc5.doAction().hasActionExecutionSucceeded());
//		IEBlocWidget bloc5 = (IEBlocWidget)dropBloc5.getDroppedWidget();
//		assertNotNull(bloc5);
//		bloc5.setTitle("Bloc5");
//		//let's drop
//		DropPartialComponent dropPartial2 = DropPartialComponent.actionType.makeNewAction(bloc5,null, _editor);
//		dropPartial2.setPartialComponent(tableComponent);
//		assertTrue(dropPartial2.doAction().hasActionExecutionSucceeded());
//		assertEquals(1,((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().size());
//		assertTrue(((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().get(0) instanceof IEHTMLTableWidget);
//		((IEHTMLTableWidget)((IEReusableComponent)tableComponent.getWOComponent()).getRootSequence().get(0)).assertIsValid();
//
//
//		// 2. drop a label in a table
//		// (need to create a new htmltable to drop in)
//		DropIEElement dropTable1 = DropIEElement.createHTMLTableInComponent(_oc, 1);
//		assertTrue(dropTable1.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table5 = (IEHTMLTableWidget)dropTable1.getDroppedWidget();
//		assertNotNull(table5);
//		table5.assertIsValid();
//		//let's drop
//		DropPartialComponent dropPartial3 = DropPartialComponent.actionType.makeNewAction(table5.getTDAt(0, 0).getSequenceWidget(),null, _editor);
//		dropPartial3.setPartialComponent(labelComponent);
//		assertTrue(dropPartial3.doAction().hasActionExecutionSucceeded());
//		table5.assertIsValid();
//
//		// 3. drop a row in a table
//		// (need to create a new htmltable to drop in)
//		DropIEElement dropTable5 = DropIEElement.createHTMLTableInComponent(_oc, 1);
//		assertTrue(dropTable5.doAction().hasActionExecutionSucceeded());
//		IEHTMLTableWidget table6 = (IEHTMLTableWidget)dropTable5.getDroppedWidget();
//		assertNotNull(table6);
//		table6.assertIsValid();
//		//let's drop
//		int rc = table6.getRowCount();
//		DropPartialComponent dropPartial4 = DropPartialComponent.actionType.makeNewAction(table6.getTDAt(2, 2),null, _editor);
//		dropPartial4.setPartialComponent(rowComponent);
//		assertTrue(dropPartial4.doAction().hasActionExecutionSucceeded());
//		table6.assertIsValid();
//		assertTrue(table6.getRowCount()==rc+1);
//
//		DropPartialComponent dropPartial5 = DropPartialComponent.actionType.makeNewAction(table6.getTDAt(0, 2),null, _editor);
//		dropPartial5.setPartialComponent(rowComponent);
//		assertTrue(dropPartial5.doAction().hasActionExecutionSucceeded());
//		table6.assertIsValid();
//		assertTrue(table6.getRowCount()==rc+2);
//		// assertNotSame(dropPartial.getComponentInstance(), dropPartial2.getComponentInstance());
//		saveProject();
//		_project.close();
//		FileUtils.deleteDir(_project.getProjectDirectory());
	}


	/**
	 * Save the project
	 *
	 */
	private void saveProject()
	{
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}
}
