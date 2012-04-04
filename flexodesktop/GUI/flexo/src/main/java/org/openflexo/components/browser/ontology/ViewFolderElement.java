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
package org.openflexo.components.browser.ontology;

import javax.naming.InvalidNameException;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewFolder.DuplicateFolderNameException;

/**
 * Browser element representing the ontology library
 * 
 * @author sguerin
 * 
 */
public class ViewFolderElement extends BrowserElement {

	protected ViewFolderElement(ViewFolder folder, ProjectBrowser browser, BrowserElement parent) {
		super(folder, BrowserElementType.OE_SHEMA_FOLDER, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (ViewFolder subFolder : getFolder().getSubFolders()) {
			addToChilds(subFolder);
		}
		for (ViewDefinition def : getFolder().getShemas()) {
			addToChilds(def);
		}
	}

	@Override
	public String getName() {
		return getFolder().getName();
	}

	@Override
	public boolean isNameEditable() {
		return true;
	}

	@Override
	public void setName(String aName) throws FlexoException {
		try {
			getFolder().setName(aName);
		} catch (DuplicateFolderNameException e) {
			// Abort
			throw new FlexoException(e.getLocalizedMessage(), e);
		} catch (InvalidNameException e) {
			throw new FlexoException(e.getLocalizedMessage(), e);
		}
	}

	public ViewFolder getFolder() {
		return (ViewFolder) getObject();
	}

}
