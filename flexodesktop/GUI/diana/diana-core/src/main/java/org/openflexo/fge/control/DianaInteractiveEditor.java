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

package org.openflexo.fge.control;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.exceptions.CopyException;
import org.openflexo.fge.control.exceptions.CutException;
import org.openflexo.fge.control.exceptions.PasteException;
import org.openflexo.fge.control.notifications.SelectionCopied;
import org.openflexo.fge.control.notifications.ToolChanged;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.control.tools.InspectedBackgroundStyle;
import org.openflexo.fge.control.tools.InspectedForegroundStyle;
import org.openflexo.fge.control.tools.InspectedShadowStyle;
import org.openflexo.fge.control.tools.InspectedTextStyle;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.model.factory.Clipboard;

/**
 * Represents an editor of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can be fully edited. A {@link DianaInteractiveEditor} generally declares some palettes.<br>
 * 
 * Additionnaly, a {@link DianaInteractiveEditor} manage clipboard operations (copy/cut/paste), and undo/redo operations
 * 
 * @author sylvain
 * 
 * @param <M>
 */
public abstract class DianaInteractiveEditor<M, F extends DianaViewFactory<F, C>, C> extends DianaInteractiveViewer<M, F, C> {

	private static final Logger logger = Logger.getLogger(DianaInteractiveEditor.class.getPackage().getName());

	public enum EditorTool {
		SelectionTool, DrawShapeTool, DrawConnectorTool, DrawTextTool
	}

	public static final int PASTE_DELTA = 10;

	private EditorTool currentTool;

	private DrawShapeToolController<?, ?> drawShapeToolController;
	private DrawShapeAction drawShapeAction;

	// private ForegroundStyle currentForegroundStyle;
	private InspectedForegroundStyle inspectedForegroundStyle;
	// private BackgroundStyle currentBackgroundStyle;
	private InspectedBackgroundStyle inspectedBackgroundStyle;
	// private TextStyle currentTextStyle;
	private InspectedTextStyle inspectedTextStyle;
	// private ShadowStyle currentShadowStyle;
	private InspectedShadowStyle inspectedShadowStyle;
	private ShapeSpecification currentShape;

	/**
	 * The clipboard beeing managed by this editor
	 */
	private Clipboard clipboard = null;

	/**
	 * The drawing tree node hosting a potential paste
	 */
	private DrawingTreeNode<?, ?> pastingContext;

	/**
	 * The location where applying paste, relative to root
	 */
	private FGEPoint pastingLocation;

	private boolean isSelectingAfterPaste = false;

	public DianaInteractiveEditor(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory, DianaToolFactory<C> toolFactory) {
		super(aDrawing, factory, dianaFactory, toolFactory, true, true, true, true, true);
		// currentForegroundStyle = factory.makeDefaultForegroundStyle();
		inspectedForegroundStyle = new InspectedForegroundStyle(this);
		inspectedBackgroundStyle = new InspectedBackgroundStyle(this);
		// currentBackgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		// currentTextStyle = factory.makeDefaultTextStyle();
		inspectedTextStyle = new InspectedTextStyle(this);
		// currentShadowStyle = factory.makeDefaultShadowStyle();
		inspectedShadowStyle = new InspectedShadowStyle(this);
		currentShape = factory.makeShape(ShapeType.RECTANGLE);
		setCurrentTool(EditorTool.SelectionTool);
		// palettes = new Vector<DrawingPalette>();
		// toolbox = new EditorToolbox(this);
	}

	public void delete() {
		super.delete();
		/*if (toolbox != null) {
			toolbox.delete();
		}
		toolbox = null;*/
		/*if (palettes != null) {
			for (DrawingPalette palette : palettes) {
				palette.delete();
			}
		}
		palettes = null;*/
	}

	protected void fireSelectionUpdated() {
		inspectedForegroundStyle.fireSelectionUpdated();
		inspectedTextStyle.fireSelectionUpdated();
		inspectedShadowStyle.fireSelectionUpdated();
		inspectedBackgroundStyle.fireSelectionUpdated();
	}

	public DrawShapeToolController<?, ?> getDrawShapeToolController() {
		return drawShapeToolController;
	}

