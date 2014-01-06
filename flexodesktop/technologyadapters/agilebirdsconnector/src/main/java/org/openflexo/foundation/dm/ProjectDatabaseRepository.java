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

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dm.eo.DMEORepository;
import org.openflexo.foundation.xml.FlexoDMBuilder;

/**
 * Represents a logical group of objects stored in a project database
 * 
 * @author sguerin
 * 
 */
public class ProjectDatabaseRepository extends DMEORepository {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ProjectDatabaseRepository.class.getPackage().getName());

	public String packageName;

	/**
	 * Constructor used during deserialization
	 */
	public ProjectDatabaseRepository(FlexoDMBuilder builder) {
		this(builder.dmModel);
		initializeDeserialization(builder);
	}

	/**
	 * Default constructor
	 */
	private ProjectDatabaseRepository(DMModel dmModel) {
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
	public static ProjectDatabaseRepository createNewProjectDatabaseRepository(DMModel dmModel, String repositoryName) {
		ProjectDatabaseRepository newProjectDatabaseRepository = new ProjectDatabaseRepository(dmModel);
		newProjectDatabaseRepository.setName(repositoryName);
		dmModel.addToProjectDatabaseRepositories(newProjectDatabaseRepository);
		return newProjectDatabaseRepository;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.foundation.dm.DMRepository#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		if (isReadOnly()) {
			return Inspectors.DM.DM_RO_EO_REPOSITORY_INSPECTOR;
		} else {
			return Inspectors.DM.DM_EO_REPOSITORY_INSPECTOR;
		}
	}

	@Override
	public int getOrder() {
		return 5;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean delete(boolean deleteEOModelFiles) {
		getDMModel().removeFromProjectDatabaseRepositories(this);
		return super.delete(deleteEOModelFiles);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "project_database_repository";
	}

}
