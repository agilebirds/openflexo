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
package org.openflexo.ve.shema;

import java.util.Hashtable;
import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.DefaultDrawing;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;

public class VEShemaRepresentation extends DefaultDrawing<View> implements GraphicalFlexoObserver, VEShemaConstants {

	private static final Logger logger = Logger.getLogger(VEShemaRepresentation.class.getPackage().getName());

	private VEShemaGR graphicalRepresentation;

	private Hashtable<ViewShape, VEShapeGR> shapesGR;
	private Hashtable<ViewConnector, VEConnectorGR> connectorsGR;

	private boolean screenshotOnly;

	public VEShemaRepresentation(View aShema, boolean screenshotOnly) {
		super(aShema);
		// graphicalRepresentation = new DrawingGraphicalRepresentation<OEShema>(this);
		// graphicalRepresentation.addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());

		this.screenshotOnly = screenshotOnly;

		shapesGR = new Hashtable<ViewShape, VEShapeGR>();
		connectorsGR = new Hashtable<ViewConnector, VEConnectorGR>();

		aShema.addObserver(this);

		updateGraphicalObjectsHierarchy();

	}

	@Override
	public void delete() {
		if (graphicalRepresentation != null) {
			graphicalRepresentation.delete();
		}
		if (getShema() != null) {
			getShema().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	protected void buildGraphicalObjectsHierarchy() {
		buildGraphicalObjectsHierarchyFor(getShema());
	}

	private void buildGraphicalObjectsHierarchyFor(ViewObject parent) {
		for (ViewObject child : parent.getChilds()) {
			if (!(child instanceof ViewConnector)) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
		for (ViewObject child : parent.getChilds()) {
			if (child instanceof ViewConnector) {
				addDrawable(child, parent);
				buildGraphicalObjectsHierarchyFor(child);
			}
		}
	}

	public View getShema() {
		return getModel();
	}

	@Override
	public VEShemaGR getDrawingGraphicalRepresentation() {
		if (graphicalRepresentation == null) {
			graphicalRepresentation = new VEShemaGR(this);
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
		if (aDrawable instanceof ViewShape) {
			ViewShape shape = (ViewShape) aDrawable;
			VEShapeGR returned = shapesGR.get(shape);
			if (returned == null) {
				returned = buildGraphicalRepresentation(shape);
				shapesGR.put(shape, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		} else if (aDrawable instanceof ViewConnector) {
			ViewConnector connector = (ViewConnector) aDrawable;
			VEConnectorGR returned = connectorsGR.get(connector);
			if (returned == null) {
				returned = buildGraphicalRepresentation(connector);
				connectorsGR.put(connector, returned);
			}
			return (GraphicalRepresentation<O>) returned;
		}
		logger.warning("Cannot build GraphicalRepresentation for " + aDrawable);
		return null;
	}

	private VEConnectorGR buildGraphicalRepresentation(ViewConnector connector) {
		if (connector.getGraphicalRepresentation() instanceof ConnectorGraphicalRepresentation) {
			VEConnectorGR graphicalRepresentation = new VEConnectorGR(connector, this);
			graphicalRepresentation.setsWith((ConnectorGraphicalRepresentation<?>) connector.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text);
			if (!screenshotOnly) {
				connector.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		VEConnectorGR graphicalRepresentation = new VEConnectorGR(connector, this);
		connector.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	private VEShapeGR buildGraphicalRepresentation(ViewShape shape) {
		/*if (shape.getGraphicalRepresentation() instanceof VEShapeGR) {
			VEShapeGR returned = (VEShapeGR) shape.getGraphicalRepresentation();
			if (!returned.isGRRegistered()) {
				returned.registerShapeGR(shape, this);
			}
			return returned;
		}*/
		if (shape.getGraphicalRepresentation() instanceof ShapeGraphicalRepresentation) {
			VEShapeGR graphicalRepresentation = new VEShapeGR(shape, this);
			graphicalRepresentation.setsWith((GraphicalRepresentation<?>) shape.getGraphicalRepresentation(),
					GraphicalRepresentation.Parameters.text /*, ShapeGraphicalRepresentation.Parameters.border*/);
			if (!screenshotOnly) {
				shape.setGraphicalRepresentation(graphicalRepresentation);
			}
			return graphicalRepresentation;
		}
		VEShapeGR graphicalRepresentation = new VEShapeGR(shape, this);
		shape.setGraphicalRepresentation(graphicalRepresentation);
		return graphicalRepresentation;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

}
