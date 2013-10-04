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
package org.openflexo.ve.diagram;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.controller.CustomDragControlAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.controller.MouseDragControl;
import org.openflexo.foundation.view.action.AddConnector;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.action.LinkSchemeAction;
import org.openflexo.foundation.view.diagram.model.DiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.DropScheme;
import org.openflexo.foundation.view.diagram.viewpoint.LinkScheme;
import org.openflexo.localization.FlexoLocalization;

public class DrawEdgeControl extends MouseDragControl {

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;
	protected DiagramShapeGR fromShape = null;
	protected DiagramShapeGR toShape = null;

	public DrawEdgeControl() {
		super("Draw edge", MouseButton.LEFT, false, true, false, false); // CTRL DRAG
		action = new DrawEdgeAction();
	}

	protected class DrawEdgeAction extends CustomDragControlAction {
		@Override
		public boolean handleMousePressed(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (graphicalRepresentation instanceof DiagramShapeGR) {
				drawEdge = true;
				fromShape = (DiagramShapeGR) graphicalRepresentation;
				((DiagramView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(GraphicalRepresentation graphicalRepresentation, final DrawingController controller,
				MouseEvent event, boolean isSignificativeDrag) {
			if (drawEdge) {
				List<LinkScheme> availableConnectors = new ArrayList<LinkScheme>();
				if (fromShape != null && toShape != null) {
					// Lets look if we match a CalcPaletteConnector
					final DiagramShape from = fromShape.getDrawable();
					final DiagramShape to = toShape.getDrawable();
					if (from.getDiagram().getViewPoint() != null && from.getEditionPattern() != null && to.getEditionPattern() != null) {
						availableConnectors = from.getDiagramSpecification().getConnectorsMatching(from.getEditionPattern(),
								to.getEditionPattern());

					}

					if (availableConnectors.size() > 0) {
						JPopupMenu popup = new JPopupMenu();
						for (final LinkScheme linkScheme : availableConnectors) {
							// final CalcPaletteConnector connector = availableConnectors.get(linkScheme);
							// System.out.println("Available: "+paletteConnector.getEditionPattern().getName());
							JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(linkScheme.getLabel() != null ? linkScheme
									.getLabel() : linkScheme.getName()));
							menuItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									// System.out.println("Action "+paletteConnector.getEditionPattern().getName());
									LinkSchemeAction action = LinkSchemeAction.actionType.makeNewAction(from.getDiagram(), null,
											((DiagramController) controller).getVEController().getEditor());
									action.setLinkScheme(linkScheme);
									action.setFromShape(from);
									action.setToShape(to);
									action.doAction();
								}
							});
							menuItem.setToolTipText(linkScheme.getDescription());
							popup.add(menuItem);
						}
						JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey("graphical_connector_only"));
						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								AddConnector action = AddConnector.actionType.makeNewAction(from, null, ((DiagramController) controller)
										.getVEController().getEditor());
								action.setToShape(to);
								action.setAutomaticallyCreateConnector(true);
								action.doAction();
							}
						});
						menuItem.setToolTipText(FlexoLocalization
								.localizedForKey("draw_basic_graphical_connector_without_ontologic_semantic"));
						popup.add(menuItem);
						popup.show(event.getComponent(), event.getX(), event.getY());
					} else {
						AddConnector action = AddConnector.actionType.makeNewAction(from, null, ((DiagramController) controller)
								.getVEController().getEditor());
						action.setToShape(to);
						action.setAutomaticallyCreateConnector(true);
						action.doAction();
					}

				}
				
				if (fromShape != null && toShape == null) {
					// Lets look if we match a CalcPaletteConnector
					final DiagramShape from = fromShape.getDrawable();
					final DiagramShape to = null;
					if (from.getDiagram().getViewPoint() != null && from.getEditionPattern() != null) {
						for(LinkScheme availableConnector : from.getDiagramSpecification().getAllConnectors()){
							if(availableConnector.getFromTargetEditionPattern().equals(from.getEditionPattern())){
								availableConnectors.add(availableConnector);
							}
						}
					}

					if (availableConnectors.size() > 0) {
						JPopupMenu popup = new JPopupMenu();
						for (final LinkScheme linkScheme : availableConnectors) {
							// final CalcPaletteConnector connector = availableConnectors.get(linkScheme);
							// System.out.println("Available: "+paletteConnector.getEditionPattern().getName());
							JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(linkScheme.getLabel() != null ? linkScheme
									.getLabel() : linkScheme.getName()));
							menuItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									
									if(linkScheme.getToTargetEditionPattern().getDropSchemes()!=null){
										DropScheme dropScheme = (DropScheme)linkScheme.getToTargetEditionPattern().getDropSchemes().get(0);
										DropSchemeAction actionDropScheme = DropSchemeAction.actionType.makeNewAction(from.getParent(), null,
												((DiagramController) controller).getVEController().getEditor());
										//actionDropScheme.dropLocation = 
										//action.setPaletteElement(element);
										actionDropScheme.setDropScheme(dropScheme);
										actionDropScheme.doAction();
										
										
										// System.out.println("Action "+paletteConnector.getEditionPattern().getName());
										LinkSchemeAction actionLinkScheme = LinkSchemeAction.actionType.makeNewAction(from.getDiagram(), null,
												((DiagramController) controller).getVEController().getEditor());
										actionLinkScheme.setLinkScheme(linkScheme);
										actionLinkScheme.setFromShape(from);
										actionLinkScheme.setToShape(actionDropScheme.getPrimaryShape());
										actionLinkScheme.doAction();
									}
									
								}
							});
							menuItem.setToolTipText(linkScheme.getDescription());
							popup.add(menuItem);
						}
						JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey("graphical_connector_only"));
						menuItem.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								AddConnector action = AddConnector.actionType.makeNewAction(from, null, ((DiagramController) controller)
										.getVEController().getEditor());
								action.setToShape(to);
								action.setAutomaticallyCreateConnector(true);
								action.doAction();
							}
						});
						menuItem.setToolTipText(FlexoLocalization
								.localizedForKey("draw_basic_graphical_connector_without_ontologic_semantic"));
						popup.add(menuItem);
						popup.show(event.getComponent(), event.getX(), event.getY());
					} else {
						AddConnector action = AddConnector.actionType.makeNewAction(from, null, ((DiagramController) controller)
								.getVEController().getEditor());
						action.setToShape(to);
						action.setAutomaticallyCreateConnector(true);
						action.doAction();
					}

				}
				
				drawEdge = false;
				fromShape = null;
				toShape = null;
				((DiagramView) controller.getDrawingView()).setDrawEdgeAction(null);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseDragged(GraphicalRepresentation graphicalRepresentation, DrawingController controller,
				MouseEvent event) {
			if (drawEdge) {
				GraphicalRepresentation gr = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (gr instanceof DiagramShapeGR && gr != fromShape && !fromShape.getAncestors().contains(gr.getDrawable())) {
					toShape = (DiagramShapeGR) gr;
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

		public void paint(Graphics g, DrawingController controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
						fromShape.getShapeSpecification().getShapeSpecification().getCenter(), fromShape, controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller.getDrawingGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(
							toShape.getShapeSpecification().getShapeSpecification().getCenter(), toShape, controller.getScale());
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
