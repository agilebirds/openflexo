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
package org.openflexo.dm.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.dm.view.popups.TypeHierarchyPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.action.ShowTypeHierarchyAction;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class ShowTypeHierarchyInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ShowTypeHierarchyInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(ShowTypeHierarchyAction.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<ShowTypeHierarchyAction> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ShowTypeHierarchyAction>() {
            @Override
			public boolean run(ActionEvent e, ShowTypeHierarchyAction action)
            {
                return true;
           }
        };
	}

     @Override
	protected FlexoActionFinalizer<ShowTypeHierarchyAction> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ShowTypeHierarchyAction>() {
            @Override
			public boolean run(ActionEvent e, ShowTypeHierarchyAction action)
            {
                DMEntity focusedEntity = (DMEntity) action.getFocusedObject();
                TypeHierarchyPopup popup = new TypeHierarchyPopup(focusedEntity, getControllerActionInitializer().getDMController());
                return true;
            }
        };
	}

}
