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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;

public class DMCut extends FlexoUndoableAction {

	private static final Logger logger = Logger.getLogger(DMCut.class.getPackage().getName());

	public static FlexoActionType actionType = new FlexoActionType("cut", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public FlexoAction makeNewAction(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
			return new DMCut(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector globalSelection) {
			return false;
		}

	};

	DMCut(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Not yet implemented
		logger.info("CUT on DM");
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO CUT on DM not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO CUT on DM not implemented yet !");
	}

}
