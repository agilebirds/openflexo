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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.FlexoWorkflow;
import org.openflexo.foundation.wkf.ProcessFolder;

public class AddSubProcess extends FlexoAction<AddSubProcess, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(AddSubProcess.class.getPackage().getName());

	public static FlexoActionType<AddSubProcess, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<AddSubProcess, FlexoModelObject, FlexoModelObject>(
			"add_new_sub_process", FlexoActionType.newMenu, FlexoActionType.newMenuGroup1, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddSubProcess makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new AddSubProcess(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof FlexoProcess && !((FlexoProcess) object).isImported() || object instanceof ProcessFolder
					&& !((ProcessFolder) object).getProcessNode().isImported() || object instanceof FlexoWorkflow;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return isVisibleForSelection(object, globalSelection);
		}

	};

	private String _newProcessName;
	private FlexoProcess _parentProcess;
	private FlexoProcess _newProcess;
	private FlexoProject _project;

	static {
		FlexoModelObject.addActionForClass(AddSubProcess.actionType, FlexoWorkflow.class);
		FlexoModelObject.addActionForClass(AddSubProcess.actionType, FlexoProcess.class);
		FlexoModelObject.addActionForClass(AddSubProcess.actionType, ProcessFolder.class);
	}

	AddSubProcess(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public void setParentProcess(FlexoProcess parentProcess) {
		_parentProcess = parentProcess;
	}

	public FlexoProcess getParentProcess() {
		return _parentProcess;
	}

	public String getNewProcessName() {
		return _newProcessName;
	}

	public void setNewProcessName(String newProcessName) {
		_newProcessName = newProcessName;
	}

	@Override
	protected void doAction(Object context) throws DuplicateResourceException, InvalidFileNameException {
		logger.info("Add sub-process");
		if (getNewProcessName() == null || getNewProcessName().trim().length() == 0) {
			throw new InvalidFileNameException("");
		}
		if (getFocusedObject() != null) {
			_project = getFocusedObject().getProject();
			_newProcess = FlexoProcess.createNewProcess(_project.getFlexoWorkflow(), getParentProcess(), getNewProcessName(),
					getParentProcess() == null && _project.getFlexoWorkflow().getRootProcess() == null);
		} else {
			logger.warning("Focused object is null !");
		}
	}

	public FlexoProcess getNewProcess() {
		return _newProcess;
	}

}
