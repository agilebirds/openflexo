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

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.PointConstruction;
import org.openflexo.fge.geomedit.gr.PointGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;


public class Point extends GeometricObject<FGEPoint> {

	private PointGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public Point(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public Point(GeometricSet set, PointConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new PointGraphicalRepresentation(this,set.getEditedDrawing());
	}

	@Override
	public String getInspectorName()
	{
		return "Point.inspector";
	}

	@Override
	public PointGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(PointGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public PointConstruction getConstruction()
	{
		return (PointConstruction)super.getConstruction();
	}

	public void setConstruction(PointConstruction pointConstruction)
	{
		_setConstruction(pointConstruction);
	}



	public double getX()
	{
		return getGeometricObject().getX();
	}

	public void setX(double x)
	{
		if (x != getX()) { 
			double oldX = getX();
			getGeometricObject().x = x;
			getGraphicalRepresentation().notify(new FGENotification("x",oldX,x));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

	public double getY()
	{
		return getGeometricObject().getY();
	}

	public void setY(double y)
	{
		if (y != getY()) { 
			double oldY = getY();
			getGeometricObject().y = y;
			getGraphicalRepresentation().notify(new FGENotification("y",oldY,y));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
	}

}
