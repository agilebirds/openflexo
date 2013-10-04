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
package org.openflexo.fge.geom;

import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEPlane;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGELine extends FGEAbstractLine<FGELine> {

	private static final Logger logger = Logger.getLogger(FGELine.class.getPackage().getName());

	public FGELine(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public FGELine(FGEPoint p1, FGEPoint p2) {
		super(p1, p2);
	}

	public FGELine(FGEAbstractLine line) {
		super(line.getP1(), line.getP2());
	}

	public FGELine() {
		super();
	}

	public FGELine(double pA, double pB, double pC) {
		super(pA, pB, pC);
	}

	public FGELine(FGESegment s) {
		this(s.getP1(), s.getP2());
	}

	public FGELine(FGEHalfLine hl) {
		this(hl.getP1(), hl.getP2());
	}

	/**
	 * Build vertical line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static FGELine makeVerticalLine(FGEPoint pt, double distance) {
		return new FGELine(pt.x, pt.y, pt.x, pt.y + distance);
	}

	/**
	 * Build vertical line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static FGELine makeVerticalLine(FGEPoint pt) {
		return makeVerticalLine(pt, 1);
	}

	/**
	 * Build horizontal line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static FGELine makeHorizontalLine(FGEPoint pt, double distance) {
		return new FGELine(pt.x, pt.y, pt.x + distance, pt.y);
	}

	/**
	 * Build horizontal line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static FGELine makeHorizontalLine(FGEPoint pt) {
		return makeHorizontalLine(pt, 1);
	}

	@Override
	public boolean contains(FGEPoint p) {
		return _containsPointIgnoreBounds(p);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint p) {
		return getProjection(p);
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
	public FGELine clone() {
		return (FGELine) super.clone();
	}

	private static FGEArea _compute_hl_hl_Intersection(FGEHalfLine hl1, FGEHalfLine hl2) {
		FGEHalfPlane hp1 = hl1.getHalfPlane();
		FGEHalfPlane hp2 = hl2.getHalfPlane();

		System.out.println(">>>>>>>>>>>>>> HERE");

		FGEArea intersect = hp1.intersect(hp2);

		if (intersect instanceof FGEEmptyArea) {
			return new FGEEmptyArea();
		} else if (intersect instanceof FGEBand) {
			return new FGESegment(hl1.getLimit(), hl2.getLimit());
		}

		else if (intersect.equals(hp1)) {
			return hl1.clone();
		} else if (intersect.equals(hp2)) {
			return hl2.clone();
		} else {
			logger.warning("Unexpected intersection: " + intersect);
			return null;
		}
	}

	@Override
	protected FGEArea computeLineIntersection(FGEAbstractLine line) {
		logger.info("computeIntersection() between " + this + "\n and " + line + " overlap=" + overlap(line));
		if (overlap(line)) {
			return line.clone();
		} else if (isParallelTo(line)) {
			return new FGEEmptyArea();
		} else {
			FGEPoint returned;
			try {
				returned = getLineIntersection(line);
				if (containsPoint(returned) && line.containsPoint(returned)) {
					return returned;
				}
			} catch (ParallelLinesException e) {
				// cannot happen
			}
			return new FGEEmptyArea();
		}
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (!overlap(l)) {
			return false;
		}

		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGELine) {
			return overlap((FGELine) obj);
		}
		return false;
	}

	@Override
	public FGELine transform(AffineTransform t) {
		return new FGELine(getP1().transform(t), getP2().transform(t));
	}

	@Override
	public String toString() {
		return "FGELine: (" + getP1() + "," + getP2() + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		FGERectangle bounds = g.getNodeNormalizedBounds();
		bounds.intersect(this).paint(g);
		// logger.info("Paint bounds="+bounds+": "+bounds.intersect(this));
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new FGEPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new FGEPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new FGEPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new FGEPlane();
			}
		}
		return null;
	}

	@Override
	public FGELine getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
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

}
