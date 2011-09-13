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

import org.openflexo.fge.geom.area.FGEBand;

public class BandWithTwoLinesConstruction extends BandConstruction {

	public LineConstruction lineConstruction1;
	public LineConstruction lineConstruction2;
	
	public BandWithTwoLinesConstruction() 
	{
		super();
	}
	
	public BandWithTwoLinesConstruction(LineConstruction aLineConstruction1, LineConstruction aLineConstruction2) 
	{
		this();
		this.lineConstruction1 = aLineConstruction1;
		this.lineConstruction2 = aLineConstruction2;
	}
	
	@Override
	protected FGEBand computeData()
	{
		FGEBand returned = new FGEBand(lineConstruction1.getLine(),lineConstruction2.getLine());
		return returned;
	}

	@Override
	public String toString()
	{
		return "BandWithTwoLinesConstruction[\n"+"> "+lineConstruction1.toString()+"\n> "+lineConstruction2.toString()+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { lineConstruction1, lineConstruction2 };
		return returned;
	}


}
