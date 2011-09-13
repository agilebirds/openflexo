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

import java.util.Enumeration;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IETDWidget;


/**
 * @author bmangez <B>Class Description</B>
 */
public class IETDElement extends IEElement
{

    /**
     * @param widget
     * @param browser
     */
    public IETDElement(IETDWidget widget, ProjectBrowser browser, BrowserElement parent)
    {
        super(widget, BrowserElementType.TD, browser,parent);
        widget.getSequenceWidget().addObserver(this);
    }

    /**
     * Overrides delete
     * @see org.openflexo.components.browser.BrowserElement#delete()
     */
    @Override
    public void delete()
    {
        ((IETDWidget)getObject()).getSequenceWidget().deleteObserver(this);
        super.delete();
    }

    @Override
	protected void buildChildrenVector()
    {
        FlexoModelObject child = null;
        for (Enumeration e = getTD().getSequenceWidget().elements(); e.hasMoreElements();) {
            child = (FlexoModelObject) e.nextElement();
            addToChilds(child);
        }
    }

    @Override
	public String getName()
    {
        return "Cell(" + (getTD().getYLocation()+1) + "," + (getTD().getXLocation()+1) + ")";
    }

    protected IETDWidget getTD()
    {
        return (IETDWidget) getObject();
    }

}
