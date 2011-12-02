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
package org.openflexo.dre.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.GenerateHelpSet;
import org.openflexo.drm.helpset.DRMHelpSet;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.param.CheckboxListParameter;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.help.FlexoHelp;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.ModuleLoader;
import org.openflexo.module.UserType;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class GenerateHelpSetInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	GenerateHelpSetInitializer(DREControllerActionInitializer actionInitializer) {
		super(GenerateHelpSet.actionType, actionInitializer);
	}

	@Override
	protected DREControllerActionInitializer getControllerActionInitializer() {
		return (DREControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<GenerateHelpSet> getDefaultInitializer() {
		return new FlexoActionInitializer<GenerateHelpSet>() {
			@Override
			public boolean run(ActionEvent e, GenerateHelpSet action) {
				ParameterDefinition[] parameters = new ParameterDefinition[4 + DocResourceManager.instance().getDocResourceCenter()
						.getLanguages().size()];

				parameters[0] = new TextFieldParameter("baseName", "base_name", action.getBaseName());
				int langNb = 0;
				for (Language language : DocResourceManager.instance().getDocResourceCenter().getLanguages()) {
					org.openflexo.localization.Language lang = org.openflexo.localization.Language.get(language.getIdentifier());
					parameters[1 + langNb] = new TextFieldParameter("title_" + language.getIdentifier(), "title_"
							+ language.getIdentifier(), FlexoLocalization.localizedForKeyAndLanguage("help_for_flexo_tool_set", lang));
					langNb++;
				}
				CheckboxListParameter<Language> languagesParam = new CheckboxListParameter<Language>("languages", "languages",
						DocResourceManager.instance().getDocResourceCenter().getLanguages(), DocResourceManager.instance()
								.getDocResourceCenter().getLanguages());
				languagesParam.addParameter("format", "toString");
				parameters[1 + langNb] = languagesParam;

				CheckboxListParameter<UserType> distributionsParam = new CheckboxListParameter<UserType>("distributions", "distributions",
						UserType.allKnownUserType(), UserType.allKnownUserType());
				distributionsParam.addParameter("format", "localizedName");
				parameters[2 + langNb] = distributionsParam;

				parameters[3 + langNb] = new TextAreaParameter("note", "note", "");
				parameters[3 + langNb].addParameter("columns", "20");
				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
						FlexoLocalization.localizedForKey("generate_helpset"),
						FlexoLocalization.localizedForKey("enter_parameters_for_helpset_generation"), parameters);
				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					String baseName = (String) parameters[0].getValue();
					action.setBaseName(baseName);
					String note = (String) dialog.parameterValueWithName("note");
					action.setNote(note);
					for (Language language : languagesParam.getValue()) {
						for (UserType userType : distributionsParam.getValue()) {
							String title = (String) dialog.parameterValueWithName("title_" + language.getIdentifier());
							action.addToGeneration(title, language, userType.getIdentifier(), userType.getDocumentationFolders());
						}
					}
					if (action.getConfigurations().size() == 0) {
						FlexoController.notify(FlexoLocalization.localizedForKey("no_helpset_to_generate"));
						return false;
					}
					return true;
				} else {
					return false;
				}
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<GenerateHelpSet> getDefaultFinalizer() {
		return new FlexoActionFinalizer<GenerateHelpSet>() {
			@Override
			public boolean run(ActionEvent e, GenerateHelpSet action) {
				if (FlexoController.confirm(FlexoLocalization.localizedForKey("helpset_has_been_sucessfully_generated") + "\n"
						+ FlexoLocalization.localizedForKey("would_you_like_helpset_to_be_dynamically_reloaded"))) {
					logger.info("Reload HelpSet...");
					DRMHelpSet selected = null;
					for (DRMHelpSet set : action.getVectorOfGeneratedHelpset()) {
						if (set.getDistributionName().equals(ModuleLoader.getUserType().getIdentifier())
								&& FlexoLocalization.getCurrentLanguage().getIdentifier()
										.equalsIgnoreCase(set.getLanguage().getIdentifier())) {
							selected = set;
						}
					}
					DynamicDropDownParameter<DRMHelpSet> helpSetParameter = new DynamicDropDownParameter<DRMHelpSet>("helpset",
							"helpset_to_reload", action.getVectorOfGeneratedHelpset(), selected);
					helpSetParameter.addParameter("format", "localizedName");
					AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
							FlexoLocalization.localizedForKey("reload_helpset"),
							FlexoLocalization.localizedForKey("choose_helpset_to_reload"), helpSetParameter);
					if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
						FlexoHelp.reloadHelpSet(helpSetParameter.getValue().getHSFile());
						logger.info("HelpSet has been reloaded: " + helpSetParameter.getValue().getLocalizedName());
						FlexoController.notify(FlexoLocalization.localizedForKey("helpset_has_been_sucessfully_dynamically_reloaded"));
					}
				}
				return true;
			}
		};
	}

}
