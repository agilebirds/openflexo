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

import java.util.logging.Level;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.dm.ComponentDMEntity;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicatePropertyNameException;
import org.openflexo.toolbox.ReservedKeyword;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class ComponentBindingDefinition extends BindingDefinition
{

    private ComponentDMEntity _componentDMEntity;

    private DMProperty _property;

    public ComponentBindingDefinition(ComponentDMEntity componentDMEntity, DMProperty property)
    {
        super(componentDMEntity);
        _componentDMEntity = componentDMEntity;
        _property = property;
    }

    /**
     * Overrides equals
     * @see org.openflexo.foundation.bindings.BindingDefinition#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if (object instanceof ComponentBindingDefinition) {
            ComponentBindingDefinition bd = (ComponentBindingDefinition)object;
            if (_property == null) {
                if (bd._property != null) return false;
             }
            else {
                if (!_property.equals(bd._property)) return false;
            }
            if (_componentDMEntity == null) {
                if (bd._componentDMEntity != null) return false;
             }
            else {
                if (!_componentDMEntity.equals(bd._componentDMEntity)) return false;
            }
            return ((getOwner() == bd.getOwner())
                    && (getType() == bd.getType())
                    && (getIsMandatory() == bd.getIsMandatory()));
       }
        else {
            return super.equals(object);
        }
    }

    public ComponentDMEntity getComponentDMEntity()
    {
        return _componentDMEntity;
    }

    public boolean getIsBindable()
    {
        if (_property != null) {
            return _componentDMEntity.isBindable(_property);
        }
        return false;
    }

    public void setIsBindable(boolean bindable)
    {
        if (_property != null) {
            _componentDMEntity.setBindable(_property, bindable);
        }
    }

    @Override
	public boolean getIsMandatory()
    {
        if (_property != null) {
            return _componentDMEntity.isMandatory(_property);
        }
        return false;
    }

    @Override
	public void setIsMandatory(boolean mandatory)
    {
        if (_property != null) {
            _componentDMEntity.setMandatory(_property, mandatory);
        }
    }

    public boolean isSettable()
    {
    	return getIsSettable();
    }

    @Override
	public boolean getIsSettable()
    {
        if (_property != null) {
            return _componentDMEntity.isSettable(_property);
        }
        return false;
    }

    @Override
	public void setIsSettable(boolean settable)
    {
        if (_property != null) {
            _componentDMEntity.setSettable(_property, settable);
        }
    }

    @Override
	public DMType getType()
    {
        if (_property != null) {
            return _property.getType();
        } else {
            return super.getType();
        }
    }

    @Override
	public void setType(DMType type)
    {
        if (_property != null) {
            _property.setType(type);
        } else {
            super.setType(type);
        }
    }

    @Override
	public String getVariableName()
    {
        if (_property != null) {
            return _property.getName();
        } else {
            return super.getVariableName();
        }
    }

    @Override
	public void setVariableName(String variableName)
    {
        if (_property != null) {
            try {
            	if(ReservedKeyword.contains(variableName))throw new InvalidNameException(variableName+" is a reserved keyword.");
				_property.setName(variableName);
			} catch (InvalidNameException e) {
				setChanged();
				notifyObserversAsReentrantModification(new DataModification(-1, "variableName", null, _property.getName()));
				new FlexoException(e.getMessage(),e);
			} catch (DuplicatePropertyNameException e) {
				if(logger.isLoggable(Level.WARNING))
					logger.warning(e.getMessage());
				setChanged();
				notifyObserversAsReentrantModification(new DataModification(-1, "variableName", null, _property.getName()));
			}
        } else {
            super.setVariableName(variableName);
        }
    }

    public DMProperty getProperty()
    {
        return _property;
    }

    public void setProperty(DMProperty property)
    {
        _property = property;
    }

    @Override
	public String toString()
    {
        return "ComponentBindingDefinition:"+getVariableName()+":"+getType()+"/"+hashCode();
    }
}
