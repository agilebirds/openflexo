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
package org.openflexo.foundation.ie.cl.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.rm.FlexoProject;

public class ShowComponentUsage extends FlexoGUIAction<ShowComponentUsage, IEObject, IEObject> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShowComponentUsage.class.getPackage().getName());

	public static FlexoActionType<ShowComponentUsage, IEObject, IEObject> actionType = new FlexoActionType<ShowComponentUsage, IEObject, IEObject>(
			"show_where_component_is_used", FlexoActionType.inspectGroup) {

		/**
		 * Factory method
		 */
		@Override
		public ShowComponentUsage makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new ShowComponentUsage(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return object instanceof ComponentDefinition || object instanceof IEWOComponent;
		}

	};

	protected ShowComponentUsage(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public ComponentDefinition getComponentDefinition() {
		if (getFocusedObject() instanceof IEWOComponent) {
			return ((IEWOComponent) getFocusedObject()).getComponentDefinition();
		}
		return (ComponentDefinition) getFocusedObject();
	}
}