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

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.icon.WKFIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;
import org.openflexo.wkf.view.doc.WKFDocumentationView;

public class DocumentationPerspective extends FlexoPerspective {

	/**
	 * 
	 */
	private final WKFController _controller;

	/**
	 * @param name
	 * @param controller
	 *            TODO
	 */
	public DocumentationPerspective(WKFController controller, String name) {
		super(name);
		_controller = controller;
		setTopLeftView(_controller.getWkfBrowserView());
		setBottomLeftView(_controller.getProcessBrowserView());
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WKFIconLibrary.WKF_DOCP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WKFIconLibrary.WKF_DOCP_SELECTED_ICON;
	}

	@Override
	public FlexoProcess getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof WKFObject) {
			return ((WKFObject) proposedObject).getProcess();
		}
		return _controller.getProject().getRootFlexoProcess();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof FlexoProcess && !((FlexoProcess) object).isImported();
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject process, FlexoController controller) {
		if (process instanceof FlexoProcess) {
			return new WKFDocumentationView((FlexoProcess) process, (WKFController) controller);
		} else {
			return null;
		}
	}

}