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

public class RemoveTemplateFile extends FlexoAction<RemoveTemplateFile, CGTemplateFile, CGTemplateFile> {

	private static final Logger logger = Logger.getLogger(RemoveTemplateFile.class.getPackage().getName());

	public static FlexoActionType<RemoveTemplateFile, CGTemplateFile, CGTemplateFile> actionType = new FlexoActionType<RemoveTemplateFile, CGTemplateFile, CGTemplateFile>(
			"remove_template", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RemoveTemplateFile makeNewAction(CGTemplateFile focusedObject, Vector<CGTemplateFile> globalSelection, FlexoEditor editor) {
			return new RemoveTemplateFile(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplateFile object, Vector<CGTemplateFile> globalSelection) {
			return object != null && object.isCustomTemplate();
		}

		@Override
		public boolean isEnabledForSelection(CGTemplateFile object, Vector<CGTemplateFile> globalSelection) {
			return object != null && object.isCustomTemplate();
		}

	};

	static {
		FlexoModelObject.addActionForClass(RemoveTemplateFile.actionType, CGTemplateFile.class);
	}

	private TemplateFileContentEditor templateFileContentEditor;

	RemoveTemplateFile(CGTemplateFile focusedObject, Vector<CGTemplateFile> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Remove CustomTemplateFile " + getFocusedObject());
		deleteTemplate(getFocusedObject());
		if (getGlobalSelection() != null) {
			for (FlexoModelObject file : getGlobalSelection()) {
				if (file instanceof CGTemplateFile) {
					deleteTemplate((CGTemplateFile) file);
				}
			}
		}
	}

	private void deleteTemplate(CGTemplateFile file) {
		if (file.isEdited()) {
			file.cancelEdition();
		}
		file.delete();
	}

	public TemplateFileContentEditor getTemplateFileContentEditor() {
		return templateFileContentEditor;
	}

	public void setTemplateFileContentEditor(TemplateFileContentEditor templateFileContentEditor) {
		this.templateFileContentEditor = templateFileContentEditor;
	}

}
