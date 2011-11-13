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
package org.openflexo.doceditor.controller.browser;

import java.util.logging.Level;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.DocTypeAdded;
import org.openflexo.foundation.cg.DocTypeRemoved;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCRepository;

public class TOCDataElement extends DEBrowserElement {
	public TOCDataElement(TOCData tocData, ProjectBrowser browser, BrowserElement parent) {
		super(tocData, BrowserElementType.TOC_DATA, browser, parent);
		tocData.getProject().addObserver(this);
	}

	@Override
	public void delete() {
		getTOCData().getProject().deleteObserver(this);
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		for (TOCRepository repository : getTOCData().getRepositories()) {
			addToChilds(repository);
		}
		for (DocType dt : getTOCData().getProject().getDocTypes()) {
			addToChilds(dt);
		}
	}

	public TOCData getTOCData() {
		return (TOCData) getObject();
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.dgmodule.controller.browser.DGBrowserElement#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getProject()) {
			if (dataModification instanceof DocTypeAdded || dataModification instanceof DocTypeRemoved)
				if (_browser != null) {
					refreshWhenPossible();
				} else if (logger.isLoggable(Level.WARNING))
					logger.warning("Received notification on null browser");
			return;
		}
		super.update(observable, dataModification);
	}
}
