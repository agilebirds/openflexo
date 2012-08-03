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
package org.openflexo.vpm.drawingshema;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.connectors.Connector.ConnectorType;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.foundation.viewpoint.AddConnector;
import org.openflexo.foundation.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.ExampleDrawingConnector;
import org.openflexo.foundation.viewpoint.LinkScheme;
import org.openflexo.foundation.viewpoint.action.AddExampleDrawingConnector;
import org.openflexo.localization.FlexoLocalization;

public class DrawEdgeControl extends MouseDragControl {

	private static final Logger logger = Logger.getLogger(DrawEdgeControl.class.getPackage().getName());

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected CalcDrawingShapeGR fromShape = null;
	protected CalcDrawingShapeGR toShape = null;

	public DrawEdgeControl() {
		super("Draw edge", MouseButton.LEFT, false, true, false, false); // CTRL DRAG
		action = new DrawEdgeAction();
	}

	protected class DrawEdgeAction extends CustomDragControlAction {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
				MouseEvent event) {
			if (graphicalRepresentation instanceof CalcDrawingShapeGR) {
				drawEdge = true;
				fromShape = (CalcDrawingShapeGR) graphicalRepresentation;
				((CalcDrawingShemaView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation<?> graphicalRepresentation, final DrawingController<?> controller,
				MouseEvent event, boolean isSignificativeDrag) {
			if (drawEdge && toShape != null) {

				if (fromShape.getDrawable().getViewPoint() != null) {
					Vector<LinkScheme> availableConnectors = fromShape.getDrawable().getViewPoint().getAllConnectors();

					if (availableConnectors.size() > 0) {
						JPopupMenu popup = new JPopupMenu();
						for (final LinkScheme linkScheme : availableConnectors) {
							JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(linkScheme.getLabel() != null ? linkScheme
									.getLabel() : linkScheme.getName()));
							menuItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									for (EditionAction a : linkScheme.getActions()) {
										if (a instanceof AddConnector) {
											ConnectorPatternRole patternRole = ((AddConnector) a).getPatternRole();
											logger.warning("Implement this !!!");
											String text = patternRole.getLabel().getBinding().toString();// Value(null);
											performAddConnector(controller,
													(ConnectorGraphicalRepresentation<?>) patternRole.getGraphicalRepresentation(), text);
											return;
										}
									}
									performAddDefaultConnector(controller);
								}
							});
							popup.add(menuItem);
						}
						JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey("graphical_connector_only"));
						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								performAddDefaultConnector(controller);
							}
						});
						popup.add(menuItem);
						popup.show(event.getComponent(), event.getX(), event.getY());
						return false;
					} else {
						performAddDefaultConnector(controller);
					}
				}

				drawEdge = false;
				fromShape = null;
				toShape = null;
				((CalcDrawingShemaView) controller.getDrawingView()).setDrawEdgeAction(null);

			}
			return false;
		}

		private void performAddDefaultConnector(DrawingController<?> controller) {
			AddExampleDrawingConnector action = AddExampleDrawingConnector.actionType.makeNewAction(fromShape.getDrawable(), null,
					((CalcDrawingShemaController) controller).getCEDController().getEditor());
			action.toShape = toShape.getDrawable();

			ConnectorGraphicalRepresentation<?> connectorGR = new ConnectorGraphicalRepresentation<ExampleDrawingConnector>();
			connectorGR.setConnectorType(ConnectorType.LINE);
			connectorGR.setIsSelectable(true);
			connectorGR.setIsFocusable(true);
			connectorGR.setIsReadOnly(false);
			connectorGR.setForeground(((CalcDrawingShemaController) controller).getCurrentForegroundStyle());
			connectorGR.setTextStyle(((CalcDrawingShemaController) controller).getCurrentTextStyle());

			action.graphicalRepresentation = connectorGR;

			action.doAction();

			drawEdge = false;
			fromShape = null;
			toShape = null;
			((CalcDrawingShemaView) controller.getDrawingView()).setDrawEdgeAction(null);

		}

		private void performAddConnector(DrawingController<?> controller, ConnectorGraphicalRepresentation<?> connectorGR, String text) {
			AddExampleDrawingConnector action = AddExampleDrawingConnector.actionType.makeNewAction(fromShape.getDrawable(), null,
					((CalcDrawingShemaController) controller).getCEDController().getEditor());
			action.toShape = toShape.getDrawable();
			action.graphicalRepresentation = connectorGR;
			action.newConnectorName = text;
			action.doAction();

			drawEdge = false;
			fromShape = null;
			toShape = null;
			((CalcDrawingShemaView) controller.getDrawingView()).setDrawEdgeAction(null);

		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation<?> graphicalRepresentation, DrawingController<?> controller,
				MouseEvent event) {
			if (drawEdge) {
				GraphicalRepresentation<?> gr = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (gr instanceof CalcDrawingShapeGR && gr != fromShape && !fromShape.getAncestors().contains(gr.getDrawable())) {
					toShape = (CalcDrawingShapeGR) gr;
				} else {
					toShape = null;
				}
				currentDraggingLocationInDrawingView = SwingUtilities.convertPoint((Component) event.getSource(), event.getPoint(),
						controller.getDrawingView());
				controller.getDrawingView().repaint();
				return true;
			}
			return false;
		}

		public void paint(Graphics g, DrawingController<?> controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
						fromShape.getShape().getShape().getCenter(), fromShape, controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
							toShape.getShape().getShape().getCenter(), toShape, controller.getScale());
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
