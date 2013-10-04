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
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.MouseDragControlAction;

public abstract class MouseDragControlActionImpl extends MouseControlActionImpl implements MouseDragControlAction<DrawingControllerImpl<?>> {

	static final Logger logger = Logger.getLogger(MouseDragControlActionImpl.class.getPackage().getName());

	@Override
	public abstract MouseDragControlActionType getActionType();

	/**
	 * Handle mouse pressed event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @return
	 */
	@Override
	public abstract boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event);

	/**
	 * Handle mouse released event, by performing what is required here Return flag indicating if event has been correctely handled and
	 * thus, should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @param isSignificativeDrag
	 *            TODO
	 * @return
	 */
	@Override
	public abstract boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event,
			boolean isSignificativeDrag);

	/**
	 * Handle mouse dragged event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 *            TODO
	 * @param event
	 *            TODO
	 * @return
	 */
	@Override
	public abstract boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event);

	public static class None extends MouseDragControlActionImpl {
		@Override
		public MouseDragControlActionType getActionType() {
			return MouseDragControlActionType.NONE;
		}

		@Override
		public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
			return true;
		}

		@Override
		public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event) {
			return false;
		}

		@Override
		public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DrawingControllerImpl<?> controller, MouseEvent event,
				boolean isSignificativeDrag) {
			return false;
		}
	}

}
