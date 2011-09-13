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
package org.openflexo.components.browser.view;

import java.awt.AWTEvent;

import javax.swing.JFrame;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.foundation.FlexoObservable;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class BrowserActionEvent extends AWTEvent
{

    private static int id = AWTEvent.RESERVED_ID_MAX + 1;

    private BrowserElement _element;

    private JFrame _frame;

    public BrowserActionEvent(BrowserElement element, JFrame frame)
    {
        super(element, id++);
        _element = element;
        _frame = frame;
    }

    public BrowserElement getBrowserElement()
    {
        return _element;
    }

    public FlexoObservable getObject()
    {
        return _element.getObject();
    }

    public JFrame getFrame()
    {
        return _frame;
    }

}
