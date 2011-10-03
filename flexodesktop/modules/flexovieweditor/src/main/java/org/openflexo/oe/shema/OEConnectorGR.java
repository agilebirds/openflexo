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
package org.openflexo.oe.shema;

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.view.OEConnector;
import org.openflexo.foundation.xml.OEShemaBuilder;
import org.openflexo.toolbox.ToolBox;


public class OEConnectorGR extends ConnectorGraphicalRepresentation<OEConnector> implements GraphicalFlexoObserver,OEShemaConstants {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(OEConnectorGR.class.getPackage().getName());

	/**
	 * Constructor invoked during deserialization
	 * DO NOT use it
	 */
	public OEConnectorGR(OEShemaBuilder builder)
	{
		this(null,null);
	}

	public OEConnectorGR(OEConnector aConnector, Drawing<?> aDrawing) 
	{
		super(ConnectorType.LINE,
				aDrawing != null ? (ShapeGraphicalRepresentation<?>)aDrawing.getGraphicalRepresentation(aConnector.getStartShape()) : null,
				aDrawing != null ? (ShapeGraphicalRepresentation<?>)aDrawing.getGraphicalRepresentation(aConnector.getEndShape()) : null,
				aConnector, aDrawing);
		//setText(getRole().getName());

		addToMouseClickControls(new OEShemaController.ShowContextualMenuControl());
		if (ToolBox.getPLATFORM()!=ToolBox.MACOS) {
			addToMouseClickControls(new OEShemaController.ShowContextualMenuControl(true));
		}
		//addToMouseDragControls(new DrawRoleSpecializationControl());

		if (aConnector != null) aConnector.addObserver(this);

	}
	
	@Override
	public void delete()
	{
		if (getDrawable() != null) getDrawable().deleteObserver(this);
		super.delete();
	}

	public OEConnector getOEConnector()
	{
		return getDrawable();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification)
	{
		if (observable == getOEConnector()) {
			if (dataModification instanceof NameChanged) {
				//logger.info("received NameChanged notification");
				notifyChange(org.openflexo.fge.GraphicalRepresentation.Parameters.text);
				//setText(getText());
			}
		}
	}

	@Override
	public String getText()
	{
		if (getOEConnector() != null)
			return getOEConnector().getName();
		return null;
	}

	@Override
	public void setTextNoNotification(String text) 
	{
		if (getOEConnector() != null)
			getOEConnector().setName(text);
	}
}
