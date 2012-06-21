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
import org.openflexo.foundation.action.InvalidParametersException;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewLibraryObject;

public class AddViewFolder extends FlexoAction<AddViewFolder, ViewLibraryObject, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(AddViewFolder.class.getPackage().getName());

	public static FlexoActionType<AddViewFolder, ViewLibraryObject, ViewLibraryObject> actionType = new FlexoActionType<AddViewFolder, ViewLibraryObject, ViewLibraryObject>(
			"create_view_folder", FlexoActionType.newMenu, FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddViewFolder makeNewAction(ViewLibraryObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
			return new AddViewFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ViewLibraryObject object, Vector<ViewLibraryObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ViewLibraryObject object, Vector<ViewLibraryObject> globalSelection) {
			return ((object != null) && ((object instanceof ViewFolder) || (object instanceof ViewLibrary)));
		}

	};

	static {
		FlexoModelObject.addActionForClass(AddViewFolder.actionType, ViewLibrary.class);
		FlexoModelObject.addActionForClass(AddViewFolder.actionType, ViewFolder.class);
	}

	private ViewFolder _newFolder;
	private ViewFolder _parentFolder;
	private String _newFolderName;

	AddViewFolder(ViewLibraryObject focusedObject, Vector<ViewLibraryObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws InvalidParametersException {
		logger.info("Add shema folder");
		if (getFocusedObject() != null) {
			if (getParentFolder() != null) {
				_newFolder = ViewFolder.createNewFolder(getParentFolder().getShemaLibrary(), getParentFolder(), getNewFolderName());
			} else {
				if (!getFocusedObject().getProject().getFlexoComponentLibrary().hasRootFolder()) {
					_parentFolder = ViewFolder.createNewRootFolder(getFocusedObject().getProject().getShemaLibrary());
				}
				if (getFocusedObject() instanceof ViewFolder) {
					_parentFolder = (ViewFolder) getFocusedObject();
				} else {
					_parentFolder = getFocusedObject().getProject().getShemaLibrary().getRootFolder();
				}
				_newFolder = ViewFolder.createNewFolder(getParentFolder().getShemaLibrary(), getParentFolder(), getNewFolderName());
			}
		} else {
			throw new InvalidParametersException("unable to create shema folder: no focused object supplied");
		}
	}

	public String getNewFolderName() {
		return _newFolderName;
	}

	public void setNewFolderName(String newFolderName) {
		_newFolderName = newFolderName;
	}

	public ViewFolder getParentFolder() {
		return _parentFolder;
	}

	public void setParentFolder(ViewFolder parentFolder) {
		_parentFolder = parentFolder;
	}

	public ViewFolder getNewFolder() {
		return _newFolder;
	}

}
