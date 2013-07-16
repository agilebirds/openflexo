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
package org.openflexo.wkf.roleeditor;

import java.awt.Color;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.RectPolylinConnector;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.fge.connectors.RectPolylinConnector.RectPolylinConstraints;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.wkf.RoleSpecialization;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;

public class RoleSpecializationGR extends ConnectorGraphicalRepresentation<RoleSpecialization> implements GraphicalFlexoObserver {

	private ForegroundStyle foreground;

	public RoleSpecializationGR(RoleSpecialization specialization, Drawing<?> aDrawing) {
		super(ConnectorType.LINE, (ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(specialization.getRole()),
				(ShapeGraphicalRepresentation) aDrawing.getGraphicalRepresentation(specialization.getParentRole()), specialization,
				aDrawing);
		foreground = ForegroundStyle.makeStyle(Color.DARK_GRAY);
		foreground.setLineWidth(1.6);
		setForeground(foreground);
		/*if (getConnector() instanceof LineConnector) {
			((LineConnector)getConnector()).setLineConnectorType(LineConnectorType.CENTER_TO_CENTER);
		}*/
		if (getConnector() instanceof RectPolylinConnector) {
			((RectPolylinConnector) getConnector()).setStraightLineWhenPossible(false);
			((RectPolylinConnector) getConnector()).setRectPolylinConstraints(RectPolylinConstraints.VERTICAL_LAYOUT);
		}
		setEndSymbol(EndSymbolType.PLAIN_ARROW);
		if (getStartObject() != null && getEndObject() != null) {
			setLayer(Math.max(getStartObject().getLayer(), getEndObject().getLayer()) + 1);
		}

		addToMouseClickControls(new RoleEditorController.ShowContextualMenuControl());

		specialization.addObserver(this);
	}

	@Override
	public void delete() {
		RoleSpecialization roleSpecialization = getRoleSpecialization();
		super.delete();
		roleSpecialization.deleteObserver(this);
	}

	public RoleSpecialization getRoleSpecialization() {
		return getDrawable();
	}

	@Override
	public RoleListRepresentation getDrawing() {
		return (RoleListRepresentation) super.getDrawing();
	}

	@Override
	public String getText() {
		String returned = getRoleSpecialization().getAnnotation();
		if (returned == null) {
			return null;
		}
		if (returned.trim().equals("")) {
			return null;
		}
		return returned;
	}

	@Override
	public void setTextNoNotification(String text) {
		getRoleSpecialization().setAnnotation(text);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getRoleSpecialization()) {
			if (dataModification instanceof WKFAttributeDataModification) {
				if (((WKFAttributeDataModification) dataModification).getAttributeName().equals("annotation")) {
					notifyAttributeChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				}
			}
		}
	}

}
