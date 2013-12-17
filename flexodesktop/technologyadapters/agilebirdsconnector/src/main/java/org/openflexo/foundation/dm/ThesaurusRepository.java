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

import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a logical group of objects stored in a non-database thesaurus
 * 
 * @author sguerin
 * 
 */
public class ThesaurusRepository extends DMRepository {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ThesaurusRepository.class.getPackage().getName());

	/**
	 * Constructor used during deserialization
	 */
	public ThesaurusRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	public ThesaurusRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getLibraryRepositoryFolder();
	}

	@Override
	public int getOrder() {
		return 9;
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
	public final boolean delete() {
		getDMModel().removeFromThesaurusRepositories(this);
		return super.delete();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "thesaurus_repository";
	}

}
