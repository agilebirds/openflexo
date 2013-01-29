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
package org.openflexo.vpm.examplediagram;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;

public class ExampleDiagramRepresentation extends DefaultDrawing<ExampleDiagram> implements GraphicalFlexoObserver,
		ExampleDiagramConstants {

	private static final Logger logger = Logger.getLogger(ExampleDiagramRepresentation.class.getPackage().getName());

	private ExampleDiagramGR graphicalRepresentation;

	private Boolean ignoreNotifications = true;

	private Hashtable<ExampleDiagramShape, ExampleDiagramShapeGR> shapesGR;
	private Hashtable<ExampleDiagramConnector, ExampleDiagramConnectorGR> connectorsGR;

	private boolean readOnly = false;

	public ExampleDiagramRepresentation(ExampleDiagram aShema, boolean readOnly) {
		super(aShema);

		this.readOnly = readOnly;
		// graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		// graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());

		shapesGR = new Hashtable<ExampleDiagramShape, ExampleDiagramShapeGR>();
		connectorsGR = new Hashtable<ExampleDiagramConnector, ExampleDiagramConnectorGR>();

		aShema.addObserver(this);

		updateGraphicalObjectsHierarchy();

		ignoreNotifications = false;

	}

	@Override
	public void delete() {
		if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}
		if (getExampleDiagram() != null) {
			getExampleDiagram().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void beginUpdateObjectHierarchy() {
		ignoreNotifications = true;
		super.beginUpdateObjectHierarchy();
	}

	@Override
	protected void endUpdateObjectHierarchy() {
		super.endUpdateObjectHierarchy();
		ignoreNotifications = false;
	}

	protected boolean ignoreNotifications() {
		if (ignoreNotifications == null) {
			return true;
		}
		return ignoreNotifications;
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		buildGraphicalObjectsHierarchyFor(getExampleDiagram());
	}

	private void buildGraphicalObjectsHierarchyFor(ExampleDiagramObject parent) {
		// logger.info("buildGraphicalObjectsHierarchyFor "+parent);

		for (ExampleDiagramObject child : parent.getChilds()) {
			if (!(child instanceof ExampleDiagramConnector)) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
		for (ExampleDiagramObject child : parent.getChilds()) {
			if (child instanceof ExampleDiagramConnector) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
	}

	public ExampleDiagram getExampleDiagram() {
		return getModel();
	}

	@Override
	public ExampleDiagramGR getDrawingGraphicalRepresentation() {
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new ExampleDiagramGR(this);
		}
		return graphicalRepresentation;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> GraphicalRepresentation<O> retrieveGraphicalRepresentation(O aDrawable) {
		if (aDrawable instanceof ExampleDiagramShape) {
			ExampleDiagramShape shape = (ExampleDiagramShape) aDrawable;
			ExampleDiagramShapeGR returned = shapesGR.get(shape);
			if (returned == null) {
				returned = buildGraphicalRepresentation(shape);
				shapesGR.put(shape, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		} else if (aDrawable instanceof ExampleDiagramConnector) {
			ExampleDiagramConnector connector = (ExampleDiagramConnector) aDrawable;
			ExampleDiagramConnectorGR returned = connectorsGR.get(connector);
			if (returned == null) {
				returned = buildGraphicalRepresentation(connector);
				connectorsGR.put(connector, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	private ExampleDiagramConnectorGR buildGraphicalRepresentation(ExampleDiagramConnector connector) {
		if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			ExampleDiagramConnectorGR graphicalRepresentation = new ExampleDiagramConnectorGR(connector, this);
			graphicalRepresentation.setsWith(connector.getGraphicalRepresentation());
			if (!readOnly) {
				connector.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		ExampleDiagramConnectorGR graphicalRepresentation = new ExampleDiagramConnectorGR(connector, this);
		if (!readOnly) {
			connector.setGraphicalRepresentation(graphicalRepresentation);
		}
		return graphicalRepresentation;
	}

	private ExampleDiagramShapeGR buildGraphicalRepresentation(ExampleDiagramShape shape) {
		if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			ExampleDiagramShapeGR graphicalRepresentation = new ExampleDiagramShapeGR(shape, this);
			graphicalRepresentation.setsWith(shape.getGraphicalRepresentation());
			if (!readOnly) {
				shape.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		ExampleDiagramShapeGR graphicalRepresentation = new ExampleDiagramShapeGR(shape, this);
		if (!readOnly) {
			shape.setGraphicalRepresentation(graphicalRepresentation);
		}
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

}
