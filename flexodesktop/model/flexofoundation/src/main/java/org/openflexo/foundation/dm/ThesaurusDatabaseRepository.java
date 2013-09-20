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

import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a logical group of objects stored in a thesaurus database
 * 
 * @author sguerin
 * 
 */
public class ThesaurusDatabaseRepository extends DMEORepository {

	private static final Logger logger = Logger.getLogger(ThesaurusDatabaseRepository.class.getPackage().getName());

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
	public ThesaurusDatabaseRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ThesaurusDatabaseRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getLibraryRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 10;
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
	public boolean delete(boolean deleteEOModelFiles) {
		getDMModel().removeFromThesaurusDatabaseRepositories(this);
		return super.delete(deleteEOModelFiles);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "thesaurus_database_repository";
	}

}
