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
package org.openflexo.tm.postgres.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.sg.implmodel.layer.DatabaseTechnologyModuleImplementation;
import org.openflexo.foundation.xml.ImplementationModelBuilder;

/**
 * @author Nicolas Daniels
 */
public class PostgresImplementation extends DatabaseTechnologyModuleImplementation {

	public static final String TECHNOLOGY_MODULE_NAME = "Postgres";

    private String driverClassname="org.postgresql.Driver";
    private String driverArtifactId="postgresql";
    private String driverVersion="8.4-702.jdbc3";
    private String driverGroupId="postgresql";
	// ================ //
	// = Constructors = //
	// ================ //

	/**
	 * Build a new Postgres implementation for the specified implementation model builder.<br/>
	 * This constructor is namely invoked during unserialization.
	 * 
	 * @param builder
	 *            the builder that will create this implementation
	 */
	public PostgresImplementation(ImplementationModelBuilder builder) throws TechnologyModuleCompatibilityCheckException {
		this(builder.implementationModel);
		initializeDeserialization(builder);
	}

	/**
	 * Build a new Postgres implementation for the specified implementation model.
	 * 
	 * @param implementationModel
	 *            the implementation model where to create this Postgres implementation
	 */
	public PostgresImplementation(ImplementationModel implementationModel) throws TechnologyModuleCompatibilityCheckException {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getIsReservedKeywordsForDbObject(String name) {
		// TODO Auto-generated method stub
		return false;
	}

    // =================== //
	// = Getter / Setter = //
	// =================== //


    public String getDriverClassname() {
        return driverClassname;
    }

    public void setDriverClassname(String driverClassname) {
        this.driverClassname = driverClassname;
    }

    public String getDriverArtifactId() {
        return driverArtifactId;
    }

    public void setDriverArtifactId(String driverArtifactId) {
        this.driverArtifactId = driverArtifactId;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public void setDriverVersion(String driverVersion) {
        this.driverVersion = driverVersion;
    }

    public String getDriverGroupId() {
        return driverGroupId;
    }

    public void setDriverGroupId(String driverGroupId) {
        this.driverGroupId = driverGroupId;
    }
}
