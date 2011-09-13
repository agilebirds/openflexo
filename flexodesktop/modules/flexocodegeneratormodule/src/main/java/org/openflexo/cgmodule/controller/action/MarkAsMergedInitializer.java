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
package org.openflexo.cgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.generator.action.MarkAsMerged;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class MarkAsMergedInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MarkAsMergedInitializer(GeneratorControllerActionInitializer actionInitializer)
	{
		super(MarkAsMerged.actionType,actionInitializer);
	}
	
	@Override
	protected GeneratorControllerActionInitializer getControllerActionInitializer() 
	{
		return (GeneratorControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<MarkAsMerged> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<MarkAsMerged>() {
            @Override
			public boolean run(ActionEvent e, MarkAsMerged action)
            {
                action.getProjectGenerator().startHandleLogs();
                return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<MarkAsMerged> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<MarkAsMerged>() {
            @Override
			public boolean run(ActionEvent e, MarkAsMerged action)
            {
                action.getProjectGenerator().stopHandleLogs();
                action.getProjectGenerator().flushLogs();
                return true;
          }
        };
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.MARK_AS_MERGED_ICON;
	}
 
	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.MARK_AS_MERGED_DISABLED_ICON;
	}
 
}
