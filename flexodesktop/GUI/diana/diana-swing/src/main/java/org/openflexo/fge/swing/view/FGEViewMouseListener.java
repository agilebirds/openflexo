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
package org.openflexo.fge.swing.view;

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

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseClickControl;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.MouseDragControl;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.control.JFocusRetriever;
import org.openflexo.fge.swing.control.JMouseControlContext;
import org.openflexo.fge.swing.paint.FGEPaintManager;
import org.openflexo.toolbox.ToolBox;

public class FGEViewMouseListener implements MouseListener, MouseMotionListener {

	private static final Logger logger = Logger.getLogger(FGEViewMouseListener.class.getPackage().getName());

	private DrawingTreeNode<?, ?> node;
	protected JFGEView<?, ?> view;
	private MouseEvent previousEvent;

	public <O> FGEViewMouseListener(DrawingTreeNode<O, ?> node, JFGEView<O, ?> aView) {
		this.node = node;
		view = aView;
	}

	public DrawingTreeNode<?, ?> getNode() {
		return node;
	}

	public JDrawingView<?> getDrawingView() {
		if (getController() != null) {
			return (JDrawingView<?>) getController().getDrawingView();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public DrawShapeToolController<?, MouseEvent> getDrawShapeToolController() {
		return (DrawShapeToolController<?, MouseEvent>) ((DianaInteractiveEditor<?, ?, ?>) getController()).getDrawShapeToolController();
	}

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
		boolean editable = getController().getDrawing().isEditable();

		boolean performSelectionTool = getController() instanceof DianaInteractiveViewer;

		if (getController() instanceof DianaInteractiveEditor) {
			switch (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool()) {
			case DrawShapeTool:
				performSelectionTool = false;
				if (editable) {
					getDrawShapeToolController().mouseClicked(e);
				}
				break;
			case DrawConnectorTool:
				performSelectionTool = false;
				break;
			case DrawTextTool:
				performSelectionTool = false;
				break;
			case SelectionTool:
				performSelectionTool = true;
				break;
			}

		}

		if (performSelectionTool && getFocusRetriever() != null) {

			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) getController();
			DrawingTreeNode<?, ?> focusedObject = getFocusRetriever().getFocusedObject(e);
			if (focusedObject == null) {
				focusedObject = node.getDrawing().getRoot();
			}
			editable &= focusedObject != null && !focusedObject.getGraphicalRepresentation().getIsReadOnly();
			if (editable) {
				if (controller.hasEditedLabel()) {
					if (handleEventForEditedLabel(e, focusedObject)) {
						return;
						// return;
					}
				}

				if (focusedObject != null && getFocusRetriever().focusOnFloatingLabel(focusedObject, e) && controller.hasEditedLabel()
						&& controller.getEditedLabel().getNode() == focusedObject) {
					// Special case, do nothing, since we let the label live its life !!!
					e.consume();
					return;
				}

				if (focusedObject != null && e.getClickCount() == 2 && getFocusRetriever().focusOnFloatingLabel(focusedObject, e)) {
					if (focusedObject instanceof ShapeNode) {
						view.getDrawingView().shapeViewForNode((ShapeNode<?>) focusedObject).getLabelView().startEdition();
						e.consume();
						return;
					} else if (focusedObject instanceof ConnectorNode) {
						view.getDrawingView().connectorViewForNode((ConnectorNode<?>) focusedObject).getLabelView().startEdition();
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
								getDrawingView());
						FGEPoint clickedPoint = ca.getNode().convertRemoteViewCoordinatesToLocalNormalizedPoint(
								clickedLocationInDrawingView, view.getDrawingView().getDrawing().getRoot(),
								view.getDrawingView().getScale());
						if (ca.clickOnPoint(clickedPoint, e.getClickCount())) {
							// Event was successfully handled
							e.consume();
							return;
						}
					}
				}
			}

			if (view.isDeleted()) {
				return;
			}

			MouseControlContext mcc = new JMouseControlContext(e);

			// We have now performed all low-level possible actions, let's go for the registered mouse controls
			for (MouseClickControl<?> mouseClickControl : focusedObject.getGraphicalRepresentation().getMouseClickControls()) {
				if (((MouseClickControl<DianaInteractiveViewer<?, ?, ?>>) mouseClickControl).isApplicable(focusedObject, controller, mcc)) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Applying " + mouseClickControl);
					}
					((MouseClickControl<DianaInteractiveViewer<?, ?, ?>>) mouseClickControl).handleClick(focusedObject,
							(DianaInteractiveViewer<?, ?, ?>) getController(), mcc);
				} else {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Ignoring " + mouseClickControl);
					}
				}
			}

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
			GraphicalRepresentation focusedObject = getFocusRetriever().getFocusedObject(e);
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
			GraphicalRepresentation focusedObject = getFocusRetriever().getFocusedObject(e);
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
			startMovingLocationInDrawingView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
			logger.fine("ControlPointDrag: start pt = " + startMovingLocationInDrawingView);

			FGEPoint relativeStartMovingPoint = controlArea.getNode().convertRemoteViewCoordinatesToLocalNormalizedPoint(
					startMovingLocationInDrawingView, getDrawingView().getDrawing().getRoot(), getDrawingView().getScale());
			startMovingPoint = aControlArea.getArea().getNearestPoint(relativeStartMovingPoint);
			Point clickedLocationInDrawingView = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
			aControlArea.startDragging(
					getController(),
					aControlArea.getNode().convertRemoteViewCoordinatesToLocalNormalizedPoint(clickedLocationInDrawingView,
							getDrawingView().getDrawing().getRoot(), getDrawingView().getScale()));
			if (controlArea.getNode().isConnectedToDrawing()) {
				initialWidth = controlArea.getNode().getViewWidth(view.getScale());
				initialHeight = controlArea.getNode().getViewHeight(view.getScale());
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

			FGEPoint newRelativeLocation = getDrawingView().getDrawing().getRoot()
					.convertLocalViewCoordinatesToRemoteNormalizedPoint(newLocationInDrawingView, controlArea.getNode(), view.getScale());

			FGEPoint pointRelativeToInitialConfiguration = new FGEPoint(startMovingPoint.x
					+ (newLocationInDrawingView.x - startMovingLocationInDrawingView.x) / initialWidth/* *view.getScale()*/,
					startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / initialHeight/* *view.getScale()*/);
			return controlArea.dragToPoint(newRelativeLocation, pointRelativeToInitialConfiguration, newAbsoluteLocation, startMovingPoint,
					e);
		}

		private void stopDragging(DrawingTreeNode<?, ?> focused) {
			controlArea.stopDragging(getController(), focused);
		}
	}

	private FloatingLabelDrag currentFloatingLabelDrag = null;

	private class FloatingLabelDrag {
		private DrawingTreeNode<?, ?> node;
		private Point startMovingLocationInDrawingView;
		private Point startLabelPoint;

		private boolean started = false;

		private FloatingLabelDrag(DrawingTreeNode<?, ?> aNode, Point startMovingLocationInDrawingView) {
			node = aNode;
			this.startMovingLocationInDrawingView = startMovingLocationInDrawingView;
			logger.fine("FloatingLabelDrag: start pt = " + startMovingLocationInDrawingView);
			startLabelPoint = node.getLabelLocation(view.getScale());
		}

		private void startDragging() {
			node.notifyLabelWillMove();
		}

		private void moveTo(Point newLocationInDrawingView) {
			if (!started) {
				startDragging();
				started = true;
			}
			Point newLabelCenterPoint = new Point(startLabelPoint.x + newLocationInDrawingView.x - startMovingLocationInDrawingView.x,
					startLabelPoint.y + newLocationInDrawingView.y - startMovingLocationInDrawingView.y);
			node.setLabelLocation(newLabelCenterPoint, view.getScale());

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
				getPaintManager().removeFromTemporaryObjects(node);
				getPaintManager().invalidate(node);
				getPaintManager().repaint(getDrawingView());
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (view.isDeleted()) {
			return;
		}
		boolean editable = getController().getDrawing().isEditable();
		DrawingTreeNode<?, ?> focusedObject = getFocusRetriever() != null ? getFocusRetriever().getFocusedObject(e) : null;

		if (focusedObject == null) {
			focusedObject = node.getDrawing().getRoot();
		}

		editable &= !focusedObject.getGraphicalRepresentation().getIsReadOnly();

		if (editable && getFocusRetriever() != null) {
			if (((DianaInteractiveViewer<?, ?, ?>) getController()).hasEditedLabel()) {
				if (handleEventForEditedLabel(e, focusedObject)) {
					// Special case, do nothing, since we let the label live its life !!!
					return;
				}
			}
			((DianaInteractiveViewer<?, ?, ?>) getController()).stopEditionOfEditedLabelIfAny();
			if (focusedObject.hasFloatingLabel() && getFocusRetriever().focusOnFloatingLabel(focusedObject, e)) {
				currentFloatingLabelDrag = new FloatingLabelDrag(focusedObject, SwingUtilities.convertPoint((Component) e.getSource(),
						e.getPoint(), getDrawingView()));
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
		}

		MouseControlContext mcc = new JMouseControlContext(e);

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		if (getController() instanceof DianaInteractiveViewer) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) getController();

			List<MouseDragControl<DianaInteractiveViewer<?, ?, ?>>> applicableMouseDragControls = new ArrayList<MouseDragControl<DianaInteractiveViewer<?, ?, ?>>>();
			for (MouseDragControl<?> mouseDragControl : focusedObject.getGraphicalRepresentation().getMouseDragControls()) {
				if (((MouseDragControl<DianaInteractiveViewer<?, ?, ?>>) mouseDragControl).isApplicable(focusedObject, controller, mcc)) {
					applicableMouseDragControls.add((MouseDragControl<DianaInteractiveViewer<?, ?, ?>>) mouseDragControl);
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
				logger.warning("More than one applicable CustomMouseDragControlImpl for graphical representation: " + focusedObject
						+ " Applying first and forgetting others...");
			}

			// Apply applicable mouse drag control
			MouseDragControl<DianaInteractiveViewer<?, ?, ?>> currentMouseDrag = applicableMouseDragControls.get(0);
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Applying " + currentMouseDrag);
			}
			if (currentMouseDrag.handleMousePressed(focusedObject, controller, mcc)) {
				// Everything OK
				if (getController() != null) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).setCurrentMouseDrag(currentMouseDrag);
				}
			} else {
				// Something failed, abort this drag
				if (getController() != null) {
					((DianaInteractiveViewer<?, ?, ?>) getController()).setCurrentMouseDrag(null);
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		if ((getController() instanceof DianaInteractiveViewer) && ((DianaInteractiveViewer<?, ?, ?>) getController()).hasEditedLabel()) {
			DrawingTreeNode<?, ?> focusedObject = getFocusRetriever() != null ? getFocusRetriever().getFocusedObject(e) : null;
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
			DrawingTreeNode<?, ?> focused = getFocusRetriever().getFocusedObject(e);
			// logger.info("Stop dragging, focused on " + focusedGR.getDrawable());
			currentControlAreaDrag.stopDragging(focused);
			currentControlAreaDrag = null;
			e.consume();
		}

		if (view.isDeleted()) {
			return;
		}

		MouseControlContext mcc = new JMouseControlContext(e);

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		if ((getController() instanceof DianaInteractiveViewer)
				&& ((DianaInteractiveViewer<?, ?, ?>) getController()).getCurrentMouseDrag() != null) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) getController();
			controller.getCurrentMouseDrag().handleMouseReleased(controller, mcc);
			controller.setCurrentMouseDrag(null);
		}

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		boolean editable = getController().getDrawing().isEditable();
		if (editable && getFocusRetriever() != null) {
			if ((getController() instanceof DianaInteractiveViewer) && ((DianaInteractiveViewer<?, ?, ?>) getController()).hasEditedLabel()) {
				DrawingTreeNode<?, ?> focused = getFocusRetriever().getFocusedObject(e);
				if (handleEventForEditedLabel(e, focused)) {
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
				Point newPointLocation = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
				currentFloatingLabelDrag.moveTo(newPointLocation);
				e.consume();
			}

			if (currentControlAreaDrag != null) {
				Point newPointLocation = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), getDrawingView());
				boolean continueDragging = currentControlAreaDrag.moveTo(newPointLocation, e);
				e.consume();
				if (!continueDragging) {
					DrawingTreeNode<?, ?> focused = getFocusRetriever().getFocusedObject(e);
					// logger.info("Stop dragging, focused on " + focusedGR.getDrawable());
					currentControlAreaDrag.stopDragging(focused);
					logger.fine("OK, stopping dragging point");
					currentControlAreaDrag = null;
				}
			}
		}

		if (getFocusRetriever() != null) {
			getFocusRetriever().handleMouseMove(e);
		}

		MouseControlContext mcc = new JMouseControlContext(e);

		// We have now performed all low-level possible actions, let's go for the registered mouse controls

		if ((getController() instanceof DianaInteractiveViewer)
				&& ((DianaInteractiveViewer<?, ?, ?>) getController()).getCurrentMouseDrag() != null) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) getController();
			controller.getCurrentMouseDrag().handleMouseDragged(controller, mcc);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (view.isDeleted()) {
			return;
		}

		if ((getController() instanceof DianaInteractiveViewer) && ((DianaInteractiveViewer<?, ?, ?>) getController()).hasEditedLabel()) {
			DrawingTreeNode<?, ?> focused = getFocusRetriever().getFocusedObject(e);
			if (handleEventForEditedLabel(e, focused)) {
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

		MouseControlContext mcc = new JMouseControlContext(e);

		if ((getController() instanceof DianaInteractiveViewer)
				&& ((DianaInteractiveViewer<?, ?, ?>) getController()).getCurrentMouseDrag() != null) {
			DianaInteractiveViewer<?, ?, ?> controller = (DianaInteractiveViewer<?, ?, ?>) getController();
			controller.getCurrentMouseDrag().handleMouseDragged(controller, mcc);
		}

		if (getFocusRetriever() != null) {
			getFocusRetriever().handleMouseMove(e);
		}

		if ((getController() instanceof DianaInteractiveEditor)
				&& (((DianaInteractiveEditor<?, ?, ?>) getController()).getCurrentTool() == EditorTool.DrawShapeTool)) {
			DianaInteractiveEditor<?, ?, ?> controller = (DianaInteractiveEditor<?, ?, ?>) getController();
			((DrawShapeToolController<?, MouseEvent>) controller.getDrawShapeToolController()).mouseMoved(e);
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
	private boolean handleEventForEditedLabel(MouseEvent e, DrawingTreeNode<?, ?> focusedObject) {
		if (focusedObject == null || !focusedObject.getDrawing().isEditable() || !(getController() instanceof DianaInteractiveViewer)) {
			return false;
		}
		JLabelView<?> labelView = (JLabelView<?>) ((DianaInteractiveViewer<?, ?, ?>) getController()).getEditedLabel();
		Point pointRelativeToTextComponent = SwingUtilities.convertPoint((Component) view, e.getPoint(), labelView);
		if (labelView.getNode() == focusedObject) {

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

	private void triggerMouseExitedIfNeeded(MouseEvent e, JLabelView<?> labelView, Point pointRelativeToTextComponent) {
		if (labelView.isMouseInsideLabel()) {
			MouseEvent newEvent = new MouseEvent(labelView.getTextComponent(), MouseEvent.MOUSE_EXITED, e.getWhen(), e.getModifiers(),
					pointRelativeToTextComponent.x, pointRelativeToTextComponent.y, e.getClickCount(), e.isPopupTrigger());
			labelView.getTextComponent().dispatchEvent(newEvent);
		}
	}

	public AbstractDianaEditor<?, ?, ?> getController() {
		return view.getController();
	}

	/**
	 * Return the focus retriever for this controller<br>
	 * Note that only DianaInteractiveViewer and subclasses manage a FocusRetriever and thus, if this method is called for a controller
	 * which does not specialize DianaInteractiveViewer, null will be returned
	 * 
	 * @return
	 */
	public JFocusRetriever getFocusRetriever() {
		if (getController() instanceof DianaInteractiveViewer) {
			return getDrawingView().getFocusRetriever();
		}
		return null;
	}

	public Object getDrawable() {
		return getView().getDrawable();
	}

	public JFGEView<?, ?> getView() {
		return view;
	}

	/*public GraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}*/

	public FGEPaintManager getPaintManager() {
		return view.getDrawingView().getPaintManager();
	}

}
