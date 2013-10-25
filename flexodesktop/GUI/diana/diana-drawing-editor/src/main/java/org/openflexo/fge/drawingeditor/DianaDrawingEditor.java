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
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.notifications.SelectionCopied;
import org.openflexo.fge.drawingeditor.model.Connector;
import org.openflexo.fge.drawingeditor.model.Diagram;
import org.openflexo.fge.drawingeditor.model.DiagramElement;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.fge.view.FGEView;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.exceptions.ModelExecutionException;
import org.openflexo.model.factory.Clipboard;

public class DianaDrawingEditor extends JDianaInteractiveEditor<Diagram> {

	private static final Logger logger = Logger.getLogger(DianaDrawingEditor.class.getPackage().getName());

	private JPopupMenu contextualMenu;

	// private DrawingTreeNode<?, ?> contextualMenuInvoker;
	// private Point contextualMenuClickedPoint;

	// private Shape copiedShape;

	public DianaDrawingEditor(final DiagramDrawing aDrawing, DiagramFactory factory, SwingToolFactory toolFactory) {
		super(aDrawing, factory, toolFactory);

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
					Shape newShape = getFactory().makeNewShape(st, new FGEPoint(getLastClickedPoint()), getDrawing().getModel());
					getDrawing().getModel().addToShapes(newShape);
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
				paste(/*contextualMenuInvoker,
						FGEUtils.convertNormalizedPoint(getLastSelectedNode(), getLastClickedPoint(), contextualMenuInvoker)*/);
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

		contextualMenu.addSeparator();

		JMenuItem undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		contextualMenu.add(undoItem);
		JMenuItem redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});
		contextualMenu.add(redoItem);
		// initPalette();
	}

	@Override
	public DiagramDrawing getDrawing() {
		return (DiagramDrawing) super.getDrawing();
	}

	@Override
	public DiagramFactory getFactory() {
		return (DiagramFactory) super.getFactory();
	}

	/*private void initPalette() {
		paletteModel = new DiagramEditorPalette(this);
		palette = (JDianaPalette) getToolFactory().makeDianaPalette(paletteModel);
		palette.setEditor(this);
		activatePalette(palette);
	}*/

	// private DiagramEditorPalette paletteModel;
	// private JDianaPalette palette;

	/*public DiagramEditorPalette getPaletteModel() {
		return paletteModel;
	}*/

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
		// contextualMenuInvoker = dtn;
		// contextualMenuClickedPoint = p;
		contextualMenu.show((Component) view, p.x, p.y);
	}

	/*@Override
	public JDrawingView<DiagramDrawing> makeDrawingView() {
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

	private Clipboard clipboard = null;

	public Clipboard getClipboard() {
		return clipboard;
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a COPY action
	 * 
	 * @return
	 */
	public boolean isCopiable() {
		if (getSelectedObjects().size() == 1) {
			if (getSelectedObjects().get(0) == getDrawing().getRoot()) {
				return false;
			}
			return true;
		}
		return getSelectedObjects().size() > 0;
	}

	/**
	 * Copy current selection in the clipboard
	 */
	public Clipboard copy() {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to copy");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

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

		pastingContext = FGEUtils.getFirstCommonAncestor(getSelectedObjects());
		System.out.println("Pasting context = " + pastingContext);

		notifyObservers(new SelectionCopied(clipboard));

		/*Shape shapeToCopy = (Shape) objectsToBeCopied[0];

		System.out.println("Je viens de copier l'objet " + shapeToCopy);
		System.out.println("gr=" + shapeToCopy.getGraphicalRepresentation());
		System.out.println("ss=" + shapeToCopy.getGraphicalRepresentation().getShapeSpecification());
		System.out.println("controls=" + shapeToCopy.getGraphicalRepresentation().getMouseClickControls());

		Shape copiedShape = (Shape) clipboard.getContents();
		System.out.println("J'obtiens " + copiedShape);
		System.out.println("gr=" + copiedShape.getGraphicalRepresentation());
		System.out.println("ss=" + copiedShape.getGraphicalRepresentation().getShapeSpecification());
		System.out.println("controls=" + copiedShape.getGraphicalRepresentation().getMouseClickControls());*/

		return clipboard;
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a PASTE action in supplied context and position
	 * 
	 * @return
	 */
	public boolean isPastable() {
		System.out.println("pastable ?");
		System.out.println("clipboard=" + clipboard);
		System.out.println("pastingContext=" + pastingContext);
		return clipboard != null && pastingContext != null;
	}

	/**
	 * The drawing tree node hosting a potential paste
	 */
	private DrawingTreeNode<?, ?> pastingContext;

	/**
	 * The location where applying paste, relative to root
	 */
	private FGEPoint pastingLocation;

	private boolean isSelectingAfterPaste = false;

	public void setLastClickedPoint(FGEPoint lastClickedPoint, DrawingTreeNode<?, ?> node) {

		System.out.println("On me dit avoir clique en " + lastClickedPoint + " par rapport a " + node);

		super.setLastClickedPoint(lastClickedPoint, node);
		pastingLocation = FGEUtils.convertNormalizedPoint(node, lastClickedPoint, getDrawing().getRoot());

		System.out.println("Par rapport au root, je suis donc en " + pastingLocation);

	};

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		super.addToSelectedObjects(aNode);
		if (!isSelectingAfterPaste) {
			System.out.println("le pasting context devient " + pastingContext);
			pastingContext = aNode;
		}
	}

	@Override
	public void clearSelection() {
		super.clearSelection();
		if (!isSelectingAfterPaste) {
			pastingContext = getDrawing().getRoot();
		}
	}

	/**
	 * Paste current Clipboard in supplied context and position
	 * 
	 */
	public void paste() {

		if (clipboard != null) {

			// addNewShape((Shape) copiedShape.clone(), (DiagramElement) contextualMenuInvoker.getDrawable());

			// If current selection is contained in original contents, paste on container
			/*if (clipboard.doesOriginalContentsContains(context)) {
				System.out.println("ooops, c'est pas une bonne idee de paster en " + context + " p=" + p);
				p = FGEUtils.convertNormalizedPoint(context, p, context.getParentNode());
				p.x = p.x + 20;
				p.y = p.y + 20;
				context = context.getParentNode();
				System.out.println("Je propose de paster en " + context + " p=" + p);
			}*/

			System.out.println("Pasting at " + pastingContext);
			System.out.println("Je paste en " + pastingLocation);
			FGEPoint p = FGEUtils.convertNormalizedPoint(getDrawing().getRoot(), pastingLocation, pastingContext);
			System.out.println("Du coup, par rapport a mon container je suis en " + p);

			if (pastingContext instanceof ShapeNode) {
				p.x = p.x * ((ShapeNode<?>) pastingContext).getWidth();
				p.y = p.y * ((ShapeNode<?>) pastingContext).getHeight();
			}
			System.out.println("Je les recale en " + p);

			try {
				logger.info("Pasting in " + pastingContext.getDrawable() + " at " + p);
				if (clipboard.isSingleObject()) {
					if (clipboard.getSingleContents() instanceof Shape) {
						((Shape) clipboard.getSingleContents()).getGraphicalRepresentation().setX(p.x);
						((Shape) clipboard.getSingleContents()).getGraphicalRepresentation().setY(p.y);
					}
				} else {
					for (Object o : clipboard.getMultipleContents()) {
						if (o instanceof Shape) {
							((Shape) o).getGraphicalRepresentation().setX(((Shape) o).getGraphicalRepresentation().getX() + 20);
							((Shape) o).getGraphicalRepresentation().setY(((Shape) o).getGraphicalRepresentation().getY() + 20);
							((Shape) o).setName(((Shape) o).getName() + "-new");
						} else if (o instanceof Connector) {
							((Connector) o).setName(((Connector) o).getName() + "-new");
						}
					}
				}

				// getLastClickedPoint();

				// Prevent pastingContext to be changed
				isSelectingAfterPaste = true;

				// Do the paste
				Object pasted = getFactory().paste(clipboard, pastingContext.getDrawable());

				// Try to select newly created objects
				clearSelection();
				if (clipboard.isSingleObject()) {
					addToSelectedObjects(getDrawing().getDrawingTreeNode(pasted));
				} else {
					for (Object o : (List<?>) pasted) {
						addToSelectedObjects(getDrawing().getDrawingTreeNode(o));
					}
				}
				isSelectingAfterPaste = false;

				pastingLocation.x = pastingLocation.x + 20;
				pastingLocation.y = pastingLocation.y + 20;

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
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a CUT action
	 * 
	 * @return
	 */
	public boolean isCutable() {
		return isCopiable();
	}

	/**
	 * Cut current selection, by deleting selecting contents while copying it in the clipboard for a future use
	 */
	public Clipboard cut() {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to cut");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

		try {
			clipboard = getFactory().cut(objectsToBeCopied);
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

		return clipboard;

	}

	/**
	 * Undoes appropriate edit
	 */
	public void undo() {
		System.out.println("UNDO called !!!");
		getFactory().getUndoManager().debug();
		if (getFactory().getUndoManager().canUndo()) {
			System.out.println("Effectivement, je peux faire un undo de: "
					+ getFactory().getUndoManager().editToBeUndone().getPresentationName());
			getFactory().getUndoManager().undo();
		} else {
			getFactory().getUndoManager().stopRecording(getFactory().getUndoManager().getCurrentEdition());
			System.out.println("Bon, j'arrete de force");
			if (getFactory().getUndoManager().canUndo()) {
				System.out.println("Effectivement, je peux faire un undo de: "
						+ getFactory().getUndoManager().editToBeUndone().getPresentationName());
				getFactory().getUndoManager().undo();
			}
		}
	}

	/**
	 * Redoes appropriate edit
	 */
	public void redo() {
		System.out.println("REDO called !!!");
		if (getFactory().getUndoManager().canRedo()) {
			System.out.println("Effectivement, je peux faire un redo de: "
					+ getFactory().getUndoManager().editToBeRedone().getPresentationName());
			getFactory().getUndoManager().redo();
		}
	}

	/**
	 * Internal method used to build an array with drawable objects
	 * 
	 * @param aSelection
	 * @return
	 */
	private Object[] makeArrayOfObjectsToBeCopied(List<DrawingTreeNode<?, ?>> aSelection) {
		System.out.println("Making copying selection with " + getSelectedObjects());
		Object[] objectsToBeCopied = new Object[aSelection.size()];
		int i = 0;
		for (DrawingTreeNode<?, ?> dtn : aSelection) {
			objectsToBeCopied[i] = getSelectedObjects().get(i).getDrawable();
			System.out.println("object: " + objectsToBeCopied[i] + " gr=" + getSelectedObjects().get(i));
			// System.out.println("Copied: " + serializer.serializeAsString(objectsToBeCopied[i]));
			System.out.println("Copied: " + getFactory().stringRepresentation(objectsToBeCopied[i]));
			i++;
		}
		return objectsToBeCopied;
	}

}
