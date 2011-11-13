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
package org.openflexo.view.controller.action;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoRemoteException;
import org.openflexo.foundation.FlexoServiceException;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.imported.action.RefreshImportedObjectAction;
import org.openflexo.view.controller.FlexoController;

public class FlexoWSExceptionHandler<A extends RefreshImportedObjectAction<A, FlexoModelObject, FlexoModelObject>> extends
		FlexoExceptionHandler<A> {

	private FlexoController controller;

	public FlexoWSExceptionHandler(FlexoController controller) {
		this.controller = controller;
	}

	@Override
	public boolean handleException(FlexoException exception, A action) {
		if (action.isAutomaticAction()) {
			return true;
		}
		if (exception instanceof FlexoRemoteException) {
			getController().handleWSException(((FlexoRemoteException) exception).getCause());
			return false;
		} else if (exception instanceof FlexoServiceException) {
			getController().handleWSException(((FlexoServiceException) exception).getCause());
			return false;
		}
		return false;
	}

	public FlexoController getController() {
		return controller;
	}

}
