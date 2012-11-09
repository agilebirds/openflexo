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
package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.shapes.Oval;

public class OvalImpl extends ShapeImpl implements Oval {

	private FGEEllips ellips;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public OvalImpl() {
		super();
	}

	@Deprecated
	private OvalImpl(ShapeGraphicalRepresentation<?> aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		updateShape();
	}

	@Override
	public void updateShape() {
		ellips = new FGEEllips(0, 0, 1, 1, Filling.FILLED);
		rebuildControlPoints();
		if (getGraphicalRepresentation() != null) {
			getGraphicalRepresentation().notifyShapeChanged();
		}
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.OVAL;
	}

	@Override
	public FGEEllips getShape() {
		return ellips;
	}
}
