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
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.dm.DMObject;
import org.openflexo.foundation.dm.action.DMPaste;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class DMPasteInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DMPasteInitializer(DMControllerActionInitializer actionInitializer) {
		super(DMPaste.actionType, actionInitializer);
	}

	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() {
		return (DMControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<DMPaste> getDefaultInitializer() {
		return new FlexoActionInitializer<DMPaste>() {
			@Override
			public boolean run(EventObject e, DMPaste action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<DMPaste> getDefaultFinalizer() {
		return new FlexoActionFinalizer<DMPaste>() {
			@Override
			public boolean run(EventObject e, DMPaste action) {
				getControllerActionInitializer().getSelectionManager().performSelectionPaste();
				return true;
			}
		};
	}

	/**
	 * Please override if required Default implementation return null
	 * 
	 * @return null
	 */
	@Override
	protected FlexoActionEnableCondition getEnableCondition() {
		return new FlexoActionEnableCondition<DMPaste, DMObject, DMObject>() {
			@Override
			public boolean isEnabled(FlexoActionType<DMPaste, DMObject, DMObject> actionType, DMObject object,
					Vector<DMObject> globalSelection, FlexoEditor editor) {
				return getControllerActionInitializer().getSelectionManager().hasCopiedData();
			}

		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.PASTE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_V, FlexoCst.META_MASK);
	}

}
