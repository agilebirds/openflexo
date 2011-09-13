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

import org.openflexo.fge.geom.FGECubicCurve;

public class CubicCurveWithFourPointsConstruction extends CubicCurveConstruction {

	public PointConstruction startPointConstruction;
	public PointConstruction controlPointConstruction1;
	public PointConstruction controlPointConstruction2;
	public PointConstruction endPointConstruction;
	
	public CubicCurveWithFourPointsConstruction() 
	{
		super();
	}
	
	public CubicCurveWithFourPointsConstruction(PointConstruction pointConstruction1, PointConstruction pointConstruction2, PointConstruction pointConstruction3, PointConstruction pointConstruction4) 
	{
		this();
		this.startPointConstruction = pointConstruction1;
		this.controlPointConstruction1 = pointConstruction2;
		this.controlPointConstruction2 = pointConstruction3;
		this.endPointConstruction = pointConstruction4;
	}
	
	@Override
	protected FGECubicCurve computeData()
	{
		return new FGECubicCurve(startPointConstruction.getPoint(),controlPointConstruction1.getPoint(),controlPointConstruction2.getPoint(),endPointConstruction.getPoint());
	}

	@Override
	public String toString()
	{
		return "CubicCurveWithFourPointsConstruction[\n"+"> "+startPointConstruction.toString()+"\n> "+controlPointConstruction1.toString()+"\n> "+controlPointConstruction2.toString()+"\n> "+endPointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { startPointConstruction, controlPointConstruction1, controlPointConstruction2, endPointConstruction };
		return returned;
	}


}
