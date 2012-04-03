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

import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select a DMEntity while browsing the data model
 * 
 * @author sguerin
 * 
 */
public class DMEntitySelector<D extends DMEntity> extends AbstractBrowserSelector<D> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	public DMEntitySelector(D entity) {
		super(entity != null ? entity.getProject() : null, entity, (Class<D>) DMEntity.class);
	}

	public DMEntitySelector(FlexoProject project, D entity, Class<D> entityClass) {
		super(project, entity, entityClass);
	}

	public DMEntitySelector(FlexoProject project, D entity, Class<D> entityClass, int cols) {
		super(project, entity, entityClass, cols);
	}

	DMModel getDataModel() {
		if (getProject() != null) {
			return getProject().getDataModel();
		}
		return null;
	}

	@Override
	protected AbstractSelectorPanel<D> makeCustomPanel(D editedObject) {
		return new DMEntitySelectorPanel();
	}

	@Override
	public String renderedString(D editedObject) {
		if (editedObject != null) {
			return ((DMEntity) editedObject).getLocalizedName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class DMEntitySelectorPanel extends AbstractSelectorPanel<D> {
		protected DMEntitySelectorPanel() {
			super(DMEntitySelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new DataModelBrowser(/* project.getDataModel() */);
		}

	}

	protected class DataModelBrowser extends ProjectBrowser {

		// private DMModel _dmModel;

		protected DataModelBrowser(/* DMModel dataModel */) {
			super((getDataModel() != null ? getDataModel().getProject() : null), false);
			init();
			setDMViewMode(DMViewMode.Packages);
			setRowHeight(16);
		}

		@Override
		public void configure() {
			setFilterStatus(BrowserElementType.DM_REPOSITORY_FOLDER, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN, true);
			setFilterStatus(BrowserElementType.DM_REPOSITORY, BrowserFilterStatus.OPTIONAL_INITIALLY_HIDDEN, true);
			setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_EOATTRIBUTE, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_EORELATIONSHIP, BrowserFilterStatus.HIDE);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getDataModel();
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

}
