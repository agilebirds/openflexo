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
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.rm.ProjectRestructuration;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
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
public class XMLSchemaRepository extends DMRepository
{
	
	private FlexoProjectFile schemaFile;

	private Hashtable<String,String> packageToNamespace=new Hashtable<String,String>();
	
	
	  public Hashtable<String,String> getPackageToNamespace()
	    {
	    	
	         return packageToNamespace;
	    }

	    public void setPackageToNamespace(Hashtable<String,String> props)
	    {
	    	packageToNamespace = props;
	    }

	    public void setPackageToNamespaceForKey(String value, String key)
	    {
	    	if (value!=null)
	        	packageToNamespace.put(key, value);
	    	else
	    		packageToNamespace.remove(key);
	    }

	    public void removePackageToNamespaceWithKey(String key)
	    {
	       	packageToNamespace.remove(key);
	    }
	
    private static final Logger logger = Logger.getLogger(XMLSchemaRepository.class.getPackage().getName());

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
    public XMLSchemaRepository(FlexoDMBuilder builder)
    {
        this(builder.dmModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor
     */
    public XMLSchemaRepository(DMModel dmModel)
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
    public static XMLSchemaRepository createNewXMLSchemaRepository(String aName, DMModel dmModel, File schemaFileToCopy, FlexoProgress progress)
    {
        // Creates the repository
    	XMLSchemaRepository newRepository = new XMLSchemaRepository(dmModel);

        // Copy JAR file
        File copiedFile = new File(ProjectRestructuration.getExpectedDataModelDirectory(dmModel.getProject().getProjectDirectory()), schemaFileToCopy.getName());
        if (progress != null) {
            progress.setProgress(FlexoLocalization.localizedForKey("copying") + " " + schemaFileToCopy.getName());
        }
        try {
            if (logger.isLoggable(Level.INFO))
                logger.info("Copying file " + schemaFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
            FileUtils.copyFileToFile(schemaFileToCopy, copiedFile);
        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Could not copy file " + schemaFileToCopy.getAbsolutePath() + " to " + copiedFile.getAbsolutePath());
        }

        // Perform some settings
        FlexoProjectFile wsdlFile = new FlexoProjectFile(copiedFile, dmModel.getProject());
        newRepository.setXMLSchemaFile(wsdlFile);
        newRepository.setName(aName);

        dmModel.addToXmlSchemaRepositories(newRepository);

        
        return newRepository;
    }
    
    
    
    public void addPackageAndNamespace(String packName, String namespace) {
    	packageToNamespace.put(packName, namespace);
    }
    
    public String getNamespaceFromPackage(String packName) {
    	return packageToNamespace.get(packName);
    }
    
    
    @Override
	public int getOrder()
    {
        return 13;
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

    
    @Override
	public /*final */ void delete()
    {
        getDMModel().removeFromXmlSchemaRepositories(this);
        super.delete();
    }
    
    
    public FlexoProjectFile getXMLSchemaFile(){
    		return schemaFile;
    }
    public void setXMLSchemaFile(FlexoProjectFile aFile){
    		schemaFile=aFile;
    }
    
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "xml_schema_repository";
    }
    
}
