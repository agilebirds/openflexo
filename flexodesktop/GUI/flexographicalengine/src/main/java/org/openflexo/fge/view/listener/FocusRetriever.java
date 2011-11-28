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
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.ShapeView;

public class FocusRetriever {

	private static final Logger logger = Logger.getLogger(FocusRetriever.class.getPackage().getName());

	private DrawingView<?> drawingView;

	public FocusRetriever(DrawingView aDrawingView) {
		drawingView = aDrawingView;
	}

	private boolean cursorChanged = false;

	private void resetCursorIfRequired() {
		if (cursorChanged) {
			Component frame = SwingUtilities.getAncestorOfClass(Window.class, drawingView);
			frame.setCursor(Cursor.getDefaultCursor());
			cursorChanged = false;
			getController()._setFocusedControlArea(null);
		}
	}

	public DrawingController<?> getController() {
		return drawingView.getController();
	}

	public void handleMouseMove(MouseEvent event) {
		GraphicalRepresentation newFocusedObject = getFocusedObject(event);

		if (newFocusedObject != null) {
			drawingView.getController().setFocusedFloatingLabel(focusOnFloatingLabel(newFocusedObject, event) ? newFocusedObject : null);
			ControlArea cp = getFocusedControlAreaForDrawable(newFocusedObject, event);
			if (cp != null) {
				Component frame = SwingUtilities.getAncestorOfClass(Window.class, drawingView);
				frame.setCursor(cp.getDraggingCursor());
				cursorChanged = true;
				getController()._setFocusedControlArea(cp);
			} else {
				resetCursorIfRequired();
			}
		} else {
			if (drawingView.getController().getFocusedFloatingLabel() != null) {
				drawingView.getController().setFocusedFloatingLabel(null);
			}
			resetCursorIfRequired();
		}

		// if (newFocusedObject != drawingView.getController().getFocusedObject()) {
		drawingView.getController().setFocusedObject(newFocusedObject);
		// }

	}

	public boolean focusOnFloatingLabel(GraphicalRepresentation<?> graphicalRepresentation, MouseEvent event) {
		return focusOnFloatingLabel(graphicalRepresentation, (Component) event.getSource(), event.getPoint());
	}

