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
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;

public class RectangleWithTwoPointsConstruction extends RectangleConstruction {

	public PointConstruction pointConstruction1;
	public PointConstruction pointConstruction2;

	public RectangleWithTwoPointsConstruction()
	{
		super();
	}

	public RectangleWithTwoPointsConstruction(PointConstruction pointConstruction1, PointConstruction pointConstruction2)
	{
		super();
		this.pointConstruction1 = pointConstruction1;
		this.pointConstruction2 = pointConstruction2;
	}

	@Override
	protected FGERectangle computeData()
	{
		FGEPoint p1 = pointConstruction1.getPoint();
		FGEPoint p2 = pointConstruction2.getPoint();

		FGEPoint p = new FGEPoint();
		p.x = Math.min(p1.x, p2.x);
		p.y = Math.min(p1.y, p2.y);

		double width = Math.abs(p1.x-p2.x);
		double height = Math.abs(p1.y-p2.y);

		return new FGERectangle(p.x,p.y,width,height,(getIsFilled()?Filling.FILLED:Filling.NOT_FILLED));
	}

	@Override
	public String toString()
	{
		return "RectangleWithTwoPointsConstruction[\n"+"> "+pointConstruction1.toString()+"\n> "+pointConstruction2.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { pointConstruction1, pointConstruction2 };
		return returned;
	}


}
