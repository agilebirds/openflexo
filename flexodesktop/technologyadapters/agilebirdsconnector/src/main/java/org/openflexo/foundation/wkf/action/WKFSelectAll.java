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
package org.openflexo.foundation.wkf.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.WKFObject;

public class WKFSelectAll extends FlexoGUIAction<WKFSelectAll, AgileBirdsObject, AgileBirdsObject> {

	public static FlexoActionType<WKFSelectAll, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<WKFSelectAll, AgileBirdsObject, AgileBirdsObject>(
			"select_all", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public WKFSelectAll makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
			return new WKFSelectAll(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return true;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, WKFObject.class);
	}

	WKFSelectAll(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
