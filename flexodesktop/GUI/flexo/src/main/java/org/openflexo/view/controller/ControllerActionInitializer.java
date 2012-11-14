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
package org.openflexo.view.controller;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.help.BadIDException;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.action.HelpAction;
import org.openflexo.action.InspectAction;
import org.openflexo.action.ProjectExcelExportInitializer;
import org.openflexo.action.SubmitDocumentationAction;
import org.openflexo.action.UploadPrjInitializer;
import org.openflexo.components.AskParametersDialog;
import org.openflexo.dre.SubmitNewVersionPopup;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemAction;
import org.openflexo.drm.DocItemVersion;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.drm.action.SubmitVersion;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoProperty;
import org.openflexo.foundation.action.AddFlexoProperty;
import org.openflexo.foundation.action.DeleteFlexoProperty;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoActionizer;
import org.openflexo.foundation.action.SortFlexoProperties;
import org.openflexo.foundation.param.DynamicDropDownParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ReadOnlyTextFieldParameter;
import org.openflexo.foundation.param.TextAreaParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.help.FlexoHelp;
import org.openflexo.icon.IconLibrary;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.view.controller.action.AddFlexoPropertyActionizer;
import org.openflexo.view.controller.action.DeleteFlexoPropertyActionizer;
import org.openflexo.view.controller.action.RefreshImportedProcessesActionInitializer;
import org.openflexo.view.controller.action.RefreshImportedRolesActionInitializer;
import org.openflexo.view.controller.action.SortFlexoPropertiesActionizer;

