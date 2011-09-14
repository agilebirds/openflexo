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
import org.openflexo.inspector.widget.TextFieldAndLabelWidget;

public abstract class TextFieldAndLabelParameter extends ParameterDefinition<String> {

    public TextFieldAndLabelParameter(String name, String label, String defaultValue)
    {
        super(name,label,defaultValue);
    }
    
    public TextFieldAndLabelParameter(String name, String label, String defaultValue, int cols)
    {
        this(name,label,defaultValue);
        setColumns(cols);
        addParameter(TextFieldAndLabelWidget.ADDITIONAL_LABEL,"params."+name+".additionalLabel");
    }
    
    public void setValidateOnReturn(boolean validateOnReturn)
    {
    	addParameter(TextFieldAndLabelWidget.VALIDATE_ON_RETURN,""+validateOnReturn);
    }
    
    public void setIsPassword(boolean isPassword)
    {
    	addParameter(TextFieldAndLabelWidget.PASSWORD_PARAM,""+isPassword);
    }
    
    public void setColumns(int cols)
    {
    	addParameter(TextFieldAndLabelWidget.COLUMNS_PARAM,""+cols);
    }

    public abstract String getAdditionalLabel();

    @Override
	public String getWidgetName() 
    {
    	return DenaliWidget.TEXT_FIELD_AND_LABEL;
    }

}
