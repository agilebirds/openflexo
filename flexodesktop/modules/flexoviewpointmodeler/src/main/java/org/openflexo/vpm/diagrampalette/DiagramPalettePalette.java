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
package org.openflexo.vpm.diagrampalette;

import java.awt.Color;
import java.awt.Font;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Rectangle;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddDiagramPaletteElement;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.undo.CompoundEdit;
import org.openflexo.vpm.examplediagram.CommonPalette;

public class DiagramPalettePalette extends DrawingPalette {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(CommonPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 50;
	private static final int GRID_HEIGHT = 40;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 7);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private final DiagramPaletteEditor editor;

	public DiagramPalettePalette(DiagramPaletteEditor editor) {
		super(200, 200, "default");

		this.editor = editor;

		ShapeSpecification[] ssp = new ShapeSpecification[11];

		ssp[0] = FACTORY.makeShape(ShapeType.RECTANGLE);
		ssp[1] = FACTORY.makeShape(ShapeType.RECTANGLE);
		((Rectangle) ssp[1]).setIsRounded(true);
		((Rectangle) ssp[1]).setArcSize(20);
		ssp[2] = FACTORY.makeShape(ShapeType.SQUARE);
		ssp[3] = FACTORY.makeShape(ShapeType.RECTANGULAROCTOGON);
		ssp[4] = FACTORY.makeShape(ShapeType.OVAL);
		ssp[5] = FACTORY.makeShape(ShapeType.CIRCLE);
		ssp[6] = FACTORY.makeShape(ShapeType.LOSANGE);
		ssp[7] = FACTORY.makeShape(ShapeType.POLYGON);
		ssp[8] = FACTORY.makeShape(ShapeType.TRIANGLE);
		ssp[9] = FACTORY.makeShape(ShapeType.STAR);
		ssp[10] = FACTORY.makeShape(ShapeType.ARC);

		int px = 0;
		int py = 0;
		for (ShapeSpecification sspi : ssp) {
			addElement(makePaletteElement(sspi, px, py));
			px = px + 1;
			if (px == 3) {
				px = 0;
				py++;
			}
		}

	}

	public DiagramPaletteEditor getEditor() {
		return editor;
	}

	private PaletteElement makePaletteElement(ShapeSpecification shapeSpecification, int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation(shapeSpecification);
		FACTORY.applyDefaultProperties(gr);
		// if (gr.getDimensionConstraints() == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
		if (shapeSpecification.getShapeType() == ShapeType.SQUARE || shapeSpecification.getShapeType() == ShapeType.CIRCLE) {
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
		@SuppressWarnings("serial")
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(DrawingTreeNode<?, ?> target) {
				return getEditor() != null && target instanceof ContainerNode && (target.getDrawable() instanceof DiagramPalette);
			}

			@Override
			public boolean elementDragged(DrawingTreeNode<?, ?> target, FGEPoint dropLocation) {

				if (getEditor() == null) {
					return false;
				}

				DiagramPalette container = (DiagramPalette) target.getDrawable();

				logger.info("dragging " + this + " in " + container);

				// getController().addNewShape(new Shape(getGraphicalRepresentation().getShapeType(), dropLocation,
				// getController().getDrawing()),container);

				CompoundEdit edit = getEditor().getFactory().getUndoManager().startRecording("Dragging new element in palette");

				ShapeGraphicalRepresentation shapeGR = getEditor().getFactory().makeNewShapeGR(getGraphicalRepresentation());
				if (shapeGR.getShapeSpecification().getShapeType() == ShapeType.SQUARE
						|| shapeGR.getShapeSpecification().getShapeType() == ShapeType.CIRCLE) {
					shapeGR.setWidth(40);
					shapeGR.setHeight(40);
				} else {
					shapeGR.setWidth(50);
					shapeGR.setHeight(40);
				}
				if (applyCurrentForeground) {
					shapeGR.setForeground(getEditor().getInspectedForegroundStyle().cloneStyle());
				}
				if (applyCurrentBackground) {
					shapeGR.setBackground(getEditor().getInspectedBackgroundStyle().cloneStyle());
				}
				if (applyCurrentTextStyle) {
					shapeGR.setTextStyle(getEditor().getInspectedTextStyle().cloneStyle());
				}
				if (applyCurrentShadowStyle) {
					shapeGR.setShadowStyle(getEditor().getInspectedShadowStyle().cloneStyle());
				}

				shapeGR.setX(dropLocation.x);
				shapeGR.setY(dropLocation.y);

				AddDiagramPaletteElement action = AddDiagramPaletteElement.actionType.makeNewAction(container, null, editor
						.getVPMController().getEditor());
				action.setGraphicalRepresentation(shapeGR);

				action.doAction();

				DiagramPaletteElement newElement = action.getNewElement();

				getEditor().getFactory().getUndoManager().stopRecording(edit);

				getEditor().setCurrentTool(EditorTool.SelectionTool);
				getEditor().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newElement));

				return action.hasActionExecutionSucceeded();
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

/*
 * 	private PaletteElement makePaletteElement(final PaletteElementGraphicalRepresentation gr, final boolean applyForegroundStyle,
			final boolean applyBackgroundStyle, final boolean applyShadowStyle, final boolean applyTextStyle) {
		PaletteElement returned = new PaletteElement() {
			@Override
			public boolean acceptDragging(GraphicalRepresentation target) {
				return target.getDrawable() instanceof DiagramPalette;
			}

			@Override
			public boolean elementDragged(GraphicalRepresentation containerGR, FGEPoint dropLocation) {
				if (containerGR.getDrawable() instanceof DiagramPalette) {

					DiagramPalette container = (DiagramPalette) containerGR.getDrawable();

					ShapeGraphicalRepresentation shapeGR = getGraphicalRepresentation().clone();
					shapeGR.setIsSelectable(true);
					shapeGR.setIsFocusable(true);
					shapeGR.setIsReadOnly(false);
					shapeGR.setLocationConstraints(LocationConstraints.FREELY_MOVABLE);
					shapeGR.setLocation(dropLocation);
					shapeGR.setLayer(containerGR.getLayer() + 1);
					shapeGR.setAllowToLeaveBounds(true);

					AddDiagramPaletteElement action = AddDiagramPaletteElement.actionType.makeNewAction(container, null, _editor);
					action.setGraphicalRepresentation(shapeGR);

					action.doAction();

					// container.addPaletteElement(shapeGR.getText(), shapeGR);

					return true;
				}

				return false;
			}

			@Override
			public PaletteElementGraphicalRepresentation getGraphicalRepresentation() {
				return gr;
			}

			@Override
			public DrawingPalette getPalette() {
				return DiagramPalettePalette.this;
			}

		};
		gr.setDrawable(returned);
		return returned;
	}

*/
