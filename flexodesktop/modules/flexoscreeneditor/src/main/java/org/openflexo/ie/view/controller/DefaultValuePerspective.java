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
import javax.swing.JComponent;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.ComponentInstance;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.view.IEExampleValuesView;
import org.openflexo.ie.view.IEWOComponentView;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class DefaultValuePerspective extends FlexoPerspective<ComponentInstance> {

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
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return IconLibrary.LIST_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return IconLibrary.LIST_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public ComponentInstance getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof ComponentInstance) {
			return (ComponentInstance) proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof ComponentInstance;
	}

	@Override
	public ModuleView<ComponentInstance> createModuleViewForObject(ComponentInstance object, FlexoController controller) {
		return new IEExampleValuesView((IEController) controller, object);
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView instanceof IEWOComponentView) {
			ComponentInstance ci = ((IEWOComponentView) moduleView).getRepresentedObject();
			ComponentDefinition component = ci.getComponentDefinition();
			_controller.getComponentLibraryBrowser().focusOn(component);
			_controller.getSelectionManager().setSelectedObject(component.getWOComponent());
			_controller.getComponentBrowser().setCurrentComponent(component);
			_controller.getComponentBrowser().focusOn(component);
		}
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return _controller.getSplitPaneWithBrowsers();
	}

	@Override
	public boolean doesPerspectiveControlRightView() {
		return true;
	}

	@Override
	public JComponent getRightView() {
		return _controller.getDisconnectedDocInspectorPanel();
	}

}