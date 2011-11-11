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

import javax.swing.Icon;

import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.action.DeleteProcessFolder;

public class DeleteProcessFolderInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteProcessFolderInitializer(WKFControllerActionInitializer actionInitializer) {
		super(DeleteProcessFolder.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DeleteProcessFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteProcessFolder>() {
			@Override
			public boolean run(ActionEvent e, DeleteProcessFolder action) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteProcessFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteProcessFolder>() {
			@Override
			public boolean run(ActionEvent e, DeleteProcessFolder action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<DeleteProcessFolder> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<DeleteProcessFolder>() {
			@Override
			public boolean handleException(FlexoException exception, DeleteProcessFolder action) {
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