public class ControllerActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	private final FlexoController _controller;

	protected ControllerActionInitializer(FlexoController controller) {
		super();
		_controller = controller;
	}

	public FlexoController getController() {
		return _controller;
	}

	public FlexoModule getModule() {
		return getController().getModule();
	}

	public FlexoProject getProject() {
		return getController().getProject();
	}

	public void initializeActions() {
		initializeHelpAction();
		initializeSubmitDocumentationAction();
		initializeInspectAction();
		initializeExcelAction();
		initializeFlexoPropertyActions();
		new RefreshImportedRolesActionInitializer(this).init();
		new RefreshImportedProcessesActionInitializer(this).init();
		new UploadPrjInitializer(this).init();
	}

	private void initializeFlexoPropertyActions() {
		new AddFlexoPropertyActionizer(this).init();
		new DeleteFlexoPropertyActionizer(this).init();
		new SortFlexoPropertiesActionizer(this).init();
		if (FlexoModelObject.addFlexoPropertyActionizer == null) {
			FlexoModelObject.addFlexoPropertyActionizer = new FlexoActionizer<AddFlexoProperty, FlexoModelObject, FlexoModelObject>(
					AddFlexoProperty.actionType, getController().getEditor());
		}
		if (FlexoModelObject.sortFlexoPropertiesActionizer == null) {
			FlexoModelObject.sortFlexoPropertiesActionizer = new FlexoActionizer<SortFlexoProperties, FlexoModelObject, FlexoModelObject>(
					SortFlexoProperties.actionType, getController().getEditor());
		}
		if (FlexoModelObject.deleteFlexoPropertyActionizer == null) {
			FlexoModelObject.deleteFlexoPropertyActionizer = new FlexoActionizer<DeleteFlexoProperty, FlexoProperty, FlexoProperty>(
					DeleteFlexoProperty.actionType, getController().getEditor());
		}
	}

	/**
     * 
     */
	private void initializeExcelAction() {
		new ProjectExcelExportInitializer(this).init();
	}

	protected void initializeHelpAction() {
		getController().getEditor().registerInitializerFor(HelpAction.actionType, new FlexoActionInitializer<HelpAction>() {
			@Override
			public boolean run(ActionEvent e, HelpAction action) {
				return action.getFocusedObject() instanceof InspectableObject;
			}
		}, getController().getModule());
		getController().getEditor().registerFinalizerFor(HelpAction.actionType, new FlexoActionFinalizer<HelpAction>() {
			@Override
			public boolean run(ActionEvent e, HelpAction action) {
				if (action.getFocusedObject() instanceof InspectableObject) {
					DocItem item = DocResourceManager.instance().getDocItemFor((InspectableObject) action.getFocusedObject());
					if (item != null) {
						try {
							logger.info("Trying to display help for " + item.getIdentifier());
							FlexoHelp.getHelpBroker().setCurrentID(item.getIdentifier());
							FlexoHelp.getHelpBroker().setDisplayed(true);
						} catch (BadIDException exception) {
							FlexoController.showError(FlexoLocalization.localizedForKey("sorry_no_help_available_for") + " "
									+ item.getIdentifier());
							return false;
						}
						return true;
					}
				}
				return false;
			}
		}, getController().getModule());
		registerAction(HelpAction.actionType, null, IconLibrary.HELP_ICON);
	}

	protected void initializeSubmitDocumentationAction() {
		getController().getEditor().registerInitializerFor(SubmitDocumentationAction.actionType,
				new FlexoActionInitializer<SubmitDocumentationAction>() {
					@Override
					public boolean run(ActionEvent e, SubmitDocumentationAction anAction) {
						if (!(anAction.getFocusedObject() instanceof InspectableObject)) {
							return false;
						}
						DocItem docItem;
						if (anAction.getFocusedObject() instanceof DocItem) {
							docItem = (DocItem) anAction.getFocusedObject();
						} else {
							docItem = DocResourceManager.instance().getDocItemFor((InspectableObject) anAction.getFocusedObject());
						}
						if (docItem == null) {
							return false;
						}
						Language language = null;
						if (docItem.getDocResourceCenter().getLanguages().size() > 1) {
							ParameterDefinition[] langParams = new ParameterDefinition[1];
							langParams[0] = new DynamicDropDownParameter("language", "language", docItem.getDocResourceCenter()
									.getLanguages(), docItem.getDocResourceCenter().getLanguages().firstElement());
							langParams[0].addParameter("format", "name");
							AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null,
									FlexoLocalization.localizedForKey("choose_language"),
									FlexoLocalization.localizedForKey("define_submission_language"), langParams);
							if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
								language = (Language) dialog.parameterValueWithName("language");
							} else {
								return false;
							}
						} else if (docItem.getDocResourceCenter().getLanguages().size() == 1) {
							language = docItem.getDocResourceCenter().getLanguages().firstElement();
						}
						if (language == null) {
							return false;
						}
						SubmitVersion action = (SubmitVersion) SubmitVersion.actionType.makeNewAction(docItem, null, DocResourceManager
								.instance().getEditor());
						SubmitNewVersionPopup editVersionPopup = new SubmitNewVersionPopup(action.getDocItem(), language, getController()
								.getFlexoFrame(), DocResourceManager.instance().getEditor());
						action.setVersion(editVersionPopup.getVersionToSubmit());
						if (action.getVersion() == null) {
							return false;
						}
						String title;
						DocItemAction lastAction = action.getDocItem().getLastActionForLanguage(action.getVersion().getLanguage());
						if (lastAction == null) {
							title = FlexoLocalization.localizedForKey("submit_documentation");
						} else {
							title = FlexoLocalization.localizedForKey("review_documentation");
							action.getVersion().setVersion(
									DocItemVersion.Version.versionByIncrementing(lastAction.getVersion().getVersion(), 0, 0, 1));
						}
						ParameterDefinition[] parameters = new ParameterDefinition[4];
						parameters[0] = new ReadOnlyTextFieldParameter("user", "username", DocResourceManager.instance().getUser()
								.getIdentifier());
						parameters[1] = new ReadOnlyTextFieldParameter("language", "language", action.getVersion().getLanguageId());
						parameters[2] = new TextFieldParameter("version", "version", action.getVersion().getVersion().toString());
						parameters[3] = new TextAreaParameter("note", "note", "", 25, 3);
						AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(getProject(), null, title,
								FlexoLocalization.localizedForKey("define_submission_parameters"), parameters);
						if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
							action.setAuthor(DocResourceManager.instance().getUser());
							String versionId = (String) dialog.parameterValueWithName("version");
							action.getVersion().setVersion(new DocItemVersion.Version(versionId));
							action.setNote((String) dialog.parameterValueWithName("note"));
							anAction.setContext(action);
							action.setContext(anAction);
							return true;
						} else {
							return false;
						}
					}
				}, getController().getModule());
		getController().getEditor().registerFinalizerFor(SubmitDocumentationAction.actionType,
				new FlexoActionFinalizer<SubmitDocumentationAction>() {
					@Override
					public boolean run(ActionEvent e, SubmitDocumentationAction action) {
						if (action.getContext() != null && action.getContext() instanceof SubmitVersion) {
							((SubmitVersion) action.getContext()).doAction();
							FlexoController.notify(FlexoLocalization.localizedForKey("submission_has_been_successfully_recorded"));
							return true;
						}
						return false;
					}
				}, getController().getModule());
		registerAction(SubmitDocumentationAction.actionType, null, IconLibrary.HELP_ICON);
	}

	private void initializeInspectAction() {
		getController().getEditor().registerInitializerFor(InspectAction.actionType, new FlexoActionInitializer<InspectAction>() {
			@Override
			public boolean run(ActionEvent e, InspectAction action) {
				// return getModule().isActive();
				logger.info("Action InspectAction, initializer for " + getModule());
				return true;
			}
		}, getController().getModule());
		getController().getEditor().registerFinalizerFor(InspectAction.actionType, new FlexoActionFinalizer<InspectAction>() {
			@Override
			public boolean run(ActionEvent e, InspectAction action) {
				getController().showInspector();
				return true;
			}
		}, getController().getModule());
		registerAction(InspectAction.actionType, KeyStroke.getKeyStroke(KeyEvent.VK_I, FlexoCst.META_MASK), IconLibrary.INSPECT_ICON);

	}

	protected void registerAction(FlexoActionType actionType, KeyStroke keyStroke, Icon enabledIcon, Icon disabledIcon) {
		getController().getEditor().registerAction(actionType);
		getController().getEditor().registerIcons(actionType, enabledIcon, disabledIcon);
		if (keyStroke != null) {
			actionType.setKeyStroke(keyStroke);
			getController().registerActionForKeyStroke(actionType, keyStroke);
		}
	}

	protected void registerAction(FlexoActionType actionType, KeyStroke keyStroke, Icon icon) {
		registerAction(actionType, keyStroke, icon, null);
	}

	public void registerAction(FlexoActionType actionType, KeyStroke keyStroke) {
		registerAction(actionType, keyStroke, null);
	}

	protected void registerAction(FlexoActionType actionType) {
		registerAction(actionType, null, null);
	}
}