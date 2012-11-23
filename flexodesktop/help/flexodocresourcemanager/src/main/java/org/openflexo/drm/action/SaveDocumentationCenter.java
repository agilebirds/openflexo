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
package org.openflexo.drm.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.drm.DRMObject;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;

public class SaveDocumentationCenter extends FlexoAction<SaveDocumentationCenter, DRMObject, DRMObject> {

	private static final Logger logger = Logger.getLogger(SaveDocumentationCenter.class.getPackage().getName());

	public static FlexoActionType<SaveDocumentationCenter, DRMObject, DRMObject> actionType = new FlexoActionType<SaveDocumentationCenter, DRMObject, DRMObject>(
			"save_documentation_center", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public SaveDocumentationCenter makeNewAction(DRMObject focusedObject, Vector<DRMObject> globalSelection, FlexoEditor editor) {
			return new SaveDocumentationCenter(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(DRMObject object, Vector<DRMObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(DRMObject object, Vector<DRMObject> globalSelection) {
			return true;
		}

	};

	SaveDocumentationCenter(DRMObject focusedObject, Vector<DRMObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) {
		logger.info("SaveDocumentationCenter");
		DocResourceManager.instance().save();
	}

}
