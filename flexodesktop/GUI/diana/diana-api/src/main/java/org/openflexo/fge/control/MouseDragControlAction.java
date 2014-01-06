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

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;

/**
 * A {@link MouseDragControlAction} represents an action to be applied as a "Drag"-scheme.<br>
 * It is associated to and triggered by a {@link MouseDragControl}
 * 
 * @author sylvain
 * 
 */
public interface MouseDragControlAction<E extends DianaEditor<?>> extends MouseControlAction<E> {

	public static final Logger logger = Logger.getLogger(MouseDragControlAction.class.getPackage().getName());

	// public abstract PredefinedMouseDragControlActionType getActionType();

	/**
	 * Handle mouse pressed event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	public abstract boolean handleMousePressed(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

	/**
	 * Handle mouse released event, by performing what is required here Return flag indicating if event has been correctely handled and
	 * thus, should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @param isSignificativeDrag
	 *            TODO
	 * @return
	 */
	public abstract boolean handleMouseReleased(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context,
			boolean isSignificativeDrag);

	/**
	 * Handle mouse dragged event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	public abstract boolean handleMouseDragged(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

}