package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class RectPolylinConnectorSpecificationImpl extends ConnectorSpecificationImpl implements RectPolylinConnectorSpecification {

	static final Logger logger = Logger.getLogger(RectPolylinConnectorSpecification.class.getPackage().getName());

	private boolean straightLineWhenPossible = true;
	private RectPolylinAdjustability adjustability = RectPolylinAdjustability.AUTO_LAYOUT;

	private SimplifiedCardinalDirection startOrientation;
	private SimplifiedCardinalDirection endOrientation;

	private int pixelOverlap = FGEConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP; // overlap expressed in pixels relative to 1.0 scale

	private int arcSize = FGEConstants.DEFAULT_ROUNDED_RECT_POLYLIN_ARC_SIZE;
	private boolean isRounded = false;

	private boolean isStartingLocationFixed = false;
	private boolean isEndingLocationFixed = false;

	private boolean isStartingLocationDraggable = false;
	private boolean isEndingLocationDraggable = false;

	private FGEPoint crossedControlPoint = null;
	private FGERectPolylin polylin = null;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public RectPolylinConnectorSpecificationImpl() {
		super();
	}

	@Override
	public ConnectorType getConnectorType() {
		return ConnectorType.RECT_POLYLIN;
	}

	private RectPolylinConstraints rectPolylinConstraints = RectPolylinConstraints.NONE;

	@Override
	public RectPolylinConstraints getRectPolylinConstraints() {
		return rectPolylinConstraints;
	}

	@Override
	public void setRectPolylinConstraints(RectPolylinConstraints aRectPolylinConstraints) {
		FGEAttributeNotification notification = requireChange(RECT_POLYLIN_CONSTRAINTS, aRectPolylinConstraints);
		if (notification != null) {
			rectPolylinConstraints = aRectPolylinConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public void setRectPolylinConstraints(RectPolylinConstraints someRectPolylinConstraints, SimplifiedCardinalDirection aStartOrientation,
			SimplifiedCardinalDirection aEndOrientation) {
		if (someRectPolylinConstraints != rectPolylinConstraints || startOrientation != aStartOrientation
				|| endOrientation != aEndOrientation) {
			rectPolylinConstraints = someRectPolylinConstraints;
			startOrientation = aStartOrientation;
			endOrientation = aEndOrientation;
			setRectPolylinConstraints(someRectPolylinConstraints);
		}
	}

	@Override
	public boolean getStraightLineWhenPossible() {
		return straightLineWhenPossible;
	}

	@Override
	public void setStraightLineWhenPossible(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(STRAIGHT_LINE_WHEN_POSSIBLE, aFlag);
		if (notification != null) {
			straightLineWhenPossible = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsAdjustable() {
		return getAdjustability() == RectPolylinAdjustability.FULLY_ADJUSTABLE;
	}

	@Override
	public void setIsAdjustable(boolean aFlag) {
		setAdjustability(aFlag ? RectPolylinAdjustability.FULLY_ADJUSTABLE : RectPolylinAdjustability.AUTO_LAYOUT);
	}

	@Override
	public RectPolylinAdjustability getAdjustability() {
		return adjustability;
	}

	@Override
	public void setAdjustability(RectPolylinAdjustability anAdjustability) {
		FGEAttributeNotification notification = requireChange(ADJUSTABILITY, anAdjustability);
		if (notification != null) {
			adjustability = anAdjustability;
			hasChanged(notification);
		}
	}

	@Override
	public SimplifiedCardinalDirection getEndOrientation() {
		return endOrientation;
	}

	@Override
	public void setEndOrientation(SimplifiedCardinalDirection anOrientation) {
		FGEAttributeNotification notification = requireChange(END_ORIENTATION, anOrientation);
		if (notification != null) {
			endOrientation = anOrientation;
			hasChanged(notification);
		}
	}

	@Override
	public SimplifiedCardinalDirection getStartOrientation() {
		return startOrientation;
	}

	@Override
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
		FGEAttributeNotification notification = requireChange(START_ORIENTATION, anOrientation);
		if (notification != null) {
			startOrientation = anOrientation;
			hasChanged(notification);
		}
	}

	@Override
	public int getPixelOverlap() {
		return pixelOverlap;
	}

	@Override
	public void setPixelOverlap(int aPixelOverlap) {
		FGEAttributeNotification notification = requireChange(PIXEL_OVERLAP, aPixelOverlap);
		if (notification != null) {
			pixelOverlap = aPixelOverlap;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsRounded() {
		return isRounded;
	}

	@Override
	public void setIsRounded(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(IS_ROUNDED, aFlag);
		if (notification != null) {
			isRounded = aFlag;
			hasChanged(notification);
		}

	}

	@Override
	public int getArcSize() {
		return arcSize;
	}

	@Override
	public void setArcSize(int anArcSize) {
		FGEAttributeNotification notification = requireChange(ARC_SIZE, anArcSize);
		if (notification != null) {
			arcSize = anArcSize;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsStartingLocationFixed() {
		return isStartingLocationFixed;
	}

	@Override
	public void setIsStartingLocationFixed(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(IS_STARTING_LOCATION_FIXED, aFlag);
		if (notification != null) {
			isStartingLocationFixed = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsStartingLocationDraggable() {
		return isStartingLocationDraggable;
	}

	@Override
	public void setIsStartingLocationDraggable(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(IS_STARTING_LOCATION_DRAGGABLE, aFlag);
		if (notification != null) {
			isStartingLocationDraggable = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsEndingLocationFixed() {
		return isEndingLocationFixed;
	}

	@Override
	public void setIsEndingLocationFixed(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(IS_ENDING_LOCATION_FIXED, aFlag);
		if (notification != null) {
			isEndingLocationFixed = aFlag;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getIsEndingLocationDraggable() {
		return isEndingLocationDraggable;
	}

	@Override
	public void setIsEndingLocationDraggable(boolean aFlag) {
		FGEAttributeNotification notification = requireChange(IS_ENDING_LOCATION_DRAGGABLE, aFlag);
		if (notification != null) {
			isEndingLocationDraggable = aFlag;
			hasChanged(notification);
		}
	}

	private FGEPoint fixedStartLocationRelativeToStartObject;
	private FGEPoint fixedEndLocationRelativeToEndObject;

	/**
	 * Return start location asserting start location is fixed. Return position relative to start object (in the start-object coordinates
	 * system)
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getFixedStartLocation() {
		if (!getIsStartingLocationFixed()) {
			return null;
		}
		if (fixedStartLocationRelativeToStartObject == null) {
			fixedStartLocationRelativeToStartObject = new FGEPoint(0.9, 0.9);
			/*FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5),
					getStartObject());
			fixedStartLocationRelativeToStartObject = getStartObject().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
			if (fixedStartLocationRelativeToStartObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedStartLocationRelativeToStartObject = new FGEPoint(0.9, 0.9);
			}*/
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
	@Override
	public void setFixedStartLocation(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(FIXED_START_LOCATION, aPoint);
		if (notification != null) {
			if (!getIsStartingLocationFixed() && aPoint != null) {
				setIsStartingLocationFixed(true);
			}
			fixedStartLocationRelativeToStartObject = aPoint;
			hasChanged(notification);
		}
	}

	/**
	 * Return end location asserting end location is fixed. Return position relative to end object (in the end-object coordinates system)
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getFixedEndLocation() {
		if (!getIsEndingLocationFixed()) {
			return null;
		}
		if (fixedEndLocationRelativeToEndObject == null) {
			fixedEndLocationRelativeToEndObject = new FGEPoint(0.1, 0.1);
			/*FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5),
					getEndObject());
			fixedEndLocationRelativeToEndObject = getEndObject().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
			if (fixedEndLocationRelativeToEndObject == null) {
				logger.warning("outlineIntersect() returned null");
				fixedEndLocationRelativeToEndObject = new FGEPoint(0.1, 0.1);
			}*/
		}
		return fixedEndLocationRelativeToEndObject;
	}

	/**
	 * Sets end location asserting end location is fixed. Sets position relative to end object (in the end-object coordinates system)
	 * 
	 * @param aPoint
	 *            , relative to end object
	 */
	@Override
	public void setFixedEndLocation(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(FIXED_END_LOCATION, aPoint);
		if (notification != null) {
			if (!getIsEndingLocationFixed() && aPoint != null) {
				setIsEndingLocationFixed(true);
			}
			fixedEndLocationRelativeToEndObject = aPoint;
			hasChanged(notification);
		}
	}

	/*@Override
	public RectPolylinConnectorSpecification clone() {
		RectPolylinConnectorSpecification returned = (RectPolylinConnectorSpecificationImpl) cloneObject();
		returned.setRectPolylinConstraints(getRectPolylinConstraints());
		returned.setStraightLineWhenPossible(getStraightLineWhenPossible());
		returned.setAdjustability(getAdjustability());
		returned.setStartOrientation(getStartOrientation());
		returned.setEndOrientation(getEndOrientation());
		returned.setIsRounded(getIsRounded());
		returned.setArcSize(getArcSize());
		returned.setIsStartingLocationFixed(getIsStartingLocationFixed());
		if (getIsStartingLocationFixed()) {
			returned.setFixedStartLocation(getFixedStartLocation());
		}
		returned.setIsEndingLocationFixed(getIsEndingLocationFixed());
		if (getIsEndingLocationFixed()) {
			returned.setFixedEndLocation(getFixedEndLocation());
		}
		returned.setIsStartingLocationDraggable(getIsStartingLocationDraggable());
		returned.setIsEndingLocationDraggable(getIsEndingLocationDraggable());
		returned.setCrossedControlPoint(getCrossedControlPoint());
		returned.setPolylin(getPolylin());
		return returned;
	}*/

	@Override
	public RectPolylinConnector makeConnector(ConnectorNode<?> connectorNode) {
		RectPolylinConnector returned = new RectPolylinConnector(connectorNode);
		getPropertyChangeSupport().addPropertyChangeListener(returned);
		return returned;
	}

	@Override
	public FGEPoint getCrossedControlPoint() {
		return crossedControlPoint;
	}

	@Override
	public void setCrossedControlPoint(FGEPoint aPoint) {
		FGEAttributeNotification notification = requireChange(CROSSED_CONTROL_POINT, aPoint);
		if (notification != null) {
			crossedControlPoint = aPoint;
			hasChanged(notification);
		}
	}

	@Override
	public FGERectPolylin getPolylin() {
		return polylin;
	}

	@Override
	public void setPolylin(FGERectPolylin aPolylin) {
		FGEAttributeNotification notification = requireChange(POLYLIN, aPolylin);
		if (notification != null) {
			polylin = aPolylin;
			hasChanged(notification);
		}
	}

}
