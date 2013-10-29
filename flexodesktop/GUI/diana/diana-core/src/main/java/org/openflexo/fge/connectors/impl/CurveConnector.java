package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.CurveConnectorSpecification;
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
import org.openflexo.fge.geom.GeomUtils;
import org.openflexo.fge.geom.ParallelLinesException;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public class CurveConnector extends ConnectorImpl<CurveConnectorSpecification> {

	private static final Logger logger = Logger.getLogger(CurveConnectorSpecification.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ControlPoint cp;
	private List<ControlPoint> controlPoints;

	private boolean firstUpdated = false;

	private FGESegment previous;
	private boolean willBeModified = false;

	private FGEQuadCurve curve;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public CurveConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new ArrayList<ControlPoint>();
	}

	@Override
	public void delete() {
		super.delete();
		cp1 = null;
		cp2 = null;
		controlPoints.clear();
		controlPoints = null;
	}

	public FGEPoint getCp1RelativeToStartObject() {
		return getPropertyValue(CurveConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT);
	}

	public void setCp1RelativeToStartObject(FGEPoint aPoint) {
		setPropertyValue(CurveConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT, aPoint);
	}

	public FGEPoint getCp2RelativeToEndObject() {
		return getPropertyValue(CurveConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT);
	}

	public void setCp2RelativeToEndObject(FGEPoint aPoint) {
		setPropertyValue(CurveConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT, aPoint);
	}

	public FGEPoint getCpPosition() {
		return getPropertyValue(CurveConnectorSpecification.CP_POSITION);
	}

	public void setCpPosition(FGEPoint cpPosition) {
		setPropertyValue(CurveConnectorSpecification.CP_POSITION, cpPosition);
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		// TODO: perfs issue : do not update all the time !!!
		updateControlPoints();
		return controlPoints;
	}

	private void updateControlPoints() {
		FGEPoint newP1 = null;
		FGEPoint newP2 = null;

		if (getConnectorSpecification().getAreBoundsAdjustable()) {

			System.out.println("Passe par la");
			if (getConnectorSpecification().getCp1RelativeToStartObject() == null
					|| getConnectorSpecification().getCp2RelativeToEndObject() == null) {

				// To compute initial locations, we try to draw a line joining both center
				// We have to compute the intersection between this line and the outline
				// of joined shapes

				FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(getEndNode(), new FGEPoint(0.5, 0.5),
						getStartNode());
				setCp1RelativeToStartObject(getStartNode().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject));
				if (getCp1RelativeToStartObject() == null) {
					logger.warning("outlineIntersect() returned null");
					setCp1RelativeToStartObject(new FGEPoint(0.5, 0.5));
				}

				FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(getStartNode(), new FGEPoint(0.5, 0.5),
						getEndNode());
				setCp2RelativeToEndObject(getEndNode().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject));
				if (getCp2RelativeToEndObject() == null) {
					logger.warning("outlineIntersect() returned null");
					setCp2RelativeToEndObject(new FGEPoint(0.5, 0.5));
				}
			}

			// We have either the old position, or the default one
			// We need now to find updated position according to eventual shape move, resize, reshaped, etc...
			// To do that, use outlineIntersect();

			if (cp1 != null) {
				newP1 = cp1.getPoint();
			}
			setCp1RelativeToStartObject(getStartNode().getShape().outlineIntersect(getCp1RelativeToStartObject()));
			if (getCp1RelativeToStartObject() != null) {
				newP1 = FGEUtils.convertNormalizedPoint(getStartNode(), getCp1RelativeToStartObject(), connectorNode);
			}

			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			setCp2RelativeToEndObject(getEndNode().getShape().outlineIntersect(getCp2RelativeToEndObject()));
			if (getCp2RelativeToEndObject() != null) {
				newP2 = FGEUtils.convertNormalizedPoint(getEndNode(), getCp2RelativeToEndObject(), connectorNode);
			}

			if (getCpPosition() == null) {
				// The 0.3 is there so that we can see the curve of the edge.
				setCpPosition(new FGEPoint(0.5, 0.3));
			}
		}

		else {

			System.out.println("Passe par ici");

			// Not adjustable bounds

			if (getCpPosition() == null) {
				setCpPosition(new FGEPoint(0.5, 0.4));
			}

			updateCPPositionIfNeeded();

			FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getStartNode());

			System.out.println("cpPositionSeenFromStartObject=" + cpPositionSeenFromStartObject);
			System.out.println("getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject)="
					+ getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject));

			setCp1RelativeToStartObject(getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject));
			if (getCp1RelativeToStartObject() == null) {
				logger.warning("outlineIntersect() returned null");
				setCp1RelativeToStartObject(new FGEPoint(0.5, 0.5));
			}

			FGEPoint cpPositionSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getEndNode());
			setCp2RelativeToEndObject(getEndNode().getShape().outlineIntersect(cpPositionSeenFromEndObject));
			if (getCp2RelativeToEndObject() == null) {
				logger.warning("outlineIntersect() returned null");
				setCp2RelativeToEndObject(new FGEPoint(0.5, 0.5));
			}

			newP1 = FGEUtils.convertNormalizedPoint(getStartNode(), getCp1RelativeToStartObject(), connectorNode);
			newP2 = FGEUtils.convertNormalizedPoint(getEndNode(), getCp2RelativeToEndObject(), connectorNode);

		}

		System.out.println("newP1=" + newP1);
		System.out.println("newP2=" + newP2);
		if (newP1 == null) {
			System.out.println("Quel est le pb ???");
			FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getStartNode());
			FGEPoint newPoint = getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject);
			setCp1RelativeToStartObject(newPoint);
			System.out.println("Et il me renvoit " + getCp1RelativeToStartObject());
			System.out.println("Bon, on regarde ????");
			setCp1RelativeToStartObject(newPoint);
		}

		cp1 = new ConnectorAdjustingControlPoint(connectorNode, newP1) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getConnectorSpecification().getAreBoundsAdjustable()) {
					FGEShape<?> shape = getStartNode().getFGEShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(getStartNode(),
							connectorNode));
					returned.setIsFilled(false);
					return returned;
				} else {
					return new FGEEmptyArea();
				}
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				if (getConnectorSpecification().getAreBoundsAdjustable()) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp1RelativeToStartObject(FGEUtils.convertNormalizedPoint(connectorNode, pt, getStartNode()));
					refreshCurve();
					connectorNode.notifyConnectorModified();
				}
				return true;
			}

		};

		cp2 = new ConnectorAdjustingControlPoint(connectorNode, newP2) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getConnectorSpecification().getAreBoundsAdjustable()) {
					FGEShape<?> shape = getEndNode().getFGEShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(getEndNode(),
							connectorNode));
					returned.setIsFilled(false);
					return returned;
				} else {
					return new FGEEmptyArea();
				}
			}

			@Override
			public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
					FGEPoint initialPoint, MouseEvent event) {
				if (getConnectorSpecification().getAreBoundsAdjustable()) {
					FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp2RelativeToEndObject(FGEUtils.convertNormalizedPoint(connectorNode, pt, getEndNode()));
					refreshCurve();
					connectorNode.notifyConnectorModified();
				}
				return true;
			}
		};

		cp = new ConnectorAdjustingControlPoint(connectorNode, getCpPosition()) {
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
				setCpPosition(pt);
				if (!getConnectorSpecification().getAreBoundsAdjustable()) {
					updateFromNewCPPosition();
				}
				refreshCurve();
				connectorNode.notifyConnectorModified();
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
				at.transform(getCpPosition(), newCPPosition);
				setCpPosition(newCPPosition);
				previous = newSegment;
			}
		}
	}

	@Override
	public void connectorWillBeModified() {
		super.connectorWillBeModified();
		willBeModified = true;
		previous = getCenterToCenterSegment();
	}

	private FGESegment getCenterToCenterSegment() {
		return new FGESegment(FGEUtils.convertNormalizedPoint(getStartNode(), getStartNode().getFGEShape().getCenter(), connectorNode),
				FGEUtils.convertNormalizedPoint(getEndNode(), getEndNode().getFGEShape().getCenter(), connectorNode));
	}

	@Override
	public void connectorHasBeenModified() {
		willBeModified = false;
		previous = null;
		super.connectorHasBeenModified();
	};

	private void updateFromNewCPPosition() {
		FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getStartNode());
		setCp1RelativeToStartObject(getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject));
		if (getCp1RelativeToStartObject() == null) {
			logger.warning("outlineIntersect() returned null");
			setCp1RelativeToStartObject(new FGEPoint(0.5, 0.5));
		}

		FGEPoint cpPositionSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getEndNode());
		setCp2RelativeToEndObject(getEndNode().getShape().outlineIntersect(cpPositionSeenFromEndObject));
		if (getCp2RelativeToEndObject() == null) {
			logger.warning("outlineIntersect() returned null");
			setCp2RelativeToEndObject(new FGEPoint(0.5, 0.5));
		}

		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(getStartNode(), getCp1RelativeToStartObject(), connectorNode);
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(getEndNode(), getCp2RelativeToEndObject(), connectorNode);

		cp1.setPoint(newP1);
		cp2.setPoint(newP2);
	}

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
			if (connectorNode.getConnectorSpecification().getStartSymbol() != StartSymbolType.NONE) {
				FGESegment firstSegment = curve.getApproximatedStartTangent();
				FGESegment viewSegment = firstSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(firstSegment.getP1(), connectorNode.getConnectorSpecification().getStartSymbol(), connectorNode
						.getConnectorSpecification().getStartSymbolSize(), viewSegment.getAngle());
			}
			if (connectorNode.getConnectorSpecification().getEndSymbol() != EndSymbolType.NONE) {
				FGESegment lastSegment = curve.getApproximatedEndTangent();
				FGESegment viewSegment = lastSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(lastSegment.getP2(), connectorNode.getConnectorSpecification().getEndSymbol(), connectorNode
						.getConnectorSpecification().getEndSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
			if (connectorNode.getConnectorSpecification().getMiddleSymbol() != MiddleSymbolType.NONE) {
				FGESegment cpSegment = curve.getApproximatedControlPointTangent();
				FGESegment viewSegment = cpSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(curve.getP3(), connectorNode.getConnectorSpecification().getMiddleSymbol(), connectorNode
						.getConnectorSpecification().getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
		}

	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		if (curve == null) {
			logger.warning("Curve is null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		FGEPoint nearestPointOnCurve = curve.getNearestPoint(aPoint);
		Point nearestPoint = connectorNode.convertNormalizedPointToViewCoordinates(nearestPointOnCurve, scale);
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
		if (getCpPosition() == null) {
			return new FGEPoint(0, 0);
		}
		return getCpPosition();
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
		return getCp1RelativeToStartObject();
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getEndLocation() {
		return getCp2RelativeToEndObject();
	}

	@Override
	public double getStartAngle() {
		if (cp1 != null) {
			return GeomUtils.getSlope(FGEPoint.ORIGIN_POINT, cp1.getPoint());
		}
		return 0;
	}

	@Override
	public double getEndAngle() {
		if (cp2 != null) {
			return GeomUtils.getSlope(FGEPoint.ORIGIN_POINT, cp2.getPoint());
		}
		return 0;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getSource() == getConnectorSpecification()) {
		}

		// if (notification instanceof FGENotification && observable == getConnectorSpecification()) {
		// Those notifications are forwarded by the connector specification
		// FGENotification notif = (FGENotification) notification;

		// }

	}
}
