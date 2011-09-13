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
package org.openflexo.fge.view.listener;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.view.DrawingView;


public class DrawingViewMouseListener extends FGEViewMouseListener {

	private static final Logger logger = Logger.getLogger(DrawingViewMouseListener.class.getPackage().getName());
	
	/*private boolean isRectangleSelecting = false;
	private Point rectangleSelectingOrigin;
	private Point currentMousePosition;*/
	
	public DrawingViewMouseListener(DrawingGraphicalRepresentation aDrawingGraphicalRepresentation, DrawingView aView)
	{
		super(aDrawingGraphicalRepresentation,aView);
	}
	
	public Drawing getDrawing()
	{
		return getGraphicalRepresentation().getDrawing();
	}

	@Override
	public DrawingView getView()
	{
		return (DrawingView)super.getView();
	}


	/*public void mouseClicked(MouseEvent e) 
	{
		getController().clearSelection();
		getController().selectDrawing();
	}*/

	/*public void mousePressed(MouseEvent e) 
	{
		isRectangleSelecting = true;
		rectangleSelectingOrigin = e.getPoint();
	}

	public void mouseReleased(MouseEvent e)
	{
		isRectangleSelecting = false;
		getView().repaint();
	}

	public void mouseDragged(MouseEvent e) 
	{
		if (isRectangleSelecting) {
			currentMousePosition = e.getPoint();
			getView().repaint();
		}
	}

    public Rectangle getRectangleSelection()
    {
        if (rectangleSelectingOrigin != null && currentMousePosition != null) {
            Point origin = new Point();
            Dimension dim = new Dimension();
            if (rectangleSelectingOrigin.x <= currentMousePosition.x) {
                origin.x = rectangleSelectingOrigin.x;
                dim.width = currentMousePosition.x - rectangleSelectingOrigin.x;
            } else {
                origin.x = currentMousePosition.x;
                dim.width = rectangleSelectingOrigin.x - currentMousePosition.x;
            }
            if (rectangleSelectingOrigin.y <= currentMousePosition.y) {
                origin.y = rectangleSelectingOrigin.y;
                dim.height = currentMousePosition.y - rectangleSelectingOrigin.y;
            } else {
                origin.y = currentMousePosition.y;
                dim.height = rectangleSelectingOrigin.y - currentMousePosition.y;
            }
            return new Rectangle(origin, dim);
        } else {
            return null;
        }
    }

	public boolean isRectangleSelecting()
	{
		return isRectangleSelecting;
	}
*/

}
