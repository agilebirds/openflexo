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
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IESequence;


public class IERepetitionElement extends IEElement
{

    /**
     * @param widget
     * @param browser
     */
    public IERepetitionElement(RepetitionOperator op, ProjectBrowser browser, BrowserElement parent)
    {
        super(op, BrowserElementType.REPETITION, browser,parent);
    }

    @Override
	protected void buildChildrenVector()
    {
        for (Enumeration e = getRepetition().getOperatedSequence().elements(); e.hasMoreElements();) {
            FlexoModelObject child = (FlexoModelObject) e.nextElement();
            if (child instanceof IESequence) {
                addToChilds(((IESequence) child).getOperator());
            } else
                addToChilds(child);
        }
    }

    @Override
	public String getName()
    {
        return "Repetition";
    }

    protected RepetitionOperator getRepetition()
    {
        return (RepetitionOperator) getObject();
    }

}
