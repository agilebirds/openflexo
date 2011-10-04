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
package org.openflexo.ced.drawingshema;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.openflexo.ced.controller.CEDController;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;

public class CalcDrawingShemaController extends SelectionManagingDrawingController<CalcDrawingShemaRepresentation> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(CalcDrawingShemaController.class.getPackage().getName());

	private CEDController _controller;
	private CommonPalette _commonPalette;
	private EditorToolbox _toolbox;
	private CalcDrawingShemaModuleView _moduleView;
	private Hashtable<ViewPointPalette,ContextualPalette> _contextualPalettes;
	
	public CalcDrawingShemaController(CEDController controller, ExampleDrawingShema shema, boolean readOnly)
	{
		super(new CalcDrawingShemaRepresentation(shema,readOnly),controller.getSelectionManager());
		
		_controller = controller;

		shema.getCalc().addObserver(this);
		
		if (!readOnly) {
			_commonPalette = new CommonPalette();
			registerPalette(_commonPalette);
			activatePalette(_commonPalette);

			_contextualPalettes = new Hashtable<ViewPointPalette,ContextualPalette>();
			if (shema.getCalc() != null) {
				for (ViewPointPalette palette : shema.getCalc().getPalettes()) {
					ContextualPalette contextualPalette = new ContextualPalette(palette);
					_contextualPalettes.put(palette,contextualPalette);
					registerPalette(contextualPalette);
					activatePalette(contextualPalette);
				}
			}
		}

		if (!readOnly)
			_toolbox = new EditorToolbox();
	}

	@Override
	public void delete() 
	{
		getDrawing().delete();
		if (getShema() != null && getShema().getCalc() != null) {
			getShema().getCalc().deleteObserver(this);
		}
		if (_controller!=null) {
			if (getDrawingView()!=null && _moduleView != null)
				_controller.removeModuleView(_moduleView);
			_controller.VIEW_POINT_PERSPECTIVE.removeFromControllers(this);
		}
		super.delete();
	}
	
	@Override
	public DrawingView<CalcDrawingShemaRepresentation> makeDrawingView(CalcDrawingShemaRepresentation drawing) 
	{
		return new CalcDrawingShemaView(drawing,this);
	}

	public CEDController getCEDController() 
	{
		return _controller;
	}
	
	@Override
	public CalcDrawingShemaView getDrawingView() 
	{
		return (CalcDrawingShemaView)super.getDrawingView();
	}
	
	public CalcDrawingShemaModuleView getModuleView()
	{
		if (_moduleView == null) {
			_moduleView = new CalcDrawingShemaModuleView(this);
		}
		return _moduleView;
	}
	
	public CommonPalette getCommonPalette()
	{
		return _commonPalette;
	}
	
	private JTabbedPane paletteView;
	
	private Vector<ViewPointPalette> orderedPalettes;
	
	public JTabbedPane getPaletteView()
	{
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			orderedPalettes = new Vector<ViewPointPalette>(_contextualPalettes.keySet());
			Collections.sort(orderedPalettes);
			for (ViewPointPalette palette : orderedPalettes) {
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

	@Override
	public void update(FlexoObservable observable,DataModification dataModification)
	{
		logger.fine("dataModification="+dataModification);
		if (observable == getShema().getCalc()) {
			if (dataModification instanceof CalcPaletteInserted) {
				ViewPointPalette palette = ((CalcPaletteInserted)dataModification).newValue();
				ContextualPalette newContextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette,newContextualPalette);
				registerPalette(newContextualPalette);
				activatePalette(newContextualPalette);
				paletteView.add(palette.getName(),newContextualPalette.getPaletteView());
				paletteView.revalidate();
				paletteView.repaint();
			}
			else if (dataModification instanceof CalcPaletteRemoved) {
				ViewPointPalette palette = ((CalcPaletteRemoved)dataModification).oldValue();
				ContextualPalette removedPalette = _contextualPalettes.get(palette);
				unregisterPalette(removedPalette);
				_contextualPalettes.remove(palette);
				paletteView.remove(removedPalette.getPaletteView());
				paletteView.revalidate();
				paletteView.repaint();
			}
		}
	}
	
	protected void updatePalette(ViewPointPalette palette, DrawingView<?> oldPaletteView)
	{
		int index = paletteView.indexOfComponent(oldPaletteView);
		paletteView.remove(oldPaletteView);
		ContextualPalette cp = _contextualPalettes.get(palette);
		paletteView.insertTab(palette.getName(), null, cp.getPaletteView(), null, index);
		paletteView.revalidate();
		paletteView.repaint();		
	}

	public EditorToolbox getToolbox()
	{
		return _toolbox;
	}
	
	public ExampleDrawingShema getShema()
	{
		return getDrawing().getShema();
	}

}
