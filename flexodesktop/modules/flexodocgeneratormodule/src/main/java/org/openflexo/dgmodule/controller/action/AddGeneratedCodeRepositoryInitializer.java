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
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.Format;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.GeneratedOutput;
import org.openflexo.foundation.cg.action.AddGeneratedCodeRepository;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DirectoryParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParameterDefinition.ValueListener;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.cg.FileHistory;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddGeneratedCodeRepositoryInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddGeneratedCodeRepositoryInitializer(DGControllerActionInitializer actionInitializer) {
		super(AddGeneratedCodeRepository.actionType, actionInitializer);
	}

	@Override
	protected DGControllerActionInitializer getControllerActionInitializer() {
		return (DGControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddGeneratedCodeRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<AddGeneratedCodeRepository>() {
			@Override
			public boolean run(ActionEvent e, final AddGeneratedCodeRepository action) {
				if (action.getNewGeneratedCodeRepositoryName() == null || action.getNewGeneratedCodeRepositoryDirectory() == null) {
					Vector<Format> values = new Vector<Format>();
					values.add(Format.HTML);
					values.add(Format.LATEX);
					values.add(Format.DOCX);
					final GeneratedOutput gc = action.getFocusedObject().getGeneratedCode();
					final DynamicDropDownParameter<Format> format = new DynamicDropDownParameter<Format>("format", "format", Format.HTML);
					format.setAvailableValues(values);
					format.setShowReset(false);
					final ChoiceListParameter<DocType> targetType = new ChoiceListParameter<DocType>("target", "target",
							action.getNewDocType());
					targetType.setShowReset(false);
					final TextFieldParameter paramName = new TextFieldParameter("name", "repository_name",
							action.getNewGeneratedCodeRepositoryName() == null ? gc.getProject().getNextExternalRepositoryIdentifier(
									FlexoLocalization.localizedForKey("generated_doc")) : action.getNewGeneratedCodeRepositoryName());
					DynamicDropDownParameter<TOCRepository> paramToc = null;
					if (getProject().getTOCData().getRepositories().size() > 0) {
						paramToc = new DynamicDropDownParameter<TOCRepository>("toc", "toc", getProject().getTOCData().getRepositories(),
								getProject().getTOCData().getRepositories().firstElement());
						paramToc.setFormatter("title");
						paramToc.setDepends("format");
						paramToc.setConditional("format!=HTML");
					}
					final DirectoryParameter paramDir = new DirectoryParameter("directory", "source_directory", getParamDir(action, gc,
							targetType.getValue(), format.getValue(), paramName));
					format.addValueListener(new ValueListener<Format>() {
						@Override
						public void newValueWasSet(ParameterDefinition<Format> param, Format oldValue, Format newValue) {
							if (getParamDir(action, gc, targetType.getValue(), oldValue, paramName).equals(paramDir.getValue())) {
								paramDir.setValue(getParamDir(action, gc, targetType.getValue(), newValue, paramName));
							}
						}
					});
					paramDir.setDepends("target");
					targetType.addValueListener(new ValueListener<DocType>() {

						@Override
						public void newValueWasSet(ParameterDefinition<DocType> param, DocType oldValue, DocType newValue) {
							if (getParamDir(action, gc, oldValue, format.getValue(), paramName).equals(paramDir.getValue())) {
								paramDir.setValue(getParamDir(action, gc, newValue, format.getValue(), paramName));
							}
						}

					});
					ParameterDefinition<?>[] pd = new ParameterDefinition<?>[paramToc == null ? 4 : 5];
					pd[0] = format;
					pd[1] = targetType;
					pd[2] = paramName;
					pd[3] = paramToc != null ? paramToc : paramDir;
					if (paramToc != null) {
						pd[4] = paramDir;
					}
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("create_new_generated_doc_repository"),
							FlexoLocalization.localizedForKey("enter_parameters_for_the_new_generated_doc_repository"), pd);
					System.setProperty("apple.awt.fileDialogForDirectories", "false");

					while (dialog.getStatus() == AskParametersDialog.VALIDATE
							&& getProject().getExternalRepositoryWithDirectory(paramDir.getValue()) != null) {
						if (getProject().getExternalRepositoryWithDirectory(paramDir.getValue()) != null) {
							dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
									FlexoLocalization.localizedForKey("directory_is_already_used"),
									FlexoLocalization.localizedForKey("confirm_source_directory_for_new_generated_doc"), paramDir);
						} else if (paramDir.getValue() != null && new File(paramDir.getValue(), FileHistory.HISTORY_DIR).exists()) {
							dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
									FlexoLocalization.localizedForKey("directory_seems_to_be_already_used"),
									FlexoLocalization.localizedForKey("confirm_source_directory_for_new_generated_doc"), paramDir);
						}
					}
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						action.setFormat(format.getValue());
						action.setNewDocType(targetType.getValue());
						action.setNewGeneratedCodeRepositoryName(paramName.getValue());
						action.setNewGeneratedCodeRepositoryDirectory(paramDir.getValue());
						if (paramToc != null) {
							action.setTocRepository(paramToc.getValue());
						}
						return true;
					} else {
						return false;
					}
				}

				return true;
			}

			protected File getParamDir(AddGeneratedCodeRepository action, GeneratedOutput gc, DocType docType, Format format,
					TextFieldParameter paramName) {
				if (action.getNewGeneratedCodeRepositoryDirectory() == null) {
					return new File(System.getProperty("user.home") + "/" + gc.getProject().getProjectName() + "/" + format.name() + "/"
							+ (docType != null ? docType.getName() : paramName.getValue()));
				} else {
					return action.getNewGeneratedCodeRepositoryDirectory();
				}
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

	@Override
	protected Icon getEnabledIcon() {
		return DGIconLibrary.GENERATED_DOC_ICON;
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
				return false;
			}

		};
	}
}
