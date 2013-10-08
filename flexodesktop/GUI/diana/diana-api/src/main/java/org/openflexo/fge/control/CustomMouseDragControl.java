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

public interface CustomMouseDragControl<CI> extends CustomMouseControl<CI> {

	/**
	 * Handle mouse pressed event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaEditor<?> controller, CI controlInfo);

	/**
	 * Handle mouse released event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public void handleMouseReleased(DianaEditor<?> controller, CI controlInfo);

	/**
	 * Handle mouse dragged event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	public void handleMouseDragged(DianaEditor<?> controller, CI controlInfo);

}