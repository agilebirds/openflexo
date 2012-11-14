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
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.sg.ApplicationSGTemplateRepository;
import org.openflexo.localization.FlexoLocalization;

public class CGTemplateRepositoryElement extends BrowserElement {

	public CGTemplateRepositoryElement(CGTemplateRepository templateRepository, ProjectBrowser browser, BrowserElement parent) {
		super(templateRepository, BrowserElementType.TEMPLATE_REPOSITORY, browser, parent);
	}

	@Override
	public String getName() {
		if (getCGTemplateRepository() instanceof ApplicationSGTemplateRepository) {
			return FlexoLocalization.localizedForKey("application_templates");
		} else if (getCGTemplateRepository() instanceof CustomCGTemplateRepository) {
			return ((CustomCGTemplateRepository) getCGTemplateRepository()).getName();
		}
		return "???";
	}

	@Override
	protected void buildChildrenVector() {
		/*for (Enumeration<TargetSpecificCGTemplateSet> e = getCGTemplateRepository().getTargetSpecificTemplates(); e.hasMoreElements();) {
			addToChilds(e.nextElement().getRootFolder());
		}*/
		/*for (Enumeration<CGTemplateFile> e = getCGTemplateRepository().getCommonTemplates().getSortedFiles(); e.hasMoreElements();) {
			addToChilds(e.nextElement());
		}*/
		addToChilds(getCGTemplateRepository().getCommonTemplates().getRootFolder());
	}

	protected CGTemplateRepository getCGTemplateRepository() {
		return (CGTemplateRepository) getObject();
	}

	@Override
	public boolean isNameEditable() {
		return getCGTemplateRepository() instanceof CustomCGTemplateRepository;
	}

	@Override
	public void setName(String aName) {
		_setName(aName);
	}

}
