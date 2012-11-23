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

import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.DMSelectAll;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DMSelectAllInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DMSelectAllInitializer(DMControllerActionInitializer actionInitializer) {
		super(DMSelectAll.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DMSelectAll> getDefaultInitializer() {
		return new FlexoActionInitializer<DMSelectAll>() {
			@Override
			public boolean run(EventObject e, DMSelectAll action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DMSelectAll> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DMSelectAll>() {
			@Override
			public boolean run(EventObject e, DMSelectAll action) {
				getControllerActionInitializer().getSelectionManager().performSelectionSelectAll();
				return true;
			}
		};
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_A, FlexoCst.META_MASK);
	}

}
