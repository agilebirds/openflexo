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
package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalQuadrant;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEQuarterPlane implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEQuarterPlane.class.getPackage().getName());

	public FGEHalfPlane halfPlane1;
	public FGEHalfPlane halfPlane2;

	public static FGEQuarterPlane makeFGEQuarterPlane(FGEPoint point, CardinalQuadrant quadrant) {
		if (quadrant == CardinalQuadrant.NORTH_EAST) {
			return new FGEQuarterPlane(FGEHalfPlane.makeFGEHalfPlane(point, CardinalDirection.NORTH), FGEHalfPlane.makeFGEHalfPlane(point,
					CardinalDirection.EAST));
		} else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			return new FGEQuarterPlane(FGEHalfPlane.makeFGEHalfPlane(point, CardinalDirection.NORTH), FGEHalfPlane.makeFGEHalfPlane(point,
					CardinalDirection.WEST));
		} else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			return new FGEQuarterPlane(FGEHalfPlane.makeFGEHalfPlane(point, CardinalDirection.SOUTH), FGEHalfPlane.makeFGEHalfPlane(point,
					CardinalDirection.EAST));
		} else /* quadrant == CardinalQuadrant.SOUTH_WEST */{
			return new FGEQuarterPlane(FGEHalfPlane.makeFGEHalfPlane(point, CardinalDirection.SOUTH), FGEHalfPlane.makeFGEHalfPlane(point,
					CardinalDirection.WEST));
		}
	}

	public FGEQuarterPlane(FGEHalfPlane anHalfPlane1, FGEHalfPlane anHalfPlane2) {
		super();
		halfPlane1 = anHalfPlane1;
		halfPlane2 = anHalfPlane2;
		if (halfPlane1.line.isParallelTo(halfPlane2.line)) {
			throw new IllegalArgumentException("lines are parallel");
		}
	}

	public static void main(String[] args) {
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return halfPlane1.containsPoint(p) && halfPlane2.containsPoint(p);
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since 1.2
	 */
	@Override
	public FGEQuarterPlane clone() {
		try {
			return (FGEQuarterPlane) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEQuarterPlane transform(AffineTransform t) {
		return new FGEQuarterPlane(halfPlane1.transform(t), halfPlane2.transform(t));
	}

	@Override
	public String toString() {
		return "FGEQuarterPlane: " + halfPlane1.toString() + " " + halfPlane2.toString();
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		// TODO
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof FGEHalfLine) {
			// TODO
			logger.warning("Not implemented yet");
			return false;
		}
		if (l instanceof FGESegment) {
			return true;
		}
		return false;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGELine) {
			return containsLine((FGELine) a);
		}
		if (a instanceof FGEShape) {
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}

		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
		}
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		return new FGEUnionArea(this, area);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		}
		return FGEPoint.getNearestPoint(aPoint, halfPlane1.line.getProjection(aPoint), halfPlane2.line.getProjection(aPoint));
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return this;
	}

	/**
	 * This area is infinite, so always return false
	 */
	@Override
	public final boolean isFinite() {
		return false;
	}

	/**
	 * This area is infinite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		return null;
	}

	/**
	 * Return nearest point from point "from" following supplied orientation
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param from
	 *            point from which we are coming to area
	 * @param orientation
	 *            orientation we are coming from
	 * @return
	 */
	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);
		FGEArea intersect = intersect(hl);
		return intersect.nearestPointFrom(from, orientation);
	}

}
