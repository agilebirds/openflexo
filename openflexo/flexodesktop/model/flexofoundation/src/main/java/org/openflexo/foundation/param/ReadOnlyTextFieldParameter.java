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

import org.openflexo.inspector.widget.DenaliWidget;
import org.openflexo.inspector.widget.ReadOnlyWidget;

public class ReadOnlyTextFieldParameter extends ParameterDefinition<String> {

    public ReadOnlyTextFieldParameter(String name, String label, String defaultValue)
    {
        super(name,label,defaultValue);
    }
    
    public ReadOnlyTextFieldParameter(String name, String label, String defaultValue, int cols)
    {
        this(name,label,defaultValue);
        setColumns(cols);
   }
    
    public void setColumns(int cols)
    {
    	addParameter(ReadOnlyWidget.COLUMNS_PARAM,""+cols);
    }

  @Override
public String getWidgetName() 
   {
        return DenaliWidget.READ_ONLY_TEXT_FIELD;
    }

}
