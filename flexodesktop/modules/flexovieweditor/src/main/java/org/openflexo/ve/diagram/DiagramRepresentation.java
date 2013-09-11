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
package org.openflexo.ve.diagram;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.model.DiagramConnector;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.model.DiagramShape;

public class DiagramRepresentation extends DefaultDrawing<DiagramRootPane> implements GraphicalFlexoObserver, DiagramConstants {

	private static final Logger logger = Logger.getLogger(DiagramRepresentation.class.getPackage().getName());

	private DiagramGR graphicalRepresentation;

	private Hashtable<DiagramShape, DiagramShapeGR> shapesGR;
	private Hashtable<DiagramConnector, DiagramConnectorGR> connectorsGR;

	private boolean screenshotOnly;

	public DiagramRepresentation(DiagramRootPane aDiagramRootPane, boolean screenshotOnly) {
		super(aDiagramRootPane);
		// graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		// graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());

		this.screenshotOnly = screenshotOnly;

		shapesGR = new Hashtable<DiagramShape, DiagramShapeGR>();
		connectorsGR = new Hashtable<DiagramConnector, DiagramConnectorGR>();

		aDiagramRootPane.addObserver(this);

		updateGraphicalObjectsHierarchy();

	}

	@Override
	public void delete() {
		if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}
		if (getDiagramRootPane() != null) {
			getDiagramRootPane().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		buildGraphicalObjectsHierarchyFor(getDiagramRootPane());
	}

	private void buildGraphicalObjectsHierarchyFor(DiagramElement<?> parent) {
		for (DiagramElement<?> child : parent.getChilds()) {
			if (!(child instanceof DiagramConnector)) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
		for (DiagramElement<?> child : parent.getChilds()) {
			if (child instanceof DiagramConnector) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
	}

	public DiagramRootPane getDiagramRootPane() {
		return getModel();
	}

	public Diagram getDiagram() {
		return getDiagramRootPane().getDiagram();
	}

	@Override
	public DiagramGR getDrawingGraphicalRepresentation() {
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new DiagramGR(this);
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
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable) {
		if (aDrawable instanceof DiagramShape) {
			DiagramShape shape = (DiagramShape) aDrawable;
			DiagramShapeGR returned = shapesGR.get(shape);
			if (returned == null) {
				returned = buildGraphicalRepresentation(shape);
				shapesGR.put(shape, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		} else if (aDrawable instanceof DiagramConnector) {
			DiagramConnector connector = (DiagramConnector) aDrawable;
			DiagramConnectorGR returned = connectorsGR.get(connector);
			if (returned == null) {
				returned = buildGraphicalRepresentation(connector);
				connectorsGR.put(connector, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	private DiagramConnectorGR buildGraphicalRepresentation(DiagramConnector connector) {
		if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			DiagramConnectorGR graphicalRepresentation = new DiagramConnectorGR(connector, this);
			graphicalRepresentation.setsWith((ConnectorGraphicalRepresentation<?>) connector.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			if (!screenshotOnly) {
				connector.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		DiagramConnectorGR graphicalRepresentation = new DiagramConnectorGR(connector, this);
		connector.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private DiagramShapeGR buildGraphicalRepresentation(DiagramShape shape) {
		/*if (shape.getGraphicalRepresentation() instanceof DiagramShapeGR) {
			DiagramShapeGR returned = (DiagramShapeGR) shape.getGraphicalRepresentation();
			if (!returned.isGRRegistered()) {
				returned.registerShapeGR(shape, this);
			}
			return returned;
		}*/
		if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			DiagramShapeGR graphicalRepresentation = new DiagramShapeGR(shape, this);
			graphicalRepresentation.setsWith(shape.getGraphicalRepresentation(),
					shape.getPatternRole() != null ? new GraphicalRepresentation.GRParameter[] { GraphicalRepresentation.Parameters.text } /*, ShapeGraphicalRepresentation.Parameters.border*/
					: new GraphicalRepresentation.GRParameter[] {});
			if (!screenshotOnly) {
				shape.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		DiagramShapeGR graphicalRepresentation = new DiagramShapeGR(shape, this);
		shape.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

}
