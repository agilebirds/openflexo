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
package org.openflexo.ie.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ie.action.MakePartialComponent;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.util.FolderType;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class MakePartialComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MakePartialComponentInitializer(IEControllerActionInitializer actionInitializer)
	{
		super(MakePartialComponent.actionType,actionInitializer);
	}

	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() 
	{
		return (IEControllerActionInitializer)super.getControllerActionInitializer();
	}

	@Override
	protected FlexoActionInitializer<MakePartialComponent> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<MakePartialComponent>() {
			@Override
			public boolean run(ActionEvent e, MakePartialComponent action)
			{
				String compName = JOptionPane.showInputDialog(null, FlexoLocalization.localizedForKey("component_name?"));
				Object[] nameAndFolder = new Object[] { compName, getProject().getFlexoComponentLibrary().getRootFolder().getFolderTyped(FolderType.PARTIAL_COMPONENT_FOLDER) }; 
				if (nameAndFolder[0] != null && ((String) nameAndFolder[0]).trim().length() > 0 && nameAndFolder[1] != null
						&& getProject().getFlexoComponentLibrary().getComponentNamed((String) nameAndFolder[0]) == null) {
					(action).setNewComponentName((String) nameAndFolder[0]);
					(action).setNewComponentFolder((FlexoComponentFolder) nameAndFolder[1]);
					return true;
				}
				return false;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<MakePartialComponent> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<MakePartialComponent>() {
			@Override
			public boolean run(ActionEvent e, MakePartialComponent action)
			{
				if ((action).getNewComponentResource() != null) {
					getModule().retainResource((action).getNewComponentResource());
				}
				return true;
			}
		};
	}

}
