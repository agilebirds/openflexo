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

import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.PolygonConstruction;
import org.openflexo.fge.geomedit.gr.PolygonGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;


public class Polygon extends GeometricObject<FGEPolygon> {

	private PolygonGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public Polygon(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public Polygon(GeometricSet set, PolygonConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new PolygonGraphicalRepresentation(this,set.getEditedDrawing());
	}

	@Override
	public PolygonGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(PolygonGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public PolygonConstruction getConstruction()
	{
		return (PolygonConstruction)super.getConstruction();
	}

	public void setConstruction(PolygonConstruction polygonConstruction)
	{
		_setConstruction(polygonConstruction);
	}


	@Override
	public String getInspectorName()
	{
		return "Polygon.inspector";
	}

	public boolean getIsFilled()
	{
		return getConstruction().getIsFilled();
	}

	public void setIsFilled(boolean filled)
	{
		if (filled != getIsFilled()) {
			getConstruction().setIsFilled(filled);
			getGraphicalRepresentation().notify(new FGENotification("isFilled",!filled,filled));
		}
	}
}
