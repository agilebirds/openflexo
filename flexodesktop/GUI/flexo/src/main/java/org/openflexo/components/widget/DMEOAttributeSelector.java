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
import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select a DMEOAttribute while browsing the data model
 * 
 * @author sguerin
 * 
 */
public class DMEOAttributeSelector extends AbstractBrowserSelector<DMEOAttribute> {

	protected static final String EMPTY_STRING = "";

	public DMEOAttributeSelector(FlexoProject project, DMEOAttribute attribute) {
		super(project, attribute, DMEOAttribute.class);
	}

	public DMEOAttributeSelector(FlexoProject project, DMEOAttribute attribute, int cols) {
		super(project, attribute, DMEOAttribute.class, cols);
	}

	DMModel getDataModel() {
		if (getProject() != null) {
			return getProject().getDataModel();
		}
		return null;
	}

	@Override
	protected DMEOAttributeSelectorPanel makeCustomPanel(DMEOAttribute editedObject) {
		return new DMEOAttributeSelectorPanel();
	}

	@Override
	public String renderedString(DMEOAttribute editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return EMPTY_STRING;
	}

	protected class DMEOAttributeSelectorPanel extends AbstractSelectorPanel<DMEOAttribute> {
		protected DMEOAttributeSelectorPanel() {
			super(DMEOAttributeSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new DataModelBrowser();
		}

	}

	protected class DataModelBrowser extends ProjectBrowser {

		protected DataModelBrowser() {
			super(getDataModel().getProject(), false);
			init();
		}

		@Override
		public void configure() {
			setFilterStatus(BrowserElementType.DM_PROPERTY, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DM_EOATTRIBUTE, BrowserFilterStatus.SHOW);
			setFilterStatus(BrowserElementType.DM_EORELATIONSHIP, BrowserFilterStatus.HIDE);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getDataModel();
		}
	}

}
