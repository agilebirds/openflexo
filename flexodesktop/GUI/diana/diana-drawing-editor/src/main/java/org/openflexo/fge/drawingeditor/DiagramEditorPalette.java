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

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.drawingeditor.model.DiagramElement;
import org.openflexo.fge.drawingeditor.model.DiagramFactory;
import org.openflexo.fge.drawingeditor.model.Shape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.logging.FlexoLogger;

public class DiagramEditorPalette extends DrawingPalette {

	private static final Logger logger = FlexoLogger.getLogger(DiagramEditorPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 80;
	private static final int GRID_HEIGHT = 60;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 9);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	// This factory is the one of the editor
	private DiagramFactory editorFactory;

	public DiagramEditorPalette(DiagramFactory editorFactory) {
		super(360, 350, "default");
		this.editorFactory = editorFactory;
		int px = 0;
		int py = 0;
		for (ShapeType st : ShapeType.values()) {
			if (st != ShapeType.CUSTOM_POLYGON) {
				addElement(makePaletteElement(st, px, py));
				px = px + 1;
				if (px == 3) {
					px = 0;
					py++;
				}
			}
		}

		addElement(makeSingleLabel(0, 3));
		addElement(makeMultilineLabel(1, 3));
		addElement(makeBoundedMultilineLabel(2, 3));

		makePalettePanel();
		getPaletteView().revalidate();
	}

	private PaletteElement makePaletteElement(ShapeType st, int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(st, this, null, getPaletteDrawing());
		getFactory().applyDefaultProperties(gr);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setX(px * GRID_WIDTH + 15);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(50);
			gr.setHeight(50);
		} else {
			gr.setX(px * GRID_WIDTH + 10);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(60);
			gr.setHeight(50);
		}
		gr.setText(st.name());
		gr.setTextStyle(getFactory().makeTextStyle(Color.DARK_GRAY, DEFAULT_TEXT_FONT));
		gr.setIsFloatingLabel(false);
		gr.setForeground(getFactory().makeForegroundStyle(Color.BLACK));
		gr.setBackground(getFactory().makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR));
		gr.setIsVisible(true);
		gr.setAllowToLeaveBounds(false);

		return makePaletteElement(gr, true, true, true, true);

		/*PaletteElement returned = new PaletteElement() {
			public boolean acceptDragging(GraphicalRepresentation gr)
			{
				return (gr instanceof DrawingGraphicalRepresentation) || (gr instanceof ShapeGraphicalRepresentation);
			}
			public boolean elementDragged(GraphicalRepresentation gr, FGEPoint dropLocation)
			{
				DiagramElement container = (DiagramElement)gr.getDrawable();
				getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation, getController().getDrawing()),container);
				return true;
			}
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation()
			{
				return gr;
			}		
			public DrawingPalette getPalette()
			{
				return DiagramEditorPalette.this;
			}
		};
		gr.setDrawable(returned);
		return returned;*/
	}

	@Override
	public DiagramEditorController getController() {
		return (DiagramEditorController) super.getController();
	}

	private PaletteElement makeSingleLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, this, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 15);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(getFactory().makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setText("Label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(getFactory().makeNoneForegroundStyle());
		gr.setBackground(getFactory().makeEmptyBackground());
		gr.setShadowStyle(getFactory().makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makeMultilineLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, this, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(getFactory().makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(getFactory().makeNoneForegroundStyle());
		gr.setBackground(getFactory().makeEmptyBackground());
		gr.setShadowStyle(getFactory().makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makeBoundedMultilineLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, this, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(getFactory().makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setBackground(getFactory().makeEmptyBackground());
		gr.setForeground(getFactory().makeNoneForegroundStyle());
		gr.setShadowStyle(getFactory().makeNoneShadowStyle());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, true, false);
	}

	private PaletteElement makePaletteElement(final PaletteElementGraphicalRepresentation gr, final boolean applyCurrentForeground,
			final boolean applyCurrentBackground, final boolean applyCurrentTextStyle, final boolean applyCurrentShadowStyle) {
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
				return target instanceof RootNode || target instanceof ShapeNode;
			}

			@Override
			public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {

				DiagramElement<?, ?> container = (DiagramElement<?, ?>) target.getDrawable();

				logger.info("dragging " + this + " in " + container);

				// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
				// getController().getDrawing()),container);

				Shape newShape = getController().getFactory().makeNewShape(getGraphicalRepresentation(), dropLocation,
						getController().getDrawing());

				ShapeGraphicalRepresentation shapeGR = newShape.getGraphicalRepresentation();

				if (applyCurrentForeground) {
					shapeGR.setForeground(getController().getCurrentForegroundStyle());
				}
				if (applyCurrentBackground) {
					shapeGR.setBackground(getController().getCurrentBackgroundStyle());
				}
				if (applyCurrentTextStyle) {
					shapeGR.setTextStyle(getController().getCurrentTextStyle());
				}
				if (applyCurrentShadowStyle) {
					shapeGR.setShadowStyle(getController().getCurrentShadowStyle());
				}

				container.addToShapes(newShape);

				// getController().addNewShape(editorFactory.makeNewShape(shapeGR, dropLocation, getController().getDrawing()), container);
				return true;
			}

			@Override
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public DrawingPalette getPalette() {
				return DiagramEditorPalette.this;
			}
		};
		// gr.setDrawable(returned);
		return returned;
	}
}
