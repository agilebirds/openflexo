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
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.GraphicalRepresentationUtils;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.ParallelLinesException;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public class CurveConnector extends Connector {

	private static final Logger logger = Logger.getLogger(CurveConnector.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ControlPoint cp;
	private Vector<ControlPoint> controlPoints;

	private FGEPoint cp1RelativeToStartObject;
	private FGEPoint cp2RelativeToEndObject;
	private FGEPoint cpPosition;

	private boolean areBoundsAdjustable;

	private boolean firstUpdated = false;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurveConnector() {
		this(null);
	}

	public CurveConnector(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVE;
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}

	private void updateControlPoints() {
		FGEPoint newP1 = null;
		FGEPoint newP2 = null;

		if (areBoundsAdjustable) {

			if (cp1RelativeToStartObject == null || cp2RelativeToEndObject == null) {

				// To compute initial locations, we try to draw a line joining both center
				// We have to compute the intersection between this line and the outline
				// of joined shapes

				FGEPoint centerOfEndObjectSeenFromStartObject = GraphicalRepresentationUtils.convertNormalizedPoint(getEndObject(),
						new FGEPoint(0.5, 0.5), getStartObject());
				cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
				if (cp1RelativeToStartObject == null) {
					logger.warning("outlineIntersect() returned null");
					cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
				}

				FGEPoint centerOfStartObjectSeenFromEndObject = GraphicalRepresentationUtils.convertNormalizedPoint(getStartObject(),
						new FGEPoint(0.5, 0.5), getEndObject());
				cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
				if (cp2RelativeToEndObject == null) {
					logger.warning("outlineIntersect() returned null");
					cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
				}
			}

			// We have either the old position, or the default one
			// We need now to find updated position according to eventual shape move, resize, reshaped, etc...
			// To do that, use outlineIntersect();

			if (cp1 != null) {
				newP1 = cp1.getPoint();
			}
			cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cp1RelativeToStartObject);
			if (cp1RelativeToStartObject != null) {
				newP1 = GraphicalRepresentationUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject,
						getGraphicalRepresentation());
			}

			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cp2RelativeToEndObject);
			if (cp2RelativeToEndObject != null) {
				newP2 = GraphicalRepresentationUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject,
						getGraphicalRepresentation());
			}

			if (cpPosition == null) {
				// The 0.3 is there so that we can see the curve of the edge.
				cpPosition = new FGEPoint(0.5, 0.3);
			}
		}

		else {
			// Not adjustable bounds

			if (cpPosition == null) {
				cpPosition = new FGEPoint(0.5, 0.4);
			}

			updateCPPositionIfNeeded();

			FGEPoint cpPositionSeenFromStartObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(),
					cpPosition, getStartObject());
			cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cpPositionSeenFromStartObject);
			if (cp1RelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
			}

			FGEPoint cpPositionSeenFromEndObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(),
					cpPosition, getEndObject());
			cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cpPositionSeenFromEndObject);
			if (cp2RelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
			}

			newP1 = GraphicalRepresentationUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject,
					getGraphicalRepresentation());
			newP2 = GraphicalRepresentationUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject,
					getGraphicalRepresentation());

		}

		cp1 = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), newP1) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getAreBoundsAdjustable()) {
					FGEShape<?> shape = getStartObject().getShape().getShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(GraphicalRepresentationUtils.convertNormalizedCoordinatesAT(
							getStartObject(), getGraphicalRepresentation()));
					returned.setIsFilled(false);
					return returned;
				} else {
					return new FGEEmptyArea();
				}
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				if (getAreBoundsAdjustable()) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp1RelativeToStartObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), pt,
							getStartObject());
					refreshCurve();
					getGraphicalRepresentation().notifyConnectorChanged();
				}
				return true;
			}

		};

		cp2 = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), newP2) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getAreBoundsAdjustable()) {
					FGEShape<?> shape = getEndObject().getShape().getShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(GraphicalRepresentationUtils.convertNormalizedCoordinatesAT(
							getEndObject(), getGraphicalRepresentation()));
					returned.setIsFilled(false);
					return returned;
				} else {
					return new FGEEmptyArea();
				}
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				if (getAreBoundsAdjustable()) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					cp2RelativeToEndObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(), pt,
							getEndObject());
					refreshCurve();
					getGraphicalRepresentation().notifyConnectorChanged();
				}
				return true;
			}
		};

		cp = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), cpPosition) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				return new FGEPlane();
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				/*
				 * logger.info("dragToPoint() with newRelativePoint="+newRelativePoint+" "
				 * +" pointRelativeToInitialConfiguration="+pointRelativeToInitialConfiguration +" newAbsolutePoint="+newAbsolutePoint
				 * +" initialPoint="+initialPoint);
				 */

				FGEPoint pt = getNearestPointOnAuthorizedArea(/* pointRelativeToInitialConfiguration */newRelativePoint);
				setPoint(pt);
				cpPosition = pt;
				if (!getAreBoundsAdjustable()) {
					updateFromNewCPPosition();
				}
				refreshCurve();
				getGraphicalRepresentation().notifyConnectorChanged();
				return true;
			}
		};

		controlPoints.clear();
		controlPoints.add(cp);
		controlPoints.add(cp2);
		controlPoints.add(cp1);

		refreshCurve();
	}

	/**
	 * This method updates the position according to start/end motions. However, this has a small drawback which is caused by the continuous
	 * change of the system coordinates of the connector. Indeed, it is based on the bounds of the start and end node. If one of them moves,
	 * the coordinates should ideally be compared to the same coordinates system.
	 */
	private void updateCPPositionIfNeeded() {
		if (willBeModified && previous != null) {
			FGESegment newSegment = getCenterToCenterSegment();
			double delta = newSegment.getAngle() - previous.getAngle();
			if (Math.abs(delta) > FGEGeometricObject.EPSILON) {
				FGEPoint inter;
				try {
					inter = FGEAbstractLine.getLineIntersection(previous, newSegment);
				} catch (ParallelLinesException e) {
					return;
				}
				FGEPoint newCPPosition = new FGEPoint();
				AffineTransform at = AffineTransform.getTranslateInstance(inter.x, inter.y);
				at.concatenate(AffineTransform.getRotateInstance(-delta));
				at.concatenate(AffineTransform.getTranslateInstance(-inter.x, -inter.y));
				at.transform(cpPosition, newCPPosition);
				cpPosition = newCPPosition;
				previous = newSegment;
			}
		}
	}

	private FGESegment previous;
	private boolean willBeModified = false;

	@Override
	public void connectorWillBeModified() {
		super.connectorWillBeModified();
		willBeModified = true;
		previous = getCenterToCenterSegment();
	}

	private FGESegment getCenterToCenterSegment() {
		return new FGESegment(GraphicalRepresentationUtils.convertNormalizedPoint(getStartObject(), getStartObject().getShape().getShape()
				.getCenter(), getGraphicalRepresentation()), GraphicalRepresentationUtils.convertNormalizedPoint(getEndObject(),
				getEndObject().getShape().getShape().getCenter(), getGraphicalRepresentation()));
	}

	@Override
	public void connectorHasBeenModified() {
		willBeModified = false;
		previous = null;
		super.connectorHasBeenModified();
	};

	private void updateFromNewCPPosition() {
		FGEPoint cpPositionSeenFromStartObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(),
				cpPosition, getStartObject());
		cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cpPositionSeenFromStartObject);
		if (cp1RelativeToStartObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint cpPositionSeenFromEndObject = GraphicalRepresentationUtils.convertNormalizedPoint(getGraphicalRepresentation(),
				cpPosition, getEndObject());
		cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cpPositionSeenFromEndObject);
		if (cp2RelativeToEndObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint newP1 = GraphicalRepresentationUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject,
				getGraphicalRepresentation());
		FGEPoint newP2 = GraphicalRepresentationUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject,
				getGraphicalRepresentation());

		cp1.setPoint(newP1);
		cp2.setPoint(newP2);
	}

	private FGEQuadCurve curve;

	private void refreshCurve() {
		curve = FGEQuadCurve.makeCurveFromPoints(cp1.getPoint(), cp.getPoint(), cp2.getPoint());
	}

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		g.useDefaultForegroundStyle();

		if (curve != null) {

			curve.paint(g);

			// Draw eventual symbols
			if (getGraphicalRepresentation().getStartSymbol() != StartSymbolType.NONE) {
				FGESegment firstSegment = curve.getApproximatedStartTangent();
				FGESegment viewSegment = firstSegment.transform(getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(
						g.getScale()));
				g.drawSymbol(firstSegment.getP1(), getGraphicalRepresentation().getStartSymbol(), getGraphicalRepresentation()
						.getStartSymbolSize(), viewSegment.getAngle());
			}
			if (getGraphicalRepresentation().getEndSymbol() != EndSymbolType.NONE) {
				FGESegment lastSegment = curve.getApproximatedEndTangent();
				FGESegment viewSegment = lastSegment.transform(getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(
						g.getScale()));
				g.drawSymbol(lastSegment.getP2(), getGraphicalRepresentation().getEndSymbol(), getGraphicalRepresentation()
						.getEndSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				FGESegment cpSegment = curve.getApproximatedControlPointTangent();
				FGESegment viewSegment = cpSegment.transform(getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(
						g.getScale()));
				g.drawSymbol(curve.getP3(), getGraphicalRepresentation().getMiddleSymbol(), getGraphicalRepresentation()
						.getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
		}

	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		if (curve == null) {
			logger.warning("Curve is null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(aPoint, scale);
		FGEPoint nearestPointOnCurve = curve.getNearestPoint(aPoint);
		Point nearestPoint = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(nearestPointOnCurve, scale);
		return testPoint.distance(nearestPoint);
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
	public FGEPoint getMiddleSymbolLocation() {
		if (cpPosition == null) {
			return new FGEPoint(0, 0);
		}
		return cpPosition;
	}

	public FGEPoint _getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	public void _setCp1RelativeToStartObject(FGEPoint aPoint) {
		this.cp1RelativeToStartObject = aPoint;
	}

	public FGEPoint _getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	public void _setCp2RelativeToEndObject(FGEPoint aPoint) {
		this.cp2RelativeToEndObject = aPoint;
	}

	public FGEPoint _getCpPosition() {
		return cpPosition;
	}

	public void _setCpPosition(FGEPoint cpPosition) {
		this.cpPosition = cpPosition;
	}

	public boolean getAreBoundsAdjustable() {
		return areBoundsAdjustable;
	}

	public void setAreBoundsAdjustable(boolean aFlag) {
		if (areBoundsAdjustable != aFlag) {
			areBoundsAdjustable = aFlag;
			if (getGraphicalRepresentation() != null) {
				updateControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public FGERectangle getConnectorUsedBounds() {
		if (curve == null) {
			refreshCurve();
		}
		FGERectangle returned = new FGERectangle(Filling.FILLED);
		Rectangle2D rect = curve.getBounds2D();
		returned.x = rect.getX();
		returned.y = rect.getY();
		returned.width = rect.getWidth();
		returned.height = rect.getHeight();
		return returned;
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
	public double getStartAngle() {
		if (cp1 != null) {
			return FGEUtils.getSlope(FGEPoint.ORIGIN_POINT, cp1.getPoint());
		}
		return 0;
	}

	@Override
	public double getEndAngle() {
		if (cp2 != null) {
			return FGEUtils.getSlope(FGEPoint.ORIGIN_POINT, cp2.getPoint());
		}
		return 0;
	}
}
