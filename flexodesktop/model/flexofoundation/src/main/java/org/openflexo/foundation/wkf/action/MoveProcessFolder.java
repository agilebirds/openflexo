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

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.UndoException;
import org.openflexo.foundation.wkf.FlexoFolderContainerNode;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoProcessNode;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.ProcessFolder;

public class MoveProcessFolder extends FlexoAction<MoveProcessFolder, ProcessFolder, ProcessFolder> {

	private static final Logger logger = Logger.getLogger(MoveProcessFolder.class.getPackage().getName());

	public static FlexoActionType<MoveProcessFolder, ProcessFolder, ProcessFolder> actionType = new FlexoActionType<MoveProcessFolder, ProcessFolder, ProcessFolder>(
			"move_process_folder", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public MoveProcessFolder makeNewAction(ProcessFolder focusedObject, Vector<ProcessFolder> globalSelection, FlexoEditor editor) {
			return new MoveProcessFolder(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(ProcessFolder object, Vector<ProcessFolder> globalSelection) {
			return false;
		}

		@Override
		protected boolean isEnabledForSelection(ProcessFolder object, Vector<ProcessFolder> globalSelection) {
			return object != null;
		}

	};

	MoveProcessFolder(ProcessFolder focusedObject, Vector<ProcessFolder> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	static {
		FlexoModelObject.addActionForClass(actionType, ProcessFolder.class);
	}

	private FlexoFolderContainerNode destination;

	@Override
	protected void doAction(Object context) throws InvalidParentProcessException, UndoException {
		if (getDestination() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Destination is null! Returning now");
			}
			return;
		}
		if (getDestination().isImported()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Cannot move a folder to an imported object");
			}
			return;
		}
		if (getDestination().getProcessNode() == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Destination has no parent process node! Returning now");
			}
			return;
		}
		if (getDestination() instanceof ProcessFolder) {
			if (!getFocusedObject().isAcceptableParentFolder((ProcessFolder) getDestination())) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Destination folder is not acceptable");
				}
				return;
			}
		}
		FlexoProcessNode currentParent = getFocusedObject().getProcessNode();
		FlexoProcess targetProcess = getDestination().getProcessNode().getProcess();

		if (targetProcess.getProcessNode() != currentParent) {
			for (FlexoProcessNode node : getFocusedObject().getAllDirectSubProcessNodes()) {
				if (!targetProcess.isAcceptableAsParentProcess(node.getProcess())) {
					throw new InvalidParentProcessException(node.getProcess(), targetProcess);
				}
			}
			getFocusedObject().setParent(null);
			boolean success = true;
			Vector<MoveFlexoProcess> moves = new Vector<MoveFlexoProcess>();
			for (Iterator<FlexoProcessNode> i = getFocusedObject().getAllDirectSubProcessNodes().iterator(); i.hasNext();) {
				FlexoProcessNode node = i.next();
				node.startMoving();
				try {
					MoveFlexoProcess move = MoveFlexoProcess.actionType.makeNewEmbeddedAction(node.getProcess(), null, this);
					move.setNewParentProcess(targetProcess);
					move.setDoImmediately(true);
					move.setPerformValidate(!i.hasNext());
					move.doAction();
					if (move.hasActionExecutionSucceeded()) {
						moves.add(move);
					} else {
						success = false;
						break;
					}
				} finally {
					node.stopMoving();
				}
			}
			if (!success) {
				for (MoveFlexoProcess move : moves) {
					move.undoAction();
				}
				getFocusedObject().setParent(currentParent);
				throw new InvalidParentProcessException(null, targetProcess);
			}
		}
		getDestination().addToFolders(getFocusedObject());
	}

	public void setDestination(FlexoFolderContainerNode destination) {
		this.destination = destination;
	}

	public FlexoFolderContainerNode getDestination() {
		return destination;
	}

}
