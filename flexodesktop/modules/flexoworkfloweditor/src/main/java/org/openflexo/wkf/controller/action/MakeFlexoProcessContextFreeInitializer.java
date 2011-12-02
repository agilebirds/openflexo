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
package org.openflexo.wkf.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.action.MakeFlexoProcessContextFree;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class MakeFlexoProcessContextFreeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public MakeFlexoProcessContextFreeInitializer(ControllerActionInitializer actionInitializer) {
		super(MakeFlexoProcessContextFree.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<MakeFlexoProcessContextFree> getDefaultInitializer() {
		return new FlexoActionInitializer<MakeFlexoProcessContextFree>() {
			@Override
			public boolean run(ActionEvent e, MakeFlexoProcessContextFree action) {
				return (action.getFocusedObject() != null);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MakeFlexoProcessContextFree> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MakeFlexoProcessContextFree>() {
			@Override
			public boolean run(ActionEvent e, MakeFlexoProcessContextFree action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<MakeFlexoProcessContextFree> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<MakeFlexoProcessContextFree>() {
			@Override
			public boolean handleException(FlexoException exception, MakeFlexoProcessContextFree action) {
				exception.printStackTrace();
				FlexoController.showError(exception.getLocalizedMessage());
				return true;
			}
		};
	}

}
