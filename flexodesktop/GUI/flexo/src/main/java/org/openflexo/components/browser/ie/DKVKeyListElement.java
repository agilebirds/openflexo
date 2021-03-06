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
package org.openflexo.components.browser.ie;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.dkv.Domain.KeyList;
import org.openflexo.foundation.dkv.Key;

/**
 * @author gpolet
 * 
 */
public class DKVKeyListElement extends BrowserElement {

	/**
	 * @param object
	 * @param elementType
	 * @param browser
	 */
	public DKVKeyListElement(KeyList object, ProjectBrowser browser, BrowserElement parent) {
		super(object, BrowserElementType.DKV_KEY_LIST, browser, parent);
	}

	/**
	 * Overrides buildChildrenVector
	 * 
	 * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
	 */
	@Override
	protected void buildChildrenVector() {
		KeyList kl = (KeyList) getObject();
		Object[] o = kl.getKeyList();
		for (int i = 0; i < o.length; i++) {
			if (o[i] != null) {
				addToChilds((Key) o[i]);
			}
		}
	}

}
