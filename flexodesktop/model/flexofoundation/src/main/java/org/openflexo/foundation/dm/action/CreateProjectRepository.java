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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.ProjectRepository;

public class CreateProjectRepository extends CreateDMRepository<CreateProjectRepository> {

	static final Logger logger = Logger.getLogger(CreateProjectRepository.class.getPackage().getName());

	public static FlexoActionType<CreateProjectRepository, DMObject, DMObject> actionType = new FlexoActionType<CreateProjectRepository, DMObject, DMObject>(
			"add_non_persistant_project_repository", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CreateProjectRepository makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new CreateProjectRepository(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

	};

	CreateProjectRepository(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		_newRepository = ProjectRepository.createNewProjectRepository(getNewRepositoryName(), getProject().getDataModel());
	}

	@Override
	public String getRepositoryType() {
		return PROJECT_REPOSITORY;
	}

	@Override
	public ProjectRepository getNewRepository() {
		return (ProjectRepository) super.getNewRepository();
	}

}
