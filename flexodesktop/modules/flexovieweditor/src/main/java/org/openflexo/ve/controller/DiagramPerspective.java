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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.AbstractViewObject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.inspector.FIBInspectorPanel;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.ve.shema.VEShemaController;
import org.openflexo.ve.shema.VEShemaModuleView;
import org.openflexo.ve.view.VEBrowserView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class DiagramPerspective extends FlexoPerspective {

	private final VEController _controller;

	private final ShemaLibraryBrowser _browser;
	private final ShemaBrowser shemaBrowser;
	private final VEBrowserView _browserView;
	private final VEBrowserView shemaBrowserView;

	private final Map<View, VEShemaController> _controllers;
	private final Map<VEShemaController, JSplitPane> _rightPanels;

	private final JLabel infoLabel;

	private final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	private final FIBInspectorPanel inspectorPanel;

	private final JScrollPane inspectorPanelScrollPane;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public DiagramPerspective(VEController controller) {
		super("diagram_perspective");
		EMPTY_RIGHT_VIEW.setPreferredSize(new Dimension(300, 300));
		_controller = controller;
		_controllers = new Hashtable<View, VEShemaController>();
		_rightPanels = new Hashtable<VEShemaController, JSplitPane>();
		_browser = new ShemaLibraryBrowser(controller);
		_browserView = new VEBrowserView(_browser, _controller, SelectionPolicy.ParticipateToSelection) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof View) {
					focusOnShema((View) object);
				}
			}

			/*  public void objectAddedToSelection(ObjectAddedToSelectionEvent event)
			  {
			  	if (event.getAddedObject() instanceof ERDiagram) {
			  		diagramBrowser.deleteBrowserListener(this);
			  		diagramBrowser.setRepresentedDiagram((ERDiagram)event.getAddedObject());
			  		diagramBrowser.update();
			  		diagramBrowser.addBrowserListener(this);
			  	}
			  	super.objectAddedToSelection(event);
			  }			*/
		};
		shemaBrowser = new ShemaBrowser(controller);
		shemaBrowserView = new VEBrowserView(shemaBrowser, controller, SelectionPolicy.ForceSelection);
		infoLabel = new JLabel("Diagram perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);

		// Initialized inspector panel
		inspectorPanel = new FIBInspectorPanel(controller.getModuleInspectorController());
		inspectorPanelScrollPane = new JScrollPane(inspectorPanel);
		setTopLeftView(_browserView);
		setBottomLeftView(shemaBrowserView);
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentShemaModuleView() == null) {
			return EMPTY_RIGHT_VIEW;
		}
		return getCurrentShemaModuleView().getController().getPaletteView();
	}

	@Override
	public JComponent getBottomRightView() {
		if (getCurrentShemaModuleView() == null) {
			return null;
		}
		return inspectorPanelScrollPane;
	}

	public void focusOnShema(View shema) {
		shemaBrowser.deleteBrowserListener(_browserView);
		shemaBrowser.setRootObject(shema);
		shemaBrowser.addBrowserListener(_browserView);
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

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return VEIconLibrary.VE_SP_SELECTED_ICON;
	}

	@Override
	public AbstractViewObject getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof View) {
			return (View) proposedObject;
		}
		return proposedObject.getProject().getShemaLibrary();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof View || object instanceof ViewDefinition;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof View) {
			return getControllerForShema((View) object).getModuleView();
		}
		if (object instanceof ViewDefinition) {
			ViewDefinition viewDefinition = (ViewDefinition) object;
			View shema = viewDefinition.getView();
			if (shema == null) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_view") + " " + viewDefinition.getName() + ". "
						+ FlexoLocalization.localizedForKey("make_sure_you_have_the_view_point_with_uri") + " "
						+ viewDefinition._getCalcURI() + " " + FlexoLocalization.localizedForKey("in_your_resource_center"));
			} else {
				return getControllerForShema(shema).getModuleView();
			}
		}
		return null;
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentShemaModuleView() != null) {
			return getCurrentShemaModuleView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	public VEShemaModuleView getCurrentShemaModuleView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof VEShemaModuleView) {
			return (VEShemaModuleView) _controller.getCurrentModuleView();
		}
		return null;
	}

	public VEShemaController getControllerForShema(View shema) {
		VEShemaController returned = _controllers.get(shema);
		if (returned == null) {
			returned = new VEShemaController(_controller, shema);
			_controllers.put(shema, returned);
		}
		return returned;
	}

	public void removeFromControllers(VEShemaController shemaController) {
		_controllers.remove(shemaController.getDrawing().getShema());
	}

	public String getWindowTitleforObject(FlexoModelObject object) {
		if (object == null) {
			return FlexoLocalization.localizedForKey("no_selection");
		}
		if (object instanceof ViewLibrary) {
			return FlexoLocalization.localizedForKey("shema_library");
		}
		if (object instanceof ViewDefinition) {
			return ((ViewDefinition) object).getName();
		}
		if (object instanceof View) {
			return ((View) object).getName();
		}
		if (object instanceof ProjectOntology) {
			return FlexoLocalization.localizedForKey("project_ontology");
		}
		if (object instanceof ImportedOntology) {
			return ((ImportedOntology) object).getName();
		}
		return object.getFullyQualifiedName();
	}

	public void setProject(FlexoProject project) {
		_browser.setRootObject(project.getShemaLibrary());
	}
}