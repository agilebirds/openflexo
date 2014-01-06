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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.RepositoryFolder;

public class MoveRepositoryFolder extends FlexoAction<MoveRepositoryFolder, RepositoryFolder, RepositoryFolder> {

	private static final Logger logger = Logger.getLogger(MoveRepositoryFolder.class.getPackage().getName());

	public static final FlexoActionType<MoveRepositoryFolder, RepositoryFolder, RepositoryFolder> actionType = new FlexoActionType<MoveRepositoryFolder, RepositoryFolder, RepositoryFolder>(
			"move_folder") {

		@Override
		public boolean isEnabledForSelection(RepositoryFolder object, Vector<RepositoryFolder> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(RepositoryFolder object, Vector<RepositoryFolder> globalSelection) {
			return false;
		}

		@Override
		public MoveRepositoryFolder makeNewAction(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection,
				FlexoEditor editor) {
			return new MoveRepositoryFolder(focusedObject, globalSelection, editor);
		}

	};

	private RepositoryFolder folder;

	protected MoveRepositoryFolder(RepositoryFolder focusedObject, Vector<RepositoryFolder> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFolder() == null) {
			logger.warning("Cannot move: null folder");
			return;
		}
		for (RepositoryFolder v : getGlobalSelection()) {
			if (isFolderMovableTo(v, folder)) {
				moveToFolder(v, folder);
			}
		}
	}

	private void moveToFolder(RepositoryFolder folderToMove, RepositoryFolder newFatherFolder) {
		if (isFolderMovableTo(folderToMove, newFatherFolder)) {
			RepositoryFolder oldFolder = folderToMove.getParentFolder();
			// Hack: we have first to load the view, to prevent a null value returned by FlexoViewResource.getSchemaDefinition()
			oldFolder.removeFromChildren(folderToMove);
			newFatherFolder.addToChildren(folderToMove);
		}
	}

	public RepositoryFolder getFolder() {
		return folder;
	};

	public void setFolder(RepositoryFolder folder) {
		this.folder = folder;
	}

	public static boolean isFolderMovableTo(RepositoryFolder folderToMove, RepositoryFolder newLocation) {
		return folderToMove != newLocation && !folderToMove.isFatherOf(newLocation);
	}
}
