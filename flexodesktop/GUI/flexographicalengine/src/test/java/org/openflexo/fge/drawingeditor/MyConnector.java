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

import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;


public class MyConnector extends MyDrawingElement
{
	private MyConnectorGraphicalRepresentation gr;
	
	// Called for LOAD
	public MyConnector(DrawingBuilder builder)
	{
		super(builder.drawing.getModel());
		initializeDeserialization();
	}
	
	
	// Called for NEW
	public MyConnector(MyShape from, MyShape to, EditedDrawing drawing) 
	{
		super(drawing.getModel());
		gr = new MyConnectorGraphicalRepresentation(
				ConnectorType.LINE,
				(MyShapeGraphicalRepresentation)drawing.getGraphicalRepresentation(from),
				(MyShapeGraphicalRepresentation)drawing.getGraphicalRepresentation(to),
				this,drawing);
	}
	
	@Override
	public MyConnectorGraphicalRepresentation getGraphicalRepresentation()
	{
		return gr;
	}
	
	public void setGraphicalRepresentation(MyConnectorGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		gr = aGR;
	}

	public MyShape getStartShape()
	{
		return getGraphicalRepresentation().getStartObject().getDrawable();
	}

	public MyShape geEndShape()
	{
		return getGraphicalRepresentation().getEndObject().getDrawable();
	}
	
	public MyShapeGraphicalRepresentation getStartObject()
	{
		return getGraphicalRepresentation().getStartObject();
	}

	public MyShapeGraphicalRepresentation getEndObject()
	{
		return getGraphicalRepresentation().getEndObject();
	}



}