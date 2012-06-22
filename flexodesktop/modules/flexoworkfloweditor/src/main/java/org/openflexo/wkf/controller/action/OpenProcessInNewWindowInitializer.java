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
package org.openflexo.wkf.controller.action;

import java.awt.event.KeyEvent;
import java.util.EventObject;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.wkf.controller.OpenProcessInNewWindow;
import org.openflexo.wkf.controller.WKFController;
import org.openflexo.wkf.view.ExternalProcessViewWindow;

public class OpenProcessInNewWindowInitializer extends ActionInitializer<OpenProcessInNewWindow, FlexoProcess, WKFObject> {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	OpenProcessInNewWindowInitializer(WKFControllerActionInitializer actionInitializer) {
		super(OpenProcessInNewWindow.actionType, actionInitializer);
	}

	@Override
	protected WKFControllerActionInitializer getControllerActionInitializer() {
		return (WKFControllerActionInitializer) super.getControllerActionInitializer();
	}

	@Override
	public WKFController getController() {
		return (WKFController) super.getController();
	}

	@Override
	protected FlexoActionInitializer<OpenProcessInNewWindow> getDefaultInitializer() {
		return new FlexoActionInitializer<OpenProcessInNewWindow>() {
			@Override
			public boolean run(EventObject e, OpenProcessInNewWindow anAction) {
				if (anAction.getFocusedObject() == null) {
					anAction.setFocusedObject(getController().getCurrentFlexoProcess());
				}
				return anAction.getFocusedObject() != null;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<OpenProcessInNewWindow> getDefaultFinalizer() {
		return new FlexoActionFinalizer<OpenProcessInNewWindow>() {
			@Override
			public boolean run(EventObject e, final OpenProcessInNewWindow anAction) {
				ExternalProcessViewWindow window = new ExternalProcessViewWindow(getController(), anAction.getFocusedObject());
				window.setVisible(true);
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() {
		return WKFIconLibrary.PROCESS_ICON;
	}

	@Override
	protected KeyStroke getShortcut() {
		return KeyStroke.getKeyStroke(KeyEvent.VK_U, FlexoCst.META_MASK);
	}

}
