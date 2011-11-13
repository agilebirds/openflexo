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
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.BrowserFilter.BrowserFilterStatus;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.Domain;
import org.openflexo.foundation.dkv.Key;
import org.openflexo.foundation.rm.FlexoProject;

/**
 * Widget allowing to select a Key given a Domain browsing DKV model
 * 
 * @author sguerin
 * 
 */
public class KeySelector extends AbstractBrowserSelector<Key> {

	protected static final String EMPTY_STRING = "";
	protected String STRING_REPRESENTATION_WHEN_NULL = EMPTY_STRING;

	public KeySelector(FlexoProject project, Key key) {
		super(project, key, Key.class);
	}

	public KeySelector(FlexoProject project, Key key, int cols) {
		super(project, key, Key.class, cols);
	}

	DKVModel getDKVModel() {
		if (getProject() != null) {
			return getProject().getDKVModel();
		}
		return null;
	}

	@Override
	protected KeySelectorPanel makeCustomPanel(Key editedObject) {
		return new KeySelectorPanel();
	}

	@Override
	public String renderedString(Key editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return STRING_REPRESENTATION_WHEN_NULL;
	}

	public void setDomain(Domain aDomain) {
		setRootObject(aDomain);
	}

	protected class KeySelectorPanel extends AbstractSelectorPanel<Key> {
		protected KeySelectorPanel() {
			super(KeySelector.this);
		}

		@Override
		protected ProjectBrowser createBrowser(FlexoProject project) {
			return new KeyBrowser(/* project.getDataModel() */);
		}

	}

	protected class KeyBrowser extends ProjectBrowser {

		protected KeyBrowser() {
			super((getDKVModel() != null ? getDKVModel().getProject() : null), false);
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
