package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public class CurvedPolylinConnectorSpecificationImpl extends ConnectorSpecificationImpl implements CurvedPolylinConnectorSpecification {

	private FGEPoint p1 = new FGEPoint();
	private FGEPoint p2 = new FGEPoint();
	private Vector<ControlPoint> controlPoints;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurvedPolylinConnectorSpecificationImpl() {
		super();
		controlPoints = new Vector<ControlPoint>();
	}

	/*public CurvedPolylinConnectorSpecificationImpl(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
		controlPoints.add(new ConnectorAdjustingControlPoint(graphicalRepresentation, p1));
		controlPoints.add(new ConnectorAdjustingControlPoint(graphicalRepresentation, p2));
	}*/

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.CURVED_POLYLIN;
	}

	@Override
	public List<ControlPoint> rebuildControlPoints(ConnectorNode<?> connectorNode) {
		updateControlPoints(connectorNode);
		return controlPoints;
	}

	private void updateControlPoints(final ConnectorNode<?> connectorNode) {

		controlPoints.add(new ConnectorAdjustingControlPoint(connectorNode, p1));
		controlPoints.add(new ConnectorAdjustingControlPoint(connectorNode, p2));

		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(connectorNode.getStartNode(), new FGEPoint(0.5, 0.5), connectorNode);
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(connectorNode.getEndNode(), new FGEPoint(0.5, 0.5), connectorNode);

		p1.x = newP1.x;
		p1.y = newP1.y;
		p2.x = newP2.x;
		p2.y = newP2.y;
	}

	@Override
	public void drawConnector(ConnectorNode<?> connectorNode, FGEConnectorGraphics g) {
		updateControlPoints(connectorNode);

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
	public double distanceToConnector(FGEPoint aPoint, double scale, ConnectorNode<?> connectorNode) {
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(p1, scale);
		Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(p2, scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	@Override
	public void refreshConnector(ConnectorNode<?> connectorNode, boolean force) {
		if (!force && !needsRefresh(connectorNode)) {
			return;
		}

		updateControlPoints(connectorNode);

		super.refreshConnector(connectorNode, force);

		// firstUpdated = true;

	}

	@Override
	public boolean needsRefresh(ConnectorNode<?> connectorNode) {
		// if (!firstUpdated) return true;
		return super.needsRefresh(connectorNode);
	}

	@Override
	public FGEPoint getMiddleSymbolLocation(ConnectorNode<?> connectorNode) {
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
	public CurvedPolylinConnectorSpecification clone() {
		CurvedPolylinConnectorSpecification returned = new CurvedPolylinConnectorSpecificationImpl();
		return returned;
	}

}
