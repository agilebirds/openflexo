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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.ViewDefinition;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibraryObject;

public class DeleteViewFolder extends FlexoAction<DeleteViewFolder, ViewFolder, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(DeleteViewFolder.class.getPackage().getName());

	public static FlexoActionType<DeleteViewFolder, ViewFolder, ViewLibraryObject> actionType = new FlexoActionType<DeleteViewFolder, ViewFolder, ViewLibraryObject>(
			"delete_view_folder", FlexoActionType.editGroup, FlexoActionType.DELETE_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public DeleteViewFolder makeNewAction(ViewFolder focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
			return new DeleteViewFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewFolder view, Vector<ViewLibraryObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewFolder view, Vector<ViewLibraryObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(DeleteViewFolder.actionType, ViewFolder.class);
	}

	DeleteViewFolder(ViewFolder focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Delete view folder");

		for (ViewDefinition v : getFocusedObject().getAllViews()) {
			v.getView().delete();
		}

		getFocusedObject().delete();
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

}