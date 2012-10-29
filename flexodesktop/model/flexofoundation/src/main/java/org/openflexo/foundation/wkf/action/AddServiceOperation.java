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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.FlexoPort;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceOperation;

public class AddServiceOperation extends FlexoAction<AddServiceOperation, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(AddServiceOperation.class.getPackage().getName());

	public static FlexoActionType<AddServiceOperation, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<AddServiceOperation, FlexoModelObject, FlexoModelObject>(
			"add_new_service_operation", FlexoActionType.newMenu, FlexoActionType.newMenuGroup1, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddServiceOperation makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new AddServiceOperation(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof ServiceInterface;
		}

	};

	static {
		FlexoModelObject.addActionForClass(actionType, ServiceInterface.class);
	}
	private String _newOperationName;

	private FlexoPort _relatedPort;

	private ServiceInterface _serviceInterface;

	private ServiceOperation _newServiceOperation;

	AddServiceOperation(FlexoModelObject focusedObject, Vector globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcess() {
		if (getFocusedObject() != null && getFocusedObject() instanceof ServiceInterface) {
			return ((WKFObject) getFocusedObject()).getProcess();
		}
		return null;
	}

	public String getNewOperationName() {
		return _newOperationName;
	}

	public void setNewOperationName(String newOperationName) {
		_newOperationName = newOperationName;
	}

	public FlexoPort getRelatedPort() {
		return _relatedPort;
	}

	public void setRelatedPort(FlexoPort relatedPort) {
		_relatedPort = relatedPort;
	}

	public ServiceInterface getServiceInterface() {
		// soit focussed object or specified
		if (_serviceInterface != null) {
			return _serviceInterface;
		}
		if (getFocusedObject() != null && getFocusedObject() instanceof ServiceInterface) {
			return (ServiceInterface) getFocusedObject();
		}
		return null;
	}

	public void setServiceInterface(ServiceInterface serviceInterface) {
		_serviceInterface = serviceInterface;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		logger.info("Add Service Operation");
		if (getProcess() != null && !getProcess().isImported()) {
			if (getServiceInterface() != null && getRelatedPort() != null) {
				_newServiceOperation = getServiceInterface().addServiceOperation(getNewOperationName(), getRelatedPort());
			} else {
				logger.warning("Invalid operation parameters!");
				return;
			}
		} else {
			logger.warning("Focused process is null or imported!");
		}
	}

	public ServiceOperation getNewServiceOperation() {
		return _newServiceOperation;
	}

}
