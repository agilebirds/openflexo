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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

public class InspectAction extends FlexoGUIAction<InspectAction, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = FlexoLogger.getLogger(InspectAction.class.getPackage().getName());

	public static FlexoActionType<InspectAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<InspectAction, FlexoModelObject, FlexoModelObject>(
			"inspect", FlexoActionType.inspectGroup) {

		/**
		 * Factory method
		 */
		@Override
		public InspectAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new InspectAction(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return (object instanceof InspectableObject);
		}

	};

	static {
		FlexoModelObject.addActionForClass(InspectAction.actionType, FlexoModelObject.class);
	}

	InspectAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		// logger.info("InspectAction with "+editor);
	}

}
