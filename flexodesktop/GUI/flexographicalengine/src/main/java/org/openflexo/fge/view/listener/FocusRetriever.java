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
import java.awt.Rectangle;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fge.view.LabelView;

/**
 * Utility class used in a general context to retrieve the focus owner in a graphical context.<br>
 * 
 * The policy is to return the closest connector or geometrical object from the cursor, or the top-most shape where the cursor is located
 * in.<br>
 * Manage focusable properties, as well as layers.
 * 
 * @author sylvain
 * 
 */
public class FocusRetriever {

	private static final Logger logger = Logger.getLogger(FocusRetriever.class.getPackage().getName());

	private DrawingView<?> drawingView;

	public FocusRetriever(DrawingView<?> aDrawingView) {
		drawingView = aDrawingView;
	}

	private boolean cursorChanged = false;

	private Component cursoredComponent;

	private void resetCursorIfRequired() {
		if (cursorChanged) {
			cursoredComponent.setCursor(null);
			cursoredComponent = null;
			cursorChanged = false;
			getController()._setFocusedControlArea(null);
		}
	}

	public DrawingController<?> getController() {
		return drawingView.getController();
	}

	public void handleMouseMove(MouseEvent event) {
		DrawingTreeNode<?, ?> newFocusedObject = getFocusedObject(event);

		// System.out.println("Hop, je bouge et je suis focuse sur " + newFocusedObject);

		if (newFocusedObject != null) {
			drawingView.getController().setFocusedFloatingLabel(focusOnFloatingLabel(newFocusedObject, event) ? newFocusedObject : null);
			ControlArea<?> cp = getFocusedControlAreaForDrawable(newFocusedObject, event);
			if (cp != null) {
				if (cursoredComponent != null) {
					cursoredComponent.setCursor(null);
				}
				cursoredComponent = event.getComponent();
				cursoredComponent.setCursor(cp.getDraggingCursor());
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

		// if (newFocusedObject !=
		// drawingView.getController().getFocusedObject()) {
		drawingView.getController().setFocusedObject(newFocusedObject);
		// }

	}

	public boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, MouseEvent event) {
		return focusOnFloatingLabel(node, (Component) event.getSource(), event.getPoint());
	}

	private boolean focusOnFloatingLabel(DrawingTreeNode<?, ?> node, Component eventSource, Point eventLocation) {
		// if (!graphicalRepresentation.hasText()) return false;

		if (node instanceof GeometricNode) {
			return false;
		}

		FGEView<?> view = drawingView.viewForNode(node);
		if (view == null) {
			logger.warning("Unexpected null view for node " + node + " DrawingController=" + getController() + " DrawingView="
					+ drawingView);
			/*Map<DrawingTreeNode<?, ?>, FGEView<?>> contents = getController().getContents();
			System.out.println("Pour node, j'ai:");
			FGEView v = contents.get(node);
			System.out.println("Prout");*/
		}
		FGEView<?> parenttView = node == drawingView.getDrawing().getRoot() ? drawingView : drawingView.viewForNode(node.getParentNode());
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) parenttView);
		if (node.getGraphicalRepresentation().getHasText()) {
			LabelView<?> labelView = view.getLabelView();
			if (labelView != null) {
				return labelView.getBounds().contains(p);
			}
		}
		return false;

	}

	public ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, MouseEvent event) {
		return getFocusedControlAreaForDrawable(node, drawingView.getDrawing().getRoot(), event);
	}

	public ControlArea<?> getFocusedControlAreaForDrawable(DrawingTreeNode<?, ?> node, ContainerNode<?, ?> container, MouseEvent event) {
		ControlArea<?> returned = null;
		double selectionDistance = FGEConstants.SELECTION_DISTANCE; // Math.max(5.0,FGEConstants.SELECTION_DISTANCE*getScale());
		if (node instanceof GeometricNode) {
			GeometricNode<?> geometricNode = (GeometricNode<?>) node;
			Point viewPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
					(Component) drawingView.viewForNode(container));
			// FGEPoint point =
			// graphicalRepresentation.convertViewCoordinatesToNormalizedPoint(viewPoint,
			// getScale());

			// Look if we are near a CP
			double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
			for (ControlPoint cp : geometricNode.getControlPoints()) {
				// Point pt1 =
				// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
				// getScale());
				// double cpDistance =
				// Point2D.distance(pt1.x,pt1.y,viewPoint.x,viewPoint.y);
				double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
				if (cpDistance < selectionDistance && cpDistance < distanceToNearestGeometricObject
						&& (returned == null || getController().preferredFocusedControlArea(returned, cp) == cp)) {
					distanceToNearestGeometricObject = cpDistance;
					returned = cp;
				}
			}
			return returned;
		}

		FGEView<?> view = drawingView.viewForNode(container);
		Point p = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
		FGEView<?> v = drawingView.viewForNode(node);
		Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
		FGEPoint p3 = v.getNode().convertViewCoordinatesToNormalizedPoint(p2, getScale());

		if (node instanceof ShapeNode) {
			ShapeNode<?> shapeNode = (ShapeNode<?>) node;
			if (Double.isNaN(p3.getX()) && shapeNode.getWidth() == 0) {
				p3.x = 1;
			}
			if (Double.isNaN(p3.getY()) && shapeNode.getHeight() == 0) {
				p3.y = 1;
			}
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea<?> ca : shapeNode.getControlAreas()) {
				// Point pt1 =
				// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
				// getScale());
				// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3,
				// getScale());
				// double cpDistance =
				// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
				double cpDistance = ca.getDistanceToArea(p3, getScale());
				if (cpDistance < selectionDistance && cpDistance < smallestDistance
						&& (returned == null || getController().preferredFocusedControlArea(returned, ca) == ca)) {
					returned = ca;
					smallestDistance = cpDistance;
				}
			}
		} else if (node instanceof ConnectorNode) {
			ConnectorNode<?> connectorNode = (ConnectorNode<?>) node;
			double smallestDistance = Double.POSITIVE_INFINITY;
			for (ControlArea<?> ca : connectorNode.getControlAreas()) {
				// Point pt1 =
				// gr.convertNormalizedPointToViewCoordinates(ca.getPoint(),
				// getScale());
				// Point pt2 = gr.convertNormalizedPointToViewCoordinates(p3,
				// getScale());
				// double cpDistance =
				// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
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

	public DrawingTreeNode<?, ?> getFocusedObject(MouseEvent event) {
		switch (getController().getCurrentTool()) {
		case SelectionTool:
			DrawingTreeNode<?, ?> returned = getFocusedObject(drawingView.getDrawing().getRoot(), event);
			/*
			 * System.out.println("getFocusedObject(), return "+returned); if
			 * (getController().getDrawing() instanceof DrawingImpl)
			 * ((DrawingImpl
			 * )getController().getDrawing()).printGraphicalObjectHierarchy();
			 */
			return returned;
		case DrawShapeTool:
			if (getController().getDrawShapeToolController() != null) {
				return getController().getDrawShapeToolController().getCurrentEditedShape();
			} else {
				return null;
			}
		default:
			return null;
		}
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDragEvent event) {
		return getFocusedObject(drawingView.getDrawing().getRoot(), event);
	}

	public DrawingTreeNode<?, ?> getFocusedObject(DropTargetDropEvent event) {
		return getFocusedObject(drawingView.getDrawing().getRoot(), event);
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, MouseEvent event) {
		return getFocusedObject(node, (Component) event.getSource(), event.getPoint());
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, DropTargetDragEvent event) {
		return getFocusedObject(node, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	public DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, DropTargetDropEvent event) {
		return getFocusedObject(node, event.getDropTargetContext().getComponent(), event.getLocation());
	}

	private DrawingTreeNode<?, ?> getFocusedObject(ContainerNode<?, ?> node, Component eventSource, Point eventLocation) {
		FGEView<?> view = drawingView.viewForNode(node);
		Point p = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) view);
		double distanceToNearestConnector = Double.POSITIVE_INFINITY;
		double smallestDistanceToCPOfNearestConnector = Double.POSITIVE_INFINITY;
		ConnectorNode<?> nearestConnector = null;
		List<ShapeNode<?>> enclosingShapes = new ArrayList<ShapeNode<?>>();

		double distanceToNearestGeometricObject = Double.POSITIVE_INFINITY;
		int layerOfNearestGeometricObject = 0;
		GeometricNode<?> nearestGeometricObject = null;
		List<GeometricNode<?>> enclosingGeometricObjects = new ArrayList<GeometricNode<?>>();

		// iterate on all contained objects

		ControlPoint focusedCP = null;

		for (DrawingTreeNode<?, ?> childNode : node.getChildNodes()) {

			if (childNode == null) {
				logger.warning("Null child node ");
				continue;
			}
			double selectionDistance = Math.max(5.0, FGEConstants.SELECTION_DISTANCE * getScale());
			// Work on object only if object is visible and focusable
			if (childNode.shouldBeDisplayed() && childNode.getGraphicalRepresentation().getIsFocusable()) {

				if (childNode instanceof GeometricNode) {

					GeometricNode<?> geometricNode = (GeometricNode<?>) childNode;
					Point viewPoint = SwingUtilities.convertPoint(eventSource, eventLocation, (Component) drawingView.viewForNode(node));
					FGEPoint point = childNode.convertViewCoordinatesToNormalizedPoint(viewPoint, getScale());

					if (geometricNode.getGraphicalRepresentation().getGeometricObject().containsPoint(point)) {
						enclosingGeometricObjects.add(geometricNode);
					} else {
						FGEPoint nearestPoint = geometricNode.getGraphicalRepresentation().getGeometricObject().getNearestPoint(point);
						if (nearestPoint != null) {
							double distance = FGESegment.getLength(point, nearestPoint) * getScale();
							if (distance < selectionDistance
									&& (distance < distanceToNearestGeometricObject
											&& Math.abs(distance - distanceToNearestGeometricObject) > FGEGeometricObject.EPSILON
											&& focusedCP == null || geometricNode.getGraphicalRepresentation().getLayer() > layerOfNearestGeometricObject)) {
								distanceToNearestGeometricObject = distance;
								layerOfNearestGeometricObject = geometricNode.getGraphicalRepresentation().getLayer();
								nearestGeometricObject = geometricNode;
							}
						}
					}

					// Look if we are near a CP
					for (ControlPoint cp : geometricNode.getControlPoints()) {
						// Point pt1 =
						// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
						// getScale());
						// double cpDistance =
						// Point2D.distance(pt1.x,pt1.y,viewPoint.x,viewPoint.y);
						double cpDistance = cp.getDistanceToArea(viewPoint, getScale());
						if (cpDistance <= selectionDistance
						// &&
						// Math.abs(cpDistance-distanceToNearestGeometricObject)
						// < selectionDistance
								&& (focusedCP == null || getController().preferredFocusedControlArea(focusedCP, cp) == cp)) {
							distanceToNearestGeometricObject = cpDistance;
							nearestGeometricObject = geometricNode;
							focusedCP = cp;
						}
					}

				}

				else {

					FGEView<?> v = drawingView.viewForNode(childNode);
					Rectangle r = childNode.getViewBounds(getScale());

					if (r.contains(p)) {
						// The point is located in the view built for object
						// Let's see if the point is located inside shape
						Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
						FGEPoint p3 = childNode.convertViewCoordinatesToNormalizedPoint(p2, getScale());
						if (childNode instanceof ShapeNode) {
							ShapeNode<?> shapeNode = (ShapeNode<?>) childNode;
							if (Double.isNaN(p3.getX()) && shapeNode.getWidth() == 0) {
								p3.x = 0;
							}
							if (Double.isNaN(p3.getY()) && shapeNode.getHeight() == 0) {
								p3.y = 0;
							}
							if (shapeNode.isPointInsideShape(p3)) {
								enclosingShapes.add(shapeNode);
							} else { // Look if we are near a CP
								for (ControlArea<?> ca : shapeNode.getControlAreas()) {
									// Point pt1 =
									// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
									// getScale());
									// Point pt2 =
									// gr.convertNormalizedPointToViewCoordinates(p3,
									// getScale());
									// double cpDistance =
									// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
									double caDistance = ca.getDistanceToArea(p3, getScale());
									if (caDistance < selectionDistance) {
										// System.out.println("Detected control point");
										enclosingShapes.add(shapeNode);
									}
								}
								if (focusOnFloatingLabel(shapeNode, eventSource, eventLocation)) {
									// System.out.println("Detected floating label");
									enclosingShapes.add(shapeNode);
								}
								// Look if we are not contained in a child
								// shape outside current shape
								DrawingTreeNode<?, ?> insideFocusedShape = getFocusedObject(shapeNode, eventSource, eventLocation);
								if (insideFocusedShape != null && insideFocusedShape instanceof ShapeNode) {
									enclosingShapes.add((ShapeNode<?>) insideFocusedShape);
								}
							}

						} else if (childNode instanceof ConnectorNode) {
							ConnectorNode<?> connectorNode = (ConnectorNode<?>) childNode;
							double distance = connectorNode.distanceToConnector(p3, getScale());
							if (distance < selectionDistance) {
								// The current gr can be selected if either
								// it is closer than the other edge
								// or if its middle symbol is closer (within
								// selection range of course)
								if (distance < distanceToNearestConnector) {
									// If we are clearly nearer than another
									// connector, then this is the one the
									// user has selected
									distanceToNearestConnector = distance;
									nearestConnector = connectorNode;
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										// Point pt1 =
										// gr.convertNormalizedPointToViewCoordinates(ca.getPoint(),
										// getScale());
										// Point pt2 =
										// gr.convertNormalizedPointToViewCoordinates(p3,
										// getScale());
										// double cpDistance =
										// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance && cpDistance < distance) {
											// System.out.println("Detected control point");
											distanceToNearestConnector = cpDistance;
										}
									}
									smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(connectorNode, p2,
											distance);
								} else {
									// We try to find a control area that is
									// closer than the already selected
									// connector.
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										// Point pt1 =
										// gr.convertNormalizedPointToViewCoordinates(ca.getPoint(),
										// getScale());
										// Point pt2 =
										// gr.convertNormalizedPointToViewCoordinates(p3,
										// getScale());
										// double cpDistance =
										// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										// We have found a control area
										// which is closer than the previous
										// selected connector
										if (cpDistance < selectionDistance && cpDistance < distance) {
											// System.out.println("Detected control point");
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
											smallestDistanceToCPOfNearestConnector = updateSmallestDistanceToCPForConnector(connectorNode,
													p2, cpDistance);
										}
									}
									// We can also be closer to the CP than
									// the other one.
									if (connectorNode.getConnector() != null
											&& connectorNode.getConnector().getMiddleSymbolLocation() != null) {
										double cpDistance = connectorNode.convertNormalizedPointToViewCoordinates(
												connectorNode.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
										if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
											distanceToNearestConnector = cpDistance;
											smallestDistanceToCPOfNearestConnector = cpDistance;
											nearestConnector = connectorNode;
										}

									}
									// Look if we are inside a floating
									// label
									/*
									 * if (gr.hasText()) { if
									 * (gr.getConnector
									 * ().getLabelBounds().contains(p3)) {
									 * //System.out.println(
									 * "Detected floating label");
									 * nearestConnector = gr; } }
									 */
								}
							}
							if (focusOnFloatingLabel(connectorNode, eventSource, eventLocation)) {
								// System.out.println("Detected floating label");
								nearestConnector = connectorNode;
							}
						}
					} else {
						Rectangle extendedRectangle = new Rectangle((int) (r.x - selectionDistance), (int) (r.y - selectionDistance),
								(int) (r.width + 2 * selectionDistance), (int) (r.height + 2 * selectionDistance));
						if (extendedRectangle.contains(p)) {
							// We are just outside the shape, may be we
							// focus on a CP ???
							Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
							FGEPoint p3 = childNode.convertViewCoordinatesToNormalizedPoint(p2, getScale());
							if (childNode instanceof ShapeNode) {
								ShapeNode<?> shapeNode = (ShapeNode<?>) childNode;
								for (ControlArea<?> ca : shapeNode.getControlAreas()) {
									// Point pt1 =
									// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
									// getScale());
									// Point pt2 =
									// gr.convertNormalizedPointToViewCoordinates(p3,
									// getScale());
									// double cpDistance =
									// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
									double cpDistance = ca.getDistanceToArea(p3, getScale());
									if (cpDistance < selectionDistance) {
										// System.out.println("Detected control point");
										enclosingShapes.add(shapeNode);
									}
								}
							} else if (childNode instanceof ConnectorNode) {
								ConnectorNode<?> connectorNode = (ConnectorNode<?>) childNode;
								if (connectorNode.isValidated()) {
									for (ControlArea<?> ca : connectorNode.getControlAreas()) {
										// Point pt1 =
										// gr.convertNormalizedPointToViewCoordinates(ca.getPoint(),
										// getScale());
										// Point pt2 =
										// gr.convertNormalizedPointToViewCoordinates(p3,
										// getScale());
										// double cpDistance =
										// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
										double cpDistance = ca.getDistanceToArea(p3, getScale());
										if (cpDistance < selectionDistance && cpDistance < distanceToNearestConnector) {
											// System.out.println("Detected control point2");
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
										}
									}
									if (connectorNode.getConnector() != null
											&& connectorNode.getConnector().getMiddleSymbolLocation() != null) {
										double cpDistance = connectorNode.convertNormalizedPointToViewCoordinates(
												connectorNode.getConnector().getMiddleSymbolLocation(), getScale()).distance(p2);
										if (cpDistance < selectionDistance && cpDistance < smallestDistanceToCPOfNearestConnector) {
											distanceToNearestConnector = cpDistance;
											nearestConnector = connectorNode;
											smallestDistanceToCPOfNearestConnector = cpDistance;
										}
									}
								}
							}
						}
						if (childNode.hasFloatingLabel() && focusOnFloatingLabel(childNode, eventSource, eventLocation)) {
							// System.out.println("Detected floating label");
							if (childNode instanceof ShapeNode) {
								enclosingShapes.add((ShapeNode<?>) childNode);
							} else if (childNode instanceof ConnectorNode) {
								nearestConnector = (ConnectorNode<?>) childNode;
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

			Collections.sort(enclosingGeometricObjects, new Comparator<GeometricNode<?>>() {
				@Override
				public int compare(GeometricNode<?> o1, GeometricNode<?> o2) {
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			return enclosingGeometricObjects.get(0);
		}

		DrawingTreeNode<?, ?> returned = nearestConnector;

		if (enclosingShapes.size() > 0) {

			Collections.sort(enclosingShapes, new Comparator<ShapeNode<?>>() {
				@Override
				public int compare(ShapeNode<?> o1, ShapeNode<?> o2) {
					if (o2.getIsSelected()) {
						return Integer.MAX_VALUE;
					}
					if (o1.getIsSelected()) {
						return Integer.MIN_VALUE;
					}
					return o2.getGraphicalRepresentation().getLayer() - o1.getGraphicalRepresentation().getLayer();
				}
			});

			ShapeNode<?> focusedShape = enclosingShapes.get(0);
			int layer = focusedShape.getGraphicalRepresentation().getLayer();
			if (focusedShape.getIsSelected()) {
				for (ShapeNode<?> s : enclosingShapes) {
					if (s.getIsSelected()) {
						continue;
					} else {
						layer = s.getGraphicalRepresentation().getLayer();
						break;
					}
				}
			}
			List<ShapeNode<?>> shapesInSameLayer = new ArrayList<ShapeNode<?>>();
			for (ShapeNode<?> s : enclosingShapes) {
				if (s.getGraphicalRepresentation().getLayer() == layer || s.getIsSelected()) {
					shapesInSameLayer.add(s);
				} else {
					break;
				}
			}
			if (shapesInSameLayer.size() > 1) {
				double distance = Double.MAX_VALUE;
				for (ShapeNode<?> gr : shapesInSameLayer) {
					FGEView<?> v = drawingView.viewForNode(gr);
					Point p2 = SwingUtilities.convertPoint((Component) view, p, (Component) v);
					FGEPoint p3 = gr.convertViewCoordinatesToNormalizedPoint(p2, getScale());
					if (Double.isNaN(p3.getX()) && gr.getWidth() == 0) {
						p3.x = 0;
					}
					if (Double.isNaN(p3.getY()) && gr.getHeight() == 0) {
						p3.y = 0;
					}
					for (ControlArea<?> ca : gr.getControlAreas()) {
						// Point pt1 =
						// gr.convertNormalizedPointToViewCoordinates(cp.getPoint(),
						// getScale());
						// Point pt2 =
						// gr.convertNormalizedPointToViewCoordinates(p3,
						// getScale());
						// double cpDistance =
						// Point2D.distance(pt1.x,pt1.y,pt2.x,pt2.y);
						double caDistance = ca.getDistanceToArea(p3, getScale());
						if (caDistance < distance) {
							// System.out.println("Detected control point");
							distance = caDistance;
							focusedShape = gr;
						}
					}
				}
			}
			DrawingTreeNode<?, ?> insideFocusedShape = getFocusedObject(focusedShape, eventSource, eventLocation);

			if (insideFocusedShape != null) {
				if (returned == null
						|| returned.getGraphicalRepresentation().getLayer() < insideFocusedShape.getGraphicalRepresentation().getLayer()
						|| insideFocusedShape.getIsSelected()) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Focused GR: " + insideFocusedShape);
					}
					returned = insideFocusedShape;
				}
			} else {
				if (returned == null
						|| returned.getGraphicalRepresentation().getLayer() < focusedShape.getGraphicalRepresentation().getLayer()
						|| focusedShape.getIsSelected()) {
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
	private double updateSmallestDistanceToCPForConnector(ConnectorNode<?> connectorNode, Point p2, double distance) {
		if (connectorNode.getConnector() != null && connectorNode.getConnector().getMiddleSymbolLocation() != null) {
			return connectorNode
					.convertNormalizedPointToViewCoordinates(connectorNode.getConnector().getMiddleSymbolLocation(), getScale()).distance(
							p2);
		} else {
			return distance;
		}
	}

	public double getScale() {
		return drawingView.getController().getScale();
	}

}
