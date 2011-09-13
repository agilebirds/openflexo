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
import org.openflexo.foundation.dkv.DKVModel;
import org.openflexo.foundation.dkv.DKVObject;
import org.openflexo.icon.SEIconLibrary;
import org.openflexo.ie.view.dkv.DKVEditorBrowserView;
import org.openflexo.ie.view.dkv.DKVModelView;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;


class DKVPerspective extends FlexoPerspective<DKVModel>
{

	private final IEController _controller;
    private DKVEditorBrowserView _dkvEditorBrowserView;
	protected DKVModelView dkvModelView;

	/**
	 * @param controller TODO
	 * @param name
	 */
	public DKVPerspective(IEController controller)
	{
		super("dkv_perspective");
		_controller = controller;
		_dkvEditorBrowserView = new DKVEditorBrowserView(controller);
		_dkvEditorBrowserView.setName(FlexoLocalization.localizedForKey("Key-Value",_dkvEditorBrowserView));
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon()
	{
		return SEIconLibrary.DKV_PERSPECTIVE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon()
	{
		return SEIconLibrary.DKV_PERSPECTIVE_SELECTED_ICON;
	}

	@Override
	public DKVModel getDefaultObject(FlexoModelObject proposedObject) 
	{
		if (proposedObject instanceof DKVObject) return ((DKVObject)proposedObject).getDkvModel();
		return _controller.getProject().getDKVModel();
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) 
	{
		return object instanceof DKVModel;
	}

	@Override
	public ModuleView<DKVModel> createModuleViewForObject(DKVModel object, FlexoController controller) 
	{
		if (dkvModelView == null) {
			dkvModelView = new DKVModelView(object,(IEController)controller);
		}
		return dkvModelView;
	}
	
	@Override
	public boolean isAlwaysVisible() 
	{
		return true;
	}
	
	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) 
	{
		_controller.getSelectionManager().setSelectedObject(moduleView.getRepresentedObject());
	}
	
	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public JComponent getLeftView() 
	{
		return _dkvEditorBrowserView;
	}
	
	@Override
	public boolean doesPerspectiveControlRightView() 
	{
		return true;
	}
	
	@Override
	public JComponent getRightView() 
	{
		return _controller.getDisconnectedDocInspectorPanel();
	}

}