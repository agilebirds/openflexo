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
package org.openflexo.doceditor.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.doceditor.DECst;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.action.InvalidLevelException;
import org.openflexo.foundation.toc.action.AddTOCEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddTOCEntryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public AddTOCEntryInitializer(ControllerActionInitializer actionInitializer) {
		super(AddTOCEntry.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<AddTOCEntry> getDefaultInitializer() {
		return new FlexoActionInitializer<AddTOCEntry>() {
			@Override
			public boolean run(ActionEvent e, AddTOCEntry action) {

				FIBDialog dialog = FIBDialog.instanciateAndShowDialog(DECst.CREATE_TOC_ENTRY_DIALOG_FIB, action, null, true,
						FlexoLocalization.getMainLocalizer());
				return (dialog.getStatus() == Status.VALIDATED);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddTOCEntry> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddTOCEntry>() {
			@Override
			public boolean run(ActionEvent e, AddTOCEntry action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<? super AddTOCEntry> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddTOCEntry>() {

			@Override
			public boolean handleException(FlexoException exception, AddTOCEntry action) {
				if (exception instanceof InvalidLevelException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("invalid_level"));
					return true;
				}
				return false;
			}

		};
	}

}
