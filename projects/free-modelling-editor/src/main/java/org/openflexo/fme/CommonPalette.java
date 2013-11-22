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

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.Shape;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.undo.CompoundEdit;

public class CommonPalette extends DrawingPalette {

	private static final Logger logger = FlexoLogger.getLogger(CommonPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 50;
	private static final int GRID_HEIGHT = 40;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 7);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private DianaDrawingEditor editor;

	public CommonPalette() {
		super(200, 200, "default");
		int px = 0;
		int py = 0;
		for (ShapeType st : ShapeType.values()) {
			if (st != ShapeType.CUSTOM_POLYGON && st != ShapeType.COMPLEX_CURVE && st != ShapeType.ARC) {
				addElement(makePaletteElement(st, px, py));
				px = px + 1;
				if (px == 3) {
					px = 0;
					py++;
				}
			}
		}

	}

	public DianaDrawingEditor getEditor() {
		return editor;
	}

	public void setEditor(DianaDrawingEditor editor) {
		this.editor = editor;
	}

	private PaletteElement makePaletteElement(ShapeType st, int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(st);
		FACTORY.applyDefaultProperties(gr);
		if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
			gr.setX(px * GRID_WIDTH + 15);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(30);
			gr.setHeight(30);
		} else {
			gr.setX(px * GRID_WIDTH + 10);
			gr.setY(py * GRID_HEIGHT + 10);
			gr.setWidth(40);
			gr.setHeight(30);
		}
		// gr.setText(st.name());
		gr.setTextStyle(FACTORY.makeTextStyle(Color.DARK_GRAY, DEFAULT_TEXT_FONT));
		gr.setIsFloatingLabel(false);
		gr.setForeground(FACTORY.makeForegroundStyle(Color.BLACK));
		gr.setBackground(FACTORY.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR));
		gr.setIsVisible(true);
		gr.setAllowToLeaveBounds(false);

		return makePaletteElement(gr, true, true, true, true);

	}

	private PaletteElement makePaletteElement(final ShapeGraphicalRepresentation gr, final boolean applyCurrentForeground,
			final boolean applyCurrentBackground, final boolean applyCurrentTextStyle, final boolean applyCurrentShadowStyle) {
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
				return getEditor() != null && target instanceof ContainerNode;
			}

			@Override
			public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {

				if (getEditor() == null) {
					return false;
				}

				DiagramElement<?, ?> container = (DiagramElement<?, ?>) target.getDrawable();

				logger.info("dragging " + this + " in " + container);

				// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
				// getController().getDrawing()),container);

				CompoundEdit edit = getEditor().getFactory().getUndoManager().startRecording("Dragging new Element");

				Shape newShape = getEditor().getFactory().makeNewShape(getGraphicalRepresentation(), dropLocation, container.getDiagram());

				ShapeGraphicalRepresentation shapeGR = newShape.getGraphicalRepresentation();

				if (applyCurrentForeground) {
					shapeGR.setForeground(getEditor().getInspectedForegroundStyle().cloneStyle());
				}
				if (applyCurrentBackground) {
					shapeGR.setBackground(getEditor().getInspectedBackgroundStyle().cloneStyle());
				}
				if (applyCurrentTextStyle) {
					shapeGR.setTextStyle((TextStyle) getEditor().getInspectedTextStyle().cloneStyle());
				}
				if (applyCurrentShadowStyle) {
					shapeGR.setShadowStyle((ShadowStyle) getEditor().getInspectedShadowStyle().cloneStyle());
				}

				container.addToShapes(newShape);

				getEditor().getFactory().getUndoManager().stopRecording(edit);

				getEditor().setCurrentTool(EditorTool.SelectionTool);
				getEditor().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newShape));

				return true;
			}

			@Override
			public ShapeGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public void delete() {
				gr.delete();
			}

			/*public DrawingPalette getPalette() {
				return CommonPalette.this;
			}*/
		};
		// gr.setDrawable(returned);
		return returned;
	}
}
