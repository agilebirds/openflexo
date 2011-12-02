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

import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;

public class ParallelLineWithPointConstruction extends LineConstruction {

	public LineConstruction lineConstruction;
	public PointConstruction pointConstruction;

	public ParallelLineWithPointConstruction() {
		super();
	}

	public ParallelLineWithPointConstruction(LineConstruction aLineConstruction, PointConstruction aPointConstruction) {
		this();
		this.lineConstruction = aLineConstruction;
		this.pointConstruction = aPointConstruction;
	}

	@Override
	protected FGELine computeData() {
		FGELine computedLine = lineConstruction.getLine().getParallelLine(pointConstruction.getPoint());
		FGEPoint pp1 = computedLine.getProjection(lineConstruction.getLine().getP1());
		FGEPoint pp2 = computedLine.getProjection(lineConstruction.getLine().getP2());
		computedLine.setP1(pp1);
		computedLine.setP2(pp2);
		return computedLine;
	}

	@Override
	public String toString() {
		return "ParallelLineWithPointConstruction[\n" + "> " + lineConstruction.toString() + "\n> " + pointConstruction.toString() + "\n]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { lineConstruction, pointConstruction };
		return returned;
	}

}
