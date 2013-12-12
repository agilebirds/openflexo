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
package org.openflexo.ve.diagram;

import java.awt.Component;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.controller.DrawShapeAction;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.action.AddShape;
import org.openflexo.foundation.view.diagram.action.ReindexDiagramElements;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.foundation.view.diagram.model.DiagramElement;
import org.openflexo.foundation.view.diagram.model.DiagramRootPane;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDianaEditor;
import org.openflexo.ve.controller.VEController;

public class DiagramController extends SelectionManagingDianaEditor<DiagramRepresentation> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(DiagramController.class.getPackage().getName());

	private VEController _controller;
	private CommonPalette _commonPalette;
	private DiagramModuleView _moduleView;
	private Hashtable<DiagramPalette, ContextualPalette> _contextualPalettes;

	public DiagramController(VEController controller, Diagram diagram, boolean screenshotOnly) {
		super(new DiagramRepresentation(diagram.getRootPane(), screenshotOnly), controller.getSelectionManager());

		_controller = controller;

		_commonPalette = new CommonPalette();
		registerPalette(_commonPalette);
		activatePalette(_commonPalette);

		_contextualPalettes = new Hashtable<DiagramPalette, ContextualPalette>();
		if (diagram.getDiagramSpecification() != null) {
			for (DiagramPalette palette : diagram.getDiagramSpecification().getPalettes()) {
				ContextualPalette contextualPalette = new ContextualPalette(palette);
				_contextualPalettes.put(palette, contextualPalette);
				registerPalette(contextualPalette);
				activatePalette(contextualPalette);
			}
		}

		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				logger.info("OK, perform draw new shape with " + graphicalRepresentation + " and parent: " + parentNode);

				AddShape action = AddShape.actionType.makeNewAction(getDiagramRootPane(), null, getVEController().getEditor());
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
			_controller.VIEW_LIBRARY_PERSPECTIVE.removeFromControllers(this);
		}
		super.delete();
		// Fixed huge bug with graphical representation (which are in the model) deleted when the diagram view was closed
		// getDrawing().delete();
		if (getDrawing().getDiagramRootPane() != null) {
			getDrawing().getDiagramRootPane().deleteObserver(getDrawing());
		}
	}

	@Override
	public DrawingView<DiagramRepresentation> makeDrawingView(DiagramRepresentation drawing) {
		return new DiagramView(drawing, this);
	}

	public VEController getVEController() {
		return _controller;
	}

	@Override
	public DiagramView getDrawingView() {
		return (DiagramView) super.getDrawingView();
	}

	public DiagramModuleView getModuleView() {
		if (_moduleView == null) {
			_moduleView = new DiagramModuleView(this);
		}
		return _moduleView;
	}

	public CommonPalette getCommonPalette() {
		return _commonPalette;
	}

	private JTabbedPane paletteView;

	private Vector<DiagramPalette> orderedPalettes;

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

	public DiagramRootPane getDiagramRootPane() {
		return getDrawing().getDiagramRootPane();
	}

	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

	@Override
	public boolean upKeyPressed() {
		if (!super.upKeyPressed()) {
			FlexoObject o = getSelectionManager().getLastSelectedObject();
			if (o == null) {
				o = getSelectionManager().getFocusedObject();
			}
			if (o instanceof DiagramElement) {
				ReindexDiagramElements action = ReindexDiagramElements.actionType.makeNewAction((DiagramElement) o, null, getEditor());
				action.initAsUpReindexing((DiagramElement) o);
				action.doAction();
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean downKeyPressed() {
		if (!super.downKeyPressed()) {
			FlexoObject o = getSelectionManager().getLastSelectedObject();
			if (o == null) {
				o = getSelectionManager().getFocusedObject();
			}
			if (o instanceof DiagramElement) {
				ReindexDiagramElements action = ReindexDiagramElements.actionType.makeNewAction((DiagramElement) o, null, getEditor());
				action.initAsDownReindexing((DiagramElement) o);
				action.doAction();
				return true;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		logger.fine("dataModification=" + dataModification);
		if (observable == getDrawing().getDiagram().getVirtualModel() && paletteView != null) {
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
				getPaletteView().setComponentAt(index, cp.getPaletteView());
			}

			getPaletteView().revalidate();
			getPaletteView().repaint();
		} else {
			logger.warning("updatePalette() called with null value for paletteView");
		}
	}
}
