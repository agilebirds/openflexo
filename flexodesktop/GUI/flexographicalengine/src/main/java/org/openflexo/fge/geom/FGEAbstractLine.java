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
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEGraphics;

public abstract class FGEAbstractLine<L extends FGEAbstractLine> extends Line2D.Double implements FGEGeometricObject<L> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FGEAbstractLine.class.getPackage().getName());

	// Equation is a*x + b*y + c = 0
	private double a;
	private double b;
	private double c;

	private FGEPoint p1;
	private FGEPoint p2;

	@SuppressWarnings("unchecked")
	@Override
	public FGEAbstractLine<L> clone() {
		return (FGEAbstractLine<L>) super.clone();
	}

	public FGEAbstractLine(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public FGEAbstractLine(FGEPoint p1, FGEPoint p2) {
		super(p1, p2);
	}

	public FGEAbstractLine(FGEAbstractLine line) {
		super(line.getP1(), line.getP2());
	}

	public FGEAbstractLine() {
		super();
	}

	public FGEAbstractLine(double pA, double pB, double pC) {
		super();

		if (pB != 0) {
			FGEPoint p1 = new FGEPoint(0, -pC / pB);
			FGEPoint p2 = new FGEPoint(1, -(pA + pC) / pB);
			setLine(p1, p2);
		} else {
			FGEPoint p1 = new FGEPoint(-pC / pA, 0);
			FGEPoint p2 = new FGEPoint(-pC / pA, 1);
			setLine(p1, p2);
		}
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(getP1());
		returned.add(getP2());
		return returned;
	}

	@Override
	public FGEPoint getP1() {
		return p1;
		/*
		 * Point2D returned = super.getP1(); return new FGEPoint(returned.getX(),returned.getY());
		 */
	}

	public void setP1(FGEPoint p1) {
		setX1(p1.x);
		setY1(p1.y);
	}

	@Override
	public FGEPoint getP2() {
		return p2;
		/*
		 * Point2D returned = super.getP2(); return new FGEPoint(returned.getX(),returned.getY());
		 */
	}

	public void setP2(FGEPoint p2) {
		setX2(p2.x);
		setY2(p2.y);
	}

	@Override
	public final void setLine(double X1, double Y1, double X2, double Y2) {
		super.setLine(X1, Y1, X2, Y2);
		updateCoeffs();
		// System.out.println("hop: "+(a*X1+b*Y1+c)+" et "+(a*X2+b*Y2+c));
	}

	public void setX1(double x1) {
		this.x1 = x1;
		updateCoeffs();
	}

	public void setX2(double x2) {
		this.x2 = x2;
		updateCoeffs();
	}

	public void setY1(double y1) {
		this.y1 = y1;
		updateCoeffs();
	}

	public void setY2(double y2) {
		this.y2 = y2;
		updateCoeffs();
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}

	private void updateCoeffs() {
		if (FGEUtils.doubleEquals(x1, x2) && FGEUtils.doubleEquals(y1, y2)) {
			// if (x1==x2 && y1==y2) {
			a = 0;
			b = 0;
			c = 0;
		}

		if (!FGEUtils.doubleEquals(x1, x2)) {
			// if (x1 != x2) {
			b = 1;
			a = (y2 - y1) / (x1 - x2);
			c = -(a * x1 + y1);
		} else {
			b = 0;
			a = 1;
			c = -x1;
		}
		p1 = new FGEPoint(x1, y1);
		p2 = new FGEPoint(x2, y2);
	}

	public final boolean overlap(FGEAbstractLine anOtherLine) {
		return isParallelTo(anOtherLine) && anOtherLine._containsPointIgnoreBounds(getP1());
	}

	public static FGEPoint getOppositePoint(FGEPoint p, FGEPoint pivot) {
		return FGEPoint.getOppositePoint(p, pivot);
	}

	public static FGEPoint getMiddlePoint(FGEPoint p1, FGEPoint p2) {
		return FGEPoint.getMiddlePoint(p1, p2);
	}

	/**
	 * Return a new line orthogonal to this instance and crossing supplied point
	 * 
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public FGELine getOrthogonalLine(FGEPoint p) {
		double a1 = -b;
		double b1 = a;
		double c1 = -(a1 * p.x + b1 * p.y);
		return new FGELine(a1, b1, c1);
	}

	/**
	 * Return a new line orthogonal to reference line and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public static FGELine getOrthogonalLine(FGEAbstractLine reference, FGEPoint p) {
		return reference.getOrthogonalLine(p);
	}

	/**
	 * Return a new line parallel to this instance and crossing supplied point
	 * 
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public FGELine getParallelLine(FGEPoint p) {
		double a1 = a;
		double b1 = b;
		double c1 = -(a1 * p.x + b1 * p.y);
		return new FGELine(a1, b1, c1);
	}

	/**
	 * Return a new line parallel to reference line and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public static FGELine getParallelLine(FGEAbstractLine reference, FGEPoint p) {
		return reference.getParallelLine(p);
	}

	/**
	 * Return a new line obtained by rotation of supplied angle (given in degree) and crossing supplied point
	 * 
	 * @param angle
	 *            (in degree)
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public FGELine getRotatedLine(double angle, FGEPoint p) {
		FGEAbstractLine l = transform(AffineTransform.getRotateInstance(Math.toRadians(angle)));
		return l.getParallelLine(p);
	}

	/**
	 * Return a new line obtained by rotation of supplied angle (given in degree) and crossing supplied point
	 * 
	 * @param reference
	 *            reference line
	 * @param angle
	 *            (in degree)
	 * @param p
	 *            point the returned line must cross
	 * @return a new FGELine instance
	 */
	public static FGELine getRotatedLine(FGEAbstractLine reference, double angle, FGEPoint p) {
		return reference.getRotatedLine(angle, p);
	}

	public FGEPoint getLineIntersection(FGEAbstractLine aLine) throws ParallelLinesException {
		double a1 = aLine.a;
		double b1 = aLine.b;
		double c1 = aLine.c;

		double det = a1 * b - a * b1;

		if (Math.abs(det) < EPSILON) { // parallel lines
			throw new ParallelLinesException();
		} else {
			return new FGEPoint(-(b * c1 - b1 * c) / det, (a * c1 - a1 * c) / det);
		}
	}

	public boolean isParallelTo(FGEAbstractLine<?> aLine) {
		double a1 = aLine.a;
		double b1 = aLine.b;

		double det = a1 * b - a * b1;

		if (Math.abs(det) < EPSILON) { // parallel lines
			return true;
		} else {
			return false;
		}
	}

	public boolean isOrthogonalTo(FGEAbstractLine<?> aLine) {
		double a1 = aLine.a;
		double b1 = aLine.b;

		if (Math.abs(a * a1 - b * b1) < EPSILON) { // orthogonal lines
			return true;
		} else {
			return false;
		}
	}

	public static FGEPoint getLineIntersection(FGEAbstractLine line1, FGEAbstractLine line2) throws ParallelLinesException {
		return line1.getLineIntersection(line2);
	}

	public abstract boolean contains(FGEPoint p);

	protected final boolean _containsPointIgnoreBounds(FGEPoint p) {
		return Math.abs(a * p.x + b * p.y + c) < EPSILON;
	}

	public int getPlaneLocation(FGEPoint p) {
		double k = a * p.x + b * p.y + c;
		if (Math.abs(k) < EPSILON) {
			return 0; // We are on line
		}
		if (k > 0) {
			return 1; // We are on one side
		} else {
			return -1; // We are on the other side
		}
	}

	public FGEPoint getProjection(FGEPoint p) {
		if (contains(p)) {
			return new FGEPoint(p);
		}
		try {
			return getOrthogonalLine(p).getLineIntersection(this);
		} catch (ParallelLinesException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	public static FGEPoint getProjection(FGEPoint p, FGEAbstractLine line) {
		return line.getProjection(p);
	}

	public FGEPoint pointAtAbciss(double x) {
		if (b != 0) {
			return new FGEPoint(x, -(a * x + c) / b);
		}
		throw new IllegalArgumentException("Line does not pass through this abciss");
	}

	public FGEPoint pointAtOrdinate(double y) {
		if (a != 0) {
			return new FGEPoint(-(b * y + c) / a, y);
		}
		throw new IllegalArgumentException("Line does not pass through this ordinate");
	}

	@Override
	public abstract FGEPoint getNearestPoint(FGEPoint p);

	public boolean isHorizontal() {
		return Math.abs(getY1() - getY2()) < EPSILON;
	}

	public boolean isVertical() {
		return Math.abs(getX1() - getX2()) < EPSILON;
	}

	/**
	 * Compute and return angle formed by the abstract line (NOTE: assert that coordinate system is orthonormal !!!!!)
	 * 
	 * @return angle in radians, in range -PI/2 - 3*PI/2
	 */
	public double getAngle() {
		return FGEUtils.getSlope(getP1(), getP2());
	}

	public static FGEAbstractLine getNorthernLine(FGEAbstractLine source, FGEAbstractLine destination) {
		if (source.isParallelTo(destination)) {
			FGELine orthogonalLine = source.getOrthogonalLine(source.getP1());
			FGEPoint p1 = orthogonalLine.getLineIntersection(source);
			FGEPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.y <= p2.y ? source : destination;
		} else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static FGEAbstractLine getSouthernLine(FGEAbstractLine source, FGEAbstractLine destination) {
		if (source.isParallelTo(destination)) {
			FGELine orthogonalLine = source.getOrthogonalLine(source.getP1());
			FGEPoint p1 = orthogonalLine.getLineIntersection(source);
			FGEPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.y >= p2.y ? source : destination;
		} else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static FGEAbstractLine getEasternLine(FGEAbstractLine source, FGEAbstractLine destination) {
		if (source.isParallelTo(destination)) {
			FGELine orthogonalLine = source.getOrthogonalLine(source.getP1());
			FGEPoint p1 = orthogonalLine.getLineIntersection(source);
			FGEPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.x >= p2.x ? source : destination;
		} else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	public static FGEAbstractLine getWesternLine(FGEAbstractLine source, FGEAbstractLine destination) {
		if (source.isParallelTo(destination)) {
			FGELine orthogonalLine = source.getOrthogonalLine(source.getP1());
			FGEPoint p1 = orthogonalLine.getLineIntersection(source);
			FGEPoint p2 = orthogonalLine.getLineIntersection(destination);
			return p1.x <= p2.x ? source : destination;
		} else {
			throw new IllegalArgumentException("lines are not parallel");
		}
	}

	protected abstract FGEArea computeLineIntersection(FGEAbstractLine line);

	/*
	 * { logger.info("computeIntersection() between "+this+"\n and "+line+" overlap="+overlap(line)); if (overlap(line)) { if (this
	 * instanceof FGEHalfLine) { if (line instanceof FGEHalfLine) { return _compute_hl_hl_Intersection((FGEHalfLine)this,(FGEHalfLine)line);
	 * } else if (line instanceof FGESegment) { return _compute_hl_segment_Intersection((FGEHalfLine)this,(FGESegment)line); } else { return
	 * clone(); } } else if (this instanceof FGESegment) { if (line instanceof FGEHalfLine) { return
	 * _compute_hl_segment_Intersection((FGEHalfLine)line,(FGESegment)this); } else if (line instanceof FGESegment) { return
	 * _compute_segment_segment_Intersection((FGESegment)this,(FGESegment)line); } else { return clone(); } } else { return line.clone(); }
	 * } else if (isParallelTo(line)) { return new FGEEmptyArea(); } else { FGEPoint returned; try { returned = getLineIntersection(line);
	 * if (containsPoint(returned) && line.containsPoint(returned)) return returned; } catch (ParallelLinesException e) { // cannot happen }
	 * return new FGEEmptyArea(); } }
	 */

	@Override
	public final FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public final FGEArea intersect(FGEArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}
		if (area instanceof FGEPoint) {
			FGEPoint p = (FGEPoint) area;
			if (containsPoint(p)) {
				return p.clone();
			} else {
				return new FGEEmptyArea();
			}
		}
		if (area instanceof FGEAbstractLine) {
			return computeLineIntersection((FGEAbstractLine) area);
		}
		if (area instanceof FGERectangle) {
			return ((FGERectangle) area).intersect(this);
		}
		if (area instanceof FGERoundRectangle) {
			return ((FGERoundRectangle) area).intersect(this);
		}
		if (area instanceof FGEHalfPlane) {
			return ((FGEHalfPlane) area).intersect(this);
		}
		if (area instanceof FGEHalfBand) {
			return ((FGEHalfBand) area).intersect(this);
		}
		if (area instanceof FGEBand) {
			return ((FGEBand) area).intersect(this);
		}
		if (area instanceof FGEArc) {
			return ((FGEArc) area).intersect(this);
		}
		if (area instanceof FGEPolygon) {
			return ((FGEPolygon) area).intersect(this);
		}
		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
		}
	}

	@Override
	public final FGEArea substract(FGEArea area, boolean isStrict) {
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
	public final boolean containsPoint(FGEPoint p) {
		return contains(p);
	}

	@Override
	public abstract boolean containsLine(FGEAbstractLine l);

	@Override
	public final boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEAbstractLine) {
			return containsLine((FGEAbstractLine) a);
		}
		return false;
	}

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract FGEAbstractLine transform(AffineTransform t);

	@Override
	public abstract String toString();

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public abstract void paint(FGEGraphics g);

	public static FGEArea _compute_hl_segment_Intersection(FGEHalfLine hl, FGESegment s) {
		if (hl.containsPoint(s.getP1())) {
			if (hl.containsPoint(s.getP2())) {
				return s.clone();
			} else {
				return new FGESegment(hl.getLimit(), s.getP1());
			}
		} else {
			if (hl.containsPoint(s.getP2())) {
				return new FGESegment(hl.getLimit(), s.getP2());
			} else {
				return new FGEEmptyArea();
			}
		}
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

		FGEArea returned = hl.intersect(this);

		if (hl.overlap(this)) {
			if (returned instanceof FGEArea) {
				return null;
			} else {
				return getNearestPoint(from);
			}
		}

		if (returned instanceof FGEEmptyArea) {
			return null;
		} else if (returned instanceof FGEPoint) {
			return (FGEPoint) returned;
		} else {
			logger.warning("Unexpected area: " + returned);
			return null;
		}

	}

}
