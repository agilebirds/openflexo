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
package org.openflexo.ve.controller;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.VirtualModelInstance;
import org.openflexo.foundation.view.diagram.model.Diagram;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.inspector.FIBInspectorPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.diagram.DiagramController;
import org.openflexo.ve.diagram.DiagramModuleView;
import org.openflexo.ve.view.VirtualModelInstanceView;
import org.openflexo.ve.widget.FIBViewLibraryBrowser;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class ViewLibraryPerspective extends FlexoPerspective {

	protected static final Logger logger = Logger.getLogger(ViewLibraryPerspective.class.getPackage().getName());

	private final VEController _controller;

	private final FIBViewLibraryBrowser viewLibraryBrowser;

	private final Map<Diagram, DiagramController> _controllers;

	private final JLabel infoLabel;

	private final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	private final FIBInspectorPanel inspectorPanel;

	private final JComponent inspectorPanelScrollPane;

	private JComponent bottomRightDummy;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ViewLibraryPerspective(VEController controller) {
		super("diagram_perspective");

		viewLibraryBrowser = new FIBViewLibraryBrowser(controller.getProject() != null ? controller.getProject().getViewLibrary() : null,
				controller);

		setTopLeftView(viewLibraryBrowser);

		EMPTY_RIGHT_VIEW.setPreferredSize(new Dimension(300, 300));
		_controller = controller;
		_controllers = new Hashtable<Diagram, DiagramController>();
		bottomRightDummy = new JPanel();

		infoLabel = new JLabel("Diagram perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);

		// Initialized inspector panel
		inspectorPanel = new FIBInspectorPanel(controller.getModuleInspectorController());
		inspectorPanelScrollPane = inspectorPanel; // new JScrollPane(inspectorPanel);
	}

	public void focusOnDiagram(Diagram aDiagram) {
		logger.info("focusOnDiagram " + aDiagram);
		// calcBrowser.deleteBrowserListener(_browserView);
		// calcBrowser.setRepresentedObject(viewPoint);
		// calcBrowser.update();
		// calcBrowser.addBrowserListener(_browserView);

		// viewBrowser.setRootObject(viewPoint);
		// setBottomLeftView(viewBrowser);
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentDiagramModuleView() != null) {
			return getCurrentDiagramModuleView().getController().getPaletteView();
		} else {
			return EMPTY_RIGHT_VIEW;
		}
	}

	@Override
	public JComponent getBottomRightView() {
		if (getCurrentDiagramModuleView() != null) {
			return inspectorPanelScrollPane;
		} else {
			return bottomRightDummy;
		}
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return VEIconLibrary.VE_SP_ACTIVE_ICON;
	}

	@Override
	public FlexoObject getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof View) {
			return proposedObject;
		}
		if (proposedObject instanceof FlexoProjectObject) {
			return ((FlexoProjectObject) proposedObject).getProject().getViewLibrary();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof Diagram || object instanceof VirtualModelInstance;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof VirtualModelInstance) {
			return new VirtualModelInstanceView((VirtualModelInstance) object, (VEController) controller);
		}
		if (object instanceof Diagram) {
			return getControllerForDiagram((Diagram) object).getModuleView();
		}
		return null;
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentDiagramModuleView() != null) {
			return getCurrentDiagramModuleView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	public DiagramModuleView getCurrentDiagramModuleView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof DiagramModuleView) {
			return (DiagramModuleView) _controller.getCurrentModuleView();
		}
		return null;
	}

	public VirtualModelInstanceView getCurrentVirtualModelInstanceView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof VirtualModelInstanceView) {
			return (VirtualModelInstanceView) _controller.getCurrentModuleView();
		}
		return null;
	}

	public DiagramController getControllerForDiagram(Diagram diagram) {
		DiagramController returned = _controllers.get(diagram);
		if (returned == null) {
			returned = new DiagramController(_controller, diagram, false);
			_controllers.put(diagram, returned);
		}
		return returned;
	}

	public void removeFromControllers(DiagramController shemaController) {
		if (shemaController != null) {
			if (shemaController.getDrawing() != null && shemaController.getDrawing().getDiagram() != null) {
				_controllers.remove(shemaController.getDrawing().getDiagram());
			}
		}
	}

	public String getWindowTitleforObject(FlexoObject object) {
		if (object == null) {
			return FlexoLocalization.localizedForKey("no_selection");
		}
		if (object instanceof ViewLibrary) {
			return FlexoLocalization.localizedForKey("view_library");
		}
		if (object instanceof View) {
			return ((View) object).getName();
		}
		return object.getFullyQualifiedName();
	}

	public void setProject(FlexoProject project) {
		viewLibraryBrowser.setRootObject(project.getViewLibrary());
	}
}
