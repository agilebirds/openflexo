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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.ViewLibrary;
import org.openflexo.foundation.view.ViewLibraryObject;
import org.openflexo.foundation.view.action.GenerateSepelCleanedUpProject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateSepelCleanedUpProjectInitializer extends
		ActionInitializer<GenerateSepelCleanedUpProject, ViewLibrary, ViewLibraryObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateSepelCleanedUpProjectInitializer(VEControllerActionInitializer actionInitializer) {
		super(GenerateSepelCleanedUpProject.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateSepelCleanedUpProject> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateSepelCleanedUpProject>() {
			@Override
			public boolean run(ActionEvent e, GenerateSepelCleanedUpProject action) {
				FIBDialog dialog = FIBDialog.instanciateAndShowDialog(
						new FileResource("Fib/Dialog/GenerateSepelCleanedUpProjectDialog.fib"), action, FlexoFrame.getActiveFrame(), true,
						FlexoLocalization.getMainLocalizer());
				return dialog.getStatus() == Status.VALIDATED;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateSepelCleanedUpProject> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateSepelCleanedUpProject>() {
			@Override
			public boolean run(ActionEvent e, GenerateSepelCleanedUpProject action) {
				FlexoController.notify("Project has been successfully exported as " + action.exportDirectory.getAbsolutePath());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.EXPORT_ICON;
	}

}
