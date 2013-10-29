package org.openflexo.fge.connectors.impl;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification.RectPolylinConstraints;
import org.openflexo.fge.connectors.rpc.AdjustableEndControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableFirstControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableFirstSegment;
import org.openflexo.fge.connectors.rpc.AdjustableIntermediateControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableIntermediateSegment;
import org.openflexo.fge.connectors.rpc.AdjustableLastControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableLastSegment;
import org.openflexo.fge.connectors.rpc.AdjustableMiddleControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableStartControlPoint;
import org.openflexo.fge.connectors.rpc.AdjustableUniqueSegment;
import org.openflexo.fge.connectors.rpc.RectPolylinAdjustingArea;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ConnectorNonAdjustableControlPoint;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.area.DefaultAreaProvider;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEAreaProvider;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.toolbox.ConcatenedList;

public class RectPolylinConnector extends ConnectorImpl<RectPolylinConnectorSpecification> {

	static final Logger logger = Logger.getLogger(RectPolylinConnectorSpecification.class.getPackage().getName());

	private FGERectPolylin polylin;
	private Vector<FGERectPolylin> potentialPolylin;
	// public FGERectPolylin debugPolylin;

	private ControlPoint p_start;
	private ControlPoint p_end;
	// private ControlPoint crossedControlPoint;

	private Vector<ControlPoint> controlPoints;
	private Vector<ControlArea<?>> controlAreas;

	private boolean firstUpdated = false;

	private boolean wasManuallyAdjusted = false;

	private FGERectPolylin polylinRelativeToStartObject;
	private FGERectPolylin polylinRelativeToEndObject;

	private FGERectPolylin lastKnownCleanPolylinBeforeConnectorRestructuration;
	private boolean isCleaningPolylin = false;

	private FGEPoint fixedStartLocationRelativeToStartObject;
	private FGEPoint fixedEndLocationRelativeToEndObject;
	private FGEPoint _crossedPoint;

	private FGERectPolylin _deserializedPolylin;

