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
import org.openflexo.foundation.wkf.DeadLine;


/**
 * Browser element representing a DeadLine
 *
 * @author sguerin
 * @deprecated since version 1.2
 *
 */
@Deprecated
public class DeadLineElement extends BrowserElement
{

    public DeadLineElement(DeadLine deadLine, ProjectBrowser browser, BrowserElement parent)
    {
        super(deadLine, BrowserElementType.DEADLINE, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        // No children
    }

    @Override
	public String getName()
    {
        return getDeadLine().getName();
    }

    protected DeadLine getDeadLine()
    {
        return (DeadLine) getObject();
    }

    @Override
	public boolean isNameEditable()
    {
        return true;
    }

    @Override
	public void setName(String aName)
    {
        getDeadLine().setName(aName);
    }

}
