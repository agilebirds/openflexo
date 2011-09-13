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
 * Implemented by a view representing an object able to be selected
 * 
 * @author bmangez, sguerin
 */
public interface SelectableView
{

    /**
     * Return boolean indicating if related object is selected
     * 
     * @return boolean
     */
    public boolean isSelected();

    /**
     * Sets related object to be selected or not
     * 
     * @param b
     */
    public void setIsSelected(boolean b);

    /**
     * Return represented object, instance of
     * {@link org.openflexo.foundation.FlexoModelObject}
     * 
     * @return
     */
    public FlexoModelObject getObject();

}
