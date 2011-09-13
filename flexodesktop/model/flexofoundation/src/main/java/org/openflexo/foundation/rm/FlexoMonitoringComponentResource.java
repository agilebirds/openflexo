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
package org.openflexo.foundation.rm;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEMonitoringScreen;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.MonitoringComponentDefinition;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;


public class FlexoMonitoringComponentResource extends FlexoComponentResource implements Serializable
{

	

	private static final Logger logger = Logger.getLogger(FlexoMonitoringComponentResource.class.getPackage().getName());
    
	private MonitoringComponentDefinition _componentDefinition;

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoMonitoringComponentResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
    }

    public FlexoMonitoringComponentResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public FlexoMonitoringComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource processResource, FlexoProjectFile componentFile) throws InvalidFileNameException
    {
        super(aProject, aName, processResource, componentFile);
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.MONITORING_COMPONENT;
    }

    public IEMonitoringScreen getIEScreenComponent()
    {
        return (IEMonitoringScreen) getResourceData();
    }

    @Override
	public IEWOComponent performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Loading component " + getName());
        IEMonitoringScreen monitoringScreen = (IEMonitoringScreen) super.performLoadResourceData(progress, loadingHandler);
        monitoringScreen.setProject(getProject());
        return monitoringScreen;
    }

    @Override
	public MonitoringComponentDefinition getComponentDefinition()
    {
        if (_componentDefinition == null) {
            _componentDefinition = (MonitoringComponentDefinition) getProject().getFlexoComponentLibrary().getComponentNamed(getName());
        }
        return _componentDefinition;
    }

    public static String resourceIdentifierForName(String aComponentName)
    {
        return ResourceType.MONITORING_COMPONENT.getName() + "." + aComponentName;
    }

}
