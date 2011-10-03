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
package org.openflexo.foundation.ontology.dm;

import org.openflexo.foundation.rm.FlexoOEShemaResource;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.RMNotification;
import org.openflexo.foundation.view.OEShemaDefinition;

/**
 * Notify that a Shema has been renamed
 * 
 * @author sguerin
 * 
 */
public class ShemaNameChanged extends OEDataModification implements RMNotification
{

    public OEShemaDefinition shema;

    public ShemaNameChanged(OEShemaDefinition shema, String oldName, String newName)
    {
        super(oldName, newName);
        this.shema = shema;
    }

    public ShemaNameChanged(String propertyName, OEShemaDefinition component, String oldName, String newName)
    {
        super(propertyName, oldName, newName);
        this.shema = component;
    }

    @Override
	public boolean forceUpdateWhenUnload()
    {
        return true;
    }

    @Override
	public boolean isDeepNotification()
    {
        return true;
    }

    @Override
	public boolean propagateToSynchronizedResource(FlexoResource originResource, FlexoResource targetResource)
    {
//    		return true;
        if (originResource == shema.getShemaLibrary().getFlexoResource()
        			&& (targetResource instanceof FlexoOEShemaResource)) {
          return true;
        } else {
           return false;
        }
    }

    @Override
	public boolean propagateToAlteredResource(FlexoResource originResource, FlexoResource targetResource)
    {
        if (originResource == shema.getShemaResource()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
	public String toString()
    {
        return "ShemaNameChanged " + oldValue() + "/" + newValue();
    }
}
