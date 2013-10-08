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

package org.openflexo.fge.control;

import org.openflexo.fge.Drawing.DrawingTreeNode;

/**
 * Abstract definition of a mouse control
 * 
 * @author sylvain
 * 
 * @param <CI>
 *            type of information container managed for this control (eg MouseEvent for Swing)
 */
public interface CustomMouseControl<CI> {

	public static enum MouseButton {
		LEFT, RIGHT, CENTER
	}

	public abstract boolean isApplicable(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, CI controlInfo);

	public abstract boolean isModelEditionAction();

}