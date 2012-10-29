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
package org.openflexo.foundation.dm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.dm.DMObject;

public class DMPaste extends FlexoUndoableAction<DMPaste, DMObject, DMObject> {

	private static final Logger logger = Logger.getLogger(DMPaste.class.getPackage().getName());

	public static FlexoActionType<DMPaste, DMObject, DMObject> actionType = new FlexoActionType<DMPaste, DMObject, DMObject>("paste",
			FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public DMPaste makeNewAction(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
			return new DMPaste(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(DMObject object, Vector<DMObject> globalSelection) {
			return false;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, DMObject.class);
	}

	protected DMPaste(DMObject focusedObject, Vector<DMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Not yet implemented in Foundation, but in WKF module
		logger.info("PASTE on DM");
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO PASTE on DM not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO PASTE on DM not implemented yet !");
	}

}
