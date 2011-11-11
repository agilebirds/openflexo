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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.dg.action.GeneratePDF;
import org.openflexo.dgmodule.DGPreferences;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.param.FileSelectorParameter;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.IOExceptionOccuredException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.LatexUtils;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GeneratePDFInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GeneratePDFInitializer(DGControllerActionInitializer actionInitializer) {
		super(GeneratePDF.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GeneratePDF> getDefaultInitializer() {
		return new FlexoActionInitializer<GeneratePDF>() {
			@Override
			public boolean run(ActionEvent e, GeneratePDF action) {
				if (action.getRepository().getDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (action.getFocusedObject().getPostBuildDirectory() == null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("please_supply_valid_directory"));
					return false;
				}
				if (!action.getRepository().getPostBuildRepository().getDirectory().exists()) {
					if (FlexoController.confirm(FlexoLocalization.localizedForKey("directory") + " "
							+ action.getRepository().getPostBuildRepository().getDirectory().getAbsolutePath() + " "
							+ FlexoLocalization.localizedForKey("does_not_exist") + "\n"
							+ FlexoLocalization.localizedForKey("would_you_like_to_create_it_and_continue?"))) {
						action.getRepository().getPostBuildRepository().getDirectory().mkdirs();
					} else {
						return false;
					}
				}
				action.setSaveBeforeGenerating(DGPreferences.getSaveBeforeGenerating());
				if (DGPreferences.getLatexCommand() == null || DGPreferences.getLatexCommand().trim().length() == 0) {
					DGPreferences.setLatexCommand(null);
					if (DGPreferences.getLatexCommand() == null || DGPreferences.getLatexCommand().trim().length() == 0) {
						RadioButtonListParameter radio = new RadioButtonListParameter<String>("radio", "select_a_choice", "command",
								new String[] { "command", "file" });
						TextFieldParameter command = new TextFieldParameter("command", "command",
								ToolBox.getPLATFORM() == ToolBox.WINDOWS ? "texify" : "pdflatex");
						command.setDepends("radio");
						command.setConditional("radio=" + '"' + "command" + '"');
						FileSelectorParameter file = new FileSelectorParameter("file", "file",
								ToolBox.getPLATFORM() == ToolBox.WINDOWS ? new File(System.getenv("ProgramFiles")) : new File(
										System.getProperty("user.home")));
						file.setDepends("radio");
						file.setConditional("radio=" + '"' + "file" + '"');
						AskParametersDialog ask = AskParametersDialog.createAskParametersDialog(getProject(), null,
								FlexoLocalization.localizedForKey("select_latex_command"),
								FlexoLocalization.localizedForKey("choose_command_to_execute"), radio, command, file);
						if (ask.getStatus() == AskParametersDialog.VALIDATE) {
							if (radio.getValue().equals("command")) {
								DGPreferences.setLatexCommand(command.getValue());
							} else if (radio.getValue().equals("file"))
								DGPreferences.setLatexCommand(file.getValue().getAbsolutePath());
							if (LatexUtils.testLatexCommand(DGPreferences.getLatexCommand())) {
								action.setLatexCommand(DGPreferences.getLatexCommand());
								if (FlexoController
										.confirm(FlexoLocalization
												.localizedForKey("PDF_generation_is_based_on_code_generated_on_disk._Is_your_code_on_disk_ready_to_be_compiled?"))) {
									return true;
								} else
									return false;
							}
							FlexoController.notify(FlexoLocalization.localizedForKey("invalid_latex_command"));
							return false;
						} else
							return false;// User has pressed cancel or closed
											// the dialog

					}
				}
				if (action.getRepository().getPostBuildDirectory().exists() && !action.getRepository().getPostBuildDirectory().canWrite()) {
					FlexoController.notify(FlexoLocalization.localizedForKey("permission_denied_for ")
							+ action.getRepository().getPostBuildDirectory().getAbsolutePath());
					return false;
				}
				if (LatexUtils.testLatexCommand(DGPreferences.getLatexCommand())) {
					action.setLatexCommand(DGPreferences.getLatexCommand());
					if (FlexoController
							.confirm(FlexoLocalization
									.localizedForKey("PDF_generation_is_based_on_code_generated_on_disk._Is_your_code_on_disk_ready_to_be_compiled?"))) {
						if (action.getRepository().getPostBuildFile() != null && action.getRepository().getPostBuildFile().exists()) {
							FileOutputStream test = null;
							try {
								test = new FileOutputStream(action.getRepository().getPostBuildFile());
							} catch (FileNotFoundException e1) {
								if (FlexoController.confirm(FlexoLocalization
										.localizedForKey("target_pdf_file_seems_to_be_locked.continue_anyway")))
									return true;
								else
									return false;
							} finally {
								if (test != null)
									try {
										test.close();
									} catch (IOException e1) {
										e1.printStackTrace();
									}
							}
						}
						return true;
					}
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GeneratePDF> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GeneratePDF>() {
			@Override
			public boolean run(ActionEvent e, GeneratePDF action) {
				if (action.getGeneratedPDF() != null && DGPreferences.getOpenPDF()) {
					ToolBox.openFile(action.getGeneratedPDF());
				}
				return true;
			}
		};
	}

	@Override
	protected FlexoExceptionHandler<GeneratePDF> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<GeneratePDF>() {
			@Override
			public boolean handleException(FlexoException exception, GeneratePDF action) {
				getControllerActionInitializer().getDGController().disposeProgressWindow();
				if (action.getLatexErrorMessage() != null) {
					FlexoController.notify(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ FlexoLocalization.localizedForKey("latex_error: ") + action.getLatexErrorMessage());
					return true;
				}
				if (exception instanceof IOExceptionOccuredException) {
					FlexoController
							.notify(FlexoLocalization.localizedForKey("generation_failed")
									+ ":\n"
									+ FlexoLocalization.localizedForKey("could_not_rename_output_to")
									+ " "
									+ action.getRepository().getPostBuildFile().getAbsolutePath()
									+ "\n"
									+ FlexoLocalization
											.localizedForKey("verify_that_you_have_write_permissions,_that the file is not used by another application"));
					return true;
				}
				if (exception instanceof GenerationException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("generation_failed") + ":\n"
							+ ((GenerationException) exception).getLocalizedMessage());
					return true;
				}
				exception.printStackTrace();
				return false;
			}
		};
	}

}
