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
package org.openflexo.fge.connectors;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
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

public class LineConnector extends Connector {

	private static final Logger logger = Logger.getLogger(LineConnector.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ConnectorAdjustingControlPoint middleSymbolLocationControlPoint;
	private Vector<ControlPoint> controlPoints;

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;

	private boolean firstUpdated = false;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public LineConnector() {
		this(null);
	}

	public LineConnector(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.LINE;
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}

	private ConnectorAdjustingControlPoint makeMiddleSymbolLocationControlPoint() {
		middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), getMiddleSymbolLocation()) {
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
				getGraphicalRepresentation().setRelativeMiddleSymbolLocation(segment.getRelativeLocation(pt));

				/*
				 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
				 * getStartObject());
				 */
				getGraphicalRepresentation().notifyConnectorChanged();
				return true;
			}

		};
		return middleSymbolLocationControlPoint;
	}

	private void updateControlPoints() {
		if (lineConnectorType == LineConnectorType.CENTER_TO_CENTER) {

			// With this connection type, we try to draw a line joining both center
			// We have to compute the intersection between this line and the outline
			// of joined shapes

			FGEPoint centerOfEndObjectSeenFromStartObject = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), new FGEPoint(
					0.5, 0.5), getStartObject());
			FGEPoint pointOnStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
			if (pointOnStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				pointOnStartObject = new FGEPoint(0.5, 0.5);
			}
			FGEPoint newP1 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), pointOnStartObject,
					getGraphicalRepresentation());

			FGEPoint centerOfStartObjectSeenFromEndObject = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), new FGEPoint(
					0.5, 0.5), getEndObject());
			FGEPoint pointOnEndObject = getEndObject().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
			if (pointOnEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				pointOnEndObject = new FGEPoint(0.5, 0.5);
			}
			FGEPoint newP2 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), pointOnEndObject, getGraphicalRepresentation());

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(getGraphicalRepresentation(), newP1);
			cp2 = new ConnectorControlPoint(getGraphicalRepresentation(), newP2);

			controlPoints.clear();
			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
			controlPoints.add(cp2);
			controlPoints.add(cp1);

		}

		else if (lineConnectorType == LineConnectorType.MINIMAL_LENGTH) {

			// First obtain the two affine transform allowing to convert from
			// extremity objects coordinates to connector drawable

			AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartObject(), getGraphicalRepresentation());

			AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndObject(), getGraphicalRepresentation());

			// Then compute first order covering area for both extremities

			FGEArea coveringArea = computeCoveringArea(1);

			if (coveringArea instanceof FGERectangle) {
				// The covering area is a rectangle:
				// This means that the two connector have a common connecting area
				// along x-axis or y-axis: this area is the obtained rectangle

				FGERectangle r = (FGERectangle) coveringArea;

				FGEPoint startMiddle = getStartObject().getShape().getShape().getCenter().transform(at1);
				FGEPoint endMiddle = getEndObject().getShape().getShape().getCenter().transform(at2);
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

				pointOnStartObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), pointOnStartObject,
						getStartObject());
				pointOnStartObject = getStartObject().getShape().nearestOutlinePoint(pointOnStartObject);

				pointOnEndObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), pointOnEndObject,
						getEndObject());
				pointOnEndObject = getEndObject().getShape().nearestOutlinePoint(pointOnEndObject);

				// Coordinates are expressed in object relative coordinates
				// Convert them to local coordinates

				FGEPoint newP1 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), pointOnStartObject,
						getGraphicalRepresentation());

				FGEPoint newP2 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), pointOnEndObject,
						getGraphicalRepresentation());

				// And assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(getGraphicalRepresentation(), newP1);
				cp2 = new ConnectorControlPoint(getGraphicalRepresentation(), newP2);
				controlPoints.clear();
				if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint());
				}
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				// That's all folks !
				// (for rectangle)

			}

			else if (coveringArea instanceof FGEEmptyArea) {
				// In this case, we have to join shapes using a line connecting
				// biased cardinal points of embedding rectangle

				FGEPoint startMiddle = getStartObject().getShape().getShape().getCenter().transform(at1);
				FGEPoint endMiddle = getEndObject().getShape().getShape().getCenter().transform(at2);
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
				pointOnStartObject = getStartObject().getShape().nearestOutlinePoint(pointOnStartObject);
				pointOnEndObject = getEndObject().getShape().nearestOutlinePoint(pointOnEndObject);

				// And then we convert to local coordinates
				FGEPoint newP1 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), pointOnStartObject,
						getGraphicalRepresentation());
				FGEPoint newP2 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), pointOnEndObject,
						getGraphicalRepresentation());

				// Finally assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(getGraphicalRepresentation(), newP1);
				cp2 = new ConnectorControlPoint(getGraphicalRepresentation(), newP2);
				controlPoints.clear();
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint());
				}
			}

			else {
				logger.warning("Unexpected covering area found : " + coveringArea);
			}
		}

		else if (lineConnectorType == LineConnectorType.FUNNY) {

			FGEPoint newP1 = getEndObject().getShape().nearestOutlinePoint(
					GraphicalRepresentation.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5), getEndObject()));
			newP1 = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), newP1, getGraphicalRepresentation());

			FGEPoint newP2 = getStartObject().getShape().nearestOutlinePoint(
					GraphicalRepresentation.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5), getStartObject()));
			newP2 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), newP2, getGraphicalRepresentation());

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(getGraphicalRepresentation(), newP1);
			cp2 = new ConnectorControlPoint(getGraphicalRepresentation(), newP2);
			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
		}

		else if (lineConnectorType == LineConnectorType.ADJUSTABLE) {

			if (cp1RelativeToStartObject == null || cp2RelativeToEndObject == null) {
				// In this case default location is obtained by center_to_center type
				lineConnectorType = LineConnectorType.CENTER_TO_CENTER;
				updateControlPoints();
				lineConnectorType = LineConnectorType.ADJUSTABLE;
				cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), cp1.getPoint(),
						getStartObject());
				cp2RelativeToEndObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), cp2.getPoint(),
						getEndObject());
			}

			// We have either the old position, or the default one
			// We need now to find updated position according to eventual shape move, resize, reshaped, etc...
			// To do that, use outlineIntersect();

			FGEPoint newP1 = null; /* = cp1.getPoint(); */
			if (cp1 != null) {
				newP1 = cp1.getPoint();
			}
			cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cp1RelativeToStartObject);
			if (cp1RelativeToStartObject != null) {
				newP1 = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject,
						getGraphicalRepresentation());
			}

			FGEPoint newP2 = null; /* = cp2.getPoint(); */
			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cp2RelativeToEndObject);
			if (cp2RelativeToEndObject != null) {
				newP2 = GraphicalRepresentation
						.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject, getGraphicalRepresentation());
			}

			cp1 = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), newP1) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					FGEShape<?> shape = getStartObject().getShape().getShape();
					return shape.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartObject(),
							getGraphicalRepresentation()));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), pt,
							getStartObject());
					getGraphicalRepresentation().notifyConnectorChanged();
					return true;
				}

			};

			cp2 = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), newP2) {
				@Override
				public FGEArea getDraggingAuthorizedArea() {
					FGEShape<?> shape = getEndObject().getShape().getShape();
					return shape.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndObject(),
							getGraphicalRepresentation()));
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp2RelativeToEndObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), pt,
							getEndObject());
					if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
						if (middleSymbolLocationControlPoint != null) {
							middleSymbolLocationControlPoint.setPoint(getMiddleSymbolLocation());
						}
					}
					getGraphicalRepresentation().notifyConnectorChanged();
					return true;
				}
			};

			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
		}

		else {

			logger.warning("Unexpected lineConnectorType=" + lineConnectorType);
		}
	}

	@Override
	public void refreshConnector() {
		if (!needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector();

		firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		if (!firstUpdated) {
			return true;
		}
		return super.needsRefresh();
	}

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		/*
		 * if (FGEConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		/*
		 * if (lineConnectorType == LineConnectorType.ADJUSTABLE) { g.setForeground(fs0); g.setBackground(bs0); FGEShape<?> shape =
		 * getStartObject().getShape().getShape();
		 * shape.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartObject().getDrawable(), getDrawable())) .paint(g);
		 * g.setForeground(fs1); g.setBackground(bs1); FGEShape<?> shape2 = getEndObject().getShape().getShape();
		 * shape2.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndObject().getDrawable(), getDrawable())) .paint(g);
		 * }
		 */

		g.useDefaultForegroundStyle();
		// logger.info("paintConnector() "+cp1.getPoint()+"-"+cp2.getPoint()+" with "+g.getCurrentForeground());
		g.drawLine(cp1.getPoint(), cp2.getPoint());

		Point cp1InView = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(cp1.getPoint(), 1);
		Point cp2InView = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(cp2.getPoint(), 1);

		// double angle = Math.atan2(cp2.getPoint().x-cp1.getPoint().x, cp2.getPoint().y-cp1.getPoint().y)+Math.PI/2;
		double angle = Math.atan2(cp2InView.x - cp1InView.x, cp2InView.y - cp1InView.y) + Math.PI / 2;

		// System.out.println("Angle1="+Math.toDegrees(angle));
		// System.out.println("Angle2="+Math.toDegrees(angle+Math.PI));

		if (getGraphicalRepresentation().getStartSymbol() != StartSymbolType.NONE) {
			g.drawSymbol(cp1.getPoint(), getGraphicalRepresentation().getStartSymbol(), getGraphicalRepresentation().getStartSymbolSize(),
					angle);
		}

		if (getGraphicalRepresentation().getEndSymbol() != EndSymbolType.NONE) {
			g.drawSymbol(cp2.getPoint(), getGraphicalRepresentation().getEndSymbol(), getGraphicalRepresentation().getEndSymbolSize(),
					angle + Math.PI);
		}

		if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
			g.drawSymbol(getMiddleSymbolLocation(), getGraphicalRepresentation().getMiddleSymbol(), getGraphicalRepresentation()
					.getMiddleSymbolSize(), angle + Math.PI);
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
	public FGEPoint getMiddleSymbolLocation() {
		if (cp1 == null || cp2 == null) {
			return new FGEPoint(0, 0);
		}
		return new FGESegment(cp1.getPoint(), cp2.getPoint())
				.getScaledPoint(getGraphicalRepresentation().getRelativeMiddleSymbolLocation());
	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		if (cp1 == null || cp2 == null) {
			logger.warning("Invalid date in LineConnector: control points are null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(cp1.getPoint(), scale);
		Point point2 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(cp2.getPoint(), scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	public static enum LineConnectorType {
		CENTER_TO_CENTER, MINIMAL_LENGTH, FUNNY, ADJUSTABLE
	}

	private LineConnectorType lineConnectorType = LineConnectorType.MINIMAL_LENGTH;

	public LineConnectorType getLineConnectorType() {
		return lineConnectorType;
	}

	public void setLineConnectorType(LineConnectorType aLineConnectorType) {
		lineConnectorType = aLineConnectorType;
		if (getGraphicalRepresentation() != null) {
			updateControlPoints();
			getGraphicalRepresentation().notifyConnectorChanged();
		}
	}

	// Used for serialization only
	public FGEPoint _getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	// Used for serialization only
	public void _setCp1RelativeToStartObject(FGEPoint aPoint) {
		this.cp1RelativeToStartObject = aPoint;
	}

	// Used for serialization only
	public FGEPoint _getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	// Used for serialization only
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
	public LineConnector clone() {
		LineConnector returned = new LineConnector(null);
		returned.setLineConnectorType(getLineConnectorType());
		returned._setCp1RelativeToStartObject(_getCp1RelativeToStartObject());
		returned._setCp2RelativeToEndObject(_getCp2RelativeToEndObject());
		return returned;
	}

}
