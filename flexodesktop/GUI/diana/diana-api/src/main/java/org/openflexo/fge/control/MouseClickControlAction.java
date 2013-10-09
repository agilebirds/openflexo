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
 * A {@link MouseClickControlAction} represents an action to be applied as a "Click"-scheme.<br>
 * It is associated to and triggered by a {@link MouseClickControl}
 * 
 * @author sylvain
 * 
 */
public interface MouseClickControlAction extends MouseControlAction {

	public static final Logger logger = Logger.getLogger(MouseClickControlAction.class.getPackage().getName());

	// public abstract PredefinedMouseClickControlActionType getActionType();

	/**
	 * Handle click, by performing what is required.<br>
	 * The implementation of this is technology-specific.<br>
	 * Return flag indicating if event has been correctely handled and thus, should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	public abstract boolean handleClick(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, MouseControlContext context);

}