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

import org.openflexo.fge.geom.area.FGEQuarterPlane;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.QuarterPlaneConstruction;
import org.openflexo.fge.geomedit.gr.QuarterPlaneGraphicalRepresentation;


public class QuarterPlane extends GeometricObject<FGEQuarterPlane> {

	private QuarterPlaneGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public QuarterPlane(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public QuarterPlane(GeometricSet set, QuarterPlaneConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new QuarterPlaneGraphicalRepresentation(this,set.getEditedDrawing());
	}
	
	@Override
	public QuarterPlaneGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(QuarterPlaneGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}
	
	@Override
	public QuarterPlaneConstruction getConstruction()
	{
		return (QuarterPlaneConstruction)super.getConstruction();
	}

	public void setConstruction(QuarterPlaneConstruction qpConstruction)
	{
		_setConstruction(qpConstruction);
	}

	@Override
	public String getInspectorName()
	{
		return "QuarterPlane.inspector";
	}



}
