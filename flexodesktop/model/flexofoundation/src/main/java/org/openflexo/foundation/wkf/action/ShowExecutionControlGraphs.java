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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.ExecutableWorkflowElement;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperatorNode;
import org.openflexo.foundation.wkf.node.PetriGraphNode;

public class ShowExecutionControlGraphs extends FlexoGUIAction<ShowExecutionControlGraphs, WKFObject, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowExecutionControlGraphs.class.getPackage().getName());

	public static FlexoActionType<ShowExecutionControlGraphs, WKFObject, WKFObject> actionType = new FlexoActionType<ShowExecutionControlGraphs, WKFObject, WKFObject>(
			"show_execution_control_graphs", FlexoActionType.executionModelMenu, FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ShowExecutionControlGraphs makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new ShowExecutionControlGraphs(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return object != null && object instanceof ExecutableWorkflowElement;
		}

		@Override
		public boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	static {
		FlexoModelObject.addActionForClass(ShowExecutionControlGraphs.actionType, FlexoProcess.class);
		FlexoModelObject.addActionForClass(ShowExecutionControlGraphs.actionType, PetriGraphNode.class);
		FlexoModelObject.addActionForClass(ShowExecutionControlGraphs.actionType, OperatorNode.class);
		FlexoModelObject.addActionForClass(ShowExecutionControlGraphs.actionType, FlexoPreCondition.class);
		FlexoModelObject.addActionForClass(ShowExecutionControlGraphs.actionType, FlexoPostCondition.class);
	}

	ShowExecutionControlGraphs(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
