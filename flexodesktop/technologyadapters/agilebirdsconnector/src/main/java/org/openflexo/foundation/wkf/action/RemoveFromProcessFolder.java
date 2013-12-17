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
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.UndoException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.InvalidParentProcessException;

public class RemoveFromProcessFolder extends FlexoAction<RemoveFromProcessFolder, AgileBirdsObject, AgileBirdsObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(RemoveFromProcessFolder.class.getPackage().getName());

	private FlexoProcessNode getProcessNode() {
		return getProcessNode(getFocusedObject());
	}

	static FlexoProcessNode getProcessNode(AgileBirdsObject object) {
		if (object instanceof FlexoProcessNode) {
			return (FlexoProcessNode) object;
		} else if (object instanceof FlexoProcess) {
			return ((FlexoProcess) object).getProcessNode();
		}
		return null;
	}

	public static FlexoActionType<RemoveFromProcessFolder, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<RemoveFromProcessFolder, AgileBirdsObject, AgileBirdsObject>(
			"remove_from_process_folder", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public RemoveFromProcessFolder makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection,
				FlexoEditor editor) {
			return new RemoveFromProcessFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {

			return getProcessNode(object) != null && getProcessNode(object).getParentFolder() != null;
		}

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	RemoveFromProcessFolder(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		AgileBirdsObject.addActionForClass(actionType, FlexoProcessNode.class);
		AgileBirdsObject.addActionForClass(actionType, FlexoProcess.class);
	}

	@Override
	protected void doAction(Object context) throws InvalidParentProcessException, UndoException {
		if (getProcessNode().getParentFolder() != null) {
			getProcessNode().getParentFolder().removeFromProcesses(getProcessNode());
		}
	}

}
