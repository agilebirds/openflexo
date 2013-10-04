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
package org.openflexo.fge.geom.area;

import java.awt.geom.AffineTransform;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEGeometricObject;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.AbstractFGEGraphics;

public class FGEGrid implements FGEArea {

	private static final Logger logger = Logger.getLogger(FGEGrid.class.getPackage().getName());

	public FGEPoint origin;
	public double hStep;
	public double vStep;

	public FGEGrid() {
		this(new FGEPoint(0, 0), 1.0, 1.0);
	}

	public FGEGrid(FGEPoint origin, double hStep, double vStep) {
		this.origin = origin;
		this.hStep = hStep;
		this.vStep = vStep;
	}

	public double getHorizontalStep() {
		return hStep;
	}

	public double getVerticalStep() {
		return vStep;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		if (a instanceof FGEPoint) {
			return containsPoint((FGEPoint) a);
		}
		if (a instanceof FGEGrid) {
			FGEGrid grid = (FGEGrid) a;
			return grid.origin.equals(origin) && grid.hStep == hStep && grid.vStep == vStep;
		}
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		return false;
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return Math.abs(Math.IEEEremainder(p.x - origin.x, hStep)) < FGEGeometricObject.EPSILON
				&& Math.abs(Math.IEEEremainder(p.y - origin.y, vStep)) < FGEGeometricObject.EPSILON;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEExclusiveOrArea(this, area);
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	@Override
	public FGERectangle getEmbeddingBounds() {
		// Infinite--> null
		return null;
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint point) {
		FGEPoint translatedPoint = new FGEPoint(point.x - origin.x, point.y - origin.y);
		FGEPoint remainderPoint = new FGEPoint(Math.IEEEremainder(translatedPoint.x, hStep), Math.IEEEremainder(translatedPoint.y, vStep));
		FGEPoint ulPoint = new FGEPoint(point.x - remainderPoint.x, point.y - remainderPoint.y);

		double distanceUL = point.distance(ulPoint);
		double distanceUR = point.distance(ulPoint.x + hStep, ulPoint.y);
		double distanceLL = point.distance(ulPoint.x, ulPoint.y + vStep);
		double distanceLR = point.distance(ulPoint.x + hStep, ulPoint.y + vStep);
		if (distanceUL <= distanceUR && distanceUL <= distanceLL && distanceUL <= distanceLR) {
			// Upper left is closest
			return ulPoint;
		} else if (distanceUR <= distanceUL && distanceUR <= distanceLL && distanceUR <= distanceLR) {
			// Upper right is closest
			ulPoint.x += hStep;
			return ulPoint;
		} else if (distanceLL <= distanceUR && distanceLL <= distanceUL && distanceLL <= distanceLR) {
			// Lower left is closest
			ulPoint.y += vStep;
			return ulPoint;
		} else {
			// Lower right is closest
			ulPoint.x += hStep;
			ulPoint.y += vStep;
			return ulPoint;
		}
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		return null;
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation) {
		return getNearestPoint(from).nearestPointFrom(from, orientation);
	}

	@Override
	public void paint(AbstractFGEGraphics g) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("TODO");
		}
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGESubstractionArea(this, area, isStrict);
	}

	@Override
	public FGEArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public FGEArea union(FGEArea area) {
		return new FGEUnionArea(this, area);
	}

	@Override
	public FGEGrid clone() {
		return new FGEGrid(new FGEPoint(origin), hStep, vStep);
	}

}
