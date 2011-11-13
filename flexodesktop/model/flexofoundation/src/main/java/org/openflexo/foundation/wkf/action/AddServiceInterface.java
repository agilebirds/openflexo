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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.foundation.wkf.ws.ServiceInterface;

public class AddServiceInterface extends FlexoAction<AddServiceInterface, FlexoModelObject, FlexoModelObject> {

	private static final Logger logger = Logger.getLogger(AddServiceInterface.class.getPackage().getName());

	public static FlexoActionType<AddServiceInterface, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<AddServiceInterface, FlexoModelObject, FlexoModelObject>(
			"add_new_service_interface", FlexoActionType.newMenu, FlexoActionType.newMenuGroup4, FlexoActionType.ADD_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public AddServiceInterface makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new AddServiceInterface(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof FlexoProcess && !((FlexoProcess) object).isImported();
		}

		@Override
		protected boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return object instanceof WKFObject && !((WKFObject) object).getProcess().isImported();
		}

	};

	private String _newInterfaceName;
	private ServiceInterface _serviceInterface;

	AddServiceInterface(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProcess getProcess() {
		if ((getFocusedObject() != null) && (getFocusedObject() instanceof WKFObject)) {
			return ((WKFObject) getFocusedObject()).getProcess();
		}
		return null;
	}

	public String getNewInterfaceName() {
		return _newInterfaceName;
	}

	public void setNewInterfaceName(String name) {
		_newInterfaceName = name;
	}

	public ServiceInterface getServiceInterface() {
		return _serviceInterface;
	}

	@Override
	protected void doAction(Object context) throws FlexoException {
		logger.info("Add Service Interface");
		if (getProcess() != null && !getProcess().isImported()) {
			_serviceInterface = getProcess().addServiceInterface(getNewInterfaceName());
			if (logger.isLoggable(Level.INFO))
				logger.info("ServiceInterface created:" + _serviceInterface.getName());
		} else {
			logger.warning("Focused process is null or imported!");
		}
	}

}
