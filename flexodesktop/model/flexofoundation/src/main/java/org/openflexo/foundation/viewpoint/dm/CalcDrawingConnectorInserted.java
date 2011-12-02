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
package org.openflexo.foundation.viewpoint.dm;

import org.openflexo.foundation.ontology.dm.OEDataModification;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.ExampleDrawingObject;

/**
 * Notify that a new connector has been added to palette
 * 
 * @author sguerin
 * 
 */
public class CalcDrawingConnectorInserted extends OEDataModification {

	private ExampleDrawingObject _parent;

	public CalcDrawingConnectorInserted(ExampleDrawingConnector connector, ExampleDrawingObject parent) {
		super(null, connector);
		_parent = parent;
	}

	public ExampleDrawingObject getParent() {
		return _parent;
	}

}
