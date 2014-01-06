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
package org.openflexo.action;

import java.util.Vector;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoObject.FlexoObjectImpl;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;

public class HelpAction extends FlexoGUIAction<HelpAction, FlexoObject, FlexoObject> {

	public static FlexoActionType<HelpAction, FlexoObject, FlexoObject> actionType = new FlexoActionType<HelpAction, FlexoObject, FlexoObject>(
			"help", FlexoActionType.helpGroup) {

		/**
		 * Factory method
		 */
		@Override
		public HelpAction makeNewAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
			return new HelpAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoObject object, Vector<FlexoObject> globalSelection) {
			return object instanceof InspectableObject;
		}

	};

	static {
		FlexoObjectImpl.addActionForClass(HelpAction.actionType, FlexoObject.class);
	}

	HelpAction(FlexoObject focusedObject, Vector<FlexoObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	public String getLocalizedName() {
		if (getFocusedObject() != null) {
			/* String shortClassName = null;
			 String extClassName = getFocusedObject().getClass().getName();
			 StringTokenizer st = new StringTokenizer(extClassName,".");
			 while (st.hasMoreTokens()) shortClassName = st.nextToken();*/
			return FlexoLocalization.localizedForKey("help_on") + " "
					+ FlexoLocalization.localizedForKey(getFocusedObject().getClass().getName());
		}
		return null;
	}
}
