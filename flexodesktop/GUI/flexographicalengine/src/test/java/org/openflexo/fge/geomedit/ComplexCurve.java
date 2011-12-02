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

import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.ComplexCurveConstruction;
import org.openflexo.fge.geomedit.gr.ComplexCurveGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;

public class ComplexCurve extends GeometricObject<FGEComplexCurve> {

	private ComplexCurveGraphicalRepresentation graphicalRepresentation;

	// Called for LOAD
	public ComplexCurve(GeomEditBuilder builder) {
		super(builder);
	}

	public ComplexCurve(GeometricSet set, ComplexCurveConstruction construction) {
		super(set, construction);
		graphicalRepresentation = new ComplexCurveGraphicalRepresentation(this, set.getEditedDrawing());
	}

	@Override
	public ComplexCurveGraphicalRepresentation getGraphicalRepresentation() {
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(ComplexCurveGraphicalRepresentation aGR) {
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public ComplexCurveConstruction getConstruction() {
		return (ComplexCurveConstruction) super.getConstruction();
	}

	public void setConstruction(ComplexCurveConstruction polygonConstruction) {
		_setConstruction(polygonConstruction);
	}

	@Override
	public String getInspectorName() {
		return "ComplexCurve.inspector";
	}

	public Closure getClosure() {
		return getConstruction().getClosure();
	}

	public void setClosure(Closure aClosure) {
		if (aClosure != getClosure()) {
			Closure oldClosure = getClosure();
			getConstruction().setClosure(aClosure);
			getGraphicalRepresentation().notify(new FGENotification("closure", oldClosure, aClosure));
		}
	}

}
