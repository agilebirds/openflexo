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
package org.openflexo.fge.control.actions;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.fge.geom.FGELine;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.JDrawingView;

public class ZoomAction extends MouseDragControlActionImpl {

	private Point startPoint;
	private double initialScale;
	private static final double PIXEL_TO_PERCENT = 0.005;
	private FGELine refLine;

	@Override
	public MouseDragControlActionType getActionType() {
		return MouseDragControlActionType.ZOOM;
	}

	@Override
	public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller, MouseEvent event) {
		Point currentMousePositionInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
				((JDrawingView<?>) controller.getDrawingView()));
		SimplifiedCardinalDirection card = FGEPoint.getSimplifiedOrientation(new FGEPoint(startPoint), new FGEPoint(
				currentMousePositionInDrawingView));
		boolean isPositive = true;
		double distance = 0.0;
		switch (card) {
		case NORTH:
		case WEST:
			isPositive = false;
			break;
		}
		// We compute a distance to this refline instead of a distance to the start point so that that there is no gap when going from
		// positive to negative
		distance = refLine.ptLineDist(currentMousePositionInDrawingView);
		double newScale = initialScale;
		if (isPositive) {
			newScale += distance * PIXEL_TO_PERCENT;
		} else {
			newScale -= distance * PIXEL_TO_PERCENT;
		}
		controller.setScale(newScale);
		return true;
	}

	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller, MouseEvent event) {
		startPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
				((JDrawingView<?>) controller.getDrawingView()));
		// Virtual line that goes through the start point and its orientation is NORTH_EAST (or SOUTH_WEST, it's the same)
		refLine = new FGELine(new FGEPoint(startPoint), new FGEPoint(startPoint.x + 1, startPoint.y - 1));
		initialScale = controller.getScale();
		return true;
	}

	@Override
	public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?, ?, ?> controller, MouseEvent event,
			boolean isSignificativeDrag) {
		startPoint = null;
		refLine = null;
		initialScale = controller.getScale();
		return true;
	}
}