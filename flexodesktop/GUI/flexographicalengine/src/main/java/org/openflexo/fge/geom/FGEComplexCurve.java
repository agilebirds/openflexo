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

import java.awt.geom.QuadCurve2D;
import java.util.List;
import java.util.Vector;

public class FGEComplexCurve extends FGEGeneralShape {

	private Vector<FGEPoint> _points;
	
	public FGEComplexCurve(Closure closure) 
	{
		super(closure);
		_points = new Vector<FGEPoint>();
	}

	public FGEComplexCurve() 
	{
		this(Closure.OPEN_NOT_FILLED);
	}

	public FGEComplexCurve(Closure closure,List<FGEPoint> points) 
	{
		this(closure);
		for (FGEPoint p : points) _addToPoints(p);
		 updateCurve();
	}

	public FGEComplexCurve(Closure closure,FGEPoint... points) 
	{
		this(closure);
		for (FGEPoint p : points) _addToPoints(p);
		 updateCurve();
	}

	private void _addToPoints(FGEPoint p)
	{
		_points.add(p);
	}
	
	public void addToPoints(FGEPoint p)
	{
		_points.add(p);
		 updateCurve();
	}
	
	private void updateCurve()
	{
		for (int i=0; i<_points.size(); i++) {
			FGEPoint current = _points.get(i);
			if (i==0) beginAtPoint(current);
			else if (i==1) {
				// First segment: quadratic curve
				FGEQuadCurve curve = FGEQuadCurve.makeCurveFromPoints(_points.get(0), _points.get(1), _points.get(2));
				QuadCurve2D left = new QuadCurve2D.Double();
				QuadCurve2D right = new QuadCurve2D.Double();
				curve.subdivide(left, right);
				addQuadCurve(left.getCtrlPt(), left.getP2());
			}
			else if (i==_points.size()-1) {
				// Last segment: quadratic curve
				FGEQuadCurve curve = FGEQuadCurve.makeCurveFromPoints(_points.get(i-2), _points.get(i-1), _points.get(i));
				QuadCurve2D left = new QuadCurve2D.Double();
				QuadCurve2D right = new QuadCurve2D.Double();
				curve.subdivide(left, right);
				addQuadCurve(right.getCtrlPt(), right.getP2());
			}
			else {
				// Cubic segment
				FGEQuadCurve leftCurve = FGEQuadCurve.makeCurveFromPoints(_points.get(i-2), _points.get(i-1), _points.get(i));
				FGEQuadCurve rightCurve = FGEQuadCurve.makeCurveFromPoints(_points.get(i-1), _points.get(i), _points.get(i+1));
				/*FGECubicCurve curve = new FGECubicCurve(
						_points.get(i-1),
						rightCurve.getPP1(),
						leftCurve.getPP2(),
						_points.get(i));*/
				addCubicCurve(leftCurve.getPP2(),
						rightCurve.getPP1(),
						_points.get(i));
			}
		}
	}
}
