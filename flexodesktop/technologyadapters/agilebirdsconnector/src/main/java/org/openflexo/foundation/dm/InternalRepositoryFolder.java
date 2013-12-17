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

//import org.openflexo.foundation.dm.action.CreateDMRepository;

import java.util.logging.Logger;

public class InternalRepositoryFolder extends DMRepositoryFolder {

	static final Logger logger = Logger.getLogger(InternalRepositoryFolder.class.getPackage().getName());

	/**
	 * Default constructor
	 */
	public InternalRepositoryFolder(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public int getRepositoriesCount() {
		return 7;
	}

	@Override
	public DMRepository getRepositoryAtIndex(int index) {
		switch (index) {
		case 0:
			return getDMModel().getJDKRepository();
		case 1:
			return getDMModel().getWORepository();
		case 2:
			return getDMModel().getProcessInstanceRepository();
		case 3:
			return getDMModel().getProcessBusinessDataRepository();
		case 4:
			return getDMModel().getComponentRepository();
		case 5:
			return getDMModel().getExecutionModelRepository();
		case 6:
			return getDMModel().getEOPrototypeRepository();
		default:
			logger.warning("Index out of range: " + index);
			return null;
		}
	}

	@Override
	public String getName() {
		return "internal_repositories";
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".INTERNAL_REPOSITORY_FOLDER";
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
