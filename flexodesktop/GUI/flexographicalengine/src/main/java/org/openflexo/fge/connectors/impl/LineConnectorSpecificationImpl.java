package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.LineConnectorSpecification;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ConnectorControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.notifications.ConnectorModified;

public class LineConnectorSpecificationImpl extends ConnectorSpecificationImpl implements LineConnectorSpecification {

	private static final Logger logger = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ConnectorAdjustingControlPoint middleSymbolLocationControlPoint;
	private List<ControlPoint> controlPoints;

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;

	private boolean firstUpdated = false;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public LineConnectorSpecificationImpl() {
		super();
		controlPoints = new ArrayList<ControlPoint>();
	}

	/*public LineConnectorSpecificationImpl(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
	}*/

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.LINE;
	}

	@Override
	public List<ControlPoint> rebuildControlPoints(ConnectorNode<?> connectorNode) {
		updateControlPoints(connectorNode);
		return controlPoints;
	}

	/*@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}*/

	private ConnectorAdjustingControlPoint makeMiddleSymbolLocationControlPoint(final ConnectorNode<?> connectorNode) {
		middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(connectorNode, getMiddleSymbolLocation(connectorNode)) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGESegment(cp1.getPoint(), cp2.getPoint());
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				// logger.info("OK, moving to "+point);
				FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
				setPoint(pt);
				FGESegment segment = new FGESegment(cp1.getPoint(), cp2.getPoint());
				connectorNode.getGraphicalRepresentation().setRelativeMiddleSymbolLocation(segment.getRelativeLocation(pt));

				/*
				 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
				 * connectorNode.getStartNode());
				 */
				connectorNode.notifyConnectorChanged();
				return true;
			}

		};
		return middleSymbolLocationControlPoint;
	}

	private void updateControlPoints(final ConnectorNode<?> connectorNode) {
		if (lineConnectorType == LineConnectorType.CENTER_TO_CENTER) {

			// With this connection type, we try to draw a line joining both center
			// We have to compute the intersection between this line and the outline
			// of joined shapes

			FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), new FGEPoint(0.5,
					0.5), connectorNode.getStartNode());
			FGEPoint pointOnStartObject = connectorNode.getStartNode().getShape()
					.outlineIntersect(centerOfEndObjectSeenFromStartObject, connectorNode.getStartNode());
			if (pointOnStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				pointOnStartObject = new FGEPoint(0.5, 0.5);
			}
			FGEPoint newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), pointOnStartObject, connectorNode);

			FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), new FGEPoint(0.5,
					0.5), connectorNode.getEndNode());
			FGEPoint pointOnEndObject = connectorNode.getEndNode().getShape()
					.outlineIntersect(centerOfStartObjectSeenFromEndObject, connectorNode.getEndNode());
			if (pointOnEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				pointOnEndObject = new FGEPoint(0.5, 0.5);
			}
			FGEPoint newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), pointOnEndObject, connectorNode);

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(connectorNode, newP1);
			cp2 = new ConnectorControlPoint(connectorNode, newP2);

			controlPoints.clear();
			if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint(connectorNode));
			}
			controlPoints.add(cp2);
			controlPoints.add(cp1);

		}

		else if (lineConnectorType == LineConnectorType.MINIMAL_LENGTH) {

			// First obtain the two affine transform allowing to convert from
			// extremity objects coordinates to connector drawable

			AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getStartNode(), connectorNode);

			AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getEndNode(), connectorNode);

			// Then compute first order covering area for both extremities

			FGEArea coveringArea = computeCoveringArea(connectorNode, 1);

			if (coveringArea instanceof FGERectangle) {
				// The covering area is a rectangle:
				// This means that the two connector have a common connecting area
				// along x-axis or y-axis: this area is the obtained rectangle

				FGERectangle r = (FGERectangle) coveringArea;

				FGEPoint startMiddle = connectorNode.getStartNode().getFGEShape().getCenter().transform(at1);
				FGEPoint endMiddle = connectorNode.getEndNode().getFGEShape().getCenter().transform(at2);
				FGEPoint pointOnStartObject, pointOnEndObject;

				// According to the relative orientation of both objects, compute points on start
				// object and end object, as middle of rectangle covering area

				SimplifiedCardinalDirection orientation = FGEPoint.getSimplifiedOrientation(startMiddle, endMiddle);
				if (orientation == SimplifiedCardinalDirection.NORTH) {
					pointOnStartObject = r.getNorth().getMiddle();
					pointOnEndObject = r.getSouth().getMiddle();
				} else if (orientation == SimplifiedCardinalDirection.EAST) {
					pointOnStartObject = r.getEast().getMiddle();
					pointOnEndObject = r.getWest().getMiddle();
				} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
					pointOnStartObject = r.getSouth().getMiddle();
					pointOnEndObject = r.getNorth().getMiddle();
				} else /* orientation == CardinalDirection.WEST */{
					pointOnStartObject = r.getWest().getMiddle();
					pointOnEndObject = r.getEast().getMiddle();
				}

				// Now, we still are not sure that obtained points are located on shape
				// So we must project them on shape to find nearest point located on
				// outline (using nearestOutlinePoint(FGEPoint) method)

				pointOnStartObject = FGEUtils.convertNormalizedPoint(connectorNode, pointOnStartObject, connectorNode.getStartNode());
				pointOnStartObject = connectorNode.getStartNode().getShape()
						.nearestOutlinePoint(pointOnStartObject, connectorNode.getStartNode());

				pointOnEndObject = FGEUtils.convertNormalizedPoint(connectorNode, pointOnEndObject, connectorNode.getEndNode());
				pointOnEndObject = connectorNode.getEndNode().getShape().nearestOutlinePoint(pointOnEndObject, connectorNode.getEndNode());

				// Coordinates are expressed in object relative coordinates
				// Convert them to local coordinates

				FGEPoint newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), pointOnStartObject, connectorNode);

				FGEPoint newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), pointOnEndObject, connectorNode);

				// And assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(connectorNode, newP1);
				cp2 = new ConnectorControlPoint(connectorNode, newP2);
				controlPoints.clear();
				if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint(connectorNode));
				}
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				// That's all folks !
				// (for rectangle)

			}

			else if (coveringArea instanceof FGEEmptyArea) {
				// In this case, we have to join shapes using a line connecting
				// biased cardinal points of embedding rectangle

				FGEPoint startMiddle = connectorNode.getStartNode().getFGEShape().getCenter().transform(at1);
				FGEPoint endMiddle = connectorNode.getEndNode().getFGEShape().getCenter().transform(at2);
				FGEPoint pointOnStartObject, pointOnEndObject;

				CardinalQuadrant orientation = FGEPoint.getCardinalQuadrant(startMiddle, endMiddle);

				if (orientation == CardinalQuadrant.NORTH_WEST) {
					pointOnStartObject = new FGEPoint(0, 0);
					pointOnEndObject = new FGEPoint(1, 1);
				} else if (orientation == CardinalQuadrant.SOUTH_WEST) {
					pointOnStartObject = new FGEPoint(0, 1);
					pointOnEndObject = new FGEPoint(1, 0);
				} else if (orientation == CardinalQuadrant.NORTH_EAST) {
					pointOnStartObject = new FGEPoint(1, 0);
					pointOnEndObject = new FGEPoint(0, 1);
				} else /* orientation == BiasedCardinalDirection.SOUTH_EAST */{
					pointOnStartObject = new FGEPoint(1, 1);
					pointOnEndObject = new FGEPoint(0, 0);
				}

				// We compute nearest outline point
				pointOnStartObject = connectorNode.getStartNode().getShape()
						.nearestOutlinePoint(pointOnStartObject, connectorNode.getStartNode());
				pointOnEndObject = connectorNode.getEndNode().getShape().nearestOutlinePoint(pointOnEndObject, connectorNode.getEndNode());

				// And then we convert to local coordinates
				FGEPoint newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), pointOnStartObject, connectorNode);
				FGEPoint newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), pointOnEndObject, connectorNode);

				// Finally assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(connectorNode, newP1);
				cp2 = new ConnectorControlPoint(connectorNode, newP2);
				controlPoints.clear();
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint(connectorNode));
				}
			}

			else {
				logger.warning("Unexpected covering area found : " + coveringArea);
			}
		}

		else if (lineConnectorType == LineConnectorType.FUNNY) {

			FGEPoint newP1 = connectorNode
					.getEndNode()
					.getShape()
					.nearestOutlinePoint(
							FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), new FGEPoint(0.5, 0.5),
									connectorNode.getEndNode()), connectorNode.getEndNode());
			newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), newP1, connectorNode);

			FGEPoint newP2 = connectorNode
					.getStartNode()
					.getShape()
					.nearestOutlinePoint(
							FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), new FGEPoint(0.5, 0.5),
									connectorNode.getStartNode()), connectorNode.getStartNode());
			newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), newP2, connectorNode);

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(connectorNode, newP1);
			cp2 = new ConnectorControlPoint(connectorNode, newP2);
			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint(connectorNode));
			}
		}

		else if (lineConnectorType == LineConnectorType.ADJUSTABLE) {

			if (cp1RelativeToStartObject == null || cp2RelativeToEndObject == null) {
				// In this case default location is obtained by center_to_center type
				lineConnectorType = LineConnectorType.CENTER_TO_CENTER;
				updateControlPoints(connectorNode);
				lineConnectorType = LineConnectorType.ADJUSTABLE;
				cp1RelativeToStartObject = FGEUtils.convertNormalizedPoint(connectorNode, cp1.getPoint(), connectorNode.getStartNode());
				cp2RelativeToEndObject = FGEUtils.convertNormalizedPoint(connectorNode, cp2.getPoint(), connectorNode.getEndNode());
			}

			// We have either the old position, or the default one
			// We need now to find updated position according to eventual shape move, resize, reshaped, etc...
			// To do that, use outlineIntersect();

			FGEPoint newP1 = null; /* = cp1.getPoint(); */
			if (cp1 != null) {
				newP1 = cp1.getPoint();
			}
			cp1RelativeToStartObject = connectorNode.getStartNode().getShape()
					.outlineIntersect(cp1RelativeToStartObject, connectorNode.getStartNode());
			if (cp1RelativeToStartObject != null) {
				newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), cp1RelativeToStartObject, connectorNode);
			}

			FGEPoint newP2 = null; /* = cp2.getPoint(); */
			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			cp2RelativeToEndObject = connectorNode.getEndNode().getShape()
					.outlineIntersect(cp2RelativeToEndObject, connectorNode.getEndNode());
			if (cp2RelativeToEndObject != null) {
				newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), cp2RelativeToEndObject, connectorNode);
			}

			cp1 = new ConnectorAdjustingControlPoint(connectorNode, newP1) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					FGEShape<?> shape = connectorNode.getStartNode().getFGEShape();
					return shape.transform(FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getStartNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp1RelativeToStartObject = FGEUtils.convertNormalizedPoint(connectorNode, pt, connectorNode.getStartNode());
					connectorNode.notifyConnectorChanged();
					return true;
				}

			};

			cp2 = new ConnectorAdjustingControlPoint(connectorNode, newP2) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					FGEShape<?> shape = connectorNode.getEndNode().getFGEShape();
					return shape.transform(FGEUtils.convertNormalizedCoordinatesAT(connectorNode.getEndNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp2RelativeToEndObject = FGEUtils.convertNormalizedPoint(connectorNode, pt, connectorNode.getEndNode());
					if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
						if (middleSymbolLocationControlPoint != null) {
							middleSymbolLocationControlPoint.setPoint(getMiddleSymbolLocation(connectorNode));
						}
					}
					connectorNode.notifyConnectorChanged();
					return true;
				}
			};

			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint(connectorNode));
			}
		}

		else {

			logger.warning("Unexpected lineConnectorType=" + lineConnectorType);
		}
	}

	@Override
	public void refreshConnector(ConnectorNode<?> connectorNode, boolean force) {
		if (!force && !needsRefresh(connectorNode)) {
			return;
		}

		updateControlPoints(connectorNode);

		super.refreshConnector(connectorNode, force);

		firstUpdated = true;

	}

	@Override
	public boolean needsRefresh(ConnectorNode<?> connectorNode) {
		if (!firstUpdated) {
			return true;
		}
		return super.needsRefresh(connectorNode);
	}

	@Override
	public void drawConnector(ConnectorNode<?> connectorNode, FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector(connectorNode);
		}

		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		/*
		 * if (lineConnectorType == LineConnectorType.ADJUSTABLE) { g.setForeground(fs0); g.setBackground(bs0); FGEShape<?> shape =
		 * connectorNode.getStartNode().getShape().getShape();
		 * shape.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(connectorNode.getStartNode().getDrawable(), getDrawable())) .paint(g);
		 * g.setForeground(fs1); g.setBackground(bs1); FGEShape<?> shape2 = connectorNode.getEndNode().getShape().getShape();
		 * shape2.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(connectorNode.getEndNode().getDrawable(), getDrawable())) .paint(g);
		 * }
		 */

		g.useDefaultForegroundStyle();
		// logger.info("paintConnector() "+cp1.getPoint()+"-"+cp2.getPoint()+" with "+g.getCurrentForeground());
		g.drawLine(cp1.getPoint(), cp2.getPoint());

		Point cp1InView = connectorNode.convertNormalizedPointToViewCoordinates(cp1.getPoint(), 1);
		Point cp2InView = connectorNode.convertNormalizedPointToViewCoordinates(cp2.getPoint(), 1);

		// double angle = Math.atan2(cp2.getPoint().x-cp1.getPoint().x, cp2.getPoint().y-cp1.getPoint().y)+Math.PI/2;
		double angle = Math.atan2(cp2InView.x - cp1InView.x, cp2InView.y - cp1InView.y) + Math.PI / 2;

		// System.out.println("Angle1="+Math.toDegrees(angle));
		// System.out.println("Angle2="+Math.toDegrees(angle+Math.PI));

		if (connectorNode.getGraphicalRepresentation().getStartSymbol() != StartSymbolType.NONE) {
			g.drawSymbol(cp1.getPoint(), connectorNode.getGraphicalRepresentation().getStartSymbol(), connectorNode
					.getGraphicalRepresentation().getStartSymbolSize(), angle);
		}

		if (connectorNode.getGraphicalRepresentation().getEndSymbol() != EndSymbolType.NONE) {
			g.drawSymbol(cp2.getPoint(), connectorNode.getGraphicalRepresentation().getEndSymbol(), connectorNode
					.getGraphicalRepresentation().getEndSymbolSize(), angle + Math.PI);
		}

		if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
			g.drawSymbol(getMiddleSymbolLocation(connectorNode), connectorNode.getGraphicalRepresentation().getMiddleSymbol(),
					connectorNode.getGraphicalRepresentation().getMiddleSymbolSize(), angle + Math.PI);
		}
	}

	@Override
	public double getStartAngle() {
		return FGEUtils.getSlope(cp1.getPoint(), cp2.getPoint());
	}

	@Override
	public double getEndAngle() {
		return FGEUtils.getSlope(cp2.getPoint(), cp1.getPoint());
	}

	@Override
	public FGEPoint getMiddleSymbolLocation(ConnectorNode<?> connectorNode) {
		if (cp1 == null || cp2 == null) {
			return new FGEPoint(0, 0);
		}
		return new FGESegment(cp1.getPoint(), cp2.getPoint()).getScaledPoint(connectorNode.getGraphicalRepresentation()
				.getRelativeMiddleSymbolLocation());
	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale, ConnectorNode<?> connectorNode) {
		if (cp1 == null || cp2 == null) {
			logger.warning("Invalid date in LineConnectorSpecification: control points are null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(cp1.getPoint(), scale);
		Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(cp2.getPoint(), scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	private LineConnectorType lineConnectorType = LineConnectorType.MINIMAL_LENGTH;

	@Override
	public LineConnectorType getLineConnectorType() {
		return lineConnectorType;
	}

	@Override
	public void setLineConnectorType(LineConnectorType aLineConnectorType) {
		if (lineConnectorType != aLineConnectorType) {
			// LineConnectorType oldLineConnectorType = lineConnectorType;
			lineConnectorType = aLineConnectorType;
			setChanged();
			notifyObservers(new ConnectorModified());
		}
		/*if (getGraphicalRepresentation() != null) {
			updateControlPoints();
			getGraphicalRepresentation().notifyConnectorChanged();
		}*/
	}

	// Used for serialization only
	@Override
	public FGEPoint _getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	// Used for serialization only
	@Override
	public void _setCp1RelativeToStartObject(FGEPoint aPoint) {
		this.cp1RelativeToStartObject = aPoint;
	}

	// Used for serialization only
	@Override
	public FGEPoint _getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	// Used for serialization only
	@Override
	public void _setCp2RelativeToEndObject(FGEPoint aPoint) {
		this.cp2RelativeToEndObject = aPoint;
	}

	@Override
	public FGERectangle getConnectorUsedBounds() {
		return NORMALIZED_BOUNDS;
	}

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getStartLocation() {
		return cp1RelativeToStartObject;
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getEndLocation() {
		return cp2RelativeToEndObject;
	}

	@Override
	public LineConnectorSpecification clone() {
		LineConnectorSpecification returned = new LineConnectorSpecificationImpl();
		returned.setLineConnectorType(getLineConnectorType());
		returned._setCp1RelativeToStartObject(_getCp1RelativeToStartObject());
		returned._setCp2RelativeToEndObject(_getCp2RelativeToEndObject());
		return returned;
	}

}
