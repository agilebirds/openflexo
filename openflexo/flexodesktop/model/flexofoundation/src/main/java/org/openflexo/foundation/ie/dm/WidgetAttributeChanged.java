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
package org.openflexo.foundation.ie.dm;

public class WidgetAttributeChanged extends IEDataModification
{

    //public static final String 
    
    public static final String ACTION_TYPE="actionType";
    
    public static final String IS_MANDATORY_FLEXO_ACTION="IS_MANDATORY_FLEXO_ACTION";
    
    public WidgetAttributeChanged(String propertyName, Object oldValue, Object newValue)
    {
        super(propertyName, oldValue, newValue);
    }

}
