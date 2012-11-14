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
import org.openflexo.foundation.cg.templates.ApplicationDGTemplateRepository;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateRepository;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.templates.TargetSpecificCGTemplateSet;
import org.openflexo.localization.FlexoLocalization;

public class DGTemplateRepositoryElement extends BrowserElement {

	public DGTemplateRepositoryElement(CGTemplateRepository templateRepository, ProjectBrowser browser, BrowserElement parent) {
		super(templateRepository, BrowserElementType.TEMPLATE_REPOSITORY, browser, parent);
	}

	@Override
	public String getName() {
		if (getCGTemplateRepository() instanceof ApplicationDGTemplateRepository) {
			return FlexoLocalization.localizedForKey("dg_application_templates");
		} else if (getCGTemplateRepository() instanceof CustomCGTemplateRepository) {
			return ((CustomCGTemplateRepository) getCGTemplateRepository()).getName();
		}
		return "???";
	}

	@Override
	protected void buildChildrenVector() {
		for (Enumeration<TargetSpecificCGTemplateSet> e = getCGTemplateRepository().getTargetSpecificTemplates(); e.hasMoreElements();) {
			addToChilds(e.nextElement());
		}
		for (Enumeration<CGTemplate> e = getCGTemplateRepository().getCommonTemplates().getSortedTemplates(); e.hasMoreElements();) {
			addToChilds(e.nextElement());
		}
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
		((CustomCGTemplateRepository) getCGTemplateRepository()).setName(aName);
	}

}
