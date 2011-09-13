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
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.DMCopy;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class DMCopyInitializer extends ActionInitializer {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DMCopyInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(DMCopy.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<DMCopy> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DMCopy>() {
            @Override
			public boolean run(ActionEvent e, DMCopy action)
            {
                return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<DMCopy> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DMCopy>() {
            @Override
			public boolean run(ActionEvent e, DMCopy action)
            {
            	getControllerActionInitializer().getDMSelectionManager().performSelectionCopy();
                return true;
          }
        };
	}

 	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.COPY_ICON;
	}
 
	@Override
	protected KeyStroke getShortcut()
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_C, FlexoCst.META_MASK);
	}

}
