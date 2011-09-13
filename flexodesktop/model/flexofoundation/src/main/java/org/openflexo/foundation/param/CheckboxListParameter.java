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


public class CheckboxListParameter<T> extends ParameterDefinition<Vector<T>> {

    private Vector<T> _availableValues;
    
    public CheckboxListParameter(String name,String label,Vector<T> availableValues,Vector<T> defaultSelectedValues)
    {
        super(name,label,defaultSelectedValues);
        addParameter("dynamiclist","params."+name+".availableValues");
        _availableValues = availableValues;
    }
    
    public CheckboxListParameter(String name, String label,Vector<T> defaultSelectedValues)
    {
        this(name,label,null,defaultSelectedValues);
    }
    
   @Override
public String getWidgetName() 
   {
        return DenaliWidget.CHECKBOX_LIST;
    }

   // Override this if list not defined in constructor
   public Vector<T> getAvailableValues() 
   {
       return _availableValues;
   }
   
   /*public Vector getSelectedValues()
   {
       if (getValue() instanceof Vector) {
           return (Vector)getValue();
       }
       else return null;
   }*/
   
   /*public void setSelectedValues(Vector selectedValues)
   {
       setValue(selectedValues);
   }*/

}
