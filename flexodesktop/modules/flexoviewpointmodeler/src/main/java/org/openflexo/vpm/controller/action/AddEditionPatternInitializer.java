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
package org.openflexo.vpm.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.VPMIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.vpm.controller.CEDController;


import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.viewpoint.action.AddEditionPattern;

public class AddEditionPatternInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	AddEditionPatternInitializer(CEDControllerActionInitializer actionInitializer)
	{
		super(AddEditionPattern.actionType,actionInitializer);
	}

	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() 
	{
		return (CEDControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<AddEditionPattern> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<AddEditionPattern>() {
			@Override
			public boolean run(ActionEvent e, AddEditionPattern action)
			{
				action.setNewEditionPatternName(FlexoController.askForString(FlexoLocalization.localizedForKey("name_for_new_edition_pattern")));
				return (action.getNewEditionPatternName() != null);
			}
		};
	}
	

	@Override
	protected FlexoActionFinalizer<AddEditionPattern> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<AddEditionPattern>() {
			@Override
			public boolean run(ActionEvent e, AddEditionPattern action)
			{
				((CEDController)getController()).setCurrentEditedObjectAsModuleView(action.getNewEditionPattern());
				return true;
			}
		};
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return VPMIconLibrary.EDITION_PATTERN_ICON;
	}

}
