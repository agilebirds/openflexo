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
package org.openflexo.wkf.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.roleeditor.RoleEditorController;
import org.openflexo.wkf.roleeditor.RoleEditorView;

public class RolePerspective extends FlexoPerspective {

	private final JLabel infoLabel = new JLabel("CTRL-drag to create role specialization");

	private final WKFController _controller;

	private JPanel topRightDummy;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public RolePerspective(WKFController controller) {
		super("role_editor");
		_controller = controller;
		topRightDummy = new JPanel();
		setTopLeftView(_controller.getRoleListBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
		setFooter(infoLabel);
	}

	public RoleEditorView getCurrentRoleListView() {
		if (_controller != null && _controller.getCurrentModuleView() instanceof RoleEditorView) {
			return (RoleEditorView) _controller.getCurrentModuleView();
		}
		return null;
	}

	@Override
	public JComponent getTopRightView() {
		if (getCurrentRoleListView() != null) {
			return getCurrentRoleListView().getController().getPalette().getPaletteView();
		}
		return topRightDummy;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_RP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_RP_SELECTED_ICON;
	}

	@Override
	public RoleList getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject != null) {
			return proposedObject.getProject().getWorkflow().getRoleList();
		}
		return _controller.getProject().getWorkflow().getRoleList();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof RoleList && !((RoleList) object).isImportedRoleList();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject roleList, FlexoController controller) {
		if (roleList instanceof RoleList) {
			return new RoleEditorController((RoleList) roleList, _controller).getDrawingView();
		} else {
			return null;
		}
	}

	@Override
	public JComponent getHeader() {
		if (getCurrentRoleListView() != null) {
			return getCurrentRoleListView().getController().getScalePanel();
		}
		return null;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

}