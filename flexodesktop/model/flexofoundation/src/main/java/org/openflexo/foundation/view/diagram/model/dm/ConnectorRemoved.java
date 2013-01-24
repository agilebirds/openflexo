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
package org.openflexo.foundation.view.diagram.model.dm;

import org.openflexo.foundation.view.diagram.model.ViewConnector;
import org.openflexo.foundation.view.diagram.model.ViewShape;


/**
 * Notify that a Connector has been removed from a OEShema
 * 
 * @author sguerin
 * 
 */
public class ConnectorRemoved extends DiagramDataModification {

	public ConnectorRemoved(ViewConnector connector) {
		super(connector, null);
	}

	public ViewShape getFromShape() {
		return ((ViewConnector) oldValue()).getStartShape();
	}

	public ViewShape getToShape() {
		return ((ViewConnector) oldValue()).getEndShape();
	}

}
