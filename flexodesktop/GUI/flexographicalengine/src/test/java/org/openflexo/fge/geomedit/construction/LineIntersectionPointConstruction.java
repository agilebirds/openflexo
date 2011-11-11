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

import org.openflexo.fge.geom.FGEPoint;

public class LineIntersectionPointConstruction extends PointConstruction {

	public LineConstruction lineConstruction1;
	public LineConstruction lineConstruction2;

	public LineIntersectionPointConstruction() {
		super();
	}

	public LineIntersectionPointConstruction(LineConstruction lineConstruction1, LineConstruction lineConstruction2) {
		this();
		this.lineConstruction1 = lineConstruction1;
		this.lineConstruction2 = lineConstruction2;
	}

	@Override
	protected FGEPoint computeData() {
		return lineConstruction1.getData().getLineIntersection(lineConstruction2.getData());
	}

	@Override
	public String toString() {
		return "LineIntersectionPointConstruction[\n" + "> " + lineConstruction1.toString() + "\n> " + lineConstruction2.toString() + "\n]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { lineConstruction1, lineConstruction2 };
		return returned;
	}

}
