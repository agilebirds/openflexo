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
 * A {@link MouseControlAction} is associated to a {@link MouseControl}<br>
 * It is triggered by its {@link MouseControl}
 * 
 * @author sylvain
 * 
 */
public interface MouseControlAction<E extends DianaEditor<?>> {

	/**
	 * Return boolean indicating if this control action is applicable in the current context
	 * 
	 * @param node
	 * @param controller
	 * @param context
	 * @return
	 */
	public boolean isApplicable(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

}