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
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.templates.CGTemplateFile;
import org.openflexo.foundation.cg.templates.CGTemplateFile.TemplateFileContentEditor;
import org.openflexo.foundation.cg.templates.CGTemplateObject;

public class EditCustomTemplateFile extends FlexoAction<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(EditCustomTemplateFile.class.getPackage().getName());

	public static FlexoActionType<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject> actionType = new FlexoActionType<EditCustomTemplateFile, CGTemplateFile, CGTemplateObject>(
			"edit_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public EditCustomTemplateFile makeNewAction(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection,
				FlexoEditor editor) {
			return new EditCustomTemplateFile(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection) {
			return ((object != null) && (object.isCustomTemplate()));
		}

		@Override
		public boolean isEnabledForSelection(CGTemplateFile object, Vector<CGTemplateObject> globalSelection) {
			return ((object != null) && (object.isCustomTemplate()) && (!object.isEdited()));
		}

	};

	static {
		FlexoModelObject.addActionForClass(EditCustomTemplateFile.actionType, CGTemplateFile.class);
	}

	private TemplateFileContentEditor templateFileContentEditor;

	EditCustomTemplateFile(CGTemplateFile focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Edit CustomTemplateFile " + getFocusedObject());
		if ((getFocusedObject() != null) && (templateFileContentEditor != null)) {
			getFocusedObject().edit(templateFileContentEditor);
		}
	}

	public TemplateFileContentEditor getTemplateFileContentEditor() {
		return templateFileContentEditor;
	}

	public void setTemplateFileContentEditor(TemplateFileContentEditor templateFileContentEditor) {
		this.templateFileContentEditor = templateFileContentEditor;
	}

}
