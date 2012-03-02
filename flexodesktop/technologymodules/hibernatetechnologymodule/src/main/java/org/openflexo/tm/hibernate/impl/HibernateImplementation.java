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
package org.openflexo.tm.hibernate.impl;

import java.util.Collections;
import java.util.Vector;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.event.SGAttributeModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectAddedToListModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectDeletedModification;
import org.openflexo.foundation.sg.implmodel.event.SGObjectRemovedFromListModification;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.sg.implmodel.layer.DatabaseTechnologyModuleImplementation;
import org.openflexo.foundation.xml.ImplementationModelBuilder;
import org.openflexo.tm.persistence.impl.HibernateModel;

/**
 * This class defines properties related to a Spring implementation.
 * 
 * @author Emmanuel Koch, Blue Pimento Services SPRL
 */
public class HibernateImplementation extends TechnologyModuleImplementation {

	public static final String TECHNOLOGY_MODULE_NAME = "Hibernate";

	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Hibernate implementation for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this implementation
	 * @throws TechnologyModuleCompatibilityCheckException
	 */
	public HibernateImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Hibernate implementation for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Spring implementation
	 * @throws TechnologyModuleCompatibilityCheckException
	 */
	public HibernateImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
		super(implementationModel);
	}

	// =========== //
	// = Methods = //
	// =========== //

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TechnologyModuleDefinition getTechnologyModuleDefinition() {
		return TechnologyModuleDefinition.getTechnologyModuleDefinition(TECHNOLOGY_MODULE_NAME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getHasInspector() {
		return true;
	}

}
