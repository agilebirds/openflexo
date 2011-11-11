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
package org.openflexo.fge.geomedit.construction;

import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.ComputedControlPoint;
import org.openflexo.fge.geomedit.gr.GeometricObjectGraphicalRepresentation;

public class ControlPointReference extends PointConstruction {

	public GeometricObjectGraphicalRepresentation<?, ?> reference;
	public String cpName;

	public ControlPointReference() {
		super();
	}

	public ControlPointReference(GeometricObjectGraphicalRepresentation<?, ?> aReference, String controlPointName) {
		this();
		this.reference = aReference;
		this.cpName = controlPointName;
	}

	protected ComputedControlPoint getControlPoint() {
		for (ControlPoint cp : reference.getControlPoints()) {
			if (cp instanceof ComputedControlPoint && ((ComputedControlPoint) cp).getName().equals(cpName)) {
				return (ComputedControlPoint) cp;
			}
		}
		return null;
	}

	@Override
	protected FGEPoint computeData() {
		if (getControlPoint() != null)
			return getControlPoint().getPoint();
		System.out.println("computeData() for ControlPointReference: cannot find cp " + cpName + " for " + reference);
		setModified();
		return new FGEPoint(0, 0);
	}

	@Override
	public String toString() {
		return "ControlPointReference[" + cpName + "," + reference.getDrawable().getConstruction() + "]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { reference.getDrawable().getConstruction() };
		return returned;
	}

}
