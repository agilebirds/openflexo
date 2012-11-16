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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.ie.action.DropIEElement;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.cl.action.AddComponentFolder;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResourceManager;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.logging.FlexoLoggingManager;
import org.openflexo.toolbox.FileUtils;

public class TestCreateComponent extends FlexoIETestCase {

	public TestCreateComponent(String arg0) {
		super(arg0);
	}

	protected static final Logger logger = Logger.getLogger(TestCreateComponent.class.getPackage().getName());

	private static final String TEST_COMPONENT = "TestComponent";
	private static final String TEST_COMPONENT_FOLDER = "TestFolder";

	private static FlexoEditor _editor;
	private static FlexoProject _project;
	private static File _projectDirectory;
	private static String _projectIdentifier;
	private static FlexoComponentLibrary _cl;
	private static FlexoComponentFolder _cf;
	private static IEOperationComponent _oc;

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject() {
		FlexoLoggingManager.forceInitialize(-1, true, null, Level.INFO, null);
		try {
			File tempFile = File.createTempFile(TEST_COMPONENT, "");
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
	}

	/**
	 * Creates a new component folder in the component library
	 */
	public void test1CreateComponentFolder() {
		_cl = _project.getFlexoComponentLibrary();
		AddComponentFolder addComponentFolder = AddComponentFolder.actionType.makeNewAction(_cl, null, _editor);
		addComponentFolder.setNewFolderName(TEST_COMPONENT_FOLDER);
		addComponentFolder.doAction();
		assertTrue(addComponentFolder.hasActionExecutionSucceeded());
		_cf = _cl.getRootFolder().getFlexoComponentFolderWithName(TEST_COMPONENT_FOLDER);
		assertNotNull(_cf);
		saveProject();
	}

	/**
	 * Creates a new component in the new component folder
	 */
	public void test2CreateComponent() {
		_cl = _project.getFlexoComponentLibrary();
		AddComponent addComponent = AddComponent.actionType.makeNewAction(_cf, null, _editor);
		addComponent.setNewComponentName(TEST_COMPONENT);
		addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
		addComponent.doAction();
		assertTrue(addComponent.hasActionExecutionSucceeded());
		_oc = _project.getOperationComponent(TEST_COMPONENT);
		assertNotNull(_oc);
		saveProject();
	}

	/**
	 * Edit that new component
	 */
	public void test3EditComponent() {
		// Insert a new bloc at index 0, name it Bloc1
		DropIEElement dropBloc1 = DropIEElement.createBlocInComponent(_oc, 0, _editor);
		assertTrue(dropBloc1.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc1 = (IEBlocWidget) dropBloc1.getDroppedWidget();
		assertNotNull(bloc1);
		bloc1.setTitle("Bloc1");

		// Insert a new bloc at index 1, name it Bloc2
		DropIEElement dropBloc2 = DropIEElement.createBlocInComponent(_oc, 1, _editor);
		assertTrue(dropBloc2.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc2 = (IEBlocWidget) dropBloc2.getDroppedWidget();
		assertNotNull(bloc2);
		bloc2.setTitle("Bloc2");

		// Insert a new bloc at index 1, name it Bloc3
		// This bloc is therefore placed between Bloc1 and Bloc2
		DropIEElement dropBloc3 = DropIEElement.createBlocInComponent(_oc, 1, _editor);
		assertTrue(dropBloc3.doAction().hasActionExecutionSucceeded());
		IEBlocWidget bloc3 = (IEBlocWidget) dropBloc3.getDroppedWidget();
		assertNotNull(bloc3);
		bloc3.setTitle("Bloc3");

		// Drop a table in the bloc2
		DropIEElement dropTable = DropIEElement.createTableInBloc(bloc2, _editor);
		assertTrue(dropTable.doAction().hasActionExecutionSucceeded());
		IEHTMLTableWidget table = (IEHTMLTableWidget) dropTable.getDroppedWidget();
		assertNotNull(table);

		// Drop a label in the table, at cell (0,0) at position 0
		DropIEElement dropLabel = DropIEElement.insertWidgetInTable(table, WidgetType.LABEL, 0, 0, 0, _editor);
		assertTrue(dropLabel.doAction().hasActionExecutionSucceeded());

		// Drop a TextField in the table, at cell (0,1) at position 0
		DropIEElement dropTF = DropIEElement.insertWidgetInTable(table, WidgetType.TEXTFIELD, 0, 1, 0, _editor);
		assertTrue(dropTF.doAction().hasActionExecutionSucceeded());

		// Drop a List in the table, at cell (0,2) at position 0
		DropIEElement dropList = DropIEElement.insertWidgetInTable(table, WidgetType.LIST, 0, 2, 0, _editor);
		assertTrue(dropList.doAction().hasActionExecutionSucceeded());

		// Drop a FileUpload in the table, at cell (0,3) at position 0
		DropIEElement dropFileUpload = DropIEElement.insertWidgetInTable(table, WidgetType.FILEUPLOAD, 0, 3, 0, _editor);
		assertTrue(dropFileUpload.doAction().hasActionExecutionSucceeded());

		// Drop a TextArea in the table, at cell (1,0) at position 0
		DropIEElement dropTextArea = DropIEElement.insertWidgetInTable(table, WidgetType.TEXTAREA, 1, 0, 0, _editor);
		assertTrue(dropTextArea.doAction().hasActionExecutionSucceeded());

		// Drop a Browser in the table, at cell (1,1) at position 0
		DropIEElement dropBrowser = DropIEElement.insertWidgetInTable(table, WidgetType.BROWSER, 1, 1, 0, _editor);
		assertTrue(dropBrowser.doAction().hasActionExecutionSucceeded());

		// Drop a CheckBox in the table, at cell (1,2) at position 0
		DropIEElement dropCheckBox = DropIEElement.insertWidgetInTable(table, WidgetType.CHECKBOX, 1, 2, 0, _editor);
		assertTrue(dropCheckBox.doAction().hasActionExecutionSucceeded());

		// Drop a CustomButton in the table, at cell (1,3) at position 0
		/*DropIEElement dropCustomButton = DropIEElement.insertWidgetInTable(table, WidgetType.CUSTOMBUTTON, 1, 3, 0, _editor);
		assertTrue(dropCustomButton.doAction().hasActionExecutionSucceeded());*/

		// Drop a Hyperlink in the table, at cell (2,0) at position 0
		DropIEElement dropHyperlink = DropIEElement.insertWidgetInTable(table, WidgetType.HYPERLINK, 2, 0, 0, _editor);
		assertTrue(dropHyperlink.doAction().hasActionExecutionSucceeded());

		// Drop a Hyperlink in the table, at cell (2,1) at position 0
		DropIEElement dropRadio = DropIEElement.insertWidgetInTable(table, WidgetType.RADIO, 2, 1, 0, _editor);
		assertTrue(dropRadio.doAction().hasActionExecutionSucceeded());

		// Drop a Header in the table, at cell (2,2) at position 0
		DropIEElement dropHeader = DropIEElement.insertWidgetInTable(table, WidgetType.HEADER, 2, 2, 0, _editor);
		assertTrue(dropHeader.doAction().hasActionExecutionSucceeded());

		// Drop a Tabs in the table, at cell (2,3) at position 0
		DropIEElement dropTabs = DropIEElement.insertWidgetInTable(table, WidgetType.TABS, 2, 3, 0, _editor);
		assertTrue(dropTabs.doAction().hasActionExecutionSucceeded());

		// Drop a HTMLTable in the table, at cell (3,0) at position 0
		DropIEElement dropHtmlTable = DropIEElement.insertWidgetInTable(table, WidgetType.HTMLTable, 3, 0, 0, _editor);
		assertTrue(dropHtmlTable.doAction().hasActionExecutionSucceeded());

		// Drop a String in the table, at cell (3,1) at position 0
		DropIEElement dropString = DropIEElement.insertWidgetInTable(table, WidgetType.STRING, 3, 1, 0, _editor);
		assertTrue(dropString.doAction().hasActionExecutionSucceeded());

		// Drop a Wysiwig in the table, at cell (3,2) at position 0
		DropIEElement dropWysiwig = DropIEElement.insertWidgetInTable(table, WidgetType.WYSIWYG, 3, 2, 0, _editor);
		assertTrue(dropWysiwig.doAction().hasActionExecutionSucceeded());

		// Drop a DropDown in the table, at cell (3,3) at position 0
		DropIEElement dropDropDown = DropIEElement.insertWidgetInTable(table, WidgetType.DROPDOWN, 3, 3, 0, _editor);
		assertTrue(dropDropDown.doAction().hasActionExecutionSucceeded());

		// Drop a Block in the table, at cell (3,3) at position 0
		DropIEElement dropBlock = DropIEElement.insertWidgetInTable(table, WidgetType.BLOCK, 0, 0, 1, _editor);
		assertTrue(dropBlock.doAction().hasActionExecutionSucceeded());

		// Save project
		saveProject();
		// The last test must call this to stop the RM checking
		_project.close();
		FileUtils.deleteDir(_project.getProjectDirectory());
		_cf = null;
		_cl = null;
		_editor = null;
		_oc = null;
		_project = null;
		_projectDirectory = null;
		_projectIdentifier = null;
	}

	/**
	 * Save the project
	 * 
	 */
	private void saveProject() {
		try {
			_project.save();
		} catch (SaveResourceException e) {
			fail("Cannot save project");
		}
	}

}
