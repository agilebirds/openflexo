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
package org.openflexo.fge.geomedit.gr;

import java.util.List;
import java.util.Vector;

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geomedit.GeometricDrawing;
import org.openflexo.fge.geomedit.GeometricObject;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;

public class ComputedAreaGraphicalRepresentation<G extends GeometricObject<FGEArea>> extends
		GeometricObjectGraphicalRepresentation<FGEArea, G> {

	// Called for LOAD
	public ComputedAreaGraphicalRepresentation(GeomEditBuilder builder) {
		this(null, builder.drawing);
		initializeDeserialization();
	}

	public ComputedAreaGraphicalRepresentation(G object, GeometricDrawing aDrawing) {
		super(object, aDrawing);
	}

	@Override
	public List<ControlPoint> rebuildControlPoints() {
		_controlPoints = new Vector<ControlPoint>();
		_controlPoints.clear();
		return _controlPoints;
	}

	public void recompute() {
		getDrawable().resetResultingGeometricObject();
	}
}
