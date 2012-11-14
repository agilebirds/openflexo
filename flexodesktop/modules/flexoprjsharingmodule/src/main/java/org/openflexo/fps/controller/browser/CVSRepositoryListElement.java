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
package org.openflexo.fps.controller.browser;

import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.localization.FlexoLocalization;

public class CVSRepositoryListElement extends FPSBrowserElement {
	public CVSRepositoryListElement(CVSRepositoryList repList, ProjectBrowser browser, BrowserElement parent) {
		super(repList, BrowserElementType.CVS_REPOSITORY_LIST, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("buildChildrenVector() for CVSRepositoryListElement");
		}
		for (CVSRepository rep : (Vector<CVSRepository>) getCVSRepositoryList().getCVSRepositories().clone()) {
			addToChilds(rep);
		}
	}

	@Override
	public String getName() {
		if (getCVSRepositoryList().getCVSRepositories().size() == 0) {
			return FlexoLocalization.localizedForKey("no_cvs_repositories");
		} else {
			return FlexoLocalization.localizedForKey("cvs_repositories");
		}
	}

	public CVSRepositoryList getCVSRepositoryList() {
		return (CVSRepositoryList) getObject();
	}

}
