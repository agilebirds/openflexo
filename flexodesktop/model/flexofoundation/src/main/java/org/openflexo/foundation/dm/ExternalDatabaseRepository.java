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

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a connexion to an already existing database (customer database for example)
 * 
 * @author sguerin
 * 
 */
public class ExternalDatabaseRepository extends DMEORepository {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ExternalDatabaseRepository.class.getPackage().getName());

	// ==========================================================================
	// ============================= Instance variables
	// =========================
	// ==========================================================================

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	/**
	 * Constructor used during deserialization
	 */
	public ExternalDatabaseRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private ExternalDatabaseRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getPersistantDataRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static ExternalDatabaseRepository createNewExternalDatabaseRepository(DMModel dmModel, String repositoryName) {
		Vector eoModelFiles = new Vector();
		ExternalDatabaseRepository newExternalDatabaseRepository = new ExternalDatabaseRepository(dmModel);
		newExternalDatabaseRepository.setName(repositoryName);
		dmModel.addToExternalDatabaseRepositories(newExternalDatabaseRepository);
		return newExternalDatabaseRepository;
	}

	@Override
	public int getOrder() {
		return 7;
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public void delete(boolean deleteEOModelFiles) {
		getDMModel().removeFromExternalDatabaseRepositories(this);
		super.delete(deleteEOModelFiles);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "external_database_repository";
	}

}
