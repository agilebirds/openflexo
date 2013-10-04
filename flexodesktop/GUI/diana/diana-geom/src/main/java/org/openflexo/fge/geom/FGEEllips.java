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

@SuppressWarnings("serial")
public class FGEEllips extends FGEArc {

	private static final Logger logger = Logger.getLogger(FGEEllips.class.getPackage().getName());

	private Filling _filling;

	public FGEEllips() {
		this(Filling.NOT_FILLED);
	}

	public FGEEllips(Filling filling) {
		super(filling == Filling.FILLED ? ArcType.PIE : ArcType.OPEN);
		start = 0;
		extent = 360;
		_filling = filling;
	}

	public FGEEllips(double x, double y, double w, double h, Filling filling) {
		super(x, y, w, h, 0, 360, filling == Filling.FILLED ? ArcType.PIE : ArcType.OPEN);
		_filling = filling;
	}

	public FGEEllips(FGEPoint center, FGEDimension size, Filling filling) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, filling);
	}

	@Override
	public boolean getIsFilled() {
		return _filling == Filling.FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		_filling = filled ? Filling.FILLED : Filling.NOT_FILLED;
		super.setIsFilled(filled);
	}

	@Override
	public boolean includeAngle(double radianAngle) {
		return true;
	}

	@Override
	public List<FGEPoint> getControlPoints() {
		Vector<FGEPoint> returned = new Vector<FGEPoint>();
		returned.add(new FGEPoint(x + width / 2.0, y));
		returned.add(new FGEPoint(x, y + height / 2.0));
		returned.add(new FGEPoint(x + width / 2.0, y + height));
		returned.add(new FGEPoint(x + width, y + height / 2.0));

		return returned;
	}

	/*public FGEPoint nearestOutlinePoint(FGEPoint aPoint) 
	{
		double atan2 = Math.PI/2-Math.atan2(aPoint.x-getCenterX(),aPoint.y-getCenterY());
		return new FGEPoint(Math.cos(atan2)*getWidth()/2+getCenterX(),Math.sin(atan2)*getHeight()/2+getCenterY());
	}*/

	@Override
	public FGEEllips transform(AffineTransform t) {
		// TODO: this implementation is not correct if AffineTransform contains rotation and if
		// width not equals to height

		FGEPoint newCenter = new FGEPoint(getX() + getWidth() / 2, getY() + getHeight() / 2).transform(t);

		FGERectangle bounds = new FGERectangle(getX(), getY(), getWidth(), getHeight());
		FGEArea t_bounds = bounds.transform(t);

		if (t_bounds instanceof FGEShape) {
			FGERectangle boundingBox = ((FGEShape<?>) t_bounds).getBoundingBox();
			return new FGEEllips(newCenter, new FGEDimension(boundingBox.getWidth(), boundingBox.getHeight()), _filling);
		}
		logger.warning("Cannot compute transform for " + this + " with " + t);
		return (FGEEllips) clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEEllips) {
			FGEEllips p = (FGEEllips) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON;
		}
		return super.equals(obj);
	}

	@Override
	public FGERectangle getBoundingBox() {
		return new FGERectangle(x, y, width, height, Filling.FILLED);
	}

	@Override
	public String toString() {
		return "FGEEllips: (" + x + "," + y + "," + width + "," + height + " type=" + getFGEArcType() + ")";
	}

}
