/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
import org.openflexo.fge.GRParameter;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
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
@XMLElement(xmlTag = "RectPolylinConnectorSpecification")
public interface RectPolylinConnectorSpecification extends ConnectorSpecification {

	// Property keys

	@PropertyIdentifier(type = RectPolylinConstraints.class)
	public static final String RECT_POLYLIN_CONSTRAINTS_KEY = "rectPolylinConstraints";
	@PropertyIdentifier(type = Boolean.class)
	public static final String STRAIGHT_LINE_WHEN_POSSIBLE_KEY = "straightLineWhenPossible";
	@PropertyIdentifier(type = RectPolylinAdjustability.class)
	public static final String ADJUSTABILITY_KEY = "adjustability";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String START_ORIENTATION_KEY = "startOrientation";
	@PropertyIdentifier(type = SimplifiedCardinalDirection.class)
	public static final String END_ORIENTATION_KEY = "endOrientation";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ROUNDED_KEY = "isRounded";
	@PropertyIdentifier(type = Integer.class)
	public static final String ARC_SIZE_KEY = "arcSize";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_STARTING_LOCATION_FIXED_KEY = "isStartingLocationFixed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ENDING_LOCATION_FIXED_KEY = "isEndingLocationFixed";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_STARTING_LOCATION_DRAGGABLE_KEY = "isStartingLocationDraggable";
	@PropertyIdentifier(type = Boolean.class)
	public static final String IS_ENDING_LOCATION_DRAGGABLE_KEY = "isEndingLocationDraggable";
	@PropertyIdentifier(type = FGEPoint.class)
	public static final String CROSSED_CONTROL_POINT_KEY = "crossedControlPoint";
	@PropertyIdentifier(type = FGEPoint.class)
	public static final String FIXED_START_LOCATION_KEY = "fixedStartLocation";
	@PropertyIdentifier(type = FGEPoint.class)
	public static final String FIXED_END_LOCATION_KEY = "fixedEndLocation";
	@PropertyIdentifier(type = FGERectPolylin.class)
	public static final String POLYLIN_KEY = "polylin";
	@PropertyIdentifier(type = Integer.class)
	public static final String PIXEL_OVERLAP_KEY = "pixelOverlap";

