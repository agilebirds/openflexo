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
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEArc.ArcType;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

@SuppressWarnings("serial")
public class FGERoundRectangle extends RoundRectangle2D.Double implements FGEGeometricObject<FGERoundRectangle>,
		FGEShape<FGERoundRectangle> {

	private static final Logger logger = Logger.getLogger(FGERoundRectangle.class.getPackage().getName());

	protected Filling _filling;

	public FGERoundRectangle() {
		this(0, 0, 0, 0, 0, 0, Filling.NOT_FILLED);
	}

	public FGERoundRectangle(double aX, double aY, double aWidth, double aHeight, double anArcWidth, double anArcHeight) {
		this(aX, aY, aWidth, aHeight, anArcWidth, anArcHeight, Filling.NOT_FILLED);
	}

	public FGERoundRectangle(double aX, double aY, double aWidth, double aHeight, double anArcWidth, double anArcHeight, Filling filling) {
		super(aX, aY, aWidth, aHeight, anArcWidth, anArcHeight);
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

	public FGERoundRectangle(FGEPoint p1, FGEPoint p2, double anArcWidth, double anArcHeight, Filling filling) {
		this(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), anArcWidth, anArcHeight, filling);
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
		return new FGEPoint(getCenterX(), getCenterY());
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
	public FGERoundRectangle clone() {
		return (FGERoundRectangle) super.clone();
	}

	public FGEPoint getNorthWestPt() {
		return getNorth().getP1();
	}

	public FGEPoint getNorthPt() {
		return getNorth().getMiddle();
	}

	public FGEPoint getNorthEastPt() {
		return getNorth().getP2();
	}

	public FGEPoint getSouthWestPt() {
		return getSouth().getP1();
	}

	public FGEPoint getSouthPt() {
		return getSouth().getMiddle();
	}

	public FGEPoint getSouthEastPt() {
		return getSouth().getP2();
	}

	public FGEPoint getEastPt() {
		return getEast().getMiddle();
	}

	public FGEPoint getWestPt() {
		return getWest().getMiddle();
	}

	public FGESegment getNorth() {
		return new FGESegment(getX(), getY(), getX() + getWidth(), getY());
	}

	public FGESegment getSouth() {
		return new FGESegment(getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight());
	}

	public FGESegment getEast() {
		return new FGESegment(getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight());
	}

	public FGESegment getWest() {
		return new FGESegment(getX(), getY(), getX(), getY() + getHeight());
	}

	public FGESegment getArcExcludedNorth() {
		return new FGESegment(getX() + arcwidth / 2, getY(), getX() + getWidth() - arcwidth / 2, getY());
	}

	public FGESegment getArcExcludedSouth() {
		return new FGESegment(getX() + arcwidth / 2, getY() + getHeight(), getX() + getWidth() - arcwidth / 2, getY() + getHeight());
	}

	public FGESegment getArcExcludedEast() {
		return new FGESegment(getX() + getWidth(), getY() + archeight / 2, getX() + getWidth(), getY() - archeight / 2 + getHeight());
	}

	public FGESegment getArcExcludedWest() {
		return new FGESegment(getX(), getY() + archeight / 2, getX(), getY() + getHeight() - archeight / 2);
	}

	public List<FGESegment> getFrameSegments() {
		Vector<FGESegment> returned = new Vector<FGESegment>();
		returned.add(getNorth());
		returned.add(getSouth());
		returned.add(getEast());
		returned.add(getWest());
		return returned;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (getIsFilled() && containsPoint(aPoint)) {
			return aPoint.clone();
		}
		return nearestOutlinePoint(aPoint);
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {
		FGEPoint returned = null;
		double distSq, minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;

		FGESegment north = getNorth();
		FGESegment south = getSouth();
		FGESegment east = getEast();
		FGESegment west = getWest();

		FGEPoint p = north.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = south.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = east.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}

		p = west.getNearestPointOnSegment(aPoint);
		distSq = FGEPoint.distanceSq(p, aPoint);
		if (distSq < minimalDistanceSq) {
			returned = p;
			minimalDistanceSq = distSq;
		}
		if (returned == null) {
			returned = new FGEPoint(0, 0);
		}
		if (getNorthWestRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in NW");
			return getNorthWestRound().nearestOutlinePoint(aPoint);
		}

		if (getSouthWestRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in SW");
			return getSouthWestRound().nearestOutlinePoint(aPoint);
		}

		if (getNorthEastRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in NE");
			return getNorthEastRound().nearestOutlinePoint(aPoint);
		}

		if (getSouthEastRoundBounds().containsPoint(returned)) {
			// System.out.println("nearestOutlinePoint() in SE");
			return getSouthEastRound().nearestOutlinePoint(aPoint);
		}

		return returned;
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

		FGEArea intersection = computeLineIntersection(hl);
		if (intersection instanceof FGEEmptyArea) {
			return null;
		} else if (intersection instanceof FGEPoint) {
			return (FGEPoint) intersection;
		} else if (intersection instanceof FGEUnionArea) {
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			FGEPoint returned = null;
			for (FGEArea a : ((FGEUnionArea) intersection).getObjects()) {
				if (a instanceof FGEPoint) {
					double distSq = FGEPoint.distanceSq(from, (FGEPoint) a);
					if (distSq < minimalDistanceSq) {
						returned = (FGEPoint) a;
						minimalDistanceSq = distSq;
					}
				}
			}
			return returned;
		} else if (intersection instanceof FGESegment) {
			FGEPoint p1, p2;
			p1 = ((FGESegment) intersection).getP1();
			p2 = ((FGESegment) intersection).getP2();
			if (FGEPoint.distanceSq(from, p1) < FGEPoint.distanceSq(from, p2)) {
				return p1;
			} else {
				return p2;
			}
		}

		else {
			logger.warning("Unexpected area: " + intersection);
			return null;
		}

		/*if (getNorthWestRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in NW");
			return getNorthWestRound().nearestPointFrom(returned,orientation);
		}

		if (getSouthWestRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in SW");
			return getSouthWestRound().nearestPointFrom(returned,orientation);
		}

		if (getNorthEastRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in NE");
			return getNorthEastRound().nearestPointFrom(returned,orientation);
		}


		if (getSouthEastRoundBounds().contains(returned)) {
			//System.out.println("outlineIntersect() in SE");
			return getSouthEastRound().nearestPointFrom(returned,orientation);
		}*/

		// return returned;
	}

	protected FGEArc getNorthWestRound() {
		return new FGEArc(x, y, arcwidth, archeight, 90, 90);
	}

	protected FGEArc getFilledNorthWestRound() {
		return new FGEArc(x, y, arcwidth, archeight, 90, 90, ArcType.PIE);
	}

	protected FGEArc getSouthWestRound() {
		return new FGEArc(x, y + height - archeight, arcwidth, archeight, 180, 90);
	}

	protected FGEArc getFilledSouthWestRound() {
		return new FGEArc(x, y + height - archeight, arcwidth, archeight, 180, 90, ArcType.PIE);
	}

	protected FGEArc getNorthEastRound() {
		return new FGEArc(x + width - arcwidth, y, arcwidth, archeight, 0, 90);
	}

	protected FGEArc getFilledNorthEastRound() {
		return new FGEArc(x + width - arcwidth, y, arcwidth, archeight, 0, 90, ArcType.PIE);
	}

	protected FGEArc getSouthEastRound() {
		return new FGEArc(x + width - arcwidth, y + height - archeight, arcwidth, archeight, -90, 90);
	}

	protected FGEArc getFilledSouthEastRound() {
		return new FGEArc(x + width - arcwidth, y + height - archeight, arcwidth, archeight, -90, 90, ArcType.PIE);
	}

	protected FGERectangle getNorthWestRoundBounds() {
		return getNorthWestRound().getBoundingBox();
	}

	protected FGERectangle getSouthWestRoundBounds() {
		return getSouthWestRound().getBoundingBox();
	}

	protected FGERectangle getNorthEastRoundBounds() {
		return getNorthEastRound().getBoundingBox();
	}

	protected FGERectangle getSouthEastRoundBounds() {
		return getSouthEastRound().getBoundingBox();
	}

	private FGEArea computeRectangleIntersection(FGERoundRectangle rect) {
		Vector<FGEPoint> pts = new Vector<FGEPoint>() {
			@Override
			public synchronized boolean add(FGEPoint o) {
				if (!contains(o)) {
					return super.add(o);
				}
				return false;
			}
		};

		List<FGESegment> sl = rect.getFrameSegments();
		for (FGESegment seg : sl) {
			FGEArea a = intersect(seg);
			if (a instanceof FGEPoint) {
				pts.add((FGEPoint) a);
			} else if (a instanceof FGESegment) {
				pts.add(((FGESegment) a).getP1());
				pts.add(((FGESegment) a).getP2());
			} else if (a instanceof FGEUnionArea) {
				for (FGEArea a2 : ((FGEUnionArea) a).getObjects()) {
					if (a2 instanceof FGEPoint) {
						pts.add((FGEPoint) a2);
					}
					if (a2 instanceof FGESegment) {
						pts.add(((FGESegment) a2).getP1());
						pts.add(((FGESegment) a2).getP2());
					}
				}
			}
		}

		FGEPoint ne, nw, se, sw;
		ne = new FGEPoint(x + width, y);
		nw = new FGEPoint(x, y);
		se = new FGEPoint(x + width, y + height);
		sw = new FGEPoint(x, y + height);
		if (rect.containsPoint(ne)) {
			pts.add(ne);
		}
		if (rect.containsPoint(nw)) {
			pts.add(nw);
		}
		if (rect.containsPoint(se)) {
			pts.add(se);
		}
		if (rect.containsPoint(sw)) {
			pts.add(sw);
		}

		if (pts.size() == 0) {
			return new FGEEmptyArea();
		} else if (pts.size() == 2) {
			return new FGESegment(pts.firstElement(), pts.elementAt(1));
		} else if (pts.size() != 4) {
			logger.warning("Strange situation here while computeRectangleIntersection between " + this + " and " + rect);
		}

		double minx = java.lang.Double.POSITIVE_INFINITY;
		double miny = java.lang.Double.POSITIVE_INFINITY;
		double maxx = java.lang.Double.NEGATIVE_INFINITY;
		double maxy = java.lang.Double.NEGATIVE_INFINITY;
		for (FGEPoint p : pts) {
			if (p.x < minx) {
				minx = p.x;
			}
			if (p.y < miny) {
				miny = p.y;
			}
			if (p.x > maxx) {
				maxx = p.x;
			}
			if (p.y > maxy) {
				maxy = p.y;
			}
		}
		return new FGERoundRectangle(minx, miny, maxx - minx, maxy - miny, arcwidth, archeight, Filling.FILLED);
	}

	private FGEArea computeHalfPlaneIntersection(FGEHalfPlane hp) {
		if (hp.containsArea(this)) {
			return this.clone();
		}
		FGEArea computeLineIntersection = computeLineIntersection(hp.line);
		if (computeLineIntersection instanceof FGEEmptyArea) {
			return new FGEEmptyArea();
		} else {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("computeHalfPlaneIntersection() for rectangle when halfplane cross rectangle");
			}
			FGEArea a = computeLineIntersection;
			Vector<FGEPoint> pts = new Vector<FGEPoint>();
			if (a instanceof FGEUnionArea && ((FGEUnionArea) a).isUnionOfPoints() && ((FGEUnionArea) a).getObjects().size() == 2) {
				pts.add((FGEPoint) ((FGEUnionArea) a).getObjects().firstElement());
				pts.add((FGEPoint) ((FGEUnionArea) a).getObjects().elementAt(1));
			} else if (a instanceof FGESegment) {
				if (getArcExcludedEast().containsArea(a) || getArcExcludedWest().containsArea(a) || getArcExcludedNorth().containsArea(a)
						|| getArcExcludedSouth().containsArea(a)) {
					return a;
				}
				pts.add(((FGESegment) a).getP1());
				pts.add(((FGESegment) a).getP2());
			}
			FGEPoint ne, nw, se, sw;
			ne = new FGEPoint(x + width, y);
			nw = new FGEPoint(x, y);
			se = new FGEPoint(x + width, y + height);
			sw = new FGEPoint(x, y + height);
			if (hp.containsPoint(ne) && !pts.contains(ne)) {
				pts.add(ne);
			}
			if (hp.containsPoint(nw) && !pts.contains(nw)) {
				pts.add(nw);
			}
			if (hp.containsPoint(se) && !pts.contains(se)) {
				pts.add(se);
			}
			if (hp.containsPoint(sw) && !pts.contains(sw)) {
				pts.add(sw);
			}
			if (getIsFilled()) {
				return FGEPolygon.makeArea(Filling.FILLED, pts);
			} else {
				return new FGEUnionArea(pts);
			}
		}
	}

	@SuppressWarnings("unused")
	private FGEArea computeLineIntersection(FGEAbstractLine<?> line) {
		FGERectangle rectangle = new FGERectangle(x, y, width, height, _filling);
		FGEArea returned = rectangle.intersect(line);
		if (returned instanceof FGEEmptyArea) {
			return returned;
		} else if (returned instanceof FGEPoint) {
			FGEPoint p = (FGEPoint) returned;
			if (containsPoint(p)) {
				return p;
			}
			if (getNorthEastRoundBounds().containsPoint(p)) {
				return getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p)) {
				return getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p)) {
				return getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p)) {
				return getSouthWestRound().intersect(line);
			}
			return new FGEEmptyArea();
		} else {
			FGEPoint p1;
			FGEPoint p2;
			if (returned instanceof FGEUnionArea && ((FGEUnionArea) returned).isUnionOfPoints()) {
				p1 = (FGEPoint) ((FGEUnionArea) returned).getObjects().firstElement();
				p2 = (FGEPoint) ((FGEUnionArea) returned).getObjects().elementAt(1);
				if (containsPoint(p1) && containsPoint(p2)) {
					return returned;
				}
			} else if (returned instanceof FGESegment) {
				p1 = ((FGESegment) returned).getP1();
				p2 = ((FGESegment) returned).getP2();
				boolean p1Contained = containsPoint(p1);
				boolean p2Contained = containsPoint(p2);
				if (p1Contained && p2Contained) {
					return returned;
				} else if (p1Contained || p2Contained) {
					FGEPoint contained = p1Contained ? p1 : p2;
					FGEArea p = getNorthEastRound().intersect(returned);
					if (p instanceof FGEPoint) {
						return new FGESegment(contained, (FGEPoint) p);
					}
					p = getNorthWestRound().intersect(returned);
					if (p instanceof FGEPoint) {
						return new FGESegment(contained, (FGEPoint) p);
					}
					p = getSouthEastRound().intersect(returned);
					if (p instanceof FGEPoint) {
						return new FGESegment(contained, (FGEPoint) p);
					}
					p = getSouthWestRound().intersect(returned);
					if (p instanceof FGEPoint) {
						return new FGESegment(contained, (FGEPoint) p);
					}
				} else {
					FGEArea area = getNorthEastRound().intersect(returned);
					FGEPoint p = null;
					if (area instanceof FGEPoint) {
						if (p != null) {
							return new FGESegment(p, (FGEPoint) area);
						} else {
							p = (FGEPoint) area;
						}
					}
					area = getNorthWestRound().intersect(returned);
					if (area instanceof FGEPoint) {
						if (p != null) {
							return new FGESegment(p, (FGEPoint) area);
						} else {
							p = (FGEPoint) area;
						}
					}
					area = getSouthEastRound().intersect(returned);
					if (area instanceof FGEPoint) {
						if (p != null) {
							return new FGESegment(p, (FGEPoint) area);
						} else {
							p = (FGEPoint) area;
						}
					}
					area = getSouthWestRound().intersect(returned);
					if (area instanceof FGEPoint) {
						if (p != null) {
							return new FGESegment(p, (FGEPoint) area);
						} else {
							p = (FGEPoint) area;
						}
					}
					if (p != null) {
						return p;
					} else {
						return new FGEEmptyArea();
					}
				}
			} else {
				logger.warning("Unexpected " + returned);
				return new FGEEmptyArea();
			}
			FGEArea newP1 = p1;
			FGEArea newP2 = p2;
			if (getNorthEastRoundBounds().containsPoint(p1)) {
				newP1 = getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p1)) {
				newP1 = getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p1)) {
				newP1 = getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p1)) {
				newP1 = getSouthWestRound().intersect(line);
			}
			if (getNorthEastRoundBounds().containsPoint(p2)) {
				newP2 = getNorthEastRound().intersect(line);
			}
			if (getSouthEastRoundBounds().containsPoint(p2)) {
				newP2 = getSouthEastRound().intersect(line);
			}
			if (getNorthWestRoundBounds().containsPoint(p2)) {
				newP2 = getNorthWestRound().intersect(line);
			}
			if (getSouthWestRoundBounds().containsPoint(p2)) {
				newP2 = getSouthWestRound().intersect(line);
			}

			if (newP1 instanceof FGEPoint) {
				if (newP2 instanceof FGEPoint) {
					if (returned instanceof FGEUnionArea) {
						return new FGEUnionArea(newP1, newP2);
					} else if (returned instanceof FGESegment) {
						return new FGESegment((FGEPoint) newP1, (FGEPoint) newP2);
					}
				} else if (newP2 instanceof FGEEmptyArea) {
					return newP1;
				}
			} else if (newP1 instanceof FGEEmptyArea) {
				if (newP2 instanceof FGEPoint) {
					return newP2;
				} else {
					return new FGEEmptyArea();
				}
			} else if (newP1 instanceof FGEUnionArea && ((FGEUnionArea) newP1).isUnionOfPoints() && newP2 instanceof FGEUnionArea
					&& ((FGEUnionArea) newP2).isUnionOfPoints() && newP1.equals(newP2)) {
				return newP1;
			}

			logger.warning("Unexpected " + returned + " newP1=" + newP1 + " newP2=" + newP2);
			return new FGEEmptyArea();
		}

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
			return computeLineIntersection((FGEAbstractLine<?>) area);
		}
		if (area instanceof FGERoundRectangle) {
			return computeRectangleIntersection((FGERoundRectangle) area);
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

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(new FGEPoint(x, y));
		returned.add(new FGEPoint(x + width, y));
		returned.add(new FGEPoint(x, y + height));
		returned.add(new FGEPoint(x + width, y + height));
		returned.add(getNorth().getMiddle());
		returned.add(getEast().getMiddle());
		returned.add(getWest().getMiddle());
		returned.add(getSouth().getMiddle());
		return returned;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		if (p.x >= getX() - EPSILON && p.x <= getX() + getWidth() + EPSILON && p.y >= getY() - EPSILON
				&& p.y <= getY() + getHeight() + EPSILON) {

			if (!getIsFilled()) {
				FGESegment north = getArcExcludedNorth();
				FGESegment south = getArcExcludedSouth();
				FGESegment west = getArcExcludedWest();
				FGESegment east = getArcExcludedEast();
				return north.contains(p) || south.contains(p) || east.contains(p) || west.contains(p) || getNorthEastRound().contains(p)
						|| getNorthWestRound().contains(p) || getSouthEastRound().contains(p) || getSouthWestRound().contains(p);
			} else {
				if (new FGERectangle(new FGEPoint(getX(), getY() + archeight / 2), new FGEDimension(getWidth(), getHeight() - archeight),
						Filling.FILLED).contains(p)) {
					return true;
				}
				if (new FGERectangle(new FGEPoint(getX() + arcwidth / 2, getY()), new FGEDimension(getWidth() - arcwidth, getHeight()),
						Filling.FILLED).contains(p)) {
					return true;
				}
				return getFilledNorthEastRound().containsPoint(p) || getFilledNorthWestRound().containsPoint(p)
						|| getFilledSouthEastRound().containsPoint(p) || getFilledSouthWestRound().containsPoint(p);
			}
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		if (l instanceof FGEHalfLine) {
			return false;
		}
		if (l instanceof FGESegment) {
			return containsPoint(l.getP1()) && containsPoint(l.getP2());
		}
		return false;
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
	public FGEArea transform(AffineTransform t) {
		// TODO: not valid for AffineTransform containing rotations

		FGEPoint p1 = new FGEPoint(getX(), getY()).transform(t);
		FGEPoint p2 = new FGEPoint(getX() + getWidth(), getY() + getHeight()).transform(t);

		// TODO: if transformation contains a rotation, turn into a regular polygon
		// arcwidth,archeight must also be computed according to this rotation

		return new FGERoundRectangle(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), arcwidth
				* t.getScaleX(), archeight * t.getScaleY(), _filling);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		if (getIsFilled()) {
			g.useDefaultBackgroundStyle();
			g.fillRoundRect(getX(), getY(), getWidth(), getHeight(), arcwidth, archeight);
		}

		/*getNorthWestRoundBounds().paint(g);
		getSouthWestRoundBounds().paint(g);
		getNorthEastRoundBounds().paint(g);
		getSouthEastRoundBounds().paint(g);*/

		/*getFilledNorthWestRound().paint(g);
		getFilledSouthWestRound().paint(g);
		getFilledNorthEastRound().paint(g);
		getFilledSouthEastRound().paint(g);*/

		/*new FGERectangle(
				new FGEPoint(getX(),getY()+archeight/2),
				new FGEDimension(getWidth(),getHeight()-archeight),Filling.FILLED)
		.paint(g);
		new FGERectangle(
				new FGEPoint(getX()+arcwidth/2,getY()),
				new FGEDimension(getWidth()-arcwidth,getHeight()),Filling.FILLED)
		.paint(g);*/

		g.useDefaultForegroundStyle();
		g.drawRoundRect(getX(), getY(), getWidth(), getHeight(), arcwidth, archeight);

	}

	@Override
	public String toString() {
		return "FGERoundRectangle: (" + x + "," + y + "," + width + "," + height + "," + arcwidth + "," + archeight + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public FGERectangle getBoundingBox() {
		return new FGERectangle(x, y, width, height);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGERoundRectangle) {
			FGERoundRectangle p = (FGERoundRectangle) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON
					&& Math.abs(getArcWidth() - p.getArcWidth()) <= EPSILON && Math.abs(getArcHeight() - p.getArcHeight()) <= EPSILON;
		}
		return super.equals(obj);
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return getAnchorAreaFrom(orientation).getOrthogonalPerspectiveArea(orientation);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			FGEArc northWestRound = getNorthWestRound();
			northWestRound.extent = 45;
			FGEArc northEastRound = getNorthEastRound();
			northEastRound.extent = 45;
			northEastRound.start += 45;
			return new FGEUnionArea(getArcExcludedNorth(), northWestRound, northEastRound);
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			// return getSouth();
			FGEArc southWestRound = getSouthWestRound();
			southWestRound.start += 45;
			southWestRound.extent = 45;
			FGEArc southEastRound = getSouthEastRound();
			southEastRound.extent = 45;
			return new FGEUnionArea(getArcExcludedSouth(), southWestRound, southEastRound);
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			// return getEast();
			FGEArc northEastRound = getNorthEastRound();
			northEastRound.extent = 45;
			FGEArc southEastRound = getSouthEastRound();
			southEastRound.extent = 45;
			southEastRound.start += 45;
			return new FGEUnionArea(getArcExcludedEast(), northEastRound, southEastRound);
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			// return getWest();
			FGEArc northWestRound = getNorthWestRound();
			northWestRound.extent = 45;
			northWestRound.start += 45;
			FGEArc southWestRound = getSouthWestRound();
			southWestRound.extent = 45;
			return new FGEUnionArea(getArcExcludedWest(), northWestRound, southWestRound);
		}
		logger.warning("Unexpected: " + orientation);
		return null;
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
		return new FGERectangle(x, y, width, height, Filling.FILLED);
	}

}
