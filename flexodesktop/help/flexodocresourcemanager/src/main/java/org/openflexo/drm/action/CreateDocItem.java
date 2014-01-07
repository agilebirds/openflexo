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

import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class CreateDocItem extends FlexoAction {

	private static final Logger logger = Logger.getLogger(CreateDocItem.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("create_new_item", FlexoActionType.newMenu,
			FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new CreateDocItem(focusedObject, globalSelection, editor);
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

	private DocItemFolder _docItemFolder;
	private String _newItemIdentifier;
	private String _newItemDescription;
	private DocItem _newDocItem;

	CreateDocItem(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("CreateDocItem");
		if (getDocItemFolder() != null) {
			_newDocItem = DocItem.createDocItem(getNewItemIdentifier(), getNewItemDescription(), getDocItemFolder(), false);

		}
	}

	public String getNewItemIdentifier() {
		return _newItemIdentifier;
	}

	public void setNewItemIdentifier(String anIdentifier) {
		_newItemIdentifier = anIdentifier;
	}

	public String getNewItemDescription() {
		return _newItemDescription;
	}

	public void setNewItemDescription(String newItemDescription) {
		_newItemDescription = newItemDescription;
	}

	public DocItemFolder getDocItemFolder() {
		if (_docItemFolder == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DocItemFolder) {
				_docItemFolder = (DocItemFolder) getFocusedObject();
			}
		}
		return _docItemFolder;
	}

	public DocItem getNewDocItem() {
		return _newDocItem;
	}

}
