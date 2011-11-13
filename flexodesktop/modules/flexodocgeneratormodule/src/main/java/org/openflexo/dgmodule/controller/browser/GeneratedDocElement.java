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
package org.openflexo.dgmodule.controller.browser;

import java.util.logging.Level;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.DocTypeAdded;
import org.openflexo.foundation.cg.DocTypeRemoved;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.toc.TOCRepository;

public class GeneratedDocElement extends DGBrowserElement {
	public GeneratedDocElement(GeneratedDoc generatedCode, ProjectBrowser browser, BrowserElement parent) {
		super(generatedCode, BrowserElementType.GENERATED_DOC, browser, parent);
		generatedCode.getProject().addObserver(this);
		generatedCode.getProject().getTOCData().addObserver(this);
	}

	@Override
	public void delete() {
		getGeneratedCode().getProject().deleteObserver(this);
		getGeneratedCode().getProject().getTOCData().deleteObserver(this);
		super.delete();
	}

	@Override
	protected void buildChildrenVector() {
		for (GenerationRepository repository : getGeneratedCode().getGeneratedRepositories()) {
			addToChilds(repository);
		}
		addToChilds(getGeneratedCode().getTemplates());
		for (TOCRepository rep : getProject().getTOCData().getRepositories()) {
			addToChilds(rep);
		}
		for (DocType dt : getGeneratedCode().getProject().getDocTypes()) {
			addToChilds(dt);
		}
	}

	public GeneratedDoc getGeneratedCode() {
		return (GeneratedDoc) getObject();
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
