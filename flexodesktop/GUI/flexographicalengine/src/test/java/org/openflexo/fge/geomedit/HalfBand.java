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

import org.openflexo.fge.geom.area.FGEHalfBand;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.HalfBandConstruction;
import org.openflexo.fge.geomedit.gr.HalfBandGraphicalRepresentation;


public class HalfBand extends GeometricObject<FGEHalfBand> {

	private HalfBandGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public HalfBand(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public HalfBand(GeometricSet set, HalfBandConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new HalfBandGraphicalRepresentation(this,set.getEditedDrawing());
	}
	
	@Override
	public HalfBandGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(HalfBandGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}
	
	@Override
	public HalfBandConstruction getConstruction()
	{
		return (HalfBandConstruction)super.getConstruction();
	}

	public void setConstruction(HalfBandConstruction hbConstruction)
	{
		_setConstruction(hbConstruction);
	}

	@Override
	public String getInspectorName()
	{
		return "HalfBand.inspector";
	}

}
