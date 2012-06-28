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

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawShapeAction;
import org.openflexo.fge.controller.DrawingController;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.inspector.selection.EmptySelection;
import org.openflexo.inspector.selection.MultipleSelection;
import org.openflexo.inspector.selection.UniqueSelection;

public class MyDrawingController extends DrawingController<EditedDrawing> {
	private JPopupMenu contextualMenu;
	private GraphicalRepresentation<?> contextualMenuInvoker;
	private Point contextualMenuClickedPoint;

	private MyShape copiedShape;

	public MyDrawingController(final EditedDrawing aDrawing) {
		super(aDrawing);
		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation<?> graphicalRepresentation,
					GraphicalRepresentation<?> parentGraphicalRepresentation) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " et parent: "
						+ parentGraphicalRepresentation);
				MyShape newShape = new MyShape(graphicalRepresentation, graphicalRepresentation.getLocation(), getDrawing());
				if (parentGraphicalRepresentation != null && parentGraphicalRepresentation.getDrawable() instanceof MyDrawingElement) {
					addNewShape(newShape, (MyDrawingElement) parentGraphicalRepresentation.getDrawable());
				} else {
					addNewShape(newShape, (MyDrawing) getDrawingGraphicalRepresentation().getDrawable());
				}
			}
		});
		contextualMenu = new JPopupMenu();
		for (final ShapeType st : ShapeType.values()) {
			JMenuItem menuItem = new JMenuItem("Add " + st.name());
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MyShape newShape = new MyShape(st, new FGEPoint(contextualMenuClickedPoint), getDrawing());
					addNewShape(newShape, (MyDrawingElement) contextualMenuInvoker.getDrawable());
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

	private void initPalette() {
		// TODO Auto-generated method stub
		_palette = new MyDrawingPalette();
		registerPalette(_palette);
		activatePalette(_palette);
	}

	private MyDrawingPalette _palette;

	public MyDrawingPalette getPalette() {
		return _palette;
	}

	public void addNewShape(MyShape aShape, MyDrawingElement father) {
		father.addToChilds(aShape);
		// getDrawing().addDrawable(aShape,
		// contextualMenuInvoker.getDrawable());
	}

	public void addNewConnector(MyConnector aConnector) {
		ShapeGraphicalRepresentation startObject = aConnector.getStartObject();
		ShapeGraphicalRepresentation endObject = aConnector.getEndObject();
		GraphicalRepresentation fatherGR = GraphicalRepresentation.getFirstCommonAncestor(startObject, endObject);
		((MyDrawingElement) fatherGR.getDrawable()).addToChilds(aConnector);
		// getDrawing().addDrawable(aConnector, fatherGR.getDrawable());
	}

	public void showContextualMenu(GraphicalRepresentation gr, FGEView view, Point p) {
		contextualMenuInvoker = gr;
		contextualMenuClickedPoint = p;
		contextualMenu.show((Component) view, p.x, p.y);
	}

	@Override
	public void addToSelectedObjects(GraphicalRepresentation anObject) {
		super.addToSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
		} else {
			setChanged();
			notifyObservers(new MultipleSelection());
		}
	}

	@Override
	public void removeFromSelectedObjects(GraphicalRepresentation anObject) {
		super.removeFromSelectedObjects(anObject);
		if (getSelectedObjects().size() == 1) {
			setChanged();
			notifyObservers(new UniqueSelection(getSelectedObjects().get(0), null));
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
		notifyObservers(new UniqueSelection(getDrawingGraphicalRepresentation(), null));
	}

	@Override
	public DrawingView<EditedDrawing> makeDrawingView(EditedDrawing drawing) {
		return new MyDrawingView(drawing, this);
	}

	@Override
	public MyDrawingView getDrawingView() {
		return (MyDrawingView) super.getDrawingView();
	}

	public void copy() {
		if (contextualMenuInvoker instanceof MyShapeGraphicalRepresentation) {
			copiedShape = (MyShape) ((MyShapeGraphicalRepresentation) getFocusedObjects().firstElement()).getDrawable().clone();
			System.out.println("Copied: " + copiedShape);
		}
	}

	public void paste() {
		System.out.println("Paste in " + contextualMenuInvoker.getDrawable());
		addNewShape((MyShape) copiedShape.clone(), (MyDrawingElement) contextualMenuInvoker.getDrawable());
	}

	public void cut() {

	}

}
