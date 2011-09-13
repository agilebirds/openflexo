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
import org.openflexo.foundation.ie.cl.ComponentDefinition;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class IETabComponentElement extends ComponentElement
{

    public IETabComponentElement(ComponentDefinition component, ProjectBrowser browser, BrowserElement parent)
    {
        super(component, BrowserElementType.TAB_COMPONENT, browser, parent);
    }

    /**
     * Overrides getFilteredElementType
     * @see org.openflexo.components.browser.ie.ComponentElement#getFilteredElementType()
     */
    @Override
	protected BrowserElementType getFilteredElementType()
    {
        return BrowserElementType.TAB_COMPONENT;
    }

}
