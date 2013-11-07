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
package org.openflexo.fge.control.tools;

import java.util.logging.Logger;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GRBinding.ConnectorGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.ConnectorGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.FGEConnectorGraphics;
import org.openflexo.fge.impl.ConnectorNodeImpl;
import org.openflexo.fge.impl.ContainerNodeImpl;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.impl.ShapeNodeImpl;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Abstract implementation for the controller of the DrawConnector tool
 * 
 * @author sylvain
 * 
 * @param <ME>
 */
public abstract class DrawConnectorToolController<ME> extends ToolController<ME> {

	private static final Logger logger = Logger.getLogger(DrawConnectorToolController.class.getPackage().getName());

	boolean drawEdge = false;
	protected ShapeNode<?> startNode = null;
	protected ShapeNode<?> endNode = null;
	// protected Point currentDraggingLocationInDrawingView;
	private CompoundEdit drawConnectorEdit;

	private ConnectorGraphicalRepresentation connectorGR;
	private ShapeGraphicalRepresentation cursorGR;

	private FGEConnectorGraphics graphics;

	public DrawConnectorToolController(DianaInteractiveEditor<?, ?, ?> controller, DrawConnectorAction toolAction) {
		super(controller, toolAction);
	}

	public abstract FGEConnectorGraphics makeGraphics(ForegroundStyle foregroundStyle);

	public FGEConnectorGraphics getGraphics() {
		return graphics;
	}

	@Override
	public DrawConnectorAction getToolAction() {
		return (DrawConnectorAction) super.getToolAction();
	}

	private ShapeNode<DrawConnectorToolController> cursorNode;
	protected ConnectorNode<DrawConnectorToolController> connectorNode;

	protected void startMouseEdition(ME e) {
		drawConnectorEdit = startRecordEdit("Draw connector");
		super.startMouseEdition(e);
		drawEdge = true;
		// currentDraggingLocationInDrawingView = new Point();

		cursorGR = getFactory().makeShapeGraphicalRepresentation(ShapeType.RECTANGLE);
		cursorGR.setBorder(getFactory().makeShapeBorder(0, 0, 0, 0));
		cursorGR.setWidth(1);
		cursorGR.setHeight(1);
		ShapeGRBinding<DrawConnectorToolController> cursorGRBinding = getController().getDrawing().bindShape(
				DrawConnectorToolController.class, "cursor", new ShapeGRProvider<DrawConnectorToolController>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(DrawConnectorToolController drawable, FGEModelFactory factory) {
						return cursorGR;
					}
				});

		connectorGR = getFactory().makeConnectorGraphicalRepresentation();
		connectorGR.setForeground(getController().getInspectedForegroundStyle().getDefaultValue());
		connectorGR.setConnectorSpecification(getController().getInspectedConnectorSpecification().getDefaultValue());

		System.out.println("foreground=" + connectorGR.getForeground().toNiceString());

		ConnectorGRBinding<DrawConnectorToolController> connectorGRBinding = getController().getDrawing().bindConnector(
				DrawConnectorToolController.class, "connector", new ConnectorGRProvider<DrawConnectorToolController>() {
					@Override
					public ConnectorGraphicalRepresentation provideGR(DrawConnectorToolController drawable, FGEModelFactory factory) {
						return connectorGR;
					}
				});

		cursorNode = new ShapeNodeImpl<DrawConnectorToolController>((DrawingImpl<?>) getController().getDrawing(), this, cursorGRBinding,
				(ContainerNodeImpl<?, ?>) getController().getDrawing().getRoot());
		connectorNode = new ConnectorNodeImpl<DrawConnectorToolController>((DrawingImpl<?>) getController().getDrawing(), this,
				connectorGRBinding, (ContainerNodeImpl<?, ?>) getController().getDrawing().getRoot(), (ShapeNodeImpl<?>) startNode,
				(ShapeNodeImpl<?>) cursorNode);

		graphics = makeGraphics(getFactory().makeDefaultForegroundStyle());

	}

	protected void stopMouseEdition() {
		System.out.println(">>>>> Hop, stop mouse edition");

		drawEdge = false;
		makeNewConnector();
		super.stopMouseEdition();
		connectorNode.delete();
		// connectorGR.delete();
		cursorNode.delete();
		// cursorGR.delete();
		connectorNode = null;
		connectorGR = null;
		cursorNode = null;
		cursorGR = null;
		stopRecordEdit(drawConnectorEdit);
		getController().setCurrentTool(EditorTool.SelectionTool);
	}

	public ConnectorNode<DrawConnectorToolController> getConnectorNode() {
		return connectorNode;
	}

	public void makeNewConnector() {
		if (getToolAction() != null && startNode != null && endNode != null) {
			getToolAction().performedDrawNewConnector(connectorGR, startNode, endNode);
		} else {
			System.out.println("toolAction=" + getToolAction());
			logger.warning("No DrawConnectorAction defined !");
		}
	}

	public void delete() {
		logger.warning("Please implement deletion for DrawConnectorToolController");
		super.delete();
	}

	@Override
	public boolean mousePressed(ME e) {
		System.out.println("mousePressed() on " + getPoint(e));
		DrawingTreeNode<?, ?> focused = getFocusedObject(e);
		if (focused instanceof ShapeNode) {
			// System.out.println("OK, je detecte une shape de depart");
			startNode = (ShapeNode<?>) focused;
			getController().clearSelection();
			startNode.setIsFocused(true);
			startMouseEdition(e);
		}
		return true;
	}

	public abstract void paintConnector();

	@Override
	public boolean mouseDragged(ME e) {
		// System.out.println("mouseDragged() on " + getPoint(e));
		if (drawEdge && startNode != null) {
			FGEPoint p = getPoint(e);
			cursorGR.setX(p.x);
			cursorGR.setY(p.y);

			// currentDraggingLocationInDrawingView.x = (int) p.x;
			// currentDraggingLocationInDrawingView.y = (int) p.y;

			DrawingTreeNode<?, ?> focused = getFocusedObject(e);
			if ((focused instanceof ShapeNode) && focused != startNode && !startNode.getAncestors().contains(focused)) {
				endNode = (ShapeNode<?>) focused;
				endNode.setIsFocused(true);
				((ConnectorNodeImpl<?>) connectorNode).setEndNode((ShapeNodeImpl<?>) endNode);
			} else {
				endNode = null;
				((ConnectorNodeImpl<?>) connectorNode).setEndNode((ShapeNodeImpl<?>) cursorNode);
			}

			connectorNode.refreshConnector();
			paintConnector();
		}
		return false;
	}

	@Override
	public boolean mouseReleased(ME e) {
		System.out.println("mouseReleased() on " + getPoint(e));
		if (drawEdge) {
			if (startNode != null && endNode != null) {
				// System.out.println("Add ConnectorSpecification contextualMenuInvoker="+contextualMenuInvoker+" point="+contextualMenuClickedPoint);
				stopMouseEdition();
			}
			if (startNode != null) {
				startNode.setIsFocused(false);
			}
			if (endNode != null) {
				endNode.setIsFocused(false);
			}
			drawEdge = false;
			startNode = null;
			endNode = null;
			paintConnector();
			getController().setCurrentTool(EditorTool.SelectionTool);
			return true;
		}
		return false;
	}

	public ShapeNode<?> getStartNode() {
		return startNode;
	}

	public ShapeNode<?> getEndNode() {
		return endNode;
	}

}
