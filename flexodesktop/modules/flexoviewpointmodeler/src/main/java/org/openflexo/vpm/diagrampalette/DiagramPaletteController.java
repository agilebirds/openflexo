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

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.vpm.controller.VPMController;

public class DiagramPaletteController extends SelectionManagingDrawingController<DiagramPaletteRepresentation> {

	private VPMController _controller;
	private DiagramPalettePalette _commonPalette;
	private DiagramPaletteModuleView _moduleView;

	public DiagramPaletteController(VPMController controller, DiagramPalette palette, boolean readOnly) {
		super(new DiagramPaletteRepresentation(palette, readOnly), controller.getSelectionManager());
		_controller = controller;

		if (!readOnly) {
			_commonPalette = new DiagramPalettePalette(controller.getEditor());
			registerPalette(_commonPalette);
			activatePalette(_commonPalette);
		}

	}

	@Override
	public void delete() {
		if (_controller != null) {
			if (getDrawingView() != null && _moduleView != null) {
				_controller.removeModuleView(_moduleView);
			}
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<DiagramPaletteRepresentation> makeDrawingView(DiagramPaletteRepresentation drawing) {
		return new DrawingView<DiagramPaletteRepresentation>(drawing, this);
	}

	public VPMController getCEDController() {
		return _controller;
	}

	public DiagramPaletteModuleView getModuleView() {
		if (_moduleView == null) {
			_moduleView = new DiagramPaletteModuleView(this);
		}
		return _moduleView;
	}

	public DiagramPalettePalette getCommonPalette() {
		return _commonPalette;
	}

	private JTabbedPane paletteView;

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			paletteView.add(FlexoLocalization.localizedForKey("Common", getCommonPalette().getPaletteViewInScrollPane()),
					getCommonPalette().getPaletteViewInScrollPane());
		}
		return paletteView;
	}

	public DiagramPalette getDiagramPalette() {
		return getDrawing().getModel();
	}

}
