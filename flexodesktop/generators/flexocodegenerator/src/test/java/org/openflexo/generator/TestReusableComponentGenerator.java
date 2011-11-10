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

import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoTestCase;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.FlexoComponentLibrary;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.ie.util.WidgetType;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.OperationPetriGraph;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractActivityNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SubProcessNode;


public class TestReusableComponentGenerator extends FlexoTestCase{

	protected static final Logger logger = Logger.getLogger(TestReusableComponentGenerator.class.getPackage().getName());

	private FlexoEditor _editor;
	private FlexoProject _project;
	private FlexoComponentLibrary _cl;
	private FlexoComponentFolder _cf;
	private IEOperationComponent _oc1;
	private IEOperationComponent _oc2;
	private IEPopupComponent _popup1;
	private IEOperationComponent _targetComponent;
	private static final String TEST_REUSABLE_COMPONENT = "TestReusableGenerator";
	private static final String TEST_COMPONENT_1 = "Component1";
	private static final String TEST_COMPONENT_2 = "Component2";
	private static final String POPUP_COMPONENT_NAME = "Popup1";

	private static final String PARTIAL_COMPONENT = "PartialTestComponent";
	private static final String TEST_COMPONENT_FOLDER = "TestFolder";

	public TestReusableComponentGenerator(String arg0) {
		super(arg0);
	}

	/**
	 * Creates a new empty project in a temp directory
	 */
	public void test0CreateProject()
	{
		_editor = createProject(TEST_REUSABLE_COMPONENT);
		_project = _editor.getProject();
		_cl = _project.getFlexoComponentLibrary();
		_cf = FlexoTestCase.createFolder(TEST_COMPONENT_FOLDER, null, _editor);
		_oc1 = (IEOperationComponent)FlexoTestCase.createComponent(TEST_COMPONENT_1,_cf,AddComponent.ComponentType.OPERATION_COMPONENT, _editor);
		_oc2 = (IEOperationComponent)FlexoTestCase.createComponent(TEST_COMPONENT_2,_cf,AddComponent.ComponentType.OPERATION_COMPONENT, _editor);
		_popup1 = (IEPopupComponent)FlexoTestCase.createComponent(POPUP_COMPONENT_NAME, _cf, AddComponent.ComponentType.POPUP_COMPONENT, _editor);

		// Insert a new bloc at index 0, name it Bloc1
		IEBlocWidget bloc1 = FlexoTestCase.dropBlocAtIndex("bloc1",_oc1,0,_editor);
		IEBlocWidget bloc2 = FlexoTestCase.dropBlocAtIndex("bloc2",_oc1,1,_editor);
		IEBlocWidget bloc3 = FlexoTestCase.dropBlocAtIndex("bloc3",_oc1,0,_editor);

		assertEquals(1,bloc1.getIndex());
		assertEquals(2,bloc2.getIndex());
		assertEquals(0,bloc3.getIndex());

		IEHTMLTableWidget table = FlexoTestCase.dropTableInBloc(bloc1,_editor);


		// Drop a label in the table3, at cell (0,0) at position 0
		IELabelWidget label = (IELabelWidget)FlexoTestCase.dropWidgetInTable(WidgetType.LABEL, table, 0, 0, 0,_editor);
		IETextFieldWidget textField = (IETextFieldWidget)FlexoTestCase.dropWidgetInTable(WidgetType.TEXTFIELD, table, 0, 0, 1,_editor);
		IETextAreaWidget textArea = (IETextAreaWidget)FlexoTestCase.dropWidgetInTable(WidgetType.TEXTAREA, table, 1, 1, 0,_editor);
		// Save project
		saveProject();

		//reuse table
		IEReusableWidget reusableTable = FlexoTestCase.makePartial(PARTIAL_COMPONENT, _cf, table, _editor);

		IEReusableWidget dropTableInComponent2 = FlexoTestCase.dropPartialComponent((IEReusableComponent)reusableTable.getReusableComponentInstance().getComponentDefinition().getWOComponent(), _oc2, _editor);
		// assertNotSame(dropPartial.getComponentInstance(), dropPartial2.getComponentInstance());
		saveProject();

		FlexoProcess sub1 = FlexoTestCase.createSubProcess("sub1", _project.getRootFlexoProcess(), _editor);

		AbstractActivityNode beginRootProcessNode = (AbstractActivityNode)_project.getRootFlexoProcess().getAllBeginNodes().get(0);
		AbstractActivityNode endRootProcessNode = (AbstractActivityNode)_project.getRootFlexoProcess().getAllEndNodes().get(0);

		SubProcessNode sub1Node = FlexoTestCase.instanciateForkSubProcess(sub1, _project.getRootFlexoProcess(), 200, 200, _editor);
		FlexoTestCase.openOperationLevel(sub1Node, _editor);
		OperationPetriGraph operationGraph = sub1Node.getOperationPetriGraph();
		OperationNode beginOperation = (OperationNode)operationGraph.getAllBeginNodes().get(0);


		FlexoPostCondition edge = FlexoTestCase.createEdge(beginRootProcessNode, beginOperation, _editor);
		OperationNode monitoring = FlexoTestCase.createOperationNode("monitoring", sub1Node, 100, 50, _editor);

		FlexoTestCase.openActionLevel(monitoring, _editor);
		ActionNode beginAction = (ActionNode)monitoring.getActionPetriGraph().getAllBeginNodes().get(0);
		FlexoPostCondition edge2 = FlexoTestCase.createEdge(beginOperation, beginAction, _editor);

		saveProject();

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
