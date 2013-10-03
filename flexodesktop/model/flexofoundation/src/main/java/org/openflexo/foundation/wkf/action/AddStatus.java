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
import org.openflexo.foundation.wkf.DuplicateStatusException;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.Status;
import org.openflexo.foundation.wkf.StatusList;
import org.openflexo.foundation.wkf.WKFObject;

public class AddStatus extends FlexoAction<AddStatus, WKFObject, WKFObject> {

	private static final Logger logger = Logger.getLogger(AddStatus.class.getPackage().getName());

	public static FlexoActionType<AddStatus, WKFObject, WKFObject> actionType = new FlexoActionType<AddStatus, WKFObject, WKFObject>(
			"add_new_status", FlexoActionType.newMenu, FlexoActionType.newMenuGroup1, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddStatus makeNewAction(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new AddStatus(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return false;
		}

		@Override
		public boolean isEnabledForSelection(WKFObject object, Vector<WKFObject> globalSelection) {
			return object != null && (object instanceof FlexoProcess || object instanceof StatusList || object instanceof Status);
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, StatusList.class);
		FlexoModelObject.addActionForClass(actionType, Status.class);
		FlexoModelObject.addActionForClass(actionType, FlexoProcess.class);
	}

	private String _newStatusName;
	private String _newDescription;
	private Status _newStatus;

	AddStatus(WKFObject focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcess() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProcess();
		}
		return null;
	}

	public String getNewStatusName() {
		return _newStatusName;
	}

	public void setNewStatusName(String newStatusName) {
		_newStatusName = newStatusName;
	}

	@Override
	protected void doAction(Object context) throws DuplicateStatusException {
		logger.info("Add status");
		if (getProcess() != null && getProcess().getStatusList() != null) {
			StatusList statusList = getProcess().getStatusList();
			statusList.addToStatus(_newStatus = new Status(getProcess(), getNewStatusName()));
			if (getNewDescription() != null) {
				_newStatus.setDescription(getNewDescription());
			}
		} else {
			logger.warning("Focused process is null or imported!");
		}
	}

	public Status getNewStatus() {
		return _newStatus;
	}

	public String getNewDescription() {
		return _newDescription;
	}

	public void setNewDescription(String newDescription) {
		_newDescription = newDescription;
	}

}
