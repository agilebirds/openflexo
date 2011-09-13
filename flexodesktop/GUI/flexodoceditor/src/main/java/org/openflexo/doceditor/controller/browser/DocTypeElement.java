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
package org.openflexo.doceditor.controller.browser;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoModelObject;


/**
 * @author gpolet
 *
 */
public class DocTypeElement extends BrowserElement
{

    /**
     * @param object
     * @param elementType
     * @param browser
     */
    public DocTypeElement(FlexoModelObject object, ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.DOC_TYPE, browser, parent);
    }

    /**
     * Overrides buildChildrenVector
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
    protected void buildChildrenVector()
    {
    }

    /**
     * Overrides getObject
     * @see org.openflexo.components.browser.BrowserElement#getObject()
     */
    @Override
    public DocType getObject()
    {
        return (DocType) super.getObject();
    }
    
    /**
     * Overrides getName
     * @see org.openflexo.components.browser.BrowserElement#getName()
     */
    @Override
    public String getName()
    {
        return getObject().getName();
    }
}
