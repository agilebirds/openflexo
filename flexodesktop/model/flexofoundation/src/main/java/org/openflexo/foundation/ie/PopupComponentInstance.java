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
package org.openflexo.foundation.ie;

import java.util.Vector;

import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.menu.FlexoNavigationMenu;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.foundation.xml.FlexoNavigationMenuBuilder;

/**
 * @author bmangez <B>Class Description</B>
 */
public class PopupComponentInstance extends ComponentInstance {

	public PopupComponentInstance(PopupComponentDefinition component, IEWOComponent container) {
		super(component, container);
	}

	/**
	 * 
	 * @param component
	 * @param menu
	 * @deprecated Menu items should never reference directly a component
	 */
	@Deprecated
	public PopupComponentInstance(PopupComponentDefinition component, FlexoNavigationMenu menu) {
		super(component, menu);
	}

	public PopupComponentInstance(FlexoComponentBuilder builder) {
		super(builder);
	}

	public PopupComponentInstance(FlexoNavigationMenuBuilder builder) {
		super(builder);
	}

	public PopupComponentDefinition getPopupComponentDefinition() {
		return (PopupComponentDefinition) getComponentDefinition();
	}

	/*
	 * public void update(FlexoObservable o, DataModification arg) { if(arg
	 * instanceof ComponentNameChanged){ _componentName =
	 * (String)((ComponentNameChanged)arg).newValue(); }
	 *  }
	 */

	@Override
	public String getFullyQualifiedName() {
		return "POPUP_COMPONENT_INSTANCE." + getComponentDefinition().getName();
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "popup_component_instance";
	}

	public String getParentName() {
		if (getXMLResourceData() instanceof IEWOComponent) {
			return ((IEWOComponent) getXMLResourceData()).getName();
		} else if (getXMLResourceData() instanceof FlexoNavigationMenu) {
			return "Menu";
		}
		return "Unknown !!!";
	}

	public String getContextIdentifier() {
		return getParentName();
	}

	@Override
	public Vector<IObject> getWOComponentEmbeddedIEObjects() {
		return getComponentDefinition().getAllEmbeddedIEObjects();
	}
}
