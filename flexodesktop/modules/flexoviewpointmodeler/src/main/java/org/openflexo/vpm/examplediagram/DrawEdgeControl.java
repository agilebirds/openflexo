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
package org.openflexo.vpm.examplediagram;

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

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.ConnectorSpecification.ConnectorType;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.control.actions.MouseDragControlActionImpl;
import org.openflexo.fge.control.actions.MouseDragControlImpl;
import org.openflexo.fge.swing.control.JMouseControlContext;
import org.openflexo.foundation.view.diagram.viewpoint.ConnectorPatternRole;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramConnector;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramFactory;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramObject;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramShape;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddExampleDiagramConnector;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.undo.CompoundEdit;

public class DrawEdgeControl extends MouseDragControlImpl<ExampleDiagramEditor> {

	private static final Logger logger = Logger.getLogger(DrawEdgeControl.class.getPackage().getName());

	protected Point currentDraggingLocationInDrawingView = null;
	protected boolean drawEdge = false;

	public DrawEdgeControl(ExampleDiagramFactory factory) {
		super("Draw edge", MouseButton.LEFT, new DrawEdgeAction(factory), false, true, false, false, factory); // CTRL-DRAG
	}

	protected static class DrawEdgeAction extends MouseDragControlActionImpl<ExampleDiagramEditor> {

		Point currentDraggingLocationInDrawingView = null;
		boolean drawEdge = false;
		ShapeNode<ExampleDiagramShape> fromShape = null;
		ShapeNode<ExampleDiagramShape> toShape = null;
		private ExampleDiagramFactory factory;

		public DrawEdgeAction(ExampleDiagramFactory factory) {
			this.factory = factory;
		}

		@Override
		public boolean handleMousePressed(DrawingTreeNode<?, ?> node, ExampleDiagramEditor controller, MouseControlContext context) {
			if (node instanceof ShapeNode) {
				drawEdge = true;
				fromShape = (ShapeNode<ExampleDiagramShape>) node;
				((ExampleDiagramView) controller.getDrawingView()).setDrawEdgeAction(this);
				return true;
			}
			return false;
		}

