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
package org.openflexo.foundation.cg.version.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.rm.cg.ContentSource;

public class ShowDifferences extends FlexoGUIAction<ShowDifferences, CGObject, CGObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowDifferences.class.getPackage().getName());

	public static FlexoActionType<ShowDifferences, CGObject, CGObject> actionType = new FlexoActionType<ShowDifferences, CGObject, CGObject>(
			"show_differences", AbstractGCAction.versionningMenu, AbstractGCAction.versionningShowGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ShowDifferences makeNewAction(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ShowDifferences(focusedObject, globalSelection, editor);
		}

		@Override
		protected boolean isVisibleForSelection(CGObject object, Vector<CGObject> globalSelection) {
			return ((object != null) && ((object instanceof CGFile) || (object instanceof AbstractCGFileVersion)));
		}

		@Override
		protected boolean isEnabledForSelection(CGObject object, Vector<CGObject> globalSelection) {
			if (object == null)
				return false;
			if (object instanceof CGFile)
				return (((CGFile) object).getRepository().getManageHistory());
			if (object instanceof AbstractCGFileVersion)
				return (((AbstractCGFileVersion) object).getCGFile().getRepository().getManageHistory());
			return false;
		}

	};

	static {
		FlexoModelObject.addActionForClass(ShowDifferences.actionType, CGFile.class);
		FlexoModelObject.addActionForClass(ShowDifferences.actionType, AbstractCGFileVersion.class);
	}

	ShowDifferences(CGObject focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private ContentSource _leftSource;
	private ContentSource _rightSource;

	public ContentSource getLeftSource() {
		return _leftSource;
	}

	public void setLeftSource(ContentSource leftSource) {
		_leftSource = leftSource;
	}

	public ContentSource getRightSource() {
		return _rightSource;
	}

	public void setRightSource(ContentSource rightSource) {
		_rightSource = rightSource;
	}

	public CGFile getCGFile() {
		if (getFocusedObject() instanceof CGFile)
			return (CGFile) getFocusedObject();
		if (getFocusedObject() instanceof AbstractCGFileVersion)
			return ((AbstractCGFileVersion) getFocusedObject()).getCGFile();
		return null;
	}

}
