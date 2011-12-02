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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.Vector;

import javax.swing.Icon;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.components.AskParametersDialog.ValidationCondition;
import org.openflexo.foundation.DocType;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.ChoiceListParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.foundation.toc.action.AddTOCRepository;
import org.openflexo.foundation.xml.FlexoTOCBuilder;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.xmlcode.XMLDecoder;

public class AddTOCRepositoryInitializer extends ActionInitializer {

	public AddTOCRepositoryInitializer(DEControllerActionInitializer controllerActionInitializer) {
		super(AddTOCRepository.actionType, controllerActionInitializer);
	}

	@Override
	protected FlexoActionInitializer<? super AddTOCRepository> getDefaultInitializer() {
		return new FlexoActionInitializer<AddTOCRepository>() {

			@Override
			public boolean run(ActionEvent event, AddTOCRepository action) {
				ParameterDefinition[] def = new ParameterDefinition[3];
				def[0] = new TextFieldParameter("name", "toc_name", "");
				ChoiceListParameter<DocType> docTypeParameter = new ChoiceListParameter<DocType>("docType", "base_model", getProject()
						.getDocTypes().get(0));
				docTypeParameter.setShowReset(false);
				def[1] = docTypeParameter;

				File tocTemplateDirectory = new FileResource("Config/TOCTemplates");
				File[] availableTemplates = tocTemplateDirectory.listFiles(new FileFilter() {
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile() && pathname.getName().endsWith(".xml");
					}
				});
				Vector<String> templatesList = new Vector<String>();
				for (File f : availableTemplates) {
					templatesList.add(f.getName().substring(0, f.getName().length() - 4));
				}
				DynamicDropDownParameter<String> tocTemplateParameter = new DynamicDropDownParameter<String>("tocTemplate", "toc_template",
						templatesList, null);
				tocTemplateParameter.setShowReset(true);
				def[2] = tocTemplateParameter;

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), getController().getFlexoFrame(),
						FlexoLocalization.localizedForKey("enter_parameters"),
						FlexoLocalization.localizedForKey("enter_parameters_for_table_of_content"), new ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						boolean b = model.parameterForKey("name").getValue() != null
								&& ((String) model.parameterForKey("name").getValue()).trim().length() > 0;
								if (!b) {
									errorMessage = FlexoLocalization.localizedForKey("name_cannot_be_empty");
								}
								if (model.parameterForKey("docType").getValue() == null) {
									if (b) {
										errorMessage = FlexoLocalization.localizedForKey("select_a_base_model");
									}
									b = false;
								}
								if (getProject().getTOCData().getRepositoryWithTitle((String) model.parameterForKey("name").getValue()) != null) {
									errorMessage = FlexoLocalization.localizedForKey("there_is_already_a_toc_with_that_name");
									b = false;
								}
								return b;
					}

				}, def);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					action.setRepositoryName((String) def[0].getValue());
					action.setDocType((DocType) def[1].getValue());

					if (def[2].getValue() != null) {
						String tocTemplateFileName = def[2].getValue() + ".xml";
						File tocTemplateFile = new FileResource("Config/TOCTemplates/" + tocTemplateFileName);
						try {
							TOCRepository tocTemplate = (TOCRepository) XMLDecoder.decodeObjectWithMappingFile(new FileInputStream(
									tocTemplateFile), new FileResource("Models/TOCModel/toc_template_0.1.xml"), new FlexoTOCBuilder(null));
							action.setTocTemplate(tocTemplate);
						} catch (Exception e) {
							e.printStackTrace();
							FlexoController.showError(e.getMessage());
						}

					}
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return DEIconLibrary.TOC_REPOSITORY;
	}

	@Override
	protected FlexoActionFinalizer<? super AddTOCRepository> getDefaultFinalizer() {

		return new FlexoActionFinalizer<AddTOCRepository>() {

			@Override
			public boolean run(ActionEvent event, AddTOCRepository action) {
				if (action.getNewRepository() != null) {
					getController().setCurrentEditedObjectAsModuleView(action.getNewRepository());
				}
				return false;
			}

		};
	}
}
