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
package org.openflexo.rm.view;

import java.awt.Color;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.rm.view.RMViewerRepresentation.ResourceSynchronization;

public class ResourceSynchronizationGR extends ConnectorGraphicalRepresentation<ResourceSynchronization> {

	private ForegroundStyle foreground;

	public ResourceSynchronizationGR(ResourceSynchronization synchro, Drawing<?> aDrawing) {
		super(ConnectorType.LINE, (ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(synchro.getR1()),
				(ShapeGraphicalRepresentation<?>) aDrawing.getGraphicalRepresentation(synchro.getR2()), synchro, aDrawing);
		foreground = ForegroundStyle.makeStyle(Color.RED);
		foreground.setLineWidth(1.6);
		setForeground(foreground);

		setStartSymbol(StartSymbolType.PLAIN_ARROW);
		setEndSymbol(EndSymbolType.PLAIN_ARROW);
		setLayer(Math.max(getStartObject().getLayer(), getEndObject().getLayer()) + 1);

	}

	public ResourceSynchronization getResourceSynchronization() {
		return getDrawable();
	}

	@Override
	public RMViewerRepresentation getDrawing() {
		return (RMViewerRepresentation) super.getDrawing();
	}

}
