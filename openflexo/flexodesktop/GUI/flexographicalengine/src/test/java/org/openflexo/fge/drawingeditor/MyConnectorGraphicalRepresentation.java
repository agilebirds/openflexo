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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.drawingeditor.MyDrawing.DrawingBuilder;
import org.openflexo.fge.view.ConnectorView;
import org.openflexo.xmlcode.XMLSerializable;


public class MyConnectorGraphicalRepresentation extends ConnectorGraphicalRepresentation<MyConnector> implements XMLSerializable 
{

	// Called for LOAD
	public MyConnectorGraphicalRepresentation(DrawingBuilder builder)
	{
		this(ConnectorType.LINE,null,null,null,builder.drawing);
		initializeDeserialization();
	}
	
	public MyConnectorGraphicalRepresentation(ConnectorType aConnectorType, 
			MyShapeGraphicalRepresentation aStartObject, 
			MyShapeGraphicalRepresentation anEndObject,
			MyConnector aDrawable, 
			EditedDrawing aDrawing)
	{
		super(aConnectorType, aStartObject, anEndObject, aDrawable, aDrawing);
		addToMouseClickControls(new ShowContextualMenuControl());
	}
	
	@Override
	public MyShapeGraphicalRepresentation getStartObject()
	{
		return (MyShapeGraphicalRepresentation)super.getStartObject();
	}

	@Override
	public MyShapeGraphicalRepresentation getEndObject()
	{
		return (MyShapeGraphicalRepresentation)super.getEndObject();
	}

	
	@Override
	public MyConnectorView makeConnectorView(DrawingController<?> controller)
	{
		return new MyConnectorView(this,controller);
	}
	
	public class MyConnectorView extends ConnectorView<MyConnector>
	{
		public MyConnectorView(MyConnectorGraphicalRepresentation aGraphicalRepresentation,DrawingController<?> controller)
		{
			super(aGraphicalRepresentation,controller);
		}
		
		@Override
		public MyDrawingView getDrawingView()
		{
			return (MyDrawingView)super.getDrawingView();
		}

	}

}