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
package org.openflexo.fme;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.actions.DrawConnectorAction;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.exceptions.CopyException;
import org.openflexo.fge.control.exceptions.CutException;
import org.openflexo.fge.control.exceptions.PasteException;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.view.FGEView;
import org.openflexo.fme.model.Connector;
import org.openflexo.fme.model.Diagram;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.DiagramFactory;
import org.openflexo.fme.model.Shape;

public class DianaDrawingEditor extends JDianaInteractiveEditor<Diagram> {

	private static final Logger logger = Logger.getLogger(DianaDrawingEditor.class.getPackage().getName());

	private JPopupMenu contextualMenu;

	private DiagramEditor diagramEditor;

	public DianaDrawingEditor(final DiagramDrawing aDrawing, DiagramFactory factory, DiagramEditor aDiagramEditor) {
		super(aDrawing, factory, aDiagramEditor.getApplication().getToolFactory());

		this.diagramEditor = aDiagramEditor;

		DrawShapeAction drawShapeAction = new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);
				Shape newShape = getFactory().makeNewShape(graphicalRepresentation, getDrawing().getModel());
				getDrawing().getModel().addToShapes(newShape);
			}
		};

		DrawConnectorAction drawConnectorAction = new DrawConnectorAction() {

			@Override
			public void performedDrawNewConnector(ConnectorGraphicalRepresentation graphicalRepresentation, ShapeNode<?> startNode,
					ShapeNode<?> endNode) {
				System.out.println("OK, perform draw new connector with " + graphicalRepresentation + " start: " + startNode + " end: "
						+ endNode);
				Connector newConnector = getFactory().makeNewConnector(graphicalRepresentation, (Shape) startNode.getDrawable(),
						(Shape) endNode.getDrawable(), getDrawing().getModel());
				DrawingTreeNode<?, ?> fatherNode = FGEUtils.getFirstCommonAncestor(startNode, endNode);
				((DiagramElement<?, ?>) fatherNode.getDrawable()).addToConnectors(newConnector);

			}
		};

		setDrawCustomShapeAction(drawShapeAction);
		setDrawShapeAction(drawShapeAction);
		setDrawConnectorAction(drawConnectorAction);

		contextualMenu = new JPopupMenu();

		JMenuItem newConceptItem = new JMenuItem("New concept");
		newConceptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedObjects().size() == 1 && getSelectedObjects().get(0).getDrawable() instanceof DiagramElement) {
					diagramEditor.createNewConceptAndNewInstance((DiagramElement<?, ?>) getSelectedObjects().get(0).getDrawable());
				}
			}
		});
		contextualMenu.add(newConceptItem);

		JMenuItem newInstanceOfExistingConceptItem = new JMenuItem("New instance of existing concept");
		newInstanceOfExistingConceptItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (getSelectedObjects().size() == 1 && getSelectedObjects().get(0).getDrawable() instanceof DiagramElement) {
					diagramEditor.createNewInstance((DiagramElement<?, ?>) getSelectedObjects().get(0).getDrawable());
				}
			}
		});
		contextualMenu.add(newInstanceOfExistingConceptItem);

		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.setIcon(FMEIconLibrary.DELETE_ICON);
		deleteItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<DiagramElement<?, ?>> objectsToDelete = new ArrayList<DiagramElement<?, ?>>();
				for (DrawingTreeNode<?, ?> n : getSelectedObjects(ShapeNode.class, ConnectorNode.class)) {
					Object drawable = n.getDrawable();
					if (drawable instanceof DiagramElement) {
						objectsToDelete.add((DiagramElement<?, ?>) drawable);
					}
				}
				diagramEditor.delete(objectsToDelete);

			}
		});
		contextualMenu.add(deleteItem);

		contextualMenu.addSeparator();
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.setIcon(FMEIconLibrary.COPY_ICON);
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					copy();
				} catch (CopyException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.setIcon(FMEIconLibrary.PASTE_ICON);
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					paste();
				} catch (PasteException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(pasteItem);
		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.setIcon(FMEIconLibrary.CUT_ICON);
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cut();
				} catch (CutException e1) {
					e1.printStackTrace();
				}
			}
		});
		contextualMenu.add(cutItem);

	}

	@Override
	public DiagramDrawing getDrawing() {
		return (DiagramDrawing) super.getDrawing();
	}

	@Override
	public DiagramFactory getFactory() {
		return (DiagramFactory) super.getFactory();
	}

	public void showContextualMenu(DrawingTreeNode<?, ?> dtn, FGEView view, Point p) {
		contextualMenu.show((Component) view, p.x, p.y);
	}

	@Override
	public DiagramEditorView makeDrawingView() {
		return new DiagramEditorView(this);
	}

	@Override
	public DiagramEditorView getDrawingView() {
		return (DiagramEditorView) super.getDrawingView();
	}

	protected void prepareClipboardForPasting(FGEPoint proposedPastingLocation) {
		logger.info("Pasting in " + getPastingContext().getDrawable() + " at " + proposedPastingLocation);
		if (getClipboard().isSingleObject()) {
			if (getClipboard().getSingleContents() instanceof Shape) {
				Shape shapeBeingPasted = (Shape) getClipboard().getSingleContents();
				shapeBeingPasted.setName(shapeBeingPasted.getName() + "-new");
				shapeBeingPasted.getGraphicalRepresentation().setX(proposedPastingLocation.x);
				shapeBeingPasted.getGraphicalRepresentation().setY(proposedPastingLocation.y);
			} else if (getClipboard().getSingleContents() instanceof Connector) {
				Connector connectorBeingPasted = (Connector) getClipboard().getSingleContents();
				connectorBeingPasted.setName(connectorBeingPasted.getName() + "-new");
			}
		} else {
			for (Object o : getClipboard().getMultipleContents()) {
				if (o instanceof Shape) {
					((Shape) o).getGraphicalRepresentation().setX(((Shape) o).getGraphicalRepresentation().getX() + PASTE_DELTA);
					((Shape) o).getGraphicalRepresentation().setY(((Shape) o).getGraphicalRepresentation().getY() + PASTE_DELTA);
					((Shape) o).setName(((Shape) o).getName() + "-new");
				} else if (o instanceof Connector) {
					((Connector) o).setName(((Connector) o).getName() + "-new");
				}
			}
		}
	}

	@Override
	protected void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		if (getSelectedObjects().size() == 0) {
			diagramEditor.getApplication().getInspector().switchToEmptyContent();
		} else if (getSelectedObjects().size() == 1) {
			if (getSelectedObjects().get(0) instanceof RootNode) {
				diagramEditor.getApplication().getInspector().switchToEmptyContent();
			} else {
				diagramEditor.getApplication().getInspector().inspectObject(getSelectedObjects().get(0).getDrawable());
			}
		} else {
			diagramEditor.getApplication().getInspector().switchToMultipleContent();
		}
	}
}
