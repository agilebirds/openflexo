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

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.node.AbstractNode;
import org.openflexo.foundation.wkf.node.ActionNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.foundation.wkf.utils.OperationAssociatedWithComponentSuccessfully;
import org.openflexo.toolbox.FlexoBoolean;

public class BindButtonsToActionNode extends FlexoAction<BindButtonsToActionNode, OperationNode, WKFObject> {

	protected static final Logger logger = Logger.getLogger(BindButtonsToActionNode.class.getPackage().getName());

	public static FlexoActionType<BindButtonsToActionNode, OperationNode, WKFObject> actionType = new FlexoActionType<BindButtonsToActionNode, OperationNode, WKFObject>(
			"bind_buttons_to_action_nodes", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public BindButtonsToActionNode makeNewAction(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new BindButtonsToActionNode(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return !(object instanceof SelfExecutableNode);
		}

		@Override
		public boolean isEnabledForSelection(OperationNode object, Vector<WKFObject> globalSelection) {
			return object != null && object.getComponentInstance() != null;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, OperationNode.class);
	}

	protected BindButtonsToActionNode(OperationNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public static final int OK = 0;

	public static final int CANCEL = 1;

	public static final int IGNORE = 2;

	/**
	 * The operation node for which action nodes will be created
	 */
	private OperationNode operationNode;

	/**
	 * This hashtable keeps a record of the associations made by the end-user. The key list contains all the buttons of the component that
	 * needs to be associated with an action. The values are ActionNode that needs to be associated with the button. If the ActionNode
	 * returns a <code>null</code> value, then it means that the action needs to be created (it was the dummy ActionNode that was selected).
	 */
	private Map<IEHyperlinkWidget, ActionNode> associations;

	/**
	 * This is the vector of actions (including the dummy) that were selectable to be bound to a button
	 */
	private List<ActionNode> actions;

	/**
	 * This boolean indicates wheter previous ActionNodes should be kept or not
	 */
	private boolean cleanActions = false;

	/**
	 * This array of FlexoBoolean indicates wheter the associated ActionNode (the order is the same as the one given by the keys of the
	 * hashtable) should be linked to the Begin and end node or not.
	 */
	private FlexoBoolean[] insertActionNode;

	/**
	 * If an exception was thrown then it needs to be passed also (mainly to handle the CANCEL option, information for going back to the
	 * previous state is contained in that exception)
	 */
	private OperationAssociatedWithComponentSuccessfully exception;

	private Vector<IEHyperlinkWidget> buttons;

	private int retval = -1;

	@Override
	protected void doAction(Object context) throws DuplicateResourceException {
		switch (retval) {
		case OK:
			handleOK();
			break;
		case CANCEL:
			handleCancel(exception);
			break;
		case IGNORE:
			handleIgnore();
			break;
		}
	}

	public OperationNode getOperationNode() {
		return operationNode;
	}

	public void setOperationNode(OperationNode operationNode) {
		this.operationNode = operationNode;
	}

	private void handleOK() {
		// cleanActions(cleanActions);
		int i = 0;
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Ok pressed");
		}
		for (IEHyperlinkWidget w : buttons) {
			if (!getInsertActionNode()[i++].getValue()) {
				continue;
			}
			ActionNode node = getAssociations().get(w);
			if (node.getProcess() == null) {
				node = OperationNode.createNewActionNodeForButton(w, operationNode);
			} else {
				associateNodeWithButton(node, w);
			}
			if (node != null) {
				linkActionToBeginNode(node);
			}
		}
	}

	private static void linkActionToBeginNode(ActionNode node) {
		boolean bind = true;
		Enumeration<FlexoPreCondition> en = node.getPreConditions().elements();
		while (en.hasMoreElements() && bind) {
			FlexoPreCondition pre = en.nextElement();
			Enumeration<FlexoPostCondition<AbstractNode, AbstractNode>> en1 = pre.getIncomingPostConditions().elements();
			while (en1.hasMoreElements() && bind) {
				FlexoPostCondition<AbstractNode, AbstractNode> post = en1.nextElement();
				if (post.getStartNode() instanceof ActionNode && ((ActionNode) post.getStartNode()).isBeginNode()) {
					bind = false;
				}
			}
		}
		if (bind) {
			OperationNode.linkActionToBeginNode(node);
		}
	}

	private static void handleIgnore() {
		// Well, for now we do nothing and it should not change in the future
		// (Look at my Master comment!)
	}

	private static void handleCancel(OperationAssociatedWithComponentSuccessfully ex) {
		if (ex == null) {
			// instance has not changed.
			return;
		}
		try {
			if (ex.getPreviousComponentInstance() != null) {
				ex.getNode().setComponentInstance(ex.getPreviousComponentInstance());
			} else {
				ex.getNode().removeComponentInstance();
			}
		} catch (OperationAssociatedWithComponentSuccessfully e) {
			// We have nothing to do here since we set the component instance
			// back to the ways it was
			// NTH: re-associate action nodes with the butons of the UI.
		}
	}

	private void cleanActions(boolean cleanActions) {
		if (cleanActions) {
			for (ActionNode a : actions) {
				if (a.getAssociatedButtonWidget() == null && a.getProcess() != null) {
					a.delete();
				}
			}
		}
	}

	private void associateNodeWithButton(ActionNode node, IEHyperlinkWidget w) {
		node.setAssociatedButtonWidget(w);
	}

	public Map<IEHyperlinkWidget, ActionNode> getAssociations() {
		return associations;
	}

	public void setAssociations(Map<IEHyperlinkWidget, ActionNode> map) {
		this.associations = map;
	}

	public List<ActionNode> getActions() {
		return actions;
	}

	public void setActions(List<ActionNode> list) {
		this.actions = list;
	}

	public boolean getCleanActions() {
		return cleanActions;
	}

	public void setCleanActions(boolean cleanActions) {
		this.cleanActions = cleanActions;
	}

	public FlexoBoolean[] getInsertActionNode() {
		return insertActionNode;
	}

	public void setInsertActionNode(FlexoBoolean[] linkToBeginAndEndNode) {
		this.insertActionNode = linkToBeginAndEndNode;
	}

	public int getRetval() {
		return retval;
	}

	public void setRetval(int retval) {
		this.retval = retval;
	}

	public OperationAssociatedWithComponentSuccessfully getException() {
		return exception;
	}

	public void setException(OperationAssociatedWithComponentSuccessfully exception) {
		this.exception = exception;
	}

	/**
	 * @param buttons
	 */
	public void setButtons(Vector<IEHyperlinkWidget> buttons) {
		this.buttons = buttons;
	}

}
