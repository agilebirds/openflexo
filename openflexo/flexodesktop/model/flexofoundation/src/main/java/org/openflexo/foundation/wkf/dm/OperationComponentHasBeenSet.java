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
package org.openflexo.foundation.wkf.dm;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.wkf.node.OperationNode;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class OperationComponentHasBeenSet extends WKFDataModification
{

    public OperationNode node;

    public OperationComponentHasBeenSet(ComponentDefinition component, OperationNode node)
    {
        super(null, component);
        this.node = node;
    }

    public OperationComponentHasBeenSet(String propertyName, ComponentDefinition component, OperationNode node)
    {
        super(propertyName, component, null);
        this.node = node;
    }
    
    /**
     * Overrides propertyName
     * @see org.openflexo.foundation.DataModification#propertyName()
     */
    @Override
	public String propertyName()
    {
        return super.propertyName();
    }

}
