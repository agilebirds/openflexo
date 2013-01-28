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

import java.util.logging.Logger;

import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorInserted;
import org.openflexo.foundation.view.diagram.model.dm.ConnectorRemoved;
import org.openflexo.foundation.view.diagram.model.dm.ShapeInserted;
import org.openflexo.foundation.view.diagram.model.dm.ShapeRemoved;
import org.openflexo.foundation.xml.ViewBuilder;

public class DiagramGR extends DrawingGraphicalRepresentation<Diagram> implements GraphicalFlexoObserver, DiagramConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DiagramGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public DiagramGR(ViewBuilder builder) {
		this((DiagramRepresentation) null);
	}

	public DiagramGR(DiagramRepresentation aDrawing) {
		super(aDrawing);

		if (aDrawing != null && aDrawing.getDiagram() != null && aDrawing.getDiagram().getRootPane().getGraphicalRepresentation() != null) {
			setsWith(aDrawing.getDiagram().getRootPane().getGraphicalRepresentation());
		}

		addToMouseClickControls(new DiagramController.ShowContextualMenuControl());

		if (aDrawing != null && aDrawing.getDiagram() != null) {
			aDrawing.getDiagram().getRootPane().setGraphicalRepresentation(this);
			aDrawing.getDiagram().getRootPane().addObserver(this);
		}

	}

	@Override
	public DiagramRepresentation getDrawing() {
		return (DiagramRepresentation) super.getDrawing();
	}

	public Diagram getDiagram() {
		return getDrawing().getDiagram();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getDiagram() || observable == getDiagram().getRootPane()) {
			// logger.info("Notified "+dataModification);
			if (dataModification instanceof ShapeInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ShapeRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ConnectorInserted) {
				getDrawing().updateGraphicalObjectsHierarchy();
			} else if (dataModification instanceof ConnectorRemoved) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
	}

}
