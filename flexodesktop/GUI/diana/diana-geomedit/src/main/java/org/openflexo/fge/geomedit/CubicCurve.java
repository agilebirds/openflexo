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
package org.openflexo.fge.geomedit;

import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.CubicCurveConstruction;
import org.openflexo.fge.geomedit.gr.CubicCurveGraphicalRepresentation;

public class CubicCurve extends GeometricObject<FGECubicCurve> {

	private CubicCurveGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public CubicCurve(GeomEditBuilder builder) {
		super(builder);
	}

	public CubicCurve(GeometricSet set, CubicCurveConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new CubicCurveGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public String getInspectorName() {
		return "QuadCurve.inspector";
	}

	@Override
	public CubicCurveGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(CubicCurveGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public CubicCurveConstruction getConstruction() {
		return (CubicCurveConstruction) super.getConstruction();
	}

	public void setConstruction(CubicCurveConstruction pointConstruction) {
		_setConstruction(pointConstruction);
	}

}
