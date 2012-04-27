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
package org.openflexo.ve.shema;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawShapeAction;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.action.AddShape;
import org.openflexo.foundation.viewpoint.ViewPointPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.ve.controller.OEController;

public class VEShemaController extends SelectionManagingDrawingController<VEShemaRepresentation> {

	private OEController _controller;
	private CommonPalette _commonPalette;
	private VEShemaModuleView _moduleView;
	private Hashtable<ViewPointPalette, ContextualPalette> _contextualPalettes;

	public VEShemaController(OEController controller, View shema) {
		super(new VEShemaRepresentation(shema), controller.getSelectionManager());

		_controller = controller;

		_commonPalette = new CommonPalette();
		registerPalette(_commonPalette);
		activatePalette(_commonPalette);

		_contextualPalettes = new Hashtable<ViewPointPalette, ContextualPalette>();
		if (shema.getCalc() != null) {
			for (ViewPointPalette palette : shema.getCalc().getPalettes()) {
				ContextualPalette contextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette, contextualPalette);
				registerPalette(contextualPalette);
				activatePalette(contextualPalette);
			}
		}

		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation,
					GraphicalRepresentation parentGraphicalRepresentation) {
				System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " et parent: "
						+ parentGraphicalRepresentation);

				AddShape action = AddShape.actionType.makeNewAction(getShema(), null, getOEController().getEditor());
				action.setGraphicalRepresentation(graphicalRepresentation);
				action.setNameSetToNull(true);

				action.doAction();

			}
		});

	}

	@Override
	public void delete() {
		if (_controller != null) {
			if (getDrawingView() != null) {
				_controller.removeModuleView(getModuleView());
			}
			_controller.DIAGRAM_PERSPECTIVE.removeFromControllers(this);
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<VEShemaRepresentation> makeDrawingView(VEShemaRepresentation drawing) {
		return new VEShemaView(drawing, this);
	}

	public OEController getOEController() {
		return _controller;
	}

	@Override
	public VEShemaView getDrawingView() {
		return (VEShemaView) super.getDrawingView();
	}

	public VEShemaModuleView getModuleView() {
		if (_moduleView == null) {
			_moduleView = new VEShemaModuleView(this);
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
				paletteView.add(palette.getName(), _contextualPalettes.get(palette).getPaletteView());
			}
			paletteView.add(FlexoLocalization.localizedForKey("Common", getCommonPalette().getPaletteView()), getCommonPalette()
					.getPaletteView());
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

	public View getShema() {
		return getDrawing().getShema();
	}

	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

}
