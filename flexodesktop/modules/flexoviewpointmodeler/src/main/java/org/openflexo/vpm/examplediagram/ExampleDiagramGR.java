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

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramConnectorInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramConnectorRemoved;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramShapeInserted;
import org.openflexo.foundation.viewpoint.dm.ExampleDiagramShapeRemoved;

public class ExampleDiagramGR extends DrawingGraphicalRepresentation<ExampleDiagram> implements GraphicalFlexoObserver,
		ExampleDiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExampleDiagramGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public ExampleDiagramGR() {
		this(null);
	}

	public ExampleDiagramGR(ExampleDiagramRepresentation aDrawing) {
		super(aDrawing);

		if (aDrawing != null && aDrawing.getExampleDiagram() != null && aDrawing.getExampleDiagram().getGraphicalRepresentation() != null) {
			setsWith(aDrawing.getExampleDiagram().getGraphicalRepresentation());
		}

		addToMouseClickControls(new ExampleDiagramController.ShowContextualMenuControl());

		if (aDrawing != null && aDrawing.getExampleDiagram() != null) {
			aDrawing.getExampleDiagram().setGraphicalRepresentation(this);
			aDrawing.getExampleDiagram().addObserver(this);
		}

	}

	@Override
	public ExampleDiagramRepresentation getDrawing() {
		return (ExampleDiagramRepresentation) super.getDrawing();
	}

	public ExampleDiagram getShema() {
		if (getDrawing() != null) {
			return getDrawing().getExampleDiagram();
		}
		return null;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getShema()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof ExampleDiagramShapeInserted || dataModification instanceof ExampleDiagramShapeRemoved
					|| dataModification instanceof ExampleDiagramConnectorInserted
					|| dataModification instanceof ExampleDiagramConnectorRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		super.notifyObservers(arg);
		if (arg instanceof FGEAttributeNotification && ((FGEAttributeNotification) arg).isModelNotification() && getDrawing() != null
				&& !getDrawing().ignoreNotifications() && getShema() != null && !getShema().ignoreNotifications()) {
			getShema().setChanged();
		}
	}

}
