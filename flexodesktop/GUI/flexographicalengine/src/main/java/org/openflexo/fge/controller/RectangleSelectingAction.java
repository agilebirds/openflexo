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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;


public class RectangleSelectingAction extends MouseDragControlAction
{

	static final Logger logger = Logger.getLogger(RectangleSelectingAction.class.getPackage().getName());

	private Point rectangleSelectingOriginInDrawingView;
	private Point currentMousePositionInDrawingView;

	@Override
	public MouseDragControlActionType getActionType()
	{
		return MouseDragControlActionType.RECTANGLE_SELECTING;
	}

	@Override
	public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
	{
		//logger.info("Perform mouse PRESSED on RECTANGLE_SELECTING MouseDragControlAction");		
		rectangleSelectingOriginInDrawingView = SwingUtilities.convertPoint((Component)event.getSource(), event.getPoint(), controller.getDrawingView());
		currentMousePositionInDrawingView = rectangleSelectingOriginInDrawingView;
		if (controller.getDrawingView() == null) return false;
		controller.getDrawingView().setRectangleSelectingAction(this);
		return true;
	}

	@Override
	public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event, boolean isSignificativeDrag)
	{
		//logger.info("Perform mouse RELEASED on RECTANGLE_SELECTING MouseDragControlAction");
		if (isSignificativeDrag) {
			Vector<GraphicalRepresentation> newSelection = buildCurrentSelection(graphicalRepresentation, controller);
			controller.setSelectedObjects(newSelection);
			if (controller.getDrawingView() == null) return false;
			controller.getDrawingView().resetRectangleSelectingAction();
		}
		return true;
	}
	
	@Override
	public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller, MouseEvent event)
	{
		if (logger.isLoggable(Level.FINE))
			logger.fine("Perform mouse DRAGGED on RECTANGLE_SELECTING MouseDragControlAction");
		currentMousePositionInDrawingView = SwingUtilities.convertPoint((Component)event.getSource(), event.getPoint(), controller.getDrawingView());

		Vector<GraphicalRepresentation> newFocusSelection = buildCurrentSelection(graphicalRepresentation, controller);
		controller.setFocusedObjects(newFocusSelection);
		if (controller.getDrawingView() == null) return false;
		controller.getDrawingView().getPaintManager().repaint(controller.getDrawingView());

		return true;
	}

	
	private Vector<GraphicalRepresentation> buildCurrentSelection(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller)
	{
		if (getRectangleSelection() == null) return null;
		Vector<GraphicalRepresentation> returned = new Vector<GraphicalRepresentation>();
		for (GraphicalRepresentation child : graphicalRepresentation.getContainedGraphicalRepresentations()) {
			if (child.getIsVisible()) {
				if (child.isContainedInSelection(getRectangleSelection(), controller.getScale())) {
					returned.add(child);
				}
				if (child instanceof ShapeGraphicalRepresentation) {
					returned.addAll(buildCurrentSelection(child, controller));
				}

			}
		}
		return returned;
	}

    /**
     * Return current rectangle selection
     * 
     * @return Rectangle object as current selection
     */
    private Rectangle getRectangleSelection()
    {
        if (rectangleSelectingOriginInDrawingView != null && currentMousePositionInDrawingView != null) {
            Point origin = new Point();
            Dimension dim = new Dimension();
            if (rectangleSelectingOriginInDrawingView.x <= currentMousePositionInDrawingView.x) {
                origin.x = rectangleSelectingOriginInDrawingView.x;
                dim.width = currentMousePositionInDrawingView.x - rectangleSelectingOriginInDrawingView.x;
            } else {
                origin.x = currentMousePositionInDrawingView.x;
                dim.width = rectangleSelectingOriginInDrawingView.x - currentMousePositionInDrawingView.x;
            }
            if (rectangleSelectingOriginInDrawingView.y <= currentMousePositionInDrawingView.y) {
                origin.y = rectangleSelectingOriginInDrawingView.y;
                dim.height = currentMousePositionInDrawingView.y - rectangleSelectingOriginInDrawingView.y;
            } else {
                origin.y = currentMousePositionInDrawingView.y;
                dim.height = rectangleSelectingOriginInDrawingView.y - currentMousePositionInDrawingView.y;
            }
            return new Rectangle(origin, dim);
        } else {
            return null;
        }
    }
    
	public void paint(Graphics g, DrawingController controller)
	{
		Rectangle selection = getRectangleSelection();
		if (selection == null) return;
		g.setColor(controller.getDrawingGraphicalRepresentation().getRectangleSelectingSelectionColor());
		g.drawRect(selection.x,selection.y,selection.width,selection.height);
	}

}