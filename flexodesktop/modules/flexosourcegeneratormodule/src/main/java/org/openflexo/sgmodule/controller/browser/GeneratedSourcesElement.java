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
package org.openflexo.sgmodule.controller.browser;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.sg.GeneratedSources;
import org.openflexo.foundation.sg.implmodel.ImplementationModelDefinition;
import org.openflexo.localization.FlexoLocalization;

public class GeneratedSourcesElement extends SGBrowserElement {
	public GeneratedSourcesElement(GeneratedSources generatedSources, ProjectBrowser browser, BrowserElement parent) {
		super(generatedSources, BrowserElementType.GENERATED_SOURCES, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (ImplementationModelDefinition implementationModel : getGeneratedSources().getImplementationModels()) {
			addToChilds(implementationModel.getImplementationModel());
		}
		for (GenerationRepository repository : getGeneratedSources().getGeneratedRepositories()) {
			addToChilds(repository);
		}
		addToChilds(getGeneratedSources().getTemplates());
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO Auto-generated method stub
		super.update(observable, dataModification);
	}

	public GeneratedSources getGeneratedSources() {
		return (GeneratedSources) getObject();
	}

	@Override
	public String getName() {
		return FlexoLocalization.localizedForKey("generated_sources");
	}
}
