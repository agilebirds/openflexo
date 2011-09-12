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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGEHalfLine;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEGraphics;


public class FGEPoint extends Point2D.Double implements FGEGeometricObject<FGEPoint> {

	public static final FGEPoint ORIGIN_POINT = new FGEPoint() {
		@Override
		public void setX(double value) {
		};

		@Override
		public void setY(double value) {
		};
	};

	public static final FGEPoint NORMALIZED_CENTRAL_POINT = new FGEPoint(0.5, 0.5) {
		@Override
		public void setX(double value) {
		};

		@Override
		public void setY(double value) {
		};
	};

	public FGEPoint(double aX, double aY) {
		super(aX, aY);
	}

	public FGEPoint(FGEPoint p) {
		super(p.getX(), p.getY());
	}

	public FGEPoint(Point2D p) {
		super(p.getX(), p.getY());
	}

	public FGEPoint() {
		super(0, 0);
	}

	public void setX(double value) {
		x = value;
	}

	public void setY(double value) {
		y = value;
	}

	public static double distance(FGEPoint p1, FGEPoint p2) {
		return p1.distance(p2);
	}

	public static double distanceSq(FGEPoint p1, FGEPoint p2) {
		return p1.distanceSq(p2);
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
	public FGEPoint clone() {
		return (FGEPoint) super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEPoint) {
			FGEPoint p = (FGEPoint) obj;
			boolean result = (Math.abs(getX() - p.getX()) < EPSILON || java.lang.Double.isNaN(getX()) && java.lang.Double.isNaN(p.getX()) || getX() == p
					.getX())
					&& (Math.abs(getY() - p.getY()) < EPSILON || java.lang.Double.isNaN(getY()) && java.lang.Double.isNaN(p.getY()) || getY() == p
							.getY());
			return result;
		}
		return super.equals(obj);
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return equals(p);
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		return false;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		return a instanceof FGEPoint && containsPoint((FGEPoint) a);
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		if (area.containsPoint(this)) {
			return this.clone();
		} else {
			return new FGEEmptyArea();
		}
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		if (area.containsPoint(this)) {
			return new FGEEmptyArea();
		} else {
			return this.clone();
		}
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
	public FGEPoint transform(AffineTransform t) {
		FGEPoint returned = new FGEPoint();
		t.transform(this, returned);
		return returned;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}

	@Override
	public String getStringRepresentation() {
		return "FGEPoint: " + toString();
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(new FGEPoint(x, y));
		return returned;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint p) {
		return clone();
	}

	@Override
	public void paint(FGEGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawPoint(this);
	}

	public static CardinalQuadrant getCardinalQuadrant(FGEPoint source, FGEPoint destination) {
		if (destination.x > source.x) {
			if (destination.y > source.y) {
				return CardinalQuadrant.SOUTH_EAST;
			} else {
				return CardinalQuadrant.NORTH_EAST;
			}
		} else {
			if (destination.y > source.y) {
				return CardinalQuadrant.SOUTH_WEST;
			} else {
				return CardinalQuadrant.NORTH_WEST;
			}
		}
	}

	public static SimplifiedCardinalDirection getSimplifiedOrientation(FGEPoint source, FGEPoint destination) {
		AffineTransform rotation = AffineTransform.getRotateInstance(Math.PI / 4);
		FGEPoint s2 = source.transform(rotation);
		FGEPoint d2 = destination.transform(rotation);
		CardinalQuadrant d = getCardinalQuadrant(s2, d2);
		if (d == CardinalQuadrant.NORTH_EAST) {
			return SimplifiedCardinalDirection.NORTH;
		} else if (d == CardinalQuadrant.SOUTH_EAST) {
			return SimplifiedCardinalDirection.EAST;
		} else if (d == CardinalQuadrant.SOUTH_WEST) {
			return SimplifiedCardinalDirection.SOUTH;
		} else if (d == CardinalQuadrant.NORTH_WEST) {
			return SimplifiedCardinalDirection.WEST;
		}
		return null;
	}

	private static final double tanPI_8 = Math.tan(Math.PI / 8);
	private static final double tan3PI_8 = Math.tan(3 * Math.PI / 8);

	public static CardinalDirection getOrientation(FGEPoint source, FGEPoint destination) {
		try {
			double slope = (destination.y - source.y) / (destination.x - source.x);
			if (slope >= 0) {
				if (slope < tanPI_8) {
					if (destination.x > source.x) {
						return CardinalDirection.EAST;
					} else {
						return CardinalDirection.WEST;
					}
				} else if (slope > tanPI_8 && slope < tan3PI_8) {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH_EAST;
					} else {
						return CardinalDirection.NORTH_WEST;
					}
				} else {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH;
					} else {
						return CardinalDirection.NORTH;
					}
				}
			} else {
				slope = -slope;
				if (slope < tanPI_8) {
					if (destination.x > source.x) {
						return CardinalDirection.EAST;
					} else {
						return CardinalDirection.WEST;
					}
				} else if (slope > tanPI_8 && slope < tan3PI_8) {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH_WEST;
					} else {
						return CardinalDirection.NORTH_EAST;
					}
				} else {
					if (destination.y > source.y) {
						return CardinalDirection.SOUTH;
					} else {
						return CardinalDirection.NORTH;
					}
				}
			}
		} catch (ArithmeticException e) {
			if (destination.y > source.y) {
				return CardinalDirection.SOUTH;
			} else {
				return CardinalDirection.NORTH;
			}
		}

	}

	public static FGEPoint getNearestPoint(FGEPoint aPoint, FGEPoint... pts) {
		Vector<FGEPoint> v = new Vector<FGEPoint>();
		for (FGEPoint pt : pts) {
			v.add(pt);
		}
		return getNearestPoint(aPoint, v);
	}

	public static FGEPoint getNearestPoint(FGEPoint aPoint, List<FGEPoint> pts) {
		if (aPoint == null) {
			return null;
		}

		double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
		FGEPoint returned = null;

		for (FGEPoint pt : pts) {
			if (pt != null) {
				double currentDistance = distanceSq(pt, aPoint);
				if (currentDistance < minimalDistanceSq) {
					returned = pt;
					minimalDistanceSq = currentDistance;
				}
			}
		}
		return returned;
	}

	@Override
	public FGEHalfLine getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			return new FGEHalfLine(this, new FGEPoint(x, y - 1));
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			return new FGEHalfLine(this, new FGEPoint(x, y + 1));
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			return new FGEHalfLine(this, new FGEPoint(x + 1, y));
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			return new FGEHalfLine(this, new FGEPoint(x - 1, y));
		}
		return null;
	}

	@Override
	public FGEPoint getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	public static boolean areAligned(FGEPoint p1, FGEPoint p2, FGEPoint p3) {
		FGELine line1 = new FGELine(p1, p2);
		FGELine line2 = new FGELine(p2, p3);
		return line1.overlap(line2);
	}

	public static FGEPoint middleOf(FGEPoint p1, FGEPoint p2) {
		return new FGESegment(p1, p2).getMiddle();
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
		return new FGERectangle(x, y, 0, 0, Filling.FILLED);
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

		if (hl.contains(this)) {
			return clone();
		} else {
			return null;
		}

	}

	public static FGEPoint getOppositePoint(FGEPoint p, FGEPoint pivot) {
		return new FGEPoint(2.0 * pivot.x - p.x, 2.0 * pivot.y - p.y);
	}

	public static FGEPoint getMiddlePoint(FGEPoint p1, FGEPoint p2) {
		return new FGEPoint((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
	}

}
