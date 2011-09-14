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
package org.openflexo.ced.drawingshema;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.calc.CalcDrawingConnector;
import org.openflexo.foundation.ontology.calc.CalcDrawingObject;
import org.openflexo.foundation.ontology.calc.CalcDrawingShape;
import org.openflexo.foundation.ontology.calc.CalcDrawingShema;


public class CalcDrawingShemaRepresentation extends DefaultDrawing<CalcDrawingShema> implements GraphicalFlexoObserver, CalcDrawingShemaConstants {

	private static final Logger logger = Logger.getLogger(CalcDrawingShemaRepresentation.class.getPackage().getName());
	
	private CalcDrawingShemaGR graphicalRepresentation;
	
	private Boolean ignoreNotifications = true;
	
	private Hashtable<CalcDrawingShape,CalcDrawingShapeGR> shapesGR;
	private Hashtable<CalcDrawingConnector,CalcDrawingConnectorGR> connectorsGR;

	private boolean readOnly = false;
	
	public CalcDrawingShemaRepresentation(CalcDrawingShema aShema, boolean readOnly)
	{
		super(aShema);
		
		this.readOnly = readOnly;
		//graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		//graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());
		
		shapesGR = new Hashtable<CalcDrawingShape, CalcDrawingShapeGR>();
		connectorsGR = new Hashtable<CalcDrawingConnector, CalcDrawingConnectorGR>();
		
		aShema.addObserver(this);
		
		updateGraphicalObjectsHierarchy();
		
		ignoreNotifications = false;

	}
	
	@Override
	public void delete()
	{
		if (graphicalRepresentation != null) graphicalRepresentation.delete();
		if (getShema() != null) getShema().deleteObserver(this);
	}
	
	
	@Override
	protected void beginUpdateObjectHierarchy()
	{
		ignoreNotifications = true;
		super.beginUpdateObjectHierarchy();
	}
	
	@Override
	protected void endUpdateObjectHierarchy()
	{
		super.endUpdateObjectHierarchy();
		ignoreNotifications = false;
	}
	
	protected boolean ignoreNotifications()
	{
		if (ignoreNotifications == null) return true;
		return ignoreNotifications;
	}
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
 		buildGraphicalObjectsHierarchyFor(getShema());
	}
	
	private void buildGraphicalObjectsHierarchyFor(CalcDrawingObject parent)
	{
		//logger.info("buildGraphicalObjectsHierarchyFor "+parent);
		
		for (CalcDrawingObject child : parent.getChilds()) {
			if (!(child instanceof CalcDrawingConnector)) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
		for (CalcDrawingObject child : parent.getChilds()) {
			if (child instanceof CalcDrawingConnector) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
	}
	
	public CalcDrawingShema getShema()
	{
		return getModel();
	}

	@Override
	public CalcDrawingShemaGR getDrawingGraphicalRepresentation()
	{
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new CalcDrawingShemaGR(this);
		}
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		if (aDrawable instanceof CalcDrawingShape) {
			CalcDrawingShape shape = (CalcDrawingShape)aDrawable;
			CalcDrawingShapeGR returned = shapesGR.get(shape);
			if (returned == null) {
				returned = buildGraphicalRepresentation(shape);
				shapesGR.put(shape, returned);
			}
			return (GraphicalRepresentation<O>)returned;
		}
		else if (aDrawable instanceof CalcDrawingConnector) {
			CalcDrawingConnector connector = (CalcDrawingConnector)aDrawable;
			CalcDrawingConnectorGR returned = connectorsGR.get(connector);
			if (returned == null) {
				returned = buildGraphicalRepresentation(connector);
				connectorsGR.put(connector, returned);
			}
			return (GraphicalRepresentation<O>)returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}

	private CalcDrawingConnectorGR buildGraphicalRepresentation(CalcDrawingConnector connector)
	{
		if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			CalcDrawingConnectorGR graphicalRepresentation = new CalcDrawingConnectorGR(connector,this);
			graphicalRepresentation.setsWith(
					(ConnectorGraphicalRepresentation)connector.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			if (!readOnly) connector.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		CalcDrawingConnectorGR graphicalRepresentation = new CalcDrawingConnectorGR(connector,this);
		if (!readOnly) connector.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private CalcDrawingShapeGR buildGraphicalRepresentation(CalcDrawingShape shape)
	{
		if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			CalcDrawingShapeGR graphicalRepresentation = new CalcDrawingShapeGR(shape,this);
			graphicalRepresentation.setsWith(
					(ShapeGraphicalRepresentation)shape.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			if (!readOnly) shape.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		CalcDrawingShapeGR graphicalRepresentation = new CalcDrawingShapeGR(shape,this);
		if (!readOnly) shape.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
	}
	
}
