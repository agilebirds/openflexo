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
package org.openflexo.components.browser.wkf;

import java.util.Enumeration;

import javax.swing.Icon;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.ws.AbstractInPort;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.OutputPort;
import org.openflexo.icon.WKFIconLibrary;


/**
 * Browser element representing a FlexoPort
 *
 * @author sguerin
 *
 */
public class PortElement extends BrowserElement
{

    public PortElement(FlexoPort port, ProjectBrowser browser, BrowserElement parent)
    {
        super(port, BrowserElementType.PORT, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    		if(getPort().isInPort()){
    			addToChilds(((AbstractInPort)getPort()).getInputMessageDefinition());
    		}
    		if(getPort().isOutPort()){
    			addToChilds(((OutputPort)getPort()).getOutputMessageDefinition());
    		}
    		//TODO: Add Fault Message Definition


        // We add post conditions
        if (getPort() instanceof AbstractInPort) {
            for (Enumeration e = ((AbstractInPort) getPort()).getOutgoingPostConditions().elements(); e.hasMoreElements();) {
                addToChilds((FlexoPostCondition) e.nextElement());
            }
        }
    }

    @Override
	public String getName()
    {
        return getPort().getName();
    }

    protected FlexoPort getPort()
    {
        return (FlexoPort) getObject();
    }

    @Override
	protected BrowserElementType getFilteredElementType()
    {
        // filtered element type should be PORT and not PORT_REGISTERY
    		return BrowserElementType.PORT;
    		//return BrowserElementType.PORT_REGISTERY;
    }

    @Override
	public Icon getIcon()
    {
		return WKFIconLibrary.getSmallImageIconForFlexoPort(getPort());
     }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName)
    {
        getPort().setName(aName);
    }

}
