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

import java.util.Enumeration;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.templates.CustomCGTemplateRepository;
import org.openflexo.foundation.cg.utils.TemplateRepositoryType;
import org.openflexo.foundation.sg.SGTemplates;


public class SGTemplatesElement extends BrowserElement
{

    public SGTemplatesElement(SGTemplates templates, ProjectBrowser browser, BrowserElement parent)
    {
        super(templates, BrowserElementType.TEMPLATES, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    	addToChilds(getSGTemplates().getApplicationRepository().getCommonTemplates().getRootFolder());
        for (Enumeration<CustomCGTemplateRepository> e = getSGTemplates().getCustomRepositories(); e.hasMoreElements();) {
        	 CustomCGTemplateRepository rep = e.nextElement();
        	 if (rep.getRepositoryType()==TemplateRepositoryType.Code)
        		 addToChilds(rep.getCommonTemplates().getRootFolder());
        }
    }

    protected SGTemplates getSGTemplates()
    {
        return (SGTemplates) getObject();
    }

}
