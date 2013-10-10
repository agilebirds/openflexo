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
package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.dnd.DragSource;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.GRBinding.DrawingGRBinding;
import org.openflexo.fge.GRBinding.ShapeGRBinding;
import org.openflexo.fge.GRProvider.DrawingGRProvider;
import org.openflexo.fge.GRProvider.ShapeGRProvider;
import org.openflexo.fge.GRStructureWalker;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.DrawingPalette;
import org.openflexo.fge.control.PaletteElement;
import org.openflexo.fge.impl.DrawingImpl;
import org.openflexo.fge.view.DianaViewFactory;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fib.utils.FIBIconLibrary;
import org.openflexo.toolbox.ToolBox;

/**
 * A DianaPaletteC represents the graphical tool representing a {@link DrawingPalette} (the model)
 * 
 * @author sylvain
 * 
 */
public abstract class DianaPalette<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	private static final Logger logger = Logger.getLogger(DianaPalette.class.getPackage().getName());

	private static Image DROP_OK_IMAGE = FIBIconLibrary.DROP_OK_CURSOR.getImage();
	private static Image DROP_KO_IMAGE = FIBIconLibrary.DROP_KO_CURSOR.getImage();

	public static final Cursor dropOK = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_OK_IMAGE, new Point(16, 16), "Drop OK") : DragSource.DefaultMoveDrop;

	public static final Cursor dropKO = ToolBox.getPLATFORM() == ToolBox.MACOS ? Toolkit.getDefaultToolkit().createCustomCursor(
			DROP_KO_IMAGE, new Point(16, 16), "Drop KO") : DragSource.DefaultMoveNoDrop;

	private DrawingPalette palette = null;

	private PaletteDrawing paletteDrawing;
	// This controller is the local controller for displaying the palette, NOT the controller
	// Which this palette is associated to.
	private PaletteController<?, ?> paletteController;

	// private DragSourceContext dragSourceContext;

	public DianaPalette(DrawingPalette palette) {
		super();
		setPalette(palette);
	}

	public DrawingPalette getPalette() {
		return palette;
	}

	public void setPalette(DrawingPalette palette) {
		if (palette != this.palette) {
			updatePalette(palette);
		}
	}

	protected void updatePalette(DrawingPalette palette) {
		if (paletteController != null) {
			paletteController.delete();
		}
		if (paletteDrawing != null) {
			paletteDrawing.delete();
		}
		this.palette = palette;
		paletteDrawing = new PaletteDrawing(palette);
		paletteController = getEditor().getDianaFactory().makePaletteController(this);
	}

	public void delete() {
		if (paletteController != null) {
			paletteController.delete();
		}
		if (paletteDrawing != null) {
			paletteDrawing.delete();
		}
	}

	public DrawingView<DrawingPalette, ?> getPaletteView() {
		if (paletteController == null) {
			return null;
		}
		return paletteController.getDrawingView();
	}

	public PaletteDrawing getPaletteDrawing() {
		return paletteDrawing;
	}

	public static class PaletteDrawing extends DrawingImpl<DrawingPalette> implements Drawing<DrawingPalette> {

		private final DrawingGraphicalRepresentation gr;

		private PaletteDrawing(DrawingPalette palette) {
			super(palette, DrawingPalette.FACTORY, PersistenceMode.UniqueGraphicalRepresentations);
			gr = DrawingPalette.FACTORY.makeDrawingGraphicalRepresentation(this, false);
			gr.setWidth(palette.getWidth());
			gr.setHeight(palette.getHeight());
			gr.setBackgroundColor(Color.WHITE);
			gr.setDrawWorkingArea(true);
			setEditable(true);
		}

		@Override
		public void init() {

			final DrawingGRBinding<DrawingPalette> paletteBinding = bindDrawing(DrawingPalette.class, "palette",
					new DrawingGRProvider<DrawingPalette>() {
						@Override
						public DrawingGraphicalRepresentation provideGR(DrawingPalette drawable, FGEModelFactory factory) {
							return gr;
						}
					});
			final ShapeGRBinding<PaletteElement> paletteElementBinding = bindShape(PaletteElement.class, "paletteElement",
					new ShapeGRProvider<PaletteElement>() {
						@Override
						public ShapeGraphicalRepresentation provideGR(PaletteElement drawable, FGEModelFactory factory) {
							return drawable.getGraphicalRepresentation();
						}
					});

			paletteBinding.addToWalkers(new GRStructureWalker<DrawingPalette>() {

				@Override
				public void walk(DrawingPalette palette) {
					for (PaletteElement element : palette.getElements()) {
						drawShape(paletteElementBinding, element, palette);
					}
				}
			});
		}
	}

	/**
	 * Return the DrawingView of the controller this palette is associated to
	 * 
	 * @return
	 */
	public DrawingView<?, ?> getDrawingView() {
		if (getEditor() != null) {
			return getEditor().getDrawingView();
		}
		return null;
	}

	public void updatePalette() {
		paletteController.rebuildDrawingView();
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
