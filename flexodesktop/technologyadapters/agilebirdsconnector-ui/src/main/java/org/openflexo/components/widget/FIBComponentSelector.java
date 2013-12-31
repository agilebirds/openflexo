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
package org.openflexo.components.widget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.cl.ReusableComponentDefinition;
import org.openflexo.foundation.ie.cl.TabComponentDefinition;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.toolbox.FileResource;

/**
 * Widget allowing to select a FlexoRole
 * 
 * @author sguerin
 * 
 */
public class FIBComponentSelector extends FIBModelObjectSelector<ComponentDefinition> {
	@SuppressWarnings("hiding")
	static final Logger logger = Logger.getLogger(FIBComponentSelector.class.getPackage().getName());

	public static final FileResource FIB_FILE = new FileResource("Fib/ComponentSelector.fib");

	private Class<? extends ComponentDefinition> type = ComponentDefinition.class;

	public FIBComponentSelector(ComponentDefinition editedObject) {
		super(editedObject);
	}

	@Override
	protected CustomFIBController makeCustomFIBController(FIBComponent fibComponent) {
		return new CustomFIBController(fibComponent, this);
	}

	/**
	 * Override when required
	 */
	@Override
	protected Collection<ComponentDefinition> getAllSelectableValues() {
		if (getProject() != null) {
			return new ArrayList<ComponentDefinition>(getProject().getFlexoComponentLibrary().getRootFolder()
					.getComponentsOfType(type, true));
		}
		return null;
	}

	@Override
	public File getFIBFile() {
		return FIB_FILE;
	}

	@Override
	protected boolean isAcceptableValue(Object o) {
		return super.isAcceptableValue(o) && type.isAssignableFrom(o.getClass());
	}

	@Override
	public Class<ComponentDefinition> getRepresentedType() {
		return ComponentDefinition.class;
	}

	@Override
	public String renderedString(ComponentDefinition editedObject) {
		if (editedObject != null) {
			return editedObject.getName();
		}
		return "";
	}

	public String getType() {
		return type.getName();
	}

	@CustomComponentParameter(name = "type", type = CustomComponentParameter.Type.OPTIONAL)
	public void setType(String type) {
		try {
			this.type = (Class<? extends ComponentDefinition>) Class.forName(type);
			if (!ComponentDefinition.class.isAssignableFrom(this.type)) {
				logger.warning(type + " is not a sub-class " + ComponentDefinition.class.getName());
				this.type = ComponentDefinition.class;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static class CustomFIBController extends FIBModelObjectSelector.SelectorFIBController {
		public CustomFIBController(FIBComponent component, FIBComponentSelector selector) {
			super(component, selector);
		}

		public Icon getIconForComponent(ComponentDefinition component) {
			if (component instanceof OperationComponentDefinition) {
				return SEIconLibrary.OPERATION_COMPONENT_ICON;
			} else if (component instanceof PopupComponentDefinition) {
				return SEIconLibrary.POPUP_COMPONENT_ICON;
			} else if (component instanceof TabComponentDefinition) {
				return SEIconLibrary.TAB_COMPONENT_ICON;
			} else if (component instanceof ReusableComponentDefinition) {
				return SEIconLibrary.REUSABLE_COMPONENT_ICON;
			} else {
				return SEIconLibrary.OPERATION_COMPONENT_ICON;
			}
		}

	}

	/*public static void main(String[] args) {
		testSelector(new FIBProcessSelector(null));
	}*/
}
