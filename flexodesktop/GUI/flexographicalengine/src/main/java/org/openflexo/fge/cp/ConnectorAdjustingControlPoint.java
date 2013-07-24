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
package org.openflexo.fge.cp;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;

public class ConnectorAdjustingControlPoint extends ConnectorControlPoint {

	public ConnectorAdjustingControlPoint(ConnectorNode<?> node, FGEPoint pt) {
		super(node, pt);
	}

	@Override
	public ConnectorNode<?> getNode() {
		return (ConnectorNode<?>) super.getNode();
	}

	@Override
	public Cursor getDraggingCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void startDragging(DrawingController<?> controller, FGEPoint startPoint) {
		super.startDragging(controller, startPoint);
		if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().addToTemporaryObjects(getNode());
			controller.getPaintManager().invalidate(getNode());
		}
	}

	@Override
	public boolean dragToPoint(FGEPoint newRelativePoint, FGEPoint pointRelativeToInitialConfiguration, FGEPoint newAbsolutePoint,
			FGEPoint initialPoint, MouseEvent event) {
		return true;
	}

	@Override
	public void stopDragging(DrawingController<?> controller, DrawingTreeNode<?, ?> focused) {
		super.stopDragging(controller, focused);
		if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().removeFromTemporaryObjects(getNode());
			controller.getPaintManager().invalidate(getNode());
			controller.getPaintManager().repaint(controller.getDrawingView());
		}
	}
}
