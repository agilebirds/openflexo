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
import org.openflexo.fge.geom.FGERectangle;

public class FGEFiniteGrid extends FGEGrid {

	public FGERectangle bounds;

	public FGEFiniteGrid(FGEPoint origin, double hStep, double vStep, FGERectangle bounds) {
		super(origin, hStep, vStep);
		this.bounds = bounds;
	}

	@Override
	public boolean isFinite() {
		return true;
	}

	@Override
	public FGERectangle getEmbeddingBounds() {
		return bounds;
	}

	@Override
	public FGEFiniteGrid clone() {
		return new FGEFiniteGrid(origin.clone(), hStep, vStep, bounds.clone());
	}

	@Override
	public boolean containsPoint(FGEPoint p) {
		return super.containsPoint(p) && bounds.containsPoint(p);
	}

	@Override
	public boolean containsArea(FGEArea a) {
		return super.containsArea(a) && bounds.containsArea(a);
	}

	@Override
	public FGEPoint getNearestPoint(FGEPoint point) {
		return super.getNearestPoint(bounds.getNearestPoint(point));
	}

}
