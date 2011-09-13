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

import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.area.FGEArea;

public class RectPolylinWithStartAndEndAreaConstruction extends PolylinConstruction {

	public ObjectReference<? extends FGEArea> startAreaConstruction;
	public ObjectReference<? extends FGEArea> endAreaConstruction;
	public SimplifiedCardinalDirection startOrientation;
	public SimplifiedCardinalDirection endOrientation;
	
	public RectPolylinWithStartAndEndAreaConstruction() 
	{
		super();
	}
	
	public RectPolylinWithStartAndEndAreaConstruction(
			ObjectReference<? extends FGEArea> aStartAreaConstruction, 
			SimplifiedCardinalDirection aStartOrientation,
			ObjectReference<? extends FGEArea> anEndAreaConstruction, 
			SimplifiedCardinalDirection aEndOrientation)
	{
		this();
		this.startAreaConstruction = aStartAreaConstruction;
		this.startOrientation = aStartOrientation;
		this.endAreaConstruction = anEndAreaConstruction;
		this.endOrientation = aEndOrientation;
	}
	
	@Override
	protected FGEPolylin computeData()
	{
		FGEArea startArea = startAreaConstruction.getData();
		FGEArea endArea = endAreaConstruction.getData();
		if (startOrientation == null) startOrientation = SimplifiedCardinalDirection.NORTH;
		if (endOrientation == null) endOrientation = SimplifiedCardinalDirection.SOUTH;
		return new FGERectPolylin(startArea,startOrientation,endArea,endOrientation,false,10,10);
	}

	@Override
	public String toString()
	{
		return "RectPolylinWithStartAndEndAreaConstruction[\n"+"> "
		+startAreaConstruction.toString()+"-"+startOrientation+"\n> "+"> "
		+endAreaConstruction.toString()+"-"+endOrientation+"\n]";
	}

	@Override
	public GeometricConstruction[] getDepends()
	{
		GeometricConstruction[] returned = { startAreaConstruction, endAreaConstruction };
		return returned;
	}


}
