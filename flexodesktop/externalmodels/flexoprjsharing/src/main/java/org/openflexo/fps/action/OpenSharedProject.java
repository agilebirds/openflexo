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
package org.openflexo.fps.action;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.SharedProject;

public class OpenSharedProject extends CVSAction<OpenSharedProject, FPSObject> {

	private static final Logger logger = Logger.getLogger(OpenSharedProject.class.getPackage().getName());

	public static FlexoActionType<OpenSharedProject, FPSObject, FPSObject> actionType = new FlexoActionType<OpenSharedProject, FPSObject, FPSObject>(
			"open_project", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public OpenSharedProject makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new OpenSharedProject(getRepositoryList(focusedObject), globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FPSObject object, Vector<FPSObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FPSObject object, Vector<FPSObject> globalSelection) {
			return getRepositoryList(object) != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, CVSRepositoryList.class);
		FlexoModelObject.addActionForClass(actionType, CVSRepository.class);
		FlexoModelObject.addActionForClass(actionType, CVSModule.class);
	}

	private SharedProject _newProject;
	private File _projectDirectory = null;
	private CVSRepository _repository;

	OpenSharedProject(CVSRepositoryList focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (_repository != null) {
			_newProject = SharedProject.openProject(getRepositoryList(getFocusedObject()), _projectDirectory, _repository, getEditor());
		} else {
			_newProject = SharedProject.openProject(getRepositoryList(getFocusedObject()), _projectDirectory, getEditor());
		}
	}

	public SharedProject getNewProject() {
		return _newProject;
	}

	public File getProjectDirectory() {
		return _projectDirectory;
	}

	public void setProjectDirectory(File projectDirectory) {
		_projectDirectory = projectDirectory;
	}

	public CVSRepository getRepository() {
		return _repository;
	}

	public void setRepository(CVSRepository repository) {
		_repository = repository;
	}

}
