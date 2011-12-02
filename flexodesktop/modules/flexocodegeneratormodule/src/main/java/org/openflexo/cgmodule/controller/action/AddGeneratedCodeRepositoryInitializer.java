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
package org.openflexo.cgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.MissingReaderRepositoryException;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParameterDefinition.ValueListener;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddGeneratedCodeRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddGeneratedCodeRepositoryInitializer(GeneratorControllerActionInitializer actionInitializer) {
		super(AddGeneratedCodeRepository.actionType, actionInitializer);
	}

	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() {
		return (GeneratorControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddGeneratedCodeRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<AddGeneratedCodeRepository>() {
			@Override
			public boolean run(ActionEvent e, AddGeneratedCodeRepository action) {
				if ((action.getNewGeneratedCodeRepositoryName() == null) || (action.getNewGeneratedCodeRepositoryDirectory() == null)) {
					GeneratedOutput gc = action.getFocusedObject().getGeneratedCode();
					Vector<DGRepository> availableReaderRepositories = new Vector<DGRepository>();
					for (GenerationRepository r : gc.getProject().getGeneratedDoc().getGeneratedRepositories()) {
						if (r instanceof DGRepository) {
							if (((DGRepository) r).getFormat() == Format.HTML) {
								availableReaderRepositories.add((DGRepository) r);
							}
						} else {
							if (logger.isLoggable(Level.SEVERE)) {
								logger.severe("Found something else than a DGRepository in generated doc!: " + r);
							}
						}
					}
					ChoiceListParameter<CodeType> targetType = new ChoiceListParameter<CodeType>("target", "target",
							action.getNewTargetType());
					targetType.setShowReset(false);
					final TextFieldParameter paramName = new TextFieldParameter("name", "repository_name",
							(action.getNewGeneratedCodeRepositoryName() == null ? gc.getProject().getNextExternalRepositoryIdentifier(
									FlexoLocalization.localizedForKey("generated_code")) : action.getNewGeneratedCodeRepositoryName()));
					DirectoryParameter paramDir = new DirectoryParameter("directory", "source_directory",
							(action.getNewGeneratedCodeRepositoryDirectory() == null ? new File(System.getProperty("user.home"), gc
									.getProject().getProjectName()) : action.getNewGeneratedCodeRepositoryDirectory()));
					final CheckboxParameter includeReader = new CheckboxParameter("includeReader", "include_reader", true);
					final DynamicDropDownParameter<DGRepository> readerRepository = new DynamicDropDownParameter<DGRepository>(
							"repository", "reader_repository", availableReaderRepositories,
							availableReaderRepositories.size() > 0 ? availableReaderRepositories.firstElement() : null);
					readerRepository.setDepends("includeReader");
					readerRepository.setConditional("includeReader=true");
					readerRepository.setFormatter("name");
					final DirectoryParameter readerDir = new DirectoryParameter("readerDirectory", "reader_directory", getReaderParamDir(
							paramName, readerRepository.getValue()));
					readerRepository.addValueListener(new ValueListener<DGRepository>() {

						@Override
						public void newValueWasSet(ParameterDefinition<DGRepository> param, DGRepository oldValue, DGRepository newValue) {
							if (includeReader.getValue() && (newValue != null)) {
								readerDir.setValue(getReaderParamDir(paramName, newValue));
							}
						}

					});
					readerDir.setDepends("repository,includeReader");
					readerDir.setConditional("includeReader=true AND repository!=null AND repository.isConnected=false");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("create_new_generated_code_repository"),
							FlexoLocalization.localizedForKey("enter_parameters_for_the_new_generated_code_repository"),
							new AskParametersDialog.ValidationCondition() {

								@Override
								public boolean isValid(ParametersModel model) {
									if ((paramName.getValue() == null) || (paramName.getValue().trim().length() == 0)) {
										errorMessage = FlexoLocalization.localizedForKey("repository_name_cannot_be_empty");
										return false;
									}
									if (includeReader.getValue() && (readerRepository.getValue() == null)) {
										errorMessage = FlexoLocalization.localizedForKey("you_must_provide_a_reader_repository");
										return false;
									}
									if (includeReader.getValue()
											&& (!readerRepository.getValue().isConnected() && (readerDir.getValue() == null))) {
										errorMessage = FlexoLocalization.localizedForKey("reader_not_configured");
										return false;
									}
									return true;
								}

							}, targetType, paramName, paramDir, includeReader, readerRepository, readerDir);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setNewTargetType(targetType.getValue());
						action.setNewGeneratedCodeRepositoryName(paramName.getValue());
						action.setNewGeneratedCodeRepositoryDirectory(paramDir.getValue());
						action.setIncludeReader(includeReader.getValue());
						action.setReaderRepository(readerRepository.getValue());
						if ((action.getReaderRepository() != null) && !action.getReaderRepository().isConnected()) {
							action.getReaderRepository().setDirectory(readerDir.getValue());
						}
						if (action.isIncludeReader() && !action.getReaderRepository().isConnected()) {
							FlexoController.notify(FlexoLocalization.localizedForKey("reader_not_configured"));
							return false;
						}
						return true;
					} else {
						return false;
					}
				}
				return true;
			}

			protected File getReaderParamDir(final TextFieldParameter paramName, DGRepository newValue) {
				if (newValue == null) {
					return null;
				}
				return newValue.getDirectory() != null ? newValue.getDirectory() : new File(System.getProperty("user.home") + "/"
						+ getProject().getProjectName() + "/" + newValue.getFormat().name() + "/"
						+ (newValue.getDocType() != null ? newValue.getDocType().getName() : paramName.getValue()));
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddGeneratedCodeRepository> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddGeneratedCodeRepository>() {
			@Override
			public boolean run(ActionEvent e, AddGeneratedCodeRepository action) {
				if (action.getNewGeneratedCodeRepository() != null) {
					getController().setCurrentEditedObjectAsModuleView(action.getNewGeneratedCodeRepository());
				}
				return true;
			}
		};
	}

	/**
	 * Overrides getDefaultExceptionHandler
	 * 
	 * @see org.openflexo.view.controller.ActionInitializer#getDefaultExceptionHandler()
	 */
	@Override
	protected FlexoExceptionHandler<AddGeneratedCodeRepository> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddGeneratedCodeRepository>() {

			@Override
			public boolean handleException(FlexoException exception, AddGeneratedCodeRepository action) {
				if (exception instanceof DuplicateCodeRepositoryNameException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("name_is_already_used"));
					return true;
				}
				if (exception instanceof MissingReaderRepositoryException) {
					FlexoController.notify(FlexoLocalization.localizedForKey("you_must_provide_a_reader_repository"));
					return true;
				}
				return false;
			}

		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return CGIconLibrary.GENERATED_CODE_REPOSITORY_ICON;
	}

}
