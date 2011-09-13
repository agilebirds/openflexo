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
package org.openflexo.ie.view.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Focus listener for IE Module
 * 
 * @author sguerin
 */
public class IEFocusListener implements FocusListener
{

    private static final Logger logger = Logger.getLogger(IEFocusListener.class.getPackage().getName());

    private static IEFocusListener _instance;

    public static IEFocusListener instance()
    {
        if (_instance == null) {
            _instance = new IEFocusListener();
        }
        return _instance;
    }

    @Override
	public void focusGained(FocusEvent event)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("focusGained for component" + event.getComponent());
    }

    @Override
	public void focusLost(FocusEvent event)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("focusLost for component" + event.getComponent());
    }

}
