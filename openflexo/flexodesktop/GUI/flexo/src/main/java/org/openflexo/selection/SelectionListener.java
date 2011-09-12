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
package org.openflexo.selection;

import org.openflexo.foundation.FlexoModelObject;

/**
 * Implemented by components back-synchronized with a SelectionManager
 * This means that this component, once registered in a SelectionManager,
 * reveive synchronization request, but is not supposed to send some.
 * 
 * @author sguerin
 */
public interface SelectionListener
{

    /**
     * Notified that supplied object has been added to selection
     * 
     * @param object: the object that has been added to selection
     */
    public void fireObjectSelected(FlexoModelObject object);

    /**
      * Notified that supplied object has been removed from selection
     * 
     * @param object: the object that has been removed from selection
     */
    public void fireObjectDeselected(FlexoModelObject object);

    /**
     * Notified selection has been resetted
     */
    public void fireResetSelection();

    /**
     * Notified that the selection manager is performing a multiple selection
     */
    public void fireBeginMultipleSelection();

    /**
     * Notified that the selection manager has finished to perform a multiple
     * selection
     */
    public void fireEndMultipleSelection();

}
