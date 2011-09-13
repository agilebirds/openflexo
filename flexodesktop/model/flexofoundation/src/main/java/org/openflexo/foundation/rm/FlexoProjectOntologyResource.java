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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.ProjectOntology;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingHandler;


/**
 * Represents an EOModel resource
 *
 * @author sguerin
 *
 */
public class FlexoProjectOntologyResource extends FlexoStorageResource<ProjectOntology>
{

    private static final Logger logger = Logger.getLogger(FlexoProjectOntologyResource.class.getPackage().getName());

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     *
     * @param builder
     */
    public FlexoProjectOntologyResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
    }

    public FlexoProjectOntologyResource(FlexoProject aProject)
    {
        super(aProject);
    }

    /*public FlexoProjectOntologyResource(FlexoProject aProject, FlexoDMResource dmResource, FlexoProjectFile eoModelFile)
            throws InvalidFileNameException
    {
        this(aProject);
        setResourceFile(eoModelFile);
        addToSynchronizedResources(dmResource);
        if (logger.isLoggable(Level.INFO))
            logger.info("Build new FlexoEOModelResource");
    }*/

    public FlexoProjectOntologyResource(
    		FlexoProject aProject,
    		ProjectOntology aProjectOntology,
    		FlexoProjectFile ontologyFile) throws InvalidFileNameException, DuplicateResourceException
    {
        this(aProject);
        _resourceData = aProjectOntology;
        aProjectOntology.setFlexoResource(this);
        setResourceFile(ontologyFile);
    }

    @Override
    public ResourceType getResourceType()
    {
        return ResourceType.PROJECT_ONTOLOGY;
    }

    @Override
	public String getName()
    {
        return getProject().getProjectName();
    }

    public Class getResourceDataClass()
    {
        return ProjectOntology.class;
    }

    @Override
    public void setName(String aName)
    {
        // Not allowed
    }

    @Override
    public ProjectOntology performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException
    {
    	_resourceData = getProject().getProjectOntologyLibrary()._loadProjectOntology(getProject().getURI(), getFile());
    	try {
			_resourceData.setFlexoResource(this);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			logger.warning("Should not happen");
		}
    	notifyResourceStatusChanged();
        return _resourceData;
    }

    /**
     * Implements
     *
     * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
     * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
     */
    @Override
    protected void saveResourceData(boolean clearIsModified) throws SaveResourceException
    {
        if (!hasWritePermission()) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Permission denied : " + getFile().getAbsolutePath());
            throw new SaveResourcePermissionDeniedException(this);
        }
        if (_resourceData != null) {
           	FileWritingLock lock = willWriteOnDisk();
           	_writeToFile();
           	hasWrittenOnDisk(lock);
           	notifyResourceStatusChanged();
           	if (logger.isLoggable(Level.INFO))
                logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
        }
        if (clearIsModified)
            getResourceData().clearIsModified(false);
    }
    
    
	public void _writeToFile() throws SaveResourceException 
	{
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			_resourceData.getOntModel().write(out,null,_resourceData.getOntologyURI());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SaveResourceException(this);
			}
		}
		
		logger.info("Wrote "+getFile());
	}



 }
