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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.openflexo.components.ProgressWindow;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.PartialComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.ProjectClosedNotification;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.IEReusableWidgetComponentView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.ie.view.palette.IEPalette;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

class ComponentPerspective extends FlexoPerspective implements FlexoObserver {

	private final IEController _controller;

	private Map<FlexoProject, IEPalette> palettes;
	private IEPalette currentPalette;

	private JPanel topRightDummy;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public ComponentPerspective(IEController controller) {
		super("component_editor_perspective");
		_controller = controller;
		topRightDummy = new JPanel();
		palettes = new HashMap<FlexoProject, IEPalette>();
		setTopLeftView(controller.getComponentLibraryBrowserView());
		setBottomLeftView(controller.getComponentBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return SEIconLibrary.COMPONENT_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
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
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof ComponentInstance) {
			ComponentInstance ci = (ComponentInstance) object;
			ComponentDefinition component = ci.getComponentDefinition();
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
					returned = new IEWOComponentView((IEController) controller, ci);
				}
				returned.doLayout();
				// componentResource.setResourceData(returned.getModel());
				return returned;
			}
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView instanceof IEWOComponentView) {
			ComponentInstance ci = ((IEWOComponentView) moduleView).getRepresentedObject();
			ComponentDefinition component = ci.getComponentDefinition();
			_controller.getComponentLibraryBrowser().focusOn(component);
			_controller.getSelectionManager().setSelectedObject(component.getWOComponent());
			_controller.getComponentBrowser().setRootObject(component);
		}
	}

	public IEPalette getCurrentPalette() {
		return currentPalette;
	}

	@Override
	public JComponent getTopRightView() {
		currentPalette = getIEPalette(_controller.getProject());
		if (currentPalette != null) {
			return currentPalette;
		} else {
			return topRightDummy;
		}
	}

	public IEPalette getIEPalette(FlexoProject flexoProject) {
		if (flexoProject == null) {
			return null;
		}
		IEPalette palette = palettes.get(flexoProject);
		if (palette == null) {
			palettes.put(flexoProject, palette = new IEPalette(_controller, flexoProject));
			flexoProject.addObserver(this);
		}
		return palette;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable instanceof FlexoProject) {
			if (dataModification instanceof ProjectClosedNotification) {
				IEPalette palette = palettes.get(observable);
				if (palette != null) {
					palette.disposePalettes();
				}
				palettes.remove(observable);
				observable.deleteObserver(this);
			}
		}
	}

	public void disposePalettes() {
		for (Map.Entry<FlexoProject, IEPalette> e : palettes.entrySet()) {
			e.getKey().deleteObserver(this);
			e.getValue().disposePalettes();
		}
		palettes.clear();

	}
}