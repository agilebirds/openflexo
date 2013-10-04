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

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.view.LabelView;

public class LabelViewMouseListener extends FGEViewMouseListener {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(LabelViewMouseListener.class.getPackage().getName());

	// private GraphicalRepresentation graphicalRepresentation;
	// private LabelView<?> labelView;

	public <O> LabelViewMouseListener(DrawingTreeNode<O, ?> node, LabelView<O> aView) {
		/*graphicalRepresentation = aGraphicalRepresentation;
		labelView = aView;*/
		super(node, aView);
	}

	/*public void mouseClicked(MouseEvent e) 
	{
		GraphicalRepresentation focusedObject = getFocusRetriever().getFocusedObject(e);
		
		if (focusedObject == null) {
			getController().clearSelection();
			return;
		}
		
		if (focusedObject.getIsSelectable()) {
			if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK) {
				// Multiple selection
				getController().toogleSelection(focusedObject);
			}
			else {
				getController().setSelectedObject(focusedObject);
			}
		}
	}

	public void mouseEntered(MouseEvent e) 
	{	
		// ignore
	}

	public void mouseExited(MouseEvent e) 
	{
		// ignore
	}
	
	private FloatingLabelDrag currentFloatingLabelDrag = null;
	
	private class FloatingLabelDrag
	{
		private GraphicalRepresentation graphicalRepresentation;
		private Point startMovingLocationInDrawingView;
		private Point startLabelCenterPoint;
		
		private FloatingLabelDrag(GraphicalRepresentation aGraphicalRepresentation,MouseEvent e)
		{
			graphicalRepresentation = aGraphicalRepresentation;
			startMovingLocationInDrawingView = SwingUtilities.convertPoint(
					(Component)e.getSource(), 
					e.getPoint(), 
					labelView.getDrawingView());
			logger.fine("FloatingLabelDrag: start pt = "+startMovingLocationInDrawingView);
			startLabelCenterPoint = aGraphicalRepresentation.getLabelViewCenter(labelView.getScale());

			System.out.println("startMovingPoint: "+startLabelCenterPoint);
		}

		private void moveTo(Point newLocationInDrawingView) 
		{
			Point newLabelCenterPoint = new Point(
					startLabelCenterPoint.x+newLocationInDrawingView.x-startMovingLocationInDrawingView.x,
					startLabelCenterPoint.y+newLocationInDrawingView.y-startMovingLocationInDrawingView.y);

			graphicalRepresentation.setLabelViewCenter(newLabelCenterPoint, labelView.getScale());

		}

		private void stopDragging()
		{
		}
	}


	public void mousePressed(MouseEvent e) 
	{
		GraphicalRepresentation focusedObject = getFocusRetriever().getFocusedObject(e);

		if (focusedObject == null) return;

		if (getFocusRetriever().focusOnFloatingLabel(focusedObject, e)) {
			currentFloatingLabelDrag = new FloatingLabelDrag(focusedObject,e);
			e.consume();
		}
	}

	public void mouseReleased(MouseEvent e) 
	{
		if (currentFloatingLabelDrag != null) {
			currentFloatingLabelDrag.stopDragging();
			currentFloatingLabelDrag = null;
			e.consume();
		}		
	}

	public void mouseDragged(MouseEvent e) 
	{
		if (currentFloatingLabelDrag != null) {
			Point newPointLocation = SwingUtilities.convertPoint(
					(Component)e.getSource(), 
					e.getPoint(), 
					labelView.getDrawingView());
			currentFloatingLabelDrag.moveTo(newPointLocation);
			e.consume();
		}		
	}

	public void mouseMoved(MouseEvent e) 
	{
		getFocusRetriever().handleMouseMove(e);
	}
	
	public DrawingControllerImpl getController() 
	{
		return labelView.getController();
	}

	public FocusRetriever getFocusRetriever()
	{
		return labelView.getDrawingView().getFocusRetriever();
	}

	public Object getDrawable()
	{
		return getGraphicalRepresentation().getDrawable();
	}

	public FGEView getLabelView()
	{
		return labelView;
	}

	public GraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}
	*/

}