	public EditorTool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(EditorTool aTool) {
		if (aTool != currentTool) {
			EditorTool oldTool = currentTool;
			logger.fine("Switch to tool " + aTool);
			switch (aTool) {
			case SelectionTool:
				/*if (currentTool == EditorTool.DrawShapeTool && drawShapeToolController != null) {
					drawShapeToolController.makeNewShape();
				}*/
				break;
			case DrawShapeTool:
				// if (drawShapeAction != null) {
				drawShapeToolController = getToolFactory().makeDrawPolygonToolController(this, drawShapeAction);
				// }
				break;
			case DrawConnectorTool:
				break;
			case DrawTextTool:
				break;
			default:
				break;
			}
			currentTool = aTool;
			notifyObservers(new ToolChanged(oldTool, currentTool));
		}
	}

	public InspectedForegroundStyle getInspectedForegroundStyle() {
		return inspectedForegroundStyle;
	}

	/*public ForegroundStyle getCurrentForegroundStyle() {
		return currentForegroundStyle;
	}

	public void setCurrentForegroundStyle(ForegroundStyle currentForegroundStyle) {
		this.currentForegroundStyle = currentForegroundStyle;
	}*/

	public InspectedBackgroundStyle getInspectedBackgroundStyle() {
		return inspectedBackgroundStyle;
	}

	/*public BackgroundStyle getCurrentBackgroundStyle() {
		return currentBackgroundStyle;
	}

	public void setCurrentBackgroundStyle(BackgroundStyle currentBackgroundStyle) {
		this.currentBackgroundStyle = currentBackgroundStyle;
	}*/

	/*public TextStyle getCurrentTextStyle() {
		return currentTextStyle;
	}

	public void setCurrentTextStyle(TextStyle currentTextStyle) {
		this.currentTextStyle = currentTextStyle;
	}*/

	public InspectedTextStyle getInspectedTextStyle() {
		return inspectedTextStyle;
	}

	public InspectedShadowStyle getInspectedShadowStyle() {
		return inspectedShadowStyle;
	}

	/*public ShadowStyle getCurrentShadowStyle() {
		return currentShadowStyle;
	}

	public void setCurrentShadowStyle(ShadowStyle currentShadowStyle) {
		this.currentShadowStyle = currentShadowStyle;
	}*/

	public ShapeSpecification getCurrentShape() {
		return currentShape;
	}

	public void setCurrentShape(ShapeSpecification currentShape) {
		this.currentShape = currentShape;
	}

	public DrawShapeAction getDrawShapeAction() {
		return drawShapeAction;
	}

	public void setDrawShapeAction(DrawShapeAction drawShapeAction) {
		this.drawShapeAction = drawShapeAction;
	}

	/*public Vector<DrawingPalette> getPalettes() {
		return palettes;
	}

	public void registerPalette(DrawingPalette aPalette) {
		logger.fine("Register palette for " + this);
		palettes.add(aPalette);
		aPalette.registerController(this);
		// if (getDrawingView() != null)
		// getDrawingView().registerPalette(aPalette);
	}

	public void unregisterPalette(DrawingPalette aPalette) {
		logger.fine("Un-Register palette for " + this);
		palettes.remove(aPalette);
	}*/

	public void activatePalette(DianaPalette<?, ?> aPalette) {
		if (getDrawingView() != null) {
			getDrawingView().activatePalette(aPalette);
		}
	}

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
	 * 
	 * @throws CopyException
	 */
	public Clipboard copy() throws CopyException {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to copy");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

		try {
			clipboard = getFactory().copy(objectsToBeCopied);
		} catch (Throwable e) {
			throw new CopyException(e, getFactory());
		}

		// System.out.println(clipboard.debug());

		pastingContext = FGEUtils.getFirstCommonAncestor(getSelectedObjects());
		// System.out.println("Pasting context = " + pastingContext);

		notifyObservers(new SelectionCopied(clipboard));

		return clipboard;
	}

	/**
	 * Return boolean indicating if the current selection is suitable for a PASTE action in supplied context and position
	 * 
	 * @return
	 */
	public boolean isPastable() {
		return clipboard != null && pastingContext != null;
	}

