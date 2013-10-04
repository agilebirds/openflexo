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

import org.openflexo.fge.geom.FGESegment;

public class SegmentWithTwoPointsConstruction extends SegmentConstruction {

	public PointConstruction pointConstruction1;
	public PointConstruction pointConstruction2;

	public SegmentWithTwoPointsConstruction() {
		super();
	}

	public SegmentWithTwoPointsConstruction(PointConstruction pointConstruction1, PointConstruction pointConstruction2) {
		this();
		this.pointConstruction1 = pointConstruction1;
		this.pointConstruction2 = pointConstruction2;
	}

	@Override
	protected FGESegment computeData() {
		return new FGESegment(pointConstruction1.getPoint(), pointConstruction2.getPoint());
	}

	@Override
	public String toString() {
		return "SegmentWithTwoPointsConstruction[\n" + "> " + pointConstruction1.toString() + "\n> " + pointConstruction2.toString()
				+ "\n]";
	}

	@Override
	public GeometricConstruction[] getDepends() {
		GeometricConstruction[] returned = { pointConstruction1, pointConstruction2 };
		return returned;
	}

}
