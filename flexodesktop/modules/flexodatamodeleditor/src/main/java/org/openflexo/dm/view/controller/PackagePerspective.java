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

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.components.browser.ProjectBrowser.ObjectAddedToSelectionEvent;
import org.openflexo.dm.view.DMBrowserView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class PackagePerspective extends DMPerspective {

	private final DMController _controller;

	private final DMBrowser _browser;
	private final PropertiesBrowser propertiesBrowser;
	private final DMBrowserView _browserView;
	private final DMBrowserView propertiesBrowserView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public PackagePerspective(DMController controller) {
		super("package_perspective", controller);
		_controller = controller;
		_browser = new DMBrowser(controller);
		_browser.setDMViewMode(DMViewMode.Packages);
		_browserView = new DMBrowserView(_browser, _controller) {
			@Override
			public void objectAddedToSelection(ObjectAddedToSelectionEvent event) {
				if (event.getAddedObject() instanceof DMEntity) {
					propertiesBrowser.deleteBrowserListener(this);
					propertiesBrowser.setRepresentedEntity((DMEntity) event.getAddedObject());
					propertiesBrowser.update();
					propertiesBrowser.addBrowserListener(this);
				}
				super.objectAddedToSelection(event);
			}
		};
		propertiesBrowser = new PropertiesBrowser(controller);
		propertiesBrowserView = new DMBrowserView(propertiesBrowser, controller);
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(searchPanel, BorderLayout.NORTH);
		panel.add(_browserView);
		setTopLeftView(panel);
		setBottomLeftView(propertiesBrowserView);
	}

	@Override
	public void setProject(FlexoProject project) {
		super.setProject(project);
		_browser.setRootObject(project != null ? project.getDataModel() : null);
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DMEIconLibrary.DME_PP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DMEIconLibrary.DME_PP_SELECTED_ICON;
	}

	@Override
	public DMObject getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof DMObject && hasModuleViewForObject(proposedObject)) {
			return (DMObject) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		// Only DMProperty or Diagrams objects have no module view representation
		return object instanceof DMProperty || object instanceof ERDiagram;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof DMObject) {
			return _controller.createDMView((DMObject) object);
		} else {
			return null;
		}
	}

	@Override
	protected boolean browserMayRepresent(DMEntity entity) {
		return _browser.mayRepresents(entity);
	}

	@Override
	protected void changeBrowserFiltersFor(DMEntity entity) {

	}

}