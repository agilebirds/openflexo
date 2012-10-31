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
import org.openflexo.foundation.view.ViewObject;

public class VECopy extends FlexoAction<VECopy, ViewObject, ViewObject> {

	private static final Logger logger = Logger.getLogger(VECopy.class.getPackage().getName());

	public static FlexoActionType<VECopy, ViewObject, ViewObject> actionType = new FlexoActionType<VECopy, ViewObject, ViewObject>("copy",
			FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public VECopy makeNewAction(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
			return new VECopy(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ViewObject object, Vector<ViewObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		protected boolean isEnabledForSelection(ViewObject object, Vector<ViewObject> globalSelection) {

			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(VECopy.actionType, ViewObject.class);
	}

	VECopy(ViewObject focusedObject, Vector<ViewObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Implemented in WKF module
		logger.info("COPY on VE");
	}

}
