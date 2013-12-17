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
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.FlexoProject;

/**
 * Widget allowing to select a Domain while browsing the DKV model
 * 
 * @author sguerin
 * 
 */
public class DomainSelector extends AbstractBrowserSelector<Domain> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	public DomainSelector(FlexoProject project, Domain domain) {
		super(project, domain, Domain.class);
	}

	public DomainSelector(FlexoProject project, Domain domain, int cols) {
		super(project, domain, Domain.class, cols);
	}

	DKVModel getDKVModel() {
		if (getProject() != null) {
			return getProject().getDKVModel();
		}
		return null;
	}

	@Override
	protected DomainSelectorPanel makeCustomPanel(Domain editedObject) {
		return new DomainSelectorPanel();
	}

	@Override
	public String renderedString(Domain editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	protected class DomainSelectorPanel extends AbstractSelectorPanel<Domain> {
		protected DomainSelectorPanel() {
			super(DomainSelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new DomainBrowser(/* project.getDataModel() */);
		}

	}

	protected class DomainBrowser extends ProjectBrowser {

		protected DomainBrowser() {
			super(getDKVModel() != null ? getDKVModel().getProject() : null, false);
			init();
		}

		@Override
		public void configure() {
			setFilterStatus(BrowserElementType.DKV_DOMAIN_LIST, BrowserFilterStatus.HIDE, true);
			setFilterStatus(BrowserElementType.DKV_LANGUAGE_LIST, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_KEY_LIST, BrowserFilterStatus.HIDE, true);
			setFilterStatus(BrowserElementType.DKV_VALUE_LIST, BrowserFilterStatus.HIDE);
			setFilterStatus(BrowserElementType.DKV_VALUE, BrowserFilterStatus.HIDE);
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return getDKVModel();
		}
	}

	public void setNullStringRepresentation(String aString) {
		STRING_REPRESENTATION_WHEN_NULL = aString;
	}

}
