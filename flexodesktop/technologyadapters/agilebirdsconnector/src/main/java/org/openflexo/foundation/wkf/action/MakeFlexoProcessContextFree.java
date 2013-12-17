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
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoUndoableAction;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;

public class MakeFlexoProcessContextFree extends FlexoUndoableAction<MakeFlexoProcessContextFree, FlexoProcess, WKFObject> {

	private static final Logger logger = Logger.getLogger(MakeFlexoProcessContextFree.class.getPackage().getName());

	public static FlexoActionType<MakeFlexoProcessContextFree, FlexoProcess, WKFObject> actionType = new FlexoActionType<MakeFlexoProcessContextFree, FlexoProcess, WKFObject>(
			"make_context_free", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public MakeFlexoProcessContextFree makeNewAction(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new MakeFlexoProcessContextFree(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) {
			return object != null && object.getParentProcess() != null;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(MakeFlexoProcessContextFree.actionType, FlexoProcess.class);
	}

	private SetPropertyAction _setParentProcessAction;

	MakeFlexoProcessContextFree(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		_setParentProcessAction = SetPropertyAction.actionType.makeNewEmbeddedAction(getFocusedObject(), null, this);
		_setParentProcessAction.setKey("parentProcess");
		_setParentProcessAction.setValue(null);
		_setParentProcessAction.doAction();
	}

	@Override
	protected void undoAction(Object context) throws FlexoException {
		_setParentProcessAction.undoAction();
	}

	@Override
	protected void redoAction(Object context) throws FlexoException {
		_setParentProcessAction.redoAction();
	}

}