	public static GRParameter<RectPolylinConstraints> RECT_POLYLIN_CONSTRAINTS = GRParameter.getGRParameter(
			RectPolylinConnectorSpecification.class, RECT_POLYLIN_CONSTRAINTS_KEY, RectPolylinConstraints.class);
	public static GRParameter<Boolean> STRAIGHT_LINE_WHEN_POSSIBLE = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			STRAIGHT_LINE_WHEN_POSSIBLE_KEY, Boolean.class);
	public static GRParameter<RectPolylinAdjustability> ADJUSTABILITY = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			ADJUSTABILITY_KEY, RectPolylinAdjustability.class);
	public static GRParameter<SimplifiedCardinalDirection> START_ORIENTATION = GRParameter.getGRParameter(
			RectPolylinConnectorSpecification.class, START_ORIENTATION_KEY, SimplifiedCardinalDirection.class);
	public static GRParameter<SimplifiedCardinalDirection> END_ORIENTATION = GRParameter.getGRParameter(
			RectPolylinConnectorSpecification.class, END_ORIENTATION_KEY, SimplifiedCardinalDirection.class);
	public static GRParameter<Boolean> IS_ROUNDED = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class, IS_ROUNDED_KEY,
			Boolean.class);
	public static GRParameter<Integer> ARC_SIZE = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class, ARC_SIZE_KEY,
			Integer.class);
	public static GRParameter<Boolean> IS_STARTING_LOCATION_FIXED = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_STARTING_LOCATION_FIXED_KEY, Boolean.class);
	public static GRParameter<Boolean> IS_ENDING_LOCATION_FIXED = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_ENDING_LOCATION_FIXED_KEY, Boolean.class);
	public static GRParameter<Boolean> IS_STARTING_LOCATION_DRAGGABLE = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_STARTING_LOCATION_DRAGGABLE_KEY, Boolean.class);
	public static GRParameter<Boolean> IS_ENDING_LOCATION_DRAGGABLE = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			IS_ENDING_LOCATION_DRAGGABLE_KEY, Boolean.class);
	public static GRParameter<FGEPoint> CROSSED_CONTROL_POINT = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			CROSSED_CONTROL_POINT_KEY, FGEPoint.class);
	public static GRParameter<FGEPoint> FIXED_START_LOCATION = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			FIXED_START_LOCATION_KEY, FGEPoint.class);
	public static GRParameter<FGEPoint> FIXED_END_LOCATION = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			FIXED_END_LOCATION_KEY, FGEPoint.class);
	public static GRParameter<FGERectPolylin> POLYLIN = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class, POLYLIN_KEY,
			FGERectPolylin.class);
	public static GRParameter<Integer> PIXEL_OVERLAP = GRParameter.getGRParameter(RectPolylinConnectorSpecification.class,
			PIXEL_OVERLAP_KEY, Integer.class);

	/*public static enum RectPolylinConnectorParameters implements GRParameter {
		rectPolylinConstraints,
		straightLineWhenPossible,
		adjustability,
		startOrientation,
		endOrientation,
		isRounded,
		arcSize,
		isStartingLocationFixed,
		isEndingLocationFixed,
		isStartingLocationDraggable,
		isEndingLocationDraggable,
		crossedControlPoint,
		fixedStartLocation,
		fixedEndLocation,
		polylin,
		pixelOverlap
	}*/

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

	@Getter(value = RECT_POLYLIN_CONSTRAINTS_KEY)
	@XMLAttribute
	public RectPolylinConstraints getRectPolylinConstraints();

	@Setter(value = RECT_POLYLIN_CONSTRAINTS_KEY)
	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints);

	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation);

	@Getter(value = STRAIGHT_LINE_WHEN_POSSIBLE_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getStraightLineWhenPossible();

	@Setter(value = STRAIGHT_LINE_WHEN_POSSIBLE_KEY)
	public void setStraightLineWhenPossible(boolean aFlag);

	@Getter(value = ADJUSTABILITY_KEY)
	@XMLAttribute
	public RectPolylinAdjustability getAdjustability();

	@Setter(value = ADJUSTABILITY_KEY)
	public void setAdjustability(RectPolylinAdjustability anAdjustability);

	@Getter(value = END_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getEndOrientation();

	@Setter(value = END_ORIENTATION_KEY)
	public void setEndOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = START_ORIENTATION_KEY)
	@XMLAttribute
	public SimplifiedCardinalDirection getStartOrientation();

	@Setter(value = START_ORIENTATION_KEY)
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation);

	@Getter(value = IS_ROUNDED_KEY, defaultValue = "true")
	@XMLAttribute
	public boolean getIsRounded();

	@Setter(value = IS_ROUNDED_KEY)
	public void setIsRounded(boolean aFlag);

	@Getter(value = ARC_SIZE_KEY, defaultValue = "10")
	@XMLAttribute
	public int getArcSize();

	@Setter(value = ARC_SIZE_KEY)
	public void setArcSize(int anArcSize);

	@Getter(value = IS_STARTING_LOCATION_FIXED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationFixed();

	@Setter(value = IS_STARTING_LOCATION_FIXED_KEY)
	public void setIsStartingLocationFixed(boolean aFlag);

	@Getter(value = IS_STARTING_LOCATION_DRAGGABLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsStartingLocationDraggable();

	@Setter(value = IS_STARTING_LOCATION_DRAGGABLE_KEY)
	public void setIsStartingLocationDraggable(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_FIXED_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationFixed();

	@Setter(value = IS_ENDING_LOCATION_FIXED_KEY)
	public void setIsEndingLocationFixed(boolean aFlag);

	@Getter(value = IS_ENDING_LOCATION_DRAGGABLE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getIsEndingLocationDraggable();

	@Setter(value = IS_ENDING_LOCATION_DRAGGABLE_KEY)
	public void setIsEndingLocationDraggable(boolean aFlag);

	@Getter(value = CROSSED_CONTROL_POINT_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getCrossedControlPoint();

	@Setter(value = CROSSED_CONTROL_POINT_KEY)
	public void setCrossedControlPoint(FGEPoint aPoint);

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_START_LOCATION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getFixedStartLocation();

	/**
	 * Sets start location asserting start location is fixed. Sets position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @param aPoint
	 *            : relative to start object
	 */
	@Setter(value = FIXED_START_LOCATION_KEY)
	public void setFixedStartLocation(FGEPoint aPoint);

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	@Getter(value = FIXED_END_LOCATION_KEY, isStringConvertable = true)
	@XMLAttribute
	public FGEPoint getFixedEndLocation();

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	@Setter(value = FIXED_END_LOCATION_KEY)
	public void setFixedEndLocation(FGEPoint aPoint);

	@Getter(value = PIXEL_OVERLAP_KEY, defaultValue = "" + FGEConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP)
	@XMLAttribute
	public int getPixelOverlap();

	@Setter(value = PIXEL_OVERLAP_KEY)
	public void setPixelOverlap(int aPixelOverlap);

	@Getter(value = POLYLIN_KEY, ignoreType = true)
	public FGERectPolylin getPolylin();

	@Setter(value = POLYLIN_KEY)
	public void setPolylin(FGERectPolylin aPolylin);

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
