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
package org.openflexo.vpm.drawingshema;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawShapeAction;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.viewpoint.ExampleDrawingShema;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.foundation.viewpoint.action.AddExampleDrawingShape;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.CalcPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.vpm.controller.VPMController;

public class CalcDrawingShemaController extends SelectionManagingDrawingController<CalcDrawingShemaRepresentation> implements
		GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(CalcDrawingShemaController.class.getPackage().getName());

	private VPMController _controller;
	private CommonPalette _commonPalette;
	private CalcDrawingShemaModuleView _moduleView;
	private Hashtable<ViewPointPalette, ContextualPalette> _contextualPalettes;

	public CalcDrawingShemaController(VPMController controller, ExampleDrawingShema shema, boolean readOnly) {
		super(new CalcDrawingShemaRepresentation(shema, readOnly), controller.getSelectionManager());

		_controller = controller;

		shema.getViewPoint().addObserver(this);

		if (!readOnly) {
			_commonPalette = new CommonPalette();
			registerPalette(_commonPalette);
			activatePalette(_commonPalette);

			_contextualPalettes = new Hashtable<ViewPointPalette, ContextualPalette>();
			if (shema.getViewPoint() != null) {
				for (ViewPointPalette palette : shema.getViewPoint().getPalettes()) {
					ContextualPalette contextualPalette = new ContextualPalette(palette);
					_contextualPalettes.put(palette, contextualPalette);
					registerPalette(contextualPalette);
					activatePalette(contextualPalette);
				}
			}
		}

		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation,
					GraphicalRepresentation parentGraphicalRepresentation) {
				/*System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " et parent: "
						+ parentGraphicalRepresentation);*/

				AddExampleDrawingShape action = AddExampleDrawingShape.actionType.makeNewAction(getShema(), null, getCEDController()
						.getEditor());
				action.graphicalRepresentation = graphicalRepresentation;
				action.newShapeName = graphicalRepresentation.getText();
				if (action.newShapeName == null) {
					action.newShapeName = FlexoLocalization.localizedForKey("shape");
				}

				action.doAction();

			}
		});

	}

	@Override
	public void delete() {
		if (getShema() != null && getShema().getViewPoint() != null) {
			getShema().getViewPoint().deleteObserver(this);
		}
		if (_controller != null) {
			if (getDrawingView() != null && _moduleView != null) {
				_controller.removeModuleView(_moduleView);
			}
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<CalcDrawingShemaRepresentation> makeDrawingView(CalcDrawingShemaRepresentation drawing) {
		return new CalcDrawingShemaView(drawing, this);
	}

	public VPMController getCEDController() {
		return _controller;
	}

	@Override
	public CalcDrawingShemaView getDrawingView() {
		return (CalcDrawingShemaView) super.getDrawingView();
	}

	public CalcDrawingShemaModuleView getModuleView() {
		if (_moduleView == null) {
			_moduleView = new CalcDrawingShemaModuleView(this);
		}
		return _moduleView;
	}

	public CommonPalette getCommonPalette() {
		return _commonPalette;
	}

	private JTabbedPane paletteView;

	private Vector<ViewPointPalette> orderedPalettes;

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			orderedPalettes = new Vector<ViewPointPalette>(_contextualPalettes.keySet());
			Collections.sort(orderedPalettes);
			for (ViewPointPalette palette : orderedPalettes) {
				paletteView.add(palette.getName(), _contextualPalettes.get(palette).getPaletteViewInScrollPane());
			}
			paletteView.add(FlexoLocalization.localizedForKey("Common", getCommonPalette().getPaletteViewInScrollPane()),
					getCommonPalette().getPaletteViewInScrollPane());
			paletteView.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (paletteView.getSelectedIndex() < orderedPalettes.size()) {
						activatePalette(_contextualPalettes.get(orderedPalettes.elementAt(paletteView.getSelectedIndex())));
					} else if (paletteView.getSelectedIndex() == orderedPalettes.size()) {
						activatePalette(getCommonPalette());
					}
				}
			});
			paletteView.setSelectedIndex(0);
			if (orderedPalettes.size() > 0) {
				activatePalette(_contextualPalettes.get(orderedPalettes.firstElement()));
			} else {
				activatePalette(getCommonPalette());
			}
		}
		return paletteView;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		logger.fine("dataModification=" + dataModification);
		if (observable == getShema().getViewPoint()) {
			if (dataModification instanceof CalcPaletteInserted) {
				ViewPointPalette palette = ((CalcPaletteInserted) dataModification).newValue();
				ContextualPalette newContextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette, newContextualPalette);
				registerPalette(newContextualPalette);
				activatePalette(newContextualPalette);
				paletteView.add(palette.getName(), newContextualPalette.getPaletteViewInScrollPane());
				paletteView.revalidate();
				paletteView.repaint();
			} else if (dataModification instanceof CalcPaletteRemoved) {
				ViewPointPalette palette = ((CalcPaletteRemoved) dataModification).oldValue();
				ContextualPalette removedPalette = _contextualPalettes.get(palette);
				unregisterPalette(removedPalette);
				_contextualPalettes.remove(palette);
				paletteView.remove(removedPalette.getPaletteViewInScrollPane());
				paletteView.revalidate();
				paletteView.repaint();
			}
		}
	}

	protected void updatePalette(ViewPointPalette palette, JScrollPane oldPaletteView) {
		int index = paletteView.indexOfComponent(oldPaletteView);
		paletteView.remove(oldPaletteView);
		ContextualPalette cp = _contextualPalettes.get(palette);
		paletteView.insertTab(palette.getName(), null, cp.getPaletteViewInScrollPane(), null, index);
		paletteView.revalidate();
		paletteView.repaint();
	}

	public ExampleDrawingShema getShema() {
		return getDrawing().getShema();
	}

}
