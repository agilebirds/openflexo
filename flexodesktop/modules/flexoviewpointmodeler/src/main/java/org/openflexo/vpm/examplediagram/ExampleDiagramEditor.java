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
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.actions.DrawShapeAction;
import org.openflexo.fge.swing.control.tools.JDianaLayoutWidget;
import org.openflexo.fge.swing.control.tools.JDianaPalette;
import org.openflexo.fge.swing.control.tools.JDianaStyles;
import org.openflexo.fge.swing.control.tools.JDianaToolSelector;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPalette;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagram;
import org.openflexo.foundation.view.diagram.viewpoint.ExampleDiagramFactory;
import org.openflexo.foundation.view.diagram.viewpoint.action.AddExampleDiagramShape;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteInserted;
import org.openflexo.foundation.viewpoint.dm.DiagramPaletteRemoved;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.selection.SelectionManagingDianaEditor;
import org.openflexo.vpm.controller.VPMController;

public class ExampleDiagramEditor extends SelectionManagingDianaEditor<ExampleDiagram> implements GraphicalFlexoObserver {

	private static final Logger logger = Logger.getLogger(ExampleDiagramEditor.class.getPackage().getName());

	private final VPMController vpmController;
	private ExampleDiagramModuleView moduleView;

	private JTabbedPane paletteView;
	private Vector<DiagramPalette> orderedPalettes;

	private JPanel toolsPanel;
	private JDianaToolSelector toolSelector;
	private JDianaLayoutWidget layoutWidget;
	private JDianaStyles stylesWidget;
	private JDianaPalette commonPalette;
	private CommonPalette commonPaletteModel;
	private Hashtable<DiagramPalette, ContextualPalette> contextualPaletteModels;
	private Hashtable<DiagramPalette, JDianaPalette> contextualPalettes;

