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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.InvalidArgumentException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.node.FlexoNode;
import org.openflexo.foundation.wkf.node.FlexoPreCondition;
import org.openflexo.localization.FlexoLocalization;

public class CreatePreCondition extends FlexoUndoableAction<CreatePreCondition, FlexoNode, WKFObject> {

	private static final Logger logger = Logger.getLogger(CreatePreCondition.class.getPackage().getName());

	public static FlexoActionType<CreatePreCondition, FlexoNode, WKFObject> actionType = new FlexoActionType<CreatePreCondition, FlexoNode, WKFObject>(
			"create_pre_condition", FlexoActionType.newMenu, FlexoActionType.newMenuGroup3, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreatePreCondition makeNewAction(FlexoNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new CreatePreCondition(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoNode object, Vector<WKFObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoNode object, Vector<WKFObject> globalSelection) {
			return object != null;
		}

		private String[] persistentProperties = { "attachedBeginNode" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CreatePreCondition.actionType, FlexoNode.class);
	}

	CreatePreCondition(FlexoNode focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private FlexoPreCondition newPreCondition = null;

	// Optional arguments
	private FlexoNode attachedBeginNode;

	@Override
	protected void doAction(Object context) throws InvalidArgumentException {
		if (selectedPreCondition != null)
			return;

		if (getFocusedObject() == null)
			throw new InvalidArgumentException("Trying to create pre-condition on null FlexoNode");

		if (getAttachedBeginNode() != null) {
			if (!getAttachedBeginNode().isBeginNode())
				throw new InvalidArgumentException("Supplied attached begin node is not a begin node");
			if (getAttachedBeginNode().getParentPetriGraph() == null
					|| getAttachedBeginNode().getParentPetriGraph().getContainer() != getFocusedObject())
				throw new InvalidArgumentException("Supplied attached begin node is not a child node of focused node");
		}

		if (logger.isLoggable(Level.FINE))
			logger.fine("Create pre-condition for " + getFocusedObject() + " with attached begin node=" + getAttachedBeginNode());
		newPreCondition = new FlexoPreCondition(getFocusedObject(), getAttachedBeginNode());

		objectCreated("NEW_PRE_CONDITION", newPreCondition);

	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		logger.info("CreatePreCondition: UNDO");
		if (selectedPreCondition != null)
			return;
		newPreCondition.delete();
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		logger.info("CreatePreCondition: REDO");
		doAction(context);
	}

	public FlexoNode getAttachedBeginNode() {
		return attachedBeginNode;
	}

	public void setAttachedBeginNode(FlexoNode attachedBeginNode) {
		this.attachedBeginNode = attachedBeginNode;
	}

	public FlexoPreCondition getNewPreCondition() {
		if (selectedPreCondition != null)
			return selectedPreCondition;
		return newPreCondition;
	}

	private boolean allowsToSelectPreconditionOnly = false;

	private FlexoPreCondition selectedPreCondition;

	@Override
	public String getLocalizedName() {
		if (allowsToSelectPreconditionOnly)
			return FlexoLocalization.localizedForKey("create_or_select_pre_condition");
		else
			return super.getLocalizedName();
	}

	public boolean allowsToSelectPreconditionOnly() {
		return allowsToSelectPreconditionOnly;
	}

	public void setAllowsToSelectPreconditionOnly(boolean allowsToSelectPreconditionOnly) {
		this.allowsToSelectPreconditionOnly = allowsToSelectPreconditionOnly;
	}

	public FlexoPreCondition getSelectedPreCondition() {
		return selectedPreCondition;
	}

	public void setSelectedPreCondition(FlexoPreCondition selectedPreCondition) {
		this.selectedPreCondition = selectedPreCondition;
	}
}
