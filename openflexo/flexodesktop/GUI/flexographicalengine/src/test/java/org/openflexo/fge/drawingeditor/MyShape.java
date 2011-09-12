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
package org.openflexo.fge.drawingeditor;

import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;


public class MyShape extends MyDrawingElement
{
	public String name;
	private MyShapeGraphicalRepresentation gr;
	
	// Called for LOAD
	public MyShape(DrawingBuilder builder)
	{
		super(builder.drawing.getModel());
		initializeDeserialization();
	}
	
	
	// Called for NEW
	public MyShape(ShapeType shape, FGEPoint p, EditedDrawing drawing) 
	{
		super(drawing.getModel());
		gr = new MyShapeGraphicalRepresentation(shape,this,drawing);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setWidth(80);
			gr.setHeight(80);
		}
		else {
			gr.setWidth(100);
			gr.setHeight(80);
		}
		gr.setX(p.x);
		gr.setY(p.y);
	}
	
	public MyShape(ShapeGraphicalRepresentation<?> aGR, FGEPoint p, EditedDrawing drawing) 
	{
		super(drawing.getModel());
		gr = new MyShapeGraphicalRepresentation(aGR,this,drawing);
		/*if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setWidth(80);
			gr.setHeight(80);
		}
		else {
			gr.setWidth(100);
			gr.setHeight(80);
		}*/
		gr.setX(p.x);
		gr.setY(p.y);
		
	}
	
	@Override
	public MyShapeGraphicalRepresentation getGraphicalRepresentation()
	{
		return gr;
	}
	
	public void setGraphicalRepresentation(MyShapeGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		gr = aGR;
	}

	@Override
	public String toString()
	{
		return "MyShape["+name+":"+gr.toString()+"]";
	}

}