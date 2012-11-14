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

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.ModelReinjectableFile;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.generator.file.AbstractCGFile;

public class OpenDMEntity extends FlexoGUIAction<OpenDMEntity, CGFile, CGObject> {

	public static FlexoActionType<OpenDMEntity, CGFile, CGObject> actionType = new FlexoActionType<OpenDMEntity, CGFile, CGObject>(
			"open_entity_in_dm", GCAction.MODEL_MENU, GCAction.MODEL_GROUP2, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public OpenDMEntity makeNewAction(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new OpenDMEntity(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGFile file, Vector<CGObject> globalSelection) {
			return file instanceof AbstractCGFile;
		}

		@Override
		protected boolean isEnabledForSelection(CGFile file, Vector<CGObject> globalSelection) {
			return file.getResource() != null && file instanceof ModelReinjectableFile && file.supportModelReinjection();
		}

	};

	static {
		FlexoModelObject.addActionForClass(OpenDMEntity.actionType, CGFile.class);
	}

	OpenDMEntity(CGFile focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public OpenDMEntity(CGFile operationNode, FlexoEditor editor) {
		super(actionType, operationNode, null, editor);
	}

	public DMEntity getModelEntity() {
		if (getFocusedObject() instanceof ModelReinjectableFile) {
			return ((ModelReinjectableFile) getFocusedObject()).getModelEntity();
		}
		return null;
	}

}
