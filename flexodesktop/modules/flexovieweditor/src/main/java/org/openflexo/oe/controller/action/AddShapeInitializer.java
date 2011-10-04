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
package org.openflexo.oe.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.VEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.oe.controller.OEController;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.action.AddShape;


public class AddShapeInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddShapeInitializer(OEControllerActionInitializer actionInitializer)
	{
		super(AddShape.actionType,actionInitializer);
	}

	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() 
	{
		return (OEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddShape> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddShape>() {
			@Override
			public boolean run(ActionEvent e, AddShape action)
			{
				if ((action.getNewShapeName() != null || action.isNameSetToNull()) 
						&& (action.getParent() != null))
					return true;

				ViewObject parent = action.getParent();
				if (parent != null) {
						action.setNewShapeName(FlexoController.askForString(FlexoLocalization.localizedForKey("name_for_new_shape")));
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<AddShape> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddShape>() {
			@Override
			public boolean run(ActionEvent e, AddShape action)
			{
				((OEController)getController()).getSelectionManager().setSelectedObject(action.getNewShape());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return VEIconLibrary.SHAPE_ICON;
	}


}
