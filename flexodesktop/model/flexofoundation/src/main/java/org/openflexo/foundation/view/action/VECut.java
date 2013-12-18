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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.view.VirtualModelInstanceObject;

public class VECut extends FlexoUndoableAction<VECut, VirtualModelInstanceObject, VirtualModelInstanceObject> {

	private static final Logger logger = Logger.getLogger(VECut.class.getPackage().getName());

	public static FlexoActionType<VECut, VirtualModelInstanceObject, VirtualModelInstanceObject> actionType = new FlexoActionType<VECut, VirtualModelInstanceObject, VirtualModelInstanceObject>(
			"cut", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public VECut makeNewAction(VirtualModelInstanceObject focusedObject, Vector<VirtualModelInstanceObject> globalSelection,
				FlexoEditor editor) {
			return new VECut(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(VirtualModelInstanceObject object, Vector<VirtualModelInstanceObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(VirtualModelInstanceObject object, Vector<VirtualModelInstanceObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoObject.addActionForClass(VECut.actionType, EditionPatternInstance.class);
	}

	VECut(VirtualModelInstanceObject focusedObject, Vector<VirtualModelInstanceObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Not yet implemented in Foundation, but in WKF module
		logger.info("CUT on VE");
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO CUT on VE not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO CUT on VE not implemented yet !");
	}

}
