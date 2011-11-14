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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.ViewPointFolder;

/**
 * Browser element representing a calc folder
 * 
 * @author sguerin
 * 
 */
public class CalcFolderElement extends BrowserElement {

	protected CalcFolderElement(ViewPointFolder folder, ProjectBrowser browser, BrowserElement parent) {
		super(folder, BrowserElementType.CALC_FOLDER, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (ViewPointFolder subFolder : getFolder().getChildren()) {
			addToChilds(subFolder);
		}
		for (ViewPoint calc : getFolder().getViewPoints()) {
			addToChilds(calc);
		}
	}

	@Override
	public String getName() {
		return getFolder().getName();
	}

	protected ViewPointFolder getFolder() {
		return (ViewPointFolder) getObject();
	}

}
