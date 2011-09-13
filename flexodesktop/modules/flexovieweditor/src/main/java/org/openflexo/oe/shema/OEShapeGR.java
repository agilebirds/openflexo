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
package org.openflexo.oe.shema;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.ontology.shema.ConnectorInserted;
import org.openflexo.foundation.ontology.shema.ConnectorRemoved;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.ShapeInserted;
import org.openflexo.foundation.ontology.shema.ShapeRemoved;
import org.openflexo.foundation.xml.OEShemaBuilder;
import org.openflexo.toolbox.ConcatenedList;
import org.openflexo.toolbox.ToolBox;


public class OEShapeGR extends ShapeGraphicalRepresentation<OEShape> implements GraphicalFlexoObserver, OEShemaConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OEShapeGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * DO NOT use it
	 */
	public OEShapeGR(OEShemaBuilder builder)
	{
		this(null,null);
	}

	public OEShapeGR(OEShape aShape, Drawing<?> aDrawing) 
	{
		super(ShapeType.RECTANGLE, aShape, aDrawing);

		addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
			addToMouseClickControls(new OEShemaController.ShowContextualMenuControl(true));
		}
		addToMouseDragControls(new DrawEdgeControl());

		if (aShape != null) aShape.addObserver(this);

		setBorder(new ShapeGraphicalRepresentation.ShapeBorder(25,25,25,25));
		
	}

	@Override
	public void delete()
	{
		if (getDrawable() != null) getDrawable().deleteObserver(this);
		super.delete();
	}


	@Override
	public OEShemaRepresentation getDrawing() 
	{
		return (OEShemaRepresentation)super.getDrawing();
	}
	
	public OEShape getOEShape()
	{
		return getDrawable();
	}


	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getOEShape()) {
			//logger.info("Notified "+dataModification);
			if (dataModification instanceof ShapeInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof ShapeRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof ConnectorInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof ConnectorRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
			else if (dataModification instanceof NameChanged) {
				//logger.info("received NameChanged notification");
				setText(getText());
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
			}
		}
	}

	@Override
	public boolean getAllowToLeaveBounds()
	{
		return false;
	}
	
	@Override
	public String getText()
	{
		if (getOEShape() != null)
			return getOEShape().getName();
		return null;
	}

	@Override
	public void setTextNoNotification(String text) 
	{
		if (getOEShape() != null)
			getOEShape().setName(text);
	}

	private ConcatenedList<ControlArea> controlAreas;

	@Override
	public List<? extends ControlArea> getControlAreas() 
	{
		if (controlAreas==null) {
			controlAreas = new ConcatenedList<ControlArea>();
			controlAreas.addElementList(super.getControlAreas());
			if (getOEShape().getAvailableLinkSchemeFromThisShape() != null 
					&& getOEShape().getAvailableLinkSchemeFromThisShape().size() > 0) {
				controlAreas.addElement(new FloatingPalette(this, getDrawable().getShema(),SimplifiedCardinalDirection.EAST));
				controlAreas.addElement(new FloatingPalette(this, getDrawable().getShema(),SimplifiedCardinalDirection.WEST));
				controlAreas.addElement(new FloatingPalette(this, getDrawable().getShema(),SimplifiedCardinalDirection.NORTH));
				controlAreas.addElement(new FloatingPalette(this, getDrawable().getShema(),SimplifiedCardinalDirection.SOUTH));
			}
		}
		return controlAreas;
	}

}
