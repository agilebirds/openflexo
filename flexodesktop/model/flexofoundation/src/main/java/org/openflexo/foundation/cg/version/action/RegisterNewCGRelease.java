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
package org.openflexo.foundation.cg.version.action;

import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.version.CGRelease;
import org.openflexo.foundation.cg.version.CGVersionIdentifier;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringRepresentable;

public class RegisterNewCGRelease extends AbstractGCAction<RegisterNewCGRelease, GenerationRepository> {

	public enum IncrementType implements StringRepresentable {
		IncrementMajor, IncrementMinor;

		@Override
		public String toString() {
			return FlexoLocalization.localizedForKey(super.toString());
		}
	}

	private static final Logger logger = Logger.getLogger(RegisterNewCGRelease.class.getPackage().getName());

	public static FlexoActionType<RegisterNewCGRelease, GenerationRepository, CGObject> actionType = new FlexoActionType<RegisterNewCGRelease, GenerationRepository, CGObject>(
			"register_new_release", versionningMenu, versionningActionsGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RegisterNewCGRelease makeNewAction(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RegisterNewCGRelease(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(GenerationRepository object, Vector<CGObject> globalSelection) {
			return object != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, GenerationRepository.class);
	}

	RegisterNewCGRelease(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private String _name;
	private String _description;
	private Date _date;
	private String _userId;
	private CGVersionIdentifier _versionIdentifier;

	private CGRelease _newCGRelease;

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException {
		logger.info("Register new CGRelease");

		makeFlexoProgress(FlexoLocalization.localizedForKey("release_as") + " " + getVersionIdentifier().versionAsString(),
				getFocusedObject().getFiles().size() + 1);

		_newCGRelease = new CGRelease(getFocusedObject());
		_newCGRelease.setName(getName());
		_newCGRelease.setDescription(getDescription());
		_newCGRelease.setDate(getDate());
		_newCGRelease.setUserId(getUserId());
		_newCGRelease.setVersionIdentifier(getVersionIdentifier());
		getFocusedObject().addToReleases(_newCGRelease);

		for (CGFile file : getFocusedObject().getFiles()) {
			setProgress(FlexoLocalization.localizedForKey("release") + " " + file.getFileName());
			file.releaseAs(_newCGRelease);
		}

		// Refreshing repository
		getFocusedObject().refresh();

		hideFlexoProgress();

	}

	public String getDescription() {
		return _description;
	}

	public void setDescription(String description) {
		_description = description;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getUserId() {
		return _userId;
	}

	public void setUserId(String userId) {
		_userId = userId;
	}

	public CGVersionIdentifier getVersionIdentifier() {
		return _versionIdentifier;
	}

	public void setVersionIdentifier(CGVersionIdentifier versionIdentifier) {
		_versionIdentifier = versionIdentifier;
	}

	public Date getDate() {
		return _date;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public CGRelease getNewCGRelease() {
		return _newCGRelease;
	}

}
