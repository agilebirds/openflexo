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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public class IESequenceElement extends BrowserElement
{
    private static final Logger logger = FlexoLogger.getLogger(IESequenceElement.class.getPackage().getName());

    /**
     * @param object
     * @param elementType
     * @param browser
     */
    public IESequenceElement(IESequence object, BrowserElementType elementType, ProjectBrowser browser, BrowserElement parent)
    {
        super(object, elementType, browser, parent);
    }

    /**
     * Overrides getObject
     * @see org.openflexo.components.browser.BrowserElement#getObject()
     */
    @Override
    public IESequence<IWidget> getObject()
    {
        return (IESequence<IWidget>) super.getObject();
    }

    /**
     * Overrides buildChildrenVector
     * @see org.openflexo.components.browser.BrowserElement#buildChildrenVector()
     */
    @Override
    protected void buildChildrenVector()
    {
        Enumeration<IWidget> en = getObject().elements();
        while (en.hasMoreElements()) {
            addToChilds((FlexoModelObject) en.nextElement());
        }
    }

    /**
     * Overrides getName
     * @see org.openflexo.components.browser.BrowserElement#getName()
     */
    @Override
    public String getName()
    {
        if (getObject().getOperator()!=null)
            if (getObject().isConditional())
                return FlexoLocalization.localizedForKey("Conditional");
            else if (getObject().isRepetition())
                return FlexoLocalization.localizedForKey("Repetition");
            else
                return super.getName();

        else
            return super.getName();
    }

    /**
     * Overrides getIcon
     * @see org.openflexo.components.browser.BrowserElement#getIcon()
     */
    @Override
    public Icon getIcon()
    {
        if (getObject().getOperator()!=null) {
            if (getObject().isConditional())
                return BrowserElementType.CONDITIONAL.getIcon();
            else if (getObject().isRepetition())
                return BrowserElementType.REPETITION.getIcon();
            else {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Unknown operator");
                return super.getIcon();
            }
        } else
            return super.getIcon();
    }
}
