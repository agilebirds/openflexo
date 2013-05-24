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
import java.util.Random;
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

public class FGEBand implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEBand.class.getPackage().getName());

	public FGELine line1;
	public FGELine line2;

	private FGEPoint testPoint;

	public FGEBand() {
		super();
	}

	public FGEBand(FGELine aLine1, FGELine aLine2) {
		super();
		this.line1 = new FGELine(aLine1);
		this.line2 = new FGELine(aLine2);
		if (!line1.isParallelTo(line2)) {
			throw new IllegalArgumentException("lines are not parallel");
		}
		computeTestPoint();
	}

	protected void computeTestPoint() {
		FGEPoint randPoint = null;
		boolean found = false;
		while (!found) {
			Random rand = new Random();
			randPoint = new FGEPoint(rand.nextInt(), rand.nextInt());
			if (!line1.contains(randPoint) && !line2.contains(randPoint)) {
				found = true;
			}
		}
		FGESegment segment = new FGESegment(line1.getProjection(randPoint), line2.getProjection(randPoint));
		testPoint = segment.getMiddle();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return bandContainsPoint(p);
	}

	private boolean bandContainsPoint(FGEPoint p) {
		if (line1.contains(p)) {
			return true;
		}
		if (line2.contains(p)) {
			return true;
		}
		return line1.getPlaneLocation(testPoint) == line1.getPlaneLocation(p)
				&& line2.getPlaneLocation(testPoint) == line2.getPlaneLocation(p);
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (!containsLineIgnoreBounds(l)) {
			return false;
		}

		if (l instanceof FGEHalfLine) {
			return l.isParallelTo(line1);
		}
		if (l instanceof FGESegment) {
			return true;
		}
		if (l instanceof FGELine) {
			return l.isParallelTo(line1);
		}
		logger.warning("Unexpected: " + l);
		return false;
	}

	public boolean containsLineIgnoreBounds(FGEAbstractLine l) {
		return bandContainsPoint(l.getP1()) && bandContainsPoint(l.getP2());
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
		if (a instanceof FGEBand) {
			return containsLine(((FGEBand) a).line1) && containsLine(((FGEBand) a).line2);
		}
		return false;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		// There is a big problem if we uncomment this here
		// We really need to build many JUnit tests in order to cover all area computation
		// Many cases are wrong !!!

		// if (area.containsArea(this)) return this.clone();
		// if (containsArea(area)) return area.clone();
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
	public FGEBand clone() {
		try {
			return (FGEBand) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		FGELine l1 = line1.transform(t);
		FGELine l2 = line1.transform(t);
		if (l1.overlap(l2)) {
			return l1;
		} else {
			return new FGEBand(l1, l2);
		}
	}

	@Override
	public void paint(FGEGraphics g) {
		FGERectangle bounds = g.getGraphicalRepresentation().getNormalizedBounds();

		g.useDefaultBackgroundStyle();
		bounds.intersect(this).paint(g);

		g.useDefaultForegroundStyle();
		line1.paint(g);
		line2.paint(g);

		/*
		System.out.println("paint() for "+this);
		
		FGERectangle bounds = g.getGraphicalRepresentation().getLogicalBounds();
		Vector<FGEPoint> pts = new Vector<FGEPoint>();

		FGEArea a1 = bounds.intersect(new FGELine(line1));
		if (a1 instanceof FGESegment) {
			FGESegment s1 = (FGESegment)a1;
			pts.add(s1.getP1());
			pts.add(s1.getP2());
		}
		else if (a1 instanceof FGEPoint) {
			pts.add((FGEPoint)a1);
		}
		else if (a1 instanceof FGEEmptyArea) {
		}
		else {
			logger.warning("Unexpected intersection: "+a1);
		}
		
		FGEArea a2 = bounds.intersect(new FGELine(line2));
		if (a2 instanceof FGESegment) {
			FGESegment s2 = (FGESegment)a2;
			pts.add(s2.getP1());
			pts.add(s2.getP2());
		}
		else if (a2 instanceof FGEPoint) {
			pts.add((FGEPoint)a2);
		}
		else if (a2 instanceof FGEEmptyArea) {
		}
		else {
			logger.warning("Unexpected intersection: "+a2);
		}
		
		if (containsPoint(bounds.getNorthEastPt())) pts.add(bounds.getNorthEastPt());
		if (containsPoint(bounds.getNorthWestPt())) pts.add(bounds.getNorthWestPt());
		if (containsPoint(bounds.getSouthEastPt())) pts.add(bounds.getSouthEastPt());
		if (containsPoint(bounds.getSouthWestPt())) pts.add(bounds.getSouthWestPt());
		
		g.useDefaultBackgroundStyle();
		FGEPolygon.makeArea(Filling.FILLED,pts).paint(g);
		

		g.useDefaultForegroundStyle();
		line1.paint(g);
		line2.paint(g);*/
	}

	@Override
	public String toString() {
		return "FGEBand: " + line1.toString() + " " + line2.toString();
	}

	public static void main(String[] args) {
		FGELine line0 = new FGELine(0, 0, 10, 0);
		FGELine line1 = new FGELine(0, 1, 10, 1);
		FGELine line2 = new FGELine(0, 2, 10, 2);
		FGELine line3 = new FGELine(0, 3, 10, 3);
		FGEBand b1 = new FGEBand(line3, line0);
		FGEBand b2 = new FGEBand(line2, line1);
		System.out.println("intersection: " + b2.intersect(b1));

		FGEHalfLine hl3 = new FGEHalfLine(new FGEPoint(1, 3), new FGEPoint(-1, 3));
		FGEHalfLine hl2 = new FGEHalfLine(new FGEPoint(0, 2), new FGEPoint(2, 2));
		FGEHalfLine hl1 = new FGEHalfLine(new FGEPoint(0, 1), new FGEPoint(2, 1));
		FGEHalfLine hl0 = new FGEHalfLine(new FGEPoint(1, 0), new FGEPoint(-1, 0));

		FGEHalfBand hb1 = new FGEHalfBand(hl3, hl0);
		FGEHalfBand hb2 = new FGEHalfBand(hl2, hl1);
		System.out.println("intersection: " + hb2.intersect(hb1));

		System.out.println("hb1=" + hb1);
		System.out.println("hb1.intersect(hb1.halfPlane)=" + hb1.intersect(hb1.halfPlane));
	}

	protected FGEArea computeHalfPlaneIntersection(FGEHalfPlane hp) {
		if (hp.containsArea(this)) {
			return clone();
		}

		if (hp.line.isParallelTo(line1)) {
			if (hp.containsLine(line1)) {
				if (hp.containsLine(line2)) {
					return clone();
				} else {
					return new FGEBand(line1, hp.line);
				}
			} else {
				if (hp.containsLine(line2)) {
					return new FGEBand(line2, hp.line);
				} else {
					return new FGEEmptyArea();
				}
			}
		}

		else {
			try {
				FGEPoint i1 = line1.getLineIntersection(hp.line);
				FGEPoint i2 = line2.getLineIntersection(hp.line);
				FGEPoint pp1, pp2;

				if (!hp.line.containsPoint(line1.getP1())) {
					if (hp.containsPoint(line1.getP1())) {
						pp1 = line1.getP1();
					} else {
						pp1 = FGEAbstractLine.getOppositePoint(line1.getP1(), i1);
					}
				} else {
					if (hp.containsPoint(line1.getP2())) {
						pp1 = line1.getP2();
					} else {
						pp1 = FGEAbstractLine.getOppositePoint(line1.getP2(), i1);
					}
				}

				if (!hp.line.containsPoint(line2.getP1())) {
					if (hp.containsPoint(line2.getP1())) {
						pp2 = line2.getP1();
					} else {
						pp2 = FGEAbstractLine.getOppositePoint(line2.getP1(), i2);
					}
				} else {
					if (hp.containsPoint(line2.getP2())) {
						pp2 = line2.getP2();
					} else {
						pp2 = FGEAbstractLine.getOppositePoint(line2.getP2(), i2);
					}
				}

				FGEHalfLine hl1 = new FGEHalfLine(i1, pp1);
				FGEHalfLine hl2 = new FGEHalfLine(i2, pp2);

				if (hl1.overlap(hl2)) {
					return new FGEEmptyArea();
				} else {
					return new FGEHalfBand(hl1, hl2);
				}

			} catch (ParallelLinesException e) {
				// Cannot happen
				e.printStackTrace();
				return null;
			}
		}
	}

	protected FGEArea computeHalfBandIntersection(FGEHalfBand hb) {
		FGEArea returned = hb.embeddingBand.computeBandIntersection(this);
		FGEArea reply = returned.intersect(hb.halfPlane);
		return reply;
	}

	protected FGEArea computeBandIntersection(FGEBand band) {
		FGEArea returned = null;

		if (!line1.isParallelTo(band.line1)) {
			try {
				if (line1.isOrthogonalTo(band.line1)) {
					FGEPoint p1 = line1.getLineIntersection(band.line1);
					FGEPoint p2 = line2.getLineIntersection(band.line2);
					returned = new FGERectangle(p1, p2, Filling.FILLED);
				} else {
					returned = FGEPolygon.makeArea(Filling.FILLED, line1.getLineIntersection(band.line1),
							line1.getLineIntersection(band.line2), line2.getLineIntersection(band.line2),
							line2.getLineIntersection(band.line1));
				}
			} catch (ParallelLinesException e) {
				// Cannot happen
				e.printStackTrace();
				return null;
			}
		} else {
			if (containsArea(band)) {
				returned = band.clone();
			} else if (band.containsArea(this)) {
				returned = this.clone();
			} else if (containsLineIgnoreBounds(band.line1)) {
				if (containsLine(band.line2)) {
					returned = band.clone();
				} else {
					if (band.containsLineIgnoreBounds(line2)) {
						returned = new FGEBand(band.line1, line2);
						// System.out.println("Built from Parallel lines "+returned);
					} else {
						returned = new FGEBand(band.line1, line1);
						// System.out.println("Built from Parallel lines "+returned);
					}
				}
			} else {
				if (containsLineIgnoreBounds(band.line2)) {
					if (band.containsLineIgnoreBounds(line2)) {
						returned = new FGEBand(band.line2, line2);
						// System.out.println("Built from Parallel lines "+returned);
					} else {
						returned = new FGEBand(band.line2, line1);
						// System.out.println("Built from Parallel lines "+returned);
					}
				} else {
					returned = new FGEEmptyArea();
				}
			}
		}

		if (returned == null) {
			return new FGEEmptyArea();
		}

		/*if (band instanceof FGEHalfBand) {
			return returned.intersect(((FGEHalfBand)band).halfPlane);
		}*/

		// if (containsArea(returned) && band.containsArea(returned)) return returned;
		return returned;

		// return new FGEEmptyArea();
	}

	private FGEArea computeLineIntersection(FGEAbstractLine aLine) {
		FGEHalfPlane hp1 = new FGEHalfPlane(line1, line2.getP1());
		FGEArea a1 = hp1.intersect(aLine);
		if (a1 instanceof FGEEmptyArea) {
			return a1;
		}
		FGEHalfPlane hp2 = new FGEHalfPlane(line2, line1.getP1());
		return hp2.intersect(a1);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (containsPoint(aPoint)) {
			return aPoint.clone();
		} else {
			return FGEPoint.getNearestPoint(aPoint, line1.getProjection(aPoint), line2.getProjection(aPoint));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEBand) {
			FGEBand b = (FGEBand) obj;
			return (line1.overlap(b.line1) || line1.overlap(b.line2)) && (line2.overlap(b.line1) || line2.overlap(b.line2));
		}
		return false;
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (line1.isVertical()) {
				return clone();
			} else {
				return FGEAbstractLine.getNorthernLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (line1.isVertical()) {
				return clone();
			} else {
				return FGEAbstractLine.getSouthernLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (line1.isHorizontal()) {
				return clone();
			} else {
				return FGEAbstractLine.getEasternLine(line1, line2);
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (line1.isHorizontal()) {
				return clone();
			} else {
				return FGEAbstractLine.getWesternLine(line1, line2);
			}
		}
		return null;
	}

	@Override
	public FGEBand getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
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
