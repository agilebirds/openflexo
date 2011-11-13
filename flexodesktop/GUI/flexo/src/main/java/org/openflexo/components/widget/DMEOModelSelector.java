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
package org.openflexo.components.widget;

import java.awt.Dimension;

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.ProjectDatabaseRepository;
import org.openflexo.foundation.dm.eo.DMEOModel;
import org.openflexo.foundation.rm.FlexoProject;

public class DMEOModelSelector extends AbstractBrowserSelector<DMEOModel> {

	protected static final String EMPTY_STRING = "";

	private ProjectDatabaseRepository _repository;

	public DMEOModelSelector(DMEOModel eomodel) {
		super(null, eomodel, DMEOModel.class);
	}

	public DMEOModelSelector(FlexoProject project, DMEOModel eomodel, int cols) {
		super(project, eomodel, DMEOModel.class, cols);
	}

	public ProjectDatabaseRepository getCVSRepositoryList() {
		return _repository;
	}

	@Override
	protected ProjectDatabaseRepositorySelectorPanel makeCustomPanel(DMEOModel editedObject) {
		return new ProjectDatabaseRepositorySelectorPanel();
	}

	@Override
	public String renderedString(DMEOModel editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class ProjectDatabaseRepositorySelectorPanel extends AbstractSelectorPanel<DMEOModel> {
		protected ProjectDatabaseRepositorySelectorPanel() {
			super(DMEOModelSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new DMEOModelBrowser(project);
		}

		@Override
		public Dimension getDefaultSize() {
			Dimension returned = _browserView.getDefaultSize();
			returned.width = returned.width;
			returned.height = returned.height - 100;
			return returned;
		}
	}

	protected class DMEOModelBrowser extends ProjectBrowser {

		protected DMEOModelBrowser(FlexoProject project) {
			super(project, false);
			init();
		}

		@Override
		public void configure() {
			// setFilterStatus(BrowserElementType.DM_REPOSITORY, ElementTypeBrowserFilter.HIDE);
			setFilterStatus(BrowserElementType.DM_ENTITY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_EOATTRIBUTE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_EOENTITY, BrowserFilterStatus.HIDE);
			// setFilterStatus(BrowserElementType.DM_EOMODEL, ElementTypeBrowserFilter.HIDE);
			setFilterStatus(BrowserElementType.DM_EORELATIONSHIP, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_METHOD, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_MODEL, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_PACKAGE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);
			// setFilterStatus(BrowserElementType.DM_REPOSITORY, ElementTypeBrowserFilter.HIDE);
			// setFilterStatus(BrowserElementType.DM_REPOSITORY_FOLDER, ElementTypeBrowserFilter.HIDE);
			// setFilterStatus(BrowserElementType.DM_EOREPOSITORY, ElementTypeBrowserFilter.HIDE);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getProject().getDataModel();
		}
	}

}
