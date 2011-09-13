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
package org.openflexo.foundation.bindings;

import org.openflexo.foundation.dm.DMModel;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.localization.FlexoLocalization;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class ComponentBindingVariable extends BindingVariable
{

    private BindingDefinition _bindingDefinition;

    public ComponentBindingVariable(Bindable container, DMModel dataModel, BindingDefinition bindingDefinition)
    {
        super(container, dataModel,FlexoLocalization.localizedForKey("access_to_the_current_component"));
        _bindingDefinition = bindingDefinition;
    }

    @Override
	public DMType getType()
    {
        if (_bindingDefinition == null)
            return null;
        return _bindingDefinition.getType();
    }

    @Override
	public void setType(DMType type)
    {
        if (_bindingDefinition == null)
            return;
        _bindingDefinition.setType(type);
        setChanged();
    }

    @Override
	public String getVariableName()
    {
        if (_bindingDefinition == null)
            return null;
        return _bindingDefinition.getVariableName();
    }

    @Override
	public void setVariableName(String variableName)
    {
        if (_bindingDefinition == null)
            return;
        _bindingDefinition.setVariableName(variableName);
        setChanged();
    }

}
