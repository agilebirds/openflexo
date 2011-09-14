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
package org.openflexo.foundation.ie;

import java.io.Serializable;

import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.widget.IEReusableWidget;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;
import org.openflexo.foundation.xml.FlexoProcessBuilder;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class PartialComponentInstance extends ComponentInstance implements Serializable
{
	
	private IEReusableWidget reusableWidget;
	
    /**
     * @param component
     */
    public PartialComponentInstance(ComponentDefinition componentDef, XMLStorageResourceData container)
    {
        super(componentDef, container);
    }

    public PartialComponentInstance(FlexoComponentBuilder builder)
    {
        super(builder);
    }

    public PartialComponentInstance(FlexoProcessBuilder builder) {
    	super(builder);
    }

    /**
     * 
     * @param builder
     * @deprecated - {@link FlexoItemMenu} should not reference partial components
     */
	@Deprecated
	public PartialComponentInstance(FlexoNavigationMenuBuilder builder) {
		super(builder);
	}

	public IEReusableWidget getReusableWidget()
    {
        return reusableWidget;
    }

    public void setReusableWidget(IEReusableWidget reusableWidget) {
    	this.reusableWidget = reusableWidget;
    	setOwner(reusableWidget);
    }
    
	@Override
	public String getClassNameKey() {
		return "reusable_component_instance";
	}
    
}
