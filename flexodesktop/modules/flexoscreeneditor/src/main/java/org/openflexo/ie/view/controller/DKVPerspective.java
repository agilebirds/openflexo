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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.dkv.DKVEditorBrowserView;
import org.openflexo.ie.view.dkv.DKVModelView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

class DKVPerspective extends FlexoPerspective {

	private final IEController _controller;
	private DKVEditorBrowserView _dkvEditorBrowserView;
	protected DKVModelView dkvModelView;

	/**
	 * @param controller
	 *            TODO
	 * @param name
	 */
	public DKVPerspective(IEController controller) {
		super("dkv_perspective");
		_controller = controller;
		_dkvEditorBrowserView = new DKVEditorBrowserView(controller);
		_dkvEditorBrowserView.setName(FlexoLocalization.localizedForKey("Key-Value", _dkvEditorBrowserView));
		setTopLeftView(_dkvEditorBrowserView);
		setBottomRightView(_controller.getDisconnectedDocInspectorPanel());
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return SEIconLibrary.DKV_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return SEIconLibrary.DKV_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public DKVModel getDefaultObject(FlexoModelObject proposedObject) {
		if (proposedObject instanceof DKVObject) {
			return ((DKVObject) proposedObject).getDkvModel();
		}
		return _controller.getProject().getDKVModel();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof DKVModel;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof DKVModel) {
			if (dkvModelView == null) {
				dkvModelView = new DKVModelView((DKVModel) object, (IEController) controller);
			}
			return dkvModelView;
		} else {
			return null;
		}
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		_controller.getSelectionManager().setSelectedObject(moduleView.getRepresentedObject());
	}

}