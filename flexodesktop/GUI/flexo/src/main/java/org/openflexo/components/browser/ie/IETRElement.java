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
import org.openflexo.foundation.ie.widget.IESpanTDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;


/**
 * @author bmangez <B>Class Description</B>
 */
public class IETRElement extends IEElement
{

    /**
     * @param widget
     * @param browser
     */
    public IETRElement(IETRWidget widget, ProjectBrowser browser, BrowserElement parent)
    {
        super(widget, BrowserElementType.TR, browser,parent);
        widget.getSequenceTD().addObserver(this);
    }
    /**
     * Overrides delete
     * @see org.openflexo.components.browser.BrowserElement#delete()
     */
    @Override
    public void delete()
    {
        getTR().getSequenceTD().deleteObserver(this);
        super.delete();
    }
    @Override
	protected void buildChildrenVector()
    {
        for (Enumeration e = getTR().colsEnumeration(); e.hasMoreElements();) {
            FlexoModelObject o = (FlexoModelObject) e.nextElement();
            if (o instanceof IESpanTDWidget) // Span TD's are virtual TD's
                continue;
            else
                addToChilds(o);
        }
    }

    @Override
	public String getName()
    {
        return "Row" +(getTR().getRowIndex()+1);
    }

    protected IETRWidget getTR()
    {
        return (IETRWidget) getObject();
    }

}
