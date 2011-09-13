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
package org.openflexo.components.browser.ie;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IEBrowserWidget;


/**
 * @author gpolet Created on 13 sept. 2005
 */
public class IEBrowserWidgetElement extends IEElement
{

    /**
     * @param widget
     * @param browserElementType
     * @param browser
     */
    public IEBrowserWidgetElement(FlexoModelObject widget, BrowserElementType browserElementType, ProjectBrowser browser, BrowserElement parent)
    {
        super(widget, BrowserElementType.BROWSERWIDGET, browser,parent);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
	protected void buildChildrenVector()
    {
        // Empty block
    }

    /*
     * (non-Javadoc)
     *
     * @see org.openflexo.components.browser.BrowserElement#getName()
     */
    @Override
	public String getName()
    {
        if (getBrowser().getName() == null)
            return "Browser";
        else
            return getBrowser().getName();
    }

    public IEBrowserWidget getBrowser()
    {
        return (IEBrowserWidget) getObject();
    }

}
