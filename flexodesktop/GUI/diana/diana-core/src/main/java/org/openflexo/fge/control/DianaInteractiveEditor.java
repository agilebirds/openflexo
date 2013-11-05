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

import java.util.Arrays;
import java.util.Collection;
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
import org.openflexo.fge.control.notifications.ToolOptionChanged;
import org.openflexo.fge.control.tools.DianaPalette;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.control.tools.InspectedBackgroundStyle;
import org.openflexo.fge.control.tools.InspectedConnectorSpecification;
import org.openflexo.fge.control.tools.InspectedForegroundStyle;
import org.openflexo.fge.control.tools.InspectedLocationSizeProperties;
import org.openflexo.fge.control.tools.InspectedShadowStyle;
import org.openflexo.fge.control.tools.InspectedShapeSpecification;
import org.openflexo.fge.control.tools.InspectedTextStyle;
import org.openflexo.fge.geom.FGEPoint;
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

	public static enum EditorTool {
		SelectionTool {
			@Override
			public Collection<EditorToolOption> getOptions() {
				return null;
			}
		},
		DrawShapeTool {
			@Override
			public Collection<DrawShapeToolOption> getOptions() {
				return Arrays.asList(DrawShapeToolOption.values());
			}
		},
		DrawCustomShapeTool {
			@Override
			public Collection<DrawCustomShapeToolOption> getOptions() {
				return Arrays.asList(DrawCustomShapeToolOption.values());
			}
		},
		DrawConnectorTool {
			@Override
			public Collection<DrawConnectorToolOption> getOptions() {
				return Arrays.asList(DrawConnectorToolOption.values());
			}
		},
		DrawTextTool {
			@Override
			public Collection<EditorToolOption> getOptions() {
				return null;
			}
		};
		public abstract Collection<? extends EditorToolOption> getOptions();
	}

	public static interface EditorToolOption {
		public String name();
	}

	public enum DrawShapeToolOption implements EditorToolOption {
		DrawRectangle, DrawOval
	}

	public enum DrawCustomShapeToolOption implements EditorToolOption {
		DrawPolygon, DrawClosedCurve, DrawOpenedCurve, DrawComplexShape
	}

	public enum DrawConnectorToolOption implements EditorToolOption {
		DrawLine, DrawCurve, DrawRectPolylin, DrawCurvedPolylin
	}

	public static final int PASTE_DELTA = 10;

	private EditorTool currentTool;
	private DrawShapeToolOption drawShapeToolOption;
	private DrawCustomShapeToolOption drawCustomShapeToolOption;
	private DrawConnectorToolOption drawConnectorToolOption;

	private DrawShapeToolController<?, ?> drawShapeToolController;
	private DrawShapeAction drawShapeAction;

	private InspectedForegroundStyle inspectedForegroundStyle;
	private InspectedBackgroundStyle inspectedBackgroundStyle;
	private InspectedTextStyle inspectedTextStyle;
	private InspectedShadowStyle inspectedShadowStyle;
	private InspectedShapeSpecification inspectedShapeSpecification;
	private InspectedConnectorSpecification inspectedConnectorSpecification;
	private InspectedLocationSizeProperties inspectedLocationSizeProperties;

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
		inspectedForegroundStyle = new InspectedForegroundStyle(this);
		inspectedBackgroundStyle = new InspectedBackgroundStyle(this);
		inspectedTextStyle = new InspectedTextStyle(this);
		inspectedShadowStyle = new InspectedShadowStyle(this);
		inspectedShapeSpecification = new InspectedShapeSpecification(this);
		inspectedConnectorSpecification = new InspectedConnectorSpecification(this);
		inspectedLocationSizeProperties = new InspectedLocationSizeProperties(this);
		setCurrentTool(EditorTool.SelectionTool);
		setDrawShapeToolOption(DrawShapeToolOption.DrawRectangle);
		setDrawCustomShapeToolOption(DrawCustomShapeToolOption.DrawPolygon);
		setDrawConnectorToolOption(DrawConnectorToolOption.DrawLine);
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
		inspectedShapeSpecification.fireSelectionUpdated();
		inspectedConnectorSpecification.fireSelectionUpdated();
		inspectedLocationSizeProperties.fireSelectionUpdated();
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
				break;
			case DrawCustomShapeTool:
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

	public DrawShapeToolOption getDrawShapeToolOption() {
		return drawShapeToolOption;
	}

	public void setDrawShapeToolOption(DrawShapeToolOption drawShapeToolOption) {
		if (this.drawShapeToolOption != drawShapeToolOption) {
			DrawShapeToolOption oldToolOption = this.drawShapeToolOption;
			this.drawShapeToolOption = drawShapeToolOption;
			notifyObservers(new ToolOptionChanged(oldToolOption, drawShapeToolOption));
		}
	}

	public DrawCustomShapeToolOption getDrawCustomShapeToolOption() {
		return drawCustomShapeToolOption;
	}

	public void setDrawCustomShapeToolOption(DrawCustomShapeToolOption drawCustomShapeToolOption) {
		if (this.drawCustomShapeToolOption != drawCustomShapeToolOption) {
			DrawCustomShapeToolOption oldToolOption = this.drawCustomShapeToolOption;
			this.drawCustomShapeToolOption = drawCustomShapeToolOption;
			if (drawShapeToolController != null) {
				drawShapeToolController.delete();
			}
			prepareDrawShapeToolController();
			notifyObservers(new ToolOptionChanged(oldToolOption, drawCustomShapeToolOption));
		}
	}

	private void prepareDrawShapeToolController() {
		if (drawShapeAction != null) {
			switch (getDrawCustomShapeToolOption()) {
			case DrawPolygon:
				drawShapeToolController = getToolFactory().makeDrawPolygonToolController(this, drawShapeAction);
				break;
			case DrawClosedCurve:
				drawShapeToolController = getToolFactory().makeDrawClosedCurveToolController(this, drawShapeAction, true);
				break;
			case DrawOpenedCurve:
				drawShapeToolController = getToolFactory().makeDrawClosedCurveToolController(this, drawShapeAction, false);
				break;
			default:
				logger.warning("Not implemented: " + getDrawCustomShapeToolOption());
			}
		}

	}

	public DrawConnectorToolOption getDrawConnectorToolOption() {
		return drawConnectorToolOption;
	}

	public void setDrawConnectorToolOption(DrawConnectorToolOption drawConnectorToolOption) {
		if (this.drawConnectorToolOption != drawConnectorToolOption) {
			DrawConnectorToolOption oldToolOption = this.drawConnectorToolOption;
			this.drawConnectorToolOption = drawConnectorToolOption;
			notifyObservers(new ToolOptionChanged(oldToolOption, drawConnectorToolOption));
		}
	}

	public InspectedForegroundStyle getInspectedForegroundStyle() {
		return inspectedForegroundStyle;
	}

	public InspectedBackgroundStyle getInspectedBackgroundStyle() {
		return inspectedBackgroundStyle;
	}

	public InspectedTextStyle getInspectedTextStyle() {
		return inspectedTextStyle;
	}

	public InspectedShadowStyle getInspectedShadowStyle() {
		return inspectedShadowStyle;
	}

	public InspectedShapeSpecification getInspectedShapeSpecification() {
		return inspectedShapeSpecification;
	}

	public InspectedConnectorSpecification getInspectedConnectorSpecification() {
		return inspectedConnectorSpecification;
	}

	public InspectedLocationSizeProperties getInspectedLocationSizeProperties() {
		return inspectedLocationSizeProperties;
	}

	public DrawShapeAction getDrawShapeAction() {
		return drawShapeAction;
	}

	public void setDrawShapeAction(DrawShapeAction drawShapeAction) {
		this.drawShapeAction = drawShapeAction;
		prepareDrawShapeToolController();
	}

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
		logger.info("UNDO called");
		getFactory().getUndoManager().debug();
		if (getFactory().getUndoManager().canUndo()) {
			logger.info("Undoing: " + getFactory().getUndoManager().editToBeUndone().getPresentationName());
			getFactory().getUndoManager().undo();
		} else {
			if (getFactory().getUndoManager().canUndoIfStoppingCurrentEdition()) {
				getFactory().getUndoManager().stopRecording(getFactory().getUndoManager().getCurrentEdition());
				if (getFactory().getUndoManager().canUndo()) {
					logger.info("Undoing: " + getFactory().getUndoManager().editToBeUndone().getPresentationName());
					getFactory().getUndoManager().undo();
				}
			} else {
				System.out.println("JE peux pas faire d'UNDO et puis c'est tout !");
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
		logger.info("REDO called");
		if (getFactory().getUndoManager().canRedo()) {
			logger.info("Redoing: " + getFactory().getUndoManager().editToBeRedone().getPresentationName());
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
