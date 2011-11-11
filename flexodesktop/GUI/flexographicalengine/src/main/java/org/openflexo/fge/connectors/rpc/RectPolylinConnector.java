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
package org.openflexo.fge.connectors.rpc;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.connectors.Connector;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
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
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle.DashStyle;
import org.openflexo.toolbox.ConcatenedList;

/**
 * A RectPolylinConnector is a connector joining 2 shapes with a path of orthogonal segments (this connector is encoded as a
 * {@link FGERectPolylin} instance).
 * 
 * This connector has many configuration parameters.
 * 
 * Mainly, there are three principal modes, regarding user control
 * 
 * - automatic layout: the layout is continuously recomputed and updated, given location and orientation constraints (adjustability =
 * AUTO_LAYOUT) - semi-adjustable layout: a user is editing the layout by moving a single control point (any point located on connector).
 * Locations and orientation constraints are respected (adjustability = BASICALLY_ADJUSTABLE) - adjustable layout: layout is fully
 * controlled by user: layout is editable by moving, adding and removing control points, and segments are translatable. Locations and
 * orientation constraints are respected
 * 
 * Layout mode is configurable by using {@link #setAdjustability(RectPolylinAdjustability)} method (default is automatic layout).
 * 
 * This connector encodes many control points: - start control point is the starting control point, located on the outline of starting shape
 * - first control point is the (eventual) first control point located outside both shapes (just after start control point) - end control
 * point is the ending control point, located on the outline of ending shape - last control point is the (eventual) last control point
 * located outside both shapes (just before end control point) - an intermediate control point is an other control point, which is not the
 * start, first, last or end control point
 * 
 * @author sylvain
 * 
 */
public class RectPolylinConnector extends Connector {

	static final Logger logger = Logger.getLogger(RectPolylinConnector.class.getPackage().getName());

	private FGERectPolylin polylin;
	private Vector<FGERectPolylin> potentialPolylin;
	// public FGERectPolylin debugPolylin;

	private ControlPoint p_start;
	private ControlPoint p_end;
	// private ControlPoint crossedControlPoint;

	private Vector<ControlPoint> controlPoints;
	private Vector<ControlArea<?>> controlAreas;

	private boolean firstUpdated = false;

	private boolean straightLineWhenPossible = true;
	private RectPolylinAdjustability adjustability = RectPolylinAdjustability.AUTO_LAYOUT;
	private boolean wasManuallyAdjusted = false;

	private SimplifiedCardinalDirection startOrientation;
	private SimplifiedCardinalDirection endOrientation;

	private int pixelOverlap = FGEConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP; // overlap expressed in pixels relative to 1.0 scale

	private int arcSize = FGEConstants.DEFAULT_ROUNDED_RECT_POLYLIN_ARC_SIZE;
	private boolean isRounded = false;

	private boolean isStartingLocationFixed = false;
	private boolean isEndingLocationFixed = false;

	private boolean isStartingLocationDraggable = false;
	private boolean isEndingLocationDraggable = false;

	// private FGEPoint startCPRelativeToStartObject;
	// private FGEPoint endCPRelativeToEndObject;

	private FGERectPolylin polylinRelativeToStartObject;
	private FGERectPolylin polylinRelativeToEndObject;

	private FGERectPolylin lastKnownCleanPolylinBeforeConnectorRestructuration;
	private boolean isCleaningPolylin = false;

	private FGEPoint fixedStartLocationRelativeToStartObject;
	private FGEPoint fixedEndLocationRelativeToEndObject;
	private FGEPoint _crossedPoint;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public RectPolylinConnector() {
		this(null);
	}

	public RectPolylinConnector(ConnectorGraphicalRepresentation graphicalRepresentation) {
		super(graphicalRepresentation);
		controlPoints = new Vector<ControlPoint>();
		controlAreas = new Vector<ControlArea<?>>();
		potentialPolylin = new Vector<FGERectPolylin>();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.RECT_POLYLIN;
	}

	Vector<ControlPoint> _getControlPoints() {
		return controlPoints;
	}

