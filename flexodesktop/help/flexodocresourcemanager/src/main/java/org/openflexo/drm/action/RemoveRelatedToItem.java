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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class RemoveRelatedToItem extends FlexoAction {

	public static FlexoActionType actionType = new FlexoActionType("remove_related_to_item", FlexoActionType.defaultGroup,
			FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new RemoveRelatedToItem(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector globalSelection) {
			return object != null && object instanceof DocItem && globalSelection.size() > 0
					&& globalSelection.firstElement() instanceof DocItem;
		}

	};

	private DocItem _docItemToRemove;

	RemoveRelatedToItem(FlexoObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		if (getOppositeDocItem() != null && getDocItemToRemove() != null) {
			getOppositeDocItem().removeFromRelatedToItems(getDocItemToRemove());
		}
	}

	public void setDocItemToRemove(DocItem docItemToRemove) {
		_docItemToRemove = docItemToRemove;
	}

	public DocItem getDocItemToRemove() {
		if (_docItemToRemove == null) {
			if (getFocusedObject() != null && getFocusedObject() instanceof DocItem) {
				_docItemToRemove = (DocItem) getFocusedObject();
			}
		}
		return _docItemToRemove;
	}

	public DocItem getOppositeDocItem() {
		if (getGlobalSelection().size() > 0 && getGlobalSelection().firstElement() instanceof DocItem) {
			return (DocItem) getGlobalSelection().firstElement();
		}
		return null;
	}

}
