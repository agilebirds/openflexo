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
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.foundation.dkv.Language;


/**
 * @author gpolet
 *
 */
public class DKVLanguageElement extends BrowserElement
{

    /**
     * @param object
     * @param elementType
     * @param browser
     */
    public DKVLanguageElement(Language object,
            ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.DKV_LANGUAGE, browser, parent);
    }

    /**
     * Overrides buildChildrenVector
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
	protected void buildChildrenVector()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Overrides getName
     * @see org.openflexo.components.browser.BrowserElement#getName()
     */
    @Override
	public String getName()
    {
        return ((DKVObject)getObject()).getName();
    }
}
