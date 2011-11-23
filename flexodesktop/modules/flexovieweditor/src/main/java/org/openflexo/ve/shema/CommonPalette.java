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
package org.openflexo.ve.shema;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.controller.DrawingPalette;
import org.openflexo.fge.controller.PaletteElement;
import org.openflexo.fge.controller.PaletteElement.PaletteElementGraphicalRepresentation;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.graphics.BackgroundStyle;
import org.openflexo.fge.graphics.ForegroundStyle;
import org.openflexo.fge.graphics.ShadowStyle;
import org.openflexo.fge.graphics.TextStyle;
import org.openflexo.fge.shapes.Shape.ShapeType;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.action.AddShape;

public class CommonPalette extends DrawingPalette {

	private static final Logger logger = Logger.getLogger(CommonPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 80;
	private static final int GRID_HEIGHT = 60;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 9);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	public CommonPalette() {
		super(260, 400, "default");

		int px = 0;
		int py = 0;
		for (ShapeType st : ShapeType.values()) {
			addElement(makeShapeElement(st, px, py));
			px = px + 1;
			if (px == 3) {
				px = 0;
				py++;
			}
		}

		addElement(makeSingleLabel(0, 3));
		addElement(makeMultilineLabel(1, 3));
		addElement(makeBoundedMultilineLabel(2, 3));

		makePalettePanel();
		getPaletteView().revalidate();
	}

	private PaletteElement makeShapeElement(ShapeType st, int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(st, null, getPaletteDrawing());
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
		/*gr.setText(st.name());
		gr.setTextStyle(TextStyle.makeTextStyle(Color.DARK_GRAY, DEFAULT_TEXT_FONT));
		gr.setIsFloatingLabel(false);*/
		/*gr.setForeground(ForegroundStyle.makeStyle(Color.BLACK));
		gr.setBackground(BackgroundStyle.makeColoredBackground(Color.RED));*/
		gr.setIsVisible(true);
		gr.setIsFloatingLabel(false);

		return makePaletteElement(gr, true, true, true, true);

	}

	@Override
	public VEShemaController getController() {
		return (VEShemaController) super.getController();
	}

	private PaletteElement makeSingleLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 15);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(TextStyle.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setText("Label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(ForegroundStyle.makeNone());
		gr.setBackground(BackgroundStyle.makeEmptyBackground());
		gr.setShadowStyle(ShadowStyle.makeNone());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, false, true);
	}

	private PaletteElement makeMultilineLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(TextStyle.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setForeground(ForegroundStyle.makeNone());
		gr.setBackground(BackgroundStyle.makeEmptyBackground());
		gr.setShadowStyle(ShadowStyle.makeNone());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, false, true);
	}

	private PaletteElement makeBoundedMultilineLabel(int px, int py) {
		final PaletteElementGraphicalRepresentation gr = new PaletteElementGraphicalRepresentation(ShapeType.RECTANGLE, null,
				getPaletteDrawing());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(60);
		gr.setHeight(20);
		gr.setAdjustMinimalWidthToLabelWidth(true);
		gr.setAdjustMinimalHeightToLabelHeight(true);

		gr.setTextStyle(TextStyle.makeTextStyle(Color.BLACK, LABEL_FONT));
		gr.setIsMultilineAllowed(true);
		gr.setText("Multiple\nlines label");
		gr.setIsFloatingLabel(false);
		gr.setBackground(BackgroundStyle.makeEmptyBackground());
		gr.setShadowStyle(ShadowStyle.makeNone());
		gr.setIsVisible(true);

		return makePaletteElement(gr, false, false, false, true);
	}

	private PaletteElement makePaletteElement(final PaletteElementGraphicalRepresentation gr, final boolean applyForegroundStyle,
			final boolean applyBackgroundStyle, final boolean applyShadowStyle, final boolean applyTextStyle) {
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(GraphicalRepresentation target) {
				return target instanceof VEShemaGR || target instanceof VEShapeGR;
			}

			@Override
			public boolean elementDragged(GraphicalRepresentation containerGR, FGEPoint dropLocation) {
				if (containerGR.getDrawable() instanceof ViewObject) {

					ViewObject container = (ViewObject) containerGR.getDrawable();

					ShapeGraphicalRepresentation<?> shapeGR = getGraphicalRepresentation().clone();
					shapeGR.setIsSelectable(true);
					shapeGR.setIsFocusable(true);
					shapeGR.setIsReadOnly(false);
					shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
					if (applyForegroundStyle) {
						shapeGR.setForeground(getController().getCurrentForegroundStyle());
					}
					if (applyBackgroundStyle) {
						shapeGR.setBackground(getController().getCurrentBackgroundStyle());
					}
					if (applyShadowStyle) {
						shapeGR.setShadowStyle(getController().getCurrentShadowStyle());
					}
					if (applyTextStyle) {
						shapeGR.setTextStyle(getController().getCurrentTextStyle());
					}
					shapeGR.setLocation(dropLocation);
					shapeGR.setLayer(containerGR.getLayer() + 1);
					shapeGR.setAllowToLeaveBounds(true);

					AddShape action = AddShape.actionType.makeNewAction(container, null, getController().getOEController().getEditor());
					action.setGraphicalRepresentation(shapeGR);
					action.setNameSetToNull(true);
					// action.setNewShapeName(FlexoLocalization.localizedForKey("unnamed"));

					action.doAction();
					return action.hasActionExecutionSucceeded();
				}

				return false;
			}

			@Override
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public DrawingPalette getPalette() {
				return CommonPalette.this;
			}

		};
		gr.setDrawable(returned);
		return returned;
	}
}
