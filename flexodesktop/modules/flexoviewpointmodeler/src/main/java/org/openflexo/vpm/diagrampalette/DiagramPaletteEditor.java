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

import javax.swing.JTabbedPane;

import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDianaEditor;
import org.openflexo.vpm.controller.VPMController;

public class DiagramPaletteEditor extends SelectionManagingDianaEditor<DiagramPalette> {

	private final VPMController vpmController;
	private DiagramPalettePalette palettePaletteModel;
	private JDianaPalette palettePalette;
	private DiagramPaletteModuleView moduleView;

	public DiagramPaletteEditor(VPMController vpmController, DiagramPalette palette, boolean readOnly) {
		super(new DiagramPaletteDrawing(palette, readOnly), vpmController.getSelectionManager(), palette.getResource().getFactory(),
				vpmController.getToolFactory());
		this.vpmController = vpmController;

		if (!readOnly) {
			palettePaletteModel = new DiagramPalettePalette(this);
			palettePalette = vpmController.getToolFactory().makeDianaPalette(palettePaletteModel);
			palettePalette.attachToEditor(this);
		}

	}

	@Override
	public void delete() {
		if (vpmController != null) {
			if (getDrawingView() != null && moduleView != null) {
				vpmController.removeModuleView(moduleView);
			}
		}
		super.delete();
		getDrawing().delete();
	}

	public VPMController getVPMController() {
		return vpmController;
	}

	@Override
	public DiagramPaletteFactory getFactory() {
		return (DiagramPaletteFactory) super.getFactory();
	}

	public DiagramPaletteModuleView getModuleView() {
		if (moduleView == null) {
			moduleView = new DiagramPaletteModuleView(this);
		}
		return moduleView;
	}

	public JDianaPalette getPalettePalette() {
		return palettePalette;
	}

	private JTabbedPane paletteView;

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			paletteView.add(FlexoLocalization.localizedForKey("Common", getPalettePalette().getPaletteViewInScrollPane()),
					getPalettePalette().getPaletteViewInScrollPane());
		}
		return paletteView;
	}

	public DiagramPalette getDiagramPalette() {
		return getDrawing().getModel();
	}

}
