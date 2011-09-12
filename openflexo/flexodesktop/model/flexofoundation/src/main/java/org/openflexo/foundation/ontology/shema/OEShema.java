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
package org.openflexo.foundation.ontology.shema;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ontology.calc.OntologyCalc;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoOEShemaResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.XMLStorageResourceData;
import org.openflexo.foundation.xml.OEShemaBuilder;
import org.openflexo.xmlcode.XMLMapping;



public class OEShema extends OEShemaObject implements XMLStorageResourceData {

    private static final Logger logger = Logger.getLogger(OEShema.class.getPackage().getName());

	private FlexoProject _project;
	private FlexoOEShemaResource _resource;
	private OEShemaDefinition _shemaDefinition;
	private OntologyCalc _calc;
	
    /**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
    public OEShema(OEShemaBuilder builder)
    {
    	this(builder.shemaDefinition,builder.getProject());
    	builder.shema = this;
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for OEShema
     * 
     * @param shemaDefinition
     */
    public OEShema(OEShemaDefinition shemaDefinition, FlexoProject project)
    {
        super(project);
        logger.info("Created new shema with project "+project);
        _project = project;
        _shemaDefinition = shemaDefinition;
        setShema(this);
        if (getCalc() != null) getCalc().loadWhenUnloaded();
   }

    
    public OEShemaDefinition getShemaDefinition()
    {
        return _shemaDefinition;
    }

	@Override
	public FlexoOEShemaResource getFlexoResource() 
	{
		return _resource;
	}

	@Override
	public FlexoXMLStorageResource getFlexoXMLFileResource() 
	{
		return getFlexoResource();
	}

	@Override
	public void setFlexoResource(FlexoResource resource) throws DuplicateResourceException 
	{
		_resource = (FlexoOEShemaResource)resource;
	}

	@Override
	public void save() throws SaveResourceException 
	{
		getFlexoResource().saveResourceData();
	}

	@Override
	public FlexoProject getProject()
	{
		return _project;
	}

	@Override
	public void setProject(FlexoProject aProject)
	{
		_project = aProject;
	}

	@Override
	public String getClassNameKey() 
	{
		return "oe_shema";
	}

    @Override
	public String getName()
    {
        if (getShemaDefinition() != null) {
            return getShemaDefinition().getName();
        }
        return null;
    }

    @Override
	public void setName(String name) throws DuplicateResourceException, InvalidNameException
    {
        if (getShemaDefinition() != null) {
        	getShemaDefinition().setName(name);
        }
    }

    @Override
    public String getDescription() 
    {
    	if (isSerializing()) return null;
        if (getShemaDefinition() != null) {
        	return getShemaDefinition().getDescription();
        }
        return super.getDescription();
   }
    
    @Override
    public void setDescription(String description)
    {
        if (getShemaDefinition() != null) {
        	getShemaDefinition().setDescription(description);
        }
        super.setDescription(description);
    }
    
    @Override
    public Hashtable<String, String> getSpecificDescriptions() 
    {
    	if (isSerializing()) return null;
        if (getShemaDefinition() != null) {
        	return getShemaDefinition().getSpecificDescriptions();
        }
        return super.getSpecificDescriptions();
    }
    
    @Override
    public boolean getHasSpecificDescriptions() 
    {
         if (getShemaDefinition() != null) {
        	return getShemaDefinition().getHasSpecificDescriptions();
        }
    	return super.getHasSpecificDescriptions();
    }
    
	@Override
	public String getFullyQualifiedName() 
	{
		return getProject().getFullyQualifiedName()+"."+getShemaDefinition().getName();
	}

	@Override
	public XMLMapping getXMLMapping()
	{
		return getProject().getXmlMappings().getShemaMapping();
	}

    @Override
	public String getInspectorName() 
    {
    	return Inspectors.OE.OE_SHEMA_INSPECTOR;
    }

	public OntologyCalc getCalc() 
	{
		if (getShemaDefinition() != null) {
			return getShemaDefinition().getCalc();
		}
		return null;
	}

	@Override
	public boolean isContainedIn(OEShemaObject o)
	{
		return o == this;
	}
	
	@Override
	public String getDisplayableDescription()
	{
		return "Shema "+getName()+(getCalc() != null ? " (calc "+getCalc().getName()+")" : "");
	}

	/**
	 * @return
	 */
	public static final String getTypeName() 
	{
		return "SHEMA";
	}


}
