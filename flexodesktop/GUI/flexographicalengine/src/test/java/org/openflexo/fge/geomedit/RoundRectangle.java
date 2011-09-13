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

import org.openflexo.fge.geom.FGERoundRectangle;
import org.openflexo.fge.geomedit.GeometricSet.GeomEditBuilder;
import org.openflexo.fge.geomedit.construction.RoundRectangleConstruction;
import org.openflexo.fge.geomedit.gr.RoundRectangleGraphicalRepresentation;
import org.openflexo.fge.notifications.FGENotification;


public class RoundRectangle extends GeometricObject<FGERoundRectangle> {

	private RoundRectangleGraphicalRepresentation graphicalRepresentation;
	
	// Called for LOAD
	public RoundRectangle(GeomEditBuilder builder)
	{
		super(builder);
	}
	
	public RoundRectangle(GeometricSet set, RoundRectangleConstruction construction) 
	{
		super(set, construction);
		graphicalRepresentation = new RoundRectangleGraphicalRepresentation(this,set.getEditedDrawing());
	}

	@Override
	public RoundRectangleGraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}

	public void setGraphicalRepresentation(RoundRectangleGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		graphicalRepresentation = aGR;
	}

	@Override
	public RoundRectangleConstruction getConstruction()
	{
		return (RoundRectangleConstruction)super.getConstruction();
	}

	public void setConstruction(RoundRectangleConstruction rectangleConstruction)
	{
		_setConstruction(rectangleConstruction);
	}


	@Override
	public String getInspectorName()
	{
		return "RoundRectangle.inspector";
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

	public double getWidth()
	{
		return getGeometricObject().getWidth();
	}

	public void setWidth(double width)
	{
		if (width != getWidth()) { 
			double oldWidth = getWidth();
			getGeometricObject().width = width;
			getGraphicalRepresentation().notify(new FGENotification("width",oldWidth,width));
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

	public double getHeight()
	{
		return getGeometricObject().getHeight();
	}

	public void setHeight(double height)
	{
		if (height != getHeight()) { 
			double oldHeight = getHeight();
			getGeometricObject().height = height;
			getGraphicalRepresentation().notify(new FGENotification("height",oldHeight,height));
			getGraphicalRepresentation().notifyGeometryChanged();
		}
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

	public double getArcHeight()
	{
		return getConstruction().getArcHeight();
	}

	public void setArcHeight(double arcHeight)
	{
		if (arcHeight != getArcHeight()) {
			double oldArcHeight = getArcHeight();
			getConstruction().setArcHeight(arcHeight);
			getGraphicalRepresentation().notify(new FGENotification("arcHeight",oldArcHeight,arcHeight));
		}
	}

	public double getArcWidth()
	{
		return getConstruction().getArcWidth();
	}

	public void setArcWidth(double arcWidth)
	{
		if (arcWidth != getArcHeight()) {
			double oldArcWidth = getArcWidth();
			getConstruction().setArcWidth(arcWidth);
			getGraphicalRepresentation().notify(new FGENotification("arcWidth",oldArcWidth,arcWidth));
		}
	}
}
