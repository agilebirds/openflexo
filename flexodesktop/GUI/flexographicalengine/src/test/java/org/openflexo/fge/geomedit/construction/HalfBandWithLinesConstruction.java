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

import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geom.area.FGEHalfPlane;

public class HalfBandWithLinesConstruction extends HalfBandConstruction {

	public LineConstruction lineConstruction1;
	public LineConstruction lineConstruction2;
	public LineConstruction limitLineConstruction;
	public PointConstruction pointConstruction;
	
	public HalfBandWithLinesConstruction() 
	{
		super();
	}
	
	public HalfBandWithLinesConstruction(LineConstruction aLineConstruction1, LineConstruction aLineConstruction2, LineConstruction limitLineConstruction, PointConstruction aPointConstruction) 
	{
		this();
		this.lineConstruction1 = aLineConstruction1;
		this.lineConstruction2 = aLineConstruction2;
		this.limitLineConstruction = limitLineConstruction;
		this.pointConstruction = aPointConstruction;
	}
	
	@Override
	protected FGEHalfBand computeData()
	{
		FGEHalfBand returned = new FGEHalfBand(
				lineConstruction1.getLine(),
				lineConstruction2.getLine(),
				new FGEHalfPlane(limitLineConstruction.getLine(),pointConstruction.getPoint()));
		return returned;
	}

	@Override
	public String toString()
	{
		return "HalfBandWithLinesConstruction[\n"+"> "
		+lineConstruction1.toString()+"\n> "+"> "
		+lineConstruction2.toString()+"\n> "+"> "
		+limitLineConstruction.toString()+"\n> "
		+pointConstruction.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { lineConstruction1, lineConstruction2, limitLineConstruction,  pointConstruction };
		return returned;
	}


}
