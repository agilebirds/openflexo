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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.action.AddTab;
import org.openflexo.foundation.ie.action.DuplicateComponentAction;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IESequenceTab;
import org.openflexo.foundation.ie.widget.IETabWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.IEPanel;
import org.openflexo.ie.view.controller.IEController;
import org.openflexo.ie.view.popups.AskNewTabDialog;
import org.openflexo.ie.view.widget.IETabContainerWidgetView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.EmptyVector;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class AddTabInitializer extends ActionInitializer<AddTab, IEWidget, IEWidget> {

	AddTabInitializer(IEControllerActionInitializer actionInitializer) {
		super(AddTab.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddTab> getDefaultInitializer() {
		return new FlexoActionInitializer<AddTab>() {
			@Override
			public boolean run(ActionEvent e, AddTab action) {
				IESequenceTab tabContainer = null;
				if (action.getFocusedObject() instanceof IESequenceTab) {
					tabContainer = (IESequenceTab) action.getFocusedObject();
				} else if (action.getFocusedObject() instanceof IETabWidget) {
					tabContainer = (IESequenceTab) ((IETabWidget) action.getFocusedObject()).getParent();
				}
				if (tabContainer == null) {
					return false;
				} else {
					tabContainer = tabContainer.getRootParent();
				}
				IEPanel tabPane = ((IEController) getController()).getComponentForWidgetInCurrentComponent(tabContainer);
				if (tabPane instanceof IETabContainerWidgetView) {
					action.setTabIndex(((IETabContainerWidgetView) tabPane).getSelectedIndex() + 1);
				}
				AskNewTabDialog newTabDialog = new AskNewTabDialog(getControllerActionInitializer().getIEController().getProject()
						.getFlexoComponentLibrary());
				if (newTabDialog.getStatus() == AskNewTabDialog.VALIDATE_NEW_TAB) {
					String newThumbWOName = newTabDialog.getTabName();// FlexoController.askForString(FlexoLocalization.localizedForKey("enter_a_component_name_for_the_new_tab"));
					if (!TabComponentDefinition.isAValidNewTabName(newThumbWOName, getControllerActionInitializer().getIEController()
							.getProject())) {
						FlexoController.showError(FlexoLocalization
								.localizedForKey("invalid_name_a_component_with_this_name_already_exists"));
						return false;
					}

					if (newThumbWOName == null || newThumbWOName.equals("")
							|| !IERegExp.JAVA_CLASS_NAME_PATTERN.matcher(newThumbWOName).matches()) {
						FlexoController.showError(FlexoLocalization
								.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
						return false;
					}
				}
				FlexoComponentFolder selectedFolder = getControllerActionInitializer().getIEController().getProject()
						.getFlexoComponentLibrary().getRootFolder();
				action.setFolder(selectedFolder);
				action.setTabTitle(newTabDialog.getTabTitle());
				action.setTabContainer(tabContainer);
				boolean executeAction = false;
				if (newTabDialog.getStatus() == AskNewTabDialog.VALIDATE_NEW_TAB) {
					action.setTabName(newTabDialog.getTabName());
					executeAction = true;
				} else if (newTabDialog.getStatus() == AskNewTabDialog.VALIDATE_EXISTING_TAB) {
					ComponentDefinition compDef = getControllerActionInitializer().getIEController().getProject()
							.getFlexoComponentLibrary().getComponentNamed(newTabDialog.getTabName());
					Enumeration<IETabWidget> en = tabContainer.getAllTabs().elements();
					boolean alreadyInContainer = false;
					while (en.hasMoreElements()) {
						IETabWidget tab = en.nextElement();
						if (tab.getTabComponentDefinition() == compDef) {
							alreadyInContainer = true;
						}
					}
					if (alreadyInContainer) {
						if (FlexoController.confirm("add_tab_already_in_tab_container")) {
							DuplicateComponentAction dupAction = (DuplicateComponentAction) DuplicateComponentAction.actionType
									.makeNewAction(compDef, EmptyVector.EMPTY_VECTOR, action.getEditor());
							dupAction.doAction();
							compDef = dupAction.getComponentDefinition();
							if (compDef == null) {
								return false;
							}
						} else {
							return false;
						}
					}
					action.setTabDef((TabComponentDefinition) compDef);
					executeAction = true;
				}
				return executeAction;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return SEIconLibrary.THUMBNAILCONTAINER_ICON;
	}

	@Override
	protected FlexoExceptionHandler<AddTab> getDefaultExceptionHandler() {
		return new FlexoExceptionHandler<AddTab>() {
			@Override
			public boolean handleException(FlexoException exception, AddTab action) {
				if (exception.getCause() != null && exception.getCause() instanceof DuplicateResourceException) {
					FlexoController.showError(exception.getMessage());
					return true;
				} else {
					exception.printStackTrace();
				}
				return true;
			}
		};
	}

}
