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
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ie.view.print.PrintComponentAction;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class PrintComponentInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	PrintComponentInitializer(IEControllerActionInitializer actionInitializer)
	{
		super(PrintComponentAction.actionType,actionInitializer);
	}
	
	@Override
	protected IEControllerActionInitializer getControllerActionInitializer() 
	{
		return (IEControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<PrintComponentAction> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<PrintComponentAction>() {
            @Override
			public boolean run(ActionEvent e, PrintComponentAction action)
            {
            	return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<PrintComponentAction> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<PrintComponentAction>() {
            @Override
			public boolean run(ActionEvent e, PrintComponentAction action)
            {
               	return true;
          }
        };
	}

  	@Override
	public void init()
	{
        PrintComponentAction.initWithController(getControllerActionInitializer().getIEController());
        getControllerActionInitializer().registerAction(PrintComponentAction.actionType,getShortcut());
	}
	
	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.PRINT_ICON;
	}

	@Override
	protected KeyStroke getShortcut() 
	{
		return KeyStroke.getKeyStroke(KeyEvent.VK_P, FlexoCst.META_MASK);
	}
	
}
