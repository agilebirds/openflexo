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

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;

public interface MouseClickControlAction<E extends DianaEditor<?>, CI> extends MouseControlAction<E> {

	public static final Logger logger = Logger.getLogger(MouseClickControlAction.class.getPackage().getName());

	public static enum MouseClickControlActionType {
		NONE, SELECTION, MULTIPLE_SELECTION, CONTINUOUS_SELECTION, CUSTOM;
	}

	public abstract MouseClickControlActionType getActionType();

	/**
	 * Handle click event, by performing what is required here Return flag indicating if event has been correctely handled and thus, should
	 * be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param controlInfo
	 *            TODO
	 * @return
	 */
	public abstract boolean handleClick(DrawingTreeNode<?, ?> node, E controller, CI controlInfo);

}