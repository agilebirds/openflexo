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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.InvalidParentProcessException;
import org.openflexo.foundation.wkf.action.MoveProcessFolder;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class MoveProcessFolderInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveProcessFolderInitializer(WKFControllerActionInitializer actionInitializer) {
		super(MoveProcessFolder.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveProcessFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveProcessFolder>() {
			@Override
			public boolean run(EventObject e, MoveProcessFolder action) {
				return /* FlexoController.confirm(FlexoLocalization.localizedForKey("are_you_sure_you_want_to_move_all_contained_processes?"));*/true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveProcessFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveProcessFolder>() {
			@Override
			public boolean run(EventObject e, MoveProcessFolder action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<MoveProcessFolder> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<MoveProcessFolder>() {
			@Override
			public boolean handleException(FlexoException exception, MoveProcessFolder action) {
				if (exception instanceof InvalidParentProcessException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_parent"));
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_FOLDER_ICON;
	}

}
