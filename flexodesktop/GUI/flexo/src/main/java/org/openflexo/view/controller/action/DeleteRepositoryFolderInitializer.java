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

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.DeleteRepositoryFolder;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DeleteRepositoryFolderInitializer extends ActionInitializer<DeleteRepositoryFolder, RepositoryFolder, RepositoryFolder> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public DeleteRepositoryFolderInitializer(ControllerActionInitializer actionInitializer) {
		super(DeleteRepositoryFolder.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<DeleteRepositoryFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<DeleteRepositoryFolder>() {
			@Override
			public boolean run(EventObject e, DeleteRepositoryFolder action) {
				return FlexoController.confirm(FlexoLocalization.localizedForKey("really_delete_this_view_folder_and_all_its_contents_?"));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DeleteRepositoryFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DeleteRepositoryFolder>() {
			@Override
			public boolean run(EventObject e, DeleteRepositoryFolder action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.DELETE_ICON;
	}

}
