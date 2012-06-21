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
import org.openflexo.foundation.cg.DuplicateCodeRepositoryNameException;
import org.openflexo.foundation.cg.templates.CGTemplateObject;

public class RefreshTemplates extends FlexoAction<RefreshTemplates, CGTemplateObject, CGTemplateObject> {

	private static final Logger logger = Logger.getLogger(RefreshTemplates.class.getPackage().getName());

	public static FlexoActionType<RefreshTemplates, CGTemplateObject, CGTemplateObject> actionType = new FlexoActionType<RefreshTemplates, CGTemplateObject, CGTemplateObject>(
			"refresh", FlexoActionType.defaultGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public RefreshTemplates makeNewAction(CGTemplateObject focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
			return new RefreshTemplates(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(CGTemplateObject object, Vector<CGTemplateObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(CGTemplateObject object, Vector<CGTemplateObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(RefreshTemplates.actionType, CGTemplateObject.class);
	}

	RefreshTemplates(CGTemplateObject focusedObject, Vector<CGTemplateObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws DuplicateCodeRepositoryNameException {
		logger.info("Refresh " + getFocusedObject());
		if (getFocusedObject() != null) {
			getFocusedObject().refresh();
		}
		if ((getGlobalSelection() != null) && (getGlobalSelection().size() > 0)) {
			for (CGTemplateObject o : getGlobalSelection()) {
				o.refresh();
			}
		}
	}

}
