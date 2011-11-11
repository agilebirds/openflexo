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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;

public abstract class FGEOperationArea implements FGEArea {

	@Override
	public abstract boolean containsPoint(FGEPoint p);

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

		return new FGEUnionArea(this, area);
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
	public FGEOperationArea clone() {
		try {
			return (FGEOperationArea) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		return this;
	}

	@Override
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return this;
	}

}