	@Override
	public List<? extends ControlArea> getControlAreas() {
		if (getGraphicalRepresentation().getMiddleSymbol() == MiddleSymbolType.NONE && controlAreas.size() == 0) {
			return controlPoints;
		}

		// Otherwise, we have to manage a concatenation
		if (allControlAreas == null) {
			allControlAreas = new ConcatenedList<ControlArea>();
			allControlAreas.addElementList(controlPoints);
			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE && middleSymbolLocationControlPoint != null) {
				allControlAreas.add(0, middleSymbolLocationControlPoint);
			}
			allControlAreas.addElementList(controlAreas);
		}
		return allControlAreas;
	}

	private ConcatenedList<ControlArea> allControlAreas;

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		/*
		 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.PINK, 1.0f, DashStyle.SMALL_DASHES));
		 * debugPolylin.paint(g); }
		 */

		if (getDebug()) {
			g.setDefaultForeground(ForegroundStyle.makeStyle(Color.GRAY, 1.0f, DashStyle.SMALL_DASHES));
			for (FGERectPolylin p : potentialPolylin) {
				p.paint(g);
			}
			g.setDefaultForeground(ForegroundStyle.makeStyle(Color.BLACK, 3.0f, DashStyle.PLAIN_STROKE));
			if (polylin != null) {
				polylin.debugPaint(g);
			}
		} else {
			g.setDefaultForeground(getGraphicalRepresentation().getForeground());
			if (polylin != null) {
				if (getIsRounded()) {
					polylin.paintWithRounds(g, getArcSize());
				} else {
					polylin.paint(g);
				}
			}
		}

		/*
		 * if (debugPolylin != null) { g.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED, 1.0f, DashStyle.PLAIN_STROKE));
		 * debugPolylin.paint(g); }
		 */

		// Draw eventual symbols
		if (polylin != null && polylin.getSegments() != null && polylin.getSegments().size() > 0) {
			// Segments are here all orthogonal, we can can then rely on getAngle() computation performed on geom layer
			// (we dont need to convert to view first)
			if (getGraphicalRepresentation().getStartSymbol() != StartSymbolType.NONE) {
				FGESegment firstSegment = polylin.getSegments().firstElement();
				if (firstSegment != null) {
					g.drawSymbol(firstSegment.getP1(), getGraphicalRepresentation().getStartSymbol(), getGraphicalRepresentation()
							.getStartSymbolSize(), firstSegment.getAngle());
				}
			}
			if (getGraphicalRepresentation().getEndSymbol() != EndSymbolType.NONE) {
				FGESegment lastSegment = polylin.getSegments().lastElement();
				if (lastSegment != null) {
					g.drawSymbol(lastSegment.getP2(), getGraphicalRepresentation().getEndSymbol(), getGraphicalRepresentation()
							.getEndSymbolSize(), lastSegment.getAngle() + Math.PI);
				}
			}
			if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
				g.drawSymbol(getMiddleSymbolLocation(), getGraphicalRepresentation().getMiddleSymbol(), getGraphicalRepresentation()
						.getMiddleSymbolSize(), getMiddleSymbolAngle());
			}
		}

	}

	@Override
	public FGEPoint getMiddleSymbolLocation() {
		if (polylin == null) {
			return new FGEPoint(0, 0);
		}
		AffineTransform at = getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(1.0);

		FGERectPolylin transformedPolylin = polylin.transform(at);
		FGEPoint point = transformedPolylin.getPointAtRelativePosition(getGraphicalRepresentation().getRelativeMiddleSymbolLocation());
		try {
			point = point.transform(at.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		if (!getIsRounded()) {
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
		if (!getIsRounded()) {
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
	public void refreshConnector() {
		if (!needsRefresh()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Skipping refreshConnector() for " + getGraphicalRepresentation().getDrawable());
			}
			return;
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Perform refreshConnector() for " + getGraphicalRepresentation().getDrawable());
			}
		}

		updateLayout();

		if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

		super.refreshConnector();

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

		Point testPoint = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(aPoint, scale);

		for (FGESegment s : polylin.getSegments()) {
			Point point1 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(s.getP1(), scale);
			Point point2 = getGraphicalRepresentation().convertNormalizedPointToViewCoordinates(s.getP2(), scale);
			double distanceToCurrentSegment = Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
			if (distanceToCurrentSegment < returned) {
				returned = distanceToCurrentSegment;
			}
		}
		return returned;

	}

	public static enum RectPolylinAdjustability {
		AUTO_LAYOUT, BASICALLY_ADJUSTABLE, FULLY_ADJUSTABLE
	}

	public static enum RectPolylinConstraints {
		NONE,
		ORTHOGONAL_LAYOUT,
		ORTHOGONAL_LAYOUT_HORIZONTAL_FIRST,
		ORTHOGONAL_LAYOUT_VERTICAL_FIRST,
		HORIZONTAL_OR_VERTICAL_LAYOUT,
		HORIZONTAL_LAYOUT,
		VERTICAL_LAYOUT,
		ORIENTATIONS_FIXED,
		START_ORIENTATION_FIXED,
		END_ORIENTATION_FIXED
	}

	private RectPolylinConstraints rectPolylinConstraints = RectPolylinConstraints.NONE;

	public RectPolylinConstraints getRectPolylinConstraints() {
		return rectPolylinConstraints;
	}

	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints) {
		if (aRectPolylinConstraints != rectPolylinConstraints) {
			rectPolylinConstraints = aRectPolylinConstraints;
			p_start = null;
			p_end = null;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation) {
		if (someRectPolylinConstraints != rectPolylinConstraints || startOrientation != aStartOrientation
				|| endOrientation != aEndOrientation) {
			rectPolylinConstraints = someRectPolylinConstraints;
			startOrientation = aStartOrientation;
			endOrientation = aEndOrientation;
			// p_start = null;
			// p_end = null;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public boolean getStraightLineWhenPossible() {
		return straightLineWhenPossible;
	}

	public void setStraightLineWhenPossible(boolean aFlag) {
		straightLineWhenPossible = aFlag;
		if (getGraphicalRepresentation() != null) {
			updateLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
		}
	}

	public boolean getIsAdjustable() {
		return getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE;
	}

	public void setIsAdjustable(boolean aFlag) {
		setAdjustability(aFlag ? RectPolylinAdjustability.FULLY_ADJUSTABLE : RectPolylinAdjustability.AUTO_LAYOUT);
	}

	public RectPolylinAdjustability getAdjustability() {
		return adjustability;
	}

	public void setAdjustability(RectPolylinAdjustability anAdjustability) {
		if (adjustability != anAdjustability) {
			adjustability = anAdjustability;

			// logger.info("Switching to setIsAdjustable("+aFlag+")");

			if (polylin != null) {
				updateWithNewPolylin(polylin);
			}
			// if (isAdjustable) polylin = null;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				getGraphicalRepresentation().notifyConnectorChanged();
			}

			if (adjustability != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
				setWasManuallyAdjusted(false);
			}
		}
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
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				if (polylin != null) {
					updateWithNewPolylin(polylin);
				}
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public SimplifiedCardinalDirection getEndOrientation() {
		return endOrientation;
	}

	public void setEndOrientation(SimplifiedCardinalDirection anOrientation) {
		// logger.info("setEndOrientation="+anOrientation);
		if (anOrientation != endOrientation) {
			endOrientation = anOrientation;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public SimplifiedCardinalDirection getStartOrientation() {
		return startOrientation;
	}

	public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
		if (anOrientation != startOrientation) {
			startOrientation = anOrientation;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public int getPixelOverlap() {
		return pixelOverlap;
	}

	public void setPixelOverlap(int aPixelOverlap) {
		if (aPixelOverlap != pixelOverlap) {
			pixelOverlap = aPixelOverlap;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	double getOverlapXResultingFromPixelOverlap() {
		// Compute relative overlap along X-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(getPixelOverlap(), 0);
		FGEPoint overlap_pp1 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		FGEPoint overlap_pp2 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.x - overlap_pp2.x);
	}

	double getOverlapYResultingFromPixelOverlap() {
		// Compute relative overlap along Y-axis
		Point overlap_p1 = new Point(0, 0);
		Point overlap_p2 = new Point(0, getPixelOverlap());
		FGEPoint overlap_pp1 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(overlap_p1, 1);
		FGEPoint overlap_pp2 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(overlap_p2, 1);
		return Math.abs(overlap_pp1.y - overlap_pp2.y);
	}

	public boolean getIsRounded() {
		return isRounded;
	}

	public void setIsRounded(boolean aFlag) {
		if (isRounded != aFlag) {
			isRounded = aFlag;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public int getArcSize() {
		return arcSize;
	}

	public void setArcSize(int anArcSize) {
		if (anArcSize != arcSize) {
			arcSize = anArcSize;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public FGERectangle getConnectorUsedBounds() {
		// logger.info("Called getConnectorUsedBounds()");
		if (polylin != null) {
			FGERectangle minimalBounds = polylin.getBoundingBox();
			FGERectangle returned = new FGERectangle(Filling.FILLED);

			// Compute required space to draw symbols, eg arrows
			double maxSymbolSize = Math.max(getGraphicalRepresentation().getStartSymbolSize(),
					Math.max(getGraphicalRepresentation().getMiddleSymbolSize(), getGraphicalRepresentation().getEndSymbolSize()));
			double relativeWidthToAdd = maxSymbolSize * 2 / getGraphicalRepresentation().getViewWidth(1.0);
			double relativeHeightToAdd = maxSymbolSize * 2 / getGraphicalRepresentation().getViewHeight(1.0);

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

	private FGERectPolylin _deserializedPolylin;

	// Used for serialization only
	public FGERectPolylin _getPolylin() {
		if (getAdjustability() != RectPolylinAdjustability.FULLY_ADJUSTABLE) {
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
			middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(getGraphicalRepresentation(), getMiddleSymbolLocation()) {
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
					AffineTransform at = getGraphicalRepresentation().convertNormalizedPointToViewCoordinatesAT(1.0);
					pt = pt.transform(at);
					FGERectPolylin transformedPolylin = polylin.transform(at);

					// FGESegment segment = new FGESegment(cp1.getPoint(),cp2.getPoint());
					getGraphicalRepresentation().setRelativeMiddleSymbolLocation(transformedPolylin.getRelativeLocation(pt));

					/*
					 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
					 * getStartObject());
					 */
					getGraphicalRepresentation().notifyConnectorChanged();
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
		FGEPoint arcP1 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(new Point(0, 0), 1.0);
		FGEPoint arcP2 = getGraphicalRepresentation().convertViewCoordinatesToNormalizedPoint(new Point(arcSize, arcSize), 1.0);
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

	public FGEPoint getCrossedControlPoint() {
		// if (crossedControlPoint == null) {
		return _crossedPoint;
		/*
		 * } return crossedControlPoint.getPoint();
		 */
	}

	public void setCrossedControlPoint(FGEPoint aPoint) {
		/*
		 * if (aPoint == null) { _crossedPoint = null; crossedControlPoint = null; return; } else {
		 */
		_crossedPoint = aPoint;
		if (getGraphicalRepresentation() != null) {
			updateLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
		}
		// }
	}

	public FGEPoint getCrossedControlPointOnRoundedArc() {
		if (getCrossedControlPoint() != null) {
			if (getIsRounded()) {
				UnnormalizedArcSize arcSize = computeUnnormalizedArcSize();
				return polylin.getNearestPointLocatedOnRoundedRepresentation(getCrossedControlPoint(), arcSize.arcWidth, arcSize.arcWidth);
			} else {
				return getCrossedControlPoint();
			}
		}
		return null;
	}

	public boolean getIsStartingLocationFixed() {
		return isStartingLocationFixed;
	}

	public void setIsStartingLocationFixed(boolean aFlag) {
		if (isStartingLocationFixed != aFlag) {
			isStartingLocationFixed = aFlag;
			if (isStartingLocationFixed && fixedStartLocationRelativeToStartObject == null && p_start != null) {
				// In this case, we can initialize fixed start location to its current value
				fixedStartLocationRelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(),
						p_start.getPoint(), getStartObject());
			}
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public boolean getIsStartingLocationDraggable() {
		return isStartingLocationDraggable;
	}

	public void setIsStartingLocationDraggable(boolean aFlag) {
		if (isStartingLocationDraggable != aFlag) {
			isStartingLocationDraggable = aFlag;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				// Force control points to be rebuild in order to get draggable feature
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public boolean getIsEndingLocationFixed() {
		return isEndingLocationFixed;
	}

	public void setIsEndingLocationFixed(boolean aFlag) {
		if (isEndingLocationFixed != aFlag) {
			isEndingLocationFixed = aFlag;
			if (isEndingLocationFixed && fixedEndLocationRelativeToEndObject == null && p_end != null) {
				// In this case, we can initialize fixed start location to its current value
				fixedEndLocationRelativeToEndObject = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(),
						p_end.getPoint(), getEndObject());
			}
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public boolean getIsEndingLocationDraggable() {
		return isEndingLocationDraggable;
	}

	public void setIsEndingLocationDraggable(boolean aFlag) {
		if (isEndingLocationDraggable != aFlag) {
			isEndingLocationDraggable = aFlag;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				// Force control points to be rebuild in order to get draggable feature
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	public FGEPoint getFixedStartLocation() {
		if (!getIsStartingLocationFixed()) {
			return null;
		}
		if (fixedStartLocationRelativeToStartObject == null) {
			FGEPoint centerOfEndObjectSeenFromStartObject = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), new FGEPoint(
					0.5, 0.5), getStartObject());
			fixedStartLocationRelativeToStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
			if (fixedStartLocationRelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedStartLocationRelativeToStartObject = new FGEPoint(0.9, 0.9);
			}
		}
		return fixedStartLocationRelativeToStartObject;
	}

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	public void setFixedStartLocation(FGEPoint aPoint) {
		if (!isStartingLocationFixed) {
			isStartingLocationFixed = true;
		}
		if (getStartObject() != null) {
			FGEShape startArea = getStartObject().getShape().getOutline();
			// startArea.setIsFilled(false);
			aPoint = startArea.getNearestPoint(aPoint);
		}
		if (fixedStartLocationRelativeToStartObject == null || !fixedStartLocationRelativeToStartObject.equals(aPoint)) {
			fixedStartLocationRelativeToStartObject = aPoint;
			// logger.info("fixedStartLocationRelativeToStartObject="+fixedStartLocationRelativeToStartObject);
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	public FGEPoint getFixedEndLocation() {
		if (!getIsEndingLocationFixed()) {
			return null;
		}
		if (fixedEndLocationRelativeToEndObject == null) {
			FGEPoint centerOfStartObjectSeenFromEndObject = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), new FGEPoint(
					0.5, 0.5), getEndObject());
			fixedEndLocationRelativeToEndObject = getEndObject().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
			if (fixedEndLocationRelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedEndLocationRelativeToEndObject = new FGEPoint(0.1, 0.1);
			}
		}
		return fixedEndLocationRelativeToEndObject;
	}

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	public void setFixedEndLocation(FGEPoint aPoint) {
		if (!isEndingLocationFixed) {
			isEndingLocationFixed = true;
		}
		if (getEndObject() != null) {
			FGEShape endArea = getEndObject().getShape().getOutline();
			// endArea.setIsFilled(false);
			aPoint = endArea.getNearestPoint(aPoint);
		}
		if (fixedEndLocationRelativeToEndObject == null || !fixedEndLocationRelativeToEndObject.equals(aPoint)) {
			fixedEndLocationRelativeToEndObject = aPoint;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	public Vector<SimplifiedCardinalDirection> getAllowedStartOrientations() {
		Vector<SimplifiedCardinalDirection> returned = getPrimitiveAllowedStartOrientations();
		if (getIsStartingLocationFixed() && getFixedStartLocation() != null) {
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
		FGEShape startArea = getStartObject().getShape().getOutline();
		// startArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (startArea.getAnchorAreaFrom(o).containsPoint(getFixedStartLocation())) {
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
		switch (getRectPolylinConstraints()) {
		case NONE:
			return SimplifiedCardinalDirection.allDirections();
		case START_ORIENTATION_FIXED:
			if (getStartOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getStartOrientation());
		case ORIENTATIONS_FIXED:
			if (getStartOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getStartOrientation());
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
		if (getIsEndingLocationFixed() && getFixedEndLocation() != null) {
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
		FGEShape endArea = getEndObject().getShape().getOutline();
		// endArea.setIsFilled(false);
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			// if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(getFixedEndLocation())) returned.add(o);
			if (endArea.getAnchorAreaFrom(o).containsPoint(getFixedEndLocation())) {
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
		switch (getRectPolylinConstraints()) {
		case NONE:
			return SimplifiedCardinalDirection.allDirections();
		case END_ORIENTATION_FIXED:
			if (getEndOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getEndOrientation());
		case ORIENTATIONS_FIXED:
			if (getEndOrientation() == null) {
				return SimplifiedCardinalDirection.uniqueDirection(SimplifiedCardinalDirection.NORTH);
			}
			return SimplifiedCardinalDirection.uniqueDirection(getEndOrientation());
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
	void updateLayout() {
		if (getGraphicalRepresentation() == null) {
			return;
		}

		if (!getGraphicalRepresentation().isRegistered()) {
			return;
		}

		if (getAdjustability() == RectPolylinAdjustability.AUTO_LAYOUT || getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE
				&& !getWasManuallyAdjusted()) {

			_updateAsAutoLayout();

		}

		else if (getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {

			if (polylin == null) {

				if (_deserializedPolylin != null) {
					// Rebuild from deserialized polylin
					updateWithNewPolylin(new FGERectPolylin(_deserializedPolylin.getPoints(), getStraightLineWhenPossible(),
							getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap()));
					_deserializedPolylin = null;
				}

				else {
					// Was never computed, do it now
					_updateAsAutoLayout();
				}
			}

			_updateAsBasicallyAdjustable();

		}

		else /* RectPolylinConnector is adjustable, getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE */{

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
	protected FGEArea retrieveStartArea() {
		FGEArea startArea = retrieveAllowedStartArea(true);

		if (getIsStartingLocationFixed() && getFixedStartLocation() != null) {
			FGEPoint fixedPoint = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), getFixedStartLocation(),
					getGraphicalRepresentation());
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
	protected FGEArea retrieveAllowedStartArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartObject(), getGraphicalRepresentation());

		FGEArea startArea = getStartObject().getShape().getOutline().transform(at1);
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
	protected FGEArea retrieveEndArea() {
		// System.out.println("retrieveAllowedEndArea()="+retrieveAllowedEndArea());

		FGEArea endArea = retrieveAllowedEndArea(true);

		if (getIsEndingLocationFixed() && getFixedEndLocation() != null) {
			FGEPoint fixedPoint = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), getFixedEndLocation(),
					getGraphicalRepresentation());
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
	protected FGEArea retrieveAllowedEndArea(boolean takeFixedControlPointUnderAccount) {
		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndObject(), getGraphicalRepresentation());

		FGEArea endArea = getEndObject().getShape().getOutline().transform(at2);
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

		if (getRectPolylinConstraints() == RectPolylinConstraints.ORIENTATIONS_FIXED) {
			potentialStartOrientations.add(getStartOrientation());
			potentialEndOrientations.add(getEndOrientation());
		}

		else {
			CardinalQuadrant quadrant = FGEPoint.getCardinalQuadrant(startMiddle, endMiddle);

			RectPolylinConstraints constraints = getRectPolylinConstraints();
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

			if (getRectPolylinConstraints() == RectPolylinConstraints.START_ORIENTATION_FIXED) {
				potentialStartOrientations.clear();
				potentialStartOrientations.add(getStartOrientation());

			}

			if (getRectPolylinConstraints() == RectPolylinConstraints.END_ORIENTATION_FIXED) {
				potentialEndOrientations.clear();
				potentialEndOrientations.add(getEndOrientation());
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
							potentialEndOrientations.get(i), getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
							getOverlapYResultingFromPixelOverlap());
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
			for (int i = 0; i < potentialStartOrientations.size(); i++) {
				for (int j = 0; j < potentialEndOrientations.size(); j++) {
					if (allowedStartOrientations.contains(potentialStartOrientations.get(i))
							&& allowedEndOrientations.contains(potentialEndOrientations.get(j))) {
						FGERectPolylin newPolylin = new FGERectPolylin(startArea, potentialStartOrientations.get(i), endArea,
								potentialEndOrientations.get(j), getStraightLineWhenPossible(), getOverlapXResultingFromPixelOverlap(),
								getOverlapYResultingFromPixelOverlap());
						potentialPolylin.add(newPolylin);
						if (newPolylin.doesRespectAllConstraints() && newPolylin.getLength() < minimalLength + FGEGeometricObject.EPSILON /*
																																			* Hysteresis
																																			* to
																																			* avoid
																																			* blinking
																																			*/) {
							polylin = newPolylin;
							minimalLength = newPolylin.getLength();
							choosenStartOrientation = potentialStartOrientations.get(i);
							choosenEndOrientation = potentialEndOrientations.get(j);
						}
					}
				}
			}
		}

		startOrientation = polylin.getStartOrientation();
		endOrientation = polylin.getEndOrientation();

		// logger.info("Best polylin found from/to "+startOrientation+"/"+endOrientation+" with "+polylin.getPointsNb()+" points");
		// logger.info("Polylin="+polylin);

		if (startOrientation != choosenStartOrientation) {
			logger.warning("Requested start orientation was: " + choosenStartOrientation + " but is finally: " + startOrientation);
		}
		if (endOrientation != choosenEndOrientation) {
			logger.warning("Requested end orientation was: " + choosenEndOrientation + " but is finally: " + endOrientation);
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
		FGEArea startArea = retrieveStartArea();
		FGEArea endArea = retrieveEndArea();

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getAllowedEndOrientations();

		FGERectPolylin newPolylin;

		if (_crossedPoint != null) {

			// System.out.println("startArea="+startArea);
			// System.out.println("endArea="+endArea);
			newPolylin = FGERectPolylin.makeRectPolylinCrossingPoint(startArea, endArea, _crossedPoint, true,
					getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap(),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));
		}

		else {
			newPolylin = FGERectPolylin.makeShortestRectPolylin(startArea, endArea, getStraightLineWhenPossible(),
					getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap(),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedStartOrientations),
					SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));
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
				updateWithNewPolylin(new FGERectPolylin(_deserializedPolylin.getPoints(), getStraightLineWhenPossible(),
						getOverlapXResultingFromPixelOverlap(), getOverlapYResultingFromPixelOverlap()));
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
			lastStartPoint = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), lastStartPoint, getGraphicalRepresentation());
			lastEndPoint = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), lastEndPoint, getGraphicalRepresentation());
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
						pointRelativeToStartObject = getStartObject().getShape().getOutline()
								.nearestOutlinePoint(pointRelativeToStartObject);
						polylin.updatePointAt(i, GraphicalRepresentation.convertNormalizedPoint(getStartObject(),
								pointRelativeToStartObject, getGraphicalRepresentation()));
					} else if (i == 1 && polylin.getPointsNb() >= 6) {
						FGEPoint firstPoint = GraphicalRepresentation.convertNormalizedPoint(getStartObject(), pointRelativeToStartObject,
								getGraphicalRepresentation());
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
						pointRelativeToEndObject = getEndObject().getShape().getOutline().nearestOutlinePoint(pointRelativeToEndObject);
						polylin.updatePointAt(i, GraphicalRepresentation.convertNormalizedPoint(getEndObject(), pointRelativeToEndObject,
								getGraphicalRepresentation()));
					} else if (i == polylin.getPointsNb() - 2 && polylin.getPointsNb() >= 6) {
						FGEPoint lastPoint = GraphicalRepresentation.convertNormalizedPoint(getEndObject(), pointRelativeToEndObject,
								getGraphicalRepresentation());
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

	void updateWithNewPolylin(FGERectPolylin aPolylin) {
		updateWithNewPolylin(aPolylin, false, false);
	}

	void updateWithNewPolylin(FGERectPolylin aPolylin, boolean temporary) {
		updateWithNewPolylin(aPolylin, false, temporary);
	}

	void updateWithNewPolylin(FGERectPolylin aPolylin, boolean assertLayoutIsValid, boolean temporary) {
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
		if (!getGraphicalRepresentation().isRegistered()) {
			return;
		}

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

		switch (getAdjustability()) {

		case AUTO_LAYOUT:
			for (int i = 0; i < nPoints; i++) {
				FGEPoint p = polylin.getPointAt(i);
				if (i == 0 && getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(p, this));
				} else if (i == nPoints - 1 && getIsEndingLocationDraggable()) {
					controlPoints.add(new AdjustableEndControlPoint(p, this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(getGraphicalRepresentation(), p));
				}
			}
			break;

		case BASICALLY_ADJUSTABLE:
			for (int i = 0; i < nPoints; i++) {
				FGEPoint p = polylin.getPointAt(i);
				if (i == 0 && getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(p, this));
				} else if (i == nPoints - 1 && getIsEndingLocationDraggable()) {
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
				if (getIsStartingLocationDraggable()) {
					controlPoints.add(new AdjustableStartControlPoint(polylin.getFirstPoint(), this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(getGraphicalRepresentation(), polylin.getFirstPoint()));
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
				if (getIsEndingLocationDraggable()) {
					controlPoints.add(new AdjustableEndControlPoint(polylin.getLastPoint(), this));
				} else {
					controlPoints.add(new ConnectorNonAdjustableControlPoint(getGraphicalRepresentation(), polylin.getLastPoint()));
				}
			}
			int nSegments = polylin.getSegments().size();
			if (nSegments < 1) {
				// Pathologic case
				logger.warning("Unexpected situation here");
			} else if (nSegments == 1) {
				if ((!getIsStartingLocationFixed() || getIsStartingLocationDraggable())
						&& (!getIsEndingLocationFixed() || getIsEndingLocationDraggable())) {
					controlAreas.add(new AdjustableUniqueSegment(polylin.getFirstSegment(), this));
				}
			} else {
				if (!getIsStartingLocationFixed() || getIsStartingLocationDraggable()) {
					controlAreas.add(new AdjustableFirstSegment(polylin.getFirstSegment(), this));
				}
				for (int i = 1; i < nSegments - 1; i++) {
					FGESegment s = polylin.getSegmentAt(i);
					controlAreas.add(new AdjustableIntermediateSegment(s, this));
				}
				if (!getIsEndingLocationFixed() || getIsEndingLocationDraggable()) {
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
	void _connectorChanged(boolean temporary) {
		if (getGraphicalRepresentation().isRegistered() && !temporary) {

			if (polylin != null) {
				if (GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(getGraphicalRepresentation(), getStartObject())) {
					AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getGraphicalRepresentation(),
							getStartObject());
					polylinRelativeToStartObject = polylin.transform(at1);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted

				if (GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(getGraphicalRepresentation(), getEndObject())) {
					AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(getGraphicalRepresentation(),
							getEndObject());
					polylinRelativeToEndObject = polylin.transform(at2);
				}
				// Otherwise, don't try to remember layout, edge is probably beeing deleted
			}
		}

		if (controlPoints.size() > 0) {
			p_start = controlPoints.firstElement();
			p_end = controlPoints.lastElement();
		}

		if (getGraphicalRepresentation().getMiddleSymbol() != MiddleSymbolType.NONE) {
			updateMiddleSymbolLocationControlPoint();
		}

	}

	/**
	 * Internal method called to update connector asserting layout is defined as FULLY_ADJUSTABLE, and when the last known polylin was a
	 * single segment
	 */
	protected boolean _updateAsFullyAdjustableForUniqueSegment(FGEPoint pt) {
		FGEArea startArea = retrieveAllowedStartArea(true);
		if (getIsStartingLocationFixed() && !getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = retrieveStartArea();
		}
		FGEArea endArea = retrieveAllowedEndArea(true);
		if (getIsEndingLocationFixed() && !getIsEndingLocationDraggable()) {
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
	void checkAndUpdateStartCP(FGERectPolylin initialPolylin) {
		if (p_start == null) {
			_connectorChanged(true);
		}
		if (p_start == null) {
			return;
		}

		if (!GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(getStartObject(), getGraphicalRepresentation())) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}

		// Retrieve start area in connector coordinates system
		FGEArea startArea = retrieveStartArea();
		FGEArea allowedStartArea = retrieveAllowedStartArea(true);

		if (getIsStartingLocationFixed() && getFixedStartLocation() != null && !getIsStartingLocationDraggable()) {
			allowedStartArea = getFixedStartLocation();
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

		if (getIsStartingLocationFixed()) { // Don't forget this !!!
			setFixedStartLocation(GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), startCPLocation,
					getGraphicalRepresentation().getStartObject()));
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
	void checkAndUpdateEndCP(FGERectPolylin initialPolylin) {
		if (p_end == null) {
			_connectorChanged(true);
		}
		if (p_end == null) {
			return;
		}

		if (!GraphicalRepresentation.areElementsConnectedInGraphicalHierarchy(getEndObject(), getGraphicalRepresentation())) {
			// Dont't try to do anything, edge is probably beeing deleted
			return;
		}
		// Retrieve end area in connector coordinates system
		FGEArea endArea = retrieveEndArea();
		FGEArea allowedEndArea = retrieveAllowedEndArea(true);

		if (getIsEndingLocationFixed() && getFixedEndLocation() != null && !getIsEndingLocationDraggable()) {
			allowedEndArea = getFixedEndLocation();
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

		if (getIsEndingLocationFixed()) { // Don't forget this !!!
			setFixedEndLocation(GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), endCPLocation,
					getGraphicalRepresentation().getEndObject()));
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
	FGERectPolylin mergePolylins(FGERectPolylin p1, int startIndex1, int endIndex1, FGERectPolylin p2, int startIndex2, int endIndex2) {
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
	void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index) {
		_simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index, null);
	}

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable. If a location is given, this location will be
	 * used to adapt position of previous and next point asserting that they must be located on an horizontal or vertical segment.
	 * 
	 * @param index
	 * @param newCPLocation
	 */
	void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index, FGEPoint newCPLocation) {
		SimplifiedCardinalDirection relatedSegmentOrientation = getCurrentPolylin().getApproximatedOrientationOfSegment(index);
		getCurrentPolylin().removePointAtIndex(index);
		getCurrentPolylin().removePointAtIndex(index);
		_getControlPoints().remove(index);
		_getControlPoints().remove(index);
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
		FGEPoint returned = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), polylin.getFirstPoint(),
				getStartObject());
		returned = getStartObject().getShape().getShape().getNearestPoint(returned);
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
		FGEPoint returned = GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), polylin.getLastPoint(),
				getEndObject());
		returned = getEndObject().getShape().getShape().getNearestPoint(returned);
		return returned;
	}

}
