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
package org.openflexo.foundation.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.flexo.model.FlexoModelObject;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.resource.RepositoryFolder;

public class AddRepositoryFolder extends FlexoAction<AddRepositoryFolder, RepositoryFolder, RepositoryFolder> {

	private static final Logger logger = Logger.getLogger(AddRepositoryFolder.class.getPackage().getName());

	public static FlexoActionType<AddRepositoryFolder, RepositoryFolder, RepositoryFolder> actionType = new FlexoActionType<AddRepositoryFolder, RepositoryFolder, RepositoryFolder>(
			"create_folder", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddRepositoryFolder makeNewAction(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection,
				FlexoEditor editor) {
			return new AddRepositoryFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<RepositoryFolder> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<RepositoryFolder> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddRepositoryFolder.actionType, RepositoryFolder.class);
	}

	private RepositoryFolder newFolder;
	private String newFolderName;

	AddRepositoryFolder(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Add view folder");
		if (getFocusedObject() != null) {
			newFolder = getFocusedObject().getResourceRepository().createNewFolder(getNewFolderName(), getFocusedObject());
		} else {
			throw new InvalidParametersException("unable to create view folder: no focused object supplied");
		}
	}

	public String getNewFolderName() {
		return newFolderName;
	}

	public void setNewFolderName(String newFolderName) {
		this.newFolderName = newFolderName;
	}

	public RepositoryFolder getNewFolder() {
		return newFolder;
	}

}
