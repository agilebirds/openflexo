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
package org.openflexo.fge.connectors.rpc;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.area.FGEArea;

public abstract class RectPolylinAdjustableControlPoint extends ConnectorAdjustingControlPoint {
	protected FGERectPolylin initialPolylin;
	private RectPolylinConnector connector;

	public RectPolylinAdjustableControlPoint(FGEPoint point, RectPolylinConnector connector) {
		super(connector.getGraphicalRepresentation(), point);
		this.connector = connector;
	}

	@Override
	public Cursor getDraggingCursor() {
		return FGEConstants.MOVE_CURSOR;
	}

	@Override
	public abstract FGEArea getDraggingAuthorizedArea();

	@Override
	public abstract boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event);

	protected void notifyConnectorChanged() {
		getGraphicalRepresentation().notifyConnectorChanged();
	}

	@Override
	public void startDragging(DrawingController controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		initialPolylin = getPolylin().clone();
		getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public final void stopDragging(DrawingController controller, GraphicalRepresentation focusedGR) {
		super.stopDragging(controller, focusedGR);
		getConnector().setWasManuallyAdjusted(true);
		getConnector()._connectorChanged(false);
	}

	public RectPolylinConnector getConnector() {
		return connector;
	}

	public FGERectPolylin getPolylin() {
		return getConnector().getCurrentPolylin();
	}

}