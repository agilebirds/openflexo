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
import org.openflexo.foundation.wkf.ProcessFolder;

public class AddToProcessFolder extends FlexoAction<AddToProcessFolder, AgileBirdsObject, AgileBirdsObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(AddToProcessFolder.class.getPackage().getName());

	public static FlexoActionType<AddToProcessFolder, AgileBirdsObject, AgileBirdsObject> actionType = new FlexoActionType<AddToProcessFolder, AgileBirdsObject, AgileBirdsObject>(
			"add_to_process_folder", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public AddToProcessFolder makeNewAction(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
			return new AddToProcessFolder(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(AgileBirdsObject object, Vector<AgileBirdsObject> globalSelection) {
			return getProcessNode(object) != null;
		}

	};

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

	AddToProcessFolder(AgileBirdsObject focusedObject, Vector<AgileBirdsObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		AgileBirdsObject.addActionForClass(actionType, FlexoProcessNode.class);
		AgileBirdsObject.addActionForClass(actionType, FlexoProcess.class);
	}

	private ProcessFolder destination;

	@Override
	protected void doAction(Object context) throws InvalidParentProcessException, UndoException {
		if (getDestination() != null) {
			getDestination().addToProcesses(getProcessNode());
		}
	}

	public void setDestination(ProcessFolder destination) {
		this.destination = destination;
	}

	public ProcessFolder getDestination() {
		return destination;
	}

}