	public ExampleDiagramEditor(VPMController controller, ExampleDiagram exampleDiagram, boolean readOnly) {
		super(new ExampleDiagramDrawing(exampleDiagram, readOnly), controller.getSelectionManager(), exampleDiagram.getResource()
				.getFactory(), controller.getToolFactory());

		vpmController = controller;

		exampleDiagram.getVirtualModel().addObserver(this);

		if (!readOnly) {

			toolSelector = controller.getToolFactory().makeDianaToolSelector(this);
			stylesWidget = controller.getToolFactory().makeDianaStyles();
			stylesWidget.attachToEditor(this);
			layoutWidget = controller.getToolFactory().makeDianaLayoutWidget();
			layoutWidget.attachToEditor(this);

			toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			toolsPanel.add(toolSelector.getComponent());
			toolsPanel.add(stylesWidget.getComponent());
			toolsPanel.add(layoutWidget.getComponent());

			commonPaletteModel = new CommonPalette(this);

			commonPalette = controller.getToolFactory().makeDianaPalette(commonPaletteModel);
			commonPalette.attachToEditor(this);

			contextualPaletteModels = new Hashtable<DiagramPalette, ContextualPalette>();
			contextualPalettes = new Hashtable<DiagramPalette, JDianaPalette>();

			if (exampleDiagram.getVirtualModel() != null) {
				for (DiagramPalette palette : exampleDiagram.getVirtualModel().getPalettes()) {
					ContextualPalette contextualPaletteModel = new ContextualPalette(palette, this);
					contextualPaletteModels.put(palette, contextualPaletteModel);
					JDianaPalette dianaPalette = controller.getToolFactory().makeDianaPalette(contextualPaletteModel);
					dianaPalette.attachToEditor(this);
					contextualPalettes.put(palette, dianaPalette);
				}
			}
		}

		setDrawShapeAction(new DrawShapeAction() {
			@Override
			public void performedDrawNewShape(ShapeGraphicalRepresentation graphicalRepresentation, ContainerNode<?, ?> parentNode) {
				/*System.out.println("OK, perform draw new shape with " + graphicalRepresentation + " et parent: "
						+ parentGraphicalRepresentation);*/

				AddExampleDiagramShape action = AddExampleDiagramShape.actionType.makeNewAction(getExampleDiagram(), null,
						getVPMController().getEditor());
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
	public ExampleDiagramFactory getFactory() {
		return (ExampleDiagramFactory) super.getFactory();
	}

	@Override
	public void delete() {
		if (getExampleDiagram() != null && getExampleDiagram().getVirtualModel() != null) {
			getExampleDiagram().getVirtualModel().deleteObserver(this);
		}
		if (vpmController != null) {
			if (getDrawingView() != null && moduleView != null) {
				vpmController.removeModuleView(moduleView);
			}
		}
		super.delete();
		getDrawing().delete();
	}

	public JPanel getToolsPanel() {
		return toolsPanel;
	}

	@Override
	public JDrawingView<ExampleDiagram> makeDrawingView() {
		return new ExampleDiagramView(this);
	}

	public VPMController getVPMController() {
		return vpmController;
	}

	@Override
	public ExampleDiagramView getDrawingView() {
		return (ExampleDiagramView) super.getDrawingView();
	}

	public ExampleDiagramModuleView getModuleView() {
		if (moduleView == null) {
			moduleView = new ExampleDiagramModuleView(this);
		}
		return moduleView;
	}

	public JDianaPalette getCommonPalette() {
		return commonPalette;
	}

	public JTabbedPane getPaletteView() {
		if (paletteView == null) {
			paletteView = new JTabbedPane();
			orderedPalettes = new Vector<DiagramPalette>(contextualPalettes.keySet());
			Collections.sort(orderedPalettes);
			for (DiagramPalette palette : orderedPalettes) {
				paletteView.add(palette.getName(), contextualPalettes.get(palette).getPaletteViewInScrollPane());
			}
			paletteView.add(FlexoLocalization.localizedForKey("Common", getCommonPalette().getPaletteViewInScrollPane()),
					getCommonPalette().getPaletteViewInScrollPane());
			paletteView.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (paletteView.getSelectedIndex() < orderedPalettes.size()) {
						activatePalette(contextualPalettes.get(orderedPalettes.elementAt(paletteView.getSelectedIndex())));
					} else if (paletteView.getSelectedIndex() == orderedPalettes.size()) {
						activatePalette(getCommonPalette());
					}
				}
			});
			paletteView.setSelectedIndex(0);
			if (orderedPalettes.size() > 0) {
				activatePalette(contextualPalettes.get(orderedPalettes.firstElement()));
			} else {
				activatePalette(getCommonPalette());
			}
		}
		return paletteView;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		logger.fine("dataModification=" + dataModification);
		if (observable == getExampleDiagram().getVirtualModel() && paletteView != null) {
			if (dataModification instanceof DiagramPaletteInserted) {
				logger.info("Handling palette added");
				DiagramPalette palette = ((DiagramPaletteInserted) dataModification).newValue();
				ContextualPalette newContextualPalette = new ContextualPalette(palette, this);
				contextualPaletteModels.put(palette, newContextualPalette);
				JDianaPalette dianaPalette = vpmController.getToolFactory().makeDianaPalette(newContextualPalette);
				dianaPalette.attachToEditor(this);
				contextualPalettes.put(palette, dianaPalette);
				paletteView.add(palette.getName(), dianaPalette.getPaletteViewInScrollPane());
				paletteView.revalidate();
				paletteView.repaint();
			} else if (dataModification instanceof DiagramPaletteRemoved) {
				logger.info("Handling palette removed");
				DiagramPalette palette = ((DiagramPaletteRemoved) dataModification).oldValue();
				JDianaPalette removedPalette = contextualPalettes.get(palette);
				removedPalette.delete();
				ContextualPalette removedPaletteModel = contextualPaletteModels.get(palette);
				removedPaletteModel.delete();
				// unregisterPalette(removedPalette);
				contextualPalettes.remove(palette);
				contextualPaletteModels.remove(palette);
				paletteView.remove(removedPalette.getPaletteViewInScrollPane());
				paletteView.revalidate();
				paletteView.repaint();
			}
		}
	}

	protected void updatePalette(DiagramPalette palette, JDianaPalette oldPaletteView) {
		if (getPaletteView() != null) {
			// System.out.println("update palette with " + oldPaletteView);
			int index = -1;
			for (int i = 0; i < getPaletteView().getComponentCount(); i++) {
				// System.out.println("> " + paletteView.getComponentAt(i));
				Component c = getPaletteView().getComponentAt(i);
				if (SwingUtilities.isDescendingFrom(oldPaletteView.getComponent(), c)) {
					index = i;
					System.out.println("Found index " + index);
				}
			}
			if (index > -1) {
				getPaletteView().remove(getPaletteView().getComponentAt(index));
				JDianaPalette p = contextualPalettes.get(palette);
				p.updatePalette();
				getPaletteView().insertTab(palette.getName(), null, p.getPaletteViewInScrollPane(), null, index);
			}
			getPaletteView().revalidate();
			getPaletteView().repaint();
		} else {
			logger.warning("updatePalette() called with null value for paletteView");
		}
	}

	public ExampleDiagram getExampleDiagram() {
		return getDrawing().getModel();
	}

}
