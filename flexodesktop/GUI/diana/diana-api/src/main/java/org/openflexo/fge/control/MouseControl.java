/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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

package org.openflexo.fge.control;

import org.openflexo.fge.Drawing.DrawingTreeNode;

/**
 * Abstract definition of a mouse control<br>
 * A {@link MouseControl} has a name, and some triggering options, such as mouse button which is pressed, and key modifiers.<br>
 * A {@link MouseControl} is associated with a {@link MouseControlAction} which is triggered when relevant
 * 
 * @author sylvain
 * 
 */
public interface MouseControl<E extends DianaEditor<?>> {

	public static enum MouseButton {
		LEFT, RIGHT, CENTER
	}

	public MouseControlAction<E> getControlAction();

	public boolean isApplicable(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

	public String getName();

	public boolean isShiftPressed();

	public boolean isCtrlPressed();

	public boolean isMetaPressed();

	public boolean isAltPressed();

	public MouseButton getButton();

}