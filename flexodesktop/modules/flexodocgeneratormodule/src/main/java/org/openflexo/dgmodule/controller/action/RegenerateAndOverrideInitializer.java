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
package org.openflexo.dgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;


import org.openflexo.dgmodule.view.DGMainPane;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.generator.action.RegenerateAndOverride;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


public class RegenerateAndOverrideInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RegenerateAndOverrideInitializer(DGControllerActionInitializer actionInitializer)
	{
		super(RegenerateAndOverride.actionType,actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() 
	{
		return (DGControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<RegenerateAndOverride> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<RegenerateAndOverride>() {
			@Override
			public boolean run(ActionEvent e, RegenerateAndOverride action)
			{
				((DGMainPane)getController().getMainPane()).getDgBrowserView().getBrowser().setHoldStructure();
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<RegenerateAndOverride> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<RegenerateAndOverride>() {
			@Override
			public boolean run(ActionEvent e, RegenerateAndOverride action)
			{
				((DGMainPane)getController().getMainPane()).getDgBrowserView().getBrowser().resetHoldStructure();
				((DGMainPane)getController().getMainPane()).getDgBrowserView().getBrowser().update();
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<RegenerateAndOverride> getDefaultExceptionHandler() 
	{
		return new FlexoExceptionHandler<RegenerateAndOverride>() {
			@Override
			public boolean handleException(FlexoException exception, RegenerateAndOverride action) {
				((DGMainPane)getController().getMainPane()).getDgBrowserView().getBrowser().resetHoldStructure();
				((DGMainPane)getController().getMainPane()).getDgBrowserView().getBrowser().update();
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				exception.printStackTrace();
				FlexoController.showError(FlexoLocalization.localizedForKey("code_generation_synchronization_for_repository_failed")
						+ ":\n" + exception.getLocalizedMessage());
				return true;
			}
		};
	}

}
