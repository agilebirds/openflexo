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
package org.openflexo.foundation.cg.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGFile;

public class CompareFiles extends FlexoGUIAction<CompareFiles, CGFile, CGFile> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CompareFiles.class.getPackage().getName());

	public static List<CGFile> getTemplates(CGFile object, Vector<CGFile> globalSelection) {
		if (globalSelection == null) {
			return Collections.emptyList();
		}
		List<CGFile> v = new ArrayList<CGFile>(globalSelection.size() + 1);
		if (globalSelection != null) {
			v.addAll(globalSelection);
		}
		if (object != null && !v.contains(object)) {
			v.add(object);
		}
		return v;
	}

	public static FlexoActionType<CompareFiles, CGFile, CGFile> actionType = new FlexoActionType<CompareFiles, CGFile, CGFile>(
			"compare_each_other", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CompareFiles makeNewAction(CGFile focusedObject, Vector<CGFile> globalSelection, FlexoEditor editor) {
			return new CompareFiles(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGFile object, Vector<CGFile> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(CGFile object, Vector<CGFile> globalSelection) {
			return getTemplates(object, globalSelection).size() == 2;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(CompareFiles.actionType, CGFile.class);
	}

	CompareFiles(CGFile focusedObject, Vector<CGFile> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public List<CGFile> getFiles() {
		return getTemplates(getFocusedObject(), getGlobalSelection());
	}

}
