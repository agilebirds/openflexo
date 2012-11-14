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
import org.openflexo.fge.geom.FGEArc;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.ParallelLinesException;
import org.openflexo.fge.graphics.FGEGraphics;

public class FGEHalfBand implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEHalfBand.class.getPackage().getName());

	// Storage data
	public FGEHalfLine halfLine1;
	public FGEHalfLine halfLine2;

	// Computed data
	public FGESegment limit;
	public FGEHalfPlane halfPlane;
	public FGEBand embeddingBand;

	public FGEHalfBand() {
		super();
	}

	public FGEHalfBand(FGEHalfLine anHalfLine1, FGEHalfLine anHalfLine2) {
		super();
		this.halfLine1 = anHalfLine1;
		this.halfLine2 = anHalfLine2;
		updateParametersFromHalflines();
	}

	public FGEHalfBand(FGELine line1, FGELine line2, FGEHalfPlane hp) {
		this((FGEHalfLine) hp.intersect(line1), (FGEHalfLine) hp.intersect(line2));
	}

	public FGEHalfBand(FGESegment segment, SimplifiedCardinalDirection orientation) {
		this(segment.getP1().getOrthogonalPerspectiveArea(orientation), segment.getP2().getOrthogonalPerspectiveArea(orientation));
	}

	private void updateParametersFromHalflines() {
		if (halfLine1.overlap(halfLine2)) {
			throw new IllegalArgumentException("lines are overlaping halfLine1=" + halfLine1 + " halfLine2=" + halfLine2);
		}
		if (!halfLine1.isParallelTo(halfLine2)) {
			throw new IllegalArgumentException("lines are not parallel: " + halfLine1 + " and " + halfLine2);
		}
		limit = new FGESegment(halfLine1.getLimit(), halfLine2.getLimit());
		halfPlane = new FGEHalfPlane(new FGELine(limit), halfLine1.getOpposite());
		if (!halfPlane.containsPoint(halfLine2.getOpposite())) {
			throw new IllegalArgumentException("half lines have opposite directions");
		}
		embeddingBand = new FGEBand(new FGELine(halfLine1), new FGELine(halfLine2));
	}

	public static void main(String[] args) {
		FGEHalfLine hl1 = new FGEHalfLine(new FGEPoint(1, 2), new FGEPoint(4, 2));
		FGEHalfLine hl2 = new FGEHalfLine(new FGEPoint(0, 1), new FGEPoint(8, 1));
		FGEHalfBand test = new FGEHalfBand(hl1, hl2);
		System.out.println("test=" + test);
		FGEPoint p = new FGEPoint(5, 1.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		p = new FGEPoint(-0.5, 1.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		p = new FGEPoint(5, 2.5);
		System.out.println("contains " + p + " = " + test.containsPoint(p));
		AffineTransform at = AffineTransform.getScaleInstance(100, 200);
		System.out.println("test2=" + test.transform(at));
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		if (!embeddingBand.containsPoint(p)) {
			return false;
		}
		return halfPlane.containsPoint(p);
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (!embeddingBand.containsLine(l)) {
			return false;
		}
		return halfPlane.containsLine(l);
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
	public FGEHalfBand clone() {
		try {
			return (FGEHalfBand) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		FGEHalfLine l1 = halfLine1.transform(t);
		FGEHalfLine l2 = halfLine2.transform(t);
		if (l1.overlap(l2)) {
			return l1.intersect(l2);
		} else {
			return new FGEHalfBand(l1, l2);
		}
	}

	@Override
	public String toString() {
		return "FGEHalfBand: " + halfLine1.toString() + " " + halfLine2.toString();
	}

	/*public void paint(FGEGraphics g)
	{
		super.paint(g);
		halfLine1.paint(g);
		halfLine2.paint(g);
		limit.paint(g);
	}*/

	@Override
	public void paint(FGEGraphics g) {
		// System.out.println("paint() for "+this);
		FGERectangle bounds = g.getGraphicalRepresentation().getNormalizedBounds();
		FGEArea l1 = bounds.intersect(halfLine1);
		FGEArea l2 = bounds.intersect(halfLine2);

		// System.out.println("l1="+l1);
		// System.out.println("l2="+l2);
		if (l1 instanceof FGESegment && l2 instanceof FGESegment) {
			FGESegment s1 = (FGESegment) l1;
			FGESegment s2 = (FGESegment) l2;
			g.useDefaultBackgroundStyle();
			if (new FGESegment(s1.getP1(), s2.getP1()).intersectsInsideSegment(new FGESegment(s1.getP2(), s2.getP2()))) {
				FGEPolygon.makeArea(Filling.FILLED, s1.getP1(), s1.getP2(), s2.getP1(), s2.getP2()).paint(g);
			} else {
				FGEPolygon.makeArea(Filling.FILLED, s1.getP1(), s1.getP2(), s2.getP2(), s2.getP1()).paint(g);
			}
		}

		g.useDefaultForegroundStyle();
		halfLine1.paint(g);
		halfLine2.paint(g);
		limit.paint(g);
	}

	protected FGEArea computeBandIntersection(FGEBand band) {
		FGEArea returned = embeddingBand.computeBandIntersection(band);
		FGEArea reply = returned.intersect(halfPlane);
		return reply;
	}

	protected FGEArea computeHalfBandIntersection(FGEHalfBand band) {
		FGEArea returned = embeddingBand.computeHalfBandIntersection(band);
		FGEArea reply = returned.intersect(halfPlane);
		return reply;
	}

	private FGEArea computeLineIntersection(FGEAbstractLine aLine) {
		FGEArea returned = embeddingBand.intersect(aLine);
		return returned.intersect(halfPlane);
	}

	protected FGEArea computeHalfPlaneIntersection(FGEHalfPlane anHalfPlane) {
		// FGEArea returned = embeddingBand.intersect(anHalfPlane);
		// return returned.intersect(halfPlane);

		if (anHalfPlane.containsArea(this)) {
			return clone();
		}

		if (anHalfPlane.line.isParallelTo(halfLine1)) {
			if (anHalfPlane.containsLine(halfLine1)) {
				if (anHalfPlane.containsLine(halfLine2)) {
					return clone();
				} else {
					FGEPoint inters;
					try {
						inters = anHalfPlane.line.getLineIntersection(limit);
						// We must project opposite pt on line, parallel to limit
						FGELine l = halfLine1.getParallelLine(inters);
						FGELine l2 = limit.getParallelLine(halfLine1.getOpposite());
						FGEPoint opp = l.getLineIntersection(l2);
						return new FGEHalfBand(halfLine1, new FGEHalfLine(inters, opp));
					} catch (ParallelLinesException e) {
						// cannot happen
						e.printStackTrace();
						return null;
					} catch (IllegalArgumentException e) {
						logger.warning("IllegalArgumentException raised while computing intersection of " + this + " and " + anHalfPlane);
						e.printStackTrace();
						return new FGEEmptyArea();
					}
				}
			} else {
				if (anHalfPlane.containsLine(halfLine2)) {
					try {
						FGEPoint inters = anHalfPlane.line.getLineIntersection(limit);

						// We must project opposite pt on line, parallel to limit
						FGELine l = halfLine2.getParallelLine(inters);
						FGELine l2 = limit.getParallelLine(halfLine2.getOpposite());
						FGEPoint opp = l.getLineIntersection(l2);
						return new FGEHalfBand(halfLine2, new FGEHalfLine(inters, opp));

						// TODO: this is not exact, we must project opposite pt on line, parallel to limit
						/*logger.warning("Please implement this better");
						FGEPoint opp = halfLine2.getProjection(halfLine2.getOpposite());
						return new FGEHalfBand(halfLine2,new FGEHalfLine(inters,opp));*/
					} catch (ParallelLinesException e) {
						// cannot happen
						e.printStackTrace();
						return null;
					}
				} else {
					return new FGEEmptyArea();
				}
			}
		}

		FGEArea returned = embeddingBand.computeHalfPlaneIntersection(anHalfPlane);

		if (returned instanceof FGEHalfBand) {
			// we must now intersect with halfplane
			FGEHalfBand hb = (FGEHalfBand) returned;
			FGEPoint pp1 = hb.halfLine1.getLineIntersection(halfPlane.line);
			FGEPoint pp2 = hb.halfLine2.getLineIntersection(halfPlane.line);
			if (hb.halfLine1.containsPoint(pp1) && hb.halfLine2.containsPoint(pp2)) {
				FGEArea returnThis = FGEPolygon.makeArea(Filling.FILLED, hb.halfLine1.getLimit(), hb.halfLine2.getLimit(), pp2, pp1);
				return returnThis;
			} else if (hb.halfLine1.containsPoint(pp1) || hb.halfLine2.containsPoint(pp2)) {
				return new FGEIntersectionArea(returned, this);
			} else {
				// No intersection, we have all or nothing
				if (halfPlane.containsLine(hb.halfLine1) && halfPlane.containsLine(hb.halfLine2)) {
					return hb;
				}
				return new FGEEmptyArea();
			}
		}

		return returned;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEHalfBand) {
			FGEHalfBand hb = (FGEHalfBand) obj;
			return hb.halfLine1.equals(halfLine1) && hb.halfLine2.equals(halfLine2) || hb.halfLine2.equals(halfLine1)
					&& hb.halfLine1.equals(halfLine2);
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
		if (a instanceof FGEHalfBand) {
			return containsLine(((FGEHalfBand) a).halfLine1) && containsLine(((FGEHalfBand) a).halfLine2);
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
		if (area instanceof FGEAbstractLine) {
			return computeLineIntersection((FGEAbstractLine) area);
		}
		if (area instanceof FGERectangle) {
			return area.intersect(this);
		}
		if (area instanceof FGEPolygon) {
			return area.intersect(this);
		}
		if (area instanceof FGEArc) {
			return area.intersect(this);
		}
		if (area instanceof FGEBand) {
			return computeBandIntersection((FGEBand) area);
		}
		if (area instanceof FGEHalfBand) {
			return computeHalfBandIntersection((FGEHalfBand) area);
		}
		if (area instanceof FGEHalfPlane) {
			return computeHalfPlaneIntersection((FGEHalfPlane) area);
		}

		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
		}
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		} else {
			return FGEPoint.getNearestPoint(aPoint, halfLine1.getNearestPoint(aPoint), halfLine2.getNearestPoint(aPoint));
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
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (halfLine1.isVertical()) {
				return clone();
			} else {
				FGEPoint p = limit.getP1().y > limit.getP2().y ? limit.getP1() : limit.getP2();
				return new FGEHalfPlane(FGELine.makeVerticalLine(p), halfLine1.getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (halfLine1.isVertical()) {
				return clone();
			} else {
				FGEPoint p = limit.getP1().y > limit.getP2().y ? limit.getP2() : limit.getP1();
				return new FGEHalfPlane(FGELine.makeVerticalLine(p), halfLine1.getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (halfLine1.isHorizontal()) {
				return clone();
			} else {
				FGEPoint p = limit.getP1().x > limit.getP2().x ? limit.getP2() : limit.getP1();
				return new FGEHalfPlane(FGELine.makeHorizontalLine(p), halfLine1.getOpposite());
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (halfLine1.isHorizontal()) {
				return clone();
			} else {
				FGEPoint p = limit.getP1().x > limit.getP2().x ? limit.getP1() : limit.getP2();
				return new FGEHalfPlane(FGELine.makeHorizontalLine(p), halfLine1.getOpposite());
			}
		}
		return null;
	}

	@Override
	public FGEHalfBand getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
