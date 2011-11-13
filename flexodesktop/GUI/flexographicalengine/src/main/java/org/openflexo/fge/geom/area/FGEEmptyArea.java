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

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.graphics.FGEGraphics;

public class FGEEmptyArea implements FGEArea {

	public FGEEmptyArea() {
		super();
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return false;
	}

	@Override
	public boolean containsArea(FGEArea a) {
		return false;
	}

	@Override
	public boolean containsLine(FGEAbstractLine l) {
		return false;
	}

	@Override
	public FGEArea exclusiveOr(FGEArea area) {
		return new FGEEmptyArea();
	}

	@Override
	public FGEArea intersect(FGEArea area) {
		return new FGEEmptyArea();
	}

	@Override
	public FGEArea substract(FGEArea area, boolean isStrict) {
		return new FGEEmptyArea();
	}

	@Override
	public FGEArea union(FGEArea area) {
		return area.clone();
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
	public FGEEmptyArea clone() {
		try {
			return (FGEEmptyArea) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		return "FGEEmptyArea";
	}

	@Override
	public FGEEmptyArea transform(AffineTransform t) {
		return clone();
	}

	@Override
	public void paint(FGEGraphics g) {
		// Easy !
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint aPoint) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FGEEmptyArea)
			return true;
		else
			return super.equals(obj);
	}

	@Override
	public FGEEmptyArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public FGEEmptyArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return this;
	}

	/**
	 * This area trivially finite, so return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is trivially finite, but always return null (!)
	 */
	@Override
	public final FGERectangle getEmbeddingBounds() {
		return null;
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
		return null;

	}

}
