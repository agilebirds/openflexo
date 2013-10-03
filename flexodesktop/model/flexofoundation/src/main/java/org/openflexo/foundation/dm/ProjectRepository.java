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
 * Represents a logical group of objects used but not stored in memory
 * 
 * @author sguerin
 * 
 */
public class ProjectRepository extends DMRepository {

	private static final Logger logger = Logger.getLogger(ProjectRepository.class.getPackage().getName());

	public String packageName;

	// ==========================================================
	// =================== Constructor ==========================
	// ==========================================================

	/**
	 * Constructor used during deserialization
	 */
	public ProjectRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private ProjectRepository(DMModel dmModel) {
		super(dmModel);
	}

	@Override
	public DMRepositoryFolder getRepositoryFolder() {
		return getDMModel().getNonPersistantDataRepositoryFolder();
	}

	/**
	 * @param dmModel
	 * @return
	 */
	public static ProjectRepository createNewProjectRepository(String aName, DMModel dmModel) {
		ProjectRepository newProjectRepository = new ProjectRepository(dmModel);
		newProjectRepository.setName(aName);
		dmModel.addToProjectRepositories(newProjectRepository);
		return newProjectRepository;
	}

	@Override
	public int getOrder() {
		return 4;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public final boolean delete() {
		getDMModel().removeFromProjectRepositories(this);
		return super.delete();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "project_repository";
	}

}
