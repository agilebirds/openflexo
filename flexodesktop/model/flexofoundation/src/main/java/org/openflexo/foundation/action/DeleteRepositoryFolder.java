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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.RepositoryFolder;

public class DeleteRepositoryFolder extends FlexoAction<DeleteRepositoryFolder, RepositoryFolder, RepositoryFolder> {

	private static final Logger logger = Logger.getLogger(DeleteRepositoryFolder.class.getPackage().getName());

	public static FlexoActionType<DeleteRepositoryFolder, RepositoryFolder, RepositoryFolder> actionType = new FlexoActionType<DeleteRepositoryFolder, RepositoryFolder, RepositoryFolder>(
			"delete_folder", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteRepositoryFolder makeNewAction(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection,
				FlexoEditor editor) {
			return new DeleteRepositoryFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder folder, Vector<RepositoryFolder> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(RepositoryFolder folder, Vector<RepositoryFolder> globalSelection) {
			return (folder.getResources().size() == 0);
		}

	};

	static {
		FlexoObject.addActionForClass(DeleteRepositoryFolder.actionType, RepositoryFolder.class);
	}

	DeleteRepositoryFolder(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Delete view folder");

		if (getFocusedObject() != null) {
			getFocusedObject().getResourceRepository().deleteFolder(getFocusedObject());
		} else {
			throw new InvalidParametersException("unable to create view folder: no focused object supplied");
		}
	}

}