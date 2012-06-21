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

public class CompareTemplatesInNewWindow extends FlexoGUIAction<CompareTemplatesInNewWindow, CGTemplate, CGTemplate> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CompareTemplatesInNewWindow.class.getPackage().getName());

	public static Vector<CGTemplate> getTemplates(CGTemplate object, Vector<CGTemplate> globalSelection) {
		Vector<CGTemplate> v = globalSelection == null ? new Vector<CGTemplate>(1) : new Vector<CGTemplate>(globalSelection.size() + 1);
		if (globalSelection != null) {
			v.addAll(globalSelection);
		}
		if (object != null && !v.contains(object)) {
			v.add(object);
		}
		return v;
	}

	public static FlexoActionType<CompareTemplatesInNewWindow, CGTemplate, CGTemplate> actionType = new FlexoActionType<CompareTemplatesInNewWindow, CGTemplate, CGTemplate>(
			"compare_each_other", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public CompareTemplatesInNewWindow makeNewAction(CGTemplate focusedObject, Vector<CGTemplate> globalSelection, FlexoEditor editor) {
			return new CompareTemplatesInNewWindow(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplate object, Vector<CGTemplate> globalSelection) {
			return isEnabledForSelection(object, globalSelection);
		}

		@Override
		public boolean isEnabledForSelection(CGTemplate object, Vector<CGTemplate> globalSelection) {
			return getTemplates(object, globalSelection).size() == 2;
		}

	};

	static {
		FlexoModelObject.addActionForClass(CompareTemplatesInNewWindow.actionType, CGTemplate.class);
	}

	CompareTemplatesInNewWindow(CGTemplate focusedObject, Vector<CGTemplate> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public Vector<CGTemplate> getTemplates() {
		return getTemplates(getFocusedObject(), getGlobalSelection());
	}

}
