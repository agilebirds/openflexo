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
package org.openflexo.dm.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.dm.action.GenerateProcessesBusinessDataDMEntity;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateProcessesBusinessDataDMEntityInitializer extends ActionInitializer {
	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	protected GenerateProcessesBusinessDataDMEntityInitializer(DMControllerActionInitializer actionInitializer) {
		super(GenerateProcessesBusinessDataDMEntity.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionFinalizer<GenerateProcessesBusinessDataDMEntity> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateProcessesBusinessDataDMEntity>() {
			@Override
			public boolean run(ActionEvent e, GenerateProcessesBusinessDataDMEntity action) {
				FlexoController.notify(FlexoLocalization.localizedForKey("generate_processes_business_data_succeed"));
				return true;
			}
		};
	}
}
