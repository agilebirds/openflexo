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

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.notifications.FGENotification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.toolbox.ToolBox;

public class ExampleDiagramConnectorGR extends ConnectorGraphicalRepresentation<ExampleDiagramConnector> implements GraphicalFlexoObserver,
		ExampleDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExampleDiagramConnectorGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public ExampleDiagramConnectorGR() {
		super(ConnectorType.LINE, null, null, null, null);
	}

	public ExampleDiagramConnectorGR(ExampleDiagramConnector aConnector, Drawing<?> aDrawing) {
		super(ConnectorType.LINE, aDrawing != null ? (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aConnector
				.getStartShape()) : null, aDrawing != null ? (ShapeGraphicalRepresentation) aDrawing
				.getGraphicalRepresentation(aConnector.getEndShape()) : null, aConnector, aDrawing);

		setStartObject((ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aConnector.getStartShape()));
		setEndObject((ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(aConnector.getEndShape()));

		addToMouseClickControls(new ExampleDiagramController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			addToMouseClickControls(new ExampleDiagramController.ShowContextualMenuControl(true));
		}

		if (aConnector != null) {
			aConnector.addObserver(this);
		}

	}

	@Override
	public void delete() {
		if (getDrawable() != null) {
			getDrawable().deleteObserver(this);
		}
		super.delete();
	}

	@Override
	public ExampleDiagramRepresentation getDrawing() {
		return (ExampleDiagramRepresentation) super.getDrawing();
	}

	public ExampleDiagramConnector getCalcDrawingConnector() {
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getCalcDrawingConnector()) {
			if (dataModification instanceof NameChanged) {
				// logger.info("received NameChanged notification");
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				// setText(getText());
			}
		}
	}

	/*@Override
	public String getText() {
		if (getCalcDrawingConnector() != null) {
			return getCalcDrawingConnector().getName();
		}
		return null;
	}

	@Override
	public void setTextNoNotification(String text) {
		if (getCalcDrawingConnector() != null) {
			getCalcDrawingConnector().setName(text);
		}
	}*/

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGENotification && ((FGENotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getCalcDrawingConnector() != null
				&& !getCalcDrawingConnector().getExampleDiagram().ignoreNotifications()) {
			getCalcDrawingConnector().setChanged();
		}
	}

}
