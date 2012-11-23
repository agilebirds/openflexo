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
package org.openflexo.ie.view.print;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoGUIAction;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;

public class PrintComponentAction extends FlexoGUIAction<PrintComponentAction, FlexoModelObject, FlexoModelObject> {

	protected static final Logger logger = Logger.getLogger(PrintComponentAction.class.getPackage().getName());

	public static FlexoActionType<PrintComponentAction, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<PrintComponentAction, FlexoModelObject, FlexoModelObject>(
			"print_component", FlexoActionType.printGroup, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public PrintComponentAction makeNewAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
				FlexoEditor editor) {
			return new PrintComponentAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject object, Vector<FlexoModelObject> globalSelection) {
			return getComponentForObject(object) != null;
		}

	};

	static {
		FlexoModelObject.addActionForClass(PrintComponentAction.actionType, IEWOComponent.class);
		FlexoModelObject.addActionForClass(PrintComponentAction.actionType, ComponentDefinition.class);
	}

	protected static IEWOComponent getComponentForObject(FlexoModelObject object) {
		if (object == null) {
			return null;
		}
		if (object instanceof IEWOComponent) {
			return (IEWOComponent) object;
		} else if (object instanceof ComponentDefinition) {
			return ((ComponentDefinition) object).getWOComponent();
		} else if (object instanceof ComponentInstance) {
			return ((ComponentInstance) object).getWOComponent();
		}
		return null;
	}

	PrintComponentAction(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public IEWOComponent getComponent() {
		return getComponentForObject(getFocusedObject());
	}

}
