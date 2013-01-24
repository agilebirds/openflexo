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
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.roleeditor.RoleEditorController;
import org.openflexo.wkf.roleeditor.RoleEditorView;
import org.openflexo.wkf.view.ImportedRoleView;

public class RolePerspective extends FlexoPerspective {

	private final JLabel infoLabel = new JLabel();

	private final WKFController _controller;

	private JPanel topRightDummy;

	private ImportedRoleView importedRoleView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public RolePerspective(WKFController controller) {
		super("role_editor");
		_controller = controller;
		topRightDummy = new JPanel();
		importedRoleView = new ImportedRoleView(controller);
		infoLabel.setText(FlexoLocalization.localizedForKey("CTRL-drag to create role specialization", infoLabel));
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
			return getCurrentRoleListView().getController().getPalette().getPaletteViewInScrollPane();
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

	@Override
	public RoleList getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject != null) {
			return proposedObject.getProject().getWorkflow().getRoleList();
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof RoleList && !((RoleList) object).isImportedRoleList();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller, boolean editable) {
		if (object instanceof RoleList) {
			RoleEditorView drawingView = new RoleEditorController((RoleList) object, _controller).getDrawingView();
			drawingView.getDrawing().setEditable(editable);
			return drawingView;
		} else {
			return null;
		}
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		return createModuleViewForObject(object, controller, false);
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

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView instanceof RoleEditorView) {
			importedRoleView.setSelected(((RoleEditorView) moduleView).getRepresentedObject());
		}
	}

	protected void updateMiddleLeftView() {
		if (_controller.getProject() != null && _controller.getProject().hasImportedProjects()) {
			setMiddleLeftView(importedRoleView);
		} else {
			setMiddleLeftView(null);
		}
	}

	public void setProject(FlexoProject project) {
		updateMiddleLeftView();
	}

}