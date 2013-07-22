package org.openflexo.fge.connectors.impl;

import java.util.logging.Logger;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.RectPolylinConnectorSpecification;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEShape;

public class RectPolylinConnectorSpecificationImpl extends ConnectorSpecificationImpl implements RectPolylinConnectorSpecification {

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

	@Override
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

	@Override
	public boolean getStraightLineWhenPossible() {
		return straightLineWhenPossible;
	}

	@Override
	public void setStraightLineWhenPossible(boolean aFlag) {
		straightLineWhenPossible = aFlag;
		if (getGraphicalRepresentation() != null) {
			updateLayout();
			getGraphicalRepresentation().notifyConnectorChanged();
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

	@Override
	public SimplifiedCardinalDirection getEndOrientation() {
		return endOrientation;
	}

	@Override
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

	@Override
	public SimplifiedCardinalDirection getStartOrientation() {
		return startOrientation;
	}

	@Override
	public void setStartOrientation(SimplifiedCardinalDirection anOrientation) {
		if (anOrientation != startOrientation) {
			startOrientation = anOrientation;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public int getPixelOverlap() {
		return pixelOverlap;
	}

	@Override
	public void setPixelOverlap(int aPixelOverlap) {
		if (aPixelOverlap != pixelOverlap) {
			pixelOverlap = aPixelOverlap;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public boolean getIsRounded() {
		return isRounded;
	}

	@Override
	public void setIsRounded(boolean aFlag) {
		if (isRounded != aFlag) {
			isRounded = aFlag;
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public int getArcSize() {
		return arcSize;
	}

	@Override
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
	public boolean getIsStartingLocationFixed() {
		return isStartingLocationFixed;
	}

	@Override
	public void setIsStartingLocationFixed(boolean aFlag) {
		if (isStartingLocationFixed != aFlag) {
			isStartingLocationFixed = aFlag;
			if (isStartingLocationFixed && fixedStartLocationRelativeToStartObject == null && p_start != null) {
				// In this case, we can initialize fixed start location to its current value
				fixedStartLocationRelativeToStartObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), p_start.getPoint(),
						getStartObject());
			}
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public boolean getIsStartingLocationDraggable() {
		return isStartingLocationDraggable;
	}

	@Override
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

	@Override
	public boolean getIsEndingLocationFixed() {
		return isEndingLocationFixed;
	}

	@Override
	public void setIsEndingLocationFixed(boolean aFlag) {
		if (isEndingLocationFixed != aFlag) {
			isEndingLocationFixed = aFlag;
			if (isEndingLocationFixed && fixedEndLocationRelativeToEndObject == null && p_end != null) {
				// In this case, we can initialize fixed start location to its current value
				fixedEndLocationRelativeToEndObject = FGEUtils.convertNormalizedPoint(getGraphicalRepresentation(), p_end.getPoint(),
						getEndObject());
			}
			if (getGraphicalRepresentation() != null) {
				updateLayout();
				_rebuildControlPoints();
				getGraphicalRepresentation().notifyConnectorChanged();
			}
		}
	}

	@Override
	public boolean getIsEndingLocationDraggable() {
		return isEndingLocationDraggable;
	}

	@Override
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
	@Override
	public FGEPoint getFixedStartLocation() {
		if (!getIsStartingLocationFixed()) {
			return null;
		}
		if (fixedStartLocationRelativeToStartObject == null) {
			FGEPoint centerOfEndObjectSeenFromStartObject = FGEUtils.convertNormalizedPoint(getEndObject(), new FGEPoint(0.5, 0.5),
					getStartObject());
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
	@Override
	public void setFixedStartLocation(FGEPoint aPoint) {
		if (!isStartingLocationFixed && aPoint != null) {
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
	@Override
	public FGEPoint getFixedEndLocation() {
		if (!getIsEndingLocationFixed()) {
			return null;
		}
		if (fixedEndLocationRelativeToEndObject == null) {
			FGEPoint centerOfStartObjectSeenFromEndObject = FGEUtils.convertNormalizedPoint(getStartObject(), new FGEPoint(0.5, 0.5),
					getEndObject());
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
	@Override
	public void setFixedEndLocation(FGEPoint aPoint) {
		if (!isEndingLocationFixed && aPoint != null) {
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

	@Override
	public RectPolylinConnectorSpecification clone() {
		RectPolylinConnectorSpecification returned = new RectPolylinConnectorSpecificationImpl();
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
		returned._setPolylin(_getPolylin());
		return returned;
	}

}
