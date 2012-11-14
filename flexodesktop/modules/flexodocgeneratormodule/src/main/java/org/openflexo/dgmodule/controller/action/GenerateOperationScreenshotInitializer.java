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

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.dgmodule.view.popups.ShowScreenshotDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.action.GenerateOperationScreenshot;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateOperationScreenshotInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateOperationScreenshotInitializer(DGControllerActionInitializer actionInitializer) {
		super(GenerateOperationScreenshot.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateOperationScreenshot> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateOperationScreenshot>() {
			@Override
			public boolean run(ActionEvent e, GenerateOperationScreenshot action) {
				// This action could be called from outside the
				// scope of the DocumentationGenerator, sooo....
				// We always return true, here, but we also store
				// controller in context object
				logger.info("Active controller is: " + FlexoModule.getActiveModule().getFlexoController());
				action.setContext(FlexoModule.getActiveModule().getFlexoController());
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateOperationScreenshot> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateOperationScreenshot>() {
			@Override
			public boolean run(ActionEvent e, GenerateOperationScreenshot action) {
				FlexoController controller = (FlexoController) action.getContext();
				if (action.getScreenshotResource() == null) {
					FlexoController.showError(FlexoLocalization.localizedForKey("error_while_generating_screenshot"));
				} else {
					String title;
					if (action.hasBeenRegenerated()) {
						title = FlexoLocalization.localizedForKey("screenshot") + " " + action.getScreenshotResource().getFile().getName()
								+ " " + FlexoLocalization.localizedForKey("has_been_regenerated");
					} else {
						title = FlexoLocalization.localizedForKey("screenshot") + " " + action.getScreenshotResource().getFile().getName()
								+ " " + FlexoLocalization.localizedForKey("was_up_to_date");
					}
					if (controller instanceof DGController && ((DGController) controller).getIgnoreScreenshotVisualization()) {
						// Ignore
					} else {
						ShowScreenshotDialog newDialog = new ShowScreenshotDialog(title, action.getScreenshotResource(),
								controller.getFlexoFrame());
					}
				}
				return true;
			}
		};
	}

}
