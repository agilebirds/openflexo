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

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.connectors.impl.RectPolylinConnectorSpecificationImpl;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * A RectPolylinConnectorSpecification is a connector joining 2 shapes with a path of orthogonal segments (this connector is encoded as a
 * {@link FGERectPolylin} instance).
 * 
 * This connector has many configuration parameters.
 * 
 * Mainly, there are three principal modes, regarding user control
 * 
 * <ul>
 * <li>automatic layout: the layout is continuously recomputed and updated, given location and orientation constraints (adjustability =
 * AUTO_LAYOUT)</li>
 * <li>semi-adjustable layout: a user is editing the layout by moving a single control point (any point located on connector). Locations and
 * orientation constraints are respected (adjustability = BASICALLY_ADJUSTABLE)</li>
 * <li>adjustable layout: layout is fully controlled by user: layout is editable by moving, adding and removing control points, and segments
 * are translatable. Locations and orientation constraints are respected</li>
 * </ul>
 * 
 * Layout mode is configurable by using {@link #setAdjustability(RectPolylinAdjustability)} method (default is automatic layout).
 * 
 * This connector encodes many control points:
 * <ul>
 * <li>start control point is the starting control point, located on the outline of starting shape</li>
 * <li>first control point is the (eventual) first control point located outside both shapes (just after start control point)</li>
 * <li>end control point is the ending control point, located on the outline of ending shape</li>
 * <li>last control point is the (eventual) last control point located outside both shapes (just before end control point)</li>
 * <li>an intermediate control point is an other control point, which is not the start, first, last or end control point</li>
 * </ul>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(RectPolylinConnectorSpecificationImpl.class)
@XMLElement(xmlTag = "RectPolylinConnectorSpecification")
public interface RectPolylinConnectorSpecification extends ConnectorSpecification {

	// Property keys

	public static final String RECT_POLYLIN_CONSTRAINTS = "rectPolylinConstraints";
	public static final String STRAIGHT_LINE_WHEN_POSSIBLE = "straightLineWhenPossible";
	public static final String ADJUSTABILITY = "adjustability";
	public static final String START_ORIENTATION = "startOrientation";
	public static final String END_ORIENTATION = "endOrientation";
	public static final String IS_ROUNDED = "isRounded";
	public static final String ARC_SIZE = "arcSize";
	public static final String IS_STARTING_LOCATION_FIXED = "isStartingLocationFixed";
	public static final String IS_ENDING_LOCATION_FIXED = "isEndingLocationFixed";
	public static final String IS_STARTING_LOCATION_DRAGGABLE = "isStartingLocationDraggable";
	public static final String IS_ENDING_LOCATION_DRAGGABLE = "isEndingLocationDraggable";
	public static final String CROSSED_CONTROL_POINT = "crossedControlPoint";
	public static final String FIXED_START_LOCATION = "fixedStartLocation";
	public static final String FIXED_END_LOCATION = "fixedEndLocation";
	public static final String POLYLIN = "polylin";
	public static final String PIXEL_OVERLAP = "pixelOverlap";

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

	// *******************************************************************************
	// * Properties
	// *******************************************************************************

	@Getter(value = RECT_POLYLIN_CONSTRAINTS)
	@XMLAttribute
	public RectPolylinConstraints getRectPolylinConstraints();

	@Setter(value = RECT_POLYLIN_CONSTRAINTS)
	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints);

	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation);

	@Getter(value = STRAIGHT_LINE_WHEN_POSSIBLE, defaultValue = "true")
	@XMLAttribute
	public boolean getStraightLineWhenPossible();

	@Setter(value = STRAIGHT_LINE_WHEN_POSSIBLE)
	public void setStraightLineWhenPossible(boolean aFlag);

	@Getter(value = ADJUSTABILITY)
	@XMLAttribute
	public RectPolylinAdjustability getAdjustability();

	@Setter(value = ADJUSTABILITY)
	public void setAdjustability(RectPolylinAdjustability anAdjustability);

	@Getter(value = END_ORIENTATION)
	@XMLAttribute
	public SimplifiedCardinalDirection getEndOrientation();

	@Setter(value = END_ORIENTATION)
	public void setEndOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = START_ORIENTATION)
	@XMLAttribute
	public SimplifiedCardinalDirection getStartOrientation();

	@Setter(value = START_ORIENTATION)
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = IS_ROUNDED, defaultValue = "true")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED)
	public void setIsRounded(boolean aFlag);

	@Getter(value = ARC_SIZE, defaultValue = "10")
	@XMLAttribute
	public int getArcSize();

	@Setter(value = ARC_SIZE)
	public void setArcSize(int anArcSize);

	@Getter(value = IS_STARTING_LOCATION_FIXED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationFixed();

	@Setter(value = IS_STARTING_LOCATION_FIXED)
	public void setIsStartingLocationFixed(boolean aFlag);

	@Getter(value = IS_STARTING_LOCATION_DRAGGABLE, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationDraggable();

	@Setter(value = IS_STARTING_LOCATION_DRAGGABLE)
	public void setIsStartingLocationDraggable(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_FIXED, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationFixed();

	@Setter(value = IS_ENDING_LOCATION_FIXED)
	public void setIsEndingLocationFixed(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_DRAGGABLE, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationDraggable();

	@Setter(value = IS_ENDING_LOCATION_DRAGGABLE)
	public void setIsEndingLocationDraggable(boolean aFlag);

	@Getter(value = CROSSED_CONTROL_POINT, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getCrossedControlPoint();

	@Setter(value = CROSSED_CONTROL_POINT)
	public void setCrossedControlPoint(FGEPoint aPoint);

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_START_LOCATION, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getFixedStartLocation();

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	@Setter(value = FIXED_START_LOCATION)
	public void setFixedStartLocation(FGEPoint aPoint);

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_END_LOCATION, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getFixedEndLocation();

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	@Setter(value = FIXED_END_LOCATION)
	public void setFixedEndLocation(FGEPoint aPoint);

	@Getter(value = PIXEL_OVERLAP, defaultValue = "" + FGEConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP)
	@XMLAttribute
	public int getPixelOverlap();

	@Setter(value = PIXEL_OVERLAP)
	public void setPixelOverlap(int aPixelOverlap);

	@Getter(value = POLYLIN, ignoreType = true)
	public FGERectPolylin _getPolylin();

	@Setter(value = POLYLIN)
	public void _setPolylin(FGERectPolylin aPolylin);

	// *******************************************************************************
	// * Utils
	// *******************************************************************************

	public boolean getIsAdjustable();

	public void setIsAdjustable(boolean aFlag);

	/*public boolean getWasManuallyAdjusted();

	public void setWasManuallyAdjusted(boolean aFlag);*/

	/**
	 * 
	 * @return angle expressed in radians
	 */
	// public double getMiddleSymbolAngle(ConnectorNode<?> node);

	/*public FGERectPolylin getCurrentPolylin();

	public void manuallySetPolylin(FGERectPolylin aPolylin);

	public ControlPoint getEndControlPoint();

	public ControlPoint getStartControlPoint();

	public FGEPoint getCrossedControlPointOnRoundedArc();

	public Vector<SimplifiedCardinalDirection> getAllowedStartOrientations();*/

	/**
	 * Return all allowed start orientation as this is defined in orientation constraint Does NOT take under account the fact that starting
	 * position could have been fixed and can also induced an other start orientation.
	 * 
	 * @return
	 */
	// public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedStartOrientations();

	/*public Vector<SimplifiedCardinalDirection> getExcludedStartOrientations();

	public Vector<SimplifiedCardinalDirection> getAllowedEndOrientations();

	public Vector<SimplifiedCardinalDirection> getPrimitiveAllowedEndOrientations();

	public Vector<SimplifiedCardinalDirection> getExcludedEndOrientations();

	public Vector<ControlPoint> _getControlPoints();

	public void updateLayout();

	public void updateWithNewPolylin(FGERectPolylin aPolylin);

	public void updateWithNewPolylin(FGERectPolylin aPolylin, boolean temporary);

	public void updateWithNewPolylin(FGERectPolylin aPolylin, boolean assertLayoutIsValid, boolean temporary);

	public boolean _updateAsFullyAdjustableForUniqueSegment(FGEPoint pt);

	public void _connectorChanged(boolean temporary);*/

	/**
	 * Compute and return start area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of start area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return FGEArea
	 */
	// public FGEArea retrieveStartArea();

	/**
	 * Compute and return allowed start area, in the connector coordinates system If some orientation constraints are defined, return
	 * portion of start area outline matching allowed orientations
	 * 
	 * @return FGEArea
	 */
	// public FGEArea retrieveAllowedStartArea(boolean takeFixedControlPointUnderAccount);

	/**
	 * Compute and return end area outline, in the connector coordinates system
	 * 
	 * If some orientation constraints are defined, return portion of end area outline matching allowed orientations
	 * 
	 * If starting location is fixed return this location
	 * 
	 * @return FGEArea
	 */
	// public FGEArea retrieveEndArea();

	/**
	 * Compute and return allowed end area, in the connector coordinates system If some orientation constraints are defined, return portion
	 * of end area outline matching allowed orientations
	 * 
	 * @return FGEArea
	 */
	// public FGEArea retrieveAllowedEndArea(boolean takeFixedControlPointUnderAccount);

	// public double getOverlapXResultingFromPixelOverlap(ConnectorNode<?> node);

	// public double getOverlapYResultingFromPixelOverlap(ConnectorNode<?> node);

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
	// public void checkAndUpdateStartCP(FGERectPolylin initialPolylin);

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
	// public void checkAndUpdateEndCP(FGERectPolylin initialPolylin);

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
	// public FGERectPolylin mergePolylins(FGERectPolylin p1, int startIndex1, int endIndex1, FGERectPolylin p2, int startIndex2, int
	// endIndex2);

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable.
	 * 
	 * @param index
	 */
	// public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index);

	/**
	 * Simplify layout of current polylin asserting that two points are safelly removable. If a location is given, this location will be
	 * used to adapt position of previous and next point asserting that they must be located on an horizontal or vertical segment.
	 * 
	 * @param index
	 * @param newCPLocation
	 */
	// public void _simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(int index, FGEPoint newCPLocation);
}
