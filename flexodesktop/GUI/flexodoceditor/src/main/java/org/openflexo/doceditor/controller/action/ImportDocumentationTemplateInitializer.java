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

import javax.swing.Icon;

import org.openflexo.doceditor.DECst;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.action.ImportDocumentationTemplates;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportDocumentationTemplateInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public ImportDocumentationTemplateInitializer(DEControllerActionInitializer actionInitializer) {
		super(ImportDocumentationTemplates.actionType, actionInitializer);
	}

	@Override
	protected DEControllerActionInitializer getControllerActionInitializer() {
		return (DEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoExceptionHandler getDefaultExceptionHandler() {
		return new FlexoExceptionHandler() {
			@Override
			public boolean handleException(FlexoException exception, FlexoAction action) {
				FlexoController.notify(exception.getMessage());
				return false;
			}

		};
	}

	@Override
	protected FlexoActionInitializer<ImportDocumentationTemplates> getDefaultInitializer() {
		return new FlexoActionInitializer<ImportDocumentationTemplates>() {
			@Override
			public boolean run(ActionEvent e, ImportDocumentationTemplates action) {
				FIBDialog dialog = FIBDialog.instanciateDialog(DECst.IMPORT_DOCUMENTATION_TEMPLATES_FIB, action,
						FlexoFrame.getActiveFrame(), true, FlexoLocalization.getMainLocalizer());
				dialog.setTitle(FlexoLocalization.localizedForKey("import_documentation_templates"));
				dialog.center();
				dialog.setVisible(true);
				return dialog.getStatus() == Status.VALIDATED;
			}

		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.IMPORT_ICON;
	}

}
