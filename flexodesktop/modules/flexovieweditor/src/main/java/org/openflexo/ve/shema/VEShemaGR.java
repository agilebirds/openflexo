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

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.impl.DrawingGraphicalRepresentationImpl;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.ConnectorInserted;
import org.openflexo.foundation.view.ConnectorRemoved;
import org.openflexo.foundation.view.ShapeInserted;
import org.openflexo.foundation.view.ShapeRemoved;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.xml.VEShemaBuilder;

public class VEShemaGR extends DrawingGraphicalRepresentationImpl<View> implements GraphicalFlexoObserver, VEShemaConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(VEShemaGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization DO NOT use it
	 */
	public VEShemaGR(VEShemaBuilder builder) {
		this((VEShemaRepresentation) null);
	}

	public VEShemaGR(VEShemaRepresentation aDrawing) {
		super(aDrawing);

		if (aDrawing != null && aDrawing.getShema() != null && aDrawing.getShema().getGraphicalRepresentation() != null) {
			setsWith((GraphicalRepresentation<?>) aDrawing.getShema().getGraphicalRepresentation());
		}

		addToMouseClickControls(new VEShemaController.ShowContextualMenuControl());

		if (aDrawing != null && aDrawing.getShema() != null) {
			aDrawing.getShema().setGraphicalRepresentation(this);
			aDrawing.getShema().addObserver(this);
		}

	}

	@Override
	public VEShemaRepresentation getDrawing() {
		return (VEShemaRepresentation) super.getDrawing();
	}

	public View getShema() {
		return getDrawing().getShema();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getShema()) {
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
