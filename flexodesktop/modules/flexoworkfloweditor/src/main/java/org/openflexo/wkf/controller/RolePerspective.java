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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSplitPane;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.utils.FlexoSplitPaneLocationSaver;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wkf.WKFCst;
import org.openflexo.wkf.roleeditor.RoleEditorController;

public class RolePerspective extends FlexoPerspective<RoleList> {

	private final WKFController _controller;
	private RoleEditorController _roleEditorController;

	private JSplitPane splitPaneWithRolePaletteAndDocInspectorPanel;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public RolePerspective(WKFController controller) {
		super("role_editor");
		_controller = controller;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_RP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_RP_SELECTED_ICON;
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
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
		return (object instanceof RoleList) && !((RoleList) object).isImportedRoleList();
	}

	@Override
	public ModuleView<RoleList> createModuleViewForObject(RoleList roleList, FlexoController controller) {
		return getRoleEditorController().getDrawingView();
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() {
		return _controller.getRoleListBrowserView();
	}

	@Override
	public JComponent getHeader() {
		return getRoleEditorController().getScalePanel();
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	private final JLabel infoLabel = new JLabel("CTRL-drag to create role specialization");

	@Override
	public boolean doesPerspectiveControlRightView() {
		return true;
	}

	@Override
	public JComponent getRightView() {
		return getSplitPaneWithRolePaletteAndDocInspectorPanel();
	}

	public RoleEditorController getRoleEditorController() {
		if (_roleEditorController == null) {
			_roleEditorController = new RoleEditorController(_controller);
		}
		return _roleEditorController;
	}

	/**
	 * Return Split pane with Role palette and doc inspector panel Disconnect doc inspector panel from its actual parent
	 * 
	 * @return
	 */
	protected JSplitPane getSplitPaneWithRolePaletteAndDocInspectorPanel() {
		if (splitPaneWithRolePaletteAndDocInspectorPanel == null) {
			splitPaneWithRolePaletteAndDocInspectorPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getRoleEditorController().getPalette()
					.getPaletteView(), _controller.getDisconnectedDocInspectorPanel());
			splitPaneWithRolePaletteAndDocInspectorPanel.setBorder(BorderFactory.createEmptyBorder());
			splitPaneWithRolePaletteAndDocInspectorPanel.setResizeWeight(0);
			splitPaneWithRolePaletteAndDocInspectorPanel.setDividerLocation(WKFCst.PALETTE_DOC_SPLIT_LOCATION);
		}
		if (splitPaneWithRolePaletteAndDocInspectorPanel.getBottomComponent() == null) {
			splitPaneWithRolePaletteAndDocInspectorPanel.setBottomComponent(_controller.getDisconnectedDocInspectorPanel());
		}
		new FlexoSplitPaneLocationSaver(splitPaneWithRolePaletteAndDocInspectorPanel, "RolePaletteAndDocInspectorPanel");
		return splitPaneWithRolePaletteAndDocInspectorPanel;
	}

	public void removeFromRoleController(RoleEditorController roleEditorController) {
		if (_roleEditorController == roleEditorController) {
			_roleEditorController = null;
			splitPaneWithRolePaletteAndDocInspectorPanel = null;
		}
	}

}