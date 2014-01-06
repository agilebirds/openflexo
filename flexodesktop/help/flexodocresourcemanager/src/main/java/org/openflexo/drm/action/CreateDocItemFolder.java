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
package org.openflexo.drm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class CreateDocItemFolder extends FlexoAction {

	private static final Logger logger = Logger.getLogger(CreateDocItemFolder.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("create_new_folder", FlexoActionType.newMenu,
			FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new CreateDocItemFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof DocItemFolder;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, DocItemFolder.class);
	}

	private DocItemFolder _parentDocItemFolder;
	private String _newItemFolderIdentifier;
	private String _newItemFolderDescription;
	private DocItemFolder _newDocItemFolder;

	CreateDocItemFolder(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("CreateDocItemFolder");
		if (getParentDocItemFolder() != null) {
			_newDocItemFolder = DocItemFolder.createDocItemFolder(getNewItemFolderIdentifier(), getNewItemFolderDescription(),
					getParentDocItemFolder(), getParentDocItemFolder().getDocResourceCenter());

		}
	}

	public String getNewItemFolderIdentifier() {
		return _newItemFolderIdentifier;
	}

	public void setNewItemFolderIdentifier(String anIdentifier) {
		_newItemFolderIdentifier = anIdentifier;
	}

	public String getNewItemFolderDescription() {
		return _newItemFolderDescription;
	}

	public void setNewItemFolderDescription(String newItemDescription) {
		_newItemFolderDescription = newItemDescription;
	}

	public DocItemFolder getParentDocItemFolder() {
		if (_parentDocItemFolder == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DocItemFolder) {
				_parentDocItemFolder = (DocItemFolder) getFocusedObject();
			}
		}
		return _parentDocItemFolder;
	}

	public DocItemFolder getNewDocItemFolder() {
		return _newDocItemFolder;
	}

}
