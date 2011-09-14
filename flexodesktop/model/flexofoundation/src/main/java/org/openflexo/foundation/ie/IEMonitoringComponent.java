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

import org.openflexo.foundation.ie.cl.MonitoringComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a WOComponent (reusable component generally embedded in a
 * IEOperationComponent or a IEMonitoring Screen) representing a perspective to
 * monitor a FlexoProcess
 * 
 * @author sguerin
 * 
 */
public final class IEMonitoringComponent extends IEPartialComponent
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEMonitoringComponent.class.getPackage().getName());

    protected FlexoProcess monitoredProcess;

    public IEMonitoringComponent(FlexoComponentBuilder builder)
    {
        super(builder);
        initializeDeserialization(builder);
    }

    public IEMonitoringComponent(MonitoringComponentDefinition def, FlexoProject project)
    {
        super(def, project);
    }
    
    public FlexoProcess getMonitoredProcess()
    {
        return ((MonitoringComponentDefinition)getComponentDefinition()).getProcess();
    }

    @Override
	public String getInspectorName()
    {
        return "MonitoringComponent.inspector";
    }

    /**
     * Return a Vector of embedded IEObjects at this level. NOTE that this is
     * NOT a recursive method
     * 
     * @return a Vector of IEObject instances
     */
    @Override
	public Vector<IObject> getEmbeddedIEObjects()
    {
        return EMPTY_IOBJECT_VECTOR;
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "Monitoring:" + getName();
    }

    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "monitoring_component";
    }
    
}
