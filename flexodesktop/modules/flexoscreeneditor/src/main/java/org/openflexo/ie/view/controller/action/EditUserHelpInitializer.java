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
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.view.CommentZone;
import org.openflexo.ie.view.controller.EditUserHelpAction;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class EditUserHelpInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	EditUserHelpInitializer(IEControllerActionInitializer actionInitializer) {
		super(EditUserHelpAction.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<EditUserHelpAction> getDefaultInitializer() {
		return new FlexoActionInitializer<EditUserHelpAction>() {
			@Override
			public boolean run(ActionEvent e, EditUserHelpAction action) {
				return (action.getFocusedObject() instanceof InspectableObject);
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<EditUserHelpAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<EditUserHelpAction>() {
			@Override
			public boolean run(ActionEvent e, EditUserHelpAction action) {
				if (action.getFocusedObject() instanceof IEOperationComponent) {
					CommentZone commentZone = new CommentZone(getController().getFlexoFrame(),
							(IEOperationComponent) action.getFocusedObject());
					commentZone.setVisible(true);
					return true;
				} else if (action.getFocusedObject() instanceof OperationComponentDefinition) {
					CommentZone commentZone = new CommentZone(getController().getFlexoFrame(),
							(OperationComponentDefinition) action.getFocusedObject());
					commentZone.setVisible(true);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.HELP_ICON;
	}

}
