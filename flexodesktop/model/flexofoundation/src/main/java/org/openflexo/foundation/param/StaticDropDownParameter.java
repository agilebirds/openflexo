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
package org.openflexo.foundation.param;

import java.util.Vector;

import org.openflexo.inspector.widget.DenaliWidget;


public class StaticDropDownParameter<T> extends ParameterDefinition<T> {

    public StaticDropDownParameter(String name, String label, Vector<T> allStaticValues, T defaultValue, boolean showReset)
    {
    	this(name, label, allStaticValues, defaultValue);
    	setShowReset(showReset);
    }
    
    public StaticDropDownParameter(String name, String label, Vector<T> allStaticValues, T defaultValue)
    {
        super(name,label,defaultValue);
        String allChoices = "";
        boolean isFirst = true;
        for (T next : allStaticValues) {
            allChoices += (!isFirst?",":"")+next;
            isFirst=false;
        }
        addParameter("staticlist",allChoices);
    }
    
   @Override
public String getWidgetName() 
   {
        return DenaliWidget.DROPDOWN;
    }

   public void setShowReset(boolean showReset)
   {
   	addParameter("showReset",""+showReset);
   }
   

}
