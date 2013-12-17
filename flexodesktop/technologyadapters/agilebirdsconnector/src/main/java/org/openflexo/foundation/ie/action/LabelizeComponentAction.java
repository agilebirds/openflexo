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
package org.openflexo.foundation.ie.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.TopComponentContainer;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.localization.FlexoLocalization;

/**
 * @author gpolet
 * 
 */
public class LabelizeComponentAction extends FlexoAction<LabelizeComponentAction, ComponentDefinition, ComponentDefinition>

{

	protected static final Logger logger = Logger.getLogger(LabelizeComponentAction.class.getPackage().getName());

	public static FlexoActionType<LabelizeComponentAction, ComponentDefinition, ComponentDefinition> actionType = new FlexoActionType<LabelizeComponentAction, ComponentDefinition, ComponentDefinition>(
			"labelize_component", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public LabelizeComponentAction makeNewAction(ComponentDefinition focusedObject, Vector<ComponentDefinition> globalSelection,
				FlexoEditor editor) {
			return new LabelizeComponentAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(ComponentDefinition object, Vector<ComponentDefinition> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(ComponentDefinition object, Vector<ComponentDefinition> globalSelection) {
			return object instanceof TopComponentContainer || object instanceof OperationComponentDefinition
					|| object instanceof PopupComponentDefinition || object instanceof TabComponentDefinition;
		}

	};

	static {
		AgileBirdsObject.addActionForClass(actionType, ComponentDefinition.class);
	}

	protected LabelizeComponentAction(ComponentDefinition focusedObject, Vector<ComponentDefinition> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private IEWOComponent component;

	@Override
	protected void doAction(Object context) {
		Vector v = component.getAllEmbeddedIEObjects();
		makeFlexoProgress(FlexoLocalization.localizedForKey("setting_label"), v.size());
		for (Object o : v) {
			if (o instanceof IEWidget) {
				if (((IEWidget) o).getLabel() == null || ((IEWidget) o).getLabel().length() == 0) {
					((IEWidget) o).setLabel(((IEWidget) o).getCalculatedLabel());
				}
			}
			setProgress(FlexoLocalization.localizedForKey("setting_label"));
		}
	}

	public IEWOComponent getComponent() {
		return component;
	}

	public void setComponent(IEWOComponent component) {
		this.component = component;
	}

}
