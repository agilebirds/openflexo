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
package org.openflexo.foundation.cg.action;

import java.util.Vector;

import org.openflexo.foundation.DocType;
import org.openflexo.foundation.DocType.DefaultDocType;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

/**
 * @author gpolet
 * 
 */
public class RemoveDocType extends FlexoAction<RemoveDocType, DocType, DocType> {

	public static final FlexoActionType<RemoveDocType, DocType, DocType> actionType = new FlexoActionType<RemoveDocType, DocType, DocType>(
			"remove_doc_type", FlexoActionType.DELETE_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(DocType object, Vector<DocType> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

		@Override
		public boolean isVisibleForSelection(DocType object, Vector<DocType> globalSelection) {
			Vector<FlexoObject> v = getGlobalSelectionAndFocusedObject(object, globalSelection);
			boolean ok = v.size() > 0;
			for (FlexoObject o : v) {
				for (DefaultDocType defaultDocType : DefaultDocType.values()) {
					ok &= (o instanceof FlexoModelObject && o != ((FlexoModelObject) o).getProject().getDocTypeNamed(defaultDocType.name()));
				}
			}
			return ok;
		}

		@Override
		public RemoveDocType makeNewAction(DocType focusedObject, Vector<DocType> globalSelection, FlexoEditor editor) {
			return new RemoveDocType(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(RemoveDocType.actionType, DocType.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 */
	protected RemoveDocType(DocType focusedObject, Vector<DocType> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	/**
	 * Overrides doAction
	 * 
	 * @see org.openflexo.foundation.action.FlexoAction#doAction(java.lang.Object)
	 */
	@Override
	protected void doAction(Object context) throws FlexoException {
		DocType dt = getFocusedObject();
		dt.getProject().removeFromDocTypes(dt);
	}

}
