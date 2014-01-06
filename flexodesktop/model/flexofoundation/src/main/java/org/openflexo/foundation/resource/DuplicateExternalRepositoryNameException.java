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
package org.openflexo.foundation.resource;

import org.openflexo.foundation.FlexoException;
import org.openflexo.localization.FlexoLocalization;

/**
 * Thrown when attempting to create or rename an ProjectExternalRepository whose name is duplicated
 * 
 * @author gpolet
 * 
 */
@SuppressWarnings("serial")
public class DuplicateExternalRepositoryNameException extends FlexoException {

	private final ProjectExternalRepository repository;
	private final String name;

	/**
	 * @param folder
	 * @param name
	 */
	public DuplicateExternalRepositoryNameException(ProjectExternalRepository repository, String name) {
		this.repository = repository;// may be null
		this.name = name;
	}

	public ProjectExternalRepository getRepository() {
		return repository;
	}

	public String getName() {
		return name;
	}

	/**
	 * Overrides getLocalizedMessage
	 * 
	 * @see org.openflexo.foundation.FlexoException#getLocalizedMessage()
	 */
	@Override
	public String getLocalizedMessage() {
		return FlexoLocalization.localizedForKey("duplicate_external_repository_name");
	}
}
