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
package org.openflexo.ve.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.ViewFolder;
import org.openflexo.foundation.view.action.MoveViewFolder;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MoveViewFolderInitializer extends ActionInitializer<MoveViewFolder, ViewFolder, ViewFolder> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MoveViewFolderInitializer(VEControllerActionInitializer actionInitializer) {
		super(MoveViewFolder.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MoveViewFolder> getDefaultInitializer() {
		return new FlexoActionInitializer<MoveViewFolder>() {
			@Override
			public boolean run(EventObject e, MoveViewFolder action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MoveViewFolder> getDefaultFinalizer() {
		return new FlexoActionFinalizer<MoveViewFolder>() {
			@Override
			public boolean run(EventObject e, MoveViewFolder action) {
				return true;
			}
		};
	}

}
