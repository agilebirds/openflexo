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
package org.openflexo.foundation.ie.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.ie.IEObject;

public class IESelectAll extends FlexoGUIAction<IESelectAll, IEObject, IEObject> {

	public static FlexoActionType<IESelectAll, IEObject, IEObject> actionType = new FlexoActionType<IESelectAll, IEObject, IEObject>(
			"select_all", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public IESelectAll makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new IESelectAll(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

	};

	IESelectAll(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
