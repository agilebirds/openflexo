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
package org.openflexo.dm.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.foundation.dm.ComponentRepository;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMMethod;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.dm.WORepository;
import org.openflexo.foundation.dm.eo.DMEOJoin;
import org.openflexo.foundation.dm.eo.DMEORelationship;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.model.InvalidJoinException;
import org.openflexo.foundation.dm.javaparser.JavaParseException;
import org.openflexo.foundation.dm.javaparser.ParserNotInstalledException;
import org.openflexo.foundation.param.PropertyListParameter;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DMSetPropertyInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	public DMSetPropertyInitializer(ControllerActionInitializer actionInitializer) {
		super(SetPropertyAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<SetPropertyAction> getDefaultInitializer() {
		return new FlexoActionInitializer<SetPropertyAction>() {
			@Override
			public boolean run(ActionEvent e, SetPropertyAction action) {
				return action.getFocusedObject() != null;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SetPropertyAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<SetPropertyAction>() {
			@Override
			public boolean run(ActionEvent e, SetPropertyAction action) {

				/*	if (!updatedMethod.isResolvable()) {
						throw new UnresolvedTypesException(updatedMethod.getUnresolvedTypes()) {
							public void performNewAttemptAfterTypeResolving() {
								logger.info("Perform new attempt");
							}

						};
					}*/

				if (action.getFocusedObject() instanceof WORepository && action.getKey().equals(WORepository.CUSTOM_COMPONENT_ENTITY_KEY)) {

					ComponentRepository rep = ((WORepository) action.getFocusedObject()).getDMModel().getComponentRepository();
					Vector<DMEntity> nullParentTypeEntities = new Vector<DMEntity>();
					for (DMEntity entity : rep.getEntities().values()) {
						if (entity.getParentType() == null || entity.getParentType().getSimplifiedStringRepresentation().equals("null")) {
							nullParentTypeEntities.add(entity);
						}
					}
					if (nullParentTypeEntities.size() > 0 && action.getValue() != null) {
						final PropertyListParameter<DMEntity> concernedComponentsParam = new PropertyListParameter<DMEntity>(
								"concerned_component", "concerned_component", nullParentTypeEntities, 20, 10);
						concernedComponentsParam.addIconColumn("icon", "", 30, false);
						concernedComponentsParam.addReadOnlyTextFieldColumn("name", "name", 150, true);

						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(
								action.getFocusedObject().getProject(),
								null,
								FlexoLocalization.localizedForKey("selecting_default_component_entity"),
								"<html><center>"
										+ IconLibrary.UNFIXABLE_WARNING_ICON.getHTMLImg()
										+ "<b>&nbsp;"
										+ FlexoLocalization.localizedForKey("warning")
										+ "</b></center><br>"
										+ FlexoLocalization.localizedForKey("following_components_dont_declare_any_parent_component")
										+ "<br>"
										+ FlexoLocalization
												.localizedForKey("would_you_like_to_set_this_component_as_parent_component_(recommanded)")
										+ "</html>", concernedComponentsParam);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							for (DMEntity componentEntity : nullParentTypeEntities) {
								componentEntity.setParentType(
										DMType.makeResolvedDMType(((WORepository) action.getFocusedObject()).getCustomComponentEntity()),
										true);
							}
						}
					}

					return true;
				}

				return true;

			}
		};
	}

	@Override
	protected FlexoExceptionHandler<SetPropertyAction> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<SetPropertyAction>() {
			@Override
			public boolean handleException(final FlexoException exception, final SetPropertyAction action) {
				if (exception instanceof EOAccessException) {
					if (action.getFocusedObject() instanceof DMEORelationship) {
						if (action.getKey().equals("destinationEntity")) {
							FlexoController.showError(exception.getMessage());
							((DMEORelationship) action.getFocusedObject()).setDestinationEntity(null);
							return true;
						}
					} else if (action.getFocusedObject() instanceof DMEOJoin) {
						if (action.getKey().equals("sourceAttribute")) {
							FlexoController.showError(exception.getMessage());
							try {
								((DMEOJoin) action.getFocusedObject()).setSourceAttribute(null);
							} catch (EOAccessException e) {
							} catch (InvalidJoinException e) {
							}
							return true;
						}
						if (action.getFocusedObject().equals("destinationAttribute")) {
							FlexoController.showError(exception.getMessage());
							try {
								((DMEOJoin) action.getFocusedObject()).setDestinationAttribute(null);
							} catch (EOAccessException e) {
							} catch (InvalidJoinException e) {
							}
							return true;
						}
					} else {
						FlexoController.showError(exception.getMessage());
						return true;
					}
				}

				if (action.getFocusedObject() instanceof DMMethod) {
					if (exception instanceof ParserNotInstalledException) {
						FlexoController.showError(FlexoLocalization.localizedForKey("sorry_java_parser_not_installed"));
						return true;
					} else if (exception instanceof JavaParseException) {
						FlexoController.showError(FlexoLocalization
								.localizedForKey("sorry_this_code_could_not_be_parsed_as_valid_java_code"));
						return true;
					} else if (exception instanceof DuplicateMethodSignatureException) {
						exception.printStackTrace();
						FlexoController.showError(FlexoLocalization
								.localizedForKey("sorry_this_signature_matches_an_other_method_signature"));
						return true;
					}
				}

				exception.printStackTrace();
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						FlexoController.notify(FlexoLocalization.localizedForKey("could_not_set_property")
								+ " "
								+ (action.getLocalizedPropertyName() != null ? "'" + action.getLocalizedPropertyName() + "' " : "")
								+ FlexoLocalization.localizedForKey("to")
								+ " "
								+ (action.getValue() == null || action.getValue().equals("") ? FlexoLocalization
										.localizedForKey("empty_value") : action.getValue())
								+ (exception.getLocalizedMessage() != null ? "\n(" + FlexoLocalization.localizedForKey("details: ")
										+ exception.getLocalizedMessage() + ")" : ""));
					}
				});
				return true;
			}
		};
	}

	/*	private void tryToResolveEntities(SetPropertyAction action, UnresolvedTypesException e)
		{
			final DMEntityParameter[] params = new DMEntityParameter[e.getUnresolvedTypes().size()];
			int i= 0;
			for (DMType type : e.getUnresolvedTypes()) {
				Vector<DMEntity> proposals = action.getFocusedObject().getProject().getDataModel().getDMEntitiesWithName(type.getName());
				params[i++] = new DMEntityParameter(type.getName(),type.getName(),(proposals!=null&&proposals.size()>0?proposals.firstElement():null));
			}
			AskParametersDialog dialog = new AskParametersDialog(getProject(),
					FlexoLocalization.localizedForKey("type_resolving"),
					(e.getUnresolvedTypes().size()>1?FlexoLocalization.localizedForKey("some_types_were_not_resolved_dialog"):FlexoLocalization.localizedForKey("unresolved_type_dialog")),
					(ParameterDefinition[])params) {
				protected boolean isValidateEnabled()
				{
					for (DMEntityParameter param : params) {
						if (param.getValue() == null) return false;
					}
					return true;
				}
			};
			if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
				// TODO: Mettre les types a jour ici
				e.performNewAttemptAfterTypeResolving();
			}
		}*/
}
