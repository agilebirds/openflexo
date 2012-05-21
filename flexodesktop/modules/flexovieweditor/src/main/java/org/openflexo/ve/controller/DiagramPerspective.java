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

import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.view.BrowserView.SelectionPolicy;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.ImportedOntology;
import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.view.AbstractViewObject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.ve.VECst;
import org.openflexo.ve.shema.VEShemaController;
import org.openflexo.ve.shema.VEShemaModuleView;
import org.openflexo.ve.view.VEBrowserView;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class DiagramPerspective extends FlexoPerspective<AbstractViewObject> {

	private final VEController _controller;

	private final ShemaLibraryBrowser _browser;
	private final ShemaBrowser shemaBrowser;
	private final VEBrowserView _browserView;
	private final VEBrowserView shemaBrowserView;

	private final Hashtable<View, VEShemaController> _controllers;
	private final Hashtable<VEShemaController, JSplitPane> _splitPaneForProcess;

	private final JSplitPane splitPane;

	private final JLabel infoLabel;

	private static final JPanel EMPTY_RIGHT_VIEW = new JPanel();

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public DiagramPerspective(VEController controller) {
		super("diagram_perspective");
		_controller = controller;
		_controllers = new Hashtable<View, VEShemaController>();
		_splitPaneForProcess = new Hashtable<VEShemaController, JSplitPane>();
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
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _browserView, shemaBrowserView);
		splitPane.setDividerLocation(0.7);
		splitPane.setResizeWeight(0.7);
		infoLabel = new JLabel("Diagram perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	public void focusOnShema(View shema) {
		shemaBrowser.deleteBrowserListener(_browserView);
		shemaBrowser.setRepresentedShema(shema);
		shemaBrowser.update();
		shemaBrowser.addBrowserListener(_browserView);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return VEIconLibrary.VE_SP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
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
		return object instanceof AbstractViewObject;
	}

	@Override
	public ModuleView<? extends AbstractViewObject> createModuleViewForObject(AbstractViewObject object, FlexoController controller) {
		if (object instanceof View) {
			return getControllerForShema((View) object).getModuleView();
		}
		if (object instanceof ViewDefinition) {
			ViewDefinition viewDefinition = (ViewDefinition) object;
			View shema = viewDefinition.getShema();
			if (shema == null) {
				FlexoController.notify(FlexoLocalization.localizedForKey("could_not_load_view") + " " + viewDefinition.getName() + ". "
						+ FlexoLocalization.localizedForKey("make_sure_you_have_the_view_point_with_uri") + " "
						+ viewDefinition._getCalcURI() + " " + FlexoLocalization.localizedForKey("in_your_resource_center"));
			} else {
				return getControllerForShema(shema).getModuleView();
			}
		}
		return new EmptyPanel<AbstractViewObject>(controller, this, object);
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return splitPane;
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
		_splitPaneForProcess.remove(shemaController);
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
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

	@Override
	public boolean doesPerspectiveControlRightView() {
		return true;
	}

	@Override
	public JComponent getRightView() {
		if (getCurrentShemaModuleView() == null) {
			return EMPTY_RIGHT_VIEW;
		}
		return getSplitPaneWithPalettesAndDocInspectorPanel();
	}

	/**
	 * Return Split pane with Role palette and doc inspector panel Disconnect doc inspector panel from its actual parent
	 * 
	 * @return
	 */
	protected JSplitPane getSplitPaneWithPalettesAndDocInspectorPanel() {
		JSplitPane splitPaneWithPalettesAndDocInspectorPanel = _splitPaneForProcess.get(getCurrentShemaModuleView().getController());
		if (splitPaneWithPalettesAndDocInspectorPanel == null) {
			splitPaneWithPalettesAndDocInspectorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getCurrentShemaModuleView()
					.getController().getPaletteView(), _controller.getDisconnectedDocInspectorPanel());
			splitPaneWithPalettesAndDocInspectorPanel.setResizeWeight(0);
			splitPaneWithPalettesAndDocInspectorPanel.setDividerLocation(VECst.PALETTE_DOC_SPLIT_LOCATION);
			_splitPaneForProcess.put(getCurrentShemaModuleView().getController(), splitPaneWithPalettesAndDocInspectorPanel);
		}
		if (splitPaneWithPalettesAndDocInspectorPanel.getBottomComponent() == null) {
			splitPaneWithPalettesAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
		}
		new FlexoSplitPaneLocationSaver(splitPaneWithPalettesAndDocInspectorPanel, "OEPaletteAndDocInspectorPanel");
		return splitPaneWithPalettesAndDocInspectorPanel;
	}

}