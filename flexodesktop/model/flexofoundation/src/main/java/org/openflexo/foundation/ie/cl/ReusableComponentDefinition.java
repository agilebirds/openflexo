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
package org.openflexo.foundation.ie.cl;

import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEReusableComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoReusableComponentResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.toolbox.FileUtils;


/**
 * @author bmangez
 * <B>Class Description</B>
 */
public class ReusableComponentDefinition extends PartialComponentDefinition implements Serializable
{
    private static final Logger logger = Logger.getLogger(TabComponentDefinition.class.getPackage().getName());

    /**
     * Constructor used during deserialization
     * 
     * @throws DuplicateResourceException
     */
    public ReusableComponentDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException
    {
        this(null, builder.componentLibrary, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public ReusableComponentDefinition(FlexoComponentLibrary componentLibrary)
    {
        super(componentLibrary);
    }

    public ReusableComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder, FlexoProject prj,
            boolean checkUnicity) throws DuplicateResourceException
    {
        super(aComponentName, componentLibrary, aFolder, prj);
        if (checkUnicity) {
            String resourceIdentifier = FlexoReusableComponentResource.resourceIdentifierForName(aComponentName);
            if ((aFolder != null) && (aFolder.getProject() != null) && (aFolder.getProject().isRegistered(resourceIdentifier))) {
                aFolder.removeFromComponents(this);
                throw new DuplicateResourceException(resourceIdentifier);
            }
        }
    }

    public ReusableComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder, FlexoProject prj)
            throws DuplicateResourceException
    {
        this(aComponentName, componentLibrary, aFolder, prj, true);
    }

    @Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists)
    {
        if (getProject() != null) {
            FlexoComponentResource returned = getProject().getFlexoReusableComponentResource(getName());
            if (returned == null && createIfNotExists) {
            	//if(isLoadingComponentResource)return null;
            	//isLoadingComponentResource = true;
            	if (logger.isLoggable(Level.INFO))
                    logger.info("Creating new reusable component resource !");
                // FlexoProcessResource processRes =
                // getProject().getFlexoProcessResource(getProcess().getName());
                File componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(), this), _componentName
                        + ".woxml");
                FlexoProjectFile resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                FlexoReusableComponentResource compRes=null;
                try {
                    compRes = new FlexoReusableComponentResource(getProject(), _componentName, getProject()
                            .getFlexoComponentLibraryResource(), resourceComponentFile);
                    try {
						getProject().registerResource(compRes);
					} catch (DuplicateResourceException e) {
						throw new InvalidFileNameException(resourceComponentFile);
					}
               } catch (InvalidFileNameException e1) {
                    boolean ok = false;
                    for (int i = 0; i < 100 && !ok; i++) {
                        try {
                            componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(), this), FileUtils.getValidFileName(_componentName)+i
                                    + ".woxml");
                            resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                            compRes = new FlexoReusableComponentResource(getProject(), _componentName, getProject()
                                    .getFlexoComponentLibraryResource(), resourceComponentFile);
                            try {
        						getProject().registerResource(compRes);
                                ok = true;
        					} catch (DuplicateResourceException e) {
        						throw new InvalidFileNameException(resourceComponentFile);
        					}
                        } catch (InvalidFileNameException e) {
                            if (logger.isLoggable(Level.SEVERE))
                                logger.severe("This should not happen");
                            //isLoadingComponentResource = false;
                            return null;
                        }
                    }
                    if (!ok) {
                        componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(), this), FileUtils.getValidFileName(_componentName)+getFlexoID()
                                + ".woxml");
                        resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                        try {
                            compRes = new FlexoReusableComponentResource(getProject(), _componentName, getProject()
                                    .getFlexoComponentLibraryResource(), resourceComponentFile);
                            try {
        						getProject().registerResource(compRes);
        					} catch (DuplicateResourceException e) {
        						throw new InvalidFileNameException(resourceComponentFile);
        					}
                       } catch (InvalidFileNameException e) {
                            if (logger.isLoggable(Level.SEVERE))
                                logger.severe("This should really not happen.");
                            //isLoadingComponentResource = false;
                            return null;
                        }
                    }
                }
                //isLoadingComponentResource = false;
                if (compRes==null)
                    return null;
                
                
                if (logger.isLoggable(Level.INFO))
                        logger.info("Registered component " + _componentName + " file: " + componentFile);
                    
                
                returned = compRes;
            }
            return returned;
        }
        return null;
    }

    @Override
    public String getClassNameKey() {
    	return "reusable_component";
    }
    
    @Override
    public IEWOComponent createNewComponent() {
    	return new IEReusableComponent(this,getProject());
    }
    
    @Override
	public String getInspectorName()
    {
        return Inspectors.IE.REUSABLE_COMPONENT_DEFINITION_INSPECTOR;
    }

}
