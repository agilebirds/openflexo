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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.cp.ControlArea;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectPolylin;
import org.openflexo.fge.geom.FGESegment;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.graphics.FGEGraphics;

public abstract class RectPolylinAdjustableSegment extends ControlArea<FGESegment> {
	protected FGERectPolylin initialPolylin;
	private RectPolylinConnector connector;

	public RectPolylinAdjustableSegment(FGESegment segment, RectPolylinConnector connector) {
		super(connector.getGraphicalRepresentation(), segment);
		this.connector = connector;
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
		if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().addToTemporaryObjects(getGraphicalRepresentation());
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
		}
		initialPolylin = getPolylin().clone();
		getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public void stopDragging(DrawingController<?> controller, GraphicalRepresentation<?> focusedGR) {
		super.stopDragging(controller, focusedGR);
		if (controller.getPaintManager().isPaintingCacheEnabled()) {
			controller.getPaintManager().removeFromTemporaryObjects(getGraphicalRepresentation());
			controller.getPaintManager().invalidate(getGraphicalRepresentation());
			controller.getPaintManager().repaint(controller.getDrawingView());
		}
		getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public Cursor getDraggingCursor() {
		SimplifiedCardinalDirection orientation = getArea().getApproximatedOrientation();
		if (orientation != null) {
			if (orientation.isHorizontal()) {
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			}
			if (orientation.isVertical()) {
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			}
		}
		return null;
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Rectangle paint(FGEGraphics graphics) {
		return null;
		/*if (true) return null;
		Point p1 = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(getArea().getP1(), getGraphicalRepresentation(), drawingView.getScale());
		Point p2 = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(getArea().getP2(), getGraphicalRepresentation(), drawingView.getScale());
		//System.out.println("Peint le segment: "+p1+"-"+p2);
		graphics.setColor(Color.GREEN);
		graphics.drawLine(p1.x,p1.y,p2.x,p2.y);
		return new Rectangle(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));*/
	}

	public RectPolylinConnector getConnector() {
		return connector;
	}

	@Override
	public ConnectorGraphicalRepresentation<?> getGraphicalRepresentation() {
		return connector.getGraphicalRepresentation();
	}

	public FGERectPolylin getPolylin() {
		return getConnector().getCurrentPolylin();
	}
}
