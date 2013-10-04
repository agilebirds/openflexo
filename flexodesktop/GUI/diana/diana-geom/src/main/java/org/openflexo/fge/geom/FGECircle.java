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

import java.util.logging.Logger;

import org.openflexo.fge.geom.area.FGEUnionArea;

@SuppressWarnings("serial")
public class FGECircle extends FGEEllips {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FGECircle.class.getPackage().getName());

	public FGECircle() {
		this(Filling.NOT_FILLED);
	}

	public FGECircle(Filling filling) {
		super(filling);
	}

	public FGECircle(FGEPoint center, double radius, Filling filling) {
		super(center, new FGEDimension(radius * 2, radius * 2), filling);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGECircle) {
			FGECircle p = (FGECircle) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getRadius() - p.getRadius()) <= EPSILON && getCenter().equals(p.getCenter());
		}
		return super.equals(obj);
	}

	/**
	 * Return union of two points defining tangent to supplied circle and crossing supplied point
	 * 
	 * @param cicle
	 * @param p
	 * @return
	 */
	public static FGEUnionArea getTangentsPointsToCircle(FGECircle circle, FGEPoint p) throws PointInsideCircleException {
		FGELine l = new FGELine(p, circle.getCenter());
		double asin = circle.getWidth() / 2 / FGESegment.getLength(p, circle.getCenter());
		if (asin >= 1 || asin <= -1) {
			throw new PointInsideCircleException();
		}
		double angle = Math.toDegrees(Math.asin(asin));
		FGELine tan1 = l.getRotatedLine(angle, p);
		FGELine tan2 = l.getRotatedLine(-angle, p);
		FGEPoint p1 = tan1.getProjection(circle.getCenter());
		FGEPoint p2 = tan2.getProjection(circle.getCenter());
		return new FGEUnionArea(p1, p2);
	}

	public double getRadius() {
		return getWidth() / 2;
	}

	public void setRadius(double radius) {
		FGEPoint center = getCenter();
		x = center.x - radius;
		y = center.y - radius;
		width = radius * 2;
		height = radius * 2;
	}

	@Override
	public String toString() {
		return "FGECircle: (" + x + "," + y + "," + width + "," + height + " type=" + getFGEArcType() + ")";
	}

}
