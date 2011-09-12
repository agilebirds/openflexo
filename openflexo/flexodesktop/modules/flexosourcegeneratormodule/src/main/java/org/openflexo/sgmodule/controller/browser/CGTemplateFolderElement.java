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
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFolder;


public class CGTemplateFolderElement extends BrowserElement
{

    public CGTemplateFolderElement(CGTemplateFolder templateFolder, ProjectBrowser browser, BrowserElement parent)
    {
        super(templateFolder, BrowserElementType.TEMPLATE_FOLDER, browser, parent);
    }

    @Override
	public String getName()
    {
    	return getCGTemplateFolder().getName();
    }

    @Override
	protected void buildChildrenVector()
    {
    	for (CGTemplateFolder f : getCGTemplateFolder().dirs) {
    		addToChilds(f);
    	}
		for (CGTemplate f : getCGTemplateFolder().templates) {
    		addToChilds(f);
    	}
      }

    protected CGTemplateFolder getCGTemplateFolder()
    {
        return (CGTemplateFolder) getObject();
    }

 }
