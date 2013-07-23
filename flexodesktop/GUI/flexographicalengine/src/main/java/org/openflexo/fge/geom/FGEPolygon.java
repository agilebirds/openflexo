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
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;
import java.util.Vector;
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

/**
 * The <code>FGEPolygon</code> class encapsulates a description of a closed, two-dimensional region within a coordinate space. This region
 * is bounded by an arbitrary number of line segments, each of which is one side of the polygon.
 * 
 * Some parts of this code are "inspired" from java.awt.Polygon implementation
 * 
 * @author sylvain
 * 
 */
public class FGEPolygon implements FGEGeometricObject<FGEPolygon>, FGEShape<FGEPolygon> {

	private static final Logger logger = Logger.getLogger(FGEPolygon.class.getPackage().getName());

	protected Filling _filling;

	protected Vector<FGEPoint> _points;
	protected Vector<FGESegment> _segments;

	private FGERectangle bounds;

	public FGEPolygon() {
		this(Filling.NOT_FILLED);
	}

	public FGEPolygon(Filling filling) {
		super();
		_filling = filling;
		_points = new Vector<FGEPoint>();
		_segments = new Vector<FGESegment>();
	}

	public FGEPolygon(Filling filling, List<FGEPoint> points) {
		this(filling);
		if (points != null) {
			for (FGEPoint p : points) {
				addToPoints(p);
			}
		}
	}

	public FGEPolygon(Filling filling, FGEPoint... points) {
		this(filling);
		for (FGEPoint p : points) {
			addToPoints(p);
		}
	}

	@Override
	public boolean getIsFilled() {
		return _filling == Filling.FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		_filling = filled ? Filling.FILLED : Filling.NOT_FILLED;
	}

	@Override
	public FGEPoint getCenter() {
		if (_points.size() == 0) {
			return new FGEPoint(0, 0);
		}

		double sumX = 0;
		double sumY = 0;

		for (FGEPoint p : _points) {
			sumX += p.x;
			sumY += p.y;
		}
		return new FGEPoint(sumX / _points.size(), sumY / _points.size());
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		return getPoints();
	}

	public void clearPoints() {
		_points.clear();
		_segments.clear();
	}

	public Vector<FGEPoint> getPoints() {
		return _points;
	}

	public void setPoints(Vector<FGEPoint> points) {
		_points.clear();
		_segments.clear();
		for (FGEPoint p : points) {
			addToPoints(p);
		}
	}

	public void addToPoints(FGEPoint aPoint) {
		_points.add(aPoint);
		if (_points.size() > 1) {
			FGESegment s2 = new FGESegment(_points.elementAt(_points.size() - 2), _points.elementAt(_points.size() - 1));
			if (_segments.size() <= _points.size() - 2) {
				_segments.add(s2);
			} else {
				_segments.set(_points.size() - 2, s2);
			}
			FGESegment s3 = new FGESegment(_points.elementAt(_points.size() - 1), _points.elementAt(0));
			_segments.add(s3);
		}
		reCalculateBounds();
	}

	public void removeFromPoints(FGEPoint aPoint) {
		_points.remove(aPoint);
		reCalculateBounds();
	}

	public Vector<FGESegment> getSegments() {
		return _segments;
	}

	public int getPointsNb() {
		return _points.size();
	}

	public FGEPoint getPointAt(int index) {
		return _points.elementAt(index);
	}

	public void geometryChanged() {
		reCalculateBounds();
	}

