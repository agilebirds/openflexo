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
package org.openflexo.view.controller.action;

import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.action.InspectAction;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class InspectActionizer extends ActionInitializer<InspectAction, FlexoModelObject, FlexoModelObject> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(InspectActionizer.class.getPackage()
			.getName());

	public InspectActionizer(ControllerActionInitializer actionInitializer) {
		super(InspectAction.actionType, actionInitializer);
	}

	@Override
	protected FlexoActionFinalizer<InspectAction> getDefaultFinalizer() {
		return new FlexoActionFinalizer<InspectAction>() {
			@Override
			public boolean run(EventObject e, InspectAction action) {
				getController().showInspector();
				return true;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_I, FlexoCst.META_MASK);
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.INSPECT_ICON;
	}
}
