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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.action.AddComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.NodeType;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;

public class SetAndOpenOperationComponent extends FlexoAction<SetAndOpenOperationComponent, OperationNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(SetAndOpenOperationComponent.class.getPackage().getName());

	public static FlexoActionType<SetAndOpenOperationComponent, OperationNode, WKFObject> actionType = new FlexoActionType<SetAndOpenOperationComponent, OperationNode, WKFObject>(
			"set_operation_component", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public SetAndOpenOperationComponent makeNewAction(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new SetAndOpenOperationComponent(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return object != null && !(object instanceof SelfExecutableNode) && object.getNodeType() == NodeType.NORMAL
					&& object.getComponentInstance() == null;
		}

		@Override
		public boolean isEnabledForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return object.getNodeType() == NodeType.NORMAL && object.getComponentInstance() == null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, OperationNode.class);
	}

	SetAndOpenOperationComponent(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private String _newComponentName;

	private boolean hasCreatedComponent = false;

	@Override
	protected void doAction(Object context) throws OperationAssociatedWithComponentSuccessfully, DuplicateResourceException,
			InvalidParametersException {
		if (getFocusedObject().getNodeType() != NodeType.NORMAL) {
			throw new InvalidParametersException("Cannot associate operation to non-normal operation nodes");
		}
		ComponentDefinition foundComponent = getFocusedObject().getProject().getFlexoComponentLibrary()
				.getComponentNamed(getNewComponentName());
		OperationComponentDefinition newComponent = null;
		if (foundComponent instanceof OperationComponentDefinition) {
			newComponent = (OperationComponentDefinition) foundComponent;
		} else if (foundComponent != null) {
			throw new InvalidParametersException("Cannot associate component to operation nodes: this is not an operation component");
		} else {
			// We use here a null editor because this action is embedded
			AddComponent addComponent = AddComponent.actionType.makeNewEmbeddedAction(getFocusedObject().getProject()
					.getFlexoComponentLibrary(), null, this);
			addComponent.setNewComponentName(getNewComponentName());
			addComponent.setComponentType(AddComponent.ComponentType.OPERATION_COMPONENT);
			addComponent.doAction();
			if (addComponent.hasActionExecutionSucceeded()) {
				newComponent = (OperationComponentDefinition) addComponent.getNewComponent();
				hasCreatedComponent = true;
			} else {
				logger.info("exception: " + addComponent.getThrownException());
				throw new InvalidParametersException("Cannot create component");
			}
		}
		logger.info("setOperationComponent with " + newComponent.getName());
		getFocusedObject().setOperationComponent(newComponent);
	}

	public String getNewComponentName() {
		return _newComponentName;
	}

	public void setNewComponentName(String newComponentName) {
		_newComponentName = newComponentName;
	}

	// Ceci est rendu necessaire a cause du OperationAssociatedWithComponentSucessfully, qu'on va
	// Virer par la suite quand on aura implemente proprement tout ca.
	@Override
	public boolean hasActionExecutionSucceeded() {
		return true;
	}

	public boolean hasCreatedComponent() {
		return hasCreatedComponent;
	}

}
