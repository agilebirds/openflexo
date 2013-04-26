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

import org.flexo.model.WKFAnnotation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.edge.WKFAssociation;
import org.openflexo.foundation.wkf.edge.WKFAssociation.Arrow;
import org.openflexo.foundation.wkf.node.WKFNode;

public class CreateAssociation extends FlexoUndoableAction<CreateAssociation, WKFNode, WKFNode> {

	private static final Logger logger = Logger.getLogger(CreateAssociation.class.getPackage().getName());

	public static FlexoActionType<CreateAssociation, WKFNode, WKFNode> actionType = new FlexoActionType<CreateAssociation, WKFNode, WKFNode>(
			"create_association", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public CreateAssociation makeNewAction(WKFNode focusedObject, Vector<WKFNode> globalSelection, FlexoEditor editor) {
			return new CreateAssociation(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFNode object, Vector<WKFNode> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(WKFNode object, Vector<WKFNode> globalSelection) {
			return true;
		}

		private String[] persistentProperties = { "startNode", "endNode" };

		@Override
		protected String[] getPersistentProperties() {
			return persistentProperties;
		}

	};

	CreateAssociation(WKFNode focusedObject, Vector<WKFNode> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		FlexoModelObject.addActionForClass(actionType, WKFNode.class);
	}

	WKFNode startNode;
	WKFNode endNode;
	private String newEdgeName;

	private WKFAssociation newAssociation;

	@Override
	protected void doAction(Object context) throws InvalidEdgeDefinition, DisplayActionCannotBeBound {
		// 1. Check validity
		// Cannot have null start node or end node
		if (startNode == null || endNode == null) {
			throw new InvalidEdgeDefinition();
		}

		newAssociation = new WKFAssociation(startNode, endNode);
		if (!(startNode instanceof WKFAnnotation) && !(endNode instanceof WKFAnnotation)) {
			newAssociation.setArrow(Arrow.START_TO_END);
		}
		objectCreated("NEW_ASSOCIATION", newAssociation);
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		logger.info("Create association: UNDO");
		getNewAssociation().delete();
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		logger.info("Create association: REDO");
		doAction(context);
	}

	public WKFNode getStartNode() {
		if (startNode == null) {
			return getFocusedObject();
		}
		return startNode;
	}

	public void setStartingNode(WKFNode startNode) {
		this.startNode = startNode;
	}

	public WKFNode getEndNode() {
		return endNode;
	}

	public void setEndNode(WKFNode endNode) {
		this.endNode = endNode;
	}

	public class InvalidEdgeDefinition extends FlexoException {
		protected InvalidEdgeDefinition() {
			super("Invalid association startNode=" + startNode + " endNode=" + endNode, "invalid_association");
		}
	}

	public class DisplayActionCannotBeBound extends FlexoException {
		protected DisplayActionCannotBeBound() {
			super("DisplayActionCannotBeBound", "display_action_can_not_be_bound");
		}
	}

	public WKFAssociation getNewAssociation() {
		return newAssociation;
	}

	public String getNewEdgeName() {
		return newEdgeName;
	}

	public void setNewEdgeName(String newEdgeName) {
		this.newEdgeName = newEdgeName;
	}

}
