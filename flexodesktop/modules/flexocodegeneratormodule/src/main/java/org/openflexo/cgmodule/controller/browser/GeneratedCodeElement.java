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
package org.openflexo.cgmodule.controller.browser;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;

public class GeneratedCodeElement extends GCBrowserElement {
	public GeneratedCodeElement(GeneratedOutput generatedCode, ProjectBrowser browser, BrowserElement parent) {
		super(generatedCode, BrowserElementType.GENERATED_CODE, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		for (GenerationRepository repository : getGeneratedCode().getGeneratedRepositories()) {
			addToChilds(repository);
		}
		addToChilds(getGeneratedCode().getTemplates());
	}

	public GeneratedOutput getGeneratedCode() {
		return (GeneratedOutput) getObject();
	}
}
