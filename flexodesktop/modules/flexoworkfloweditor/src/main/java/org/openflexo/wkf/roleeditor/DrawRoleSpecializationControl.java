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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.foundation.wkf.action.AddRoleSpecialization;
import org.openflexo.wkf.WKFCst;

public class DrawRoleSpecializationControl extends MouseDragControl {

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected RoleGR fromRole = null;
	protected RoleGR toRole = null;

	public DrawRoleSpecializationControl() {
		super("Draw edge", MouseButton.LEFT, false, true, false, false); // CTRL DRAG
		action = new DrawRoleSpecializationAction();
	}

	protected class DrawRoleSpecializationAction extends CustomDragControlAction {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (graphicalRepresentation instanceof RoleGR) {
				drawEdge = true;
				fromRole = (RoleGR) graphicalRepresentation;
				((RoleEditorView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event, boolean isSignificativeDrag) {
			if (drawEdge) {
				if (fromRole != null && toRole != null) {
					AddRoleSpecialization action = AddRoleSpecialization.actionType.makeNewAction(fromRole.getDrawable(), null,
							((RoleEditorController) controller).getWKFController().getEditor());
					action.setNewParentRole(toRole.getDrawable());
					action.setRoleSpecializationAutomaticallyCreated(true);
					action.doAction();
				}
				drawEdge = false;
				fromRole = null;
				toRole = null;
				((RoleEditorView) controller.getDrawingView()).setDrawEdgeAction(null);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (drawEdge) {
				GraphicalRepresentation gr = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (gr instanceof RoleGR && gr != fromRole && !fromRole.getAncestors().contains(gr.getDrawable())) {
					toRole = (RoleGR) gr;
				} else {
					toRole = null;
				}
				currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
						controller.getDrawingView());
				controller.getDrawingView().repaint();
				return true;
			}
			return false;
		}

		public void paint(Graphics g, DrawingController controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
						fromRole.getShape().getShapeSpecification().getCenter(), fromRole, controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toRole != null) {
					to = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
							toRole.getShape().getShapeSpecification().getCenter(), toRole, controller.getScale());
					g.setColor(WKFCst.OK);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
