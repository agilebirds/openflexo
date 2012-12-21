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
package org.openflexo.ie.view.controller;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.view.IEExampleValuesView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

class DefaultValuePerspective extends FlexoPerspective {

	/**
		 * 
		 */
	private final IEController _controller;

	/**
	 * @param controller
	 *            TODO
	 * 
	 */
	public DefaultValuePerspective(IEController controller) {
		super("default_values_perspective");
		_controller = controller;
		setTopLeftView(controller.getComponentLibraryBrowserView());
		setBottomLeftView(controller.getComponentBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return IconLibrary.LIST_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return IconLibrary.LIST_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public ComponentInstance getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof ComponentInstance) {
			return (ComponentInstance) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof ComponentInstance;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof ComponentInstance) {
			return new IEExampleValuesView((IEController) controller, (ComponentInstance) object);
		} else {
			return null;
		}
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView instanceof IEWOComponentView) {
			ComponentInstance ci = ((IEWOComponentView) moduleView).getRepresentedObject();
			ComponentDefinition component = ci.getComponentDefinition();
			_controller.getComponentLibraryBrowser().focusOn(component);
			_controller.getSelectionManager().setSelectedObject(component.getWOComponent());
			_controller.getComponentBrowser().setRootObject(component);
		}
	}

}