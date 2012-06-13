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
package org.openflexo.wkf.roleeditor;

import java.awt.Graphics;

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.wkf.roleeditor.DrawRoleSpecializationControl.DrawRoleSpecializationAction;

public class RoleEditorView extends DrawingView<RoleListRepresentation> implements ModuleView<RoleList> {

	public RoleEditorView(RoleListRepresentation aDrawing, RoleEditorController controller) {
		super(aDrawing, controller);
	}

	@Override
	public RoleEditorController getController() {
		return (RoleEditorController) super.getController();
	}

	@Override
	public void deleteModuleView() {
		getController().delete();
	}

	@Override
	public FlexoPerspective getPerspective() {
		return getController().getWKFController().ROLE_EDITOR_PERSPECTIVE;
	}

	public FlexoProject getProject() {
		return getRepresentedObject().getProject();
	}

	@Override
	public RoleList getRepresentedObject() {
		return getModel().getRoleList();
	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}

	@Override
	public void willHide() {
		getPaintManager().clearPaintBuffer();
	}

	@Override
	public void willShow() {
	}

	private DrawRoleSpecializationAction _drawRoleSpecializationAction;

	public void setDrawEdgeAction(DrawRoleSpecializationAction action) {
		_drawRoleSpecializationAction = action;
	}

	public void resetDrawEdgeAction() {
		_drawRoleSpecializationAction = null;
		repaint();
	}

	private FloatingPalette floatingPalette;

	public void setFloatingPalette(FloatingPalette palette) {
		floatingPalette = palette;
	}

	public void resetFloatingPalette() {
		floatingPalette = null;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		boolean isBuffering = isBuffering();
		super.paint(g);
		if (_drawRoleSpecializationAction != null && !isBuffering) {
			_drawRoleSpecializationAction.paint(g, getController());
		}
		if (floatingPalette != null && !isBuffering) {
			floatingPalette.paint(g, getController());
		}
	}

}
