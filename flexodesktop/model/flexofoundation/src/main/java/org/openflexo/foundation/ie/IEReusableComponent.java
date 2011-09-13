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

import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class IEReusableComponent extends IEPartialComponent
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEReusableComponent.class.getPackage().getName());


    /**
     * Constructor invoked during deserialization for IEPopupComponent
     * 
     * @param componentDefinition
     */
    public IEReusableComponent(FlexoComponentBuilder builder)
    {
        super(builder);
    }

    /**
     * @param model
     */
    public IEReusableComponent(ComponentDefinition model, FlexoProject prj)
    {
        super(model, prj);
    }
    
    @Override
    public ReusableComponentDefinition getComponentDefinition() {
    	return (ReusableComponentDefinition) super.getComponentDefinition();
    }
    
    @Override
    public String getClassNameKey() {
    	return "reusable_component";
    }

	@Override
	public String getFullyQualifiedName() {
		return "REUSABLE_COMPONENT_"+getName();
	}

	@Override
	public String getInspectorName() {
		return Inspectors.IE.REUSABLE_COMPONENT_INSPECTOR;
	}

}
