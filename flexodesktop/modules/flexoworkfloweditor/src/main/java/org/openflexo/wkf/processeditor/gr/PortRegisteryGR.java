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
package org.openflexo.wkf.processeditor.gr;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.wkf.action.OpenPortRegistery;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.PortInserted;
import org.openflexo.foundation.wkf.dm.PortRemoved;
import org.openflexo.foundation.wkf.ws.PortRegistery;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.wkf.processeditor.ProcessRepresentation;

public class PortRegisteryGR extends ContainerGR<PortRegistery> {

	public PortRegisteryGR(PortRegistery object, ProcessRepresentation aDrawing) {
		super(object, aDrawing, PORT_REGISTRY_PG_COLOR, OPERATION_PG_BACK_COLOR);
		setLayer(ACTIVITY_PG_LAYER);
	}

	public PortRegistery getPortRegistery() {
		return getDrawable();
	}

	@Override
	public String getLabel() {
		return FlexoLocalization.localizedForKey("port_registery");
	}

	@Override
	public void closingRequested() {
		OpenPortRegistery.actionType.makeNewAction(getPortRegistery().getProcess(), null, getDrawing().getEditor()).doAction();
		// Is now performed by receiving notification
		// getDrawing().updateGraphicalObjectsHierarchy();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// logger.info(">>>>>>>>>>>  Notified "+dataModification+" for "+observable);
		if (observable == getModel()) {
			if ((dataModification instanceof PortInserted) || (dataModification instanceof PortRemoved)) {
				getDrawing().updateGraphicalObjectsHierarchy();
				notifyShapeNeedsToBeRedrawn();
				notifyObjectMoved();
				notifyObjectResized();
			}
			if (dataModification instanceof ObjectVisibilityChanged) {
				getDrawing().updateGraphicalObjectsHierarchy();
			}
		}
		super.update(observable, dataModification);
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultX() {
		return 150;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultY() {
		return 60;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultWidth() {
		return 80;
	}

	/**
	 * Overriden to implement defaut automatic layout
	 */
	@Override
	public double getDefaultHeight() {
		return 20;
	}

}
