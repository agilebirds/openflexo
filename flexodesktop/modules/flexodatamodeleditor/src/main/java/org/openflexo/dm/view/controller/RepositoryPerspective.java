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

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser.DMViewMode;
import org.openflexo.dm.view.DMBrowserView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.dm.ExternalRepository;
import org.openflexo.foundation.dm.FlexoExecutionModelRepository;
import org.openflexo.foundation.dm.JDKRepository;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.eo.EOPrototypeRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class RepositoryPerspective extends DMPerspective {

	private final DMBrowser _browser;
	private final DMBrowserView _browserView;
	private final JPanel leftView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public RepositoryPerspective(DMController controller) {
		super("repository_perspective", controller);
		_browser = new DMBrowser(controller);
		_browser.setDMViewMode(DMViewMode.Repositories);
		_browserView = new DMBrowserView(_browser, controller);
		leftView = new JPanel(new BorderLayout());
		leftView.add(_browserView, BorderLayout.CENTER);
		leftView.add(searchPanel, BorderLayout.NORTH);
		setTopLeftView(leftView);
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
		return DMEIconLibrary.DME_RP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DMEIconLibrary.DME_RP_SELECTED_ICON;
	}

	@Override
	public DMObject getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof DMObject && hasModuleViewForObject(proposedObject)) {
			return (DMObject) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		// Only DMProperty or Diagrams objects have no module view representation
		return !(object instanceof DMProperty) && !(object instanceof ERDiagram);
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof DMObject) {
			return getController().createDMView((DMObject) object);
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
		if (entity.getRepository() != null) {
			if (entity.getRepository() instanceof JDKRepository) {
				_browser.setFilterStatus(BrowserElementType.JDK_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			} else if (entity.getRepository() instanceof WORepository) {
				_browser.setFilterStatus(BrowserElementType.WO_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			} else if (entity.getRepository() instanceof ExternalRepository) {
				_browser.setFilterStatus(BrowserElementType.EXTERNAL_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			} else if (entity.getRepository() instanceof EOPrototypeRepository) {
				_browser.setFilterStatus(BrowserElementType.DM_EOPROTOTYPES_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			} else if (entity.getRepository() instanceof FlexoExecutionModelRepository) {
				_browser.setFilterStatus(BrowserElementType.DM_EXECUTION_MODEL_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_SHOWN);
			}
		}
	}

}