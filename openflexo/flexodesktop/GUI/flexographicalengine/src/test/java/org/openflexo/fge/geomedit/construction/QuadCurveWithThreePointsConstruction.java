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

import org.openflexo.fge.geom.FGEQuadCurve;

public class QuadCurveWithThreePointsConstruction extends QuadCurveConstruction {

	public PointConstruction startPointConstruction;
	public PointConstruction controlPointConstruction;
	public PointConstruction endPointConstruction;
	
	public QuadCurveWithThreePointsConstruction() 
	{
		super();
	}
	
	public QuadCurveWithThreePointsConstruction(PointConstruction pointConstruction1, PointConstruction pointConstruction2, PointConstruction pointConstruction3) 
	{
		this();
		this.startPointConstruction = pointConstruction1;
		this.controlPointConstruction = pointConstruction2;
		this.endPointConstruction = pointConstruction3;
	}
	
	@Override
	protected FGEQuadCurve computeData()
	{
		return new FGEQuadCurve(startPointConstruction.getPoint(),controlPointConstruction.getPoint(),endPointConstruction.getPoint());
	}

	@Override
	public String toString()
	{
		return "QuadCurveWithThreePointsConstruction[\n"+"> "+startPointConstruction.toString()+"\n> "+controlPointConstruction.toString()+"\n> "+endPointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { startPointConstruction, controlPointConstruction, endPointConstruction };
		return returned;
	}


}
