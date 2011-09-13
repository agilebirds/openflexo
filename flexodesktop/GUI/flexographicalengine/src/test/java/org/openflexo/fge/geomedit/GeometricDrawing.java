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



import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.geomedit.gr.GeometricDrawingGraphicalRepresentation;


public class GeometricDrawing extends DefaultDrawing<GeometricSet>
{
	private GeometricDrawingGraphicalRepresentation gr;
	private GeomEditController controller; 

	public GeometricDrawing(GeometricSet drawing)
	{
		super(drawing);
		gr = new GeometricDrawingGraphicalRepresentation(this);
	}

	@Override
	public GeometricDrawingGraphicalRepresentation getDrawingGraphicalRepresentation()
	{
		return gr;
	}
	
	public void setDrawingGraphicalRepresentation(GeometricDrawingGraphicalRepresentation aGR)
	{
		gr = aGR;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		if (aDrawable == getModel()) return (GraphicalRepresentation<O>)getDrawingGraphicalRepresentation();
		if (aDrawable instanceof GeometricObject) return ((GeometricObject)aDrawable).getGraphicalRepresentation();
		(new Exception("???")).printStackTrace();
		return null;
	}
	
	public void init()
	{
		controller = new GeomEditController(this);
	}

	public GeomEditController getController()
	{
		return controller;
	}
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
	}
}