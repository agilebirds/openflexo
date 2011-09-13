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

import org.openflexo.fge.geom.FGECircle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;

public class CircleWithCenterAndPointConstruction extends CircleConstruction {

	public PointConstruction centerConstruction;
	public PointConstruction pointConstruction;

	public CircleWithCenterAndPointConstruction()
	{
		super();
	}

	public CircleWithCenterAndPointConstruction(PointConstruction pointConstruction1, PointConstruction pointConstruction2)
	{
		this();
		this.centerConstruction = pointConstruction1;
		this.pointConstruction = pointConstruction2;
	}

	@Override
	protected FGECircle computeData()
	{
		FGEPoint center = centerConstruction.getPoint();
		FGEPoint p = pointConstruction.getPoint();

		double radius = FGESegment.getLength(center,p);
		return new FGECircle(center,radius,(getIsFilled()?Filling.FILLED:Filling.NOT_FILLED));
	}

	@Override
	public String toString()
	{
		return "CircleWithCenterAndPointConstruction[\n"+"> "+centerConstruction.toString()+"\n> "+pointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { centerConstruction, pointConstruction };
		return returned;
	}


}
