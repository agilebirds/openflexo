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

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

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


public class FGERectangle extends Rectangle2D.Double implements FGEGeometricObject<FGERectangle>, FGEShape<FGERectangle> {

	private static final Logger logger = Logger.getLogger(FGERectangle.class.getPackage().getName());

	protected Filling _filling;

	public FGERectangle()
	{
		this(0,0,0,0,Filling.NOT_FILLED);
	}

	public FGERectangle(Filling filling)
	{
		this(0,0,0,0,filling);
	}

	public FGERectangle(double aX, double aY, double aWidth, double aHeight)
	{
		this(aX,aY,aWidth,aHeight,Filling.NOT_FILLED);
	}

	public FGERectangle(double aX, double aY, double aWidth, double aHeight, Filling filling)
	{
		super(aX,aY,aWidth,aHeight);
		if (aWidth < 0) {
			x = x + aWidth;
			aWidth = -aWidth;
		}
		if (aHeight < 0) {
			y = y + aHeight;
			aHeight = -aHeight;
		}
		_filling = filling;
	}

	public FGERectangle(FGEPoint point, FGEDimension dimension, Filling filling)
	{
		this(point.x,point.y,dimension.width,dimension.height,filling);
	}

	public FGERectangle(FGEPoint p1, FGEPoint p2, Filling filling)
	{
		this(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y),filling);
	}

	public FGERectangle(Rectangle rect) {
		this(rect.x,rect.y,rect.width,rect.height);
	}

	@Override
	public boolean getIsFilled()
	{
		return _filling == Filling.FILLED;
	}

	@Override
	public void setIsFilled(boolean filled)
	{
		_filling = (filled?Filling.FILLED:Filling.NOT_FILLED);
	}

	@Override
	public FGEPoint getCenter()
	{
		return new FGEPoint(getCenterX(),getCenterY());
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
	public FGERectangle clone() {
		return (FGERectangle)super.clone();
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

		FGEArea intersection = computeLineIntersection(hl);
		if (intersection instanceof FGEEmptyArea) {
			return null;
		}
		else if (intersection instanceof FGEPoint) return (FGEPoint)intersection;
		else if (intersection instanceof FGEPoint) return (FGEPoint)intersection;
		else if (intersection instanceof FGEUnionArea) {
			FGEPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (FGEArea a : ((FGEUnionArea)intersection).getObjects()) {
				if (a instanceof FGEPoint) {
					double distSq = FGEPoint.distanceSq(from,(FGEPoint)a);
					if (distSq < minimalDistanceSq) {
						returned = (FGEPoint)a;
						minimalDistanceSq = distSq;
					}
				}
			}
			return returned;
		}
		else if (intersection instanceof FGESegment) {
			FGEPoint p1,p2;
			p1 = ((FGESegment)intersection).getP1();
			p2 = ((FGESegment)intersection).getP2();
			if (FGEPoint.distanceSq(from,p1) < FGEPoint.distanceSq(from,p2)) return p1;
			else return p2;
		}

		logger.warning("Unexpected area: "+intersection);

		return null;
	}

	public FGEPoint getNorthWestPt()
	{
		return getNorth().getP1();
	}

	public FGEPoint getNorthPt()
	{
		return getNorth().getMiddle();
	}

	public FGEPoint getNorthEastPt()
	{
		return getNorth().getP2();
	}

	public FGEPoint getSouthWestPt()
	{
		return getSouth().getP1();
	}

	public FGEPoint getSouthPt()
	{
		return getSouth().getMiddle();
	}

	public FGEPoint getSouthEastPt()
	{
		return getSouth().getP2();
	}

	public FGEPoint getEastPt()
	{
		return getEast().getMiddle();
	}

	public FGEPoint getWestPt()
	{
		return getWest().getMiddle();
	}

	public FGESegment getNorth()
	{
		return new FGESegment(getX(),getY(),getX()+getWidth(),getY());
	}

	public FGESegment getSouth()
	{
		return new FGESegment(getX(),getY()+getHeight(),getX()+getWidth(),getY()+getHeight());
	}

	public FGESegment getEast()
	{
		return new FGESegment(getX()+getWidth(),getY(),getX()+getWidth(),getY()+getHeight());
	}

	public FGESegment getWest()
	{
		return new FGESegment(getX(),getY(),getX(),getY()+getHeight());
	}

	public List<FGESegment> getFrameSegments()
	{
		Vector<FGESegment> returned = new Vector<FGESegment>();
		returned.add(getNorth());
		returned.add(getSouth());
		returned.add(getEast());
		returned.add(getWest());
		return returned;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint)
	{
		if (getIsFilled() && containsPoint(aPoint)) return aPoint.clone();
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint)
	{
		FGEPoint returned = null;
		double distSq, minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;

		FGESegment north = getNorth();
		FGESegment south = getSouth();
		FGESegment east =  getEast();
		FGESegment west =  getWest();

		FGEPoint p = north.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p,aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = south.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p,aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = east.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p,aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = west.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p,aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		return returned;
	}

	private FGEArea computeRectangleIntersection(FGERectangle rect)
	{
		if (!getIsFilled() && rect.getIsFilled()) {
			return rect.computeRectangleIntersection(this);
		}

		if (rect.equals(this)) return clone();

		double x1 = Math.max(getMinX(), rect.getMinX());
		double y1 = Math.max(getMinY(), rect.getMinY());
		double x2 = Math.min(getMaxX(), rect.getMaxX());
		double y2 = Math.min(getMaxY(), rect.getMaxY());

		if (y1 > y2 || x1 > x2) return new FGEEmptyArea();

		// There is a common intersection
		FGERectangle intersection = new FGERectangle(x1, y1, x2-x1, y2-y1);

		/*if (intersection.getWidth() == 0) {
			if (FGEPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.EAST) {
				return getEast().intersect(rect.getWest());
			}
			else if (FGEPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.WEST) {
				return getWest().intersect(rect.getEast());
			}
			else {
				logger.warning("Unexpected situation encountered here while computing rectangle intersection");
			}
		}

		if (intersection.getHeight() == 0) {
			if (FGEPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.NORTH) {
				return getNorth().intersect(rect.getSouth());
			}
			else if (FGEPoint.getSimplifiedOrientation(getCenter(), rect.getCenter()) == SimplifiedCardinalDirection.SOUTH) {
				return getSouth().intersect(rect.getNorth());
			}
			else {
				logger.warning("Unexpected situation encountered here while computing rectangle intersection");
			}
		}*/

		if (getIsFilled()) {
			if (rect.getIsFilled()) {
				intersection.setIsFilled(true);
				return intersection;
			}
			else {
				FGEUnionArea returned = new FGEUnionArea();
				returned.addArea(computeLineIntersection(rect.getNorth()));
				returned.addArea(computeLineIntersection(rect.getSouth()));
				returned.addArea(computeLineIntersection(rect.getEast()));
				returned.addArea(computeLineIntersection(rect.getWest()));
				if (returned.getObjects().size() == 0) return new FGEEmptyArea();
				if (returned.getObjects().size() == 1) return returned.getObjects().firstElement();
				return returned;
			}
		}

		// Both rectangle are opened

		FGEUnionArea returned = new FGEUnionArea();
		returned.addArea(getNorth().intersect(rect.getNorth()));
		returned.addArea(getNorth().intersect(rect.getSouth()));
		returned.addArea(getNorth().intersect(rect.getEast()));
		returned.addArea(getNorth().intersect(rect.getWest()));
		returned.addArea(getSouth().intersect(rect.getNorth()));
		returned.addArea(getSouth().intersect(rect.getSouth()));
		returned.addArea(getSouth().intersect(rect.getEast()));
		returned.addArea(getSouth().intersect(rect.getWest()));
		returned.addArea(getEast().intersect(rect.getNorth()));
		returned.addArea(getEast().intersect(rect.getSouth()));
		returned.addArea(getEast().intersect(rect.getEast()));
		returned.addArea(getEast().intersect(rect.getWest()));
		returned.addArea(getWest().intersect(rect.getNorth()));
		returned.addArea(getWest().intersect(rect.getSouth()));
		returned.addArea(getWest().intersect(rect.getEast()));
		returned.addArea(getWest().intersect(rect.getWest()));

		if (returned.getObjects().size() == 0) return new FGEEmptyArea();
		if (returned.getObjects().size() == 1) return returned.getObjects().firstElement();
		return returned;
	}

	/*private FGEArea computeRectangleIntersection(FGERectangle rect)
	{
		Vector<FGEPoint> pts = new Vector<FGEPoint>() {
			@Override
			public synchronized boolean add(FGEPoint o)
			{
				if (!contains(o))
					return super.add(o);
				return false;
			}
		};

		List<FGESegment> sl = rect.getFrameSegments();
		for (FGESegment seg : sl) {
			FGEArea a = intersect(seg);
			if (a instanceof FGEPoint) pts.add((FGEPoint)a);
			else if (a instanceof FGESegment) {
				pts.add(((FGESegment)a).getP1());
				pts.add(((FGESegment)a).getP2());
			}
			else if (a instanceof FGEUnionArea) {
				for (FGEArea a2: ((FGEUnionArea)a).getObjects()) {
					if (a2 instanceof FGEPoint) pts.add((FGEPoint)a2);
					if (a2 instanceof FGESegment) {
						pts.add(((FGESegment)a2).getP1());
						pts.add(((FGESegment)a2).getP2());
					}
				}
			}
		}

		FGEPoint ne,nw,se,sw;
		ne = new FGEPoint(x+width,y);
		nw = new FGEPoint(x,y);
		se = new FGEPoint(x+width,y+height);
		sw = new FGEPoint(x,y+height);
		if (rect.containsPoint(ne)) pts.add(ne);
		if (rect.containsPoint(nw)) pts.add(nw);
		if (rect.containsPoint(se)) pts.add(se);
		if (rect.containsPoint(sw)) pts.add(sw);

		if (pts.size() == 0) return new FGEEmptyArea();

		else if (pts.size() == 2) {
			return new FGESegment(pts.firstElement(),pts.elementAt(1));
		}

		else if (pts.size() != 4) {
			logger.warning("Strange situation here while computeRectangleIntersection between "+this+" and "+rect);
		}

		double minx = java.lang.Double.POSITIVE_INFINITY;
		double miny = java.lang.Double.POSITIVE_INFINITY;
		double maxx = java.lang.Double.NEGATIVE_INFINITY;
		double maxy = java.lang.Double.NEGATIVE_INFINITY;
		for (FGEPoint p : pts) {
			if (p.x<minx) minx = p.x;
			if (p.y<miny) miny = p.y;
			if (p.x>maxx) maxx = p.x;
			if (p.y>maxy) maxy = p.y;
		}
		return new FGERectangle(minx,miny,maxx-minx,maxy-miny,Filling.FILLED);
	}*/

	private FGEArea computeHalfPlaneIntersection(FGEHalfPlane hp)
	{
		if (hp.containsArea(this)) return this.clone();
		if (computeLineIntersection(hp.line) instanceof FGEEmptyArea) {
			return new FGEEmptyArea();
		}
		else {
			if (logger.isLoggable(Level.FINE))
				logger.fine("computeHalfPlaneIntersection() for rectangle when halfplane cross rectangle");

			if (getIsFilled()) {

				FGEArea a = computeLineIntersection(hp.line);
				Vector<FGEPoint> pts = new Vector<FGEPoint>();
				if (a instanceof FGEUnionArea && ((FGEUnionArea)a).isUnionOfPoints() && ((FGEUnionArea)a).getObjects().size() == 2) {
					pts.add((FGEPoint)((FGEUnionArea)a).getObjects().firstElement());
					pts.add((FGEPoint)((FGEUnionArea)a).getObjects().elementAt(1));
				}
				else if (a instanceof FGESegment) {
					pts.add(((FGESegment)a).getP1());
					pts.add(((FGESegment)a).getP2());
				}
				FGEPoint ne,nw,se,sw;
				ne = new FGEPoint(x+width,y);
				nw = new FGEPoint(x,y);
				se = new FGEPoint(x+width,y+height);
				sw = new FGEPoint(x,y+height);
				if (hp.containsPoint(ne) && !pts.contains(ne)) pts.add(ne);
				if (hp.containsPoint(nw) && !pts.contains(nw)) pts.add(nw);
				if (hp.containsPoint(se) && !pts.contains(se)) pts.add(se);
				if (hp.containsPoint(sw) && !pts.contains(sw)) pts.add(sw);
				return FGEPolygon.makeArea(Filling.FILLED,pts);
			}

			else { // open rectangle

				Vector<FGEArea> returned = new Vector<FGEArea>();
				returned.add(hp.intersect(getNorth()));
				returned.add(hp.intersect(getSouth()));
				returned.add(hp.intersect(getEast()));
				returned.add(hp.intersect(getWest()));
				return FGEUnionArea.makeUnion(returned);

			}
		}
	}

	private FGEArea computeHalfBandIntersection(FGEHalfBand hb)
	{
		FGEArea bandIntersection = computeBandIntersection(hb.embeddingBand);
		FGEArea returned = bandIntersection.intersect(hb.halfPlane);
		return returned;
	}

	private FGEArea computeBandIntersection(FGEBand band)
	{
		if (getIsFilled()) {

			Vector<FGEPoint> pts = new Vector<FGEPoint>();

			FGEArea a1 = intersect(new FGELine(band.line1));
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

			FGEArea a2 = intersect(new FGELine(band.line2));
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

			if (band.containsPoint(getNorthEastPt())) pts.add(getNorthEastPt());
			if (band.containsPoint(getNorthWestPt())) pts.add(getNorthWestPt());
			if (band.containsPoint(getSouthEastPt())) pts.add(getSouthEastPt());
			if (band.containsPoint(getSouthWestPt())) pts.add(getSouthWestPt());

			if (pts.size() == 0) return new FGEEmptyArea();

			if (pts.size() == 1) return pts.firstElement().clone();

			return FGEPolygon.makeArea(Filling.FILLED,pts);
		}

		else { // Open rectangle

			Vector<FGEArea> returned = new Vector<FGEArea>();
			returned.add(band.intersect(getNorth()));
			returned.add(band.intersect(getSouth()));
			returned.add(band.intersect(getEast()));
			returned.add(band.intersect(getWest()));
			return FGEUnionArea.makeUnion(returned);
		}
	}

	private FGEArea computeLineIntersection(FGEAbstractLine line)
	{

		FGESegment north = getNorth();
		FGESegment south = getSouth();
		FGESegment east =  getEast();
		FGESegment west =  getWest();

		if (line == null) return new FGEEmptyArea();

		if (line.overlap(north)) return north.intersect(line);//north.clone();
		if (line.overlap(south)) return south.intersect(line);//south.clone();
		if (line.overlap(east)) return east.intersect(line);//east.clone();
		if (line.overlap(west)) return west.intersect(line);//west.clone();

		Vector<FGEPoint> crossed = new Vector<FGEPoint>();

		try {
			if (north.intersectsInsideSegment(line)) {
				FGEPoint intersection = north.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) crossed.add(intersection);
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (south.intersectsInsideSegment(line)) {
				FGEPoint intersection = south.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) crossed.add(intersection);
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (east.intersectsInsideSegment(line)) {
				FGEPoint intersection = east.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) crossed.add(intersection);
			}
		} catch (ParallelLinesException e) {
			// don't care
		}
		try {
			if (west.intersectsInsideSegment(line)) {
				FGEPoint intersection = west.getLineIntersection(line);
				if (line.contains(intersection) && !crossed.contains(intersection)) crossed.add(intersection);
			}
		} catch (ParallelLinesException e) {
			// don't care
		}

		if (getIsFilled()) {
			if (line instanceof FGEHalfLine) {
				if (containsPoint(((FGEHalfLine)line).getLimit())) {
					if (!crossed.contains(((FGEHalfLine)line).getLimit()))
						crossed.add(((FGEHalfLine)line).getLimit());
				}
			}
			else if (line instanceof FGESegment) {
				if (containsPoint(((FGESegment)line).getP1()))
					if (!crossed.contains(((FGESegment)line).getP1()))
						crossed.add(((FGESegment)line).getP1());
				if (containsPoint(((FGESegment)line).getP2()))
					if (!crossed.contains(((FGESegment)line).getP2()))
						crossed.add(((FGESegment)line).getP2());
			}
		}

		if (crossed.size() == 0) return new FGEEmptyArea();

		FGEArea returned;

		if (crossed.size() == 1) returned = crossed.firstElement();

		else if (crossed.size() == 2) {
			FGEPoint p1 = crossed.firstElement();
			FGEPoint p2 = crossed.elementAt(1);
			if (getIsFilled()) returned = new FGESegment(p1,p2);
			else returned = FGEUnionArea.makeUnion(p1,p2);
		}
		else if (crossed.size() == 4) { // Crossed on edges
			FGEPoint p1 = crossed.firstElement();
			FGEPoint p2 = crossed.elementAt(1);
			// Choose those because north and south tested at first (cannot intersect)
			if (getIsFilled()) returned = new FGESegment(p1,p2);
			else returned = FGEUnionArea.makeUnion(p1,p2);
		}
		else {
			logger.warning("crossed.size()="+crossed.size()+" How is it possible ??? rectangle="+this+ " line="+line+"\ncrossed="+crossed);
			return null;
		}

		return returned;

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
		if (containsArea(area)) {
			return area.clone();
		}
		if (area instanceof FGEAbstractLine) return computeLineIntersection((FGEAbstractLine)area);
		if (area instanceof FGERectangle) return computeRectangleIntersection((FGERectangle)area);
		if (area instanceof FGEHalfPlane) return computeHalfPlaneIntersection((FGEHalfPlane)area);
		if (area instanceof FGEBand) return computeBandIntersection((FGEBand)area);
		if (area instanceof FGEHalfBand) return computeHalfBandIntersection((FGEHalfBand)area);
		if (area instanceof FGEPolygon) return FGEShape.AreaComputation.computeShapeIntersection(this, (FGEPolygon)area);

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
	public final FGEArea union(FGEArea area)
	{
		if (containsArea(area)) return clone();
		if (area.containsArea(this)) return area.clone();

		if (area instanceof FGERectangle && ((FGERectangle)area).getIsFilled() == getIsFilled()) {
			FGERectangle r = (FGERectangle)area;
			if ((containsArea(r.getNorth()) && getWidth() == r.getWidth())
					|| (containsArea(r.getSouth()) && getWidth() == r.getWidth())
					|| (containsArea(r.getEast()) && getHeight() == r.getHeight())
					|| (containsArea(r.getWest()) && getHeight() == r.getHeight())) {
				return rectangleUnion(r);
			}
		}
		return new FGEUnionArea(this,area);
	}

	@Override
	public List<FGEPoint> getControlPoints()
	{
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(new FGEPoint(x,y));
		returned.add(new FGEPoint(x+width,y));
		returned.add(new FGEPoint(x,y+height));
		returned.add(new FGEPoint(x+width,y+height));
		returned.add(getNorth().getMiddle());
		returned.add(getEast().getMiddle());
		returned.add(getWest().getMiddle());
		returned.add(getSouth().getMiddle());
		return returned;
	}


	@Override
	public boolean containsPoint(FGEPoint p)
	{
		if ((p.x >= getX()-EPSILON)
				&& (p.x <=getX()+getWidth()+EPSILON)
				&& (p.y >= getY()-EPSILON)
				&& (p.y <= getY()+getHeight()+EPSILON)) {
			if (!getIsFilled()) {
				FGESegment north = new FGESegment(new FGEPoint(x,y),new FGEPoint(x+width,y));
				FGESegment south = new FGESegment(new FGEPoint(x,y+height),new FGEPoint(x+width,y+height));
				FGESegment west = new FGESegment(new FGEPoint(x,y),new FGEPoint(x,y+height));
				FGESegment east = new FGESegment(new FGEPoint(x+width,y),new FGEPoint(x+width,y+height));
				return (north.contains(p)
						|| south.contains(p)
						|| east.contains(p)
						|| west.contains(p));
			}
			else {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l)
	{
		if (l instanceof FGEHalfLine) return false;
		if (l instanceof FGESegment) {
			return (containsPoint(l.getP1()) && containsPoint(l.getP2()));
		}
		return false;
	}

	@Override
	public boolean containsArea(FGEArea a)
	{
		if (a instanceof FGEPoint) return containsPoint((FGEPoint)a);
		if (a instanceof FGESegment && !(a instanceof FGEHalfLine)) return containsPoint(((FGESegment)a).getP1()) && containsPoint(((FGESegment)a).getP2());
		if (a instanceof FGEShape) return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>)a, this);
		return false;
	}


	@Override
	public FGEArea transform(AffineTransform t)
	{
		// Better implementation allowing rotations

		FGEPoint p1 = (new FGEPoint(getX(),getY())).transform(t);
		FGEPoint p2 = (new FGEPoint(getX()+getWidth(),getY())).transform(t);
		FGEPoint p3 = (new FGEPoint(getX(),getY()+getHeight())).transform(t);
		FGEPoint p4 = (new FGEPoint(getX()+getWidth(),getY()+getHeight())).transform(t);

		return FGEPolygon.makeArea(_filling,p1,p2,p3,p4);

		// Old implementation follows (commented)

		/*	FGEPoint p1 = (new FGEPoint(getX(),getY())).transform(t);
		FGEPoint p2 = (new FGEPoint(getX()+getWidth(),getY()+getHeight())).transform(t);

		// TODO: if transformation contains a rotation, turn into a regular polygon
		return new FGERectangle(
				Math.min(p1.x,p2.x),
				Math.min(p1.y,p2.y),
				Math.abs(p1.x-p2.x),
				Math.abs(p1.y-p2.y),
				_filling);*/
	}

	@Override
	public void paint(FGEGraphics g)
	{
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillRect(getX(),getY(),getWidth(),getHeight());
		}
		g.useDefaultForegroundStyle();
		g.drawRect(getX(),getY(),getWidth(),getHeight());
	}

	@Override
	public String toString()
	{
		return "FGERectangle: ("+x+","+y+","+width+","+height+")";
	}

	@Override
	public String getStringRepresentation()
	{
		return toString();
	}

	@Override
	public FGERectangle getBoundingBox()
	{
		return clone();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FGERectangle) {
			FGERectangle p = (FGERectangle)obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return ((Math.abs(getX()-p.getX()) <= EPSILON) &&
					(Math.abs(getY()-p.getY()) <= EPSILON) &&
					(Math.abs(getWidth()-p.getWidth()) <= EPSILON) &&
					(Math.abs(getHeight()-p.getHeight()) <= EPSILON));
		}
		return super.equals(obj);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation)
	{
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public FGESegment getAnchorAreaFrom(SimplifiedCardinalDirection orientation)
	{
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			return getNorth();
		}
		else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			return getSouth();
		}
		else if (orientation == SimplifiedCardinalDirection.EAST) {
			return getEast();
		}
		else if (orientation == SimplifiedCardinalDirection.WEST) {
			return getWest();
		}
		logger.warning("Unexpected: "+orientation);
		return null;
	}

	public FGERectangle rectangleUnion(FGERectangle r)
	{
		double x1 = Math.min(x, r.x);
		double x2 = Math.max(x + width, r.x + r.width);
		double y1 = Math.min(y, r.y);
		double y2 = Math.max(y + height, r.y + r.height);
		return new FGERectangle(x1, y1, x2 - x1, y2 - y1,Filling.FILLED);
	}

	/**
	 * This area is finite, so always return true
	 */
	@Override
	public final boolean isFinite()
	{
		return true;
	}

	/**
	 * This area is finite, so always return null
	 */
	@Override
	public final FGERectangle getEmbeddingBounds()
	{
		return new FGERectangle(x,y,width,height,Filling.FILLED);
	}

	/**
	 * Build and return new polylin representing outline
	 * @return
	 */
	public FGEPolylin getOutline()
	{
		return new FGEPolylin(getNorthEastPt(),getSouthEastPt(),getSouthWestPt(),getNorthWestPt());
	}


}