		@Override
		public boolean handleMouseReleased(DrawingTreeNode<?, ?> node, final ExampleDiagramEditor controller, MouseControlContext context,
				boolean isSignificativeDrag) {
			if (drawEdge) {
				if (fromShape != null && toShape != null) {

					// VINCENT: I comment this because I tried on huge viewpoints with many link schemes, and this is not easy to use.
					// Most of the case what we want to reuse is the shape of connector pattern roles, so, I change the code to display only
					// the
					// shapes
					// of the connector pattern roles available for this virtual model
					if (fromShape.getDrawable().getVirtualModel() != null) {
						Vector<EditionPattern> availableEditionPatterns = fromShape.getDrawable().getVirtualModel().getEditionPatterns();
						Vector<ConnectorPatternRole> aivalableConnectorPatternRoles = new Vector<ConnectorPatternRole>();
						for (EditionPattern editionPattern : availableEditionPatterns) {
							if (editionPattern.getConnectorPatternRoles() != null) {
								aivalableConnectorPatternRoles.addAll(editionPattern.getConnectorPatternRoles());
							}
						}

						if (aivalableConnectorPatternRoles.size() > 0) {
							JPopupMenu popup = new JPopupMenu();
							for (final ConnectorPatternRole connectorPatternRole : aivalableConnectorPatternRoles) {
								JMenuItem menuItem = new JMenuItem(FlexoLocalization.localizedForKey(connectorPatternRole
										.getEditionPattern().getName()));
								menuItem.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										String text = connectorPatternRole.getLabel().getExpression().toString();// Value(null);
										performAddConnector(controller, connectorPatternRole.getGraphicalRepresentation(), text);
										return;
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
							popup.show((Component) context.getSource(), context.getPoint().x, context.getPoint().y);
							return false;
						} else {
							performAddDefaultConnector(controller);
						}
					}

					/*if (fromShape.getDrawable().getVirtualModel() != null) {
						Vector<LinkScheme> availableConnectors = fromShape.getDrawable().getVirtualModel().getAllConnectors();

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
												String text = patternRole.getLabel().getExpression().toString();// Value(null);
												performAddConnector(controller, patternRole.getGraphicalRepresentation(), text);
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
					}*/

					// System.out.println("Add ConnectorSpecification contextualMenuInvoker="+contextualMenuInvoker+" point="+contextualMenuClickedPoint);
					CompoundEdit drawEdge = factory.getUndoManager().startRecording("Draw edge");
					ExampleDiagramConnector newConnector = factory.makeNewConnector(fromShape.getDrawable(), toShape.getDrawable(),
							controller.getDrawing().getModel());
					DrawingTreeNode<?, ?> fatherNode = FGEUtils.getFirstCommonAncestor(fromShape, toShape);
					((ExampleDiagramObject) fatherNode.getDrawable()).addToChilds(newConnector);
					System.out.println("Add new connector !");
					factory.getUndoManager().stopRecording(drawEdge);
					((ExampleDiagramEditor) controller).setSelectedObject(controller.getDrawing().getDrawingTreeNode(newConnector));
				}
				drawEdge = false;
				fromShape = null;
				toShape = null;
				((ExampleDiagramView) controller.getDrawingView()).setDrawEdgeAction(null);
				return true;
			}
			return false;

		}

		private void performAddDefaultConnector(ExampleDiagramEditor controller) {
			CompoundEdit drawEdgeEdit = factory.getUndoManager().startRecording("Draw edge");
			AddExampleDiagramConnector action = AddExampleDiagramConnector.actionType.makeNewAction(fromShape.getDrawable(), null,
					((ExampleDiagramEditor) controller).getVPMController().getEditor());
			action.toShape = toShape.getDrawable();

			ConnectorGraphicalRepresentation connectorGR = factory.makeConnectorGraphicalRepresentation();
			connectorGR.setConnectorType(ConnectorType.LINE);
			connectorGR.setIsSelectable(true);
			connectorGR.setIsFocusable(true);
			connectorGR.setIsReadOnly(false);
			connectorGR.setForeground(((ExampleDiagramEditor) controller).getInspectedForegroundStyle().cloneStyle());
			connectorGR.setTextStyle(((ExampleDiagramEditor) controller).getInspectedTextStyle().cloneStyle());

			action.graphicalRepresentation = connectorGR;

			action.doAction();

			System.out.println("Added new connector !");
			factory.getUndoManager().stopRecording(drawEdgeEdit);
			((ExampleDiagramEditor) controller).setSelectedObject(controller.getDrawing().getDrawingTreeNode(action.getNewConnector()));

			drawEdge = false;
			fromShape = null;
			toShape = null;
			((ExampleDiagramView) controller.getDrawingView()).setDrawEdgeAction(null);

		}

		private void performAddConnector(ExampleDiagramEditor controller, ConnectorGraphicalRepresentation connectorGR, String text) {
			CompoundEdit drawEdgeEdit = factory.getUndoManager().startRecording("Draw edge");
			AddExampleDiagramConnector action = AddExampleDiagramConnector.actionType.makeNewAction(fromShape.getDrawable(), null,
					((ExampleDiagramEditor) controller).getVPMController().getEditor());
			action.toShape = toShape.getDrawable();
			action.graphicalRepresentation = connectorGR;
			action.newConnectorName = text;
			action.doAction();

			System.out.println("Added new connector !");
			factory.getUndoManager().stopRecording(drawEdgeEdit);
			((ExampleDiagramEditor) controller).setSelectedObject(controller.getDrawing().getDrawingTreeNode(action.getNewConnector()));

			drawEdge = false;
			fromShape = null;
			toShape = null;
			((ExampleDiagramView) controller.getDrawingView()).setDrawEdgeAction(null);

		}

		@Override
		public boolean handleMouseDragged(DrawingTreeNode<?, ?> node, ExampleDiagramEditor controller, MouseControlContext context) {
			if (drawEdge) {
				MouseEvent event = ((JMouseControlContext) context).getMouseEvent();
				DrawingTreeNode<?, ?> dtn = controller.getDrawingView().getFocusRetriever().getFocusedObject(event);
				if (dtn instanceof ShapeNode && dtn != fromShape && !fromShape.getAncestors().contains(dtn)) {
					toShape = (ShapeNode<ExampleDiagramShape>) dtn;
				} else {
					toShape = null;
				}
				currentDraggingLocationInDrawingView = getPointInDrawingView(controller, context);
				controller.getDrawingView().getPaintManager().repaint(controller.getDrawingView());
				return true;
			}
			return false;
		}

		public void paint(Graphics g, AbstractDianaEditor controller) {
			if (drawEdge && currentDraggingLocationInDrawingView != null) {
				Point from = controller
						.getDrawing()
						.getRoot()
						.convertRemoteNormalizedPointToLocalViewCoordinates(fromShape.getShape().getShape().getCenter(), fromShape,
								controller.getScale());
				Point to = currentDraggingLocationInDrawingView;
				if (toShape != null) {
					to = controller
							.getDrawing()
							.getRoot()
							.convertRemoteNormalizedPointToLocalViewCoordinates(toShape.getShape().getShape().getCenter(), toShape,
									controller.getScale());
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawLine(from.x, from.y, to.x, to.y);
			}
		}
	}

}
