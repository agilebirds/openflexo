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
import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.netbeans.lib.cvsclient.command.CommandException;
import org.netbeans.lib.cvsclient.connection.AuthenticationException;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.fps.CVSModule;
import org.openflexo.fps.CVSRepository;
import org.openflexo.fps.CVSRepositoryList;
import org.openflexo.fps.FPSObject;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.SharedProject;
import org.openflexo.localization.FlexoLocalization;

public class ShareProject extends CVSAction<ShareProject, FPSObject> {

	private static final Logger logger = Logger.getLogger(ShareProject.class.getPackage().getName());

	public static FlexoActionType<ShareProject, FPSObject, FPSObject> actionType = new FlexoActionType<ShareProject, FPSObject, FPSObject>(
			"share_project", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ShareProject makeNewAction(FPSObject focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new ShareProject(getRepositoryList(focusedObject), globalSelection, editor);
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
	private String _moduleName;
	private boolean _cvsIgnorize = false;
	private boolean _removeExistingCVSDirectories = false;
	private String _logMessage;
	private String _vendorTag;
	private String _releaseTag;

	ShareProject(CVSRepositoryList focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoAuthentificationException {
		if (getCvsIgnorize()) {
			FlexoProject.cvsIgnorize(_projectDirectory);
		}
		if (getRemoveExistingCVSDirectories()) {
			FlexoProject.removeCVSDirs(_projectDirectory);
		}
		try {
			_newProject = SharedProject.shareProject(getRepositoryList(getFocusedObject()), getProjectDirectory(), getRepository(),
					getModuleName(), getVendorTag(), getReleaseTag(), getLogMessage(), getEditor());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CommandException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			throw new FlexoAuthentificationException(_repository);
		}
	}

	public SharedProject getProject() {
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

	public boolean getCvsIgnorize() {
		return _cvsIgnorize;
	}

	public void setCvsIgnorize(boolean cvsIgnorize) {
		_cvsIgnorize = cvsIgnorize;
	}

	public String getLogMessage() {
		if (_logMessage == null)
			_logMessage = FlexoLocalization.localizedForKey("project_shared_on") + " " + new Date();
		return _logMessage;
	}

	public void setLogMessage(String logMessage) {
		_logMessage = logMessage;
	}

	public String getReleaseTag() {
		if (_releaseTag == null)
			_releaseTag = "start";
		return _releaseTag;
	}

	public void setReleaseTag(String releaseTag) {
		_releaseTag = releaseTag;
	}

	public String getVendorTag() {
		if (_vendorTag == null)
			_vendorTag = System.getProperty("user.name");
		return _vendorTag;
	}

	public void setVendorTag(String vendorTag) {
		_vendorTag = vendorTag;
	}

	public boolean getRemoveExistingCVSDirectories() {
		return _removeExistingCVSDirectories;
	}

	public void setRemoveExistingCVSDirectories(boolean removeExistingCVSDirectories) {
		_removeExistingCVSDirectories = removeExistingCVSDirectories;
	}

	public String getModuleName() {
		if (_moduleName == null) {
			_moduleName = getProjectDirectory().getName();
		}
		return _moduleName;
	}

	public void setModuleName(String moduleName) {
		_moduleName = moduleName;
	}

}
