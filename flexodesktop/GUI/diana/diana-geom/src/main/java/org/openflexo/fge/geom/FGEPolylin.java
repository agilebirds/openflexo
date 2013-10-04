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
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

/**
 * The <code>FGEPolylin</code> class encapsulates a description of an open path, defined as an arbitrary number of line segments
 * 
 * @author sylvain
 * 
 */
public class FGEPolylin implements FGEGeometricObject<FGEPolylin> {

	private static final Logger logger = Logger.getLogger(FGEPolylin.class.getPackage().getName());

	protected Vector<FGEPoint> _points;
	protected Vector<FGESegment> _segments;

	private FGERectangle bounds;

	public FGEPolylin() {
		super();
		_points = new Vector<FGEPoint>();
		_segments = new Vector<FGESegment>();
	}

	public FGEPolylin(List<FGEPoint> points) {
		this();
		for (FGEPoint p : points) {
			addToPoints(p);
		}
	}

	public FGEPolylin(FGEPoint... points) {
		this();
		for (FGEPoint p : points) {
			addToPoints(p);
		}
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
			_segments.add(s2);
		}
		reCalculateBounds();
	}

	public void removeFromPoints(FGEPoint aPoint) {
		_points.remove(aPoint);
		reCalculateBounds();
	}

	public void removePointAtIndex(int index) {
		_points.remove(index);
		if (index == getSegmentNb()) {
			// Last segment
			_segments.remove(index - 1);
		} else {
			if (index < _segments.size()) {
				_segments.remove(index);
			}
			if (index >= 1 && index - 1 < _segments.size() && index < _points.size()) {
				_segments.elementAt(index - 1).setP2(_points.elementAt(index));
			}
		}
		boundsChanged();
	}

	public void insertPointAtIndex(FGEPoint aPoint, int index) {
		_points.add(index, aPoint);
		if (index >= 1 && index - 1 < _segments.size()) {
			_segments.get(index - 1).setP2(aPoint);
		}
		if (index + 1 < _points.size()) {
			_segments.add(index, new FGESegment(aPoint, _points.get(index + 1)));
		}
		boundsChanged();
	}

	public void updatePointAt(int index, FGEPoint aPoint) {
		_points.elementAt(index).setX(aPoint.x);
		_points.elementAt(index).setY(aPoint.y);
		if (getPointsNb() > 1) {
			if (index < _segments.size()) {
				_segments.elementAt(index).setP1(aPoint);
			}
			if (index > 0) {
				_segments.elementAt(index - 1).setP2(aPoint);
			}
		}
		boundsChanged();
	}

	public int getPointsNb() {
		return _points.size();
	}

	public FGEPoint getPointAt(int index) {
		if (index >= 0 && index < _points.size()) {
			return _points.elementAt(index);
		}
		return null;
	}

	public FGEPoint getFirstPoint() {
		return getPointAt(0);
	}

	public FGEPoint getLastPoint() {
		return getPointAt(getPointsNb() - 1);
	}

	public int getPointIndex(FGEPoint point) {
		int index = 0;
		for (FGEPoint p : getPoints()) {
			if (p.equals(point)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public Vector<FGESegment> getSegments() {
		return _segments;
	}

	public int getSegmentNb() {
		return _segments.size();
	}

	public FGESegment getSegmentAt(int index) {
		if (index >= 0 && index < _segments.size()) {
			return _segments.elementAt(index);
		}
		return null;
	}

	public int getSegmentIndex(FGESegment segment) {
		int index = 0;
		for (FGESegment s : getSegments()) {
			if (s.equals(segment)) {
				return index;
			}
			index++;
		}
		return -1;
	}

	public FGESegment getFirstSegment() {
		return getSegmentAt(0);
	}

	public FGESegment getLastSegment() {
		return getSegmentAt(getSegmentNb() - 1);
	}

	public FGESegment getBiggestSegment() {
		double length = 0;
		FGESegment returned = null;
		for (FGESegment seg : getSegments()) {
			if (seg.getLength() > length) {
				length = seg.getLength();
				returned = seg;
			}
		}
		return returned;
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
		bounds = new FGERectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, Filling.FILLED);
		boundsChanged = false;
	}

	public FGERectangle getBoundingBox() {
		if (boundsChanged || bounds == null) {
			reCalculateBounds();
		}
		return bounds;
	}

	private boolean boundsChanged = false;

	public void boundsChanged() {
		boundsChanged = true;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		if (l instanceof FGESegment) {
			for (FGESegment s : _segments) {
				if (s.containsLine(l)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean contains(double x, double y) {
		return containsPoint(new FGEPoint(x, y));
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
	public FGEPolylin clone() {
		try {
			return (FGEPolylin) super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		return nearestOutlinePoint(aPoint);
	}

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
		return returned;
	}

	/**
	 * Return the closest segment of supplied point
	 * 
	 * @return
	 */
	public FGESegment getNearestSegment(FGEPoint p) {
		double shortestDistance = Double.POSITIVE_INFINITY;
		FGESegment returned = null;
		for (FGESegment s : getSegments()) {
			double distance = FGEPoint.distance(p, s.getNearestPoint(p));
			if (distance <= shortestDistance) {
				returned = s;
				shortestDistance = distance;
			}
		}
		return returned;
	}

	public double getRelativeLocation(FGEPoint p) {
		double cumulated = 0;
		FGESegment s = getNearestSegment(p);
		FGEPoint proj = s.getNearestPointOnSegment(p);
		int index = getSegmentIndex(s);
		for (int i = 0; i < index; i++) {
			cumulated += getSegmentAt(i).getLength();
		}
		cumulated += s.getLength() * s.getRelativeLocation(proj);
		return cumulated / getLength();
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
					if (line.contains(intersection)) {
						crossed.add(intersection);
					}
				}
			} catch (ParallelLinesException e) {
				// don't care
			}
		}

		return FGEUnionArea.makeUnion(crossed);
	}

	private FGEArea computePolylinIntersection(FGEPolylin polylin) {
		/*logger.info("computePolylinIntersection()");
		logger.info("polylin1="+this);
		logger.info("polylin2="+polylin);*/
		Vector<FGEArea> unionAreas = new Vector<FGEArea>();
		for (FGESegment s1 : getSegments()) {
			for (FGESegment s2 : polylin.getSegments()) {
				FGEArea i = s1.intersect(s2);
				if (!(i instanceof FGEEmptyArea)) {
					unionAreas.add(i);
				}
			}
		}

		// logger.info("return="+FGEUnionArea.makeUnion(unionAreas));
		return FGEUnionArea.makeUnion(unionAreas);
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
		if (area instanceof FGEPolylin) {
			return computePolylinIntersection((FGEPolylin) area);
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
	public final FGEArea union(FGEArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof FGEPolylin) {
			FGEPolylin p = (FGEPolylin) area;
			// logger.info("Union of "+this+" and "+p);
			FGEArea returned = clone();
			for (FGESegment s : p.getSegments()) {
				returned = returned.union(s);
			}
			if (returned instanceof FGEUnionArea) {
				return new FGEUnionArea(this, area);
			}
			return returned;
		}
		if (area instanceof FGESegment) {
			FGEPolylin clone = clone();
			if (containsArea(area)) {
				return clone;
			}
			FGESegment s = (FGESegment) area;
			if (s.getP1().equals(getFirstPoint())) {
				if (s.getP2().equals(getLastPoint())) {
					return FGEPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new FGEPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.insertPointAtIndex(s.getP2(), 0);
				return clone;
			} else if (s.getP2().equals(getFirstPoint())) {
				if (s.getP1().equals(getLastPoint())) {
					return FGEPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new FGEPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.insertPointAtIndex(s.getP1(), 0);
				return clone;
			} else if (s.getP1().equals(getLastPoint())) {
				if (s.getP2().equals(getFirstPoint())) {
					return FGEPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new FGEPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.addToPoints(s.getP2());
				return clone;
			} else if (s.getP2().equals(getLastPoint())) {
				if (s.getP1().equals(getFirstPoint())) {
					return FGEPolygon.makeArea(Filling.NOT_FILLED, getPoints());
					// return new FGEPolygon(Filling.NOT_FILLED,getPoints());
				}
				clone.addToPoints(s.getP1());
				return clone;
			}
		}
		return new FGEUnionArea(this, area);
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		for (FGESegment s : _segments) {
			if (s.containsPoint(p)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGESegment && _segments.contains(a)) {
			return true;
		}
		if (a instanceof FGEPolylin) {
			boolean allContained = true;
			for (FGESegment s : ((FGEPolylin) a).getSegments()) {
				if (!containsArea(s)) {
					allContained = false;
					break;
				}
			}
			if (allContained) {
				return true;
			}
		}
		for (FGESegment s : _segments) {
			if (s.containsArea(a)) {
				return true;
			}
		}
		if (a instanceof FGEShape) {
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		}
		return false;
	}

	@Override
	public FGEPolylin transform(AffineTransform t) {
		Vector<FGEPoint> points = new Vector<FGEPoint>();
		for (FGEPoint p : _points) {
			points.add(p.transform(t));
		}
		FGEPolylin returned = new FGEPolylin(points);
		return returned;
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		for (FGESegment s : _segments) {
			s.paint(g);
		}
	}

	@Override
	public String toString() {
		return "FGEPolylin: " + _points;
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEPolylin) {
			FGEPolylin p = (FGEPolylin) obj;
			if (getPointsNb() != p.getPointsNb()) {
				return false;
			}

			boolean isEquals = true;
			// Test in same order
			for (int i = 0; i < getPointsNb(); i++) {
				if (!getPointAt(i).equals(p.getPointAt(i))) {
					isEquals = false;
				}
			}
			if (!isEquals) {
				isEquals = true;
				// Test in reverse order
				for (int i = 0; i < getPointsNb(); i++) {
					if (!getPointAt(i).equals(p.getPointAt(getPointsNb() - 1 - i))) {
						isEquals = false;
					}
				}
			}
			return isEquals;
		}
		return super.equals(obj);
	}

	public double getLength() {
		double returned = 0;
		for (FGESegment s : getSegments()) {
			returned += s.getLength();
		}
		return returned;
	}

	public FGEPoint getMiddle() {
		return getPointAtRelativePosition(0.5);
	}

	public FGEPoint getPointAtRelativePosition(double position) {
		double middleDistancePath = getLength() * position;
		double distance = 0;
		for (FGESegment s : getSegments()) {
			if (distance <= middleDistancePath && distance + s.getLength() >= middleDistancePath) {
				double ratio = (middleDistancePath - distance) / s.getLength();
				FGEPoint p = new FGEPoint();
				p.x = s.getP1().x + (s.getP2().x - s.getP1().x) * ratio;
				p.y = s.getP1().y + (s.getP2().y - s.getP1().y) * ratio;
				return p;
			}
			distance += s.getLength();
		}
		logger.warning("Unexpected situation while computing relative position of polylin");
		return new FGEPoint(0, 0);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		Vector<FGEArea> allAreas = new Vector<FGEArea>();
		for (FGESegment s : getSegments()) {
			allAreas.add(s.getOrthogonalPerspectiveArea(orientation));
		}
		return FGEUnionArea.makeUnion(allAreas);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		return computeVisibleSegmentsFrom(orientation, getSegments());

		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		/*Vector<FGESegment> keptSegments = new Vector<FGESegment>();

		for (FGESegment s : getSegments()) {
			FGEHalfLine hl = FGEHalfLine.makeHalfLine(s.getMiddle(), orientation);
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (FGESegment s2 : getSegments()) {
				if (!s.equals(s2)) {
					FGEArea intersect = s2.intersect(hl);
					if (intersect instanceof FGEPoint) cutsAnOtherSegment = true;
					else if (intersect instanceof FGEEmptyArea) / OK
					else {
						logger.warning("Unexpected intersection: "+intersect);
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
				if (chain.size() == 0) chain.add(s);
				else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new FGESegment(s.getP2(),s.getP1()));
					}
					else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					}
					else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					}
					else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new FGESegment(s.getP2(),s.getP1()));
					}
					else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}

			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			pts.add(chain.firstElement().getP1());
			for (FGESegment s : chain) {
				pts.add(s.getP2());
			}

			//logger.info("anchor area for "+orientation+" : "+new FGEPolylin(pts) );

			return new FGEPolylin(pts);
		}
		*/
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

	public static FGEArea computeVisibleSegmentsFrom(SimplifiedCardinalDirection orientation, Vector<FGESegment> segments) {
		// This algorithm is not quite correct, you can find *very* pathologic cases, but works in most cases

		Vector<FGESegment> keptSegments = new Vector<FGESegment>();

		for (FGESegment s : segments) {
			FGEHalfLine hl = FGEHalfLine.makeHalfLine(s.getMiddle(), orientation);
			// Test if this half-line "cuts" an other segment
			boolean cutsAnOtherSegment = false;
			for (FGESegment s2 : segments) {
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
			return FGEUnionArea.makeUnion(keptSegments);

			// Chains segments
			/*Vector<FGESegment> chain = new Vector<FGESegment>();
			for (FGESegment s : keptSegments) {
				if (chain.size() == 0) chain.add(s);
				else {
					if (s.getP1().equals(chain.firstElement().getP1())) {
						chain.add(0, new FGESegment(s.getP2(),s.getP1()));
					}
					else if (s.getP2().equals(chain.firstElement().getP1())) {
						chain.add(0, s);
					}
					else if (s.getP1().equals(chain.lastElement().getP2())) {
						chain.add(s);
					}
					else if (s.getP2().equals(chain.lastElement().getP2())) {
						chain.add(new FGESegment(s.getP2(),s.getP1()));
					}
					else {
						logger.warning("Multiple chains not implemented yet");
					}
				}
			}

			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			pts.add(chain.firstElement().getP1());
			for (FGESegment s : chain) {
				pts.add(s.getP2());
			}

			//logger.info("anchor area for "+orientation+" : "+new FGEPolylin(pts) );

			return new FGEPolylin(pts);*/
		}

	}

}
