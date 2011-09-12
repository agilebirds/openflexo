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
package org.openflexo.ie.view;

import org.openflexo.foundation.DeletableObject;
import org.openflexo.selection.FocusableView;
import org.openflexo.selection.SelectableView;


/**
 * This interface defines behaviour of views representing Flexo objects able to
 * be selected, responding to focus, and able to be selected through a
 * "rectangle selection". Those objects are implementing
 * {@link org.openflexo.foundation.ie.widget.IEWidget} and could be retrieved
 * through {@link #getSelectedObject()}
 * 
 * Note that this interface inherits from
 * {@link org.openflexo.selection.SelectableView}
 * 
 * @author sguerin
 */
public interface IESelectable extends SelectableView, FocusableView
{

    /**
     * Return boolean indicating if related object is selected
     * 
     * @return boolean
     */
    @Override
	public boolean isSelected();

    /**
     * Sets related object to be selected or not
     */
    @Override
	public void setIsSelected(boolean b);

    /**
     * Sets related object to be focused or not
     */
    @Override
	public void setIsFocused(boolean b);

    /**
     * Return boolean indicating if related object is focused
     * 
     * @return boolean
     */
    @Override
	public boolean isFocused();

    /**
     * Return selected object, instance of
     * {@link org.openflexo.foundation.ie.widget.IEWidget}
     * 
     * @return
     */
    // public IEWidget getSelectedObject();
    /**
     * Return selected object, instance of
     * {@link org.openflexo.foundation.DeletableObject}
     * 
     * @return
     */
    public DeletableObject getDeletableObject();
}
