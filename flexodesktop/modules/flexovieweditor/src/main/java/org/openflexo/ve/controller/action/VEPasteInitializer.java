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
import org.openflexo.foundation.action.FlexoActionVisibleCondition;
import org.openflexo.foundation.view.action.VEPaste;
import org.openflexo.foundation.view.diagram.model.ViewObject;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ve.controller.VEController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class VEPasteInitializer extends ActionInitializer<VEPaste, ViewObject, ViewObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	VEPasteInitializer(VEControllerActionInitializer actionInitializer) {
		super(VEPaste.actionType, actionInitializer);
	}

	@Override
	protected VEControllerActionInitializer getControllerActionInitializer() {
		return (VEControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<VEPaste> getDefaultInitializer() {
		return new FlexoActionInitializer<VEPaste>() {
			@Override
			public boolean run(EventObject e, VEPaste action) {
				return true;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<VEPaste> getDefaultFinalizer() {
		return new FlexoActionFinalizer<VEPaste>() {
			@Override
			public boolean run(EventObject e, VEPaste action) {
				getControllerActionInitializer().getVESelectionManager().performSelectionPaste();
				return true;
			}
		};
	}

	@Override
	protected FlexoActionEnableCondition<VEPaste, ViewObject, ViewObject> getEnableCondition() {
		return new FlexoActionEnableCondition<VEPaste, ViewObject, ViewObject>() {
			@Override
			public boolean isEnabled(FlexoActionType<VEPaste, ViewObject, ViewObject> actionType, ViewObject object,
					Vector<ViewObject> globalSelection, FlexoEditor editor) {
				return getControllerActionInitializer().getVESelectionManager().hasCopiedData();
			}
		};
	}

	@Override
	public VEController getController() {
		return (VEController) super.getController();
	}

	@Override
	protected Icon getEnabledIcon() {
		return IconLibrary.PASTE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_V, FlexoCst.META_MASK);
	}

	@Override
	protected FlexoActionVisibleCondition<VEPaste, ViewObject, ViewObject> getVisibleCondition() {
		return new FlexoActionVisibleCondition<VEPaste, ViewObject, ViewObject>() {

			@Override
			public boolean isVisible(FlexoActionType<VEPaste, ViewObject, ViewObject> actionType, ViewObject object,
					Vector<ViewObject> globalSelection, FlexoEditor editor) {
				return true;
			}

		};
	}
}