	// private static final FGEModelFactory DEBUG_FACTORY = FGECoreUtils.TOOLS_FACTORY;
	// private static final ForegroundStyle DEBUG_GRAY_STROKE = DEBUG_FACTORY.makeForegroundStyle(Color.GRAY, 1.0f, DashStyle.SMALL_DASHES);
	// private static final ForegroundStyle DEBUG_BLACK_STROKE = DEBUG_FACTORY.makeForegroundStyle(Color.BLACK, 3.0f,
	// DashStyle.PLAIN_STROKE);

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public RectPolylinConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new Vector<ControlPoint>();
		controlAreas = new Vector<ControlArea<?>>();
		potentialPolylin = new Vector<FGERectPolylin>();
	}

	@Override
	public void delete() {
		super.delete();
		controlPoints.clear();
		controlPoints = null;
	}

	public List<ControlPoint> getControlPoints() {
		// TODO: perfs issue : do not update all the time !!!
		updateLayout();
		return controlPoints;
	}

	@Override
	public List<? extends ControlArea<?>> getControlAreas() {
		if (connectorNode.getConnectorSpecification().getMiddleSymbol() == MiddleSymbolType.NONE && controlAreas.size() == 0) {
			return controlPoints;
		}

		// Otherwise, we have to manage a concatenation
		if (allControlAreas == null) {
			allControlAreas = new ConcatenedList<ControlArea<?>>();
			allControlAreas.addElementList(controlPoints);
			if (connectorNode.getConnectorSpecification().getMiddleSymbol() != MiddleSymbolType.NONE
					&& middleSymbolLocationControlPoint != null) {
				allControlAreas.add(0, middleSymbolLocationControlPoint);
			}
			allControlAreas.addElementList(controlAreas);
		}
		return allControlAreas;
	}

	private ConcatenedList<ControlArea<?>> allControlAreas;

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		/*
		 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.PINK, 1.0f, DashStyle.SMALL_DASHES));
		 * debugPolylin.paint(g); }
		 */

		/*if (getDebug()) {
			g.setDefaultForeground(DEBUG_GRAY_STROKE);
			for (FGERectPolylin p : potentialPolylin) {
				p.paint(g);
			}
			g.setDefaultForeground(DEBUG_BLACK_STROKE);
			if (polylin != null) {
				polylin.debugPaint(g);
			}
		} else {*/
		g.setDefaultForeground(connectorNode.getGraphicalRepresentation().getForeground());
		if (polylin != null) {
			if (getConnectorSpecification().getIsRounded()) {
				polylin.paintWithRounds(g, getConnectorSpecification().getArcSize());
			} else {
				polylin.paint(g);
			}
		}
		// }

		/*
		 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED, 1.0f, DashStyle.PLAIN_STROKE));
		 * debugPolylin.paint(g); }
		 */

		// Draw eventual symbols
		if (polylin != null && polylin.getSegments() != null && polylin.getSegments().size() > 0) {
			// Segments are here all orthogonal, we can can then rely on getAngle() computation performed on geom layer
			// (we dont need to convert to view first)
			if (connectorNode.getConnectorSpecification().getStartSymbol() != StartSymbolType.NONE) {
				FGESegment firstSegment = polylin.getSegments().firstElement();
				if (firstSegment != null) {
					g.drawSymbol(firstSegment.getP1(), connectorNode.getConnectorSpecification().getStartSymbol(), connectorNode
							.getConnectorSpecification().getStartSymbolSize(), firstSegment.getAngle());
				}
			}
			if (connectorNode.getConnectorSpecification().getEndSymbol() != EndSymbolType.NONE) {
				FGESegment lastSegment = polylin.getSegments().lastElement();
				if (lastSegment != null) {
					g.drawSymbol(lastSegment.getP2(), connectorNode.getConnectorSpecification().getEndSymbol(), connectorNode
							.getConnectorSpecification().getEndSymbolSize(), lastSegment.getAngle() + Math.PI);
				}
			}
			if (connectorNode.getConnectorSpecification().getMiddleSymbol() != MiddleSymbolType.NONE) {
				g.drawSymbol(getMiddleSymbolLocation(), connectorNode.getConnectorSpecification().getMiddleSymbol(), connectorNode
						.getConnectorSpecification().getMiddleSymbolSize(), getMiddleSymbolAngle());
			}
		}

	}

	@Override
	public FGEPoint getMiddleSymbolLocation() {
		if (polylin == null) {
			return new FGEPoint(0, 0);
		}
		AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);

		FGERectPolylin transformedPolylin = polylin.transform(at);
		FGEPoint point = transformedPolylin.getPointAtRelativePosition(connectorNode.getConnectorSpecification()
				.getRelativeMiddleSymbolLocation());
		try {
			point = point.transform(at.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		if (!getConnectorSpecification().getIsRounded()) {
			return point;
		} else {
			UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
			FGEPoint returned = polylin.getNearestPointLocatedOnRoundedRepresentation(point, arcSize.arcWidth, arcSize.arcHeight);
			if (returned == null) {
				return new FGEPoint(0, 0);
			} else {
				return returned;
			}
		}
	}

	/**
	 * 
	 * @return angle expressed in radians
	 */
	public double getMiddleSymbolAngle() {
		if (polylin == null) {
			return 0;
		}

		FGEPoint middleSymbolLocation = getMiddleSymbolLocation();
		FGESegment relatedSegment = polylin.getNearestSegment(middleSymbolLocation);

		if (relatedSegment == null) {
			return 0;
		}
		if (!getConnectorSpecification().getIsRounded()) {
			return relatedSegment.getAngle() + Math.PI;
		}

		else {
			UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
			FGEArc arc = polylin.getArcForNearestPointLocatedOnRoundedRepresentation(middleSymbolLocation, arcSize.arcWidth,
					arcSize.arcHeight);
			if (arc != null) {
				double angle = arc.angleForPoint(middleSymbolLocation) + Math.PI / 2;
				if (arc.isClockWise()) {
					angle += Math.PI;
				}
				return angle;
			} else {
				return relatedSegment.getAngle() + Math.PI;
			}
		}

	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Skipping refreshConnector() for " + connectorNode);
			}
			return;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform refreshConnector() for " + connectorNode);
			}
		}

		updateLayout();

		if (connectorNode.getConnectorSpecification().getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

		super.refreshConnector(force);

		firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		if (!firstUpdated) {
			return true;
		}
		if (polylin == null) {
			return true;
		}
		return super.needsRefresh();
	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		double returned = Double.POSITIVE_INFINITY;

		if (polylin == null) {
			return Double.POSITIVE_INFINITY;
		}

		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);

		for (FGESegment s : polylin.getSegments()) {
			Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(s.getP1(), scale);
			Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(s.getP2(), scale);
			double distanceToCurrentSegment = Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
			if (distanceToCurrentSegment < returned) {
				returned = distanceToCurrentSegment;
			}
		}
		return returned;

	}

	public double getOverlapXResultingFromPixelOverlap() {
		// Compute relative overlap along X-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(getConnectorSpecification().getPixelOverlap(), 0);
		FGEPoint overlap_pp1 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		FGEPoint overlap_pp2 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.x - overlap_pp2.x);
	}

	public double getOverlapYResultingFromPixelOverlap() {
		// Compute relative overlap along Y-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(0, getConnectorSpecification().getPixelOverlap());
		FGEPoint overlap_pp1 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		FGEPoint overlap_pp2 = connectorNode.convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.y - overlap_pp2.y);
	}

	@Override
	public FGERectangle getConnectorUsedBounds() {
		// logger.info("Called getConnectorUsedBounds()");
		if (polylin != null) {
			FGERectangle minimalBounds = polylin.getBoundingBox();
			FGERectangle returned = new FGERectangle(Filling.FILLED);

			// Compute required space to draw symbols, eg arrows
			double maxSymbolSize = Math.max(connectorNode.getConnectorSpecification().getStartSymbolSize(), Math.max(connectorNode
					.getConnectorSpecification().getMiddleSymbolSize(), connectorNode.getConnectorSpecification().getEndSymbolSize()));
			double relativeWidthToAdd = maxSymbolSize * 2 / connectorNode.getViewWidth(1.0);
			double relativeHeightToAdd = maxSymbolSize * 2 / connectorNode.getViewHeight(1.0);

			// Add space to draw symbols, eg arrows
			returned.x = minimalBounds.x - relativeWidthToAdd * minimalBounds.width;
			returned.y = minimalBounds.y - relativeHeightToAdd * minimalBounds.height;
			returned.width = (1 + 2 * relativeWidthToAdd) * minimalBounds.width;
			returned.height = (1 + 2 * relativeHeightToAdd) * minimalBounds.height;
			// logger.info("Called getConnectorUsedBounds() return "+returned);
			return returned;
		}
		return NORMALIZED_BOUNDS;
	}

	@Override
	public void connectorWillBeModified() {
		super.connectorWillBeModified();
		if (polylin != null) {
			lastKnownCleanPolylinBeforeConnectorRestructuration = polylin.clone();
		}
	}

	@Override
	public void connectorHasBeenModified() {
		super.connectorHasBeenModified();
		lastKnownCleanPolylinBeforeConnectorRestructuration = null;
		_connectorChanged(false);
	}

	public FGERectPolylin getCurrentPolylin() {
		return polylin;
	}

	// Used for serialization only
	public FGERectPolylin _getPolylin() {
		if (getConnectorSpecification().getAdjustability() != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
			return null;
		}
		return polylin;
	}

	// Used for serialization only
	public void _setPolylin(FGERectPolylin aPolylin) {
		if (aPolylin != null && aPolylin.getPointsNb() > 0) {
			_deserializedPolylin = aPolylin;
			wasManuallyAdjusted = true;
		}
	}

	public void manuallySetPolylin(FGERectPolylin aPolylin) {
		updateWithNewPolylin(aPolylin);
	}

	private ConnectorAdjustingControlPoint updateMiddleSymbolLocationControlPoint() {
		if (middleSymbolLocationControlPoint == null) {
			middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(connectorNode, getMiddleSymbolLocation()) {
				@Override
				public Cursor getDraggingCursor() {
					/*
					 * SimplifiedCardinalDirection orientation =
					 * polylin.getNearestSegment(getMiddleSymbolLocation()).getApproximatedOrientation(); if (orientation != null) { if
					 * (orientation.isHorizontal()) { return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); } if
					 * (orientation.isVertical()) { return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); } }
					 */
					// return FGEConstants.MOVE_CURSOR;
					return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
				}

				@Override
				public FGEArea getDraggingAuthorizedArea() {
					return polylin;
				}

				@Override
				public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration,
						FGEPoint newAbsolutePoint, FGEPoint initialPoint, MouseEvent event) {
					if (polylin == null) {
						logger.warning("polylin is null");
						return false;
					}
					// logger.info("OK, moving to "+point);dds
					UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
					FGEPoint pt = polylin.getNearestPointLocatedOnRoundedRepresentation(newRelativePoint, arcSize.arcWidth,
							arcSize.arcHeight);

					// FGEPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);
					pt = pt.transform(at);
					FGERectPolylin transformedPolylin = polylin.transform(at);

					// FGESegment segment = new FGESegment(cp1.getPoint(),cp2.getPoint());
					connectorNode.getConnectorSpecification().setRelativeMiddleSymbolLocation(transformedPolylin.getRelativeLocation(pt));

					/*
					 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
					 * getStartObject());
					 */
					connectorNode.notifyConnectorModified();
					return true;
				}

			};
		}
		middleSymbolLocationControlPoint.setPoint(getMiddleSymbolLocation());
		return middleSymbolLocationControlPoint;
	}

	private ConnectorAdjustingControlPoint middleSymbolLocationControlPoint;

	private class UnnormalizedArcSize {
		double arcWidth;
		double arcHeight;
	}

	private UnnormalizedArcSize computeUnnormalizedArcSize() {
		UnnormalizedArcSize returned = new UnnormalizedArcSize();
		FGEPoint arcP1 = connectorNode.convertViewCoordinatesToNormalizedPoint(new Point(0, 0), 1.0);
		FGEPoint arcP2 = connectorNode.convertViewCoordinatesToNormalizedPoint(new Point(getConnectorSpecification().getArcSize(),
				getConnectorSpecification().getArcSize()), 1.0);
		returned.arcWidth = arcP2.x - arcP1.x;
		returned.arcHeight = arcP2.y - arcP1.y;
		return returned;
	}

	@Override
	public double getStartAngle() {
		if (polylin == null) {
			return 0;
		}
		return polylin.getFirstSegment().getAngle();
	}

	@Override
	public double getEndAngle() {
		if (polylin == null) {
			return -Math.PI;
		}
		return polylin.getLastSegment().getAngle();
	}

	public ControlPoint getEndControlPoint() {
		return p_end;
	}

	public ControlPoint getStartControlPoint() {
		return p_start;
	}

	public FGEPoint getCrossedControlPointOnRoundedArc() {
		if (getConnectorSpecification().getCrossedControlPoint() != null) {
			if (getConnectorSpecification().getIsRounded()) {
				UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
				return polylin.getNearestPointLocatedOnRoundedRepresentation(getConnectorSpecification().getCrossedControlPoint(),
						arcSize.arcWidth, arcSize.arcWidth);
			} else {
				return getConnectorSpecification().getCrossedControlPoint();
			}
		}
		return null;
	}

	public Vector<SimplifiedCardinalDirection> getAllowedStartOrientations() {
		Vector<SimplifiedCardinalDirection> returned = getPrimitiveAllowedStartOrientations();
		if (getConnectorSpecification().getIsStartingLocationFixed() && getConnectorSpecification().getFixedStartLocation() != null) {
			Vector<SimplifiedCardinalDirection> newConstraints = SimplifiedCardinalDirection.intersection(returned,
					_getAllowedStartOrientationsDueToFixedStartingLocation());
			if (newConstraints.size() == 0) {
				logger.warning("Cannot respect fixed start location orientation constraint primitives=" + returned
						+ " for fixed starting position=" + _getAllowedStartOrientationsDueToFixedStartingLocation());
				return returned;
			} else {
				return newConstraints;
			}
		}
		return returned;
	}

	private Vector<SimplifiedCardinalDirection> _getAllowedStartOrientationsDueToFixedStartingLocation() {
		Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
		FGEArea startArea = getStartNode().getAllowedStartAreaForConnector(connectorNode);
		// startArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (startArea.getAnchorAreaFrom(o).containsPoint(getConnectorSpecification().getFixedStartLocation())) {
				returned.add(o);
				// System.out.println("CHOOSEN: "+(startArea.getAnchorAreaFrom(o).containsPoint(getFixedStartLocation()))+" Orientation: "+o+" startArea.getAnchorAreaFrom(o)="+startArea.getAnchorAreaFrom(o)+" location="+getFixedStartLocation());
				/*
				 * if (startArea.getAnchorAreaFrom(o) instanceof FGEArc) { FGEArc arc = (FGEArc)startArea.getAnchorAreaFrom(o); double angle
				 * = arc.angleForPoint(getFixedStartLocation()); FGEPoint p = arc.getPointAtRadianAngle(angle);
				 * System.out.println("Point "+getFixedStartLocation
				 * ()+" on arc="+arc.containsPoint(getFixedStartLocation())+" angle="+angle+
				 * " otherPoint="+p+" on arc="+arc.containsPoint(p)); }
				 */
				// if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedStartLocation())) returned.add(o);
			}
		}
		// logger.info("Allowed start orientations due to fixed starting location = "+returned);
		if (returned.size() == 0) {
			logger.warning("Allowed start orientations due to fixed starting location returned an empty vector " + returned);
		}
		return returned;
	}

	/**
	 * Return all allowed start orientation as this is defined in orientation constraint Does NOT take under account the fact that starting
	 * position could have been fixed and can also induced an other start orientation.
	 * 
	 * @return
	 */
	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedStartOrientations() {
		switch (getConnectorSpecification().getRectPolylinConstraints()) {
		case NONE:
			return SimplifiedCardinalDirection.allDirections();
		case START_ORIENTATION_FIXED:
			if (getConnectorSpecification().getStartOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getConnectorSpecification().getStartOrientation());
		case ORIENTATIONS_FIXED:
			if (getConnectorSpecification().getStartOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getConnectorSpecification().getStartOrientation());
		case HORIZONTAL_LAYOUT:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
		case ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
		case VERTICAL_LAYOUT:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);
		case ORTHOGONAL_LAYOUT_VERTICAL_FIRST:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);

		default:
			return SimplifiedCardinalDirection.allDirections();
		}
	}

	public Vector<SimplifiedCardinalDirection> getExcludedStartOrientations() {
		return SimplifiedCardinalDirection.allDirectionsExcept(getAllowedStartOrientations());
	}

	public Vector<SimplifiedCardinalDirection> getAllowedEndOrientations() {
		Vector<SimplifiedCardinalDirection> returned = getPrimitiveAllowedEndOrientations();
		if (getConnectorSpecification().getIsEndingLocationFixed() && getConnectorSpecification().getFixedEndLocation() != null) {
			Vector<SimplifiedCardinalDirection> newConstraints = SimplifiedCardinalDirection.intersection(returned,
					_getAllowedEndOrientationsDueToFixedEndingLocation());
			if (newConstraints.size() == 0) {
				logger.warning("Cannot respect fixed end location orientation constraint primitives=" + returned
						+ " for fixed ending position=" + _getAllowedEndOrientationsDueToFixedEndingLocation());
				return returned;
			} else {
				return newConstraints;
			}
		}
		return returned;
	}

	private Vector<SimplifiedCardinalDirection> _getAllowedEndOrientationsDueToFixedEndingLocation() {
		Vector<SimplifiedCardinalDirection> returned = new Vector<SimplifiedCardinalDirection>();
		FGEArea endArea = getEndNode().getAllowedEndAreaForConnector(connectorNode);
		// endArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			// if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedEndLocation())) returned.add(o);
			if (endArea.getAnchorAreaFrom(o).containsPoint(getConnectorSpecification().getFixedEndLocation())) {
				returned.add(o);
				// System.out.println("CHOOSEN: "+(endArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedEndLocation()))+" Orientation: "+o+" endArea.getOrthogonalPerspectiveArea(o)="+endArea.getOrthogonalPerspectiveArea(o));
			}
		}
		// logger.info("Allowed end orientations due to fixed ending location="+returned);
		if (returned.size() == 0) {
			logger.warning("Allowed start orientations due to fixed starting location returned an empty vector " + returned);
		}
		return returned;
	}

	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedEndOrientations() {
		switch (getConnectorSpecification().getRectPolylinConstraints()) {
		case NONE:
			return SimplifiedCardinalDirection.allDirections();
		case END_ORIENTATION_FIXED:
			if (getConnectorSpecification().getEndOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getConnectorSpecification().getEndOrientation());
		case ORIENTATIONS_FIXED:
			if (getConnectorSpecification().getEndOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getConnectorSpecification().getEndOrientation());
		case HORIZONTAL_LAYOUT:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
		case ORTHOGONAL_LAYOUT_VERTICAL_FIRST:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.EAST, SimplifiedCardinalDirection.WEST);
		case VERTICAL_LAYOUT:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);
		case ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST:
			return SimplifiedCardinalDirection.someDirections(SimplifiedCardinalDirection.NORTH, SimplifiedCardinalDirection.SOUTH);

		default:
			return SimplifiedCardinalDirection.allDirections();
		}
	}

	public Vector<SimplifiedCardinalDirection> getExcludedEndOrientations() {
		return SimplifiedCardinalDirection.allDirectionsExcept(getAllowedEndOrientations());
	}

	// *******************************************************************************
	// * Internal A.P.I. for connector computation *
	// *******************************************************************************

	/**
	 * This is the general method used for connector updating Calling this method is generally safe regarding internal structure
	 */
	public void updateLayout() {
		if (connectorNode.getGraphicalRepresentation() == null) {
			return;
		}

		/*if (!connectorNode.getGraphicalRepresentation().isRegistered()) {
			return;
		}*/

		if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.AUTO_LAYOUT
				|| getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE && !getWasManuallyAdjusted()) {

			_updateAsAutoLayout();

		}

		else if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {

			if (polylin == null) {

				if (_deserializedPolylin != null) {
					// Rebuild from deserialized polylin
					updateWithNewPolylin(new FGERectPolylin(_deserializedPolylin.getPoints(), getConnectorSpecification()
							.getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap()));
					_deserializedPolylin = null;
				}

				else {
					// Was never computed, do it now
					_updateAsAutoLayout();
				}
			}

			_updateAsBasicallyAdjustable();

		}

		else /* RectPolylinConnectorSpecification is adjustable, getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE */{

			_updateAsFullyAdjustable();

			/*
			 * if (polylin == null) {
			 * 
			 * if (_deserializedPolylin != null) { // Rebuild from deserialized polylin updateWithNewPolylin(new
			 * FGERectPolylin(_deserializedPolylin
			 * .getPoints(),getStraightLineWhenPossible(),getOverlapXResultingFromPixelOverlap(),getOverlapYResultingFromPixelOverlap()));
			 * _deserializedPolylin = null; }
			 * 
			 * else { // Was never computed, do it now _updateAsAutoLayout(); } }
			 * 
			 * // Attempt to restore some locations from stored locations relative to start and end object // - start point is restored from
			 * relative location to start object and put on starting object outline // - end point is restored from relative location to end
			 * object and put on ending object outline // - first point is also restored from relative location to start object if there are
			 * more than 5 points // - next point is updated accordingly to orientation of second segment // - last point is also restored
			 * from relative location to end object if there are more than 5 points // - previous point is updated accordingly to
			 * orientation of previous-last segment
			 * 
			 * 
			 * int indexOfMiddleSegment = polylin.getSegments().indexOf(polylin.getMiddleSegment());
			 * 
			 * for (int i=0; i<polylin.getPointsNb(); i++) { if (i<=indexOfMiddleSegment && polylinRelativeToStartObject != null) { // That
			 * point is closest to start object // remember location stored relative to start object FGEPoint pointRelativeToStartObject =
			 * polylinRelativeToStartObject.getPointAt(i); if (i==0) { // This is the start object, when not, put it on starting object
			 * shape outline pointRelativeToStartObject =
			 * getStartObject().getShape().getOutline().nearestOutlinePoint(pointRelativeToStartObject); polylin.updatePointAt(i,
			 * GraphicalRepresentation.convertNormalizedPoint( getStartObject(), pointRelativeToStartObject, getGraphicalRepresentation()));
			 * } else if (i==1 && polylin.getPointsNb() >=6) { FGEPoint firstPoint = GraphicalRepresentation.convertNormalizedPoint(
			 * getStartObject(), pointRelativeToStartObject, getGraphicalRepresentation()); FGEPoint nextPoint = polylin.getPointAt(2); if
			 * (polylinRelativeToStartObject.getSegmentAt(1).getApproximatedOrientation().isHorizontal()) { nextPoint.y = firstPoint.y; }
			 * else { nextPoint.x = firstPoint.x; } polylin.updatePointAt(i,firstPoint); polylin.updatePointAt(i+1,nextPoint); } } else if
			 * (polylinRelativeToEndObject != null) { // That point is closest to end object // remember location stored relative to end
			 * object FGEPoint pointRelativeToEndObject = polylinRelativeToEndObject.getPointAt(i); if (i==polylin.getPointsNb()-1) { //
			 * This is the end object, when not, put it on ending object shape outline pointRelativeToEndObject =
			 * getEndObject().getShape().getOutline().nearestOutlinePoint(pointRelativeToEndObject); polylin.updatePointAt(i,
			 * GraphicalRepresentation.convertNormalizedPoint( getEndObject(), pointRelativeToEndObject, getGraphicalRepresentation())); }
			 * else if (i==polylin.getPointsNb()-2 && polylin.getPointsNb() >=6) { FGEPoint lastPoint =
			 * GraphicalRepresentation.convertNormalizedPoint( getEndObject(), pointRelativeToEndObject, getGraphicalRepresentation());
			 * FGEPoint previousPoint = polylin.getPointAt(polylin.getPointsNb()-3); if
			 * (polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject
			 * .getSegmentNb()-2).getApproximatedOrientation().isHorizontal()) { previousPoint.y = previousPoint.y; } else { previousPoint.x
			 * = previousPoint.x; } polylin.updatePointAt(i,lastPoint); polylin.updatePointAt(i-1,previousPoint); } } }
			 * 
			 * updateAndNormalizeCurrentPolylin();
			 */
		}

	}

	/**
	 * Compute and return start area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of start area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return FGEArea
	 */
	public FGEArea retrieveStartArea() {
		FGEArea startArea = retrieveAllowedStartArea(true);

		if (getConnectorSpecification().getIsStartingLocationFixed() && getConnectorSpecification().getFixedStartLocation() != null) {
			FGEPoint fixedPoint = FGEUtils.convertNormalizedPoint(getStartNode(), getConnectorSpecification().getFixedStartLocation(),
					connectorNode);
			/*
			 * if (startArea instanceof FGEShape) { return ((FGEShape<?>)startArea).nearestOutlinePoint(fixedPoint); } else
			 */FGEPoint returned = startArea.getNearestPoint(fixedPoint);
			if (!startArea.containsPoint(returned)) {
				logger.warning("Inconsistent data: point " + returned + " not located on area: " + startArea + " [was: " + fixedPoint + "]");
			}
			return returned;

		}

		return startArea;

		/*
		 * AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getStartObject(), getGraphicalRepresentation());
		 * 
		 * FGEArea startArea = getStartObject().getShape().getShape().transform(at1); if (startArea instanceof FGEShape) {
		 * ((FGEShape<?>)startArea).setIsFilled(false); }
		 * 
		 * Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		 * 
		 * if (getIsStartingLocationFixed() && getFixedStartLocation() != null) { if (startArea instanceof FGEShape) { FGEPoint startPoint =
		 * ((FGEShape<?>)startArea).nearestOutlinePoint(GraphicalRepresentation.convertNormalizedPoint(getStartObject(),
		 * getFixedStartLocation(), getGraphicalRepresentation())); Vector<SimplifiedCardinalDirection>
		 * allowedStartOrientationsBecauseOfFixedPoint = new Vector<SimplifiedCardinalDirection>(); for (SimplifiedCardinalDirection o :
		 * SimplifiedCardinalDirection.values()) { if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(startPoint)) {
		 * allowedStartOrientationsBecauseOfFixedPoint.add(o); } } Vector<SimplifiedCardinalDirection> resultingAllowedOrientations =
		 * SimplifiedCardinalDirection.intersection(allowedStartOrientations, allowedStartOrientationsBecauseOfFixedPoint); if
		 * (resultingAllowedOrientations.size() > 0) allowedStartOrientations = resultingAllowedOrientations; // Otherwise, cannot respect
		 * this constraint, give up logger.warning("start area is a point !!!"); startArea = startPoint; } }
		 * 
		 * return startArea;
		 */
	}

	/**
	 * Compute and return allowed start area, in the connector coordinates system If some orientation constraints are defined, return
	 * portion of start area outline matching allowed orientations
	 * 
	 * @return FGEArea
	 */
	public FGEArea retrieveAllowedStartArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(getStartNode(), connectorNode);

		FGEArea startArea = getStartNode().getAllowedStartAreaForConnector(connectorNode).transform(at1);
		/*
		 * if (startArea instanceof FGEShape) { ((FGEShape<?>)startArea).setIsFilled(false); }
		 */

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = takeFixedControlPointUnderAccount ? getAllowedStartOrientations()
				: getPrimitiveAllowedStartOrientations();

		if (allowedStartOrientations.size() > 0 && allowedStartOrientations.size() < 4) {
			// Some directions should not be available
			Vector<FGEArea> allowedAreas = new Vector<FGEArea>();
			for (SimplifiedCardinalDirection o : allowedStartOrientations) {
				// allowedAreas.add(startArea.intersect(startArea.getOrthogonalPerspectiveArea(o)));
				allowedAreas.add(startArea.getAnchorAreaFrom(o));
				/*
				 * logger.info("Orientation: "+o); logger.info("startArea="+startArea);
				 * logger.info("ortho="+startArea.getOrthogonalPerspectiveArea(o));
				 * logger.info("result="+startArea.intersect(startArea.getOrthogonalPerspectiveArea(o)));
				 */
			}
			return FGEUnionArea.makeUnion(allowedAreas);
		} else if (allowedStartOrientations.size() == 0) {
			logger.warning("Cannot respect starting orientation constraints");
		}

		return startArea;

	}

	/**
	 * Compute and return end area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of end area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return FGEArea
	 */
	public FGEArea retrieveEndArea() {
		// System.out.println("retrieveAllowedEndArea()="+retrieveAllowedEndArea());

		FGEArea endArea = retrieveAllowedEndArea(true);

		if (getConnectorSpecification().getIsEndingLocationFixed() && getConnectorSpecification().getFixedEndLocation() != null) {
			FGEPoint fixedPoint = FGEUtils.convertNormalizedPoint(getEndNode(), getConnectorSpecification().getFixedEndLocation(),
					connectorNode);
			/*
			 * if (endArea instanceof FGEShape) { return ((FGEShape<?>)endArea).nearestOutlinePoint(fixedPoint); } else
			 */
			FGEPoint returned = endArea.getNearestPoint(fixedPoint);
			if (!endArea.containsPoint(returned)) {
				logger.warning("Inconsistent data: point " + returned + " not located on area: " + endArea + " [was: " + fixedPoint + "]");
			}
			return returned;
		}

		return endArea;

		/*
		 * AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getEndObject(), getGraphicalRepresentation());
		 * 
		 * FGEArea endArea = getEndObject().getShape().getShape().transform(at2); if (endArea instanceof FGEShape) {
		 * ((FGEShape<?>)endArea).setIsFilled(false); }
		 * 
		 * Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();
		 * 
		 * if (getIsEndingLocationFixed() && getFixedEndLocation() != null) { if (endArea instanceof FGEShape) { FGEPoint endPoint =
		 * ((FGEShape<?>)endArea).nearestOutlinePoint(GraphicalRepresentation.convertNormalizedPoint(getEndObject(), getFixedEndLocation(),
		 * getGraphicalRepresentation())); Vector<SimplifiedCardinalDirection> allowedEndOrientationsBecauseOfFixedPoint = new
		 * Vector<SimplifiedCardinalDirection>(); for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) { if
		 * (endArea.getOrthogonalPerspectiveArea(o).containsPoint(endPoint)) { allowedEndOrientationsBecauseOfFixedPoint.add(o); } }
		 * Vector<SimplifiedCardinalDirection> resultingAllowedOrientations =
		 * SimplifiedCardinalDirection.intersection(allowedEndOrientations, allowedEndOrientationsBecauseOfFixedPoint); if
		 * (resultingAllowedOrientations.size() > 0) allowedEndOrientations = resultingAllowedOrientations; // Otherwise, cannot respect
		 * this constraint, give up endArea = endPoint; } }
		 * 
		 * return endArea;
		 */
	}

	/**
	 * Compute and return allowed end area, in the connector coordinates system If some orientation constraints are defined, return portion
	 * of end area outline matching allowed orientations
	 * 
	 * @return FGEArea
	 */
	public FGEArea retrieveAllowedEndArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(getEndNode(), connectorNode);

		FGEArea endArea = getEndNode().getAllowedEndAreaForConnector(connectorNode).transform(at2);
		/*
		 * if (endArea instanceof FGEShape) { ((FGEShape<?>)endArea).setIsFilled(false); }
		 */

		Vector<SimplifiedCardinalDirection> allowedEndOrientations = takeFixedControlPointUnderAccount ? getAllowedEndOrientations()
				: getPrimitiveAllowedEndOrientations();

		if (allowedEndOrientations.size() > 0 && allowedEndOrientations.size() < 4) {
			// Some directions should not be available
			Vector<FGEArea> allowedAreas = new Vector<FGEArea>();
			for (SimplifiedCardinalDirection o : allowedEndOrientations) {
				// allowedAreas.add(endArea.intersect(endArea.getOrthogonalPerspectiveArea(o)));
				allowedAreas.add(endArea.getAnchorAreaFrom(o));
				// System.out.println("Orientation: "+o+" ortho: "+endArea.getOrthogonalPerspectiveArea(o)+" intersect="+endArea.intersect(endArea.getOrthogonalPerspectiveArea(o)));
			}
			return FGEUnionArea.makeUnion(allowedAreas);
		} else if (allowedEndOrientations.size() == 0) {
			logger.warning("Cannot respect ending orientation constraints");
		}

		return endArea;
	}

	/**
	 * Internal method called to update connector asserting layout is automatically performed Whatever happen, compute the shortest polylin
	 * respecting start and end constraints (positions and/or orientations) This new polylin is automatically recomputed and set to current
	 * connector.
	 * 
	 */
	private void _updateAsAutoLayout() {
		final FGEArea startArea = retrieveStartArea();
		final FGEArea endArea = retrieveEndArea();

		// logger.info("startArea="+startArea);
		// logger.info("endArea="+endArea);

		FGEPoint startMiddle = null;

		if (startArea.isFinite()) {
			FGERectangle startAreaBounds = startArea.getEmbeddingBounds();
			if (startAreaBounds != null) {
				startMiddle = startAreaBounds.getCenter();
			}
		}
		if (startMiddle == null) {
			logger.warning("Could not find middle of resulting start area: " + startArea);
			startMiddle = new FGEPoint(0, 0);
		}

		FGEPoint endMiddle = null;

		if (endArea.isFinite()) {
			FGERectangle endAreaBounds = endArea.getEmbeddingBounds();
			if (endAreaBounds != null) {
				endMiddle = endAreaBounds.getCenter();
			}
		}
		if (endMiddle == null) {
			logger.warning("Could not find middle of resulting start area: " + startArea);
			endMiddle = new FGEPoint(1, 1);
		}

		// First obtain the two affine transform allowing to convert from
		// extremity objects coordinates to connector drawable

		/*
		 * AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getStartObject(), getGraphicalRepresentation());
		 * 
		 * AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT( getEndObject(), getGraphicalRepresentation());
		 * 
		 * FGEPoint startMiddle = getStartObject().getShape().getShape().getCenter().transform(at1); FGEPoint endMiddle =
		 * getEndObject().getShape().getShape().getCenter().transform(at2);
		 */

		Vector<SimplifiedCardinalDirection> potentialStartOrientations = new Vector<SimplifiedCardinalDirection>();
		Vector<SimplifiedCardinalDirection> potentialEndOrientations = new Vector<SimplifiedCardinalDirection>();

		boolean testAllCombinations = true;

		// logger.info("getRectPolylinConstraints()="+getRectPolylinConstraints()+" getEndOrientation()="+getEndOrientation());

		if (getConnectorSpecification().getRectPolylinConstraints() == RectPolylinConstraints.ORIENTATIONS_FIXED) {
			potentialStartOrientations.add(getConnectorSpecification().getStartOrientation());
			potentialEndOrientations.add(getConnectorSpecification().getEndOrientation());
		}

		else {
			CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(startMiddle, endMiddle);

			RectPolylinConstraints constraints = getConnectorSpecification().getRectPolylinConstraints();
			if (constraints == RectPolylinConstraints.START_ORIENTATION_FIXED
					|| constraints == RectPolylinConstraints.END_ORIENTATION_FIXED) {
				constraints = RectPolylinConstraints.NONE;
			}

			if (constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT) {
				testAllCombinations = false;
			}
			if (constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
				testAllCombinations = false;
			}

			if (quadrant == CardinalQuadrant.NORTH_EAST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					}
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
				}
			} else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					}
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
				}
			} else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					}
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
				}
			} else /* if (quadrant == CardinalQuadrant.NORTH_WEST) */{
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST
						|| constraints == RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
						potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					}
					if (constraints != RectPolylinConstraints.ORTHOGONAL_LAYOUT_VERTICAL_FIRST) {
						potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
						potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					}
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.HORIZONTAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.WEST);
					potentialStartOrientations.add(SimplifiedCardinalDirection.EAST);
					potentialEndOrientations.add(SimplifiedCardinalDirection.EAST);
				}
				if (constraints == RectPolylinConstraints.NONE || constraints == RectPolylinConstraints.VERTICAL_LAYOUT
						|| constraints == RectPolylinConstraints.HORIZONTAL_OR_VERTICAL_LAYOUT) {
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.NORTH);
					potentialStartOrientations.add(SimplifiedCardinalDirection.SOUTH);
					potentialEndOrientations.add(SimplifiedCardinalDirection.SOUTH);
				}
			}

			if (getConnectorSpecification().getRectPolylinConstraints() == RectPolylinConstraints.START_ORIENTATION_FIXED) {
				potentialStartOrientations.clear();
				potentialStartOrientations.add(getConnectorSpecification().getStartOrientation());

			}

			if (getConnectorSpecification().getRectPolylinConstraints() == RectPolylinConstraints.END_ORIENTATION_FIXED) {
				potentialEndOrientations.clear();
				potentialEndOrientations.add(getConnectorSpecification().getEndOrientation());
			}
		}

		/*
		 * FGEArea startArea = getStartObject().getShape().getShape().transform(at1); FGEArea endArea =
		 * getEndObject().getShape().getShape().transform(at2);
		 */

		potentialPolylin.clear();
		double minimalLength = Double.POSITIVE_INFINITY;
		SimplifiedCardinalDirection choosenStartOrientation = null;
		SimplifiedCardinalDirection choosenEndOrientation = null;

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		if (!testAllCombinations && potentialStartOrientations.size() != potentialEndOrientations.size()) {
			logger.warning("Inconsistent data: potentialStartOrientations.size() != potentialEndOrientations.size()");
		}

		if (testAllCombinations) {
			// Remove doublons
			Vector<SimplifiedCardinalDirection> newPotentialStartOrientations = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : potentialStartOrientations) {
				if (!newPotentialStartOrientations.contains(o)) {
					newPotentialStartOrientations.add(o);
				}
			}
			potentialStartOrientations = newPotentialStartOrientations;
			Vector<SimplifiedCardinalDirection> newPotentialEndOrientations = new Vector<SimplifiedCardinalDirection>();
			for (SimplifiedCardinalDirection o : potentialEndOrientations) {
				if (!newPotentialEndOrientations.contains(o)) {
					newPotentialEndOrientations.add(o);
				}
			}
			potentialEndOrientations = newPotentialEndOrientations;
		}

		// !!! Faire des methodes pour ca !!!

		/*
		 * logger.info("Looking for best polylin"); logger.info("potentialStartOrientations="+potentialStartOrientations);
		 * logger.info("potentialEndOrientations="+potentialEndOrientations);
		 */

		if (!testAllCombinations && potentialStartOrientations.size() == potentialEndOrientations.size()) {
			for (int i = 0; i < potentialStartOrientations.size(); i++) {
				if (allowedStartOrientations.contains(potentialStartOrientations.get(i))
						&& allowedEndOrientations.contains(potentialEndOrientations.get(i))) {
					FGERectPolylin newPolylin = new FGERectPolylin(startArea, potentialStartOrientations.get(i), endArea,
							potentialEndOrientations.get(i), getConnectorSpecification().getStraightLineWhenPossible(),
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());
					potentialPolylin.add(newPolylin);
					if (newPolylin.getPointsNb() > 0 && newPolylin.getLength() < minimalLength + FGEGeometricObject.EPSILON /*
																															* Hysteresis to
																															* avoid
																															* blinking
																															*/) {
						polylin = newPolylin;
						minimalLength = newPolylin.getLength();
						choosenStartOrientation = potentialStartOrientations.get(i);
						choosenEndOrientation = potentialEndOrientations.get(i);
					}
				}
			}
		}

		else {
			for (SimplifiedCardinalDirection startOrientation : potentialStartOrientations) {
				if (allowedStartOrientations.contains(startOrientation)) {
					for (SimplifiedCardinalDirection endOrientation : potentialEndOrientations) {
						if (allowedEndOrientations.contains(endOrientation)) {
							FGERectPolylin newPolylin = new FGERectPolylin(startArea, startOrientation, endArea, endOrientation,
									getConnectorSpecification().getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
									getOverlapYResultingFromPixelOverlap());
							potentialPolylin.add(newPolylin);
							if (newPolylin.doesRespectAllConstraints()
									&& newPolylin.getLength() < minimalLength + FGEGeometricObject.EPSILON /*
																											* Hysteresis
																											* to
																											* avoid
																											* blinking
																											*/) {
								polylin = newPolylin;
								minimalLength = newPolylin.getLength();
								choosenStartOrientation = startOrientation;
								choosenEndOrientation = endOrientation;
							}
						}
					}
				}
			}
		}

		if (polylin != null) {

			getConnectorSpecification().setStartOrientation(polylin.getStartOrientation());
			getConnectorSpecification().setEndOrientation(polylin.getEndOrientation());
		} else {
			logger.warning("polylin=null !!!!!!");
		}

		// logger.info("Best polylin found from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

		if (getConnectorSpecification().getStartOrientation() != choosenStartOrientation) {
			logger.warning("Requested start orientation was: " + choosenStartOrientation + " but is finally: "
					+ getConnectorSpecification().getStartOrientation());
		}
		if (getConnectorSpecification().getEndOrientation() != choosenEndOrientation) {
			logger.warning("Requested end orientation was: " + choosenEndOrientation + " but is finally: "
					+ getConnectorSpecification().getEndOrientation());
		}

		// logger.info("Before update, polylin from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

		// updateWithNewPolylin(polylin,true);

		if (polylin.isNormalized()) {
			updateWithNewPolylin(polylin, true, false);
		} else {
			logger.warning("Result of auto-layout computing returned a non-normalized polylin. Please investigate");
			updateWithNewPolylin(polylin, false, false);
		}

		// logger.info("After update, polylin from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

	}

	/**
	 * Internal method called to update connector asserting layout is defined as BASICALLY_ADJUSTABLE. If crossedControlPoint is defined,
	 * compute the shortest polylin crossing this point and respecting start and end constraints (positions and/or orientations) If no
	 * crossedControlPoint is defined, compute the shortest polylin respecting start and end constraints (positions and/or orientations)
	 */
	private void _updateAsBasicallyAdjustable() {
		final FGEArea startArea = retrieveStartArea();
		final FGEArea endArea = retrieveEndArea();
		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		FGERectPolylin newPolylin;

		FGEAreaProvider<SimplifiedCardinalDirection> startAreaProvider = getConnectorSpecification().getIsStartingLocationFixed() ? new DefaultAreaProvider<SimplifiedCardinalDirection>(
				startArea) : new FGEAreaProvider<SimplifiedCardinalDirection>() {
			@Override
			public FGEArea getArea(SimplifiedCardinalDirection input) {
				return getStartNode().getAllowedStartAreaForConnectorForDirection(connectorNode, startArea, input);
			}
		};
		FGEAreaProvider<SimplifiedCardinalDirection> endAreaProvider = getConnectorSpecification().getIsEndingLocationFixed() ? new DefaultAreaProvider<SimplifiedCardinalDirection>(
				endArea) : new FGEAreaProvider<SimplifiedCardinalDirection>() {
			@Override
			public FGEArea getArea(SimplifiedCardinalDirection input) {
				return getEndNode().getAllowedEndAreaForConnectorForDirection(connectorNode, endArea, input);
			}
		};
		if (_crossedPoint != null) {

			// System.out.println("startArea="+startArea);
			// System.out.println("endArea="+endArea);
			newPolylin = FGERectPolylin.makeRectPolylinCrossingPoint(startAreaProvider, endAreaProvider, _crossedPoint, true,
					getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap(),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));
		} else {
			newPolylin = FGERectPolylin.makeShortestRectPolylin(startAreaProvider, endAreaProvider, getConnectorSpecification()
					.getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap(),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations), SimplifiedCardinalDirection
							.allDirectionsExcept(allowedEndOrientations));
		}

		if (newPolylin == null) {
			logger.warning("Obtained null polylin allowedStartOrientations=" + allowedStartOrientations);
			return;
		}

		if (newPolylin.isNormalized()) {
			updateWithNewPolylin(newPolylin, true, false);
		} else {
			logger.warning("Result of basically_adjustable layout computing returned a non-normalized polylin. Please investigate");
			updateWithNewPolylin(newPolylin, false, false);
		}

	}

	/**
	 * Internal method called to update connector asserting layout is defined as FULLY_ADJUSTABLE.
	 */
	private void _updateAsFullyAdjustable() {
		if (polylin == null) {

			if (_deserializedPolylin != null) {
				// Rebuild from deserialized polylin
				updateWithNewPolylin(new FGERectPolylin(_deserializedPolylin.getPoints(), getConnectorSpecification()
						.getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap()));
				_deserializedPolylin = null;
			}

			else {
				// Was never computed, do it now
				_updateAsAutoLayout();
			}
		}

		// Special case where there is a unique segment
		// Used when connector is restructuring (start and/or end shapes are moving or resizing)
		if (lastKnownCleanPolylinBeforeConnectorRestructuration != null
				&& lastKnownCleanPolylinBeforeConnectorRestructuration.getSegmentNb() == 1 && polylinRelativeToStartObject != null
				&& polylinRelativeToEndObject != null) {
			FGEPoint lastStartPoint = polylinRelativeToStartObject.getFirstPoint();
			FGEPoint lastEndPoint = polylinRelativeToEndObject.getLastPoint();
			lastStartPoint = FGEUtils.convertNormalizedPoint(getStartNode(), lastStartPoint, connectorNode);
			lastEndPoint = FGEUtils.convertNormalizedPoint(getEndNode(), lastEndPoint, connectorNode);
			FGEPoint pt = FGEPoint.getMiddlePoint(lastStartPoint, lastEndPoint);
			if (_updateAsFullyAdjustableForUniqueSegment(pt)) {
				return;
			}
		}

		// Special case where there is a unique segment
		// Used when connector beeing edited
		if (polylin != null && polylin.getSegmentNb() == 1) {
			FGEPoint lastStartPoint = polylin.getFirstPoint();
			FGEPoint lastEndPoint = polylin.getLastPoint();
			FGEPoint pt = FGEPoint.getMiddlePoint(lastStartPoint, lastEndPoint);
			if (_updateAsFullyAdjustableForUniqueSegment(pt)) {
				return;
			}
		}

		// Attempt to restore some locations from stored locations relative to start and end object
		// - start point is restored from relative location to start object and put on starting object outline
		// - end point is restored from relative location to end object and put on ending object outline
		// - first point is also restored from relative location to start object if there are more than 5 points
		// - next point is updated accordingly to orientation of second segment
		// - last point is also restored from relative location to end object if there are more than 5 points
		// - previous point is updated accordingly to orientation of previous-last segment

		int indexOfMiddleSegment = polylin.getSegments().indexOf(polylin.getMiddleSegment());

		for (int i = 0; i < polylin.getPointsNb(); i++) {
			if (i <= indexOfMiddleSegment && polylinRelativeToStartObject != null) {
				// That point is closest to start object
				// remember location stored relative to start object
				FGEPoint pointRelativeToStartObject = polylinRelativeToStartObject.getPointAt(i);
				if (pointRelativeToStartObject != null) {
					if (i == 0) {
						// This is the start object, when not, put it on starting object shape outline
						pointRelativeToStartObject = getStartNode().getAllowedStartAreaForConnector(connectorNode).getNearestPoint(
								pointRelativeToStartObject);
						polylin.updatePointAt(i, FGEUtils.convertNormalizedPoint(getStartNode(), pointRelativeToStartObject, connectorNode));
					} else if (i == 1 && polylin.getPointsNb() >= 6) {
						FGEPoint firstPoint = FGEUtils.convertNormalizedPoint(getStartNode(), pointRelativeToStartObject, connectorNode);
						FGEPoint nextPoint = polylin.getPointAt(2);
						if (polylinRelativeToStartObject.getSegmentAt(1) != null
								&& polylinRelativeToStartObject.getSegmentAt(1).getApproximatedOrientation().isHorizontal()) {
							nextPoint.y = firstPoint.y;
						} else {
							nextPoint.x = firstPoint.x;
						}
						polylin.updatePointAt(i, firstPoint);
						polylin.updatePointAt(i + 1, nextPoint);
					}
				}
			} else if (polylinRelativeToEndObject != null) {
				// That point is closest to end object
				// remember location stored relative to end object
				FGEPoint pointRelativeToEndObject = polylinRelativeToEndObject.getPointAt(i);
				if (pointRelativeToEndObject != null) {
					if (i == polylin.getPointsNb() - 1) {
						// This is the end object, when not, put it on ending object shape outline
						pointRelativeToEndObject = getEndNode().getAllowedEndAreaForConnector(connectorNode).getNearestPoint(
								pointRelativeToEndObject);
						polylin.updatePointAt(i, FGEUtils.convertNormalizedPoint(getEndNode(), pointRelativeToEndObject, connectorNode));
					} else if (i == polylin.getPointsNb() - 2 && polylin.getPointsNb() >= 6) {
						FGEPoint lastPoint = FGEUtils.convertNormalizedPoint(getEndNode(), pointRelativeToEndObject, connectorNode);
						FGEPoint previousPoint = polylin.getPointAt(polylin.getPointsNb() - 3);
						if (polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject.getSegmentNb() - 2) != null
								&& polylinRelativeToEndObject.getSegmentAt(polylinRelativeToEndObject.getSegmentNb() - 2)
										.getApproximatedOrientation().isHorizontal()) {
							previousPoint.y = previousPoint.y;
						} else {
							previousPoint.x = previousPoint.x;
						}
						polylin.updatePointAt(i, lastPoint);
						polylin.updatePointAt(i - 1, previousPoint);
					}
				}
			}
		}

		/*
		 * 
		 * FGEPoint newStartCPLocation = polylin.getFirstPoint(); //startCPRelativeToStartObject =
		 * getStartObject().getShape().outlineIntersect(startCPRelativeToStartObject); startCPRelativeToStartObject =
		 * getStartObject().getShape().getShape().getNearestPoint(startCPRelativeToStartObject); if (startCPRelativeToStartObject != null) {
		 * newStartCPLocation = GraphicalRepresentation.convertNormalizedPoint( getStartObject(), startCPRelativeToStartObject,
		 * getGraphicalRepresentation()); polylin.updatePointAt(0, newStartCPLocation); }
		 * 
		 * FGEPoint newEndCPLocation = polylin.getLastPoint(); //endCPRelativeToEndObject =
		 * getEndObject().getShape().outlineIntersect(endCPRelativeToEndObject); endCPRelativeToEndObject =
		 * getEndObject().getShape().getShape().getNearestPoint(endCPRelativeToEndObject); if (endCPRelativeToEndObject != null) {
		 * newEndCPLocation = GraphicalRepresentation.convertNormalizedPoint( getEndObject(), endCPRelativeToEndObject,
		 * getGraphicalRepresentation()); polylin.updatePointAt(polylin.getPointsNb()-1, newEndCPLocation); }
		 */

		updateAndNormalizeCurrentPolylin();

	}

	public void updateWithNewPolylin(FGERectPolylin aPolylin) {
		updateWithNewPolylin(aPolylin, false, false);
	}

	public void updateWithNewPolylin(FGERectPolylin aPolylin, boolean temporary) {
		updateWithNewPolylin(aPolylin, false, temporary);
	}

	public void updateWithNewPolylin(FGERectPolylin aPolylin, boolean assertLayoutIsValid, boolean temporary) {
		// logger.info("updateWithNewPolylin()");

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Update with polylin with " + aPolylin.getPointsNb() + " points");
		}

		if (aPolylin.hasExtraPoints()) {
			aPolylin.removeExtraPoints();
		}

		polylin = aPolylin;

		_rebuildControlPoints();

		_connectorChanged(temporary);

		if (!assertLayoutIsValid) {
			updateAndNormalizeCurrentPolylin();
		}
	}

	/**
	 * Internal method restoring valid layout given current polylin. Current polylin is used and adapted to new conditions (eg start and/or
	 * end object have been modified (moved or resized)
	 * 
	 * Normalize obtained polylin at the end of process
	 * 
	 */
	private void updateAndNormalizeCurrentPolylin() {
		/*if (!getGraphicalRepresentation().isRegistered()) {
			return;
		}*/

		if (isCleaningPolylin) {
			// Avoid infinite loop
			return;
		}

		if (logger.isLoggable(Level.FINER)) {
			logger.finer("updateAndNormalizeCurrentPolylin()");
		}

		isCleaningPolylin = true;

		try {

			// First, check and update start and end control points
			checkAndUpdateStartCP(lastKnownCleanPolylinBeforeConnectorRestructuration != null ? lastKnownCleanPolylinBeforeConnectorRestructuration
					: polylin);
			checkAndUpdateEndCP(lastKnownCleanPolylinBeforeConnectorRestructuration != null ? lastKnownCleanPolylinBeforeConnectorRestructuration
					: polylin);

			if (polylin.isNormalized()) {
				return;
			}

			if (polylin.isNormalizable()) {
				polylin.normalize();
				for (int i = 0; i < polylin.getPointsNb(); i++) {
					controlPoints.elementAt(i).setPoint(polylin.getPointAt(i));
				}
			}

			else {
				if (logger.isLoggable(Level.FINER)) {
					logger.finer("RectPolylin layout changed");
				}
				// Layout has changed, update with new normalized polylin
				updateWithNewPolylin(polylin.makeNormalizedRectPolylin(), false, true);
			}

		}

		finally {
			isCleaningPolylin = false;
		}

	}

	/**
	 * Internal method rebuilding control points
	 */
	private void _rebuildControlPoints() {
		controlPoints.clear();
		controlAreas.clear();

		if (polylin == null) {
			return;
		}

		int nPoints = polylin.getPoints().size();

		switch (getConnectorSpecification().getAdjustability()) {

		case AUTO_LAYOUT:
			for (int i = 0; i < nPoints; i++) {
				FGEPoint p = polylin.getPointAt(i);
				if (i == 0 && getConnectorSpecification().getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(p, this));
				} else if (i == nPoints - 1 && getConnectorSpecification().getIsEndingLocationDraggable()) {
					controlPoints.add(new AdjustableEndControlPoint(p, this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, p));
				}
			}
			break;

		case BASICALLY_ADJUSTABLE:
			for (int i = 0; i < nPoints; i++) {
				FGEPoint p = polylin.getPointAt(i);
				if (i == 0 && getConnectorSpecification().getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(p, this));
				} else if (i == nPoints - 1 && getConnectorSpecification().getIsEndingLocationDraggable()) {
					controlPoints.add(new AdjustableEndControlPoint(p, this));
				} else {
					controlPoints.add(new AdjustableIntermediateControlPoint(p, -1, this));
				}
			}
			controlAreas.add(new RectPolylinAdjustingArea(this));
			/*
			 * if (_crossedPoint != null) { if (crossedControlPoint == null) { crossedControlPoint = new
			 * ConnectorNonAdjustableControlPoint(getGraphicalRepresentation(),_crossedPoint); }
			 * crossedControlPoint.setPoint(_crossedPoint); } if (crossedControlPoint != null) controlAreas.add(crossedControlPoint);
			 */
			break;

		case FULLY_ADJUSTABLE:
			if (nPoints >= 2) {
				if (getConnectorSpecification().getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(polylin.getFirstPoint(), this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, polylin.getFirstPoint()));
				}
				if (nPoints == 3) {
					controlPoints.add(new AdjustableMiddleControlPoint(polylin.getPoints().elementAt(1), this));
				} else if (nPoints > 3) { // nPoints > 3
					controlPoints.add(new AdjustableFirstControlPoint(polylin.getPoints().elementAt(1), this));
					for (int i = 2; i < nPoints - 2; i++) {
						controlPoints.add(new AdjustableIntermediateControlPoint(polylin.getPoints().elementAt(i), i, this));
					}
					controlPoints.add(new AdjustableLastControlPoint(polylin.getPoints().elementAt(nPoints - 2), this));
				}
				if (getConnectorSpecification().getIsEndingLocationDraggable()) {
					controlPoints.add(new AdjustableEndControlPoint(polylin.getLastPoint(), this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(connectorNode, polylin.getLastPoint()));
				}
			}
			int nSegments = polylin.getSegments().size();
			if (nSegments < 1) {
				// Pathologic case
				logger.warning("Unexpected situation here");
			} else if (nSegments == 1) {
				if ((!getConnectorSpecification().getIsStartingLocationFixed() || getConnectorSpecification()
						.getIsStartingLocationDraggable())
						&& (!getConnectorSpecification().getIsEndingLocationFixed() || getConnectorSpecification()
								.getIsEndingLocationDraggable())) {
					controlAreas.add(new AdjustableUniqueSegment(polylin.getFirstSegment(), this));
				}
			} else {
				if (!getConnectorSpecification().getIsStartingLocationFixed()
						|| getConnectorSpecification().getIsStartingLocationDraggable()) {
					controlAreas.add(new AdjustableFirstSegment(polylin.getFirstSegment(), this));
				}
				for (int i = 1; i < nSegments - 1; i++) {
					FGESegment s = polylin.getSegmentAt(i);
					controlAreas.add(new AdjustableIntermediateSegment(s, this));
				}
				if (!getConnectorSpecification().getIsEndingLocationFixed() || getConnectorSpecification().getIsEndingLocationDraggable()) {
					controlAreas.add(new AdjustableLastSegment(polylin.getLastSegment(), this));
				}
			}
			break;

		default:
			break;
		}

	}

	/**
	 * Internal method generally called at the end of updating process Internally store polylin relative to start and end objects
	 * 
	 */
	public void _connectorChanged(boolean temporary) {
		if (/*getGraphicalRepresentation().isRegistered() &&*/!temporary) {

			if (polylin != null) {
				if (FGEUtils.areElementsConnectedInGraphicalHierarchy(connectorNode, getStartNode())) {
					AffineTransform at1 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode, getStartNode());
					polylinRelativeToStartObject = polylin.transform(at1);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted

				if (FGEUtils.areElementsConnectedInGraphicalHierarchy(connectorNode, getEndNode())) {
					AffineTransform at2 = FGEUtils.convertNormalizedCoordinatesAT(connectorNode, getEndNode());
					polylinRelativeToEndObject = polylin.transform(at2);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted
			}
		}

		if (controlPoints.size() > 0) {
			p_start = controlPoints.firstElement();
			p_end = controlPoints.lastElement();
		}

		if (connectorNode.getConnectorSpecification().getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

	}

	/**
	 * Internal method called to update connector asserting layout is defined as FULLY_ADJUSTABLE, and when the last known polylin was a
	 * single segment
	 */
	public boolean _updateAsFullyAdjustableForUniqueSegment(FGEPoint pt) {
		FGEArea startArea = retrieveAllowedStartArea(true);
		if (getConnectorSpecification().getIsStartingLocationFixed() && !getConnectorSpecification().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = retrieveStartArea();
		}
		FGEArea endArea = retrieveAllowedEndArea(true);
		if (getConnectorSpecification().getIsEndingLocationFixed() && !getConnectorSpecification().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = retrieveEndArea();
		}

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		SimplifiedCardinalDirection orientation = null;
		FGEPoint newPt = null;

		for (SimplifiedCardinalDirection o1 : allowedStartOrientations) {
			for (SimplifiedCardinalDirection o2 : allowedEndOrientations) {
				if (o1 == o2.getOpposite()) {
					FGEArea startOrthogonalPerspectiveArea = startArea.getOrthogonalPerspectiveArea(o1);
					FGEArea endOrthogonalPerspectiveArea = endArea.getOrthogonalPerspectiveArea(o2);
					FGEArea intersectArea = startOrthogonalPerspectiveArea.intersect(endOrthogonalPerspectiveArea);
					FGEPoint aPt = intersectArea.getNearestPoint(pt);
					if (aPt != null) {
						orientation = o1;
						newPt = aPt;
						// System.out.println("orientation:"+orientation+" intersectArea="+intersectArea);
					}
				}
			}
		}

		if (orientation != null) {
			FGEPoint p1 = startArea.nearestPointFrom(newPt, orientation.getOpposite());
			FGEPoint p2 = endArea.nearestPointFrom(newPt, orientation);
			updateWithNewPolylin(new FGERectPolylin(p1, p2), true, false);
			// System.out.println("Found orientation "+orientation+" p1="+p1+" p2="+p2);
			return true;
		}

		// Could not find a layout with straight segment
		return false;
	}

	/**
	 * This method is internally called while updating starting point of polylin.
	 * 
	 * Calling this method assert that we are not sure anymore that start control point is still valid. When not, change this point to a
	 * valid location, and update polylin accordingly (eventually recompute new layout when required: modified orientation).
	 * 
	 * @param initialPolylin
	 *            Polylin to take under account to recreate new layout
	 * 
	 */
	public void checkAndUpdateStartCP(FGERectPolylin initialPolylin) {
		if (p_start == null) {
			_connectorChanged(true);
		}
		if (p_start == null) {
			return;
		}

		if (!FGEUtils.areElementsConnectedInGraphicalHierarchy(getStartNode(), connectorNode)) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}

		// Retrieve start area in connector coordinates system
		FGEArea startArea = retrieveStartArea();
		FGEArea allowedStartArea = retrieveAllowedStartArea(true);

		if (getConnectorSpecification().getIsStartingLocationFixed() && getConnectorSpecification().getFixedStartLocation() != null
				&& !getConnectorSpecification().getIsStartingLocationDraggable()) {
			allowedStartArea = getConnectorSpecification().getFixedStartLocation();
		}

		if (polylin.getPointsNb() == 0) {
			return;
		}

		// Retrieve control point location to update
		FGEPoint startCPLocation = polylin.getPointAt(0);
		if (startCPLocation == null) {
			return;
		}

		// Compute new location by computing nearest point of oldLocation
		// in end area (if this location was valid, change nothing)
		// FGEPoint oldCP = startCPLocation.clone();
		startCPLocation = startArea.getNearestPoint(startCPLocation);
		// logger.info("checkAndUpdateStartCP() from "+oldCP+" to "+startCPLocation);

		// Update polylin and end control point with this new location
		polylin.updatePointAt(0, startCPLocation);

		p_start.setPoint(startCPLocation);

		if (getConnectorSpecification().getIsStartingLocationFixed()) { // Don't forget this !!!
			setFixedStartLocation(FGEUtils.convertNormalizedPoint(connectorNode, startCPLocation, getStartNode()));
		}

		// Update for start cp
		SimplifiedCardinalDirection orientation = polylin.getOrientationOfSegment(0);

		if (orientation != null) {
			// This new location is valid
			// (the last segment is still horizontal or vertical)
			return;
		}

		else {
			// Start control point has moved (the first segment is not horizontal nor vertical anymore)

			// Find new orientation by minimizing distance between
			// current start point location and the nearest point of
			// all anchor location of all possible directions
			SimplifiedCardinalDirection newOrientation = null;
			double bestDistance = Double.POSITIVE_INFINITY;
			for (SimplifiedCardinalDirection o : getAllowedStartOrientations()) {
				double distance = FGEPoint.distance(startCPLocation, startArea.getAnchorAreaFrom(o).getNearestPoint(startCPLocation));
				if (distance < bestDistance) {
					newOrientation = o;
					bestDistance = distance;
				}
			}

			// debugPolylin = null;

			// Retrieve next point (also called "first" control point)
			if (polylin.getSegmentAt(0) == null) {
				logger.warning("Unexpected null first segment. Abort.");
				return;
			}
			FGEPoint nextPoint = polylin.getSegmentAt(0).getP2();

			if (allowedStartArea.getOrthogonalPerspectiveArea(newOrientation).containsPoint(nextPoint)
			/* || (getIsStartingLocationFixed() && getFixedStartLocation() != null) */) {
				// The general layout of polylin will not change, since next point was
				// already located in this orthogonal perspective area
				// We just need here to update previous point according to new end point location
				FGEPoint newPoint = new FGEPoint(nextPoint);
				if (newOrientation.isHorizontal()) {
					newPoint.setY(startCPLocation.y);
				} else if (newOrientation.isVertical()) {
					newPoint.setX(startCPLocation.x);
				}
				polylin.updatePointAt(1, newPoint);
				controlPoints.elementAt(1).setPoint(newPoint);

			} else {
				// In this case, the situation is worse, that means that start orientation has changed
				// We need to recompute a new layout for the polylin

				// Recompute general layout of rect polylin

				if (initialPolylin.getSegmentNb() > 2) {
					FGEPoint toPoint = initialPolylin.getPointAt(2);
					SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite();
					FGERectPolylin appendingPath;
					appendingPath = new FGERectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					// debugPolylin = appendingPath;
					FGERectPolylin mergedPolylin = mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2, initialPolylin, 2,
							initialPolylin.getPointsNb() - 1);
					updateWithNewPolylin(mergedPolylin, false, true);
				} else if (initialPolylin.getSegmentNb() > 1) {
					FGEPoint toPoint = initialPolylin.getPointAt(2);
					SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(1).getOpposite();
					FGERectPolylin appendingPath = new FGERectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					// debugPolylin = appendingPath;
					FGERectPolylin mergedPolylin = mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2, initialPolylin, 2,
							initialPolylin.getPointsNb() - 1);
					updateWithNewPolylin(mergedPolylin, false, true);
				} else {
					FGEPoint toPoint = initialPolylin.getPointAt(1);
					toPoint = retrieveEndArea().getNearestPoint(toPoint);
					newOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
					SimplifiedCardinalDirection toPointOrientation = newOrientation.getOpposite();
					FGERectPolylin newPolylin = new FGERectPolylin(startCPLocation, newOrientation, toPoint, toPointOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					updateWithNewPolylin(newPolylin, false, true);
				}
			}

		}

	}

	/**
	 * This method is internally called while updating ending point of polylin.
	 * 
	 * Calling this method assert that we are not sure anymore that end control point is still valid. When not, change this point to a valid
	 * location, and update polylin accordingly (eventually recompute new layout when required: modified orientation).
	 * 
	 * @param initialPolylin
	 *            Polylin to take under account to recreate new layout
	 * 
	 */
	public void checkAndUpdateEndCP(FGERectPolylin initialPolylin) {
		if (p_end == null) {
			_connectorChanged(true);
		}
		if (p_end == null) {
			return;
		}

		if (!FGEUtils.areElementsConnectedInGraphicalHierarchy(getEndNode(), connectorNode)) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}
		// Retrieve end area in connector coordinates system
		FGEArea endArea = retrieveEndArea();
		FGEArea allowedEndArea = retrieveAllowedEndArea(true);

		if (getConnectorSpecification().getIsEndingLocationFixed() && getConnectorSpecification().getFixedEndLocation() != null
				&& !getConnectorSpecification().getIsEndingLocationDraggable()) {
			allowedEndArea = getConnectorSpecification().getFixedEndLocation();
		}

		if (polylin.getPointsNb() == 0) {
			return;
		}

		// Retrieve control point location to update
		FGEPoint endCPLocation = polylin.getPointAt(polylin.getPointsNb() - 1);

		if (endCPLocation == null) {
			return;
		}

		// Compute new location by computing nearest point of oldLocation
		// in end area (if this location was valid, change nothing)
		// logger.info("endArea="+endArea);
		// FGEPoint oldCP = endCPLocation.clone();
		endCPLocation = endArea.getNearestPoint(endCPLocation);
		// logger.info("checkAndUpdateEndCP() from "+oldCP+" to "+endCPLocation);

		// Update polylin and end control point with this new location
		polylin.updatePointAt(polylin.getPointsNb() - 1, endCPLocation);
		p_end.setPoint(endCPLocation);

		if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
			setFixedEndLocation(FGEUtils.convertNormalizedPoint(connectorNode, endCPLocation, getEndNode()));
		}

		// Look for orientation of this newly computed segment
		SimplifiedCardinalDirection orientation = polylin.getOrientationOfSegment(polylin.getSegmentNb() - 1);

		if (orientation != null) {
			// This new location is valid
			// (the last segment is still horizontal or vertical)
			return;
		}

		else {
			// End control point has moved (the last segment is not horizontal nor vertical anymore)

			// Find new orientation by minimizing distance between
			// current end point location and the nearest point of
			// all anchor location of all possible directions
			SimplifiedCardinalDirection newOrientation = null;
			double bestDistance = Double.POSITIVE_INFINITY;
			for (SimplifiedCardinalDirection o : getAllowedEndOrientations()) {
				double distance = FGEPoint.distance(endCPLocation, endArea.getAnchorAreaFrom(o).getNearestPoint(endCPLocation));
				if (distance < bestDistance - FGEGeometricObject.EPSILON) {
					newOrientation = o;
					bestDistance = distance;
				}
			}

			// debugPolylin = null;

			// Retrieve previous point (also called "last" control point)
			if (polylin.getSegmentAt(polylin.getSegmentNb() - 1) == null) {
				logger.warning("Unexpected null last segment. Abort.");
				return;
			}
			FGEPoint previousPoint = polylin.getSegmentAt(polylin.getSegmentNb() - 1).getP1();

			if (allowedEndArea.getOrthogonalPerspectiveArea(newOrientation).containsPoint(previousPoint)
			/* || (getIsEndingLocationFixed() && getFixedEndLocation() != null) */) {
				// The general layout of polylin will not change, since previous point was
				// already located in this orthogonal perspective area
				// We just need here to update previous point according to new end point location
				FGEPoint newPoint = new FGEPoint(previousPoint);
				if (newOrientation.isHorizontal()) {
					newPoint.setY(endCPLocation.y);
				} else if (newOrientation.isVertical()) {
					newPoint.setX(endCPLocation.x);
				}
				polylin.updatePointAt(polylin.getPointsNb() - 2, newPoint);
				controlPoints.elementAt(polylin.getPointsNb() - 2).setPoint(newPoint);
			} else {
				// In this case, the situation is worse, that means that end orientation has changed
				// We need to recompute a new layout for the polylin

				// Recompute general layout of rect polylin

				if (initialPolylin.getSegmentNb() > 2) {
					FGEPoint toPoint = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
					SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin
							.getPointsNb() - 3);
					FGERectPolylin appendingPath;
					appendingPath = new FGERectPolylin(toPoint, toPointOrientation, endCPLocation, newOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					// debugPolylin = appendingPath;
					FGERectPolylin mergedPolylin = mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 3, appendingPath, 1,
							appendingPath.getPointsNb() - 1);
					updateWithNewPolylin(mergedPolylin, false, true);
				} else if (initialPolylin.getSegmentNb() > 1) {
					FGEPoint toPoint = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
					SimplifiedCardinalDirection toPointOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin
							.getPointsNb() - 3);
					FGERectPolylin appendingPath = new FGERectPolylin(toPoint, toPointOrientation, endCPLocation, newOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					// debugPolylin = appendingPath;

					// FGERectPolylin mergedPolylin = mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-2, appendingPath, 1,
					// appendingPath.getPointsNb()-1);
					// updateWithNewPolylin(mergedPolylin);

					updateWithNewPolylin(appendingPath, false, true);
				} else {
					FGEPoint fromPoint = initialPolylin.getPointAt(0);
					fromPoint = retrieveStartArea().getNearestPoint(fromPoint);
					newOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
					SimplifiedCardinalDirection toPointOrientation = newOrientation.getOpposite();
					FGERectPolylin newPolylin = new FGERectPolylin(fromPoint, newOrientation, endCPLocation, toPointOrientation, true,
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap());

					updateWithNewPolylin(newPolylin, false, true);
				}
			}

		}

	}

	/**
	 * Compute a new polylin by concatening supplied polylins and given indexes
	 * 
	 * @param p1
	 * @param startIndex1
	 * @param endIndex1
	 * @param p2
	 * @param startIndex2
	 * @param endIndex2
	 * @return
	 */
	public FGERectPolylin mergePolylins(FGERectPolylin p1, int startIndex1, int endIndex1, FGERectPolylin p2, int startIndex2, int endIndex2) {
		FGERectPolylin returned = new FGERectPolylin();
		returned.setOverlapX(getOverlapXResultingFromPixelOverlap());
		returned.setOverlapY(getOverlapYResultingFromPixelOverlap());
		for (int i = startIndex1; i <= endIndex1; i++) {
			returned.addToPoints(p1.getPointAt(i));
		}
		for (int i = startIndex2; i <= endIndex2; i++) {
			returned.addToPoints(p2.getPointAt(i));
		}
		return returned;
	}

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable.
	 * 
	 * @param index
	 */
	public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index) {
		_simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index, null);
	}

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable. If a location is given, this location will be
	 * used to adapt position of previous and next point asserting that they must be located on an horizontal or vertical segment.
	 * 
	 * @param index
	 * @param newCPLocation
	 */
	public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index, FGEPoint newCPLocation) {
		SimplifiedCardinalDirection relatedSegmentOrientation = getCurrentPolylin().getApproximatedOrientationOfSegment(index);
		getCurrentPolylin().removePointAtIndex(index);
		getCurrentPolylin().removePointAtIndex(index);
		controlPoints.remove(index);
		controlPoints.remove(index);
		if (newCPLocation != null) {
			if (relatedSegmentOrientation.isHorizontal()) {
				getCurrentPolylin().updatePointAt(index - 1, new FGEPoint(newCPLocation.x, getCurrentPolylin().getPointAt(index - 1).y));
				getCurrentPolylin().updatePointAt(index, new FGEPoint(newCPLocation.x, getCurrentPolylin().getPointAt(index).y));
			} else if (relatedSegmentOrientation.isVertical()) {
				getCurrentPolylin().updatePointAt(index - 1, new FGEPoint(getCurrentPolylin().getPointAt(index - 1).x, newCPLocation.y));
				getCurrentPolylin().updatePointAt(index, new FGEPoint(getCurrentPolylin().getPointAt(index).x, newCPLocation.y));
			}
		}
		updateWithNewPolylin(getCurrentPolylin(), false, true);
	}

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getStartLocation() {
		if (polylin == null) {
			return null;
		}
		FGEPoint returned = FGEUtils.convertNormalizedPoint(connectorNode, polylin.getFirstPoint(), getStartNode());
		returned = getStartNode().getFGEShape().getNearestPoint(returned);
		return returned;
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getEndLocation() {
		if (polylin == null) {
			return null;
		}
		FGEPoint returned = FGEUtils.convertNormalizedPoint(connectorNode, polylin.getLastPoint(), getEndNode());
		returned = getEndNode().getFGEShape().getNearestPoint(returned);
		return returned;
	}

	public boolean getWasManuallyAdjusted() {
		return wasManuallyAdjusted;
	}

	public void setWasManuallyAdjusted(boolean aFlag) {
		wasManuallyAdjusted = aFlag;
		if (!wasManuallyAdjusted) {
			if (polylin != null) {
				updateWithNewPolylin(polylin);
			}
			// if (isAdjustable) polylin = null;
			if (connectorNode != null) {
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				connectorNode.notifyConnectorModified();
			}
		}
	}

	public FGEPoint getFixedStartLocation() {
		return getConnectorSpecification().getFixedStartLocation();
	}

	public void setFixedStartLocation(FGEPoint aPoint) {
		FGEShape<?> startArea = getStartNode().getShape().getOutline();
		aPoint = startArea.getNearestPoint(aPoint);
		getConnectorSpecification().setFixedStartLocation(aPoint);
	}

	public FGEPoint getFixedEndLocation() {
		return getConnectorSpecification().getFixedEndLocation();
	}

	public void setFixedEndLocation(FGEPoint aPoint) {
		FGEShape<?> endArea = getEndNode().getShape().getOutline();
		aPoint = endArea.getNearestPoint(aPoint);
		getConnectorSpecification().setFixedEndLocation(aPoint);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getSource() == getConnectorSpecification()) {

			if (evt.getPropertyName() == RectPolylinConnectorSpecification.RECT_POLYLIN_CONSTRAINTS.getName()) {
				p_start = null;
				p_end = null;
				updateLayout();
				connectorNode.notifyConnectorModified();
			} else if (evt.getPropertyName() == RectPolylinConnectorSpecification.STRAIGHT_LINE_WHEN_POSSIBLE.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.START_ORIENTATION.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.END_ORIENTATION.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.PIXEL_OVERLAP.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ROUNDED.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.ARC_SIZE.getName()) {
				updateLayout();
				connectorNode.notifyConnectorModified();
			} else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.FIXED_START_LOCATION.getName()
					|| evt.getPropertyName() == RectPolylinConnectorSpecification.FIXED_END_LOCATION.getName()) {
				updateLayout();
				// Force control points to be rebuild in order to get draggable feature
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			} else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_STARTING_LOCATION_FIXED.getName()) {
				if (getConnectorSpecification().getIsStartingLocationFixed() && fixedStartLocationRelativeToStartObject == null
						&& p_start != null) {
					// In this case, we can initialize fixed start location to its current value
					fixedStartLocationRelativeToStartObject = FGEUtils.convertNormalizedPoint(connectorNode, p_start.getPoint(),
							getStartNode());
				}
				updateLayout();
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			} else if (evt.getPropertyName() == RectPolylinConnectorSpecification.IS_ENDING_LOCATION_FIXED.getName()) {
				if (getConnectorSpecification().getIsEndingLocationFixed() && fixedEndLocationRelativeToEndObject == null && p_end != null) {
					// In this case, we can initialize fixed start location to its current value
					fixedEndLocationRelativeToEndObject = FGEUtils.convertNormalizedPoint(connectorNode, p_end.getPoint(), getEndNode());
				}
				updateLayout();
				_rebuildControlPoints();
				connectorNode.notifyConnectorModified();
			} else if (evt.getPropertyName() == RectPolylinConnectorSpecification.ADJUSTABILITY.getName()) {
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				connectorNode.notifyConnectorModified();

				if (getConnectorSpecification().getAdjustability() != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
					setWasManuallyAdjusted(false);
				}
			}
		}
	}

}
