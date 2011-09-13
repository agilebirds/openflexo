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
package org.openflexo.foundation.dm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.ws.WSRepository;
import org.openflexo.foundation.xml.FlexoDMBuilder;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileUtils;


/**
 * Represents a logical group of objects definition extracted from
 * a RationalRose file
 * 
 * @author sguerin
 * 
 */
public class WSDLRepository extends XMLSchemaRepository
{
	
	private FlexoProjectFile wsdlFile;

	 private static final Logger logger = Logger.getLogger(WSDLRepository.class.getPackage().getName());

    // ==========================================================================
    // ============================= Instance variables =========================
    // ==========================================================================

    // ==========================================================================
    // ============================= Constructor
    // ================================
    // ==========================================================================

    /**
     * Constructor used during deserialization
     */
    public WSDLRepository(FlexoDMBuilder builder)
    {
        this(builder.dmModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    private WSDLRepository(DMModel dmModel)
    {
        super(dmModel);
    }

    @Override
	public DMRepositoryFolder getRepositoryFolder()
    {
        return getDMModel().getLibraryRepositoryFolder();
    }
    


    
    
    /**
     * @param dmModel
     * @return
     */
    public static WSDLRepository createNewWSDLRepository(String aName, DMModel dmModel, File wsdlFileToCopy, FlexoProgress progress)throws DuplicateResourceException
    {
        // Creates the repository
    		DMRepository rep = dmModel.getRepositoryNamed(aName);
    		if(rep!=null){
    			throw new DuplicateResourceException("DMREPOSITORY."+aName);
    		}
    		WSDLRepository newRepository = new WSDLRepository(dmModel);

    		if(wsdlFileToCopy!=null){
        // Copy wsdl file
        File copiedFile = new File(ProjectRestructuration.getExpectedDataModelDirectory(dmModel.getProject().getProjectDirectory()), wsdlFileToCopy.getName());
        if (progress != null) {
            progress.setProgress(FlexoLocalization.localizedForKey("copying") + " " + wsdlFileToCopy.getName());
        }
        try {
            if (logger.isLoggable(Level.INFO))
                logger.info("Copying file " + wsdlFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
            FileUtils.copyFileToFile(wsdlFileToCopy, copiedFile);
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not copy file " + wsdlFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
        }

        // Perform some settings
        FlexoProjectFile wsdlFile = new FlexoProjectFile(copiedFile, dmModel.getProject());
        newRepository.setWSDLFile(wsdlFile);
    		}
        
        newRepository.setName(aName);

        dmModel.addToWSDLRepositories(newRepository);

        
        return newRepository;
    }
    
    
    
    
    @Override
	public int getOrder()
    {
        return 12;
    }

    @Override
	public boolean isReadOnly()
    {
        return true;
    }

    @Override
	public boolean isDeletable()
    {
        return true;
    }

    
    public boolean isUsedInWebService(){
    		WSRepository rep = getProject().getFlexoWSLibrary().getWSRepositoryNamed(getName());
    		if(rep!=null && rep.getWSDLRepository()!=null && rep.getWSDLRepository().equals(this))return true;
    		return false;
    }
    
    @Override
	public final void delete()
    {
		//1. remove from WS (while if there are many portTypes...)
		while(isUsedInWebService()){
			WSRepository rep = getProject().getFlexoWSLibrary().getWSRepositoryNamed(getName());
			if(rep!=null){
				rep.delete();
			}
		}
       if(wsdlFile!=null&&wsdlFile.getFile()!=null){
        boolean deleted = FileUtils.recursiveDeleteFile(getWSDLFile().getFile());
       
        if (logger.isLoggable(Level.WARNING) && !deleted) logger.warning("WSDLFile of WSDLRepository "+getName() +" not deleted");
       }
        wsdlFile=null;
        getDMModel().removeFromWSDLRepositories(this);
        super.delete();
    }
    
    
    public FlexoProjectFile getWSDLFile(){
    		return wsdlFile;
    }
    public void setWSDLFile(FlexoProjectFile aFile){
    		wsdlFile=aFile;
    }
    
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "wsdl_repository";
    }
    
}
