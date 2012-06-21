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
package org.openflexo.foundation.view.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.view.ViewFolder;

public class MoveViewFolder extends FlexoAction<MoveViewFolder, ViewFolder, ViewFolder> {

	private static final Logger logger = Logger.getLogger(MoveViewFolder.class.getPackage().getName());

	public static final FlexoActionType<MoveViewFolder, ViewFolder, ViewFolder> actionType = new FlexoActionType<MoveViewFolder, ViewFolder, ViewFolder>(
			"move_view_folder") {

		@Override
		public boolean isEnabledForSelection(ViewFolder object, Vector<ViewFolder> globalSelection) {
			return true;
		}

		@Override
		public boolean isVisibleForSelection(ViewFolder object, Vector<ViewFolder> globalSelection) {
			return false;
		}

		@Override
		public MoveViewFolder makeNewAction(ViewFolder focusedObject, Vector<ViewFolder> globalSelection, FlexoEditor editor) {
			return new MoveViewFolder(focusedObject, globalSelection, editor);
		}

	};

	private ViewFolder folder;

	protected MoveViewFolder(ViewFolder focusedObject, Vector<ViewFolder> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		if (getFolder() == null) {
			logger.warning("Cannot move: null folder");
			return;
		}
		for (ViewFolder v : getGlobalSelection()) {
			if (isFolderMovableTo(v, folder)) {
				moveToFolder(v, folder);
			}
		}
	}

	private void moveToFolder(ViewFolder folderToMove, ViewFolder newFatherFolder) {
		if (isFolderMovableTo(folderToMove, newFatherFolder)) {
			ViewFolder oldFolder = folderToMove.getFatherFolder();
			// Hack: we have first to load the view, to prevent a null value returned by FlexoOEShemaResource.getSchemaDefinition()
			oldFolder.removeFromSubFolders(folderToMove);
			newFatherFolder.addToSubFolders(folderToMove);
		}
	}

	public ViewFolder getFolder() {
		return folder;
	};

	public void setFolder(ViewFolder folder) {
		this.folder = folder;
	}

	public static boolean isFolderMovableTo(ViewFolder folderToMove, ViewFolder newLocation) {
		return folderToMove != newLocation && !folderToMove.isFatherOf(newLocation);
	}
}
