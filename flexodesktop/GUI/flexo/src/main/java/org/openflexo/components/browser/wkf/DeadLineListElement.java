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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.DeadLine;
import org.openflexo.foundation.wkf.DeadLineList;


/**
 * Browser element representing DeadLines of a process
 *
 * @author sguerin
 * @deprecated since version 1.2
 *
 */
@Deprecated
public class DeadLineListElement extends BrowserElement
{

    public DeadLineListElement(DeadLineList deadLineList, ProjectBrowser browser, BrowserElement parent)
    {
        super(deadLineList, BrowserElementType.DEADLINE_LIST, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        // We add the deadlines
        DeadLineList deadLineList = getDeadLineList();
        for (Enumeration e = deadLineList.getDeadLines().elements(); e.hasMoreElements();) {
            addToChilds((DeadLine) e.nextElement());
        }
    }

    protected DeadLineList getDeadLineList()
    {
        return (DeadLineList) getObject();
    }

    @Override
	protected BrowserElementType getFilteredElementType()
    {
        return BrowserElementType.DEADLINE;
    }
}
