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
package org.openflexo.foundation.sg.implmodel;

import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.xml.ImplementationModelBuilder;


public abstract class TechnologyModuleImplementation extends TechnologyModelObject {

	public static final String CLASS_NAME_KEY = "technology_module_implementation";

   /**
     * Constructor invoked during deserialization
     * 
     * @param componentDefinition
     */
	public TechnologyModuleImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException
    {
    	this(builder.implementationModel);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for OEShema
     * 
     * @param shemaDefinition
     */
	public TechnologyModuleImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException
    {
        super(implementationModel);
		this.getImplementationModel().addToTechnologyModules(this);
    }
    
	@Override
	public String getFullyQualifiedName()
	{
		return getImplementationModel().getFullyQualifiedName()+"."+getClass().getSimpleName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getClassNameKey() {
		return CLASS_NAME_KEY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		String name = super.getName();
		if (name == null)
			return getTechnologyModuleDefinition().getName();
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete() {
		getImplementationModel().removeFromTechnologyModules(this);
		super.delete();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleImplementation getTechnologyModuleImplementation() {
		return this;
	}

	/**
	 * Return the TechnologyModuleDefinition associated to this implementation.
	 * 
	 * @return the TechnologyModuleDefinition associated to this implementation.
	 */
	public abstract TechnologyModuleDefinition getTechnologyModuleDefinition();
}
