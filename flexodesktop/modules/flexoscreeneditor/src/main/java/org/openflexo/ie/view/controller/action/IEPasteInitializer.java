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
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.action.IEPaste;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class IEPasteInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	IEPasteInitializer(IEControllerActionInitializer actionInitializer) {
		super(IEPaste.actionType, actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() {
		return (IEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<IEPaste> getDefaultInitializer() {
		return new FlexoActionInitializer<IEPaste>() {
			@Override
			public boolean run(EventObject e, IEPaste action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<IEPaste> getDefaultFinalizer() {
		return new FlexoActionFinalizer<IEPaste>() {
			@Override
			public boolean run(EventObject e, IEPaste action) {
				getControllerActionInitializer().getIESelectionManager().performSelectionPaste();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionEnableCondition getEnableCondition() {
		return new FlexoActionEnableCondition<IEPaste, IEObject, IEObject>() {
			@Override
			public boolean isEnabled(FlexoActionType<IEPaste, IEObject, IEObject> actionType, IEObject object,
					Vector<IEObject> globalSelection, FlexoEditor editor) {
				return getControllerActionInitializer().getIESelectionManager().hasCopiedData();
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
