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
package org.openflexo.fme.model;

import org.openflexo.fge.ConnectorGraphicalRepresentation;

public abstract class ConnectorImpl extends DiagramElementImpl<Connector, ConnectorGraphicalRepresentation> implements Connector {

	// Called for LOAD
	/*public ConnectorImpl(DrawingBuilder builder) {
		super(builder.drawing);
		// initializeDeserialization();
	}*/

	// Used by PAMELA, do not use it
	public ConnectorImpl() {
		super(null);
	}

	// Called for NEW
	/*public ConnectorImpl(Shape from, Shape to, DiagramDrawing drawing) {
		super(drawing.getModel());

		gr = drawing
				.getModel()
				.getFactory()
				.makeNewConnectorGR(ConnectorType.LINE, (MyShapeGraphicalRepresentation) drawing.getGraphicalRepresentation(from),
						(MyShapeGraphicalRepresentation) drawing.getGraphicalRepresentation(to), this, drawing);

		setGraphicalRepresentation(gr);
	}*/

}