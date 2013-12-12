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

import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureVisitor;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteFactory;
import org.openflexo.toolbox.ToolBox;

public class DiagramPaletteDrawing extends DrawingImpl<DiagramPalette> {

	private static final Logger logger = Logger.getLogger(DiagramPaletteDrawing.class.getPackage().getName());

	public DiagramPaletteDrawing(DiagramPalette model, boolean readOnly) {
		super(model, model.getResource().getFactory(), PersistenceMode.UniqueGraphicalRepresentations);
		setEditable(!readOnly);
	}

	@Override
	public void init() {

		final DrawingGRBinding<DiagramPalette> paletteBinding = bindDrawing(DiagramPalette.class, "palette",
				new DrawingGRProvider<DiagramPalette>() {
					@Override
					public DrawingGraphicalRepresentation provideGR(DiagramPalette drawable, FGEModelFactory factory) {
						return retrieveGraphicalRepresentation(drawable, (DiagramPaletteFactory) factory);
					}
				});
		final ShapeGRBinding<DiagramPaletteElement> elementBinding = bindShape(DiagramPaletteElement.class, "paletteElement",
				new ShapeGRProvider<DiagramPaletteElement>() {
					@Override
					public ShapeGraphicalRepresentation provideGR(DiagramPaletteElement drawable, FGEModelFactory factory) {
						return retrieveGraphicalRepresentation(drawable, (DiagramPaletteFactory) factory);
					}
				});

		paletteBinding.addToWalkers(new GRStructureVisitor<DiagramPalette>() {

			@Override
			public void visit(DiagramPalette palette) {
				for (DiagramPaletteElement element : palette.getElements()) {
					drawShape(elementBinding, element, palette);
				}
			}
		});

		elementBinding.setDynamicPropertyValue(GraphicalRepresentation.TEXT, new DataBinding<String>("drawable.name"), true);

	}

	@Override
	public void delete() {
		super.delete();
	}

	public DiagramPalette getDiagramPalette() {
		return getModel();
	}

	private DrawingGraphicalRepresentation retrieveGraphicalRepresentation(DiagramPalette palette, DiagramPaletteFactory factory) {
		DrawingGraphicalRepresentation returned = null;
		if (palette.getGraphicalRepresentation() != null) {
			palette.getGraphicalRepresentation().setFactory(factory);
			returned = palette.getGraphicalRepresentation();
		} else {
			returned = factory.makeDrawingGraphicalRepresentation();
			palette.setGraphicalRepresentation(returned);
		}
		returned.addToMouseClickControls(new DiagramPaletteEditor.ShowContextualMenuControl(factory));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			returned.addToMouseClickControls(new DiagramPaletteEditor.ShowContextualMenuControl(factory, true));
		}
		return returned;
	}

	private ShapeGraphicalRepresentation retrieveGraphicalRepresentation(DiagramPaletteElement element, DiagramPaletteFactory factory) {
		ShapeGraphicalRepresentation returned = null;
		if (element.getGraphicalRepresentation() != null) {
			element.getGraphicalRepresentation().setFactory(factory);
			returned = element.getGraphicalRepresentation();
		} else {
			returned = factory.makeShapeGraphicalRepresentation();
			element.setGraphicalRepresentation(returned);
		}
		returned.addToMouseClickControls(new DiagramPaletteEditor.ShowContextualMenuControl(factory));
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			returned.addToMouseClickControls(new DiagramPaletteEditor.ShowContextualMenuControl(factory, true));
		}
		return returned;
	}

}
