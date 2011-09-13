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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaDefinition;
import org.openflexo.foundation.rm.FlexoProject.FlexoIDMustBeUnique.DuplicateObjectIDIssue;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.xml.OEShemaBuilder;


/**
 * Represents a shema resource
 * 
 * @author sguerin
 * 
 */
public class FlexoOEShemaResource extends FlexoXMLStorageResource<OEShema>
{

	

	private static final Logger logger = Logger.getLogger(FlexoOEShemaResource.class.getPackage().getName());
    
	protected String name;

    public FlexoOEShemaResource(FlexoProject aProject, String aName, FlexoOEShemaLibraryResource slResource,
            FlexoProjectFile componentFile) throws InvalidFileNameException
    {
        this(aProject);
        setName(aName);
        setResourceFile(componentFile);
        addToSynchronizedResources(slResource);
     }

    /**
     * Constructor used for XML Serialization: never try to instanciate resource
     * from this constructor
     * 
     * @param builder
     */
    public FlexoOEShemaResource(FlexoProjectBuilder builder)
    {
        this(builder.project);
        builder.notifyResourceLoading(this);
    }

    public FlexoOEShemaResource(FlexoProject aProject)
    {
        super(aProject);
    }

    @Override
	public String getName()
    {
        return name;
    }

    @Override
	public void setName(String aName)
    {
        name = aName;
        setChanged();
    }
    
    public void setResourceData(OEShema shema)
    {
    	_resourceData = shema;
    }

    public OEShema getShema()
    {
        return getResourceData();
    }

    public OEShema getShema(FlexoProgress progress)
    {
        return getResourceData(progress);
    }

    public final OEShema createNewShema() 
    {
    	return getShemaDefinition().createNewShema();
    }

    private OEShemaDefinition _shemaDefinition;

    public OEShemaDefinition getShemaDefinition()
    {
        if (_shemaDefinition == null) {
        	_shemaDefinition = getProject().getShemaLibrary().getShemaNamed(getName());
        }

        return _shemaDefinition;
    }
 
    @Override
	public OEShema performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadXMLResourceException, FlexoFileNotFoundException, ProjectLoadingCancelledException, MalformedXMLException
    {
    	OEShema shema;
        if (logger.isLoggable(Level.FINE))
            logger.fine("Loading shema " + getName());
        try {
            shema = super.performLoadResourceData(progress, loadingHandler);
        } catch (FlexoFileNotFoundException e) {
            // OK, i create the resource by myself !
            if (logger.isLoggable(Level.INFO))
                logger.info("Creating new component " + getName());
            shema = createNewShema();
            try {
				shema.setFlexoResource(this);
			} catch (DuplicateResourceException e1) {
				e1.printStackTrace();
				logger.warning("DuplicateResourceException !!!");
			}
            _resourceData = shema;
        }
        if (shema != null) {
            shema.setProject(getProject());
        }
        if (logger.isLoggable(Level.FINE))
            logger.fine("Notify loading for shema " + getShemaDefinition().getName());
        getShemaDefinition().notifyShemaHasBeenLoaded();
        
        logger.info("OK, on a charge le shema");
        
        return shema;
    }

    /**
     * Returns a boolean indicating if this resource needs a builder to be
     * loaded Returns true to indicate that process deserializing requires a
     * FlexoProcessBuilder instance: in this case: YES !
     * 
     * @return boolean
     */
    @Override
	public boolean hasBuilder()
    {
        return true;
    }

    @Override
	public Class getResourceDataClass()
    {
        return OEShema.class;
    }

    /**
     * Returns the required newly instancied builder if this resource needs a
     * builder to be loaded
     * 
     * @return boolean
     */
    @Override
	public Object instanciateNewBuilder()
    {
		OEShemaBuilder builder = new OEShemaBuilder(getShemaDefinition(), this);
		builder.shema = _resourceData;
		return builder;
	}

    @Override
    protected boolean isDuplicateSerializationIdentifierRepairable() {
    	return true;
    }
    
    @Override
	protected boolean repairDuplicateSerializationIdentifier() {
		ValidationReport report = getProject().validate();
		for (ValidationIssue issue: report.getValidationIssues())
			if (issue instanceof DuplicateObjectIDIssue)
				return true;
		return false;
	}
    
    /**
     * Rebuild resource dependancies for this resource
     */
    @Override
	public void rebuildDependancies()
    {
        super.rebuildDependancies();
        addToSynchronizedResources(getProject().getFlexoShemaLibraryResource());
    }

	@Override
	public ResourceType getResourceType() 
	{
		return ResourceType.OE_SHEMA;
	}


}
