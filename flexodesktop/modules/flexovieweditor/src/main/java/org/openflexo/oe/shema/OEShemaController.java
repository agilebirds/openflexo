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
package org.openflexo.oe.shema;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.view.OEShema;
import org.openflexo.foundation.viewpoint.CalcPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.oe.controller.OEController;
import org.openflexo.selection.SelectionManagingDrawingController;


public class OEShemaController extends SelectionManagingDrawingController<OEShemaRepresentation> {

	private OEController _controller;
	private CommonPalette _commonPalette;
	private EditorToolbox _toolbox;
	private OEShemaModuleView _moduleView;
	private Hashtable<CalcPalette,ContextualPalette> _contextualPalettes;
	
	public OEShemaController(OEController controller, OEShema shema)
	{
		super(new OEShemaRepresentation(shema),controller.getSelectionManager());
		
		_controller = controller;
		
		_commonPalette = new CommonPalette();
		registerPalette(_commonPalette);
		activatePalette(_commonPalette);
		
		_contextualPalettes = new Hashtable<CalcPalette,ContextualPalette>();
		if (shema.getCalc() != null) {
			for (CalcPalette palette : shema.getCalc().getPalettes()) {
				ContextualPalette contextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette,contextualPalette);
				registerPalette(contextualPalette);
				activatePalette(contextualPalette);
			}
		}

		_toolbox = new EditorToolbox();
	}

	@Override
	public void delete() {
		if (_controller!=null) {
			if (getDrawingView()!=null)
				_controller.removeModuleView(getModuleView());
			_controller.SHEMA_PERSPECTIVE.removeFromControllers(this);
		}
		super.delete();
	}
	
	@Override
	public DrawingView<OEShemaRepresentation> makeDrawingView(OEShemaRepresentation drawing) 
	{
		return new OEShemaView(drawing,this);
	}

	public OEController getOEController() 
	{
		return _controller;
	}
	
	@Override
	public OEShemaView getDrawingView() 
	{
		return (OEShemaView)super.getDrawingView();
	}
	
	public OEShemaModuleView getModuleView()
	{
		if (_moduleView == null) {
			_moduleView = new OEShemaModuleView(this);
		}
		return _moduleView;
	}
	
	public CommonPalette getCommonPalette()
	{
		return _commonPalette;
	}
	
	private JTabbedPane paletteView;
	
	private Vector<CalcPalette> orderedPalettes;
	
	public JTabbedPane getPaletteView()
	{
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			orderedPalettes = new Vector<CalcPalette>(_contextualPalettes.keySet());
			Collections.sort(orderedPalettes);
			for (CalcPalette palette : orderedPalettes) {
				paletteView.add(palette.getName(),(_contextualPalettes.get(palette)).getPaletteView());
			}
			paletteView.add(FlexoLocalization.localizedForKey("Common",getCommonPalette().getPaletteView()),getCommonPalette().getPaletteView());
			paletteView.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (paletteView.getSelectedIndex() < orderedPalettes.size()) {
						activatePalette(_contextualPalettes.get(orderedPalettes.elementAt(paletteView.getSelectedIndex())));
					}
					else if (paletteView.getSelectedIndex() == orderedPalettes.size()) {
						activatePalette(getCommonPalette());
					}
				}
			});
			paletteView.setSelectedIndex(0);
			if (orderedPalettes.size() > 0) {
				activatePalette(_contextualPalettes.get(orderedPalettes.firstElement()));
			}
			else {
				activatePalette(getCommonPalette());
			}
		}
		return paletteView;
	}

	public EditorToolbox getToolbox()
	{
		return _toolbox;
	}
	
	public OEShema getShema()
	{
		return getDrawing().getShema();
	}
	
	public FlexoEditor getEditor()
	{
		return _controller.getEditor();
	}



}
