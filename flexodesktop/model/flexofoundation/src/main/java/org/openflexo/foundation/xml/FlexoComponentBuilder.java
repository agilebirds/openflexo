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
package org.openflexo.foundation.xml;

import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoComponentResource;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class FlexoComponentBuilder extends FlexoBuilder<FlexoComponentResource>
{

    public IEWOComponent woComponent = null;
    
    public ComponentDefinition componentDefinition;
    
    /**
     * Use this constructor to build an Operation Component
     * 
     * @param componentDefinition
     */
    public FlexoComponentBuilder(ComponentDefinition def, FlexoComponentResource resource)
    {
        super(resource);
        this.componentDefinition = def;
        if (componentDefinition.isLoaded())
        	woComponent = componentDefinition.getWOComponent();
    }

}
