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
package org.openflexo.wkf.controller;

import java.util.EventObject;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.wkf.view.ExternalProcessViewWindow;

public class OpenProcessInNewWindow extends FlexoGUIAction<OpenProcessInNewWindow, FlexoProcess, WKFObject> {

	protected static final Logger logger = Logger.getLogger(OpenProcessInNewWindow.class.getPackage().getName());

	public static FlexoActionType<OpenProcessInNewWindow, FlexoProcess, WKFObject> actionType = new FlexoActionType<OpenProcessInNewWindow, FlexoProcess, WKFObject>(
			"open_process_in_new_window", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public OpenProcessInNewWindow makeNewAction(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
			return new OpenProcessInNewWindow(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) {
			return !object.isImported();
		}

		@Override
		public boolean isEnabledForSelection(FlexoProcess object, Vector<WKFObject> globalSelection) {
			return (object != null);
		}

	};

	static {
		FlexoModelObject.addActionForClass(OpenProcessInNewWindow.actionType, FlexoProcess.class);
	}

	OpenProcessInNewWindow(FlexoProcess focusedObject, Vector<WKFObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public static void initWithController(final WKFController controller) {
		controller.getEditor().registerInitializerFor(actionType, new FlexoActionInitializer<OpenProcessInNewWindow>() {
			@Override
			public boolean run(EventObject e, OpenProcessInNewWindow anAction) {
				if (anAction.getFocusedObject() == null) {
					anAction.setFocusedObject(controller.getCurrentFlexoProcess());
				}
				return (anAction.getFocusedObject() != null);
			}
		}, controller.getModule());

		controller.getEditor().registerFinalizerFor(actionType, new FlexoActionFinalizer<OpenProcessInNewWindow>() {
			@Override
			public boolean run(EventObject e, final OpenProcessInNewWindow anAction) {
				ExternalProcessViewWindow window = new ExternalProcessViewWindow(controller, anAction.getFocusedObject());
				window.setVisible(true);
				return true;
			}
		}, controller.getModule());

	}

}
