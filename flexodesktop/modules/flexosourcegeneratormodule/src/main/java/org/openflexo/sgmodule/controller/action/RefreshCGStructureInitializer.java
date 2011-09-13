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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.generator.action.RefreshCGStructure;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class RefreshCGStructureInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	RefreshCGStructureInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(RefreshCGStructure.actionType,actionInitializer);
	}
	
	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<RefreshCGStructure> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<RefreshCGStructure>() {
            @Override
			public boolean run(ActionEvent e, RefreshCGStructure action)
            {
            	return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<RefreshCGStructure> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<RefreshCGStructure>() {
            @Override
			public boolean run(ActionEvent e, RefreshCGStructure action)
            {
            	return true;
           }
        };
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.REFRESH_ICON;
	}
 
	@Override
	protected Icon getDisabledIcon() 
	{
		return IconLibrary.REFRESH_DISABLED_ICON;
	}
 
 }
