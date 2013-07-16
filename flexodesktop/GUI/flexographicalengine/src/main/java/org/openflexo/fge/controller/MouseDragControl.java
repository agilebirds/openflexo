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
package org.openflexo.fge.controller;

import java.awt.event.MouseEvent;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.MouseDragControlAction.MouseDragControlActionType;

public class MouseDragControl extends MouseControl {
	public MouseDragControlAction action;

	private GraphicalRepresentation initialGraphicalRepresentation;

	public MouseDragControl(String aName, MouseButton button, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed,
			boolean altPressed) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button);
		action = MouseDragControlAction.MouseDragControlActionType.NONE.makeAction();
	}

	public MouseDragControl(String aName, MouseButton button, MouseDragControlAction action, boolean shiftPressed, boolean ctrlPressed,
			boolean metaPressed, boolean altPressed) {
		this(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed);
		this.action = action;
	}

	public MouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed) {
		this(aName, button, shiftPressed, ctrlPressed, metaPressed, altPressed);
		setActionType(actionType);
	}

	public static MouseDragControl makeMouseDragControl(String aName, MouseButton button) {
		return new MouseDragControl(aName, button, false, false, false, false);
	}

	public static MouseDragControl makeMouseShiftDragControl(String aName, MouseButton button) {
		return new MouseDragControl(aName, button, true, false, false, false);
	}

	public static MouseDragControl makeMouseControlDragControl(String aName, MouseButton button) {
		return new MouseDragControl(aName, button, false, true, false, false);
	}

	public static MouseDragControl makeMouseMetaDragControl(String aName, MouseButton button) {
		return new MouseDragControl(aName, button, false, false, true, false);
	}

	public static MouseDragControl makeMouseAltDragControl(String aName, MouseButton button) {
		return new MouseDragControl(aName, button, false, false, false, true);
	}

	public static MouseDragControl makeMouseDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return new MouseDragControl(aName, button, actionType, false, false, false, false);
	}

	public static MouseDragControl makeMouseShiftDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return new MouseDragControl(aName, button, actionType, true, false, false, false);
	}

	public static MouseDragControl makeMouseControlDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return new MouseDragControl(aName, button, actionType, false, true, false, false);
	}

	public static MouseDragControl makeMouseMetaDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return new MouseDragControl(aName, button, actionType, false, false, true, false);
	}

	public static MouseDragControl makeMouseAltDragControl(String aName, MouseButton button, MouseDragControlActionType actionType) {
		return new MouseDragControl(aName, button, actionType, false, false, false, true);
	}

	@Override
	public boolean isApplicable(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent e) {
		return super.isApplicable(graphicalRepresentation, controller, e);
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
	public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller, MouseEvent event) {

		if (action.handleMousePressed(graphicalRepresentation, controller, event)) {
			initialGraphicalRepresentation = graphicalRepresentation;
			// System.out.println("PRESSED initialGraphicalRepresentation="+initialGraphicalRepresentation);
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
	public void handleMouseReleased(DrawingController controller, MouseEvent event) {
		if (action.handleMouseReleased(initialGraphicalRepresentation, controller, event, isSignificativeDrag())) {
			initialGraphicalRepresentation = null;
			// System.out.println("RELEASED initialGraphicalRepresentation="+initialGraphicalRepresentation);
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
	public void handleMouseDragged(DrawingController controller, MouseEvent event) {
		if (action.handleMouseDragged(initialGraphicalRepresentation, controller, event)) {
			// System.out.println("DRAGGED initialGraphicalRepresentation="+initialGraphicalRepresentation);
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
			action = actionType.makeAction();
		} else {
			action = MouseDragControlActionType.NONE.makeAction();
		}
	}

	@Override
	public String toString() {
		return "MouseDragControl[" + name + "," + getModifiersAsString() + ",ACTION=" + getActionType().name() + "]";
	}

	public MouseDragControlAction getAction() {
		return action;
	}

	public void setAction(MouseDragControlAction anAction) {
		action = anAction;
	}

}