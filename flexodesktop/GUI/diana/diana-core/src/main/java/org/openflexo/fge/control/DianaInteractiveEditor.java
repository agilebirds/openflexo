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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.control.tools.DrawPolygonToolController;
import org.openflexo.fge.control.tools.DrawShapeToolController;
import org.openflexo.fge.control.tools.DrawingPalette;
import org.openflexo.fge.control.tools.EditorToolbox;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represents an editor of a {@link Drawing}<br>
 * 
 * The {@link Drawing} can be fully edited. A {@link DianaInteractiveEditor} generally declares some palettes
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

	private EditorTool currentTool;

	private DrawShapeToolController<?> drawShapeToolController;
	private DrawShapeAction drawShapeAction;

	private ForegroundStyle currentForegroundStyle;
	private BackgroundStyle currentBackgroundStyle;
	private TextStyle currentTextStyle;
	private ShadowStyle currentShadowStyle;
	private ShapeSpecification currentShape;

	private Vector<DrawingPalette> palettes;

	private EditorToolbox toolbox;

	public DianaInteractiveEditor(Drawing<M> aDrawing, FGEModelFactory factory, F dianaFactory) {
		super(aDrawing, factory, dianaFactory, true, true, true, true, true);
		currentForegroundStyle = factory.makeDefaultForegroundStyle();
		currentBackgroundStyle = factory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR);
		currentTextStyle = factory.makeDefaultTextStyle();
		currentShadowStyle = factory.makeDefaultShadowStyle();
		currentShape = factory.makeShape(ShapeType.RECTANGLE);
		setCurrentTool(EditorTool.SelectionTool);
		palettes = new Vector<DrawingPalette>();
		toolbox = new EditorToolbox(this);
	}

	public void delete() {
		super.delete();
		if (toolbox != null) {
			toolbox.delete();
		}
		toolbox = null;
		if (palettes != null) {
			for (DrawingPalette palette : palettes) {
				palette.delete();
			}
		}
		palettes = null;
	}

	public DrawShapeToolController<?> getDrawShapeToolController() {
		return drawShapeToolController;
	}

	public EditorTool getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(EditorTool aTool) {
		if (aTool != currentTool) {
			logger.fine("Switch to tool " + aTool);
			switch (aTool) {
			case SelectionTool:
				/*if (currentTool == EditorTool.DrawShapeTool && drawShapeToolController != null) {
					drawShapeToolController.makeNewShape();
				}*/
				break;
			case DrawShapeTool:
				// if (drawShapeAction != null) {
				drawShapeToolController = new DrawPolygonToolController(this, drawShapeAction);
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
			if (getToolbox() != null) {
				getToolbox().getToolPanel().updateButtons();
			}
		}
	}

	public ForegroundStyle getCurrentForegroundStyle() {
		return currentForegroundStyle;
	}

	public void setCurrentForegroundStyle(ForegroundStyle currentForegroundStyle) {
		this.currentForegroundStyle = currentForegroundStyle;
	}

	public BackgroundStyle getCurrentBackgroundStyle() {
		return currentBackgroundStyle;
	}

	public void setCurrentBackgroundStyle(BackgroundStyle currentBackgroundStyle) {
		this.currentBackgroundStyle = currentBackgroundStyle;
	}

	public TextStyle getCurrentTextStyle() {
		return currentTextStyle;
	}

	public void setCurrentTextStyle(TextStyle currentTextStyle) {
		this.currentTextStyle = currentTextStyle;
	}

	public ShadowStyle getCurrentShadowStyle() {
		return currentShadowStyle;
	}

	public void setCurrentShadowStyle(ShadowStyle currentShadowStyle) {
		this.currentShadowStyle = currentShadowStyle;
	}

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

	public Vector<DrawingPalette> getPalettes() {
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
	}

	public void activatePalette(DrawingPalette aPalette) {
		if (getDrawingView() != null) {
			getDrawingView().registerPalette(aPalette);
		}
	}

	public EditorToolbox getToolbox() {
		return toolbox;
	}

	public void setSelectedObject(DrawingTreeNode<?, ?> aNode) {
		super.setSelectedObject(aNode);
		if (getToolbox() != null) {
			getToolbox().update();
		}
	}

	public void addToSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		super.addToSelectedObjects(aNode);
		if (getToolbox() != null) {
			getToolbox().update();
		}
	}

	public void removeFromSelectedObjects(DrawingTreeNode<?, ?> aNode) {
		super.removeFromSelectedObjects(aNode);
		if (getToolbox() != null) {
			getToolbox().update();
		}
	}

	public void toggleSelection(DrawingTreeNode<?, ?> aNode) {
		super.toggleSelection(aNode);
		if (getToolbox() != null) {
			getToolbox().update();
		}
	}

	public void clearSelection() {
		super.clearSelection();
		if (getToolbox() != null) {
			getToolbox().update();
		}
	}

}
