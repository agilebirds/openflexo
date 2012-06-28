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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.openflexo.dg.action.ReinjectDocx;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.FlexoFileChooser;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ReinjectDocxInitializer extends ActionInitializer<ReinjectDocx, CGObject, CGObject> {

	protected static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ReinjectDocxInitializer(DGControllerActionInitializer actionInitializer) {
		super(ReinjectDocx.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<ReinjectDocx> getDefaultInitializer() {
		return new FlexoActionInitializer<ReinjectDocx>() {
			@Override
			public boolean run(ActionEvent e, ReinjectDocx action) {
				FlexoFileChooser fileChooser = new FlexoFileChooser(SwingUtilities.getWindowAncestor(getController().getMainPane()));
				fileChooser.setCurrentDirectory(((DGRepository) action.getRepository()).getPostBuildDirectory());
				fileChooser.setFileFilterAsString("*.docx");
				fileChooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fileChooser.showDialog(FlexoLocalization.localizedForKey("select"));
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					action.setDocxToReinject(fileChooser.getSelectedFile());
					return true;
				}

				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<ReinjectDocx> getDefaultFinalizer() {
		return new FlexoActionFinalizer<ReinjectDocx>() {
			@Override
			public boolean run(ActionEvent e, ReinjectDocx action) {
				FlexoController.notify(FlexoLocalization.localizedForKey("reinject_docx_succeed")
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_updated_description")
						+ ": "
						+ action.getNumberOfDescriptionUpdated()
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_updated_name")
						+ ": "
						+ action.getNumberOfNameUpdated()
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_updated_tocentry_title")
						+ ": "
						+ action.getNumberOfTocEntryTitleUpdated()
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_updated_tocentry_content")
						+ ": "
						+ action.getNumberOfTocEntryContentUpdated()
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_updated_edition_pattern")
						+ ": "
						+ action.getNumberOfEPIUpdated()
						+ "\n"
						+ FlexoLocalization.localizedForKey("number_of_not_found_object")
						+ ": "
						+ action.getNumberOfObjectNotFound()
						+ (action.hasError() ? "\n" + FlexoLocalization.localizedForKey("reinject_docx_warnings") + ":\n"
								+ action.getErrorReport() : ""));
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<ReinjectDocx> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<ReinjectDocx>() {
			@Override
			public boolean handleException(FlexoException exception, ReinjectDocx action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();

				FlexoController.notify(FlexoLocalization.localizedForKey("reinject_docx_failed") + ":\n" + exception.getLocalizedMessage());
				logger.log(Level.SEVERE, exception.getMessage(), exception);
				return false;
			}
		};
	}

}
