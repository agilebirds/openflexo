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
package org.openflexo.fps.controller.action;

import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.fps.action.UpdateFiles;
import org.openflexo.icon.FPSIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class UpdateFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	UpdateFilesInitializer(FPSControllerActionInitializer actionInitializer) {
		super(UpdateFiles.actionType, actionInitializer);
	}

	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() {
		return (FPSControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<UpdateFiles> getDefaultInitializer() {
		return new FlexoActionInitializer<UpdateFiles>() {
			@Override
			public boolean run(EventObject e, UpdateFiles action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<UpdateFiles> getDefaultFinalizer() {
		return new FlexoActionFinalizer<UpdateFiles>() {
			@Override
			public boolean run(EventObject e, UpdateFiles action) {
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return FPSIconLibrary.FPS_UPDATE_ICON;
	}

	@Override
	protected Icon getDisabledIcon() {
		return FPSIconLibrary.FPS_UPDATE_DISABLED_ICON;
	}

}
