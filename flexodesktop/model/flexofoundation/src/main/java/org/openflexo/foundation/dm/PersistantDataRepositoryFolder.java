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

import java.util.logging.Logger;

public class PersistantDataRepositoryFolder extends DMRepositoryFolder {

	static final Logger logger = Logger.getLogger(PersistantDataRepositoryFolder.class.getPackage().getName());

	/**
	 * Default constructor
	 */
	public PersistantDataRepositoryFolder(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public int getRepositoriesCount() {
		return getDMModel().getProjectDatabaseRepositories().size() + getDMModel().getExternalDatabaseRepositories().size();
	}

	@Override
	public DMRepository getRepositoryAtIndex(int index) {
		if (index >= 0 && index < getRepositoriesCount()) {
			int d1 = getDMModel().getProjectDatabaseRepositories().size();
			if (index < getDMModel().getProjectDatabaseRepositories().size()) {
				return getDMModel().getProjectDatabaseRepositories().elementAt(index);
			} else if (index - d1 < getDMModel().getExternalDatabaseRepositories().size()) {
				return getDMModel().getExternalDatabaseRepositories().elementAt(index - d1);
			}
		}
		logger.warning("Index out of range: " + index);
		return null;
	}

	@Override
	public String getName() {
		return "persistant_data_repositories";
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".PERSISTANT_DATA_REPOSITORY_FOLDER";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}
}
