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

import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
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
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.FGEGraphics;

public class FGEArc extends Arc2D.Double implements FGEGeometricObject<FGEArc>, FGEShape<FGEArc> {

	private static final Logger logger = Logger.getLogger(FGEArc.class.getPackage().getName());

	public static enum ArcType {
		/**
		 * The closure type for an open arc with no path segments connecting the two ends of the arc segment.
		 */
		OPEN,
		/**
		 * The closure type for an arc closed by drawing a straight line segment from the start of the arc segment to the end of the arc
		 * segment.
		 */
		CHORD,
		/**
		 * The closure type for an arc closed by drawing straight line segments from the start of the arc segment to the center of the full
		 * ellipse and from that point to the end of the arc segment.
		 */
		PIE
	}

	public FGEArc() {
		super(ArcType.OPEN.ordinal());
	}

	public FGEArc(ArcType type) {
		super(type.ordinal());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param start
	 *            angle in degree
	 * @param extent
	 *            angle in degree
	 * @param type
	 */
	public FGEArc(double x, double y, double w, double h, double start, double extent, ArcType type) {
		super(x, y, w, h, start, extent, type.ordinal());
	}

	/**
	 * 
	 * @param center
	 * @param size
	 * @param start
	 *            angle in degree
	 * @param extent
	 *            angle in degree
	 * @param type
	 */
	public FGEArc(FGEPoint center, FGEDimension size, double start, double extent, ArcType type) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, start, extent, type);
	}

	public FGEArc(double x, double y, double w, double h, double start, double extent) {
		super(x, y, w, h, start, extent, ArcType.OPEN.ordinal());
		if (extent >= 360)
			logger.warning("Arc created instead of ellips or circle (extent >=360) ");
	}

	public FGEArc(FGEPoint center, FGEDimension size, double start, double extent) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, start, extent, ArcType.OPEN);
	}

	public FGEArc(FGEPoint center, FGEDimension size) {
		this(center, size, 0, 360, ArcType.OPEN);
	}

	@Override
	public boolean getIsFilled() {
		return getFGEArcType() == ArcType.PIE;
	}

	@Override
	public void setIsFilled(boolean filled) {
		if (!filled)
			setFGEArcType(ArcType.OPEN);
		else
			setFGEArcType(ArcType.PIE);
	}

	@Override
	public FGEPoint getCenter() {
		return new FGEPoint(getCenterX(), getCenterY());
	}

	public FGERectangle getFGEBounds() {
		return new FGERectangle(getX(), getY(), getWidth(), getHeight(), Filling.FILLED);
	}

	public ArcType getFGEArcType() {
		if (getArcType() == ArcType.OPEN.ordinal())
			return ArcType.OPEN;
		else if (getArcType() == ArcType.CHORD.ordinal())
			return ArcType.CHORD;
		else if (getArcType() == ArcType.PIE.ordinal())
			return ArcType.PIE;
		return null;
	}

	public void setFGEArcType(ArcType arcType) {
		if (arcType == ArcType.OPEN)
			setArcType(ArcType.OPEN.ordinal());
		else if (arcType == ArcType.CHORD)
			setArcType(ArcType.CHORD.ordinal());
		else if (arcType == ArcType.PIE)
			setArcType(ArcType.PIE.ordinal());
	}

	public static void main(String[] args) {
		// FGESegment s = new FGESegment(new FGEPoint(0,5),new FGEPoint(0,2));
		// FGEHalfLine hl = new FGEHalfLine(new FGEPoint(0,0),new FGEPoint(0,1));

		FGESegment s = new FGESegment(new FGEPoint(0.11718749999999999, 0.7722772277227723), new FGEPoint(0.1171875, 0.5742574257425743));
		FGEHalfLine hl = new FGEHalfLine(new FGEPoint(0.11718749999999999, 0.22277227722772278), new FGEPoint(0.11718749999999999,
				1.2227722772277227));

		// FGESegment s = new FGESegment(new FGEPoint(0.1171875,0.7722772277227723),new FGEPoint(0.1171875,0.5742574257425743));
		// FGEHalfLine hl = new FGEHalfLine(new FGEPoint(0.1171875,0.22277227722772278),new FGEPoint(0.1171875,1.2227722772277227));

		System.out.println("intersect: " + s.intersect(hl));
		// System.out.println("intersect: "+hl.intersect(s));

		FGEArc arc = new FGEArc(new FGEPoint(0, 0), new FGEDimension(100, 100));

		/*Area area1 = new Area(arc);
		Area area2 = new Area(line);

		System.out.println("area1="+area1);
		System.out.println("area2="+area2);

		Area intersect = new Area(area1);
		intersect.intersect(area2);

		System.out.println("intersect="+intersect);

		PathIterator i = intersect.getPathIterator(null);
		System.out.println("PathIterator="+i);

		while (!i.isDone()) {
			double[] coords = null;
			i.next();
			i.currentSegment(coords);
			System.out.println("coords="+coords);
		}*/

		/*FGELine line = new FGELine(new FGEPoint(-100,-100),new FGEPoint(100,100));
		System.out.println("1: "+arc.computeIntersection(line));

		FGELine line2 = new FGELine(new FGEPoint(50,0),new FGEPoint(50,100));
		System.out.println("2: "+arc.computeIntersection(line2));

		FGELine line3 = new FGELine(new FGEPoint(0,0),new FGEPoint(0,100));
		System.out.println("3: "+arc.computeIntersection(line3));

		FGELine line4 = new FGELine(new FGEPoint(-100,50),new FGEPoint(100,50));
		System.out.println("4: "+arc.computeIntersection(line4));*/

	}

	/**
	 * Returns the angular extent of the arc, expressed in radians, in the range -2.PI/2.PI
	 * 
	 * @return A double value that represents the angular extent of the arc in radians.
	 */

	public double getRadianAngleExtent() {
		double angdeg = getAngleExtent();

		// while (angdeg < -360) angdeg += 360;
		// while (angdeg > 360) angdeg -= 360;
		return Math.toRadians(angdeg);
	}

	/**
	 * Returns the starting angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the starting angle of the arc in degrees.
	 */
	public double getRadianAngleStart() {
		return normalizeDegAngle(getAngleStart());
	}

	/**
	 * Returns the ending angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the ending angle of the arc in degrees.
	 */
	public double getRadianAngleEnd() {
		return normalizeDegAngle(getAngleStart() + getAngleExtent());
	}

	/**
	 * Returns the supplied angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the normalized angle in radians
	 */
	public static double normalizeRadianAngle(double radianAngle) {
		while (radianAngle <= -Math.PI)
			radianAngle += 2 * Math.PI;
		while (radianAngle > Math.PI)
			radianAngle -= 2 * Math.PI;
		return radianAngle;
	}

	/**
	 * Returns the supplied angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the normalized angle in radians
	 */
	public static double normalizeDegAngle(double degAngle) {
		return normalizeRadianAngle(Math.toRadians(degAngle));
	}

	/**
	 * Returns the starting point of the arc. This point is the intersection of the ray from the center defined by the starting angle and
	 * the elliptical boundary of the arc.
	 * 
	 * @return A <CODE>FGEPoint</CODE> object representing the x,y coordinates of the starting point of the arc.
	 */
	@Override
	public FGEPoint getStartPoint() {
		double angle = Math.toRadians(-getAngleStart());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new FGEPoint(x, y);
	}

	/**
	 * Returns the ending point of the arc. This point is the intersection of the ray from the center defined by the starting angle plus the
	 * angular extent of the arc and the elliptical boundary of the arc.
	 * 
	 * @return A <CODE>FGEPoint</CODE> object representing the x,y coordinates of the ending point of the arc.
	 */
	@Override
	public FGEPoint getEndPoint() {
		double angle = Math.toRadians(-getAngleStart() - getAngleExtent());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new FGEPoint(x, y);
	}

	/**
	 * Returns boolean indicating if supplied angle (expressed in radians) in contained in this arc
	 * 
	 * @param radianAngle
	 * @return
	 */
	public boolean includeAngle(double radianAngle) {
		while (radianAngle < -Math.PI - EPSILON)
			radianAngle += 2 * Math.PI;
		while (radianAngle > Math.PI + EPSILON)
			radianAngle -= 2 * Math.PI;

		double angle_start = getRadianAngleStart();
		double angle_extent = getRadianAngleExtent();

		if (angle_extent > 0) {
			return ((radianAngle >= angle_start - EPSILON && radianAngle <= angle_start + angle_extent + EPSILON)
					|| (radianAngle + 2 * Math.PI >= angle_start - EPSILON && radianAngle + 2 * Math.PI <= angle_start + angle_extent
							+ EPSILON) || (radianAngle - 2 * Math.PI >= angle_start - EPSILON && radianAngle - 2 * Math.PI <= angle_start
					+ angle_extent + EPSILON));
		} else if (angle_extent < 0) {
			return ((radianAngle <= angle_start + EPSILON && radianAngle >= angle_start + angle_extent - EPSILON)
					|| (radianAngle + 2 * Math.PI <= angle_start + EPSILON && radianAngle + 2 * Math.PI >= angle_start + angle_extent
							- EPSILON) || (radianAngle - 2 * Math.PI <= angle_start + EPSILON && radianAngle - 2 * Math.PI >= angle_start
					+ angle_extent - EPSILON));
		}
		return false;

	}

	/*private FGEArea computeIntersection(FGEAbstractLine line)
	{

		double a,b,c;

		a = line.getA()*x+line.getB()*y+line.getB()*height/2.0+line.getC();
		b = line.getB()*height;
		c = line.getA()*x+line.getB()*y+line.getA()*width+line.getB()*height/2.0+line.getC();


		double delta = b*b-4*a*c;

		if (Math.abs(a) > EPSILON && Math.abs(delta) < EPSILON) {
			double t = -b/(2*a);
			double alpha = Math.atan(t)*2.0;
			// One solution, with angle alpha
			return new FGEPoint(width/2.0*Math.cos(alpha)+x+width/2.0,height/2.0*Math.sin(alpha)+y+height/2.0);
		}

		double alpha1; // in the range -PI / PI
		double alpha2; // in the range -PI / PI

		if (Math.abs(a) < EPSILON) {
			double t = -c/b;
			// Two solutions, with angle -alpha and PI-alpha
			alpha1 = -Math.atan(t)*2.0;
			alpha2 = Math.PI-Math.atan(t)*2.0;
		}

		else if (delta > 0) {
			double t1,t2;
			t1 = -(b+Math.sqrt(delta))/(2.0*a);
			t2 = -(b-Math.sqrt(delta))/(2.0*a);
			alpha1 = -Math.atan(t1)*2.0; // in the range -PI / PI
			alpha2 = -Math.atan(t2)*2.0; // in the range -PI / PI
			// Two solutions, with angle alpha1 and alpha2
		}
		else {
			// No solution
			return new FGEEmptyArea();
		}

		FGEPoint p1 = new FGEPoint(width/2.0*Math.cos(-alpha1)+x+width/2.0,height/2.0*Math.sin(-alpha1)+y+height/2.0);
		FGEPoint p2 = new FGEPoint(width/2.0*Math.cos(-alpha2)+x+width/2.0,height/2.0*Math.sin(-alpha2)+y+height/2.0);
		boolean includeP1 = includeAngle(alpha1);
		boolean includeP2 = includeAngle(alpha2);
		//logger.info("Found angle "+Math.toDegrees(alpha1)+" "+(includeP1?"INCLUDED":"- not included - "));
		//logger.info("Found angle "+Math.toDegrees(alpha2)+" "+(includeP2?"INCLUDED":"- not included - "));
		if (includeP1 && includeP2) {
			if (getFGEArcType() == ArcType.OPEN) {
				return FGEUnionArea.makeUnion(p1,p2);
			}
			else {
				return new FGESegment(p1,p2);
			}				
		}
		FGEArea pp1 = (new FGESegment(getCenter(),getStartPoint())).intersect(line);
		FGEArea pp2 = (new FGESegment(getCenter(),getEndPoint())).intersect(line);
		if (includeP1) {
			if (getFGEArcType() == ArcType.OPEN) {
				return p1;
			}
			else {
				if (pp1 instanceof FGEPoint) 
					return new FGESegment((FGEPoint)pp1,p1);
				if (pp2 instanceof FGEPoint) 
					return new FGESegment((FGEPoint)pp2,p1);
				logger.warning("Unexpected situation encoutered here: arc="+this+" line="+line+" pp1="+pp1+" pp2="+pp2);
			}				
		}
		else if (includeP2) {
			if (getFGEArcType() == ArcType.OPEN) {
				return p2;
			}
			else {
				if (pp1 instanceof FGEPoint) 
					return new FGESegment((FGEPoint)pp1,p2);
				if (pp2 instanceof FGEPoint) 
					return new FGESegment((FGEPoint)pp2,p2);
				logger.warning("Unexpected situation encoutered here: arc="+this+" line="+line+" pp1="+pp1+" pp2="+pp2);
			}				
		}
		else {
			if (getFGEArcType() == ArcType.OPEN
					|| getFGEArcType() == ArcType.CHORD) return new FGEEmptyArea();
			// ArcType is ArcType.PIE
			if (pp1 instanceof FGEPoint && pp2 instanceof FGEPoint) {
				if (pp1.equals(pp2)) {
					return pp1;
				}
				else {
					// NOTE:
					// if is filled (not implemented yet)
					// return segment : return new FGESegment((FGEPoint)pp1,(FGEPoint)pp2);
					return FGEUnionArea.makeUnion(pp1,pp2);
				}
			}
		}

		return new FGEEmptyArea();
	}*/

	private static class LineIntersectionResult {
		private static enum SolutionType {
			NO_SOLUTION, ONE_SOLUTION, TWO_SOLUTIONS
		}

		SolutionType solutionType;
		double alpha1; // in the range -PI / PI
		double alpha2; // in the range -PI / PI

		@Override
		public String toString() {
			return "LineIntersectionResult solutionType=" + solutionType + " alpha1=" + alpha1 + " alpha2=" + alpha2;
		}
	}

	private LineIntersectionResult _computeIntersection(FGEAbstractLine line) {
		LineIntersectionResult result = new LineIntersectionResult();
		double a, b, c;

		a = line.getA() * x + line.getB() * y + line.getB() * height / 2.0 + line.getC();
		b = line.getB() * height;
		c = line.getA() * x + line.getB() * y + line.getA() * width + line.getB() * height / 2.0 + line.getC();

		double delta = b * b - 4 * a * c;

		if (Math.abs(a) > EPSILON && Math.abs(delta) < EPSILON) {
			double t = -b / (2 * a);
			double alpha = Math.atan(t) * 2.0;
			// One solution, with angle alpha
			result.solutionType = org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.ONE_SOLUTION;
			result.alpha1 = alpha;
			result.alpha2 = alpha;
			return result;
		}

		if (Math.abs(a) < EPSILON) {
			double t = -c / b;
			// Two solutions, with angle -alpha and PI-alpha
			result.solutionType = org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS;
			result.alpha1 = -Math.atan(t) * 2.0;
			result.alpha2 = Math.PI - Math.atan(t) * 2.0;
			return result;
		}

		else if (delta > 0) {
			double t1, t2;
			t1 = -(b + Math.sqrt(delta)) / (2.0 * a);
			t2 = -(b - Math.sqrt(delta)) / (2.0 * a);
			// Two solutions, with angle alpha1 and alpha2
			result.solutionType = org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS;
			result.alpha1 = -Math.atan(t1) * 2.0; // in the range -PI / PI
			result.alpha2 = -Math.atan(t2) * 2.0; // in the range -PI / PI
			return result;
		} else {
			// No solution
			result.solutionType = org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.NO_SOLUTION;
			return result;
		}
	}

	private FGEArea computeIntersection(FGEAbstractLine line) {
		FGEArea a = computeLineIntersection(new FGELine(line.getP1(), line.getP2()));
		// logger.info("computeLineIntersection(): "+a+" line="+line+" intersect="+(a.intersect(line)));
		return a.intersect(line);
	}

	private FGEArea computeLineIntersection(FGELine line) {
		LineIntersectionResult result = _computeIntersection(line);

		if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.NO_SOLUTION) {
			return new FGEEmptyArea();
		}

		else if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.ONE_SOLUTION) {
			// One solution, with angle alpha
			return new FGEPoint(width / 2.0 * Math.cos(result.alpha1) + x + width / 2.0, height / 2.0 * Math.sin(result.alpha1) + y
					+ height / 2.0);
		}

		else if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS) {
			FGEPoint p1 = new FGEPoint(width / 2.0 * Math.cos(-result.alpha1) + x + width / 2.0, height / 2.0 * Math.sin(-result.alpha1)
					+ y + height / 2.0);
			FGEPoint p2 = new FGEPoint(width / 2.0 * Math.cos(-result.alpha2) + x + width / 2.0, height / 2.0 * Math.sin(-result.alpha2)
					+ y + height / 2.0);
			boolean includeP1 = includeAngle(result.alpha1);
			boolean includeP2 = includeAngle(result.alpha2);
			// logger.info("Found angle "+Math.toDegrees(alpha1)+" "+(includeP1?"INCLUDED":"- not included - "));
			// logger.info("Found angle "+Math.toDegrees(alpha2)+" "+(includeP2?"INCLUDED":"- not included - "));
			if (includeP1 && includeP2) {
				if (getFGEArcType() == ArcType.OPEN) {
					return FGEUnionArea.makeUnion(p1, p2);
				} else {
					return new FGESegment(p1, p2);
				}
			}
			FGEArea pp1 = (new FGESegment(getCenter(), getStartPoint())).intersect(line);
			FGEArea pp2 = (new FGESegment(getCenter(), getEndPoint())).intersect(line);
			if (pp1 instanceof FGEEmptyArea && pp2 instanceof FGEEmptyArea) {
				// Nothing
				return new FGEEmptyArea();
			}
			if (includeP1) {
				if (getFGEArcType() == ArcType.OPEN) {
					return p1;
				} else {
					if (pp1 instanceof FGESegment && pp2 instanceof FGESegment) {
						// They are aligned
						return FGEUnionArea.makeUnion(pp1, pp2);
					} else if (pp1 instanceof FGESegment) {
						return pp1;
					} else if (pp2 instanceof FGESegment) {
						return pp2;
					} else if (pp1 instanceof FGEPoint) {
						return new FGESegment((FGEPoint) pp1, p1);
					} else if (pp2 instanceof FGEPoint) {
						return new FGESegment((FGEPoint) pp2, p1);
					}
					logger.warning("Unexpected situation encoutered here: arc=" + this + " line=" + line + " pp1=" + pp1 + " pp2=" + pp2
							+ " p1=" + p1 + " p2=" + p2);
				}
			} else if (includeP2) {
				if (getFGEArcType() == ArcType.OPEN) {
					return p2;
				} else {
					if (pp1 instanceof FGESegment && pp2 instanceof FGESegment) {
						// They are aligned
						return FGEUnionArea.makeUnion(pp1, pp2);
					} else if (pp1 instanceof FGESegment) {
						return pp1;
					} else if (pp2 instanceof FGESegment) {
						return pp2;
					} else if (pp1 instanceof FGEPoint) {
						return new FGESegment((FGEPoint) pp1, p2);
					}
					if (pp2 instanceof FGEPoint) {
						return new FGESegment((FGEPoint) pp2, p2);
					}
					logger.warning("Unexpected situation encoutered here: arc=" + this + " line=" + line + " pp1=" + pp1 + " pp2=" + pp2);
				}
			} else {
				if (getFGEArcType() == ArcType.OPEN || getFGEArcType() == ArcType.CHORD) {
					return new FGEEmptyArea();
				}
				// ArcType is ArcType.PIE
				if (pp1 instanceof FGEPoint && pp2 instanceof FGEPoint) {
					if (pp1.equals(pp2)) {
						return pp1;
					} else {
						// NOTE:
						// if is filled (not implemented yet)
						// return segment : return new FGESegment((FGEPoint)pp1,(FGEPoint)pp2);
						return FGEUnionArea.makeUnion(pp1, pp2);
					}
				}
			}

			logger.warning("Unexpected situation here " + result);
			logger.warning("p1=" + p1);
			logger.warning("p2=" + p2);
			logger.warning("includeP1=" + includeP1);
			logger.warning("includeP2=" + includeP2);
			logger.warning("pp1=" + pp1);
			logger.warning("pp2=" + pp2);

		}

		logger.warning("Unexpected situation here " + result);
		return new FGEEmptyArea();
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
	public FGEArc clone() {
		return (FGEArc) super.clone();
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		if (getIsFilled() && containsPoint(aPoint))
			return aPoint.clone();

		return nearestOutlinePoint(aPoint);
	}

	@Override
	public FGEPoint nearestOutlinePoint(FGEPoint aPoint) {

		double angle = angleForPoint(aPoint);
		if (!includeAngle(angle)) {
			// System.out.println("Hop, "+aPoint+" est en dehors de "+this+", je choisis entre: ");
			// System.out.println("angle: "+Math.toDegrees(getRadianAngleStart())+" is: "+getPointAtRadianAngle(getRadianAngleStart()));
			// System.out.println("angle2: "+Math.toDegrees(getRadianAngleEnd())+" is: "+getPointAtRadianAngle(getRadianAngleEnd()));
			FGEPoint returned = FGEPoint.getNearestPoint(aPoint, getPointAtRadianAngle(getRadianAngleStart()),
					getPointAtRadianAngle(getRadianAngleEnd()));
			if (!containsPoint(returned)) {
				logger.warning("Returning point not beeing in area " + this);
			}
			return returned;

			// System.out.println("C'est: "+returned);
			// return returned;
		}

		FGEArea intersection = computeIntersection(new FGELine(aPoint, getCenter()));

		// System.out.println("aPoint="+aPoint);
		// System.out.println("Intersection="+intersection);
		// System.out.println("angle: "+Math.toDegrees(angle)+" is: "+getPointAtRadianAngle(angle));

		Vector<FGEPoint> pts = new Vector<FGEPoint>();
		if (intersection instanceof FGEPoint) {
			pts.add((FGEPoint) intersection);
		} else if (intersection instanceof FGESegment) {
			pts.add(((FGESegment) intersection).getP1());
			pts.add(((FGESegment) intersection).getP2());
		} else if (intersection instanceof FGEUnionArea) {
			for (FGEArea a : ((FGEUnionArea) intersection).getObjects()) {
				if (a instanceof FGEPoint) {
					pts.add((FGEPoint) a);
				}
			}
		}
		if (pts.size() > 0) {
			FGEPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (FGEPoint p : pts) {
				double currentDistanceSq = FGEPoint.distanceSq(p, aPoint);
				if (currentDistanceSq < minimalDistanceSq) {
					returned = p.clone();
					minimalDistanceSq = currentDistanceSq;
				}
			}
			return returned;
		}

		if (getFGEArcType() == ArcType.OPEN || getFGEArcType() == ArcType.CHORD) {
			if (FGEPoint.distanceSq(aPoint, getStartPoint()) < FGEPoint.distanceSq(aPoint, getEndPoint()))
				return getStartPoint();
			else
				return getEndPoint();
		}

		// NOTE: CHORD not well handled here: also handle segment [getStartPoint();getEndPoint()]

		else { // PIE
			FGESegment s1 = new FGESegment(getCenter(), getStartPoint());
			FGESegment s2 = new FGESegment(getCenter(), getEndPoint());
			FGEPoint pp1 = s1.getNearestPointOnSegment(aPoint);
			FGEPoint pp2 = s2.getNearestPointOnSegment(aPoint);
			if (FGEPoint.distanceSq(aPoint, pp1) < FGEPoint.distanceSq(aPoint, pp2))
				return pp1;
			else
				return pp2;
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
		// logger.info("Request for nearestPointFrom: "+from+" orientation="+orientation+" for arc "+this);

		FGEHalfLine hl = FGEHalfLine.makeHalfLine(from, orientation);

		FGEArea intersect = intersect(hl);

		// logger.info("hl="+hl);
		// logger.info("intersect="+intersect);

		if (intersect instanceof FGEEmptyArea)
			return null;

		if (intersect instanceof FGEPoint)
			return (FGEPoint) intersect;

		if (intersect instanceof FGESegment) {
			FGEPoint p1, p2;
			p1 = ((FGESegment) intersect).getP1();
			p2 = ((FGESegment) intersect).getP2();
			if (FGEPoint.distanceSq(from, p1) < FGEPoint.distanceSq(from, p2))
				return p1;
			else
				return p2;
		}

		if (intersect instanceof FGEUnionArea) {
			FGEPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (FGEArea o : ((FGEUnionArea) intersect).getObjects()) {
				if (o instanceof FGEPoint) {
					double distSq = FGEPoint.distanceSq(from, (FGEPoint) o);
					if (distSq < minimalDistanceSq) {
						returned = (FGEPoint) o;
						minimalDistanceSq = distSq;
					}
				}
			}
			return returned;
		}

		return null;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		if (area.containsArea(this))
			return this.clone();
		if (containsArea(area))
			return area.clone();
		if (area instanceof FGEAbstractLine)
			return computeIntersection((FGEAbstractLine) area);
		if (area instanceof FGEArc && ((FGEArc) area).overlap(this)) {
			return computeArcIntersection((FGEArc) area);
		}
		if (area instanceof FGEBand || area instanceof FGEHalfBand) {
			// TODO please really implement this
			FGERectangle boundingBox = getBoundingBox();
			boundingBox.setIsFilled(true);
			FGEArea boundsIntersect = boundingBox.intersect(area);
			if (boundsIntersect instanceof FGEEmptyArea) {
				return new FGEEmptyArea();
			}
			FGEArea returned = intersect(boundsIntersect);
			if (returned instanceof FGEIntersectionArea) {
				// Cannot do better, sorry
			}
			return returned;
		}

		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable())
			return returned.makeDevelopped();
		else
			return returned;
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea union(FGEArea area) {
		if (containsArea(area))
			return clone();
		if (area.containsArea(this))
			return area.clone();

		if (area instanceof FGEArc && ((FGEArc) area).overlap(this)) {
			return computeArcUnion((FGEArc) area);
		}
		return new FGEUnionArea(this, area);
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		if (!getFGEBounds().containsPoint(p))
			return false;

		double radianAngle = angleForPoint(p);

		if (!includeAngle(radianAngle))
			return false;

		FGEPoint outlinePoint = getPointAtRadianAngle(radianAngle);

		if (getFGEArcType() == ArcType.OPEN) {
			return outlinePoint.equals(p);
		}

		else if ((new FGESegment(getCenter(), outlinePoint)).containsPoint(p)) {
			if (getFGEArcType() == ArcType.PIE)
				return true;
			else if (getFGEArcType() == ArcType.CHORD) {
				return !((new FGEPolygon(Filling.FILLED, getCenter(), getStartPoint(), getEndPoint())).contains(p));
			}
		}

		return false;

		// FGEArea intersection = computeIntersection(new FGELine(getCenter(),p));
		// System.out.println("p= "+p+" intersection="+intersection+" intersection.containsPoint(p)="+intersection.containsPoint(p));
		// return (intersection.containsPoint(p));
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		return (containsPoint(l.getP1()) && containsPoint(l.getP2()) && (l instanceof FGESegment));
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEArc && ((FGEArc) a).overlap(this)) {
			FGEArea arc1 = _equivalentForArcIn1D(this, false);
			FGEArea arc2 = _equivalentForArcIn1D(((FGEArc) a), false);
			if (arc1.containsArea(arc2))
				return true;
			arc1 = _equivalentForArcIn1D(this, true);
			arc2 = _equivalentForArcIn1D(((FGEArc) a), true);
			if (arc1.containsArea(arc2))
				return true;
			return false;
		}
		if (a instanceof FGEPoint)
			return containsPoint((FGEPoint) a);
		if (a instanceof FGESegment)
			return containsPoint(((FGESegment) a).getP1()) && containsPoint(((FGESegment) a).getP2());
		if (a instanceof FGEShape)
			return FGEShape.AreaComputation.isShapeContainedInArea((FGEShape<?>) a, this);
		return false;
	}

	@Override
	public FGEArc transform(AffineTransform t) {
		// logger.info("Not well implemented, do it later");

		// TODO: this implementation is not correct if AffineTransform contains rotation

		FGEPoint p1 = (new FGEPoint(getX(), getY())).transform(t);
		FGEPoint p2 = (new FGEPoint(getX() + getWidth(), getY() + getHeight())).transform(t);

		// TODO: if transformation contains a rotation, change angle start, with and heigth are not correct either
		return new FGEArc(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), getAngleStart(),
				getAngleExtent(), getFGEArcType());
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(getCenter());
		returned.add(getStartPoint());
		returned.add(getEndPoint());
		/*returned.add(new FGEPoint(x+width/2.0,y));
		returned.add(new FGEPoint(x,y+height/2.0));
		returned.add(new FGEPoint(x+width/2.0,y+height));
		returned.add(new FGEPoint(x+width,y+height/2.0));*/

		return returned;
	}

	@Override
	public void paint(FGEGraphics g) {
		if (getFGEArcType() == ArcType.CHORD || getFGEArcType() == ArcType.PIE) {
			g.useDefaultBackgroundStyle();
			g.fillArc(getX(), getY(), getWidth(), getHeight(), getAngleStart(), getAngleExtent());
		}
		g.useDefaultForegroundStyle();
		Stroke defaultForegroundStroke = null;
		if (g.getGraphicalRepresentation().getSpecificStroke() != null) {
			defaultForegroundStroke = g.g2d.getStroke();
			g.g2d.setStroke(g.getGraphicalRepresentation().getSpecificStroke());
		}
		g.drawArc(getX(), getY(), getWidth(), getHeight(), getAngleStart(), getAngleExtent());
		if (defaultForegroundStroke != null) {
			g.g2d.setStroke(defaultForegroundStroke);
		}
	}

	@Override
	public String toString() {
		return "FGEArc: (" + x + "," + y + "," + width + "," + height + "," + getAngleStart() + "," + getAngleExtent() + " type="
				+ getFGEArcType() + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	/*public FGERectangle getBoundingBox()
	{
		return new FGERectangle(x,y,width,height,Filling.FILLED);
	}*/

	@Override
	public FGERectangle getBoundingBox() {
		double minX = java.lang.Double.POSITIVE_INFINITY;
		double minY = java.lang.Double.POSITIVE_INFINITY;
		double maxX = java.lang.Double.NEGATIVE_INFINITY;
		double maxY = java.lang.Double.NEGATIVE_INFINITY;

		Vector<java.lang.Double> angles = new Vector<java.lang.Double>();
		if (includeAngle(0))
			angles.add(0.0);
		if (includeAngle(Math.PI / 2))
			angles.add(Math.PI / 2);
		if (includeAngle(Math.PI))
			angles.add(Math.PI);
		if (includeAngle(3 * Math.PI / 2))
			angles.add(3 * Math.PI / 2);
		angles.add(getRadianAngleStart());
		angles.add(getRadianAngleStart() + getRadianAngleExtent());
		for (java.lang.Double angle : angles) {
			FGEPoint p = getPointAtRadianAngle(angle);
			if (p.x < minX)
				minX = p.x;
			if (p.x > maxX)
				maxX = p.x;
			if (p.y < minY)
				minY = p.y;
			if (p.y > maxY)
				maxY = p.y;
		}

		return new FGERectangle(new FGEPoint(minX, minY), new FGEPoint(maxX, maxY), Filling.FILLED);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEArc) {
			FGEArc p = (FGEArc) obj;
			if (getFGEArcType() != p.getFGEArcType()) {
				return false;
			}
			return ((Math.abs(getX() - p.getX()) <= EPSILON) && (Math.abs(getY() - p.getY()) <= EPSILON)
					&& (Math.abs(getWidth() - p.getWidth()) <= EPSILON) && (Math.abs(getHeight() - p.getHeight()) <= EPSILON)
					&& (Math.abs(normalizeDegAngle(getAngleStart()) - normalizeDegAngle(p.getAngleStart())) <= EPSILON) && (Math
					.abs(getAngleExtent() - p.getAngleExtent()) <= EPSILON));
		}
		return super.equals(obj);
	}

	public boolean overlap(FGEArc arc) {
		if (getFGEArcType() != arc.getFGEArcType()) {
			return false;
		}
		return ((Math.abs(getX() - arc.getX()) <= EPSILON) && (Math.abs(getY() - arc.getY()) <= EPSILON)
				&& (Math.abs(getWidth() - arc.getWidth()) <= EPSILON) && (Math.abs(getHeight() - arc.getHeight()) <= EPSILON));
	}

	@Override
	public final FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == null)
			return new FGEEmptyArea();
		FGERectangle boundingBox;
		FGEArea anchorArea = getAnchorAreaFrom(orientation);
		if (anchorArea instanceof FGEArc)
			boundingBox = ((FGEArc) anchorArea).getBoundingBox();
		else
			boundingBox = getBoundingBox();

		// System.out.println("Orientation: "+orientation+" Bounding box for "+anchorArea+" is "+boundingBox);

		FGEArea hb = null;
		switch (orientation) {
		case EAST:
			hb = boundingBox.getWest().getOrthogonalPerspectiveArea(orientation);
			break;
		case WEST:
			hb = boundingBox.getEast().getOrthogonalPerspectiveArea(orientation);
			break;
		case NORTH:
			hb = boundingBox.getSouth().getOrthogonalPerspectiveArea(orientation);
			break;
		case SOUTH:
			hb = boundingBox.getNorth().getOrthogonalPerspectiveArea(orientation);
			break;
		default:
			return new FGEEmptyArea();
		}
		FGEArc filledArc = clone();
		filledArc.setIsFilled(true);
		return new FGESubstractionArea(hb, filledArc, false);
	}

	@Override
	public final FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		FGEArc returned;
		switch (orientation) {
		case EAST:
			returned = new FGEArc(x, y, width, height, -90, 180, ArcType.OPEN);
			break;
		case WEST:
			returned = new FGEArc(x, y, width, height, 90, 180, ArcType.OPEN);
			break;
		case NORTH:
			returned = new FGEArc(x, y, width, height, 0, 180, ArcType.OPEN);
			break;
		case SOUTH:
			returned = new FGEArc(x, y, width, height, -180, 180, ArcType.OPEN);
			break;
		default:
			return null;
		}

		return computeArcIntersection(returned);

	}

	/**
	 * Compute angle extent given start and end angle
	 * 
	 * @param startAngle
	 *            , expressed in radians
	 * @param endAngle
	 *            , expressed in radians
	 * @return extent in radians
	 */
	public static double angleExtent(double startAngle, double endAngle) {
		double start = normalizeRadianAngle(startAngle);
		double end = normalizeRadianAngle(endAngle);
		if (start <= end)
			return end - start;
		else
			return end + 2 * Math.PI - start;
	}

	/**
	 * Compute an equivalent for an arc in the 1-D model Angles are normalized and a segment (or a union of 2 segments mapping 2.PI-modulo
	 * is retrieved )
	 * 
	 * @param anArc
	 * @return
	 */
	private static FGEArea _equivalentForArcIn1D(FGEArc anArc, boolean forIntersection) {
		double startAngle;
		double endAngle;

		if (anArc instanceof FGEEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI) {
			return new FGEUnionArea(new FGESegment(new FGEPoint(-Math.PI, 0), new FGEPoint(Math.PI, 0)), new FGESegment(new FGEPoint(
					Math.PI, 0), new FGEPoint(3 * Math.PI, 0)));
		}

		if (anArc.getAngleExtent() >= 0) {
			startAngle = anArc.getRadianAngleStart();
			endAngle = normalizeRadianAngle(anArc.getRadianAngleStart() + anArc.getRadianAngleExtent());
		} else {
			endAngle = anArc.getRadianAngleStart();
			startAngle = normalizeRadianAngle(anArc.getRadianAngleStart() + anArc.getRadianAngleExtent());
		}

		if (endAngle < startAngle)
			endAngle += 2 * Math.PI;

		FGESegment segment = new FGESegment(new FGEPoint(startAngle, 0), new FGEPoint(endAngle, 0));

		if (anArc.includeAngle(Math.PI)) {
			if (forIntersection) {
				return new FGEUnionArea(segment, new FGESegment(new FGEPoint(startAngle + 2 * Math.PI, 0), new FGEPoint(endAngle + 2
						* Math.PI, 0)));
			}
		}

		return segment;
	}

	/**
	 * Compute an equivalent for an area in the 1-D model to arc model
	 * 
	 * @param anArc
	 * @return
	 */
	private FGEArea _equivalentToArcIn1D(FGEArea anArea) {
		if (anArea instanceof FGEEmptyArea)
			return new FGEEmptyArea();
		else if (anArea instanceof FGESegment) {
			FGESegment s = (FGESegment) anArea;
			if (s.getP2().x >= s.getP1().x + 2 * Math.PI || s.getP2().x <= s.getP1().x - 2 * Math.PI) {
				return new FGEEllips(x, y, width, height, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
			}
			return new FGEArc(x, y, width, height, Math.toDegrees(normalizeRadianAngle(s.getP1().x)), Math.toDegrees(s.getP2().x
					- s.getP1().x), getFGEArcType());
		} else if (anArea instanceof FGEPoint) {
			FGEPoint p = (FGEPoint) anArea;
			if (getIsFilled())
				return new FGESegment(getCenter(), getPointAtRadianAngle(p.x));
			else
				return getPointAtRadianAngle(p.x);
		} else if (anArea instanceof FGEUnionArea) {
			FGEUnionArea u = (FGEUnionArea) anArea;
			// if (u.isUnionOfSegments() || u.isUnionOfPoints()) {
			Vector<FGEArea> returned = new Vector<FGEArea>();
			for (FGEArea a : u.getObjects()) {
				FGEArea newArea = _equivalentToArcIn1D(a);
				if (!returned.contains(newArea))
					returned.add(newArea);
			}
			if (returned.size() == 1)
				return returned.firstElement();
			if (returned.size() == 2 && returned.firstElement() instanceof FGEArc && returned.elementAt(1) instanceof FGEArc) {
				// There is still a case where there can be 2 adjascent arcs
				// At point corresponding to -180 degree
				FGEArc arc1 = (FGEArc) returned.firstElement();
				FGEArc arc2 = (FGEArc) returned.elementAt(1);
				boolean arc1StartFromPI = FGEUtils.doubleEquals(arc1.getRadianAngleStart(), Math.PI)
						|| FGEUtils.doubleEquals(arc1.getRadianAngleStart(), -Math.PI);
				boolean arc1EndToPI = FGEUtils.doubleEquals(arc1.getRadianAngleEnd(), Math.PI)
						|| FGEUtils.doubleEquals(arc1.getRadianAngleEnd(), -Math.PI);
				boolean arc2StartFromPI = FGEUtils.doubleEquals(arc2.getRadianAngleStart(), Math.PI)
						|| FGEUtils.doubleEquals(arc2.getRadianAngleStart(), -Math.PI);
				boolean arc2EndToPI = FGEUtils.doubleEquals(arc2.getRadianAngleEnd(), Math.PI)
						|| FGEUtils.doubleEquals(arc2.getRadianAngleEnd(), -Math.PI);
				if (arc1EndToPI && arc2StartFromPI) {
					return _equivalentToArcIn1D(new FGESegment(new FGEPoint(arc1.getRadianAngleStart(), 0), new FGEPoint(
							arc2.getRadianAngleEnd() + 2 * Math.PI, 0)));
				}
				if (arc2EndToPI && arc1StartFromPI) {
					return _equivalentToArcIn1D(new FGESegment(new FGEPoint(arc2.getRadianAngleStart(), 0), new FGEPoint(
							arc1.getRadianAngleEnd() + 2 * Math.PI, 0)));
				}
			}
			FGEUnionArea union = new FGEUnionArea(returned);

			if (returned.size() == 2 && returned.get(0) instanceof FGEArc && returned.get(1) instanceof FGEPoint) {
				(new Exception("------------ Ca vient de la ce truc bizarre !!!")).printStackTrace();
				logger.info("Area=" + anArea);
			}

			if (union.getObjects().size() == 1)
				return union.getObjects().firstElement();
			return union;
			// }
		}
		logger.warning("Unexpected result: " + anArea);
		return new FGEEmptyArea();

	}

	/**
	 * Compute arc union, asserting that this arc and supplied arc differs only because of their start angles and/or angle extent
	 * 
	 * @param anArc
	 * @return
	 */
	protected FGEArea computeArcUnion(FGEArc anArc) {
		if (anArc instanceof FGEEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI
				|| this instanceof FGEEllips || getRadianAngleExtent() >= 2 * Math.PI || getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return new FGEEllips(x, y, width, height, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
		}

		FGEArea equ1 = _equivalentForArcIn1D(this, false);
		FGEArea equ2 = _equivalentForArcIn1D(anArc, false);

		FGEArea union = equ1.union(equ2);

		/*System.out.println("arc1="+this);
		System.out.println("arc2="+anArc);
		System.out.println("equ1="+equ1);
		System.out.println("equ2="+equ2);
		System.out.println("union="+union);*/

		return _equivalentToArcIn1D(union);
	}

	/**
	 * Compute arc intersection, asserting that this arc and supplied arc differs only because of their start angles and/or angle extent
	 * 
	 * @param anArc
	 * @return
	 */
	protected FGEArea computeArcIntersection(FGEArc anArc) {
		if (anArc instanceof FGEEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return clone();
		}
		if (this instanceof FGEEllips || getRadianAngleExtent() >= 2 * Math.PI || getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return anArc.clone();
		}

		FGEArea equ1 = _equivalentForArcIn1D(this, true);
		FGEArea equ2 = _equivalentForArcIn1D(anArc, true);

		FGEArea intersection = equ1.intersect(equ2);

		/*System.out.println("arc1="+this);
		System.out.println("arc2="+anArc);
		System.out.println("equ1="+equ1);
		System.out.println("arc2="+equ2);
		System.out.println("intersection="+intersection);*/

		return _equivalentToArcIn1D(intersection);
	}

	// Old (buggy implementation)
	/*protected FGEArea computeArcIntersection2(FGEArc anArc)
	{
		double startAngle1 = getRadianAngleStart();
		double endAngle1 = normalizeRadianAngle(getRadianAngleStart()+getRadianAngleExtent());
		double startAngle2 = anArc.getRadianAngleStart();
		double endAngle2 = normalizeRadianAngle(anArc.getRadianAngleStart()+anArc.getRadianAngleExtent());
		
		double startAngle;
		double endAngle;
		
		if (anArc.includeAngle(startAngle1)) {
			if (anArc.includeAngle(endAngle1)) {
				System.out.println("anArc="+anArc+" include angle "+Math.toDegrees(endAngle1));
				double midAngle1 = (startAngle1+endAngle1)/2;
				//if (anArc.includeAngle(midAngle1)) {
				if (getRadianAngleExtent() < anArc.getRadianAngleExtent()) {
					System.out.println("Hop1");
					System.out.println("anArc="+anArc);
					System.out.println("this="+this);
					System.out.println("getRadianAngleExtent()="+getRadianAngleExtent());
					System.out.println("anArc.getRadianAngleExtent()="+anArc.getRadianAngleExtent());
					return this.clone();
				}
				else {
					double a1 = Math.toDegrees(startAngle1);
					double e1 = Math.toDegrees(angleExtent(startAngle1,endAngle2));
					double a2 = Math.toDegrees(startAngle2);
					double e2 = Math.toDegrees(angleExtent(startAngle2,endAngle1));
					FGEArc arc1 = new FGEArc(x,y,width,height,a1,e1,getFGEArcType());
					FGEArc arc2 = new FGEArc(x,y,width,height,a2,e2,getFGEArcType());
					
					System.out.println("Hop2");
					return new FGEUnionArea(arc1,arc2);
				}
			}
			else {
				startAngle = startAngle1;
				endAngle = endAngle2;
				System.out.println("Hop3");
			}
		}
		else {
			if (anArc.includeAngle(endAngle1)) {
				startAngle = startAngle2;
				endAngle = endAngle1;
				System.out.println("Hop4");
			}
			else {
				double midAngle2 = (startAngle2+endAngle2)/2;
				if (includeAngle(midAngle2)) {
					System.out.println("Hop5");
					return anArc.clone();
				}
				else {
					System.out.println("Hop6");
					return new FGEEmptyArea();
				}
			}
		}
		
		double angleExtent = Math.toDegrees(angleExtent(startAngle,endAngle));
		startAngle = Math.toDegrees(startAngle);
		
		return new FGEArc(x,y,width,height,startAngle,angleExtent,getFGEArcType());

	}*/

	public FGEPoint getPointAtAngle(double degAngle) {
		return getPointAtRadianAngle(Math.toRadians(degAngle));
	}

	public FGEPoint getPointAtRadianAngle(double radianAngle) {
		return new FGEPoint(getCenter().x + getWidth() / 2 * Math.cos(radianAngle), getCenter().y - getHeight() / 2 * Math.sin(radianAngle));
	}

	/**
	 * 
	 * @param p
	 * @return angle in radians
	 */
	public double angleForPoint(FGEPoint p) {
		FGEHalfLine hl = new FGEHalfLine(getCenter(), p);
		LineIntersectionResult result = _computeIntersection(hl);

		if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.NO_SOLUTION) {
			logger.warning("Unexpected situation here");
			return java.lang.Double.NaN;
		}

		else if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.ONE_SOLUTION) {
			return result.alpha1;
		}

		else if (result.solutionType == org.openflexo.fge.geom.FGEArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS) {
			FGEPoint p1 = new FGEPoint(width / 2.0 * Math.cos(-result.alpha1) + x + width / 2.0, height / 2.0 * Math.sin(-result.alpha1)
					+ y + height / 2.0);
			FGEPoint p2 = new FGEPoint(width / 2.0 * Math.cos(-result.alpha2) + x + width / 2.0, height / 2.0 * Math.sin(-result.alpha2)
					+ y + height / 2.0);
			if (hl.containsPoint(p1))
				return result.alpha1;
			else
				/*if (hl.containsPoint(p2))*/return result.alpha2;
		}

		logger.warning("Unexpected situation while retrieving angle for point " + p + " on " + this);
		logger.warning("result=" + result);
		return java.lang.Double.NaN;
	}

	public boolean isClockWise() {
		return extent < 0;
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
		// TODO: sub-optimal implementation, you can do better job
		return new FGERectangle(x, y, width, height, Filling.FILLED);
	}

	public FGEPoint getMiddle() {
		if (this instanceof FGEEllips) {
			logger.warning("getMiddle() invoked for an ellips");
			return getCenter();
		} else {
			double angle = (getAngleStart() + getAngleExtent() / 2);
			return getPointAtAngle(angle);
		}
	}

}
