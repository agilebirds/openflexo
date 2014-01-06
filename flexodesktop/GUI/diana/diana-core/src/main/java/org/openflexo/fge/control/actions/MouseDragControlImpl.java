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
package org.openflexo.fge.control.actions;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.MouseDragControlAction;

public class MouseDragControlImpl<E extends AbstractDianaEditor<?, ?, ?>> extends MouseControlImpl<E> implements MouseDragControl<E> {

	public MouseDragControlAction<E> action;

	private DrawingTreeNode<?, ?> initialNode;
	private boolean isSignificativeDrag = false;

	public MouseDragControlImpl(String aName, MouseButton button, MouseDragControlAction<E> action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button, factory);
		this.action = action;
	}

	public MouseDragControlAction<E> getControlAction() {
		return action;
	}

	public boolean isSignificativeDrag() {
		return isSignificativeDrag;
	}

	public DrawingTreeNode<?, ?> getInitialNode() {
		return initialNode;
	}

	/**
	 * Handle mouse pressed event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context) {

		if (action.handleMousePressed(node, controller, context)) {
			initialNode = node;
			// System.out.println("PRESSED initialNode="+initialNode);
			context.consume();
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
	public void handleMouseReleased(E controller, MouseControlContext context) {
		if (action.handleMouseReleased(initialNode, controller, context, isSignificativeDrag())) {
			initialNode = null;
			// System.out.println("RELEASED initialNode="+initialNode);
			context.consume();
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
	public void handleMouseDragged(E controller, MouseControlContext context) {
		if (action.handleMouseDragged(initialNode, controller, context)) {
			// System.out.println("DRAGGED initialNode="+initialNode);
			isSignificativeDrag = true;
			context.consume();
		}
	}

	@Override
	public String toString() {
		return "MouseDragControlImpl[" + getName() + "," + getModifiersAsString() + "]";
	}

}