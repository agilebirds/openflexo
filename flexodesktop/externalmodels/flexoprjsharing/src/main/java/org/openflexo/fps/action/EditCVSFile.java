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
package org.openflexo.fps.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSFile.FileContentEditor;
import org.openflexo.fps.FPSObject;

public class EditCVSFile extends CVSAction<EditCVSFile, CVSFile> {

	private static final Logger logger = Logger.getLogger(EditCVSFile.class.getPackage().getName());

	public static FlexoActionType<EditCVSFile, CVSFile, FPSObject> actionType = new FlexoActionType<EditCVSFile, CVSFile, FPSObject>(
			"edit_file", EDITION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public EditCVSFile makeNewAction(CVSFile focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
			return new EditCVSFile(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CVSFile object, Vector<FPSObject> globalSelection) {
			return (object != null && object.getSharedProject() != null);
		}

		@Override
		public boolean isEnabledForSelection(CVSFile object, Vector<FPSObject> globalSelection) {
			return ((object != null) && (object.hasVersionOnDisk()) && (!object.isEdited()));
		}

	};

	static {
		FlexoModelObject.addActionForClass(EditCVSFile.actionType, CVSFile.class);
	}

	EditCVSFile(CVSFile focusedObject, Vector<FPSObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private FileContentEditor fileContentEditor;

	@Override
	protected void doAction(Object context) {
		logger.info("Edit file " + getFocusedObject().getFileName());
		if ((getFocusedObject() != null) && (fileContentEditor != null)) {
			getFocusedObject().edit(fileContentEditor);
		}
	}

	public FileContentEditor getFileContentEditor() {
		return fileContentEditor;
	}

	public void setFileContentEditor(FileContentEditor fileContentEditor) {
		this.fileContentEditor = fileContentEditor;
	}

}
