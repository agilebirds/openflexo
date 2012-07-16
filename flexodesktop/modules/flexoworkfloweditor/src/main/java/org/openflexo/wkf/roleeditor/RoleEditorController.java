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

import org.openflexo.fge.view.DrawingView;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.wkf.RoleList;
import org.openflexo.selection.SelectionManagingDrawingController;
import org.openflexo.wkf.controller.WKFController;

public class RoleEditorController extends SelectionManagingDrawingController<RoleListRepresentation> {

	// Can be null!
	private WKFController _controller;
	private RolePalette _palette;

	public RoleEditorController(RoleList roleList, WKFController controller) {
		super(new RoleListRepresentation(roleList, controller), controller != null ? controller.getSelectionManager() : null);
		_controller = controller;
		_palette = new RolePalette();
		registerPalette(_palette);
		activatePalette(_palette);
	}

	@Override
	public void delete() {
		if (_controller != null) {
			if (getDrawingView() != null) {
				_controller.removeModuleView(getDrawingView());
			}
		}
		super.delete();
		getDrawing().delete();
	}

	@Override
	public DrawingView<RoleListRepresentation> makeDrawingView(RoleListRepresentation drawing) {
		return new RoleEditorView(drawing, this);
	}

	/**
	 * Returns the WKFController, if any (could be null).
	 * 
	 * @return
	 */
	public WKFController getWKFController() {
		return _controller;
	}

	@Override
	public RoleEditorView getDrawingView() {
		return (RoleEditorView) super.getDrawingView();
	}

	public RolePalette getPalette() {
		return _palette;
	}

	public FlexoEditor getEditor() {
		return getDrawing().getEditor();
	}

}
