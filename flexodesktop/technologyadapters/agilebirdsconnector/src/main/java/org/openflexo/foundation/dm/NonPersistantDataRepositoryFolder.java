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

public class NonPersistantDataRepositoryFolder extends DMRepositoryFolder {

	static final Logger logger = Logger.getLogger(NonPersistantDataRepositoryFolder.class.getPackage().getName());

	/**
	 * Default constructor
	 */
	public NonPersistantDataRepositoryFolder(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public int getRepositoriesCount() {
		return getDMModel().getProjectRepositories().size();
	}

	@Override
	public DMRepository getRepositoryAtIndex(int index) {
		if (index >= 0 && index < getDMModel().getProjectRepositories().size()) {
			return getDMModel().getProjectRepositories().elementAt(index);
		}
		logger.warning("Index out of range: " + index);
		return null;
	}

	@Override
	public String getName() {
		return "non_persistant_data_repositories";
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".NON_PERSISTANT_DATA_REPOSITORY_FOLDER";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return getName();
	}

}
