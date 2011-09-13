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
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.geom.FGEGeometricObject.CardinalDirection;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.graphics.FGEGraphics;


public class FGEHalfPlane implements FGEArea {

    private static final Logger logger = Logger.getLogger(FGEHalfPlane.class.getPackage().getName());

	public FGELine line;
	public FGEPoint testPoint; // located on the "good" side

	public FGEHalfPlane() 
	{
		super();
	}

	public FGEHalfPlane(FGELine line, FGEPoint testPoint) 
	{
		super();
		this.line = line;
		this.testPoint = testPoint;
	}

	public FGEHalfPlane(FGEAbstractLine line, FGEPoint testPoint) 
	{
		super();
		this.line = new FGELine(line);
		this.testPoint = testPoint;
	}

	public static FGEHalfPlane makeFGEHalfPlane(FGEPoint point, CardinalDirection orientation) 
	{
		FGELine line;
		FGEPoint testPoint;

		if (orientation == CardinalDirection.NORTH) {
			line = FGELine.makeHorizontalLine(point);
			testPoint = new FGEPoint(point);
			testPoint.y -= 1;
		}
		else if (orientation == CardinalDirection.EAST) {
			line = FGELine.makeVerticalLine(point);
			testPoint = new FGEPoint(point);
			testPoint.x += 1;
		}
		else if (orientation == CardinalDirection.SOUTH) {
			line = FGELine.makeHorizontalLine(point);
			testPoint = new FGEPoint(point);
			testPoint.y += 1;
		}
		else /* orientation == CardinalDirection.WEST */  {
			line = FGELine.makeVerticalLine(point);
			testPoint = new FGEPoint(point);
			testPoint.x -= 1;
		}

		return new FGEHalfPlane(line,testPoint);
	}

	@Override
	public boolean containsPoint(FGEPoint p)
	{
		if (line.contains(p)) return true;
		if (line.getPlaneLocation(testPoint) == line.getPlaneLocation(p)) return true;
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l)
	{
		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) return false;

