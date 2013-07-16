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
import java.awt.Font;
import java.text.SimpleDateFormat;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.impl.ConnectorGraphicalRepresentationImpl;
import org.openflexo.rm.view.RMViewerRepresentation.ResourceDependancy;

public class ResourceDependancyGR extends ConnectorGraphicalRepresentationImpl<ResourceDependancy> {

	private ForegroundStyle foreground;

	public ResourceDependancyGR(ResourceDependancy dependancy, Drawing<?> aDrawing) {
		super(ConnectorType.LINE, (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(dependancy.getR1()),
				(ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(dependancy.getR2()), dependancy, aDrawing);
		foreground = ForegroundStyle.makeStyle(Color.DARK_GRAY);
		foreground.setLineWidth(1.6);
		setForeground(foreground);

		setEndSymbol(EndSymbolType.PLAIN_ARROW);
		setLayer(Math.max(getStartObject().getLayer(), getEndObject().getLayer()) + 1);

		setTextStyle(TextStyle.makeTextStyle(Color.GRAY, new Font("SansSerif", Font.ITALIC, 8)));

	}

	public ResourceDependancy getResourceDependancy() {
		return getDrawable();
	}

	@Override
	public RMViewerRepresentation getDrawing() {
		return (RMViewerRepresentation) super.getDrawing();
	}

	@Override
	public String getText() {
		return new SimpleDateFormat("dd/MM HH:mm:ss SSS").format(getResourceDependancy().getLastSynchronizationDate());
	}

}
