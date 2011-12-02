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
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.FlexoMenuItemView;
import org.openflexo.ie.view.FlexoMenuRootItemView;
import org.openflexo.ie.view.MenuEditorBrowserView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class MenuPerspective extends FlexoPerspective<FlexoItemMenu> {

	private final IEController _controller;
	private MenuEditorBrowserView _menuEditorBrowserView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public MenuPerspective(IEController controller) {
		super("menu_perspective");
		_controller = controller;
		_menuEditorBrowserView = new MenuEditorBrowserView(controller);
		_menuEditorBrowserView.setName(FlexoLocalization.localizedForKey("Menu", _menuEditorBrowserView));
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return SEIconLibrary.MENU_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return SEIconLibrary.MENU_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public FlexoItemMenu getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof FlexoItemMenu) {
			return (FlexoItemMenu) proposedObject;
		}
		return _controller.getProject().getFlexoNavigationMenu().getRootMenu();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof FlexoItemMenu;
	}

	@Override
	public ModuleView<FlexoItemMenu> createModuleViewForObject(FlexoItemMenu menu, FlexoController controller) {
		if (menu.isRootMenu()) {
			return new FlexoMenuRootItemView(menu, (IEController) controller);
		} else {
			return new FlexoMenuItemView(menu, (IEController) controller);
		}
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		_controller.getSelectionManager().setSelectedObject(moduleView.getRepresentedObject());
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return _menuEditorBrowserView;
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