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

import org.openflexo.fge.GraphicalRepresentation;



public abstract class MouseDragControlAction extends MouseControlAction {

	static final Logger logger = Logger.getLogger(MouseDragControlAction.class.getPackage().getName());

	public static enum MouseDragControlActionType
	{
		NONE,
		MOVE,
		RECTANGLE_SELECTING,
		ZOOM,
		CUSTOM;
		
		protected MouseDragControlAction makeAction()
		{
			if (this == NONE) {
				return new None();
			}
			else if (this == MOVE) {
				return new MoveAction();
			}
			else if (this == RECTANGLE_SELECTING) {
				return new RectangleSelectingAction();
			}
			else if (this == ZOOM) {
				return new ZoomAction();
			}
			else if (this == CUSTOM) {
				return new CustomDragControlAction() {

					@Override
					public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
					{
						logger.info("Perform mouse DRAGGED on undefined CUSTOM MouseDragControlAction");
						return true;
					}

					@Override
					public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
					{
						logger.info("Perform mouse PRESSED on undefined CUSTOM MouseDragControlAction");
						return false;
					}

					@Override
					public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event, boolean isSignificativeDrag)
					{
						logger.info("Perform mouse RELEASED on undefined CUSTOM MouseDragControlAction");
						return false;
					}
					
				};
			}
			return null;
		}
	}

	public abstract MouseDragControlActionType getActionType();
	
	/**
	 * Handle mouse pressed event, by performing what is required here
	 * Return flag indicating if event has been correctely handled and 
	 * thus, should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller TODO
	 * @param event TODO
	 * @return
	 */
	public abstract boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event);
	
	/**
	 * Handle mouse released event, by performing what is required here
	 * Return flag indicating if event has been correctely handled and 
	 * thus, should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller TODO
	 * @param event TODO
	 * @param isSignificativeDrag TODO
	 * @return
	 */
	public abstract boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event, boolean isSignificativeDrag);
	
	/**
	 * Handle mouse dragged event, by performing what is required here
	 * Return flag indicating if event has been correctely handled and 
	 * thus, should be consumed.
	 * 
	 * @param graphicalRepresentation
	 * @param controller TODO
	 * @param event TODO
	 * @return
	 */
	public abstract boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event);
	

	
	
	public static class None extends MouseDragControlAction
	{
		@Override
		public MouseDragControlActionType getActionType()
		{
			return MouseDragControlActionType.NONE;
		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
		{
			return true;
		}

		@Override
		public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
		{
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event, boolean isSignificativeDrag)
		{
			return false;
		}
}
	

}
