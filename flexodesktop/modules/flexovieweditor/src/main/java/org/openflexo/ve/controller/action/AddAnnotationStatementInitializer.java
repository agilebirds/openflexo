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

import org.openflexo.icon.OntologyIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ontology.action.AddAnnotationStatement;
import org.openflexo.foundation.param.AnnotationPropertyParameter;
import org.openflexo.foundation.param.OntologyClassParameter;
import org.openflexo.foundation.param.OntologyObjectParameter;
import org.openflexo.foundation.param.OntologyPropertyParameter;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.TextFieldParameter;

public class AddAnnotationStatementInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddAnnotationStatementInitializer(OEControllerActionInitializer actionInitializer) {
		super(AddAnnotationStatement.actionType, actionInitializer);
	}

	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() {
		return (OEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddAnnotationStatement> getDefaultInitializer() {
		return new FlexoActionInitializer<AddAnnotationStatement>() {
			@Override
			public boolean run(ActionEvent e, final AddAnnotationStatement action) {
				TextFieldParameter nameParam = new TextFieldParameter("test", "name", "coucou");
				OntologyObjectParameter ooParam = new OntologyObjectParameter("on", "on", action.getFocusedObject());
				OntologyClassParameter classParam = new OntologyClassParameter("class", "class", null);
				OntologyPropertyParameter propParam = new OntologyPropertyParameter("property", "property", null);
				AnnotationPropertyParameter annotationParam = new AnnotationPropertyParameter("annotation", "annotation", null);

				AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, action.getLocalizedName(),
						FlexoLocalization.localizedForKey("please_enter_parameters_for_new_transtyper"),
						new AskParametersDialog.ValidationCondition() {
							@Override
							public boolean isValid(ParametersModel model) {
								/*if (entriesParameters.getValue().size() == 0) {
									errorMessage = FlexoLocalization.localizedForKey("please_supply_at_least_one_entry");
									return false;
								}
								else {
									for (DMTranstyperEntry entry : entriesParameters.getValue()) {
										if (entry.getType() == null) {
											errorMessage = FlexoLocalization.localizedForKeyWithParams("entry_($0)_has_undefined_type",entry.getName());
											return false;
										}
									}
									return true;
								}*/
								return true;
							}
						}, nameParam, ooParam, classParam, propParam, annotationParam);

				if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
					/*action.setNewTranstyperName(nameParam.getValue());
					action.setNewTranstyperType(typeParam.getValue());
					action.setEntries(entriesParameters.getValue());
					action.setIsMappingDefined(transtyperTypeChoiceParam.getValue().equals(DEFINE_MAPPING));*/
					return true;
				}

				// Cancelled
				return false;

			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddAnnotationStatement> getDefaultFinalizer() {
		return new FlexoActionFinalizer<AddAnnotationStatement>() {
			@Override
			public boolean run(ActionEvent e, AddAnnotationStatement action) {
				// ((OEController)getController()).getSelectionManager().setSelectedObject(action.getNewStatement());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return OntologyIconLibrary.ONTOLOGY_STATEMENT_ICON;
	}

}
