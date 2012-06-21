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

import java.util.EventObject;
import java.util.logging.Logger;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.IERegExp;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.action.DuplicateComponentAction;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class DuplicateComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DuplicateComponentInitializer(IEControllerActionInitializer actionInitializer) {
		super(DuplicateComponentAction.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DuplicateComponentAction> getDefaultInitializer() {
		return new FlexoActionInitializer<DuplicateComponentAction>() {
			@Override
			public boolean run(EventObject e, DuplicateComponentAction action) {
				IEWOComponent c;
				if (action.getFocusedObject() instanceof ComponentDefinition) {
					c = ((ComponentDefinition) action.getFocusedObject()).getWOComponent();
				} else if (action.getFocusedObject() instanceof IEWOComponent) {
					c = (IEWOComponent) action.getFocusedObject();
				} else {
					return false;
				}
				String componentName = null;
				do {
					if (componentName != null) {
						FlexoController.notify("this_name_isalready_used_by_another_component");
					}
					componentName = FlexoController.askForStringMatchingPattern(FlexoLocalization.localizedForKey("new_component_name"),
							IERegExp.JAVA_CLASS_NAME_PATTERN,
							FlexoLocalization.localizedForKey("must_start_with_a_letter_followed_by_any_letter_or_number"));
				} while (componentName != null && getProject().getFlexoComponentLibrary().getComponentNamed(componentName) != null);
				(action).setNewComponentName(componentName);
				(action).setComponent(c);
				return (componentName != null && componentName.trim().length() > 0);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DuplicateComponentAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DuplicateComponentAction>() {
			@Override
			public boolean run(EventObject e, DuplicateComponentAction action) {
				if (action.getComponentDefinition() != null) {
					getController().setCurrentEditedObjectAsModuleView(action.getComponentDefinition().getDummyComponentInstance());
				}
				return true;
			}
		};
	}

}
