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
package org.openflexo.vpm.examplediagram;

import java.awt.Component;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawShapeAction;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddExampleDiagramShape;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.vpm.controller.VPMController;

public class ExampleDiagramController extends SelectionManagingDrawingController<ExampleDiagramRepresentation> implements
		GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ExampleDiagramController.class.getPackage().getName());

	private VPMController _controller;
	private CommonPalette _commonPalette;
	private ExampleDiagramModuleView _moduleView;
	private Hashtable<DiagramPalette, ContextualPalette> _contextualPalettes;

	private JTabbedPane paletteView;
	private Vector<DiagramPalette> orderedPalettes;

	public ExampleDiagramController(VPMController controller, ExampleDiagram shema, boolean readOnly) {
		super(new ExampleDiagramRepresentation(shema, readOnly), controller.getSelectionManager());

		_controller = controller;

		shema.getVirtualModel().addObserver(this);

		if (!readOnly) {
			_commonPalette = new CommonPalette();
			registerPalette(_commonPalette);
			activatePalette(_commonPalette);

			_contextualPalettes = new Hashtable<DiagramPalette, ContextualPalette>();
			if (shema.getVirtualModel() != null) {
				for (DiagramPalette palette : shema.getVirtualModel().getPalettes()) {
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

				AddExampleDiagramShape action = AddExampleDiagramShape.actionType.makeNewAction(getExampleDiagram(), null,
						getCEDController().getEditor());
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
		if (getExampleDiagram() != null && getExampleDiagram().getVirtualModel() != null) {
			getExampleDiagram().getVirtualModel().deleteObserver(this);
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
	public DrawingView<ExampleDiagramRepresentation> makeDrawingView(ExampleDiagramRepresentation drawing) {
		return new ExampleDiagramView(drawing, this);
	}

	public VPMController getCEDController() {
		return _controller;
	}

	@Override
	public ExampleDiagramView getDrawingView() {
		return (ExampleDiagramView) super.getDrawingView();
	}

	public ExampleDiagramModuleView getModuleView() {
		if (_moduleView == null) {
			_moduleView = new ExampleDiagramModuleView(this);
		}
		return _moduleView;
	}

	public CommonPalette getCommonPalette() {
		return _commonPalette;
	}

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			orderedPalettes = new Vector<DiagramPalette>(_contextualPalettes.keySet());
			Collections.sort(orderedPalettes);
			for (DiagramPalette palette : orderedPalettes) {
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
		if (observable == getExampleDiagram().getVirtualModel()) {
			if (dataModification instanceof DiagramPaletteInserted) {
				DiagramPalette palette = ((DiagramPaletteInserted) dataModification).newValue();
				ContextualPalette newContextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette, newContextualPalette);
				registerPalette(newContextualPalette);
				activatePalette(newContextualPalette);
				paletteView.add(palette.getName(), newContextualPalette.getPaletteView());
				paletteView.revalidate();
				paletteView.repaint();
			} else if (dataModification instanceof DiagramPaletteRemoved) {
				DiagramPalette palette = ((DiagramPaletteRemoved) dataModification).oldValue();
				ContextualPalette removedPalette = _contextualPalettes.get(palette);
				unregisterPalette(removedPalette);
				_contextualPalettes.remove(palette);
				paletteView.remove(removedPalette.getPaletteView());
				paletteView.revalidate();
				paletteView.repaint();
			}
		}
	}

	protected void updatePalette(DiagramPalette palette, DrawingView<?> oldPaletteView) {
		if (getPaletteView() != null) {
			// System.out.println("update palette with " + oldPaletteView);
			int index = -1;
			for (int i = 0; i < getPaletteView().getComponentCount(); i++) {
				// System.out.println("> " + paletteView.getComponentAt(i));
				Component c = getPaletteView().getComponentAt(i);
				if (SwingUtilities.isDescendingFrom(oldPaletteView, c)) {
					index = i;
					System.out.println("Found index " + index);
				}
			}
			if (index > -1) {
				getPaletteView().remove(getPaletteView().getComponentAt(index));
				ContextualPalette cp = _contextualPalettes.get(palette);
				cp.updatePalette();
				getPaletteView().insertTab(palette.getName(), null, cp.getPaletteViewInScrollPane(), null, index);
			}
			getPaletteView().revalidate();
			getPaletteView().repaint();
		} else {
			logger.warning("updatePalette() called with null value for paletteView");
		}
	}

	public ExampleDiagram getExampleDiagram() {
		return getDrawing().getExampleDiagram();
	}

}
