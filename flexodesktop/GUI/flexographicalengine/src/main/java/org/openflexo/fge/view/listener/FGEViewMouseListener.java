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

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.DrawingController.EditorTool;
import org.openflexo.fge.controller.MouseClickControl;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.FGEPaintManager;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.LabelView;
import org.openflexo.toolbox.ToolBox;

public class FGEViewMouseListener implements MouseListener, MouseMotionListener {

	private static final Logger logger = Logger.getLogger(FGEViewMouseListener.class.getPackage().getName());

	private GraphicalRepresentation<?> graphicalRepresentation;
	protected FGEView<?> view;

	public FGEViewMouseListener(GraphicalRepresentation<?> aGraphicalRepresentation, FGEView<?> aView) {
		graphicalRepresentation = aGraphicalRepresentation;
		view = aView;
	}

	private MouseEvent previousEvent;

	@Override
	public void mouseClicked(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			if (e.getClickCount() == 2 && previousEvent != null) {
				if (previousEvent.getClickCount() == 1 && previousEvent.getComponent() == e.getComponent()
						&& previousEvent.getButton() != e.getButton()) {
					e = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), 1,
							e.isPopupTrigger());
				}
			}
		}
		previousEvent = e;

		switch (getController().getCurrentTool()) {
		case SelectionTool:

			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);

			if (getController().hasEditedLabel()) {
				if (handleEventForEditedLabel(e, focusedObject)) {
					return;
					// return;
				}
			}

			if (focusedObject != null && getFocusRetriever().focusOnFloatingLabel(focusedObject, e) && getController().hasEditedLabel()
					&& getController().getEditedLabel().getGraphicalRepresentation() == focusedObject) {
				// Special case, do nothing, since we let the label live its life !!!
				e.consume();
				return;
			}

			if (focusedObject != null && e.getClickCount() == 2 && getFocusRetriever().focusOnFloatingLabel(focusedObject, e)) {
				if (focusedObject instanceof ShapeGraphicalRepresentation) {
					view.getDrawingView().shapeViewForObject((ShapeGraphicalRepresentation<?>) focusedObject).getLabelView().startEdition();
					e.consume();
					return;
				} else if (focusedObject instanceof ConnectorGraphicalRepresentation) {
					view.getDrawingView().connectorViewForObject((ConnectorGraphicalRepresentation<?>) focusedObject).getLabelView()
							.startEdition();
					e.consume();
					return;
				}
			}

			if (focusedObject != null) {
				ControlArea<?> ca = getFocusRetriever().getFocusedControlAreaForDrawable(focusedObject, e);
				if (ca != null && ca.isClickable()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Click on control area " + ca);
					}
					Point clickedLocationInDrawingView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(),
							view.getDrawingView());
					FGEPoint clickedPoint = ca.getGraphicalRepresentation().convertRemoteViewCoordinatesToLocalNormalizedPoint(
							clickedLocationInDrawingView, view.getDrawingView().getGraphicalRepresentation(),
							view.getDrawingView().getScale());
					if (ca.clickOnPoint(clickedPoint, e.getClickCount())) {
						// Event was successfully handled
						e.consume();
						return;
					}
				}
			}

			if (focusedObject == null) {
				focusedObject = graphicalRepresentation.getDrawing().getDrawingGraphicalRepresentation();
			}

			if (view.isDeleted()) {
				return;
			}

			// We have now performed all low-level possible actions, let's go for the registered mouse controls
			for (MouseClickControl mouseClickControl : focusedObject.getMouseClickControls()) {
				if (mouseClickControl.isApplicable(focusedObject, getController(), e)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Applying " + mouseClickControl);
					}
					mouseClickControl.handleClick(focusedObject, getController(), e);
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Ignoring " + mouseClickControl);
					}
				}
			}

			break;

		case DrawShapeTool:

			getController().getDrawShapeToolController().mouseClicked(e);

		default:
			break;
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		// SGU: I dont think that JTextComponent react to these event, but in case of, uncomment this
		// GPO: Actually this is not the case because a mouse enter/exited event in one component does
		// not make sense for another one. If we want to emulate those event, it should be done
		// in the mouse moved event.
		/*if (getController().hasEditedLabel()) {
			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);
			handleEventForEditedLabel(e, focusedObject);
		}*/

	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		// SGU: I dont think that JTextComponent react to these event, but in case of, uncomment this
		// GPO: Actually this is not the case because a mouse enter/exited event in one component does
		// not make sense for another one. If we want to emulate those event, it should be done
		// in the mouse moved event.
		/*if (getController().hasEditedLabel()) {
			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);
			handleEventForEditedLabel(e, focusedObject);
		}*/

	}

	private ControlAreaDrag currentControlAreaDrag = null;

	private class ControlAreaDrag {
		private Point startMovingLocationInDrawingView;
		private FGEPoint startMovingPoint;
		private ControlArea<?> controlArea;
		private double initialWidth;
		private double initialHeight;

		private ControlAreaDrag(ControlArea<?> aControlArea, MouseEvent e) {
			controlArea = aControlArea;
			startMovingLocationInDrawingView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view.getDrawingView());
			logger.fine("ControlPointDrag: start pt = " + startMovingLocationInDrawingView);

			FGEPoint relativeStartMovingPoint = controlArea.getGraphicalRepresentation()
					.convertRemoteViewCoordinatesToLocalNormalizedPoint(startMovingLocationInDrawingView,
							view.getDrawingView().getGraphicalRepresentation(), view.getDrawingView().getScale());
			startMovingPoint = aControlArea.getArea().getNearestPoint(relativeStartMovingPoint);
			Point clickedLocationInDrawingView = SwingUtilities
					.convertPoint((Component) e.getSource(), e.getPoint(), view.getDrawingView());
			aControlArea.startDragging(
					getController(),
					aControlArea.getGraphicalRepresentation().convertRemoteViewCoordinatesToLocalNormalizedPoint(
							clickedLocationInDrawingView, view.getDrawingView().getGraphicalRepresentation(),
							view.getDrawingView().getScale()));
			if (controlArea.getGraphicalRepresentation().isConnectedToDrawing()) {
				initialWidth = controlArea.getGraphicalRepresentation().getViewWidth(view.getScale());
				initialHeight = controlArea.getGraphicalRepresentation().getViewHeight(view.getScale());
			} else {
				// System.out.println("Not connected to drawing");
				initialWidth = 0;
				initialHeight = 0;
			}
		}

		private boolean moveTo(Point newLocationInDrawingView, MouseEvent e) {

			FGEPoint newAbsoluteLocation = new FGEPoint(startMovingPoint.x
					+ (newLocationInDrawingView.x - startMovingLocationInDrawingView.x) / view.getScale(), startMovingPoint.y
					+ (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / view.getScale());

			FGEPoint newRelativeLocation = getGraphicalRepresentation().getDrawingGraphicalRepresentation()
					.convertLocalViewCoordinatesToRemoteNormalizedPoint(newLocationInDrawingView, controlArea.getGraphicalRepresentation(),
							view.getScale());

			FGEPoint pointRelativeToInitialConfiguration = new FGEPoint(startMovingPoint.x
					+ (newLocationInDrawingView.x - startMovingLocationInDrawingView.x) / initialWidth/* *view.getScale()*/,
					startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / initialHeight/* *view.getScale()*/);
			return controlArea.dragToPoint(newRelativeLocation, pointRelativeToInitialConfiguration, newAbsoluteLocation, startMovingPoint,
					e);
		}

		private void stopDragging(GraphicalRepresentation focusedGR) {
			controlArea.stopDragging(getController(), focusedGR);
		}
	}

	private FloatingLabelDrag currentFloatingLabelDrag = null;

	private class FloatingLabelDrag {
		private GraphicalRepresentation<?> graphicalRepresentation;
		private Point startMovingLocationInDrawingView;
		private Point startLabelPoint;

		private boolean started = false;

		private FloatingLabelDrag(GraphicalRepresentation<?> aGraphicalRepresentation, Point startMovingLocationInDrawingView) {
			graphicalRepresentation = aGraphicalRepresentation;
			this.startMovingLocationInDrawingView = startMovingLocationInDrawingView;
			logger.fine("FloatingLabelDrag: start pt = " + startMovingLocationInDrawingView);
			startLabelPoint = graphicalRepresentation.getLabelLocation(view.getScale());
		}

		private void startDragging() {
			graphicalRepresentation.notifyLabelWillMove();
		}

		private void moveTo(Point newLocationInDrawingView) {
			if (!started) {
				startDragging();
				started = true;
			}
			Point newLabelCenterPoint = new Point(startLabelPoint.x + newLocationInDrawingView.x - startMovingLocationInDrawingView.x,
					startLabelPoint.y + newLocationInDrawingView.y - startMovingLocationInDrawingView.y);
			graphicalRepresentation.setLabelLocation(newLabelCenterPoint, view.getScale());

			/*if (graphicalRepresentation instanceof ShapeGraphicalRepresentation
					&& ((ShapeGraphicalRepresentation)graphicalRepresentation).isParentLayoutedAsContainer()) {
				Point resultingLocation = graphicalRepresentation.getLabelViewCenter(view.getScale());
				if (!resultingLocation.equals(newLabelCenterPoint)) {
					int dx = resultingLocation.x-newLabelCenterPoint.x;
					int dy = resultingLocation.y-newLabelCenterPoint.y;
					startLabelCenterPoint.x = startLabelCenterPoint.x+dx;
					startLabelCenterPoint.y = startLabelCenterPoint.y+dy;
				}
			}*/
		}

		private void stopDragging() {
			if (getPaintManager().isPaintingCacheEnabled()) {
				getPaintManager().removeFromTemporaryObjects(graphicalRepresentation);
				getPaintManager().invalidate(graphicalRepresentation);
				getPaintManager().repaint(view.getDrawingView());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);

		if (focusedObject == null) {
			focusedObject = graphicalRepresentation.getDrawing().getDrawingGraphicalRepresentation();
		}

		if (getController().hasEditedLabel()) {
			if (handleEventForEditedLabel(e, focusedObject)) {
				// Special case, do nothing, since we let the label live its life !!!
				return;
			}
		}
		getController().stopEditionOfEditedLabelIfAny();
		if (focusedObject.hasFloatingLabel() && getFocusRetriever().focusOnFloatingLabel(focusedObject, e)) {
			currentFloatingLabelDrag = new FloatingLabelDrag(focusedObject, SwingUtilities.convertPoint((Component) e.getSource(),
					e.getPoint(), view.getDrawingView()));
			e.consume();
			return;
		} else {
			ControlArea<?> ca = getFocusRetriever().getFocusedControlAreaForDrawable(focusedObject, e);
			if (ca != null && ca.isDraggable()) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Starting drag of control point " + ca);
				}
				currentControlAreaDrag = new ControlAreaDrag(ca, e);
				e.consume();
				return;
			}
		}

		if (view.isDeleted()) {
			return;
		}

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		List<MouseDragControl> applicableMouseDragControls = new ArrayList<MouseDragControl>();
		for (MouseDragControl mouseDragControl : focusedObject.getMouseDragControls()) {
			if (mouseDragControl.isApplicable(focusedObject, getController(), e)) {
				applicableMouseDragControls.add(mouseDragControl);
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Found applicable " + mouseDragControl);
				}
			} else {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Ignoring " + mouseDragControl);
				}
			}
		}

		if (applicableMouseDragControls.size() == 0) {
			// No applicable mouse drag
			return;
		}

		if (applicableMouseDragControls.size() > 1) {
			logger.warning("More than one applicable MouseDragControl for graphical representation: " + focusedObject
					+ " Applying first and forgetting others...");
		}

		// Apply applicable mouse drag control
		MouseDragControl currentMouseDrag = applicableMouseDragControls.get(0);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Applying " + currentMouseDrag);
		}
		if (currentMouseDrag.handleMousePressed(focusedObject, getController(), e)) {
			// Everything OK
			if (getController() != null) {
				getController().setCurrentMouseDrag(currentMouseDrag);
			}
		} else {
			// Something failed, abort this drag
			if (getController() != null) {
				getController().setCurrentMouseDrag(null);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		if (getController().hasEditedLabel()) {
			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);
			if (handleEventForEditedLabel(e, focusedObject)) {
				return;
			}
			// Special case, do nothing, since we let the label live its life !!!
			e.consume();
			return;
		}

		/*if (getController().hasEditedLabel()) {
		// Special case, do nothing, since we let the label live its life !!!
		e.consume();
		return;
		}*/

		if (currentFloatingLabelDrag != null) {
			currentFloatingLabelDrag.stopDragging();
			currentFloatingLabelDrag = null;
			e.consume();
		}

		if (currentControlAreaDrag != null) {
			GraphicalRepresentation focusedGR = getFocusRetriever().getFocusedObject(e);
			// logger.info("Stop dragging, focused on " + focusedGR.getDrawable());
			currentControlAreaDrag.stopDragging(focusedGR);
			currentControlAreaDrag = null;
			e.consume();
		}

		if (view.isDeleted()) {
			return;
		}

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		if (getController().getCurrentMouseDrag() != null) {
			DrawingController<?> controller = getController();
			controller.getCurrentMouseDrag().handleMouseReleased(getController(), e);
			controller.setCurrentMouseDrag(null);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}
		if (getController().hasEditedLabel()) {
			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);
			if (handleEventForEditedLabel(e, focusedObject)) {
				return;
			}
			// Special case, do nothing, since we let the label live its life !!!
			e.consume();
			return;
		}

		/*if (getController().hasEditedLabel()) {
		// Special case, do nothing, since we let the label live its life !!!
		e.consume();
		return;
		}*/

		if (currentFloatingLabelDrag != null) {
			Point newPointLocation = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view.getDrawingView());
			currentFloatingLabelDrag.moveTo(newPointLocation);
			e.consume();
		}

		if (currentControlAreaDrag != null) {
			Point newPointLocation = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), view.getDrawingView());
			boolean continueDragging = currentControlAreaDrag.moveTo(newPointLocation, e);
			e.consume();
			if (!continueDragging) {
				GraphicalRepresentation focusedGR = getFocusRetriever().getFocusedObject(e);
				// logger.info("Stop dragging, focused on " + focusedGR.getDrawable());
				currentControlAreaDrag.stopDragging(focusedGR);
				logger.fine("OK, stopping dragging point");
				currentControlAreaDrag = null;
			}
		}

		// We have now performed all low-level possible actions, let's go for the registered mouse controls
		getFocusRetriever().handleMouseMove(e);
		if (getController().getCurrentMouseDrag() != null) {
			getController().getCurrentMouseDrag().handleMouseDragged(getController(), e);
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		if (getController().hasEditedLabel()) {
			GraphicalRepresentation<?> focusedObject = getFocusRetriever().getFocusedObject(e);
			if (handleEventForEditedLabel(e, focusedObject)) {
				return;
				// Special case, do nothing, since we let the label live its life !!!
				/*e.consume();
				return;*/
			}
		}

		/*if (getController().hasEditedLabel()) {
		// Special case, do nothing, since we let the label live its life !!!
		e.consume();
		return;
		}*/

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		if (getController().getCurrentMouseDrag() != null) {
			getController().getCurrentMouseDrag().handleMouseDragged(getController(), e);
		}

		getFocusRetriever().handleMouseMove(e);

		if (getController().getCurrentTool() == EditorTool.DrawShapeTool) {
			getController().getDrawShapeToolController().mouseMoved(e);
		}

	}

	private Stack<MouseEvent> eventStack;

	/**
	 * What happen here ? (SGU)
	 * 
	 * Well, it's a long and difficult story. We have here a totally different view paradigm from Swing (where all view are rectangle).
	 * Here, we manage transparency, layers and complex shapes. That means that the mouse listener is sometimes not belonging to the view
	 * displayed accessed object.
	 * 
	 * But we use swing in the context of text edition. Sometimes, we receive mouse events regarding JTextComponent management on some views
	 * that have nothing to do with the label this JTextComponent is representing. (focusedObject is not necessary object represented by the
	 * view)
	 * 
	 * So, we have here to implement a re-targeting scheme for those events, for swing to correctly handle those events.
	 * 
	 * @param e
	 * @param focusedObject
	 * @return
	 */
	private boolean handleEventForEditedLabel(MouseEvent e, GraphicalRepresentation<?> focusedObject) {
		LabelView<?> labelView = getController().getEditedLabel();
		Point pointRelativeToTextComponent = SwingUtilities.convertPoint((Component) view, e.getPoint(), labelView);
		if (labelView.getGraphicalRepresentation() == focusedObject) {

			// Label being edited matches focused object:
			// We potentially need to redispatch this event
			if (labelView.contains(pointRelativeToTextComponent)) {
				if (!labelView.isMouseInsideLabel()) {
					MouseEvent newEvent = new MouseEvent(labelView.getTextComponent(), MouseEvent.MOUSE_ENTERED, e.getWhen(),
							e.getModifiers(), pointRelativeToTextComponent.x, pointRelativeToTextComponent.y, e.getClickCount(),
							e.isPopupTrigger());
					labelView.getTextComponent().dispatchEvent(newEvent);
				}
				if (labelView.isEditing()) {
					if (eventStack == null || eventStack.isEmpty() || eventStack.peek() != e) {
						// This event effectively concerns related text component
						// I will retarget it !

						MouseEvent newEvent = new MouseEvent(labelView.getTextComponent(), e.getID(), e.getWhen(), e.getModifiers(),
								pointRelativeToTextComponent.x, pointRelativeToTextComponent.y, e.getClickCount(), e.isPopupTrigger());
						if (eventStack == null) {
							eventStack = new Stack<MouseEvent>();
						}
						eventStack.add(newEvent);
						labelView.getTextComponent().dispatchEvent(newEvent);
						eventStack.pop();
						if (eventStack.isEmpty()) {
							eventStack = null;
						}
						e.consume();
						return true;
					}
				}
			} else {
				triggerMouseExitedIfNeeded(e, labelView, pointRelativeToTextComponent);
			}
			return false;
		} else {
			triggerMouseExitedIfNeeded(e, labelView, pointRelativeToTextComponent);
		}

		return false;

	}

	private void triggerMouseExitedIfNeeded(MouseEvent e, LabelView<?> labelView, Point pointRelativeToTextComponent) {
		if (labelView.isMouseInsideLabel()) {
			MouseEvent newEvent = new MouseEvent(labelView.getTextComponent(), MouseEvent.MOUSE_EXITED, e.getWhen(), e.getModifiers(),
					pointRelativeToTextComponent.x, pointRelativeToTextComponent.y, e.getClickCount(), e.isPopupTrigger());
			labelView.getTextComponent().dispatchEvent(newEvent);
		}
	}

	public DrawingController<?> getController() {
		return view.getController();
	}

	public FocusRetriever getFocusRetriever() {
		return view.getDrawingView().getFocusRetriever();
	}

	public Object getDrawable() {
		return getGraphicalRepresentation().getDrawable();
	}

	public FGEView<?> getView() {
		return view;
	}

	public GraphicalRepresentation<?> getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public FGEPaintManager getPaintManager() {
		return view.getPaintManager();
	}

}
