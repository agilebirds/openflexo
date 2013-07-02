package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.CurveConnector;
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

public class CurveConnectorImpl extends ConnectorImpl implements CurveConnector {

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
	public CurveConnectorImpl() {
		this(null);
	}

	public CurveConnectorImpl(ConnectorGraphicalRepresentation graphicalRepresentation) {
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

				FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5),
						getStartObject());
				cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
				if (cp1RelativeToStartObject == null) {
					logger.warning("outlineIntersect() returned null");
					cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
				}

				FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5),
						getEndObject());
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
				newP1 = FGEUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject, getGraphicalRepresentation());
			}

			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cp2RelativeToEndObject);
			if (cp2RelativeToEndObject != null) {
				newP2 = FGEUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject, getGraphicalRepresentation());
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

			FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), cpPosition,
					getStartObject());
			cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cpPositionSeenFromStartObject);
			if (cp1RelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
			}

			FGEPoint cpPositionSeenFromEndObject = FGEUtils
					.convertNormalizedPoint(getGraphicalRepresentation(), cpPosition, getEndObject());
			cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cpPositionSeenFromEndObject);
			if (cp2RelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
			}

			newP1 = FGEUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject, getGraphicalRepresentation());
			newP2 = FGEUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject, getGraphicalRepresentation());

		}

		cp1 = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), newP1) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getAreBoundsAdjustable()) {
					FGEShape<?> shape = getStartObject().getShape().getShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(getStartObject(),
							getGraphicalRepresentation()));
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
					cp1RelativeToStartObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), pt, getStartObject());
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
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(getEndObject(),
							getGraphicalRepresentation()));
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
					cp2RelativeToEndObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), pt, getEndObject());
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
		return new FGESegment(FGEUtils.convertNormalizedPoint(getStartObject(), getStartObject().getShape().getShape().getCenter(),
				getGraphicalRepresentation()), FGEUtils.convertNormalizedPoint(getEndObject(), getEndObject().getShape().getShape()
				.getCenter(), getGraphicalRepresentation()));
	}

	@Override
	public void connectorHasBeenModified() {
		willBeModified = false;
		previous = null;
		super.connectorHasBeenModified();
	};

	private void updateFromNewCPPosition() {
		FGEPoint cpPositionSeenFromStartObject = FGEUtils
				.convertNormalizedPoint(getGraphicalRepresentation(), cpPosition, getStartObject());
		cp1RelativeToStartObject = getStartObject().getShape().outlineIntersect(cpPositionSeenFromStartObject);
		if (cp1RelativeToStartObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint cpPositionSeenFromEndObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), cpPosition, getEndObject());
		cp2RelativeToEndObject = getEndObject().getShape().outlineIntersect(cpPositionSeenFromEndObject);
		if (cp2RelativeToEndObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(getStartObject(), cp1RelativeToStartObject, getGraphicalRepresentation());
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(getEndObject(), cp2RelativeToEndObject, getGraphicalRepresentation());

		cp1.setPoint(newP1);
		cp2.setPoint(newP2);
	}

	private FGEQuadCurve curve;

	private void refreshCurve() {
		if (cp1 != null && cp != null && cp2 != null) {
			curve = FGEQuadCurve.makeCurveFromPoints(cp1.getPoint(), cp.getPoint(), cp2.getPoint());
		}
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
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector(force);

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

	@Override
	public FGEPoint _getCp1RelativeToStartObject() {
		return cp1RelativeToStartObject;
	}

	@Override
	public void _setCp1RelativeToStartObject(FGEPoint aPoint) {
		this.cp1RelativeToStartObject = aPoint;
	}

	@Override
	public FGEPoint _getCp2RelativeToEndObject() {
		return cp2RelativeToEndObject;
	}

	@Override
	public void _setCp2RelativeToEndObject(FGEPoint aPoint) {
		this.cp2RelativeToEndObject = aPoint;
	}

	@Override
	public FGEPoint _getCpPosition() {
		return cpPosition;
	}

	@Override
	public void _setCpPosition(FGEPoint cpPosition) {
		this.cpPosition = cpPosition;
	}

	@Override
	public boolean getAreBoundsAdjustable() {
		return areBoundsAdjustable;
	}

	@Override
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

	@Override
	public CurveConnector clone() {
		CurveConnector returned = new CurveConnectorImpl(null);
		returned._setCpPosition(_getCpPosition());
		returned._setCp1RelativeToStartObject(_getCp1RelativeToStartObject());
		returned._setCp2RelativeToEndObject(_getCp2RelativeToEndObject());
		returned.setAreBoundsAdjustable(getAreBoundsAdjustable());
		return returned;
	}

}
