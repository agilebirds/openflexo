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

import java.util.Enumeration;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;

public class DGTemplatesElement extends BrowserElement {

	public DGTemplatesElement(CGTemplates templates, ProjectBrowser browser, BrowserElement parent) {
		super(templates, BrowserElementType.TEMPLATES, browser, parent);
	}

	@Override
	protected void buildChildrenVector() {
		addToChilds(getCGTemplates().getApplicationRepository());
		for (Enumeration<CustomCGTemplateRepository> e = getCGTemplates().getCustomRepositories(); e.hasMoreElements();) {
			CustomCGTemplateRepository rep = e.nextElement();
			if (rep.getRepositoryType() == TemplateRepositoryType.Documentation) {
				addToChilds(rep);
			}
		}
	}

	protected CGTemplates getCGTemplates() {
		return (CGTemplates) getObject();
	}

}
