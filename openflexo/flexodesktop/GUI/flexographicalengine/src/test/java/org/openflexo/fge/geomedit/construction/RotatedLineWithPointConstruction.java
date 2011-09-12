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

import org.openflexo.fge.geom.FGEAbstractLine;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;

public class RotatedLineWithPointConstruction extends LineConstruction {

	public PointConstruction pointConstruction;
	public LineConstruction lineConstruction;
	public double angle;
	
	public RotatedLineWithPointConstruction() 
	{
		super();
	}
	
	public RotatedLineWithPointConstruction(LineConstruction lineConstruction, PointConstruction pointConstruction, double anAngle) 
	{
		this();
		this.lineConstruction = lineConstruction;
		this.pointConstruction = pointConstruction;
		angle = anAngle;
	}
	
	@Override
	protected FGELine computeData()
	{
		FGELine computedLine = FGEAbstractLine.getRotatedLine(
				lineConstruction.getLine(),
				angle,
				pointConstruction.getPoint());
		
		FGEPoint p1,p2;
		p1 = pointConstruction.getPoint().clone();
		if (lineConstruction.getLine().contains(pointConstruction.getPoint())) {
			FGEEllips ellips = new FGEEllips(p1,new FGEDimension(200,200),Filling.NOT_FILLED);
			p2 = ellips.intersect(computedLine).getNearestPoint(p1);
		}
		else {
			p2 = computedLine.getLineIntersection(lineConstruction.getLine()).clone();
		}
		return new FGELine(p1,p2);

	}

	@Override
	public String toString()
	{
		return "RotatedLineWithPointConstruction["+angle+"\n"+"> "+lineConstruction.toString()+"\n> "+pointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { pointConstruction, lineConstruction };
		return returned;
	}


}