	private void reCalculateBounds() {
		double boundsMinX = Double.POSITIVE_INFINITY;
		double boundsMinY = Double.POSITIVE_INFINITY;
		double boundsMaxX = Double.NEGATIVE_INFINITY;
		double boundsMaxY = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < getPointsNb(); i++) {
			FGEPoint p = getPointAt(i);
			double x = p.getX();
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			double y = p.getY();
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		bounds = new FGERectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, _filling);
	}

	@Override
	public FGERectangle getBoundingBox() {
		return bounds;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (l instanceof FGEHalfLine) {
			return false;
		}
		if (l instanceof FGESegment) {
			return containsPoint(l.getP1()) && containsPoint(l.getP2());
		}
		return false;
	}

	@Override
	public boolean contains(double x, double y) {
		FGEPoint pt = new FGEPoint(x, y);
		for (FGESegment s : getSegments()) {
			if (s.contains(pt)) {
				return true;
			}
		}

		if (!getIsFilled()) {
			return false;
		}

		// Otherwise test on inside

		if (getPointsNb() <= 2 || !bounds.contains(x, y)) {
			return false;
		}
		int hits = 0;

		FGEPoint lastPoint = getPointAt(getPointsNb() - 1);

		double lastx = lastPoint.getX();
		double lasty = lastPoint.getY();

		FGEPoint currentPoint;
		double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < getPointsNb(); lastx = curx, lasty = cury, i++) {

			currentPoint = getPointAt(i);

			curx = currentPoint.getX();
			cury = currentPoint.getY();

			if (cury == lasty) {
				continue;
			}

			double leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < test2 / (lasty - cury) * (lastx - curx)) {
				hits++;
			}
		}

		return (hits & 1) != 0;
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
	public FGEPolygon clone() {
		try {
			return (FGEPolygon) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		FGEPoint returnedPoint = null;
		double smallestDistance = Double.POSITIVE_INFINITY;

		for (FGESegment segment : _segments) {
			double sqDistanceToSegment = segment.ptSegDistSq(aPoint);
			if (sqDistanceToSegment < smallestDistance) {
				returnedPoint = segment.getNearestPointOnSegment(aPoint);
				smallestDistance = sqDistanceToSegment;
			}
		}
		return returnedPoint;
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

		FGEPoint returned = null;
		double minimalDistanceSq = Double.POSITIVE_INFINITY;

		for (FGESegment segment : _segments) {
			if (FGESegment.intersectsInsideSegment(segment, hl)) {
				try {
					FGEPoint p = FGEAbstractLine.getLineIntersection(segment, hl);
					double distSq = FGEPoint.distanceSq(from, p);
					if (distSq < minimalDistanceSq) {
						returned = p;
						minimalDistanceSq = distSq;
					}
				} catch (ParallelLinesException e) {
					// Don't care
				}
			}
		}
		// logger.info("from: "+from+" orientation="+orientation+" return "+returned);
		return returned;
	}

	private FGEArea computeAreaIntersection(FGEArea area) {
		// System.out.println("Intersection between "+this+" and "+area);

		boolean fullyInside = true;
		boolean fullyOutside = true;

		for (FGESegment s : getSegments()) {
			if (!area.containsArea(s)) {
				fullyInside = false;
			}
			if (area.containsPoint(s.getP1()) || area.containsPoint(s.getP2())) {
				fullyOutside = false;
			}
		}

		if (fullyOutside) {
			// System.out.println("Fully outside");
			return new FGEEmptyArea();
		}

		if (fullyInside) {
			// System.out.println("Fully inside");
			return this.clone();
		}

		// Otherwise non null intersection

		FGERectangle filledBoundingBox = getBoundingBox().clone();
		filledBoundingBox.setIsFilled(true);
		FGEArea boundingIntersect = area.intersect(filledBoundingBox);

		// System.out.println("Y'a une intersection zarrebi: boundingIntersect="+boundingIntersect+" resultat: "+intersect(boundingIntersect));

		return intersect(boundingIntersect);

	}

	private FGEArea computeLineIntersection(FGEAbstractLine line) {
		Vector<FGEPoint> crossed = new Vector<FGEPoint>();
		for (FGESegment s : _segments) {
			if (line.overlap(s)) {
				return s.clone(); // TODO: perform union of potential multiple overlaping segments
			}
			try {
				if (s.intersectsInsideSegment(line)) {
					FGEPoint intersection = s.getLineIntersection(line);
					if (line.contains(intersection) && (crossed.size() == 0 || !crossed.lastElement().equals(intersection))) {
						crossed.add(intersection);
					}
				}
			} catch (ParallelLinesException e) {
				// don't care
			}
		}

		if (crossed.size() == 0) {
			return new FGEEmptyArea();
		}

		if (crossed.size() == 1) {
			return crossed.firstElement();
		} else if (crossed.size() == 2) {
			if (getIsFilled()) {
				return new FGESegment(crossed.firstElement(), crossed.elementAt(1));
			} else {
				return FGEUnionArea.makeUnion(crossed.firstElement(), crossed.elementAt(1));
			}
		} else {
			// TODO: not yet implemented for filled polygon
			logger.warning("computeLineIntersection() not yet implemented for polygon");
			return FGEUnionArea.makeUnion(crossed);
		}

	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		// logger.info("Polygon "+this+" intersect with "+area);

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
			return ((FGERectangle) area).intersect(this);
		}
		if (area instanceof FGEHalfPlane) {
			return ((FGEHalfPlane) area).intersect(this);
		}
		if (area instanceof FGEPolygon) {
			return FGEShape.AreaComputation.computeShapeIntersection(this, (FGEPolygon) area);
		}
		if (area instanceof FGEBand) {
			return computeAreaIntersection(area);
		}
		if (area instanceof FGEHalfBand) {
			return computeAreaIntersection(area);
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
	public boolean containsPoint(FGEPoint p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGESegment) {
			return containsPoint(((FGESegment) a).getP1()) && containsPoint(((FGESegment) a).getP2());
		}
		if (a instanceof FGEShape) {
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	@Override
	public FGEPolygon transform(AffineTransform t) {
		Vector<FGEPoint> points = new Vector<FGEPoint>();
		for (FGEPoint p : _points) {
			points.add(p.transform(t));
		}
		FGEPolygon returned = new FGEPolygon(_filling, points);
		return returned;
	}

	@Override
	public void paint(FGEGraphics g) {
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillPolygon(getPoints().toArray(new FGEPoint[getPoints().size()]));
		}
		g.useDefaultForegroundStyle();
		g.drawPolygon(getPoints().toArray(new FGEPoint[getPoints().size()]));

		/*
		g.setDefaultBackground(BackgroundStyle.makeEmptyBackground());
		g.setDefaultForeground(ForegroundStyle.makeStyle(Color.GRAY,1,DashStyle.MEDIUM_DASHES));
		getBoundingBox().paint(g);*/
	}

	@Override
	public String toString() {
		return "FGEPolygon: " + _points;
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	// TODO: this algorithm is really not optimal since we explore all solution with a combinational algorithm !!!
	// As we stay with a small number of points, we keep it for now
	private static List<FGEPoint> sortToAvoidCuts(List<FGEPoint> aList) {
		for (FGEPoint p : aList) {
			if (Double.isNaN(p.x) || p.x == Double.POSITIVE_INFINITY || p.x == Double.NEGATIVE_INFINITY) {
				return aList; // On laisse tomber
			}
			if (Double.isNaN(p.y) || p.y == Double.POSITIVE_INFINITY || p.y == Double.NEGATIVE_INFINITY) {
				return aList; // On laisse tomber
			}
		}

		return sortToAvoidCuts(new Vector<FGEPoint>(), aList);
	}

	private static List<FGEPoint> sortToAvoidCuts(List<FGEPoint> aList, List<FGEPoint> remainingPoints) {
		if (remainingPoints.size() == 0) {
			return aList;
		}

		for (FGEPoint newP : remainingPoints) {
			Vector<FGESegment> sl = new Vector<FGESegment>();
			FGEPoint previous = null;
			for (FGEPoint p : aList) {
				if (previous != null) {
					sl.add(new FGESegment(previous, p));
				}
				previous = p;
			}
			boolean thisPointMightBeGood = true;
			if (sl.size() > 0) {
				// System.out.println("Segments = "+sl);
				FGESegment newSegment = new FGESegment(aList.get(aList.size() - 1), newP);
				for (FGESegment oldS : sl) {
					if (oldS.intersectsInsideSegment(newSegment, true)) {
						thisPointMightBeGood = false;
						// System.out.println("Failed because new segment "+newSegment+" intersect with segment "+oldS);
					}
				}
				if (remainingPoints.size() == 1) {
					// This is the last point, we must also check closure
					FGESegment closure = new FGESegment(newP, aList.get(0));
					// System.out.println("Also check closure = "+closure);
					for (FGESegment oldS : sl) {
						if (oldS.intersectsInsideSegment(closure, true)) {
							thisPointMightBeGood = false;
							// System.out.println("Failed because closure "+closure+" intersect with segment "+oldS);
						}
					}
				}
			}
			if (thisPointMightBeGood) {
				Vector<FGEPoint> newList = new Vector<FGEPoint>();
				newList.addAll(aList);
				newList.add(newP);
				Vector<FGEPoint> newRemainingList = new Vector<FGEPoint>();
				newRemainingList.addAll(remainingPoints);
				newRemainingList.remove(newP);
				List<FGEPoint> returned = sortToAvoidCuts(newList, newRemainingList);
				if (returned != null) {
					// System.out.println("return "+returned);
					return returned;
				}
			}
		}

		// No point found, return
		return null;
	}

	public static void main(String[] args) {

		for (int n = 1; n < 100; n++) {
			Vector<FGEPoint> pts = new Vector<FGEPoint>();

			logger.info("n=" + n);
			Random rand = new Random();

			for (int i = 0; i < n; i++) {
				pts.add(new FGEPoint(rand.nextDouble(), rand.nextDouble()));
			}

			logger.info("resultat: " + sortToAvoidCuts(pts));
		}
	}

	/**
	 * Make a new area given a list of point May return a rectangle, a polygon or a segment
	 * 
	 * @param filling
	 * @param points
	 * @return
	 */
	public static FGEArea makeArea(Filling filling, List<FGEPoint> somePoints) {
		if (somePoints.size() < 1) {
			throw new IllegalArgumentException("makeArea() called with " + somePoints.size() + " points");
		} else if (somePoints.size() == 1) {
			return new FGEPoint(somePoints.get(0));
		} else if (somePoints.size() == 2) {
			return new FGESegment(somePoints.get(0), somePoints.get(1));
		} else {
			List<FGEPoint> points = sortToAvoidCuts(somePoints);

			if (points.size() == 4) {
				boolean isRectangle = true;
				double minx = Double.POSITIVE_INFINITY;
				double miny = Double.POSITIVE_INFINITY;
				double maxx = Double.NEGATIVE_INFINITY;
				double maxy = Double.NEGATIVE_INFINITY;
				for (int i = 0; i < points.size(); i++) {
					if (points.get(i).x < minx) {
						minx = points.get(i).x;
					}
					if (points.get(i).y < miny) {
						miny = points.get(i).y;
					}
					if (points.get(i).x > maxx) {
						maxx = points.get(i).x;
					}
					if (points.get(i).y > maxy) {
						maxy = points.get(i).y;
					}
				}
				for (int i = 0; i < points.size(); i++) {
					FGEPoint p = points.get(i);
					if (!((p.x == minx || p.x == maxx) && (p.y == miny || p.y == maxy))) {
						isRectangle = false;
					}
				}
				if (isRectangle) {
					if (maxx - minx == 0) { // width = 0
						if (maxy - miny == 0) {
							return new FGEPoint(minx, miny);
						} else {
							return new FGESegment(minx, miny, minx, maxy);
						}
					} else {
						if (maxy - miny == 0) {
							return new FGESegment(minx, miny, maxx, miny); // height = 0;
						} else {
							return new FGERectangle(minx, miny, maxx - minx, maxy - miny, filling);
						}
					}
				}
			}

			return new FGEPolygon(filling, points);
		}
	}

	public static FGEArea makeArea(Filling filling, FGEPoint... points) {
		Vector<FGEPoint> v = new Vector<FGEPoint>();
		for (FGEPoint p : points) {
			v.add(p);
		}
		return makeArea(filling, v);
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		if (_points.size() <= 0 || !bounds.intersects(x, y, w, h)) {
			return false;
		}

		// Implement this;
		// Crossings cross = getCrossings(x, y, x+w, y+h);
		// return (cross != null && cross.covers(y, y+h));

		return true;
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		// TODO Implement this

		// Crossings cross = getCrossings(x, y, x+w, y+h);
		// return (cross == null || !cross.isEmpty());

		return false;
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle((int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight());
	}

	@Override
	public Rectangle2D getBounds2D() {
		return getBounds();
	}

	/**
	 * Returns an iterator object that iterates along the boundary of this <code>Polygon</code> and provides access to the geometry of the
	 * outline of this <code>Polygon</code>. An optional {@link AffineTransform} can be specified so that the coordinates returned in the
	 * iteration are transformed accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @return a {@link PathIterator} object that provides access to the geometry of this <code>Polygon</code>.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new PolygonPathIterator(this, at);
	}

	/**
	 * Returns an iterator object that iterates along the boundary of the <code>ShapeSpecification</code> and provides access to the geometry of the
	 * outline of the <code>ShapeSpecification</code>. Only SEG_MOVETO, SEG_LINETO, and SEG_CLOSE point types are returned by the iterator. Since
	 * polygons are already flat, the <code>flatness</code> parameter is ignored. An optional <code>AffineTransform</code> can be specified
	 * in which case the coordinates returned in the iteration are transformed accordingly.
	 * 
	 * @param at
	 *            an optional <code>AffineTransform</code> to be applied to the coordinates as they are returned in the iteration, or
	 *            <code>null</code> if untransformed coordinates are desired
	 * @param flatness
	 *            the maximum amount that the control points for a given curve can vary from colinear before a subdivided curve is replaced
	 *            by a straight line connecting the endpoints. Since polygons are already flat the <code>flatness</code> parameter is
	 *            ignored.
	 * @return a <code>PathIterator</code> object that provides access to the <code>ShapeSpecification</code> object's geometry.
	 */
	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	class PolygonPathIterator implements PathIterator {
		FGEPolygon poly;
		AffineTransform transform;
		int index;

		public PolygonPathIterator(FGEPolygon pg, AffineTransform at) {
			poly = pg;
			transform = at;
			if (pg.getPointsNb() == 0) {
				// Prevent a spurious SEG_CLOSE segment
				index = 1;
			}
		}

		/**
		 * Returns the winding rule for determining the interior of the path.
		 * 
		 * @return an integer representing the current winding rule.
		 * @see PathIterator#WIND_NON_ZERO
		 */
		@Override
		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		/**
		 * Tests if there are more points to read.
		 * 
		 * @return <code>true</code> if there are more points to read; <code>false</code> otherwise.
		 */
		@Override
		public boolean isDone() {
			return index > poly.getPointsNb();
		}

		/**
		 * Moves the iterator forwards, along the primary direction of traversal, to the next segment of the path when there are more points
		 * in that direction.
		 */
		@Override
		public void next() {
			index++;
		}

		/**
		 * Returns the coordinates and type of the current path segment in the iteration. The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE. A <code>float</code> array of length 2 must be passed in and can be used to store the
		 * coordinates of the point(s). Each point is stored as a pair of <code>float</code> x,&nbsp;y coordinates. SEG_MOVETO and
		 * SEG_LINETO types return one point, and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>float</code> array that specifies the coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(float[] coords) {
			if (index >= poly.getPointsNb()) {
				return SEG_CLOSE;
			}
			FGEPoint p = poly.getPointAt(index);
			coords[0] = (float) p.x;
			coords[1] = (float) p.y;
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return index == 0 ? SEG_MOVETO : SEG_LINETO;
		}

		/**
		 * Returns the coordinates and type of the current path segment in the iteration. The return value is the path segment type:
		 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE. A <code>double</code> array of length 2 must be passed in and can be used to store the
		 * coordinates of the point(s). Each point is stored as a pair of <code>double</code> x,&nbsp;y coordinates. SEG_MOVETO and
		 * SEG_LINETO types return one point, and SEG_CLOSE does not return any points.
		 * 
		 * @param coords
		 *            a <code>double</code> array that specifies the coordinates of the point(s)
		 * @return an integer representing the type and coordinates of the current path segment.
		 * @see PathIterator#SEG_MOVETO
		 * @see PathIterator#SEG_LINETO
		 * @see PathIterator#SEG_CLOSE
		 */
		@Override
		public int currentSegment(double[] coords) {
			if (index >= poly.getPointsNb()) {
				return SEG_CLOSE;
			}
			FGEPoint p = poly.getPointAt(index);
			coords[0] = (float) p.x;
			coords[1] = (float) p.y;
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return index == 0 ? SEG_MOVETO : SEG_LINETO;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEPolygon) {
			FGEPolygon p = (FGEPolygon) obj;
			if (getPointsNb() != p.getPointsNb()) {
				return false;
			}
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			// Test same order with different indexes
			for (int j = 0; j < getPointsNb(); j++) {
				boolean testWithIndexJ = true;
				for (int i = 0; i < getPointsNb(); i++) {
					int k = i + j;
					if (k >= getPointsNb()) {
						k = k - getPointsNb();
					}
					if (!getPointAt(i).equals(p.getPointAt(k))) {
						testWithIndexJ = false;
					}
				}
				if (testWithIndexJ) {
					return true;
				}
			}
			// Test reverse order with different indexes
			for (int j = 0; j < getPointsNb(); j++) {
				boolean testWithIndexJ = true;
				for (int i = 0; i < getPointsNb(); i++) {
					int k = -i + j;
					if (k < 0) {
						k = k + getPointsNb();
					}
					if (!getPointAt(i).equals(p.getPointAt(k))) {
						testWithIndexJ = false;
					}
				}
				if (testWithIndexJ) {
					return true;
				}
			}
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		Vector<FGESegment> keptSegments = new Vector<FGESegment>();

		for (FGESegment s : getSegments()) {
			FGEHalfLine hl = FGEHalfLine.makeHalfLine(s.getMiddle(), orientation);
			/*switch (orientation) {
			case NORTH:
				hl = new FGEHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(0,-1)));
				break;
			case SOUTH:
				hl = new FGEHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(0,1)));
				break;
			case EAST:
				hl = new FGEHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(1,0)));
				break;
			case WEST:
				hl = new FGEHalfLine(s.getMiddle(),s.getMiddle().transform(AffineTransform.getTranslateInstance(-1,0)));
				break;
			default:
				break;
			}*/
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (FGESegment s2 : getSegments()) {
				if (!s.equals(s2)) {
					FGEArea intersect = s2.intersect(hl);
					if (intersect instanceof FGEPoint) {
						cutsAnOtherSegment = true;
					} else if (intersect instanceof FGEEmptyArea) {
						;
					} else {
						logger.warning("Unexpected intersection: " + intersect);
						cutsAnOtherSegment = true;
					}
				}
			}
			if (!cutsAnOtherSegment) {
				keptSegments.add(s);
			}
		}

		if (keptSegments.size() == 0) {
			return new FGEEmptyArea();
		}

		else if (keptSegments.size() == 1) {
			return keptSegments.firstElement();
		}

		else {
			// Chains segments
			Vector<FGESegment> chain = new Vector<FGESegment>();
			for (FGESegment s : keptSegments) {
				if (chain.size() == 0) {
					chain.add(s);
				} else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new FGESegment(s.getP2(), s.getP1()));
					} else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					} else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					} else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new FGESegment(s.getP2(), s.getP1()));
					} else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}

			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			pts.add(chain.firstElement().getP1());
			for (FGESegment s : chain) {
				pts.add(s.getP2());
			}

			// logger.info("anchor area for "+orientation+" : "+new FGEPolylin(pts) );

			return new FGEPolylin(pts);
		}

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
		return getBoundingBox();
	}

	/**
	 * Build and return new polylin representing outline
	 * 
	 * @return
	 */
	public FGEPolylin getOutline() {
		Vector<FGEPoint> pts = new Vector<FGEPoint>();
		pts.addAll(getPoints());
		pts.add(getPoints().firstElement());
		return new FGEPolylin(pts);
	}

}
