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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.OperationComponentInstance;
import org.openflexo.foundation.ie.TabComponentInstance;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoComponentLibraryResource;
import org.openflexo.foundation.rm.FlexoComponentResource;
import org.openflexo.foundation.rm.FlexoOperationComponentResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.wkf.node.OperationNode;
import org.openflexo.foundation.xml.FlexoComponentLibraryBuilder;
import org.openflexo.toolbox.FileUtils;


/**
 * Represents a component associated to a OperationNode, but only the
 * definition, not the component itself (no need to load the component to handle
 * a ComponentDefinition)
 * 
 * @author sguerin
 * 
 */
public final class OperationComponentDefinition extends ComponentDefinition implements Serializable
{
    private static final Logger logger = Logger.getLogger(ComponentDefinition.class.getPackage().getName());

    public OperationComponentDefinition(FlexoComponentLibrary componentLibrary)
    {
        super(componentLibrary);
    }

    /**
     * Constructor used during deserialization
     * 
     * @throws DuplicateResourceException
     * @throws DuplicateResourceException
     */
    public OperationComponentDefinition(FlexoComponentLibraryBuilder builder) throws DuplicateResourceException
    {
        this(null, builder.componentLibrary, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public OperationComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
            FlexoProject project, boolean checkUnicity) throws DuplicateResourceException
    {
        super(aComponentName, componentLibrary, aFolder, project);
        if (checkUnicity) {
            String resourceIdentifier = FlexoOperationComponentResource.resourceIdentifierForName(aComponentName);
            if ((project != null) && (project.isRegistered(resourceIdentifier))) {
                if (aFolder != null) {
                    aFolder.removeFromComponents(this);
                }
                throw new DuplicateResourceException(resourceIdentifier);
            }
        }
    }

    public OperationComponentDefinition(String aComponentName, FlexoComponentLibrary componentLibrary, FlexoComponentFolder aFolder,
            FlexoProject project) throws DuplicateResourceException
    {
        this(aComponentName, componentLibrary, aFolder, project, true);
    }
    
    @Override
    public IEWOComponent createNewComponent() {
    	return new IEOperationComponent(this,getProject());
    }

    @Override
	public FlexoComponentResource getComponentResource(boolean createIfNotExists)
    {
        if (getProject() != null) {
            FlexoComponentResource returned = getProject().getFlexoOperationComponentResource(getName());
            if (returned == null && createIfNotExists) {
                if (logger.isLoggable(Level.INFO))
                    logger.info("Creating new operation component resource !");
                // FlexoProcessResource processRes =
                // getProject().getFlexoProcessResource(getProcess().getName());
                FlexoComponentLibraryResource libRes = getProject().getFlexoComponentLibraryResource();
                File componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(),
                        this), _componentName + ".woxml");
                FlexoProjectFile resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                FlexoOperationComponentResource compRes=null;
                try {
                    compRes = new FlexoOperationComponentResource(getProject(), _componentName, libRes, resourceComponentFile);
                } catch (InvalidFileNameException e1) {
                    boolean ok = false;
                    for (int i = 0; i < 100 && !ok; i++) {
                        try {
                            componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(),
                                    this), FileUtils.getValidFileName(_componentName)+i + ".woxml");
                            resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                            compRes = new FlexoOperationComponentResource(getProject(), _componentName, libRes, resourceComponentFile);
                            ok = true;
                        } catch (InvalidFileNameException e) {
                        }
                    }
                    if (!ok) {
                        componentFile = new File(ProjectRestructuration.getExpectedDirectoryForComponent(getProject().getProjectDirectory(),
                                this), FileUtils.getValidFileName(_componentName)+getFlexoID()+ ".woxml");
                        resourceComponentFile = new FlexoProjectFile(componentFile, getProject());
                        try {
                            compRes = new FlexoOperationComponentResource(getProject(), _componentName, libRes, resourceComponentFile);
                        } catch (InvalidFileNameException e) {
                            if (logger.isLoggable(Level.SEVERE))
                                logger.severe("This should really not happen.");
                            return null;
                        }
                    }
                }
                if (compRes==null)
                    return null;
                compRes.setResourceData(new IEOperationComponent(this, getProject()));

                try {
                    compRes.getResourceData().setFlexoResource(compRes);
                    getProject().registerResource(compRes);
                } catch (DuplicateResourceException e) {
                    // Warns about the exception
                    if (logger.isLoggable(Level.WARNING))
                        logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
                    e.printStackTrace();
                    return null;
                }
                if (logger.isLoggable(Level.INFO))
                    logger.info("Registered component " + _componentName + " file: " + componentFile);
                returned = compRes;
            }
            return returned;
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not access to project !");
        }
        return null;
    }
    
    @Override
	public String getInspectorName()
    {
        return Inspectors.IE.OPERATION_COMPONENT_DEFINITION_INSPECTOR;
    }

    /**
     * Overrides getClassNameKey
     * 
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "operation_component_definition";
    }

    @Override
	public IEOperationComponent getWOComponent()
    {
        return (IEOperationComponent)super.getWOComponent();
    }

    public boolean usesTabComponent (TabComponentDefinition tab)
    {
    	return getWOComponent().usesTabComponent(tab);
    }
    
    public Vector<TabComponentDefinition> getUsedTabComponents()
    {
    	return getWOComponent().getUsedTabComponents();
    }
    
    public Vector<TabComponentInstance> getAllTabComponentInstances()
    {
    	return getWOComponent().getAllTabComponentInstances();
     }
    
    public Vector<TabComponentInstance> getAllTabComponentInstances(TabComponentDefinition tab)
    {
    	return getWOComponent().getAllTabComponentInstances(tab);
    }

	@Override
	public List<OperationNode> getAllOperationNodesLinkedToThisComponent()
	{
		List<OperationNode> results = new ArrayList<OperationNode>();
		for(ComponentInstance ci : getComponentInstances())
		{
			if (((OperationComponentInstance)ci).getOperationNode()!=null) {
				results.add(((OperationComponentInstance)ci).getOperationNode());
			}
		}
		return results;
	}
}
