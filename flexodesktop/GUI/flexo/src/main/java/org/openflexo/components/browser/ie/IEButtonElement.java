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
import org.openflexo.foundation.ie.widget.IEButtonWidget;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class IEButtonElement extends IEElement
{

    public IEButtonElement(IEButtonWidget button, ProjectBrowser browser, BrowserElement parent)
    {
        super(button, BrowserElementType.BUTTON, browser,parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        //no childs
    }

    @Override
	public String getName()
    {
        if (getButton().getImageName() == null) {
            return "Button";
        }
        return getButton().getImageName().substring(getButton().getImageName().lastIndexOf("_")+1,getButton().getImageName().length()-4);
    }

    protected IEButtonWidget getButton()
    {
        return (IEButtonWidget) getObject();
    }

}
