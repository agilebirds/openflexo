package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
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
		super();
		controlPoints = new Vector<ControlPoint>();
	}

	/*public CurveConnectorImpl(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
	}*/

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVE;
	}

	@Override
	public List<ControlPoint> rebuildControlPoints(ConnectorNode<?> connectorNode) {
		updateControlPoints(connectorNode);
		return controlPoints;
	}

	private void updateControlPoints(final ConnectorNode<?> connectorNode) {
		FGEPoint newP1 = null;
		FGEPoint newP2 = null;

		if (areBoundsAdjustable) {

			if (cp1RelativeToStartObject == null || cp2RelativeToEndObject == null) {

				// To compute initial locations, we try to draw a line joining both center
				// We have to compute the intersection between this line and the outline
				// of joined shapes

				FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), new FGEPoint(
						0.5, 0.5), connectorNode.getStartNode());
				cp1RelativeToStartObject = connectorNode.getStartNode().getShape()
						.outlineIntersect(centerOfEndObjectSeenFromStartObject, connectorNode.getStartNode());
				if (cp1RelativeToStartObject == null) {
					logger.warning("outlineIntersect() returned null");
					cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
				}

				FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), new FGEPoint(
						0.5, 0.5), connectorNode.getEndNode());
				cp2RelativeToEndObject = connectorNode.getEndNode().getShape()
						.outlineIntersect(centerOfStartObjectSeenFromEndObject, connectorNode.getEndNode());
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
			cp1RelativeToStartObject = connectorNode.getStartNode().getShape()
					.outlineIntersect(cp1RelativeToStartObject, connectorNode.getStartNode());
			if (cp1RelativeToStartObject != null) {
				newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), cp1RelativeToStartObject, connectorNode);
			}

			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			cp2RelativeToEndObject = connectorNode.getEndNode().getShape()
					.outlineIntersect(cp2RelativeToEndObject, connectorNode.getEndNode());
			if (cp2RelativeToEndObject != null) {
				newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), cp2RelativeToEndObject, connectorNode);
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

			updateCPPositionIfNeeded(connectorNode);

			FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode, cpPosition,
					connectorNode.getStartNode());
			cp1RelativeToStartObject = connectorNode.getStartNode().getShape()
					.outlineIntersect(cpPositionSeenFromStartObject, connectorNode.getStartNode());
			if (cp1RelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
			}

			FGEPoint cpPositionSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode, cpPosition, connectorNode.getEndNode());
			cp2RelativeToEndObject = connectorNode.getEndNode().getShape()
					.outlineIntersect(cpPositionSeenFromEndObject, connectorNode.getEndNode());
			if (cp2RelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
			}

			newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), cp1RelativeToStartObject, connectorNode);
			newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), cp2RelativeToEndObject, connectorNode);

		}

		cp1 = new ConnectorAdjustingControlPoint(connectorNode, newP1) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getAreBoundsAdjustable()) {
					FGEShape<?> shape = connectorNode.getStartNode().getFGEShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(
							connectorNode.getStartNode(), connectorNode));
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
					cp1RelativeToStartObject = FGEUtils.convertNormalizedPoint(connectorNode, pt, connectorNode.getStartNode());
					refreshCurve();
					connectorNode.notifyConnectorChanged();
				}
				return true;
			}

		};

		cp2 = new ConnectorAdjustingControlPoint(connectorNode, newP2) {
			@Override
			public FGEArea getDraggingAuthorizedArea() {
				if (getAreBoundsAdjustable()) {
					FGEShape<?> shape = connectorNode.getEndNode().getFGEShape();
					FGEShape<?> returned = (FGEShape<?>) shape.transform(FGEUtils.convertNormalizedCoordinatesAT(
							connectorNode.getEndNode(), connectorNode));
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
					cp2RelativeToEndObject = FGEUtils.convertNormalizedPoint(connectorNode, pt, connectorNode.getEndNode());
					refreshCurve();
					connectorNode.notifyConnectorChanged();
				}
				return true;
			}
		};

		cp = new ConnectorAdjustingControlPoint(connectorNode, cpPosition) {
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
					updateFromNewCPPosition(connectorNode);
				}
				refreshCurve();
				connectorNode.notifyConnectorChanged();
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
	private void updateCPPositionIfNeeded(ConnectorNode<?> connectorNode) {
		if (willBeModified && previous != null) {
			FGESegment newSegment = getCenterToCenterSegment(connectorNode);
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
	public void connectorWillBeModified(ConnectorNode<?> connectorNode) {
		super.connectorWillBeModified(connectorNode);
		willBeModified = true;
		previous = getCenterToCenterSegment(connectorNode);
	}

	private FGESegment getCenterToCenterSegment(ConnectorNode<?> connectorNode) {
		return new FGESegment(FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), connectorNode.getStartNode().getFGEShape()
				.getCenter(), connectorNode), FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), connectorNode.getEndNode()
				.getFGEShape().getCenter(), connectorNode));
	}

	@Override
	public void connectorHasBeenModified(ConnectorNode<?> connectorNode) {
		willBeModified = false;
		previous = null;
		super.connectorHasBeenModified(connectorNode);
	};

	private void updateFromNewCPPosition(ConnectorNode<?> connectorNode) {
		FGEPoint cpPositionSeenFromStartObject = FGEUtils.convertNormalizedPoint(connectorNode, cpPosition, connectorNode.getStartNode());
		cp1RelativeToStartObject = connectorNode.getStartNode().getShape()
				.outlineIntersect(cpPositionSeenFromStartObject, connectorNode.getStartNode());
		if (cp1RelativeToStartObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp1RelativeToStartObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint cpPositionSeenFromEndObject = FGEUtils.convertNormalizedPoint(connectorNode, cpPosition, connectorNode.getEndNode());
		cp2RelativeToEndObject = connectorNode.getEndNode().getShape()
				.outlineIntersect(cpPositionSeenFromEndObject, connectorNode.getEndNode());
		if (cp2RelativeToEndObject == null) {
			logger.warning("outlineIntersect() returned null");
			cp2RelativeToEndObject = new FGEPoint(0.5, 0.5);
		}

		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), cp1RelativeToStartObject, connectorNode);
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), cp2RelativeToEndObject, connectorNode);

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
	public void drawConnector(ConnectorNode<?> connectorNode, FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector(connectorNode);
		}

		g.useDefaultForegroundStyle();

		if (curve != null) {

			curve.paint(g);

			// Draw eventual symbols
			if (connectorNode.getGraphicalRepresentation().getStartSymbol() != StartSymbolType.NONE) {
				FGESegment firstSegment = curve.getApproximatedStartTangent();
				FGESegment viewSegment = firstSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(firstSegment.getP1(), connectorNode.getGraphicalRepresentation().getStartSymbol(), connectorNode
						.getGraphicalRepresentation().getStartSymbolSize(), viewSegment.getAngle());
			}
			if (connectorNode.getGraphicalRepresentation().getEndSymbol() != EndSymbolType.NONE) {
				FGESegment lastSegment = curve.getApproximatedEndTangent();
				FGESegment viewSegment = lastSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(lastSegment.getP2(), connectorNode.getGraphicalRepresentation().getEndSymbol(), connectorNode
						.getGraphicalRepresentation().getEndSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
			if (connectorNode.getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				FGESegment cpSegment = curve.getApproximatedControlPointTangent();
				FGESegment viewSegment = cpSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(curve.getP3(), connectorNode.getGraphicalRepresentation().getMiddleSymbol(), connectorNode
						.getGraphicalRepresentation().getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
		}

	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale, ConnectorNode<?> connectorNode) {
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
	public FGEPoint getMiddleSymbolLocation(ConnectorNode<?> connectorNode) {
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
			/*if (getGraphicalRepresentation() != null) {
				updateControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}*/
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
		CurveConnector returned = new CurveConnectorImpl();
		returned._setCpPosition(_getCpPosition());
		returned._setCp1RelativeToStartObject(_getCp1RelativeToStartObject());
		returned._setCp2RelativeToEndObject(_getCp2RelativeToEndObject());
		returned.setAreBoundsAdjustable(getAreBoundsAdjustable());
		return returned;
	}

}
