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
package org.openflexo.fge.drawingeditor;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramElement;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.FGEView;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.factory.Clipboard;

public class DianaEditor extends DianaInteractiveEditor<Diagram> {

	private JPopupMenu contextualMenu;
	private DrawingTreeNode<?, ?> contextualMenuInvoker;
	private Point contextualMenuClickedPoint;

	// private Shape copiedShape;

	public DianaEditor(final DiagramDrawing aDrawing, DiagramFactory factory) {
		super(aDrawing, factory);

		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);
				/*Shape newShape = getDrawing().getModel().getFactory()
						.makeNewShape(graphicalRepresentation, graphicalRepresentation.getLocation(), getDrawing());
				if (parentGraphicalRepresentation != null && parentGraphicalRepresentation.getDrawable() instanceof DiagramElement) {
					addNewShape(newShape, (DiagramElement) parentGraphicalRepresentation.getDrawable());
				} else {
					addNewShape(newShape, (Diagram) getDrawingGraphicalRepresentation().getDrawable());
				}*/
			}
		});
		contextualMenu = new JPopupMenu();
		for (final ShapeType st : ShapeType.values()) {
			JMenuItem menuItem = new JMenuItem("Add " + st.name());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Shape newShape = getFactory().makeNewShape(st, new FGEPoint(contextualMenuClickedPoint), getDrawing());
					addNewShape(newShape, (DiagramElement) contextualMenuInvoker.getDrawable());
				}
			});
			contextualMenu.add(menuItem);
		}
		contextualMenu.addSeparator();
		JMenuItem copyItem = new JMenuItem("Copy");
		copyItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copy();
			}
		});
		contextualMenu.add(copyItem);
		JMenuItem pasteItem = new JMenuItem("Paste");
		pasteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		});
		contextualMenu.add(pasteItem);
		JMenuItem cutItem = new JMenuItem("Cut");
		cutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cut();
			}
		});
		contextualMenu.add(cutItem);
		initPalette();
	}

	@Override
	public DiagramDrawing getDrawing() {
		return (DiagramDrawing) super.getDrawing();
	}

	@Override
	public DiagramFactory getFactory() {
		return (DiagramFactory) super.getFactory();
	}

	private void initPalette() {
		// TODO Auto-generated method stub
		_palette = new DiagramEditorPalette(getFactory());
		registerPalette(_palette);
		activatePalette(_palette);
	}

	private DiagramEditorPalette _palette;

	public DiagramEditorPalette getPalette() {
		return _palette;
	}

	@Deprecated
	public void addNewShape(Shape aShape, DiagramElement father) {
		father.addToShapes(aShape);
		// aShape.getGraphicalRepresentation().extendParentBoundsToHostThisShape();
		// getDrawing().addDrawable(aShape,
		// contextualMenuInvoker.getDrawable());
	}

	@Deprecated
	public void addNewConnector(Connector aConnector, DiagramElement father) {
		// ShapeGraphicalRepresentation startObject = aConnector.getStartObject();
		// ShapeGraphicalRepresentation endObject = aConnector.getEndObject();
		// GraphicalRepresentation fatherGR = FGEUtils.getFirstCommonAncestor(startObject, endObject);
		// ((DiagramElement) fatherGR.getDrawable()).addToChilds(aConnector);
		// getDrawing().addDrawable(aConnector, fatherGR.getDrawable());
		father.addToConnectors(aConnector);
	}

	public void showContextualMenu(DrawingTreeNode<?, ?> dtn, FGEView view, Point p) {
		contextualMenuInvoker = dtn;
		contextualMenuClickedPoint = p;
		contextualMenu.show((Component) view, p.x, p.y);
	}

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> anObject) {
		super.addToSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0).getGraphicalRepresentation(), null));
		} else {
			setChanged();
			notifyObservers(new MultipleSelection());
		}
	}

	@Override
	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> anObject) {
		super.removeFromSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0).getGraphicalRepresentation(), null));
		} else {
			setChanged();
			notifyObservers(new MultipleSelection());
		}
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		notifyObservers(new EmptySelection());
	}

	@Override
	public void selectDrawing() {
		super.selectDrawing();
		setChanged();
		notifyObservers(new UniqueSelection(getDrawing().getRoot(), null));
	}

	/*@Override
	public DrawingView<DiagramDrawing> makeDrawingView() {
		return new DiagramEditorView(drawing, this);
	}*/

	@Override
	public DiagramEditorView makeDrawingView() {
		return new DiagramEditorView(this);
	}

	@Override
	public DiagramEditorView getDrawingView() {
		return (DiagramEditorView) super.getDrawingView();
	}

	private Clipboard clipboard;

	public void copy() {
		/*if (contextualMenuInvoker instanceof MyShapeGraphicalRepresentation) {
			Shape copiedShape = (Shape) ((MyShapeGraphicalRepresentation) getFocusedObjects().firstElement()).getDrawable().clone();
			System.out.println("Copied: " + copiedShape);

			XMLSerializer serializer = copiedShape.getDrawing().getEditedDrawing().getModel().getFactory().getSerializer();
			System.out.println("Hop: " + serializer.serializeAsString(copiedShape));

			try {
				clipboard = getDrawing().getModel().getFactory().copy(copiedShape);
			} catch (ModelExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}*/

		System.out.println("Selected objects = " + getSelectedObjects());
		Object[] objectsToBeCopied = new Object[getSelectedObjects().size()];

		// XMLSerializer serializer = getDrawing().getModel().getFactory().getSerializer();
		int i = 0;
		for (DrawingTreeNode<?, ?> dtn : getSelectedObjects()) {
			objectsToBeCopied[i] = getSelectedObjects().get(i).getDrawable();
			System.out.println("object: " + objectsToBeCopied[i] + " gr=" + getSelectedObjects().get(i));
			// System.out.println("Copied: " + serializer.serializeAsString(objectsToBeCopied[i]));
			System.out.println("Copied: " + getFactory().stringRepresentation(objectsToBeCopied[i]));
			i++;
		}

		try {
			clipboard = getFactory().copy(objectsToBeCopied);
		} catch (ModelExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(clipboard.debug());

	}

	public void paste() {
		System.out.println("Paste in " + contextualMenuInvoker.getDrawable());
		// addNewShape((Shape) copiedShape.clone(), (DiagramElement) contextualMenuInvoker.getDrawable());

		try {
			getFactory().paste(clipboard, contextualMenuInvoker.getDrawable());
		} catch (ModelExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void cut() {

	}

}