	/**
	 * Paste current Clipboard in supplied context and position
	 * 
	 * @throws PasteException
	 * 
	 */
	public void paste() throws PasteException {

		if (clipboard != null) {

			// System.out.println("Pasting in " + pastingContext + " at "+pastingLocation);
			FGEPoint p = FGEUtils.convertNormalizedPoint(getDrawing().getRoot(), pastingLocation, pastingContext);

			// This point is valid for RootNode, but need to be translated in a ShapeNode
			if (pastingContext instanceof ShapeNode) {
				p.x = p.x * ((ShapeNode<?>) pastingContext).getWidth();
				p.y = p.y * ((ShapeNode<?>) pastingContext).getHeight();
			}

			prepareClipboardForPasting(p);

			// Prevent pastingContext to be changed
			isSelectingAfterPaste = true;

			// Do the paste
			try {
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
			} catch (Throwable e) {
				throw new PasteException(e, getFactory());
			}

			// OK, now we can track again new selection to set pastingContext
			isSelectingAfterPaste = false;

			pastingLocation.x = pastingLocation.x + PASTE_DELTA;
			pastingLocation.y = pastingLocation.y + PASTE_DELTA;

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
	 * 
	 * @throws CutException
	 */
	public Clipboard cut() throws CutException {
		if (getSelectedObjects().size() == 0) {
			System.out.println("Nothing to cut");
			return null;
		}

		Object[] objectsToBeCopied = makeArrayOfObjectsToBeCopied(getSelectedObjects());

		try {
			clipboard = getFactory().cut(objectsToBeCopied);
		} catch (Throwable e) {
			throw new CutException(e, getFactory());
		}

		// System.out.println(clipboard.debug());

		return clipboard;

	}

	/**
	 * Returns true if edits may be undone.<br>
	 * If en edition is in progress, return true if stopping this edition will cause UndoManager to be able to undo
	 * 
	 * 
	 * @return true if there are edits to be undone
	 */
	public boolean canUndo() {
		return getFactory() != null && getFactory().getUndoManager() != null
				&& getFactory().getUndoManager().canUndoIfStoppingCurrentEdition();
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
	 * Returns true if edits may be redone.<br>
	 * 
	 * @return true if there are edits to be undone
	 */
	public boolean canRedo() {
		return getFactory() != null && getFactory().getUndoManager() != null && getFactory().getUndoManager().canRedo();
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
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Making copying selection with " + getSelectedObjects());
		}
		Object[] objectsToBeCopied = new Object[aSelection.size()];
		int i = 0;
		for (DrawingTreeNode<?, ?> dtn : aSelection) {
			objectsToBeCopied[i] = dtn.getDrawable();
			// System.out.println("object: " + objectsToBeCopied[i] + " gr=" + getSelectedObjects().get(i));
			// System.out.println("Copied: " + getFactory().stringRepresentation(objectsToBeCopied[i]));
			i++;
		}
		return objectsToBeCopied;
	}

	/**
	 * Return the drawing tree node hosting a potential paste
	 * 
	 * @return
	 */
	public DrawingTreeNode<?, ?> getPastingContext() {
		return pastingContext;
	}

	/**
	 * Return the location where applying paste, relative to root
	 * 
	 * @return
	 */
	public FGEPoint getPastingLocation() {
		return pastingLocation;
	}

	public void setLastClickedPoint(FGEPoint lastClickedPoint, DrawingTreeNode<?, ?> node) {

		super.setLastClickedPoint(lastClickedPoint, node);
		pastingLocation = FGEUtils.convertNormalizedPoint(node, lastClickedPoint, getDrawing().getRoot());

	};

	@Override
	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		super.addToSelectedObjects(aNode);
		if (!isSelectingAfterPaste) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Pasting context set to " + pastingContext);
			}
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
	 * This is a hook to set and/or translate some properties of clipboard beeing pasted<br>
	 * This is model-specific, and thus, default implementation does nothing. Please override this
	 * 
	 * @param proposedPastingLocation
	 */
	protected void prepareClipboardForPasting(FGEPoint proposedPastingLocation) {
	}

}
