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
import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Represents a complex curve, with at least 2 points<br>
 * This complex curve is defined by a list of points linked with bezier curves, depending on defined closure.<br>
 * 
 * @author sylvain
 * 
 */
public class FGEComplexCurve extends FGEGeneralShape<FGEComplexCurve> {

	private static final Logger logger = Logger.getLogger(FGEComplexCurve.class.getPackage().getName());

	private Vector<FGEPoint> _points;

	private FGERectangle bounds;

	public FGEComplexCurve(Closure closure) {
		super(closure);
		_points = new Vector<FGEPoint>();
	}

	public FGEComplexCurve() {
		this(Closure.OPEN_NOT_FILLED);
	}

	public FGEComplexCurve(Closure closure, List<FGEPoint> points) {
		this(closure);
		for (FGEPoint p : points) {
			_addToPoints(p);
		}
		updateCurve();
	}

	public FGEComplexCurve(Closure closure, FGEPoint... points) {
		this(closure);
		for (FGEPoint p : points) {
			_addToPoints(p);
		}
		updateCurve();
	}

	public void clearPoints() {
		_points.clear();
	}

	public Vector<FGEPoint> getPoints() {
		return _points;
	}

	public void setPoints(Vector<FGEPoint> points) {
		_points.clear();
		for (FGEPoint p : points) {
			addToPoints(p);
		}
	}

	private void _addToPoints(FGEPoint p) {
		_points.add(p);
	}

	public void addToPoints(FGEPoint p) {
		_points.add(p);
		updateCurve();
		// reCalculateBounds();
	}

	public int getPointsNb() {
		return _points.size();
	}

	public FGEPoint getPointAt(int index) {
		return _points.elementAt(index);
	}

	private void updateCurve() {
		if (_points.size() == 2) {
			beginAtPoint(_points.get(0));
			addSegment(_points.get(1));

		} else if (_points.size() > 2) {
			if (getClosure() == Closure.OPEN_FILLED || getClosure() == Closure.OPEN_NOT_FILLED) {
				updateAsOpenedCurve();
			} else if (getClosure() == Closure.CLOSED_FILLED || getClosure() == Closure.CLOSED_NOT_FILLED) {
				updateAsClosedCurve();
			} else {
				logger.warning("unexpected closure:" + getClosure());
			}
		}
	}

	private void updateAsOpenedCurve() {
		if (_points.size() > 2) {
			for (int i = 0; i < _points.size(); i++) {
				FGEPoint current = _points.get(i);
				if (i == 0) {
					beginAtPoint(current);
				} else if (i == 1) {
					// First segment: quadratic curve
					FGEQuadCurve curve = FGEQuadCurve.makeCurveFromPoints(_points.get(0), _points.get(1), _points.get(2));
					QuadCurve2D left = new QuadCurve2D.Double();
					QuadCurve2D right = new QuadCurve2D.Double();
					curve.subdivide(left, right);
					addQuadCurve(left.getCtrlPt(), left.getP2());
				} else if (i == _points.size() - 1) {
					// Last segment: quadratic curve
					FGEQuadCurve curve = FGEQuadCurve.makeCurveFromPoints(_points.get(i - 2), _points.get(i - 1), _points.get(i));
					QuadCurve2D left = new QuadCurve2D.Double();
					QuadCurve2D right = new QuadCurve2D.Double();
					curve.subdivide(left, right);
					addQuadCurve(right.getCtrlPt(), right.getP2());
				} else {
					// Cubic segment
					FGEQuadCurve leftCurve = FGEQuadCurve.makeCurveFromPoints(_points.get(i - 2), _points.get(i - 1), _points.get(i));
					FGEQuadCurve rightCurve = FGEQuadCurve.makeCurveFromPoints(_points.get(i - 1), _points.get(i), _points.get(i + 1));
					/*FGECubicCurve curve = new FGECubicCurve(
							_points.get(i-1),
							rightCurve.getPP1(),
							leftCurve.getPP2(),
							_points.get(i));*/
					addCubicCurve(leftCurve.getPP2(), rightCurve.getPP1(), _points.get(i));
				}
			}
		}
	}

	private void updateAsClosedCurve() {
		if (_points.size() > 2) {
			for (int i = 0; i < _points.size() + 1; i++) {
				// FGEPoint current = _points.get(i);
				if (i == 0) {
					beginAtPoint(_points.get(0));
				}

				// Cubic segment
				FGEQuadCurve leftCurve = FGEQuadCurve.makeCurveFromPoints(getPointAtIndex(i - 2), getPointAtIndex(i - 1),
						getPointAtIndex(i));
				FGEQuadCurve rightCurve = FGEQuadCurve.makeCurveFromPoints(getPointAtIndex(i - 1), getPointAtIndex(i),
						getPointAtIndex(i + 1));
				/*FGECubicCurve curve = new FGECubicCurve(
						_points.get(i-1),
						rightCurve.getPP1(),
						leftCurve.getPP2(),
						_points.get(i));*/
				if (i > 0) {
					addCubicCurve(leftCurve.getPP2(), rightCurve.getPP1(), getPointAtIndex(i));
				}
			}
		}
	}

	// Implements circular index
	private FGEPoint getPointAtIndex(int i) {
		if (i < 0) {
			return getPointAt(i + _points.size());
		} else if (i >= _points.size()) {
			return getPointAt(i - _points.size());
		} else {
			return _points.get(i);
		}
	}

	public void geometryChanged() {
		updateCurve();
		// reCalculateBounds();
	}

	/*private void reCalculateBounds() {
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
		bounds = new FGERectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY, getIsFilled() ? Filling.FILLED
				: Filling.NOT_FILLED);
	}*/

	/*@Override
	public FGERectangle getBoundingBox() {
		if (bounds == null) {
			reCalculateBounds();
		}
		return bounds;
	}*/

	/*@Override
	public FGEPoint getCenter() {
		return getBoundingBox().getCenter();
	}*/

	@Override
	public FGEComplexCurve transform(AffineTransform t) {
		Vector<FGEPoint> points = new Vector<FGEPoint>();
		for (FGEPoint p : _points) {
			points.add(p.transform(t));
		}
		FGEComplexCurve returned = new FGEComplexCurve(getClosure(), points);
		return returned;
	}

}
