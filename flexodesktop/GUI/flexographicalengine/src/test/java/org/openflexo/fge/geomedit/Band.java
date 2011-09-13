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

import org.openflexo.fge.geom.area.FGEBand;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.BandConstruction;
import org.openflexo.fge.geomedit.gr.BandGraphicalRepresentation;


public class Band extends GeometricObject<FGEBand> {

	private BandGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public Band(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public Band(GeometricSet set, BandConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new BandGraphicalRepresentation(this,set.getEditedDrawing());
	}
	
	@Override
	public BandGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(BandGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}
	
	@Override
	public BandConstruction getConstruction()
	{
		return (BandConstruction)super.getConstruction();
	}

	public void setConstruction(BandConstruction bandConstruction)
	{
		_setConstruction(bandConstruction);
	}

	@Override
	public String getInspectorName()
	{
		return "Band.inspector";
	}


}
