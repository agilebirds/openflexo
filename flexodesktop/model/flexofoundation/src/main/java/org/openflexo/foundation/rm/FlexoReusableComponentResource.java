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
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class FlexoReusableComponentResource extends FlexoComponentResource implements Serializable
{

    private static final Logger logger = Logger.getLogger(FlexoTabComponentResource.class.getPackage().getName());

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoReusableComponentResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
   }

    public FlexoReusableComponentResource(FlexoProject aProject)
    {
        super(aProject);
    }

    public FlexoReusableComponentResource(FlexoProject aProject, String aName, FlexoComponentLibraryResource clResource, FlexoProjectFile componentFile) throws InvalidFileNameException
    {
        super(aProject, aName, clResource, componentFile);

    }

    @Override
	public String getName()
    {
        return name;
    }

    public String getLastName()
    {
        return name;
    }

    @Override
	public void setName(String aName)
    {
        StringTokenizer st = new StringTokenizer(aName, ".");
        while (st.hasMoreTokens()) {
            name = st.nextToken();
        }
        setChanged();
    }

    @Override
	public ResourceType getResourceType()
    {
        return ResourceType.REUSABLE_COMPONENT;
    }

    @Override
	public IEReusableComponent getWOComponent()
    {
        return (IEReusableComponent) getResourceData();
    }

    @Override
	public IEWOComponent performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException
    {
        if (logger.isLoggable(Level.INFO))
            logger.info("Loading component " + getName());
        IEReusableComponent singleWidgetComponent = (IEReusableComponent) super.performLoadResourceData(progress, loadingHandler);
        singleWidgetComponent.setProject(getProject());
        return singleWidgetComponent;
    }

    private ReusableComponentDefinition _componentDefinition;

    @Override
	public ReusableComponentDefinition getComponentDefinition()
    {
        if (_componentDefinition == null) {
            _componentDefinition = (ReusableComponentDefinition) getProject().getFlexoComponentLibrary().getComponentNamed(name);
            if (_componentDefinition == null) {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("cant find an SingleWidgetComponentDefinition for component: " + getName() + " in library");
                if (logger.isLoggable(Level.INFO))
                    logger.info("Trying to repair...");
                FlexoComponentFolder rootFolder = getProject().getFlexoComponentLibrary().getRootFolder();
                try {
                    _componentDefinition = new ReusableComponentDefinition(getName(), getProject().getFlexoComponentLibrary(), rootFolder.getFolderTyped(FolderType.PARTIAL_COMPONENT_FOLDER), getProject());
                } catch (DuplicateResourceException e) {
                }
            }
        }
        return _componentDefinition;
    }

    public static String resourceIdentifierForName(String aComponentName)
    {
        return ResourceType.REUSABLE_COMPONENT.getName() + "." + aComponentName;
    }

    @Override
	public Object instanciateNewBuilder()
    {
        Object returned = super.instanciateNewBuilder();
        ((FlexoComponentBuilder) returned).componentDefinition = getComponentDefinition();
        // System.out.println("instanciation of thumbnail
        // builder:"+((FlexoComponentBuilder)returned).singleWidgetComponentDefinition==null?"WITH
        // NULL DEF":"with valid def");
        return returned;
    }
}
