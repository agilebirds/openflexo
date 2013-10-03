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

import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a logical group of objects definition extracted from a RationalRose file
 * 
 * @author sguerin
 * 
 */
public class RationalRoseRepository extends DMRepository {

	private boolean isReadOnly = true;

	/**
	 * Constructor used during deserialization
	 */
	public RationalRoseRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private RationalRoseRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getLibraryRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static RationalRoseRepository createNewRationalRoseRepository(String aName, DMModel dmModel) {
		RationalRoseRepository newRepository = new RationalRoseRepository(dmModel);
		newRepository.setName(aName);
		dmModel.addToRationalRoseRepositories(newRepository);
		return newRepository;
	}

	@Override
	public int getOrder() {
		return 11;
	}

	/**
	 * By default, Rational rose repository are always read-only. However, we need to temporarily set as not "read-only" when we populate
	 * the repository from an MDL file.
	 */
	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setIsReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public final boolean delete() {
		getDMModel().removeFromRationalRoseRepositories(this);
		return super.delete();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "rational_rose_repository";
	}

}
