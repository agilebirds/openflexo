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

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.ontology.shema.OEConnector;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaObject;


public class OEShemaRepresentation extends DefaultDrawing<OEShema> implements GraphicalFlexoObserver, OEShemaConstants {

	private static final Logger logger = Logger.getLogger(OEShemaRepresentation.class.getPackage().getName());
	
	private OEShemaGR graphicalRepresentation;
	
	private Hashtable<OEShape,OEShapeGR> shapesGR;
	private Hashtable<OEConnector,OEConnectorGR> connectorsGR;

	public OEShemaRepresentation(OEShema aShema)
	{
		super(aShema);
		//graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		//graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());
		
		shapesGR = new Hashtable<OEShape,OEShapeGR>();
		connectorsGR = new Hashtable<OEConnector,OEConnectorGR>();
		
		aShema.addObserver(this);
		
		updateGraphicalObjectsHierarchy();

	}
	
	@Override
	public void delete()
	{
		if (graphicalRepresentation != null) graphicalRepresentation.delete();
		if (getShema() != null) getShema().deleteObserver(this);
	}
	
	@Override
	protected void buildGraphicalObjectsHierarchy()
	{
 		buildGraphicalObjectsHierarchyFor(getShema());
	}
	
	private void buildGraphicalObjectsHierarchyFor(OEShemaObject parent)
	{
		for (OEShemaObject child : parent.getChilds()) {
			if (!(child instanceof OEConnector)) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
		for (OEShemaObject child : parent.getChilds()) {
			if (child instanceof OEConnector) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
	}
	
	public OEShema getShema()
	{
		return getModel();
	}

	@Override
	public OEShemaGR getDrawingGraphicalRepresentation()
	{
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new OEShemaGR(this);
		}
		return graphicalRepresentation;
	}

	/*@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		return (GraphicalRepresentation<O>)buildGraphicalRepresentation(aDrawable);
	}*/
	
	/*private GraphicalRepresentation<?> buildGraphicalRepresentation(Object aDrawable)
	{
		if (aDrawable instanceof OEShape) {
			OEShape shape = (OEShape)aDrawable;
			if (shape.getGraphicalRepresentation() instanceof OEShapeGR) {
				logger.info("TODO: faire ici comme dans CalcDrawingShemaRepresentation");
				OEShapeGR returned = (OEShapeGR)shape.getGraphicalRepresentation();
				returned.setDrawable(shape);
				returned.setDrawing(this);
				return returned;
			}
			else if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
				OEShapeGR graphicalRepresentation = new OEShapeGR(shape,this);
				graphicalRepresentation.setsWith(
						(ShapeGraphicalRepresentation)shape.getGraphicalRepresentation(),
						GraphicalRepresentation.Parameters.text);
				shape.setGraphicalRepresentation(graphicalRepresentation);
				return graphicalRepresentation;
			}
			OEShapeGR graphicalRepresentation = new OEShapeGR(shape,this);
			shape.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		else if (aDrawable instanceof OEConnector) {
			OEConnector connector = (OEConnector)aDrawable;
			if (connector.getGraphicalRepresentation() instanceof OEConnectorGR) {
				logger.info("TODO: faire ici comme dans CalcDrawingShemaRepresentation");
				OEConnectorGR returned = (OEConnectorGR)connector.getGraphicalRepresentation();
				returned.setDrawable(connector);
				returned.setDrawing(this);
				return returned;
				//return (CalcDrawingConnectorGR)connector.getGraphicalRepresentation();
			}
			else if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
				OEConnectorGR graphicalRepresentation = new OEConnectorGR(connector,this);
				graphicalRepresentation.setsWith(
						(ConnectorGraphicalRepresentation)connector.getGraphicalRepresentation(),
						GraphicalRepresentation.Parameters.text);
				connector.setGraphicalRepresentation(graphicalRepresentation);
				return graphicalRepresentation;
			}
			OEConnectorGR graphicalRepresentation = new OEConnectorGR(connector,this);
			connector.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable)
	{
		if (aDrawable instanceof OEShape) {
			OEShape shape = (OEShape)aDrawable;
			OEShapeGR returned = shapesGR.get(shape);
			if (returned == null) {
				returned = buildGraphicalRepresentation(shape);
				shapesGR.put(shape, returned);
			}
			return (GraphicalRepresentation<O>)returned;
		}
		else if (aDrawable instanceof OEConnector) {
			OEConnector connector = (OEConnector)aDrawable;
			OEConnectorGR returned = connectorsGR.get(connector);
			if (returned == null) {
				returned = buildGraphicalRepresentation(connector);
				connectorsGR.put(connector, returned);
			}
			return (GraphicalRepresentation<O>)returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for "+aDrawable);
		return null;
	}


	private OEConnectorGR buildGraphicalRepresentation(OEConnector connector)
	{
		if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			OEConnectorGR graphicalRepresentation = new OEConnectorGR(connector,this);
			graphicalRepresentation.setsWith(
					(ConnectorGraphicalRepresentation)connector.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			connector.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		OEConnectorGR graphicalRepresentation = new OEConnectorGR(connector,this);
		connector.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private OEShapeGR buildGraphicalRepresentation(OEShape shape)
	{
		if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			OEShapeGR graphicalRepresentation = new OEShapeGR(shape,this);
			graphicalRepresentation.setsWith(
					(ShapeGraphicalRepresentation)shape.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text,
					ShapeGraphicalRepresentation.Parameters.border);
			shape.setGraphicalRepresentation(graphicalRepresentation);
			return graphicalRepresentation;
		}
		OEShapeGR graphicalRepresentation = new OEShapeGR(shape,this);
		shape.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
	}
	
}
