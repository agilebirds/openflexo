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
package org.openflexo.components.browser.ws;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.ws.WSPortType;


/**
 * NOT USED FOR THE MOMENT. A portType is directly represented as a ServiceInterface;
 * in a ServiceInterfaceElement.
 * @author dvanvyve
 *
 */
public class WSPortTypeElement extends BrowserElement
{

    /**
     * @param object
     * @param elementType
     * @param browser
     */
    public WSPortTypeElement(WSPortType object, ProjectBrowser browser, BrowserElement parent)
    {
        super(object, BrowserElementType.WS_PORTTYPE, browser, parent);
    }

    /**
     * Overrides buildChildrenVector
     *
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
	protected void buildChildrenVector()
    {

    }

    @Override
	public String getName() {
    	// TODO Auto-generated method stub
    	return ((WSPortType)getObject()).getName();
    }
}
