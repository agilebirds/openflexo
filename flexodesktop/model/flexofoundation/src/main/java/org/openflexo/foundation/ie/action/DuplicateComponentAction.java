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

import java.awt.HeadlessException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ie.IEMonitoringScreen;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEOperationComponent;
import org.openflexo.foundation.ie.IEPopupComponent;
import org.openflexo.foundation.ie.IETabComponent;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.TopComponentContainer;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.rm.DuplicateResourceException;

/**
 * @author gpolet
 * 
 */
public class DuplicateComponentAction extends FlexoAction<DuplicateComponentAction, IEObject, IEObject> {

	protected static final Logger logger = Logger.getLogger(DuplicateComponentAction.class.getPackage().getName());

	public static FlexoActionType<DuplicateComponentAction, IEObject, IEObject> actionType = new FlexoActionType<DuplicateComponentAction, IEObject, IEObject>(
			"duplicate_component", FlexoActionType.defaultGroup) {

		/**
		 * Factory method
		 */
		@Override
		public DuplicateComponentAction makeNewAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
			return new DuplicateComponentAction(focusedObject, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(IEObject object, Vector<IEObject> globalSelection) {
			return object instanceof TopComponentContainer || object instanceof OperationComponentDefinition
					|| object instanceof PopupComponentDefinition || object instanceof TabComponentDefinition;
		}

	};

	protected DuplicateComponentAction(IEObject focusedObject, Vector<IEObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	private IEWOComponent component;

	private String newComponentName;

	private ComponentDefinition componentDef = null;

	@Override
	protected void doAction(Object context) {
		try {
			if (component instanceof IEOperationComponent) {
				componentDef = new OperationComponentDefinition(newComponentName, component.getComponentDefinition().getComponentLibrary(),
						component.getComponentDefinition().getFolder(), component.getProject());
			} else if (component instanceof IEPopupComponent) {
				componentDef = new PopupComponentDefinition(newComponentName, component.getComponentDefinition().getComponentLibrary(),
						component.getComponentDefinition().getFolder(), component.getProject());
			} else if (component instanceof IETabComponent) {
				componentDef = new TabComponentDefinition(newComponentName, component.getComponentDefinition().getComponentLibrary(),
						component.getComponentDefinition().getFolder(), component.getProject());
			} else if (component instanceof IEMonitoringScreen) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Not implemented");
				}
			} else if (logger.isLoggable(Level.INFO)) {
				logger.info("Not implemented");
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
			return;
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			// Should not happen
			return;
		}
		if (componentDef == null) {
			return;
		}
		Enumeration<IEWidget> en = component.getRootSequence().elements();
		while (en.hasMoreElements()) {
			IEWidget obj = en.nextElement();
			IEWidget tcCopy = (IEWidget) obj.cloneUsingXMLMapping();
			componentDef.getWOComponent().getRootSequence().insertElementAt(tcCopy, Integer.MAX_VALUE);
		}
	}

	public IEWOComponent getComponent() {
		return component;
	}

	public void setComponent(IEWOComponent component) {
		this.component = component;
	}

	public String getNewComponentName() {
		return newComponentName;
	}

	public void setNewComponentName(String newComponentName) {
		this.newComponentName = newComponentName;
	}

	public ComponentDefinition getComponentDefinition() {
		return componentDef;
	}
}
