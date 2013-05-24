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
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.wkf.FlexoPetriGraph;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFGroup;
import org.openflexo.foundation.wkf.WKFObject;

public class WKFCut extends FlexoUndoableAction<WKFCut, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(WKFCut.class.getPackage().getName());

	public static FlexoActionType<WKFCut, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<WKFCut, FlexoModelObject, FlexoModelObject>(
			"cut", FlexoActionType.editGroup) {

		/**
		 * Factory method
		 */
		@Override
		public WKFCut makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new WKFCut(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return !(object instanceof FlexoPetriGraph) && !(object instanceof FlexoProcess) && !(object instanceof WKFGroup);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, WKFObject.class);
	}

	WKFCut(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		// Not yet implemented in Foundation, but in WKF module
		logger.info("CUT on WKF");
	}

	@Override
	protected void undoAction(Object context) {
		logger.warning("UNDO CUT on WKF not implemented yet !");
	}

	@Override
	protected void redoAction(Object context) {
		logger.warning("REDO CUT on WKF not implemented yet !");
	}

}
