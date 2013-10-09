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
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseControlContext;

public class MouseClickControlImpl extends MouseControlImpl implements MouseClickControl {

	private int clickCount = 1;
	private MouseClickControlAction action;

	public MouseClickControlImpl(String aName, MouseButton button, int clickCount, MouseClickControlAction action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, FGEModelFactory factory) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button, factory);
		this.clickCount = clickCount;
		this.action = action;
	}

	public MouseClickControlAction getControlAction() {
		return action;
	}

	@Override
	public int getClickCount() {
		return clickCount;
	}

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, MouseControlContext context) {
		if (!super.isApplicable(node, controller, context)) {
			return false;
		}
		return context.getClickCount() == clickCount;
	}

	/**
	 * Handle click event, by performing what is required here<br>
	 * If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 */
	public void handleClick(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, MouseControlContext context) {
		if (action != null) {
			if (action.handleClick(node, controller, context)) {
				context.consume();
			}
		}

	}

	@Override
	public String toString() {
		return "MouseClickControlImpl[" + getName() + "," + getModifiersAsString() + "]";
	}

	@Override
	protected String getModifiersAsString() {
		return super.getModifiersAsString() + ",clicks=" + clickCount;
	}

}