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
package org.openflexo.foundation.dm.eo.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 *
 */
public abstract class EOProperty extends EOObject
{
    private static final Logger logger = FlexoLogger.getLogger(EOProperty.class.getPackage().getName());
    
    private EOEntity entity;

    public EOEntity getEntity()
    {
        return entity;
    }

    public void setEntity(EOEntity entity)
    {
        if (this.entity!=null && this.entity!=entity) {
            this.entity.removeProperty(this);
        }
        this.entity = entity;
    }

    /**
     * @return
     */
    public boolean getIsClassProperty()
    {
        if (getEntity() != null) {
            return getEntity().getClassProperties().contains(this);
        } else if (logger.isLoggable(Level.WARNING))
            logger.warning("Impossible to determine if property is class property because it has no entity");
        return false;
    }
    
    /**
     * @param isClassProperty
     */
    public void setIsClassProperty(boolean isClassProperty)
    {
        if (getEntity()==null)
            throw new IllegalStateException("Trying to set a property as a class property while the property has no entity");
        if (getIsClassProperty()!=isClassProperty) {
            if (isClassProperty)
                getEntity().getClassProperties().add(this);
            else
                getEntity().getClassProperties().remove(this);
        }
    }
    /**
     * Overrides setName
     * @see org.openflexo.foundation.dm.eo.model.EOObject#setName(java.lang.String)
     */
    @Override
    public void setName(String name)
    {
        if (getEntity()!=null) {
            if (getEntity().propertyNamed(name)!=null)
                throw new IllegalArgumentException("Another property is already named "+name+" in entity "+getEntity().getName());
        }
        if (name==null)
            throw new NullPointerException();
        super.setName(name);
    }
}
