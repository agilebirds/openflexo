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
package org.openflexo.ced.palette;

import javax.swing.JTabbedPane;


import org.openflexo.ced.controller.CEDController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.ontology.calc.CalcPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;

public class CalcPaletteController extends SelectionManagingDrawingController<CalcPaletteRepresentation> {

	private CEDController _controller;
	private PalettePalette _commonPalette;
	private CalcPaletteModuleView _moduleView;
		
	public CalcPaletteController(CEDController controller, CalcPalette palette, boolean readOnly)
	{
		super(new CalcPaletteRepresentation(palette,readOnly),controller.getSelectionManager());
		_controller = controller;

		if (!readOnly) {
			_commonPalette = new PalettePalette(controller.getEditor());
			registerPalette(_commonPalette);
			activatePalette(_commonPalette);
		}

	}

	@Override
	public void delete() 
	{
		getDrawing().delete();
		if (_controller!=null) {
			if (getDrawingView()!=null && _moduleView != null)
				_controller.removeModuleView(_moduleView);
			_controller.CALC_PERSPECTIVE.removeFromControllers(this);
		}
		super.delete();
	}
	
	@Override
	public DrawingView<CalcPaletteRepresentation> makeDrawingView(CalcPaletteRepresentation drawing) 
	{
		return new DrawingView<CalcPaletteRepresentation>(drawing,this);
	}

	public CEDController getCEDController() 
	{
		return _controller;
	}
	
	public CalcPaletteModuleView getModuleView()
	{
		if (_moduleView == null) {
			_moduleView = new CalcPaletteModuleView(this);
		}
		return _moduleView;
	}
	
	public PalettePalette getCommonPalette()
	{
		return _commonPalette;
	}
	
	private JTabbedPane paletteView;
	
	public JTabbedPane getPaletteView()
	{
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			paletteView.add(FlexoLocalization.localizedForKey("Common",getCommonPalette().getPaletteView()),getCommonPalette().getPaletteView());			
		}
		return paletteView;
	}

	public CalcPalette getCalcPalette()
	{
		return getDrawing().getModel();
	}

}