	private boolean focusOnFloatingLabel(GraphicalRepresentation<?> graphicalRepresentation, Component eventSource, Point eventLocation) {
		// if (!graphicalRepresentation.hasText()) return false;

		if (graphicalRepresentation instanceof GeometricGraphicalRepresentation) {
			return false;
		}

		FGEView view = drawingView.viewForObject(graphicalRepresentation);
		FGEView containerView = drawingView.viewForObject(graphicalRepresentation.getContainerGraphicalRepresentation());
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) containerView);
		if (graphicalRepresentation.getHasText()) {
			if (view instanceof ShapeView) {
				return ((ShapeView) view).getLabelView().getBounds().contains(p);
			}

			if (view instanceof ConnectorView) {
				return ((ConnectorView) view).getLabelView().getBounds().contains(p);
			}
		}
		return false;

	}

	public ControlArea<?> getFocusedControlAreaForDrawable(GraphicalRepresentation graphicalRepresentation, MouseEvent event) {
		return getFocusedControlAreaForDrawable(graphicalRepresentation, drawingView.getGraphicalRepresentation(), event);
	}

	public ControlArea<?> getFocusedControlAreaForDrawable(GraphicalRepresentation<?> graphicalRepresentation,
			GraphicalRepresentation<?> container, MouseEvent event) {
		ControlArea returned = null;
		double selectionDistance = FGEConstants.SELECTION_DISTANCE; // Math.max(5.0,FGEConstants.SELECTION_DISTANCE*getScale());
		if (graphicalRepresentation instanceof GeometricGraphicalRepresentation) {
			GeometricGraphicalRepresentation<?> gr = (GeometricGraphicalRepresentation<?>) graphicalRepresentation;
			Point viewPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					(Component) drawingView.viewForObject(container));
			// FGEPoint point = graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(viewPoint, getScale());

			// Look if we are near a CP
			double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
			for (ControlPoint cp : gr.getControlPoints()) {
				// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
				// double cpDistance = Point2D.distance(pt1.x,pt1.y,viewPoint.x,viewPoint.y);
				double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
				if (cpDistance < selectionDistance && cpDistance < distanceToNearestGeometricObject
						&& (returned == null || getController().preferredFocusedControlArea(returned, cp) == cp)) {
					distanceToNearestGeometricObject = cpDistance;
					returned = cp;
				}
			}
			return returned;
		}

		FGEView view = drawingView.viewForObject(container);
		Point p = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
		FGEView v = drawingView.viewForObject(graphicalRepresentation);
		Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
		FGEPoint p3 = v.getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(p2, getScale());

		if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
			ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) graphicalRepresentation;
			if (Double.isNaN(p3.getX()) && gr.getWidth() == 0) {
				p3.x = 1;
			}
			if (Double.isNaN(p3.getY()) && gr.getHeight() == 0) {
				p3.y = 1;
			}
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea ca : gr.getControlAreas()) {
				// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
				// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
				// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
				double cpDistance = ca.getDistanceToArea(p3, getScale());
				if (cpDistance < selectionDistance && cpDistance < smallestDistance
						&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
					returned = ca;
					smallestDistance = cpDistance;
				}
			}
		} else if (graphicalRepresentation instanceof ConnectorGraphicalRepresentation) {
			ConnectorGraphicalRepresentation<?> gr = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea ca : gr.getControlAreas()) {
				// Point pt1 = gr.convertNormalizedPointToViewCoordinates(ca.getPoint(), getScale());
				// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
				// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
				double caDistance = ca.getDistanceToArea(p3, getScale());
				if (caDistance < selectionDistance && caDistance < smallestDistance
						&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
					returned = ca;
					smallestDistance = caDistance;
				}
			}
		}
		return returned;
	}

	public GraphicalRepresentation getFocusedObject(MouseEvent event) {
		switch (getController().getCurrentTool()) {
		case SelectionTool:
			GraphicalRepresentation returned = getFocusedObject(drawingView.getGraphicalRepresentation(), event);
			/*System.out.println("getFocusedObject(), return "+returned);
			if (getController().getDrawing() instanceof DefaultDrawing)
			((DefaultDrawing)getController().getDrawing()).printGraphicalObjectHierarchy();*/
			return returned;
		case DrawShapeTool:
			return getController().getDrawShapeToolController().getCurrentEditedShapeGR();
		default:
			return null;
		}
	}

	public GraphicalRepresentation getFocusedObject(DropTargetDragEvent event) {
		return getFocusedObject(drawingView.getGraphicalRepresentation(), event);
	}

	public GraphicalRepresentation getFocusedObject(DropTargetDropEvent event) {
		return getFocusedObject(drawingView.getGraphicalRepresentation(), event);
	}

	public GraphicalRepresentation getFocusedObject(GraphicalRepresentation<?> container, MouseEvent event) {
		return getFocusedObject(container, (Component) event.getSource(), event.getPoint());
	}

	public GraphicalRepresentation getFocusedObject(GraphicalRepresentation<?> container, DropTargetDragEvent event) {
		return getFocusedObject(container, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	public GraphicalRepresentation getFocusedObject(GraphicalRepresentation<?> container, DropTargetDropEvent event) {
		return getFocusedObject(container, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	private GraphicalRepresentation getFocusedObject(GraphicalRepresentation<?> container, Component eventSource, Point eventLocation) {
		FGEView view = drawingView.viewForObject(container);
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) view);
		double distanceToNearestConnector = Double.POSITIVE_INFINITY;
		double smallestDistanceToCPOfNearestConnector = Double.POSITIVE_INFINITY;
		ConnectorGraphicalRepresentation nearestConnector = null;
		Vector<ShapeGraphicalRepresentation> enclosingShapes = new Vector<ShapeGraphicalRepresentation>();

		double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
		int layerOfNearestGeometricObject = 0;
		GeometricGraphicalRepresentation nearestGeometricObject = null;
		Vector<GeometricGraphicalRepresentation> enclosingGeometricObjects = new Vector<GeometricGraphicalRepresentation>();

		// iterate on all contained objects

		ControlPoint focusedCP = null;

		if (container.getContainedObjects() != null) {

			for (Object o : container.getContainedObjects()) {

				GraphicalRepresentation<?> graphicalRepresentation = container.getGraphicalRepresentation(o);

				if (graphicalRepresentation == null) {
					logger.warning("No graphical representation for " + o);
					continue;
				}
				double selectionDistance = Math.max(5.0, FGEConstants.SELECTION_DISTANCE * getScale());
				// Work on object only if object is visible and focusable
				if (graphicalRepresentation.shouldBeDisplayed() && graphicalRepresentation.getIsFocusable()) {

					if (graphicalRepresentation instanceof GeometricGraphicalRepresentation) {

						boolean focused = false;
						GeometricGraphicalRepresentation<?> gr = (GeometricGraphicalRepresentation<?>) graphicalRepresentation;
						Point viewPoint = SwingUtilities.convertPoint(eventSource, eventLocation,
								(Component) drawingView.viewForObject(container));
						FGEPoint point = graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(viewPoint, getScale());

						if (gr.getGeometricObject().containsPoint(point)) {
							enclosingGeometricObjects.add(gr);
							focused = true;
						} else {
							FGEPoint nearestPoint = gr.getGeometricObject().getNearestPoint(point);
							if (nearestPoint != null) {
								double distance = FGESegment.getLength(point, nearestPoint) * getScale();
								if (distance < selectionDistance
										&& ((distance < distanceToNearestGeometricObject
												&& Math.abs(distance - distanceToNearestGeometricObject) > FGEGeometricObject.EPSILON && focusedCP == null) || (gr
												.getLayer() > layerOfNearestGeometricObject))) {
									distanceToNearestGeometricObject = distance;
									layerOfNearestGeometricObject = gr.getLayer();
									nearestGeometricObject = gr;
									focused = true;
								}
							}
						}

						// Look if we are near a CP
						for (ControlPoint cp : gr.getControlPoints()) {
							// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
							// double cpDistance = Point2D.distance(pt1.x,pt1.y,viewPoint.x,viewPoint.y);
							double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
							if (cpDistance <= selectionDistance
							// && Math.abs(cpDistance-distanceToNearestGeometricObject) < selectionDistance
									&& ((focusedCP == null) || (getController().preferredFocusedControlArea(focusedCP, cp) == cp))) {
								distanceToNearestGeometricObject = cpDistance;
								nearestGeometricObject = gr;
								focused = true;
								focusedCP = cp;
							}
						}

					}

					else {

						FGEView v = drawingView.viewForObject(graphicalRepresentation);
						Rectangle r = graphicalRepresentation.getViewBounds(getScale());

						if (r.contains(p)) {
							// The point is located in the view built for object
							// Let's see if the point is located inside shape
							Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
							FGEPoint p3 = graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(p2, getScale());
							if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
								ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) graphicalRepresentation;
								if (Double.isNaN(p3.getX()) && gr.getWidth() == 0) {
									p3.x = 0;
								}
								if (Double.isNaN(p3.getY()) && gr.getHeight() == 0) {
									p3.y = 0;
								}
								if (gr.isPointInsideShape(p3)) {
									enclosingShapes.add(gr);
								} else { // Look if we are near a CP
									for (ControlArea ca : gr.getControlAreas()) {
										// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
										// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
										// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double caDistance = ca.getDistanceToArea(p3, getScale());
										if (caDistance < selectionDistance) {
											// System.out.println("Detected control point");
											enclosingShapes.add(gr);
										}
									}
									if (focusOnFloatingLabel(gr, eventSource, eventLocation)) {
										// System.out.println("Detected floating label");
										enclosingShapes.add(gr);
									}
									// Look if we are not contained in a child shape outside current shape
									GraphicalRepresentation insideFocusedShape = getFocusedObject(gr, eventSource, eventLocation);
									if (insideFocusedShape != null && insideFocusedShape instanceof ShapeGraphicalRepresentation) {
										enclosingShapes.add((ShapeGraphicalRepresentation) insideFocusedShape);
									}
								}

							} else if (graphicalRepresentation instanceof ConnectorGraphicalRepresentation) {
								ConnectorGraphicalRepresentation<?> gr = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
								double distance = gr.distanceToConnector(p3, getScale());
								if (distance < selectionDistance) {
									// The current gr can be selected if either it is closer than the other edge
									// or if its middle symbol is closer (within selection range of course)
									if (distance < distanceToNearestConnector) {
										// If we are clearly nearer than another connector, then this is the one the user has selected
										distanceToNearestConnector = distance;
										nearestConnector = gr;
										for (ControlArea ca : gr.getControlAreas()) {
											// Point pt1 = gr.convertNormalizedPointToViewCoordinates(ca.getPoint(), getScale());
											// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
											// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
											double cpDistance = ca.getDistanceToArea(p3, getScale());
											if (cpDistance < selectionDistance && cpDistance < distance) {
												// System.out.println("Detected control point");
												distanceToNearestConnector = cpDistance;
											}
										}
										smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(gr, p2, distance);
									} else {
										// We try to find a control area that is closer than the already selected connector.
										for (ControlArea ca : gr.getControlAreas()) {
											// Point pt1 = gr.convertNormalizedPointToViewCoordinates(ca.getPoint(), getScale());
											// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
											// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
											double cpDistance = ca.getDistanceToArea(p3, getScale());
											// We have found a control area which is closer than the previous selected connector
											if (cpDistance < selectionDistance && cpDistance < distance) {
												// System.out.println("Detected control point");
												distanceToNearestConnector = cpDistance;
												nearestConnector = gr;
												smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(gr, p2,
														cpDistance);
											}
										}
										// We can also be closer to the CP than the other one.
										if (gr.getConnector() != null && gr.getConnector().getMiddleSymbolLocation() != null) {
											double cpDistance = gr.convertNormalizedPointToViewCoordinates(
													gr.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
											if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
												distanceToNearestConnector = cpDistance;
												smallestDistanceToCPOfNearestConnector = cpDistance;
												nearestConnector = gr;
											}

										}
										// Look if we are inside a floating label
										/*if (gr.hasText()) {
										if (gr.getConnector().getLabelBounds().contains(p3)) {
										//System.out.println("Detected floating label");
										nearestConnector = gr;
										}
										}*/
									}
								}
								if (focusOnFloatingLabel(gr, eventSource, eventLocation)) {
									// System.out.println("Detected floating label");
									nearestConnector = gr;
								}
							}
						} else {
							Rectangle extendedRectangle = new Rectangle((int) (r.x - selectionDistance), (int) (r.y - selectionDistance),
									(int) (r.width + 2 * selectionDistance), (int) (r.height + 2 * selectionDistance));
							if (extendedRectangle.contains(p)) {
								// We are just outside the shape, may be we focus on a CP ???
								Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
								FGEPoint p3 = graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(p2, getScale());
								if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
									ShapeGraphicalRepresentation<?> gr = (ShapeGraphicalRepresentation<?>) graphicalRepresentation;
									for (ControlArea ca : gr.getControlAreas()) {
										// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
										// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
										// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance) {
											// System.out.println("Detected control point");
											enclosingShapes.add(gr);
										}
									}
								} else if (graphicalRepresentation instanceof ConnectorGraphicalRepresentation) {
									ConnectorGraphicalRepresentation<?> gr = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
									for (ControlArea ca : gr.getControlAreas()) {
										// Point pt1 = gr.convertNormalizedPointToViewCoordinates(ca.getPoint(), getScale());
										// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
										// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance && cpDistance < distanceToNearestConnector) {
											// System.out.println("Detected control point2");
											distanceToNearestConnector = cpDistance;
											nearestConnector = gr;
										}
									}
									if (gr.getConnector() != null && gr.getConnector().getMiddleSymbolLocation() != null) {
										double cpDistance = gr.convertNormalizedPointToViewCoordinates(
												gr.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
										if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
											distanceToNearestConnector = cpDistance;
											nearestConnector = gr;
											smallestDistanceToCPOfNearestConnector = cpDistance;
										}

									}
								}
							}
							if (graphicalRepresentation.hasFloatingLabel()
									&& focusOnFloatingLabel(graphicalRepresentation, eventSource, eventLocation)) {
								// System.out.println("Detected floating label");
								if (graphicalRepresentation instanceof ShapeGraphicalRepresentation) {
									enclosingShapes.add((ShapeGraphicalRepresentation<?>) graphicalRepresentation);
								} else if (graphicalRepresentation instanceof ConnectorGraphicalRepresentation) {
									nearestConnector = (ConnectorGraphicalRepresentation<?>) graphicalRepresentation;
								}
							}
						}
					}
				}
			}

		}

		if (nearestGeometricObject != null) {
			return nearestGeometricObject;
		}

		if (enclosingGeometricObjects.size() > 0) {

			Collections.sort(enclosingGeometricObjects, new Comparator<GeometricGraphicalRepresentation>() {
				@Override
				public int compare(GeometricGraphicalRepresentation o1, GeometricGraphicalRepresentation o2) {
					return o2.getLayer() - o1.getLayer();
				}
			});

			return enclosingGeometricObjects.firstElement();
		}

		GraphicalRepresentation returned = nearestConnector;

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeGraphicalRepresentation>() {
				@Override
				public int compare(ShapeGraphicalRepresentation o1, ShapeGraphicalRepresentation o2) {
					if (o2.getIsSelected()) {
						return Integer.MAX_VALUE;
					}
					if (o1.getIsSelected()) {
						return Integer.MIN_VALUE;
					}
					return o2.getLayer() - o1.getLayer();
				}
			});

			ShapeGraphicalRepresentation focusedShape = enclosingShapes.firstElement();
			int layer = focusedShape.getLayer();
			if (focusedShape.getIsSelected()) {
				for (ShapeGraphicalRepresentation<?> s : enclosingShapes) {
					if (s.getIsSelected()) {
						continue;
					} else {
						layer = s.getLayer();
						break;
					}
				}
			}
			List<ShapeGraphicalRepresentation<?>> shapesInSameLayer = new ArrayList<ShapeGraphicalRepresentation<?>>();
			for (ShapeGraphicalRepresentation<?> s : enclosingShapes) {
				if (s.getLayer() == layer || s.getIsSelected()) {
					shapesInSameLayer.add(s);
				} else {
					break;
				}
			}
			if (shapesInSameLayer.size() > 1) {
				double distance = Double.MAX_VALUE;
				for (ShapeGraphicalRepresentation<?> gr : shapesInSameLayer) {
					FGEView v = drawingView.viewForObject(gr);
					Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
					FGEPoint p3 = gr.convertViewCoordinatesToNormalizedPoint(p2, getScale());
					if (Double.isNaN(p3.getX()) && gr.getWidth() == 0) {
						p3.x = 0;
					}
					if (Double.isNaN(p3.getY()) && gr.getHeight() == 0) {
						p3.y = 0;
					}
					for (ControlArea ca : gr.getControlAreas()) {
						// Point pt1 = gr.convertNormalizedPointToViewCoordinates(cp.getPoint(), getScale());
						// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3, getScale());
						// double cpDistance = Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
						double caDistance = ca.getDistanceToArea(p3, getScale());
						if (caDistance < distance) {
							// System.out.println("Detected control point");
							distance = caDistance;
							focusedShape = gr;
						}
					}
				}
			}
			GraphicalRepresentation insideFocusedShape = getFocusedObject(focusedShape, eventSource, eventLocation);

			if (insideFocusedShape != null) {
				if (returned == null || returned.getLayer() < insideFocusedShape.getLayer() || insideFocusedShape.getIsSelected()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focused GR: " + insideFocusedShape);
					}
					returned = insideFocusedShape;
				}
			} else {
				if (returned == null || returned.getLayer() < focusedShape.getLayer() || focusedShape.getIsSelected()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focused GR: " + focusedShape);
					}
					returned = focusedShape;
				}
			}

		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Focused GR: " + returned);
		}
		return returned;
	}

	/**
	 * @param gr
	 * @param smallestDistanceToCPOfNearestConnector
	 * @param p2
	 * @param distance
	 * @return
	 */
	private double updateSmallestDistanceToCPForConnector(ConnectorGraphicalRepresentation<?> gr, Point p2, double distance) {
		if (gr.getConnector() != null && gr.getConnector().getMiddleSymbolLocation() != null) {
			return gr.convertNormalizedPointToViewCoordinates(gr.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
		} else {
			return distance;
		}
	}

	public double getScale() {
		return drawingView.getController().getScale();
	}

}
