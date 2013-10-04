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

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.control.DrawingController;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.toolbox.ToolBox;

public class MouseClickControlImpl extends MouseControlImpl implements MouseClickControl {
	public int clickCount = 1;
	public MouseClickControlAction action;

	public MouseClickControlImpl(String aName, MouseButton button, int clickCount, boolean shiftPressed, boolean ctrlPressed,
			boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button, factory);
		this.clickCount = clickCount;
		action = MouseClickControlActionType.NONE.makeAction(factory);
	}

	public MouseClickControlImpl(String aName, MouseButton button, int clickCount, MouseClickControlAction<?> action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		this(aName, button, clickCount, shiftPressed, ctrlPressed, metaPressed, altPressed, factory);
		this.action = action;
	}

	public MouseClickControlImpl(String aName, MouseButton button, int clickCount, MouseClickControlActionType actionType,
			boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		this(aName, button, clickCount, shiftPressed, ctrlPressed, metaPressed, altPressed, factory);
		setActionType(actionType);
	}

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent e) {
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Translating, mod=" + e.getModifiers() + " button=" + e.getButton());
				}
				boolean wasConsumed = e.isConsumed();
				int mod = e.getModifiers() & ~InputEvent.BUTTON1_MASK & ~InputEvent.CTRL_MASK | InputEvent.BUTTON3_MASK;
				e = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), mod, e.getX(), e.getY(), e.getClickCount(), false);
				if (wasConsumed) {
					e.consume();
				}
			}
		}

		if (!super.isApplicable(node, controller, e)) {
			return false;
		}
		return e.getClickCount() == clickCount;
	}

	/**
	 * Handle click event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 */
	@Override
	public void handleClick(DrawingTreeNode<?, ?> node, DrawingController<?> controller, MouseEvent event) {
		if (action.handleClick(node, controller, event)) {
			event.consume();
		}
	}

	@Override
	public boolean isModelEditionAction() {
		return getActionType() != MouseClickControlActionType.SELECTION
				&& getActionType() != MouseClickControlActionType.MULTIPLE_SELECTION
				&& getActionType() != MouseClickControlActionType.CONTINUOUS_SELECTION;
	}

	@Override
	public MouseClickControlActionType getActionType() {
		if (action != null) {
			return action.getActionType();
		} else {
			return MouseClickControlActionType.NONE;
		}
	}

	@Override
	public void setActionType(MouseClickControlActionType actionType) {
		if (actionType != null) {
			action = actionType.makeAction(getFactory());
		} else {
			action = MouseClickControlActionType.NONE.makeAction(getFactory());
		}
	}

	@Override
	public String toString() {
		return "MouseClickControlImpl[" + name + "," + getModifiersAsString() + ",ACTION=" + getActionType().name() + "]";
	}

	@Override
	protected String getModifiersAsString() {
		return super.getModifiersAsString() + ",clicks=" + clickCount;
	}

}