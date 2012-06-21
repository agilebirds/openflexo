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
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;

public class OpenOperationComponent extends FlexoGUIAction<OpenOperationComponent, OperationNode, WKFObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OpenOperationComponent.class.getPackage().getName());

	public static FlexoActionType<OpenOperationComponent, OperationNode, WKFObject> actionType = new FlexoActionType<OpenOperationComponent, OperationNode, WKFObject>(
			"open_operation_component", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public OpenOperationComponent makeNewAction(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenOperationComponent(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return ((object != null) && ((object).getNodeType() == NodeType.NORMAL) && ((object).getComponentInstance() != null));
		}

		@Override
		public boolean isEnabledForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return ((object != null) && ((object).getNodeType() == NodeType.NORMAL) && ((object).getComponentInstance() != null));
		}

	};

	OpenOperationComponent(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
