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
package org.openflexo.fge.swing.actions;

import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.CustomMouseDragControl;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseDragControlAction;
import org.openflexo.fge.control.MouseDragControlAction.MouseDragControlActionType;

public class JCustomMouseDragControl extends AbstractJCustomMouseControlImpl implements CustomMouseDragControl<MouseEvent> {
	public MouseDragControlAction action;

	private DrawingTreeNode<?, ?> initialNode;

	public JCustomMouseDragControl(String aName, MouseButton button, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed, FGEModelFactory factory) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button, factory);
		action = MouseDragControlActionType.NONE.makeAction(factory);
	}

	public JCustomMouseDragControl(String aName, MouseButton button, MouseDragControlAction action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		this(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed, factory);
		this.action = action;
	}

	public JCustomMouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		this(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed, factory);
		setActionType(actionType);
	}

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, MouseEvent e) {
		return super.isApplicable(node, controller, e);
	}

	private boolean isSignificativeDrag = false;

	protected boolean isSignificativeDrag() {
		return isSignificativeDrag;
	}

	/**
	 * Handle mouse pressed event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, MouseEvent event) {

		if (action.handleMousePressed(node, controller, event)) {
			initialNode = node;
			// System.out.println("PRESSED initialNode="+initialNode);
			event.consume();
			isSignificativeDrag = false;
			return true;
		}
		return false;
	}

	/**
	 * Handle mouse released event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void handleMouseReleased(DianaEditor<?> controller, MouseEvent event) {
		if (action.handleMouseReleased(initialNode, controller, event, isSignificativeDrag())) {
			initialNode = null;
			// System.out.println("RELEASED initialNode="+initialNode);
			event.consume();
		}
	}

	/**
	 * Handle mouse dragged event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void handleMouseDragged(DianaEditor<?> controller, MouseEvent event) {
		if (action.handleMouseDragged(initialNode, controller, event)) {
			// System.out.println("DRAGGED initialNode="+initialNode);
			isSignificativeDrag = true;
			event.consume();
		}
	}

	@Override
	public boolean isModelEditionAction() {
		return getActionType() != MouseDragControlActionType.RECTANGLE_SELECTING && getActionType() != MouseDragControlActionType.ZOOM;
	}

	public MouseDragControlActionType getActionType() {
		if (action != null) {
			return action.getActionType();
		} else {
			return MouseDragControlActionType.NONE;
		}
	}

	public void setActionType(MouseDragControlActionType actionType) {
		if (actionType != null) {
			action = actionType.makeAction(getFactory());
		} else {
			action = MouseDragControlActionType.NONE.makeAction(getFactory());
		}
	}

	@Override
	public String toString() {
		return "JCustomMouseDragControl[" + name + "," + getModifiersAsString() + ",ACTION=" + getActionType().name() + "]";
	}

	public MouseDragControlAction getAction() {
		return action;
	}

	public void setAction(MouseDragControlAction anAction) {
		action = anAction;
	}

}