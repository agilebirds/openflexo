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
package org.openflexo.ie.view.controller;

import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;

import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.PartialComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.IECst;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.palette.IEPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class ComponentPerspective extends FlexoPerspective<ComponentInstance> {

	private final IEController _controller;
	private final IEPalette iePalette;
	private JSplitPane splitPaneWithIEPaletteAndDocInspectorPanel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ComponentPerspective(IEController controller) {
		super("component_editor_perspective");
		_controller = controller;
		iePalette = new IEPalette(controller);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return SEIconLibrary.COMPONENT_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return SEIconLibrary.COMPONENT_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public ComponentInstance getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof ComponentInstance) {
			return (ComponentInstance) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof ComponentInstance;
	}

	@Override
	public ModuleView<ComponentInstance> createModuleViewForObject(ComponentInstance object, FlexoController controller) {
		ComponentDefinition component = object.getComponentDefinition();
		if (component != null) {
			if (IEController.logger.isLoggable(Level.INFO)) {
				IEController.logger.info("Building EditZone for " + component.getName() + " (" + component.getClass() + ")");
			}
			if (IEController.logger.isLoggable(Level.INFO)) {
				IEController.logger.info("Load component...");
			}
			/*FlexoComponentResource componentResource = component.getComponentResource();
			if (IEController.logger.isLoggable(Level.INFO))
				IEController.logger.info("Accessed to resource: " + componentResource.getResourceIdentifier());*/
			if (!component.isLoaded()) {
				ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_component ") + component.getName(), 1);
				_controller.getSelectionManager().setSelectedObject(component.getWOComponent(ProgressWindow.instance()));
				ProgressWindow.hideProgressWindow();
			}
			IEWOComponentView returned = null;
			if (object instanceof PartialComponentInstance) {
				returned = new IEReusableWidgetComponentView((IEController) controller, (PartialComponentInstance) object);
			} else {
				returned = new IEWOComponentView((IEController) controller, object);
			}
			returned.doLayout();
			// componentResource.setResourceData(returned.getModel());
			return returned;
		}
		return null;
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView instanceof IEWOComponentView) {
			ComponentInstance ci = ((IEWOComponentView) moduleView).getRepresentedObject();
			ComponentDefinition component = ci.getComponentDefinition();
			_controller.getComponentLibraryBrowser().focusOn(component);
			_controller.getSelectionManager().setSelectedObject(component.getWOComponent());
			_controller.getComponentBrowser().setCurrentComponent(component);
			_controller.getComponentBrowser().focusOn(component);
		}
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return _controller.getSplitPaneWithBrowsers();
	}

	@Override
	public boolean doesPerspectiveControlRightView() {
		return true;
	}

	@Override
	public JComponent getRightView() {
		return getSplitPaneWithIEPaletteAndDocInspectorPanel();
	}

	/**
	 * Return Split pane with Role palette and doc inspector panel Disconnect doc inspector panel from its actual parent
	 * 
	 * @return
	 */
	protected JSplitPane getSplitPaneWithIEPaletteAndDocInspectorPanel() {
		if (splitPaneWithIEPaletteAndDocInspectorPanel == null) {
			splitPaneWithIEPaletteAndDocInspectorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, iePalette,
					_controller.getDisconnectedDocInspectorPanel());
			splitPaneWithIEPaletteAndDocInspectorPanel.setResizeWeight(0);
			splitPaneWithIEPaletteAndDocInspectorPanel.setDividerLocation(IECst.PALETTE_DOC_SPLIT_LOCATION);
			splitPaneWithIEPaletteAndDocInspectorPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		if (splitPaneWithIEPaletteAndDocInspectorPanel.getBottomComponent() == null) {
			splitPaneWithIEPaletteAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
		}
		new FlexoSplitPaneLocationSaver(splitPaneWithIEPaletteAndDocInspectorPanel, "IEPaletteAndDocInspectorPanel");
		return splitPaneWithIEPaletteAndDocInspectorPanel;
	}

	public IEPalette getIEPalette() {
		return iePalette;
	}

}