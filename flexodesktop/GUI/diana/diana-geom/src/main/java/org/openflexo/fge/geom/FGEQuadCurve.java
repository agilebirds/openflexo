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
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.QuadCurve2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEExclusiveOrArea;
import org.openflexo.fge.geom.area.FGEIntersectionArea;
import org.openflexo.fge.geom.area.FGESubstractionArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

@SuppressWarnings("serial")
public class FGEQuadCurve extends Double implements FGEGeneralShape.GeneralShapePathElement<FGEQuadCurve> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FGEQuadCurve.class.getPackage().getName());

	/**
	 * This value is internally used to compute approximated data (nearest point, distance, etc...)
	 */
	private static final double FLATTENING_PATH_LEVEL = 0.01;

	private FGEPoint p3, pp1, pp2;

	public FGEQuadCurve() {
		super();
	}

	/**
	 * Build a quadratic curve given 3 points.
	 * 
	 * @param p1
	 *            start point
	 * @param ctrlP
	 *            real control point (not located on curve)
	 * @param p2
	 *            end point
	 */
	public FGEQuadCurve(FGEPoint p1, FGEPoint ctrlP, FGEPoint p2) {
		super();
		try {
			setCurve(p1.x, p1.y, ctrlP.x, ctrlP.y, p2.x, p2.y);
		} catch (NullPointerException e) {
			e.printStackTrace();
			logger.warning("Unexpected NPE");
		}
	}

	/**
	 * Build a quadratic curve given 3 points. The curve will cross each point
	 * 
	 * @param p1
	 *            start point
	 * @param p3
	 *            control point LOCATED on curve
	 * @param p2
	 *            end point
	 * @return
	 */
	public static FGEQuadCurve makeCurveFromPoints(FGEPoint p1, FGEPoint p3, FGEPoint p2) {
		FGEQuadCurve returned = new FGEQuadCurve(p1, p3, p2);
		returned.setP3(p3);
		return returned;
	}

	@Override
	public FGEPoint getP1() {
		return new FGEPoint(x1, y1);
	}

	public void setP1(FGEPoint aPoint) {
		x1 = aPoint.x;
		y1 = aPoint.y;
		update();
	}

	@Override
	public FGEPoint getP2() {
		return new FGEPoint(x2, y2);
	}

	public void setP2(FGEPoint aPoint) {
		x2 = aPoint.x;
		y2 = aPoint.y;
		update();
	}

	public FGEPoint getCtrlPoint() {
		return new FGEPoint(ctrlx, ctrly);
	}

	public void setCtrlPoint(FGEPoint aPoint) {
		ctrlx = aPoint.x;
		ctrly = aPoint.y;
		update();
	}

	@Override
	public void setCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
		super.setCurve(x1, y1, ctrlx, ctrly, x2, y2);
		update();
	}

	private void update() {
		pp1 = FGEPoint.middleOf(getP1(), getCtrlPoint());
		pp2 = FGEPoint.middleOf(getP2(), getCtrlPoint());
		p3 = FGEPoint.middleOf(pp1, pp2);
	}

	public FGEPoint getP3() {
		return p3;
	}

	public void setP3(FGEPoint aPoint) {
		FGEPoint pp = FGEPoint.middleOf(getP1(), getP2());
		FGEPoint cp = new FGESegment(pp, aPoint).getScaledPoint(2);
		setCtrlPoint(cp);
	}

	public FGEPoint getPP1() {
		return pp1;
	}

	public FGEPoint getPP2() {
		return pp2;
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		g.useDefaultForegroundStyle();
		g.drawCurve(this);

		// DEBUG
		// g.setDefaultForeground(ForegroundStyle.makeStyle(Color.RED));
		// (buildFlattenPath(0.01)).paint(g);
	}

	@Override
	public FGEQuadCurve transform(AffineTransform t) {
		return new FGEQuadCurve(getP1().transform(t), getCtrlPoint().transform(t), getP2().transform(t));
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(getP1());
		returned.add(getP2());
		return returned;
	}

	@Override
	public String toString() {
		return "FGEQuadCurve: [" + getP1() + "," + getCtrlPoint() + "," + getP2() + "]";
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine<?> l) {
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return super.contains(p);
	}

	@Override
	public FGEArea getAnchorAreaFrom(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public FGEQuadCurve clone() {
		return (FGEQuadCurve) super.clone();
	}

	private FGEPolylin buildFlattenPath(double flatness) {
		FGEPolylin returned = new FGEPolylin();
		PathIterator p = getPathIterator(null);
		FlatteningPathIterator f = new FlatteningPathIterator(p, flatness);
		while (!f.isDone()) {
			float[] pts = new float[6];
			switch (f.currentSegment(pts)) {
			case PathIterator.SEG_MOVETO:
				// returned.addToPoints(new FGEPoint(pts[0],pts[1]));
			case PathIterator.SEG_LINETO:
				returned.addToPoints(new FGEPoint(pts[0], pts[1]));
			}
			f.next();
		}
		return returned;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		// TODO do something better later
		// logger.warning("Please implement me better later");
		return getApproximatedNearestPoint(aPoint);
	}

	public FGEPoint getApproximatedNearestPoint(FGEPoint aPoint) {
		double minimizedDistance = java.lang.Double.POSITIVE_INFINITY;
		FGEPoint returned = null;
		FGEPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		for (FGESegment s : flattenPath.getSegments()) {
			FGEPoint nearestPoint = s.getNearestPointOnSegment(aPoint);
			double currentDistance = FGEPoint.distance(nearestPoint, aPoint);
			if (currentDistance < minimizedDistance) {
				minimizedDistance = currentDistance;
				returned = nearestPoint;
			}
		}
		return returned;
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	public FGESegment getApproximatedStartTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().firstElement();
	}

	public FGESegment getApproximatedEndTangent() {
		return buildFlattenPath(FLATTENING_PATH_LEVEL).getSegments().lastElement();
	}

	public FGESegment getApproximatedControlPointTangent() {
		double minimizedDistance = java.lang.Double.POSITIVE_INFINITY;
		FGESegment returned = null;
		FGEPolylin flattenPath = buildFlattenPath(FLATTENING_PATH_LEVEL);
		for (FGESegment s : flattenPath.getSegments()) {
			FGEPoint nearestPoint = s.getNearestPointOnSegment(getP3());
			double currentDistance = FGEPoint.distance(nearestPoint, getP3());
			if (currentDistance < minimizedDistance) {
				minimizedDistance = currentDistance;
				returned = s;
			}
		}
		return returned;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		FGEIntersectionArea returned = new FGEIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		} else {
			return returned;
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
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEQuadCurve) {
			FGEQuadCurve s = (FGEQuadCurve) obj;
			return (getP1().equals(s.getP1()) || getP1().equals(s.getP2())) && (getP2().equals(s.getP1()) || getP2().equals(s.getP2()))
					&& getCtrlPoint().equals(s.getCtrlPoint());
		}
		return false;
	}

	// http://forum.java.sun.com/thread.jspa?threadID=420749&messageID=3752991

	public boolean curveIntersects(QuadCurve2D curve, double rx, double ry, double rw, double rh) {
		/**
		 * A quadratic bezier curve has the following parametric equation:
		 * 
		 * Q(t) = P0(1-t)^2 + P1(2t(1-t)) + P2(t^2)
		 * 
		 * Where 0 <= t <= 1 and P0 is the starting point, P1 is the control point and P2 is the end point.
		 * 
		 * Therefore, the equations for the x and y coordinates are:
		 * 
		 * Qx(t) = Px0(1-t)^2 + Px1(2t(1-t)) + Px2(t^2) Qy(t) = Py0(1-t)^2 + Py1(2t(1-t)) + Py2(t^2)
		 * 
		 * 0 <= t <= 1
		 * 
		 * A bezier curve intersects a rectangle if:
		 * 
		 * 1 - Either one of its endpoints is inside of the rectangle 2 - The curve intersects one of the rectangles sides (top, bottom,
		 * left or right)
		 * 
		 * The equation for a horizontal line is:
		 * 
		 * y = c
		 * 
		 * The line intersects the bezier if:
		 * 
		 * Qy(t) = c and 0 <= t <= 1
		 * 
		 * We can rewrite this as:
		 * 
		 * -c + Py0 + (-2Py0 + 2Py1)t + (Py0 - 2Py1 + Py2) t^2 == 0 and 0 <= t <= 1
		 * 
		 * We can use the valid roots of the quadratic, to evaluate Qx(t), and see if the value falls withing the rectangle bounds.
		 * 
		 * The case for vertical lines is analogous to this one. (juancn)
		 */
		double y1 = curve.getY1();
		double y2 = curve.getY2();
		double x1 = curve.getX1();
		double x2 = curve.getX2();

		// If the rectangle contains one of the endpoints, it intersects the curve
		if (rectangleContains(x1, y1, rx, ry, rw, rh) || rectangleContains(x2, y2, rx, ry, rw, rh)) {
			return true;
		}

		double eqn[] = new double[3];
		double ctrlY = curve.getCtrlY();
		double ctrlX = curve.getCtrlX();

		return intersectsLine(eqn, y1, ctrlY, y2, ry, x1, ctrlX, x2, rx, rx + rw) // Top
				|| intersectsLine(eqn, y1, ctrlY, y2, ry + rh, x1, ctrlX, x2, rx, rx + rw) // Bottom
				|| intersectsLine(eqn, x1, ctrlX, x2, rx, y1, ctrlY, y2, ry, ry + rh) // Left
				|| intersectsLine(eqn, x1, ctrlX, x2, rx + rw, y1, ctrlY, y2, ry, ry + rh); // Right
	}

	private boolean rectangleContains(double x, double y, double rx, double ry, double rw, double rh) {
		return x >= rx && y >= ry && x < rx + rw && y < ry + rh;
	}

	/**
	 * Returns true if a line segment parallel to one of the axis intersects the specified curve. This function works fine if you reverse
	 * the axes.
	 * 
	 * @param eqn
	 *            a double[] of lenght 3 used to hold the quadratic equation coeficients
	 * @param p0
	 *            starting point of the curve at the desired axis (i.e.: curve.getX1())
	 * @param p1
	 *            control point of the curve at the desired axis (i.e.: curve.getCtrlX())
	 * @param p2
	 *            end point of the curve at the desired axis (i.e.: curve.getX2())
	 * @param c
	 *            where is the line segment (i.e.: in X axis)
	 * @param pb0
	 *            starting point of the curve at the other axis (i.e.: curve.getY1())
	 * @param pb1
	 *            control point of the curve at the other axis (i.e.: curve.getCtrlY())
	 * @param pb2
	 *            end point of the curve at the other axis (i.e.: curve.getY2())
	 * @param from
	 *            starting point of the line segment (i.e.: in Y axis)
	 * @param to
	 *            end point of the line segment (i.e.: in Y axis)
	 * @return
	 */
	private static boolean intersectsLine(double[] eqn, double p0, double p1, double p2, double c, double pb0, double pb1, double pb2,
			double from, double to) {
		/**
		 * First we check if a line parallel to the axis we are evaluating intersects the curve (the line is at c).
		 * 
		 * Then we check if any of the intersection points is between 'from' and 'to' in the other axis (wether it belongs to the rectangle)
		 */

		// Fill the coefficients of the equation
		eqn[2] = p0 - 2 * p1 + p2;
		eqn[1] = 2 * p1 - 2 * p0;
		eqn[0] = p0 - c;

		int nRoots = QuadCurve2D.solveQuadratic(eqn);
		boolean result;
		switch (nRoots) {
		case 1:
			result = eqn[0] >= 0 && eqn[0] <= 1;
			if (result) {
				double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[0]);
				result = intersection >= from && intersection <= to;
			}
			break;
		case 2:
			result = eqn[0] >= 0 && eqn[0] <= 1;
			if (result) {
				double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[0]);
				result = intersection >= from && intersection <= to;
			}

			// If the first root is not a valid intersection, try the other one
			if (!result) {
				result = eqn[1] >= 0 && eqn[1] <= 1;
				if (result) {
					double intersection = evalQuadraticCurve(pb0, pb1, pb2, eqn[1]);
					result = intersection >= from && intersection <= to;
				}
			}

			break;
		default:
			result = false;
		}
		return result;
	}

	public static double evalQuadraticCurve(double c1, double ctrl, double c2, double t) {
		double u = 1 - t;
		double res = c1 * u * u + 2 * ctrl * t * u + c2 * t * t;

		return res;
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
		Rectangle2D bounds2D = getBounds2D();

		return new FGERectangle(bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight(), Filling.FILLED);
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
		// TODO: not implemented
		return null;

	}

}
