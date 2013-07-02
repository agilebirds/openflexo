package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.CurvedPolylinConnector;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public class CurvedPolylinConnectorImpl extends ConnectorImpl implements CurvedPolylinConnector {

	private FGEPoint p1 = new FGEPoint();
	private FGEPoint p2 = new FGEPoint();
	private Vector<ControlPoint> controlPoints;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurvedPolylinConnectorImpl() {
		this(null);
	}

	public CurvedPolylinConnectorImpl(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
		controlPoints.add(new ConnectorAdjustingControlPoint(graphicalRepresentation, p1));
		controlPoints.add(new ConnectorAdjustingControlPoint(graphicalRepresentation, p2));
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVED_POLYLIN;
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}

	private void updateControlPoints() {
		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5), getGraphicalRepresentation());
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5), getGraphicalRepresentation());

		p1.x = newP1.x;
		p1.y = newP1.y;
		p2.x = newP2.x;
		p2.y = newP2.y;
	}

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		updateControlPoints();

		g.drawLine(p1.x, p1.y, p2.x, p2.y);

	}

	@Override
	public double getStartAngle() {
		return FGEUtils.getSlope(p1, p2);
	}

	@Override
	public double getEndAngle() {
		return FGEUtils.getSlope(p2, p1);
	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		Point testPoint = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(p1, scale);
		Point point2 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(p2, scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector(force);

		// firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		// if (!firstUpdated) return true;
		return super.needsRefresh();
	}

	@Override
	public FGEPoint getMiddleSymbolLocation() {
		// TODO Auto-generated method stub
		return new FGEPoint(0.5, 0.5);
	}

	private FGERectangle NORMALIZED_BOUNDS = new FGERectangle(0, 0, 1, 1, Filling.FILLED);

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
		return p1;
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getEndLocation() {
		return p2;
	}

	@Override
	public CurvedPolylinConnector clone() {
		CurvedPolylinConnector returned = new CurvedPolylinConnectorImpl(null);
		return returned;
	}

}