		if (l instanceof FGEHalfLine) {			
			if (l.isParallelTo(line)) return true;
			return (!l.containsPoint(line.getLineIntersection(l))) 
			|| line.containsPoint(((FGEHalfLine)l).getLimit());
		}
		else if (l instanceof FGESegment) {
			return true;
		}
		else if (l instanceof FGELine) {
			return l.isParallelTo(line);
		}
		logger.warning("Unexpected: "+l);
		return false;
	}

	public boolean containsHalfBand(FGEHalfBand hb)
	{
		return containsLine(hb.halfLine1) && containsLine(hb.halfLine2);
	}

	public boolean containsBand(FGEBand band)
	{
		return containsLine(band.line1) && containsLine(band.line2);
	}

	@Override
	public boolean containsArea(FGEArea a)
	{
		if (a instanceof FGEPoint) return containsPoint((FGEPoint)a);
		if (a instanceof FGEAbstractLine) return containsLine((FGEAbstractLine)a);
		if (a instanceof FGEHalfBand) return containsHalfBand((FGEHalfBand)a);
		if (a instanceof FGEBand) return containsBand((FGEBand)a);
		if (a instanceof FGEShape) return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>)a, this);
		if (a instanceof FGEHalfPlane) return containsHalfPlane((FGEHalfPlane)a);
		return false;
	}

	public FGEHalfLine getHalfLine()
	{
		FGEPoint p = line.getProjection(testPoint);
		return new FGEHalfLine(p,testPoint);
	}

	private boolean containsHalfPlane(FGEHalfPlane hp)
	{
		if (line.overlap(hp.line)) {
			if (containsPoint(hp.testPoint)) return true;
			else return false; // Only line is common
		}
		else if (line.isParallelTo(hp.line)) {
			if (containsLine(hp.line) && !hp.containsLine(line)) {
				return true;
			}
		}
		return false;
	}


	private FGEArea computeLineIntersection(FGEAbstractLine aLine)
	{
		if (aLine instanceof FGELine) {
			if (aLine.equals(line)) return aLine.clone();
			if (aLine.isParallelTo(line)) {
				if (containsPoint(aLine.getP1())) return aLine.clone();
				else return new FGEEmptyArea();
			}
			else {
				FGEPoint limit = line.getLineIntersection(aLine);
				if (limit.equals(aLine.getP1())) {
					if (containsPoint(aLine.getP2())) {
						return new FGEHalfLine(limit,aLine.getP2());
					}
					else {
						return new FGEHalfLine(limit,FGEAbstractLine.getOppositePoint(aLine.getP2(), limit));
					}
				}
				else if (containsPoint(aLine.getP1())) {
					return new FGEHalfLine(limit,aLine.getP1());
				}
				else {
					return new FGEHalfLine(limit,FGEAbstractLine.getOppositePoint(aLine.getP1(), limit));
				}
			}
		}
		else if (aLine instanceof FGEHalfLine) {
			FGEHalfLine hl = (FGEHalfLine)aLine;
			if (hl.overlap(line)) return hl.clone();
			if (hl.isParallelTo(line)) {
				if (containsPoint(hl.getP1())) return hl.clone();
				else return new FGEEmptyArea();
			}
			else {
				FGEPoint intersect = hl.getLineIntersection(line);
				FGEPoint limit = hl.getLimit();
				if (intersect.equals(limit)) return intersect.clone();
				FGEPoint opposite = FGEAbstractLine.getOppositePoint(limit, intersect);
				if (containsPoint(limit)) {
					if (hl.contains(opposite)) return new FGESegment(limit,intersect);
					else return hl.clone();
				}
				else {
					if (hl.contains(opposite)) return new FGEHalfLine(intersect,opposite);
					else return new FGEEmptyArea();
				}
			}
		}
		else if (aLine instanceof FGESegment) {
			FGESegment segment = (FGESegment)aLine;
			if (segment.overlap(line)) return segment.clone();
			if (segment.isParallelTo(line)) {
				if (containsPoint(segment.getP1())) return segment.clone();
				else return new FGEEmptyArea();
			}
			else {
				if (containsPoint(segment.getP1())) {
					if (containsPoint(segment.getP2())) return segment.clone();
					else {
						FGEPoint p1 = segment.getP1();
						FGEPoint p2 = segment.getLineIntersection(line);
						if (p1.equals(p2)) return p1.clone();
						else return new FGESegment(p1,p2);
					}
				}
				else {
					if (containsPoint(segment.getP2())) {
						FGEPoint p1 = segment.getP2();
						FGEPoint p2 = segment.getLineIntersection(line);
						if (p1.equals(p2)) return p1.clone();
						else return new FGESegment(p1,p2);
					}
					else return new FGEEmptyArea();
				}
			}
		}
		else {
			logger.warning("Unexpected: "+line);
			return null;
		}
	}


	private FGEArea computeHalfPlaneIntersection(FGEHalfPlane hp)
	{
		if (line.overlap(hp.line)) {
			if (containsPoint(hp.testPoint)) return clone(); // Same half planes
			else return line.clone(); // Only line is common
		}
		else if (line.isParallelTo(hp.line)) {
			if (hp.containsLine(line)) {
				if (containsLine(hp.line)) {
					return new FGEBand(line,hp.line);
				}
				else {
					return clone();
				}
			}
			else {
				if (containsLine(hp.line)) {
					return hp.clone();
				}
				else {
					return new FGEEmptyArea();
				}
			}
		}
		else {
			// Don't try to formalize it
			return FGEIntersectionArea.makeIntersection(this,hp);
		}
	}
	
	private FGEArea computeShapeIntersection(FGEShape shape)
	{
		FGEArea workingArea = intersect(shape.getBoundingBox());
		if (workingArea instanceof FGEEmptyArea) return workingArea; // Empty area
		if (workingArea instanceof FGEShape || workingArea instanceof FGEAbstractLine || workingArea instanceof FGEPoint) {
			return workingArea.intersect(shape);
		}
		else {
			logger.warning("Unexpected: "+workingArea);
			return new FGEEmptyArea();
		}
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area)
	{
		return new FGEExclusiveOrArea(this,area);
	}

	@Override
	public FGEArea intersect(FGEArea area)
	{
		if (area.containsArea(this)) return this.clone();
		if (containsArea(area)) return area.clone();
		if (area instanceof FGEAbstractLine) return computeLineIntersection((FGEAbstractLine)area);
		if (area instanceof FGERectangle) return area.intersect(this);
		if (area instanceof FGEBand) return area.intersect(this);
		if (area instanceof FGEHalfBand) return area.intersect(this);
		if (area instanceof FGEHalfPlane) return computeHalfPlaneIntersection((FGEHalfPlane)area);
		if (area instanceof FGEShape) return computeShapeIntersection((FGEShape)area);

		FGEIntersectionArea returned = new FGEIntersectionArea(this,area);
		if (returned.isDevelopable()) return returned.makeDevelopped();
		else return returned;
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict)
	{
		return new FGESubstractionArea(this,area,isStrict);
	}

	@Override
	public FGEArea union(FGEArea area)
	{
		if (containsArea(area)) return clone();
		if (area.containsArea(this)) return area.clone();
		
		return new FGEUnionArea(this,area);
	}

	/**
	 * Creates a new object of the same class and with the same
	 * contents as this object.
	 * @return     a clone of this instance.
	 * @exception  OutOfMemoryError            if there is not enough memory.
	 * @see        java.lang.Cloneable
	 * @since      1.2
	 */
	@Override
	public FGEHalfPlane clone() {
		try {
			return (FGEHalfPlane)super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEHalfPlane transform(AffineTransform t)
	{
		return new FGEHalfPlane(line.transform(t),testPoint.transform(t));
	}

	@Override
	public void paint(FGEGraphics g)
	{
		FGERectangle bounds = g.getGraphicalRepresentation().getNormalizedBounds();

		g.useDefaultForegroundStyle();
		bounds.intersect(line).paint(g);

		g.useDefaultBackgroundStyle();
		bounds.intersect(this).paint(g);		
	}

	@Override
	public String toString()
	{
		return "FGEHalfPlane: "+line.toString()+" testPoint="+testPoint;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint)
	{
		if (containsPoint(aPoint)) return aPoint.clone();
		else return line.getProjection(aPoint);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FGEHalfPlane) {
			FGEHalfPlane hp = (FGEHalfPlane)obj;
			return (line.overlap(hp.line) && containsPoint(hp.testPoint));
		}
		return super.equals(obj);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation)
	{
		return line.getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public FGEHalfPlane getAnchorAreaFrom(SimplifiedCardinalDirection direction)
	{
		return clone();
	}

	/**
	 * This area is infinite, so always return false
	 */
	@Override
	public final boolean isFinite()
	{
		return false;
	}
	
	/**
	 * This area is infinite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds()
	{
		return null;
	}
	
	/**
	 * Return nearest point from point "from" following supplied orientation
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param from point from which we are coming to area
	 * @param orientation orientation we are coming from
	 * @return 
	 */
	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) 
	{
		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);
		FGEArea intersect = intersect(hl);
		return intersect.nearestPointFrom(from, orientation);
	}



}
