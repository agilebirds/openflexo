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
package org.openflexo.components.browser;

import org.openflexo.components.browser.ProjectBrowser.DisableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.EnableExpandingSynchronizationEvent;
import org.openflexo.components.browser.ProjectBrowser.ExpansionNotificationEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectAddedToSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.ObjectRemovedFromSelectionEvent;
import org.openflexo.components.browser.ProjectBrowser.OptionalFilterAddedEvent;
import org.openflexo.components.browser.ProjectBrowser.SelectionClearedEvent;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public interface ProjectBrowserListener
{

    public void objectAddedToSelection(ObjectAddedToSelectionEvent event);

    public void objectRemovedFromSelection(ObjectRemovedFromSelectionEvent event);

    public void selectionCleared(SelectionClearedEvent event);

    /*
     * public void selectionSaved (SaveSelectionEvent event);
     * 
     * public void selectionRestored (RestoreSelectionEvent event);
     */

    public void optionalFilterAdded(OptionalFilterAddedEvent event);

    public void notifyExpansions(ExpansionNotificationEvent event);

    public void enableExpandingSynchronization(EnableExpandingSynchronizationEvent event);

    public void disableExpandingSynchronization(DisableExpandingSynchronizationEvent event);

}
