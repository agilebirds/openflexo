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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.module.Module;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.external.ExternalDMModule;
import org.openflexo.sg.action.OpenDMEntity;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class OpenDMEntityInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenDMEntityInitializer(SGControllerActionInitializer actionInitializer) {
		super(OpenDMEntity.actionType, actionInitializer);
	}

	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() {
		return (SGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<OpenDMEntity> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenDMEntity>() {
			@Override
			public boolean run(ActionEvent e, OpenDMEntity action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenDMEntity> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenDMEntity>() {
			@Override
			public boolean run(ActionEvent e, OpenDMEntity action) {
				ExternalDMModule dmModule = ModuleLoader.getDMModule();
				if (dmModule == null) {
					return false;
				}
				dmModule.focusOn();
				dmModule.showDMEntity(action.getModelEntity());
				if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
					SwingUtilities.invokeLater(new SwitchToDMJob());
				}
				return true;
			}
		};
	}

	private class SwitchToDMJob implements Runnable {

		SwitchToDMJob() {
		}

		@Override
		public void run() {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switching to DM");
			}
			ModuleLoader.switchToModule(Module.DM_MODULE);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Switched to DM done!");
			}
		}
	}

}
