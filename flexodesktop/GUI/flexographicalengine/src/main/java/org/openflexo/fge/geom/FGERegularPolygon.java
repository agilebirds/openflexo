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

public class FGERegularPolygon extends FGEPolygon {

	private int npoints;
	private double startAngle = 90; // in degree

	public double x, y;
	public double width, height;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public FGERegularPolygon(double aX, double aY, double aWidth, double aHeight, Filling filling, int pointsNb) {
		super(filling);
		this.x = aX;
		this.y = aY;
		this.width = aWidth;
		this.height = aHeight;
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build regular polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
		updatePoints();
	}

	public FGERegularPolygon(double aX, double aY, double aWidth, double aHeight, Filling filling, int pointsNb, double startAngle) {
		this(aX, aY, aWidth, aHeight, filling, pointsNb);
		setStartAngle(startAngle);
	}

	private void updatePoints() {
		clearPoints();
		for (int i = 0; i < npoints; i++) {
			double angle = i * 2 * Math.PI / npoints - startAngle * Math.PI / 180;
			addToPoints(new FGEPoint(Math.cos(angle) * width / 2 + x + width / 2, Math.sin(angle) * height / 2 + y + height / 2));
		}
	}

	public int getNPoints() {
		return npoints;
	}

	public void setNPoints(int pointsNb) {
		if (pointsNb < 3) {
			pointsNb = 3;
		}
		npoints = pointsNb;
		updatePoints();
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double anAngle) {
		startAngle = anAngle;
		updatePoints();
	}

}
