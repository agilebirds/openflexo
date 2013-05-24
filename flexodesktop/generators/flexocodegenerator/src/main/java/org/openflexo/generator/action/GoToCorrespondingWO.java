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
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.rm.ComponentJavaFileResource;

/**
 * @author gpolet
 * 
 */
public class GoToCorrespondingWO extends FlexoGUIAction<GoToCorrespondingWO, CGJavaFile, CGJavaFile> {

	public static FlexoActionType<GoToCorrespondingWO, CGJavaFile, CGJavaFile> actionType = new FlexoActionType<GoToCorrespondingWO, CGJavaFile, CGJavaFile>(
			"go_to_corresponding_wo", GCAction.SHOW_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		@Override
		public boolean isEnabledForSelection(CGJavaFile object, Vector<CGJavaFile> globalSelection) {
			return object.getResource() instanceof ComponentJavaFileResource;
		}

		@Override
		public boolean isVisibleForSelection(CGJavaFile object, Vector<CGJavaFile> globalSelection) {
			return object.getResource() instanceof ComponentJavaFileResource;
		}

		@Override
		public GoToCorrespondingWO makeNewAction(CGJavaFile focusedObject, Vector<CGJavaFile> globalSelection, FlexoEditor editor) {
			return new GoToCorrespondingWO(focusedObject, globalSelection, editor);
		}

	};

	static {
		FlexoModelObject.addActionForClass(GoToCorrespondingWO.actionType, CGJavaFile.class);
	}

	/**
	 * @param actionType
	 * @param focusedObject
	 * @param globalSelection
	 * @param editor
	 */
	protected GoToCorrespondingWO(CGJavaFile focusedObject, Vector<CGJavaFile> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

}
