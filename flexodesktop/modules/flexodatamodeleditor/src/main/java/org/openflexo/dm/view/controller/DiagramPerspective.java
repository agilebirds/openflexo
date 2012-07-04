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
package org.openflexo.dm.view.controller;

import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import org.openflexo.FlexoCst;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.dm.view.DMBrowserView;
import org.openflexo.dm.view.erdiagram.DiagramView;
import org.openflexo.dm.view.erdiagram.ERDiagramController;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class DiagramPerspective extends DMPerspective {

	private final DMController _controller;

	private final DMBrowser _browser;
	private final DiagramBrowser diagramBrowser;
	private final DMBrowserView _browserView;
	private final DMBrowserView diagramBrowserView;

	private final JSplitPane splitPane;

	private final Hashtable<ERDiagram, ERDiagramController> _controllers;

	private JSplitPane splitPaneWithRolePaletteAndDocInspectorPanel;

	private final JLabel infoLabel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public DiagramPerspective(DMController controller) {
		super("er_diagram_perspective", controller);
		_controller = controller;
		_controllers = new Hashtable<ERDiagram, ERDiagramController>();
		_browser = new DMBrowser(controller, true);
		_browser.setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
		_browser.setFilterStatus(BrowserElementType.DM_EOENTITY, BrowserFilterStatus.HIDE);
		if (controller.getDataModel().getJDKRepository().hasDiagrams()) {
			_browser.setFilterStatus(BrowserElementType.JDK_REPOSITORY, BrowserFilterStatus.SHOW);
		}
		if (controller.getDataModel().getWORepository().hasDiagrams()) {
			_browser.setFilterStatus(BrowserElementType.WO_REPOSITORY, BrowserFilterStatus.SHOW);
		}
		for (ExternalRepository rep : controller.getDataModel().getExternalRepositories()) {
			if (rep.hasDiagrams()) {
				_browser.setFilterStatus(BrowserElementType.EXTERNAL_REPOSITORY, BrowserFilterStatus.SHOW);
			}
		}
		if (controller.getDataModel().getEOPrototypeRepository().hasDiagrams()) {
			_browser.setFilterStatus(BrowserElementType.DM_EOPROTOTYPES_REPOSITORY, BrowserFilterStatus.SHOW);
		}
		if (controller.getDataModel().getExecutionModelRepository().hasDiagrams()) {
			_browser.setFilterStatus(BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY, BrowserFilterStatus.SHOW);
		}

		_browser.setDMViewMode(DMViewMode.Diagrams);
		_browserView = new DMBrowserView(_browser, _controller) {
			@Override
			public void treeDoubleClick(FlexoModelObject object) {
				super.treeDoubleClick(object);
				if (object instanceof ERDiagram) {
					focusOnDiagram((ERDiagram) object);
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
		diagramBrowser = new DiagramBrowser(controller);
		diagramBrowserView = new DMBrowserView(diagramBrowser, controller);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _browserView, diagramBrowserView);
		splitPane.setDividerLocation(0.7);
		splitPane.setResizeWeight(0.7);
		infoLabel = new JLabel("ALT-drag to define inheritance, CTRL-drag to define properties");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	public void focusOnDiagram(ERDiagram diagram) {
		diagramBrowser.deleteBrowserListener(_browserView);
		diagramBrowser.setRepresentedDiagram(diagram);
		diagramBrowser.update();
		diagramBrowser.addBrowserListener(_browserView);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DMEIconLibrary.DME_DP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DMEIconLibrary.DME_DP_SELECTED_ICON;
	}

	@Override
	public ERDiagram getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof ERDiagram) {
			return (ERDiagram) proposedObject;
		}
		if (proposedObject instanceof DMObject) {
			if (((DMObject) proposedObject).getDMModel().getDiagrams().size() > 0) {
				return ((DMObject) proposedObject).getDMModel().getDiagrams().firstElement();
			}
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof ERDiagram;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject diagram, FlexoController controller) {
		if (diagram instanceof ERDiagram) {
			return getControllerForDiagram((ERDiagram) diagram).getDrawingView();
		} else {
			return null;
		}
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
		if (getCurrentDiagramView() != null) {
			return getCurrentDiagramView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	@Override
	public boolean doesPerspectiveControlRightView() {
		return false;
	}

	/*@Override
	public JComponent getRightView() 
	{
		return getSplitPaneWithRolePaletteAndDocInspectorPanel();
	}*/

	public DiagramView getCurrentDiagramView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof DiagramView) {
			return (DiagramView) _controller.getCurrentModuleView();
		}
		return null;
	}

	public ERDiagramController getControllerForDiagram(ERDiagram diagram) {
		ERDiagramController returned = _controllers.get(diagram);
		if (returned == null) {
			returned = new ERDiagramController(_controller, diagram);
			_controllers.put(diagram, returned);
		}
		return returned;
	}

	/**
	 * Return Split pane with Role palette and doc inspector panel Disconnect doc inspector panel from its actual parent
	 * 
	 * @return
	 */
	/*protected JSplitPane getSplitPaneWithRolePaletteAndDocInspectorPanel()
	{
		if (splitPaneWithRolePaletteAndDocInspectorPanel == null) {
			splitPaneWithRolePaletteAndDocInspectorPanel = new JSplitPane(
					JSplitPane.VERTICAL_SPLIT,
					getRoleEditorController().getPalette().getPaletteView(),
					_controller.getDisconnectedDocInspectorPanel());
			splitPaneWithRolePaletteAndDocInspectorPanel.setResizeWeight(0);
			splitPaneWithRolePaletteAndDocInspectorPanel.setDividerLocation(WKFCst.PALETTE_DOC_SPLIT_LOCATION);
		}
		if (splitPaneWithRolePaletteAndDocInspectorPanel.getBottomComponent() == null) {
			splitPaneWithRolePaletteAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
		}
		new FlexoSplitPaneLocationSaver(splitPaneWithRolePaletteAndDocInspectorPanel,"RolePaletteAndDocInspectorPanel");
		return splitPaneWithRolePaletteAndDocInspectorPanel;
	}*/

	protected class DiagramBrowser extends DMBrowser {
		private ERDiagram representedDiagram = null;

		protected DiagramBrowser(DMController controller) {
			super(controller, true);
		}

		protected ERDiagram getRepresentedDiagram() {
			return representedDiagram;
		}

		protected void setRepresentedDiagram(ERDiagram representedDiagram) {
			this.representedDiagram = representedDiagram;
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return representedDiagram;
		}

	}

	public void removeFromERControllers(ERDiagramController diagramController) {
		_controllers.remove(diagramController.getDrawing().getDiagram());
	}

	@Override
	protected boolean browserMayRepresent(DMEntity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void changeBrowserFiltersFor(DMEntity entity) {
		// TODO Auto-generated method stub

	}

}