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


import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.icon.GeneratorIconLibrary;


public class CGTemplateFileElement extends BrowserElement
{

	
	public CGTemplateFileElement(CGTemplate templateFile, ProjectBrowser browser, BrowserElement parent)
    {
        super(templateFile, BrowserElementType.TEMPLATE_FILE, browser, parent);
	}

    @Override
	public String getName()
    {
		return getCGTemplate().getTemplateName();
    }

    @Override
	protected void buildChildrenVector()
    {
    }

	protected CGTemplate getCGTemplate()
    {
		return (CGTemplate) getObject();
    }
    
    public ImageIcon getBaseIcon()
	{
		return getElementType().getIcon();
	}
	
	@Override
	public Icon getIcon()
	{
		return GeneratorIconLibrary.getIconForTemplate(getCGTemplate());
	}

	
 }
