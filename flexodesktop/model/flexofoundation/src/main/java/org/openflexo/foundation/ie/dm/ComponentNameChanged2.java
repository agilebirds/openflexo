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

import org.openflexo.foundation.ie.cl.ComponentDefinition;

/**
 * Notify that a ComponentDefinition has been renamed
 * This notification is for CG resources (which are to be notified later)
 * 
 * @author sguerin
 * 
 */
public class ComponentNameChanged2 extends IEDataModification
{

    public ComponentDefinition component;

    public ComponentNameChanged2(ComponentDefinition component, String oldName, String newName)
    {
        super(oldName, newName);
        this.component = component;
    }

    public ComponentNameChanged2(String propertyName, ComponentDefinition component, String oldName, String newName)
    {
        super(propertyName, oldName, newName);
        this.component = component;
    }

     @Override
	public String toString()
    {
        return "ComponentNameChanged2" + oldValue() + "/" + newValue();
    }
}
