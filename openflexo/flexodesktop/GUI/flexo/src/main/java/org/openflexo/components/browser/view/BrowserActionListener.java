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

import java.util.EventListener;

/**
 * The listener interface for receiving browser action events. The class that is
 * interested in processing an action event implements this interface, and the
 * object created with that class is registered with a component, using the
 * ProjectBrowser's addBrowserActionListener method. When the borwser action
 * event occurs, that object's browserActionPerformed method is invoked.
 * 
 * @author sguerin
 * @deprecated
 * 
 */
@Deprecated
public interface BrowserActionListener extends EventListener
{
    public void browserActionPerformed(BrowserActionEvent event);

}
