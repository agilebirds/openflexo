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

import org.openflexo.drm.DocItem;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class AddToEmbeddingChildItem extends FlexoAction {

	public static FlexoActionType actionType = new FlexoActionType("add_embedding_child", FlexoActionType.newMenu,
			FlexoActionType.defaultGroup, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new AddToEmbeddingChildItem(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof DocItem;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(actionType, DocItem.class);
	}

	private DocItem _parentDocItem;
	private DocItem _childDocItem;

	AddToEmbeddingChildItem(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getParentDocItem() != null && getChildDocItem() != null) {
			getParentDocItem().addToEmbeddingChildItems(getChildDocItem());
		}
	}

	public DocItem getChildDocItem() {
		return _childDocItem;
	}

	public void setChildDocItem(DocItem childDocItem) {
		_childDocItem = childDocItem;
	}

	public void setParentDocItem(DocItem parentDocItem) {
		_parentDocItem = parentDocItem;
	}

	public DocItem getParentDocItem() {
		if (_parentDocItem == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DocItem) {
				_parentDocItem = (DocItem) getFocusedObject();
			}
		}
		return _parentDocItem;
	}

}
