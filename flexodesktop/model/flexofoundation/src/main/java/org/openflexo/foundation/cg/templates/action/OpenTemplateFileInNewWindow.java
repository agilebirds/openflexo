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
package org.openflexo.foundation.cg.templates.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateObject;

public class OpenTemplateFileInNewWindow extends FlexoGUIAction<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(OpenTemplateFileInNewWindow.class.getPackage().getName());

	public static FlexoActionType<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject> actionType = new FlexoActionType<OpenTemplateFileInNewWindow, CGTemplate, CGTemplateObject>(
			"open_in_new_window", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public OpenTemplateFileInNewWindow makeNewAction(CGTemplate focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new OpenTemplateFileInNewWindow(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplate object, Vector<CGTemplateObject> globalSelection) {
			return object != null;
		}

		@Override
		public boolean isEnabledForSelection(CGTemplate object, Vector<CGTemplateObject> globalSelection) {
			return object != null && (!(object instanceof CGTemplateFile) || !((CGTemplateFile) object).isEdited());
		}

	};

	static {
		FlexoModelObject.addActionForClass(OpenTemplateFileInNewWindow.actionType, CGTemplate.class);
	}

	OpenTemplateFileInNewWindow(CGTemplate focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
