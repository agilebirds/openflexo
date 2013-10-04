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
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

@SuppressWarnings("serial")
public class FGESegment extends FGEAbstractLine<FGESegment> implements FGEGeneralShape.GeneralShapePathElement<FGESegment> {

	private static final Logger logger = Logger.getLogger(FGESegment.class.getPackage().getName());

	public FGESegment(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public FGESegment(FGEPoint p1, FGEPoint p2) {
		super(p1, p2);
	}

	public FGESegment() {
		super();
	}

	public FGEPoint getMiddle() {
		return new FGEPoint((getP1().x + getP2().x) / 2, (getP1().y + getP2().y) / 2);
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		if (!overlap(l)) {
			return false;
		}

		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		if (l instanceof FGEHalfLine) {
			return false;
		}
		if (l instanceof FGESegment) {
			return true;
		}

		// If this is a line this is false
		return false;
	}

	@Override
	public boolean contains(FGEPoint p) {
		// First see if located on line
		if (!_containsPointIgnoreBounds(p)) {
			return false;
		}

		// Now check bounds
		if (getB() != 0) {
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
			if (pp1.x > pp2.x) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (p.x >= pp1.x - EPSILON) {
				if (p.x > pp2.x + EPSILON) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
			if (pp1.y > pp2.y) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (p.y >= pp1.y - EPSILON) {
				if (p.y > pp2.y + EPSILON) {
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
	}

	public boolean projectionIntersectsInsideSegment(FGEPoint p) {
		FGEPoint projection = getProjection(p);
		return contains(projection);
	}

	/**
	 * Return flag indicating if intersection of this segment and supplied line occurs somewhere INSIDE this segment
	 * 
	 * @param line
	 * @return
	 */
	public boolean intersectsInsideSegment(FGEAbstractLine<?> line) {
		FGEPoint intersection;
		try {
			intersection = getLineIntersection(line);
		} catch (ParallelLinesException e) {
			return false;
		}
		return contains(intersection) && line.contains(intersection);
	}

	/**
	 * Return flag indicating if intersection of this segment and supplied line occurs somewhere INSIDE this segment. If insideOnly set to
	 * true and intersection is one of extremities return false
	 * 
	 * @param line
	 * @return
	 */
	public boolean intersectsInsideSegment(FGEAbstractLine<?> line, boolean insideOnly) {
		FGEPoint intersection;
		try {
			intersection = getLineIntersection(line);
		} catch (ParallelLinesException e) {
			return false;
		}
		if (!(contains(intersection) && line.contains(intersection))) {
			return false;
		}
		if (insideOnly) {
			if (intersection.equals(getP1()) || intersection.equals(getP2()) || line instanceof FGESegment
					&& (intersection.equals(line.getP1()) || intersection.equals(line.getP2()))) {
				return false;
			}
		}
		return true;
	}

	public static FGEPoint getClosestPointOnSegment(FGEPoint p, FGESegment segment) {
		return segment.getNearestPointOnSegment(p);
	}

	public static boolean intersectsInsideSegment(FGEPoint p, FGESegment segment) {
		return segment.projectionIntersectsInsideSegment(p);
	}

	/**
	 * Return flag indicating if intersection of supplied segment and supplied line occurs somewhere INSIDE supplied segment
	 * 
	 * @param line
	 * @return
	 */
	public static boolean intersectsInsideSegment(FGESegment segment, FGEAbstractLine<?> line) {
		return segment.intersectsInsideSegment(line);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint p) {
		return getNearestPointOnSegment(p);
	}

	/**
	 * Return nearest point on segment
	 * 
	 * If orthogonal projection of supplied point on segment is inside the segment, return this projection. Otherwise, return adequate
	 * segment extremity
	 * 
	 * @param p
	 * @return
	 */
	public FGEPoint getNearestPointOnSegment(FGEPoint p) {
		FGEPoint projection = getProjection(p);
		if (getB() != 0) {
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
			if (pp1.x > pp2.x) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (projection.x >= pp1.x) {
				if (projection.x >= pp2.x) {
					return pp2;
				} else {
					return projection;
				}
			} else {
				return pp1;
			}
		} else {
			FGEPoint pp1 = getP1();
			FGEPoint pp2 = getP2();
			if (pp1.y > pp2.y) {
				pp1 = getP2();
				pp2 = getP1();
			}
			if (projection.y >= pp1.y) {
				if (projection.y >= pp2.y) {
					return pp2;
				} else {
					return projection;
				}
			} else {
				return pp1;
			}
		}
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
	public FGESegment clone() {
		return (FGESegment) super.clone();
	}

	@Override
	public FGESegment transform(AffineTransform t) {
		return new FGESegment(getP1().transform(t), getP2().transform(t));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGESegment) {
			FGESegment s = (FGESegment) obj;
			if (!overlap(s)) {
				return false;
			}
			return (getP1().equals(s.getP1()) || getP1().equals(s.getP2())) && (getP2().equals(s.getP1()) || getP2().equals(s.getP2()));
		}
		return false;
	}

	@Override
	public String toString() {
		return "FGESegment: [" + getP1() + "," + getP2() + "]";
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawLine(getP1(), getP2());
	}

	public double getLength() {
		return Math.sqrt(getSqLength());
	}

	public double getSqLength() {
		return (getP1().x - getP2().x) * (getP1().x - getP2().x) + (getP1().y - getP2().y) * (getP1().y - getP2().y);
	}

	public static double getLength(FGEPoint p1, FGEPoint p2) {
		return new FGESegment(p1, p2).getLength();
	}

	@Override
	protected FGEArea computeLineIntersection(FGEAbstractLine<?> line) {
		// logger.info("computeIntersection() between "+this+"\n and "+line+" overlap="+overlap(line));
		if (overlap(line)) {
			if (line instanceof FGEHalfLine) {
				return _compute_hl_segment_Intersection((FGEHalfLine) line, this);
			} else if (line instanceof FGESegment) {
				return _compute_segment_segment_Intersection(this, (FGESegment) line);
			} else {
				return clone();
			}
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

	private static FGEArea _compute_segment_segment_Intersection(FGESegment s1, FGESegment s2) {
		if (s1.containsPoint(s2.getP1())) {
			if (s1.containsPoint(s2.getP2())) {
				return s2.clone();
			} else {
				if (s2.containsPoint(s1.getP1())) {
					if (s1.getP1().equals(s2.getP1())) {
						return s1.getP1().clone();
					}
					return new FGESegment(s1.getP1(), s2.getP1());
				} else {
					if (s2.getP1().equals(s1.getP2())) {
						return s2.getP1().clone();
					}
					return new FGESegment(s2.getP1(), s1.getP2());
				}
			}
		} else {
			if (s1.containsPoint(s2.getP2())) {
				if (s2.containsPoint(s1.getP1())) {
					if (s1.getP1().equals(s2.getP2())) {
						return s1.getP1().clone();
					}
					return new FGESegment(s1.getP1(), s2.getP2());
				} else {
					if (s2.getP2().equals(s1.getP2())) {
						return s2.getP2().clone();
					}
					return new FGESegment(s2.getP2(), s1.getP2());
				}
			} else {
				if (s2.containsPoint(s1.getP1()) && s2.containsPoint(s1.getP2())) {
					return s1.clone();
				} else {
					return new FGEEmptyArea();
				}
			}

		}
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		FGEHalfLine hl1 = null;
		FGEHalfLine hl2 = null;
		FGEPoint ps1 = getP1();
		FGEPoint ps2 = getP2();
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			hl1 = new FGEHalfLine(ps1, new FGEPoint(ps1.x, ps1.y - 1));
			hl2 = new FGEHalfLine(ps2, new FGEPoint(ps2.x, ps2.y - 1));
			if (Math.abs(ps1.x - ps2.x) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			hl1 = new FGEHalfLine(ps1, new FGEPoint(ps1.x, ps1.y + 1));
			hl2 = new FGEHalfLine(ps2, new FGEPoint(ps2.x, ps2.y + 1));
			if (Math.abs(ps1.x - ps2.x) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			hl1 = new FGEHalfLine(ps1, new FGEPoint(ps1.x + 1, ps1.y));
			hl2 = new FGEHalfLine(ps2, new FGEPoint(ps2.x + 1, ps2.y));
			if (Math.abs(ps1.y - ps2.y) < EPSILON) {
				return hl1;
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			hl1 = new FGEHalfLine(ps1, new FGEPoint(ps1.x - 1, ps1.y));
			hl2 = new FGEHalfLine(ps2, new FGEPoint(ps2.x - 1, ps2.y));
			if (Math.abs(ps1.y - ps2.y) < EPSILON) {
				return hl1;
			}
		}
		// System.out.println("Segment: "+this+" orientation="+orientation);
		try {
			return new FGEHalfBand(hl1, hl2);
		} catch (IllegalArgumentException e) {
			logger.warning("Could not obtain resulting half-band, hl1=" + hl1 + " hl2=" + hl2 + " orientation=" + orientation + " ps1="
					+ ps1 + " ps2=" + ps2);
			return new FGEEmptyArea();
		}
	}

	@Override
	public FGESegment getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	public FGEPoint getScaledPoint(double scale) {
		FGEPoint p = new FGEPoint();
		p.x = getP1().x + (getP2().x - getP1().x) * scale;
		p.y = getP1().y + (getP2().y - getP1().y) * scale;
		return p;
	}

	public double getRelativeLocation(FGEPoint p) {
		FGEPoint proj = getNearestPointOnSegment(p);
		if (Math.abs(getP2().x - getP1().x) < EPSILON) {
			return (proj.y - getP1().y) / (getP2().y - getP1().y);
		} else {
			return (proj.x - getP1().x) / (getP2().x - getP1().x);
		}
	}

	public static FGERectangle getBoundingBox(FGESegment... segments) {
		FGEPolylin polylin = new FGEPolylin();
		for (FGESegment s : segments) {
			polylin.addToPoints(s.getP1());
			polylin.addToPoints(s.getP2());
		}
		return polylin.getBoundingBox();
	}

	public SimplifiedCardinalDirection getOrientation() {
		double angle = getAngle();

		/**
		 * GPO: Angle will be between -PI/2 and 3PI/2 0-->WEST PI/2--> SOUTH -PI or PI-->EAST (Almost sure that -PI will never come but it
		 * does not cost too much so let's do it) -PI/2 or 3PI/2-->NORTH
		 */
		if (Math.abs(angle) < EPSILON) {
			return SimplifiedCardinalDirection.WEST;
		} else if (Math.abs(angle - Math.PI) < EPSILON || Math.abs(angle + Math.PI) < EPSILON) {
			return SimplifiedCardinalDirection.EAST;
		} else if (Math.abs(angle - Math.PI / 2) < EPSILON) {
			return SimplifiedCardinalDirection.SOUTH;
		} else if (Math.abs(angle - 3 * Math.PI / 2) < EPSILON || Math.abs(angle + Math.PI / 2) < EPSILON) {
			return SimplifiedCardinalDirection.NORTH;
		} else {
			return null;
		}
	}

	public SimplifiedCardinalDirection getApproximatedOrientation() {
		SimplifiedCardinalDirection returned = getOrientation();
		if (returned != null) {
			return returned;
		}

		return FGEPoint.getSimplifiedOrientation(getP1(), getP2());
	}

	@Override
	public final FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof FGEPolylin) {
			return ((FGEPolylin) area).union(this);
		}
		if (area instanceof FGESegment) {
			FGESegment s = (FGESegment) area;
			if (s.containsLine(this)) {
				return s;
			}
			if (containsLine(s)) {
				return clone();
			}
			if (overlap(s)) {
				if (containsPoint(s.getP1())) {
					// Doesn't contains P2, otherwise all contained, see above
					if (s.containsPoint(getP1())) {
						return new FGESegment(getP2(), s.getP2());
					}
					if (s.containsPoint(getP2())) {
						return new FGESegment(getP1(), s.getP2());
					}
				}
				if (containsPoint(s.getP2())) {
					// Doesn't contains P2, otherwise all contained, see above
					if (s.containsPoint(getP1())) {
						return new FGESegment(getP2(), s.getP1());
					}
					if (s.containsPoint(getP2())) {
						return new FGESegment(getP1(), s.getP1());
					}
				}
			}
			if (getP1().equals(s.getP2())) {
				return new FGEPolylin(getP2(), getP1(), s.getP1());
			}
			if (getP1().equals(s.getP1())) {
				return new FGEPolylin(getP2(), getP1(), s.getP2());
			}
			if (getP2().equals(s.getP2())) {
				return new FGEPolylin(getP1(), getP2(), s.getP1());
			}
			if (getP2().equals(s.getP1())) {
				return new FGEPolylin(getP1(), getP2(), s.getP2());
			}
			return super.union(area);
		}
		return super.union(area);
	}

	/**
	 * This area is finite, so always return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is finite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		return new FGERectangle(getP1(), getP2(), Filling.FILLED);
	}

}
