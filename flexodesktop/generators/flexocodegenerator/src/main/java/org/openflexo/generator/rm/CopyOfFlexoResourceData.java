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
package org.openflexo.generator.rm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.rm.cg.CopyOfFlexoResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileUtils;


public class CopyOfFlexoResourceData implements GeneratedResourceData, GenerationAvailableFile
{

	private static final Logger logger = FlexoLogger.getLogger(CopyOfFlexoResourceData.class.getPackage().getName());
	
    /**
	 * 
	 */
	private final CopyOfFlexoResource flexoCopiedResource;

	/**
	 * @param flexoCopiedResource
	 */
	public CopyOfFlexoResourceData(CopyOfFlexoResource flexoCopiedResource) {
		this.flexoCopiedResource = flexoCopiedResource;
	}

	private File source;

    /**
     * Overrides generate
     *
     * @see org.openflexo.foundation.rm.GeneratedResourceData#generate()
     */
    @Override
	public void generate() throws FlexoException
    {
    	if(flexoCopiedResource.getResourceToCopy() == null ||
    	   flexoCopiedResource.getResourceToCopy().isDeleted()/* ||
    	   !flexoCopiedResource.getResourceToCopy().getFile().exists()*/) {
    		source = null;
    	} else {
    		source = flexoCopiedResource.getResourceToCopy().getFile();
    	}
    	
    }

    //private 
    
    /**
     * Overrides getFlexoResource
     *
     * @see org.openflexo.foundation.rm.GeneratedResourceData#getFlexoResource()
     */
    @Override
	public FlexoGeneratedResource getFlexoResource()
    {
        return this.flexoCopiedResource;
    }

    /**
     * Overrides regenerate
     *
     * @see org.openflexo.foundation.rm.GeneratedResourceData#regenerate()
     */
    @Override
	public void regenerate() throws FlexoException
    {
        generate();
    }

    /**
     * Overrides writeToFile
     *
     * @see org.openflexo.foundation.rm.GeneratedResourceData#writeToFile(java.io.File)
     */
    @Override
	public void writeToFile(File aFile) throws FlexoException
    {
    	if(source==null) {
    		if (logger.isLoggable(Level.WARNING))
				logger.warning("Source is null for resource: "+this);
    		if (aFile.exists())
    			aFile.delete();
    		return;
    	}
        try {
            boolean needsNotifyEndOfSaving = false;
            FileWritingLock lock = null;
            if (!getFlexoResource().isSaving()) {
                logger.warning("writeToFile() called in "+getFlexoResource().getFileName()+" outside of RM-saving scheme");
                lock = getFlexoResource().willWriteOnDisk();
                needsNotifyEndOfSaving = true;
            }
            if (source.isFile())
                FileUtils.copyFileToFile(source, aFile);
            else if (source.isDirectory()) {
            	aFile.mkdirs();
                FileUtils.copyDirToDir(source, aFile);
            } else {
                if (logger.isLoggable(Level.SEVERE))
                    logger.severe("Resource to copy file is neither a file nor a directory "+this.flexoCopiedResource.getResourceToCopy().getFile().getAbsolutePath());
            }
            if (needsNotifyEndOfSaving) {
                getFlexoResource().hasWrittenOnDisk(lock);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOFlexoException(e);
        }
    }

    /**
     * Overrides getProject
     *
     * @see org.openflexo.foundation.rm.FlexoResourceData#getProject()
     */
    @Override
	public FlexoProject getProject()
    {
        return getFlexoResource().getProject();
    }

    /**
     * Overrides setFlexoResource
     *
     * @see org.openflexo.foundation.rm.FlexoResourceData#setFlexoResource(org.openflexo.foundation.rm.FlexoResource)
     */
    @Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException
    {

    }

	@Override
	public void setProject(FlexoProject project) {
		
	}

	@Override
	public FlexoResourceGenerator getGenerator() {
		return null;
	}


}