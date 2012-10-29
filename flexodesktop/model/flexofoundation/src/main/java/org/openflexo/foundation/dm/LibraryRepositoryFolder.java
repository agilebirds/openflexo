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

public class LibraryRepositoryFolder extends DMRepositoryFolder {

	static final Logger logger = Logger.getLogger(LibraryRepositoryFolder.class.getPackage().getName());

	/**
	 * Default constructor
	 */
	public LibraryRepositoryFolder(DMModel dmModel) {
		super(dmModel);
	}

	// TODO: s'occuper des repositories RationalRose + Thesaurus + ThesaurusDB

	@Override
	public int getRepositoriesCount() {
		return getDMModel().getExternalRepositories().size() + getDMModel().getDenaliFoundationRepositories().size()
				+ getDMModel().getRationalRoseRepositories().size() + getDMModel().getThesaurusRepositories().size()
				+ getDMModel().getThesaurusDatabaseRepositories().size() + getDMModel().getWSDLRepositories().size()
				+ getDMModel().getXmlSchemaRepositories().size();
	}

	@Override
	public DMRepository getRepositoryAtIndex(int index) {
		if (index >= 0 && index < getRepositoriesCount()) {
			int existingRepositories = 0;
			if (index - existingRepositories < getDMModel().getExternalRepositories().size()) {
				return getDMModel().getExternalRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getExternalRepositories().size();

			if (index - existingRepositories < getDMModel().getDenaliFoundationRepositories().size()) {
				return getDMModel().getDenaliFoundationRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getDenaliFoundationRepositories().size();

			if (index - existingRepositories < getDMModel().getRationalRoseRepositories().size()) {
				return getDMModel().getRationalRoseRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getRationalRoseRepositories().size();

			if (index - existingRepositories < getDMModel().getWSDLRepositories().size()) {
				return getDMModel().getWSDLRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getWSDLRepositories().size();

			if (index - existingRepositories < getDMModel().getXmlSchemaRepositories().size()) {
				return getDMModel().getXmlSchemaRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getXmlSchemaRepositories().size();

			if (index - existingRepositories < getDMModel().getThesaurusRepositories().size()) {
				return getDMModel().getThesaurusRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getThesaurusRepositories().size();

			if (index - existingRepositories < getDMModel().getThesaurusDatabaseRepositories().size()) {
				return getDMModel().getThesaurusDatabaseRepositories().elementAt(index - existingRepositories);
			}
			existingRepositories += getDMModel().getThesaurusDatabaseRepositories().size();
		}
		logger.warning("Index out of range: " + index);
		return null;
	}

	@Override
	public String getName() {
		return "library_repositories";
	}

	@Override
	public String getFullyQualifiedName() {
		return getDMModel().getFullyQualifiedName() + ".LIBRARY_REPOSITORY_FOLDER";
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
