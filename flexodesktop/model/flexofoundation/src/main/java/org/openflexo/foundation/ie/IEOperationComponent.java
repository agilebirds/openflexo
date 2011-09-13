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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a whole-page WOComponent related to a ComponentDefinition attached
 * to an Operation Node
 * 
 * @author sguerin
 * 
 */
public final class IEOperationComponent extends IEPageComponent
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEOperationComponent.class.getPackage().getName());

    /**
     * Constructor invoked during deserialization for IEOperationComponent
     * 
     * @param componentDefinition
     */
    public IEOperationComponent(FlexoComponentBuilder builder)
    {
        super(builder);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for IEOperationComponent
     * 
     * @param componentDefinition
     */
    public IEOperationComponent(OperationComponentDefinition componentDefinition, FlexoProject project)
    {
        super(componentDefinition, project);
    }

    @Override
	public OperationComponentDefinition getComponentDefinition()
    {
        return (OperationComponentDefinition) super.getComponentDefinition();
    }

    // ===============================================
    // ================== Generation =================
    // ===============================================

    @Override
	public String getInspectorName()
    {
        return "OperationComponent.inspector";
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "Operation:" + getName();
    }
    
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "operation_component";
    }
    
    // ===============================================
    // =============== Tabs management ===============
    // ===============================================


    public boolean usesTabComponent (TabComponentDefinition tab)
    {
    	return (getAllTabComponentInstances(tab).size() > 0);
    }
    
    public Vector<TabComponentDefinition> getUsedTabComponents()
    {
    	Vector<TabComponentDefinition> returned = new Vector<TabComponentDefinition>();
    	for (TabComponentInstance tCI : getAllTabComponentInstances()) {
    		if (!returned.contains(tCI.getComponentDefinition())) {
    			returned.add(tCI.getComponentDefinition());
    		}
    	}
    	return returned;
    }
    
    public Vector<TabComponentInstance> getAllTabComponentInstances()
    {
    	Vector<TabComponentInstance> returned = new Vector<TabComponentInstance>();
    	for (ComponentInstance ci : getAllComponentInstances()) {
            if ((ci instanceof TabComponentInstance)
            		&& (ci.getComponentDefinition() != null)) {
            	returned.add((TabComponentInstance)ci);
            }
    	}
    	return returned;
    }
    
    public Vector<TabComponentInstance> getAllTabComponentInstances(TabComponentDefinition tab)
    {
    	Vector<TabComponentInstance> returned = new Vector<TabComponentInstance>();
    	for (TabComponentInstance tCI : getAllTabComponentInstances()) {
    		if (tCI.getComponentDefinition() == tab) {
    			returned.add(tCI);
    		}
    	}
    	return returned;
    }
    
}
