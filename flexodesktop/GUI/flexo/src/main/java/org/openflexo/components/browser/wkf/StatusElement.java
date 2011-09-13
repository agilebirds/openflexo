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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.DuplicateStatusException;
import org.openflexo.foundation.wkf.Status;


/**
 * Browser element representing a Status
 *
 * @author sguerin
 *
 */
public class StatusElement extends BrowserElement
{

    public StatusElement(Status status, ProjectBrowser browser, BrowserElement parent)
    {
        super(status, BrowserElementType.STATUS, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        // No children
    }

    @Override
	public String getName()
    {
    		if(getStatus()!=null&&getParent()!=null&&((StatusListElement)getParent()).getStatusList()!=null){
    			if (getStatus().getProcess() != ((StatusListElement) getParent()).getStatusList().getProcess()) {
    				return getStatus().getName() + " [" + getStatus().getProcess().getName() + "]";
    			}
    			return getStatus().getName();
    		}
    		return super.getName();
    }

    protected Status getStatus()
    {
        return (Status) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName) throws DuplicateStatusException
    {
        getStatus().setName(aName);
    }

}
