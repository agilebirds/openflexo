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

import java.awt.Font;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fme.model.ConceptGRAssociation;
import org.openflexo.fme.model.DiagramElement;
import org.openflexo.fme.model.Shape;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.undo.CompoundEdit;

public class BusinessPalette extends DrawingPalette {

	private static final Logger logger = FlexoLogger.getLogger(BusinessPalette.class.getPackage().getName());

	private static final int GRID_WIDTH = 50;
	private static final int GRID_HEIGHT = 40;
	public static final Font DEFAULT_TEXT_FONT = new Font("SansSerif", Font.PLAIN, 7);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);

	private DiagramEditor editor;

	private List<ConceptGRAssociation> associations;

	public BusinessPalette() {
		super(200, 200, "business");
	}

	public BusinessPalette(List<ConceptGRAssociation> associations) {
		super(200, 200, "business");
		this.associations = associations;

	}

	public void init() {
		int px = 0;
		int py = 0;
		for (ConceptGRAssociation cGRa : associations) {
			addElement(makePaletteElement(cGRa, px, py));
			px = px + 1;
			if (px == 3) {
				px = 0;
				py++;
			}
		}
	}

	public DiagramEditor getEditor() {
		return editor;
	}

	public void setEditor(DiagramEditor editor) {
		this.editor = editor;
		init();
	}

	private PaletteElement makePaletteElement(ConceptGRAssociation cGRa, int px, int py) {
		final ShapeGraphicalRepresentation gr = FACTORY.makeShapeGraphicalRepresentation((ShapeGraphicalRepresentation) cGRa
				.getGraphicalRepresentation());
		gr.setX(px * GRID_WIDTH + 10);
		gr.setY(py * GRID_HEIGHT + 10);
		gr.setWidth(40);
		gr.setHeight(30);
		return makePaletteElement(gr, true, true, true, true, cGRa);

	}

	private PaletteElement makePaletteElement(final ShapeGraphicalRepresentation gr, final boolean applyCurrentForeground,
			final boolean applyCurrentBackground, final boolean applyCurrentTextStyle, final boolean applyCurrentShadowStyle,
			final ConceptGRAssociation cGRa) {
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

				CompoundEdit edit = getEditor().getFactory().getUndoManager().startRecording("Dragging new Element");

				ShapeGraphicalRepresentation shapeGR = getEditor().getFactory().makeNewShapeGR(getGraphicalRepresentation());

				Shape newShape = getEditor().createNewShape(container, cGRa, dropLocation);

				getEditor().getFactory().getUndoManager().stopRecording(edit);

				getEditor().getController().setCurrentTool(EditorTool.SelectionTool);
				getEditor().getController().setSelectedObject(getEditor().getDrawing().getDrawingTreeNode(newShape));

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

		};
		// gr.setDrawable(returned);
		return returned;
	}
}
