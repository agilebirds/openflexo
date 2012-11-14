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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.generator.file.AbstractCGFile;

public class RevertToSavedGeneratedFile extends GCAction<RevertToSavedGeneratedFile, CGFile> {

	private static final Logger logger = Logger.getLogger(RevertToSavedGeneratedFile.class.getPackage().getName());

	public static FlexoActionType<RevertToSavedGeneratedFile, CGFile, CGObject> actionType = new FlexoActionType<RevertToSavedGeneratedFile, CGFile, CGObject>(
			"revert_to_saved_file", EDITION_MENU, FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RevertToSavedGeneratedFile makeNewAction(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new RevertToSavedGeneratedFile(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGFile object, Vector<CGObject> globalSelection) {
			return object instanceof AbstractCGFile;
		}

		@Override
		protected boolean isEnabledForSelection(CGFile object, Vector<CGObject> globalSelection) {
			return object != null && object.hasVersionOnDisk() && object.isEdited();
		}

	};

	static {
		FlexoModelObject.addActionForClass(RevertToSavedGeneratedFile.actionType, CGFile.class);
	}

	RevertToSavedGeneratedFile(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Revert to saved file " + getFocusedObject().getFileName());
		if (getFocusedObject() != null) {
			getFocusedObject().revertToSaved();
		}
	}

}
