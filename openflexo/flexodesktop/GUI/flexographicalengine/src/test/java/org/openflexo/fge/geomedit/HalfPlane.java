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
package org.openflexo.fge.geomedit;

import org.openflexo.fge.geom.area.FGEHalfPlane;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.HalfPlaneConstruction;
import org.openflexo.fge.geomedit.gr.HalfPlaneGraphicalRepresentation;


public class HalfPlane extends GeometricObject<FGEHalfPlane> {

	private HalfPlaneGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public HalfPlane(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public HalfPlane(GeometricSet set, HalfPlaneConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new HalfPlaneGraphicalRepresentation(this,set.getEditedDrawing());
	}
	
	@Override
	public HalfPlaneGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(HalfPlaneGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}
	
	@Override
	public HalfPlaneConstruction getConstruction()
	{
		return (HalfPlaneConstruction)super.getConstruction();
	}

	public void setConstruction(HalfPlaneConstruction lineConstruction)
	{
		_setConstruction(lineConstruction);
	}

	@Override
	public String getInspectorName()
	{
		return "HalfPlane.inspector";
	}



}
