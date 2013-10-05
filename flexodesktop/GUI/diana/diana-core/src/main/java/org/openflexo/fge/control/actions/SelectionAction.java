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
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseClickControlAction;
import org.openflexo.fge.control.MouseClickControlAction.MouseClickControlActionType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.view.FGEView;

public class SelectionAction extends MouseClickControlActionImpl {
	@Override
	public MouseClickControlActionType getActionType() {
		return MouseClickControlActionType.SELECTION;
	}

	@Override
	public boolean handleClick(DrawingTreeNode<?, ?> node, DianaInteractiveViewer<?> controller, MouseEvent event) {
		if (controller.getDrawingView() == null) {
			return false;
		}

		if (node.getGraphicalRepresentation().getIsSelectable()) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Select " + node);
			}
			controller.setSelectedObject(node);
			if (controller.getDrawingView() == null) {
				return false;
			}
			FGEView<?> view = controller.getDrawingView().viewForNode(node);
			Point newPoint = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(), (Component) view);
			controller.setLastClickedPoint(new FGEPoint(newPoint.x / controller.getScale(), newPoint.y / controller.getScale()));
			controller.setLastSelectedGR(node);
			return false;
		} else {
			return false;
		}
	}
}