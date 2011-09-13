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
package org.openflexo.fps.controller.action;

import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.fps.FlexoAuthentificationException;
import org.openflexo.fps.action.CVSRefresh;
import org.openflexo.icon.IconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class CVSRefreshInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CVSRefreshInitializer(FPSControllerActionInitializer actionInitializer)
	{
		super(CVSRefresh.actionType,actionInitializer);
	}
	
	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() 
	{
		return (FPSControllerActionInitializer)super.getControllerActionInitializer();
	}
	
     @Override
 	protected FlexoExceptionHandler<CVSRefresh> getDefaultExceptionHandler() 
 	{
 		return new FlexoExceptionHandler<CVSRefresh>() {
 			@Override
			public boolean handleException(FlexoException exception, CVSRefresh action) {
 	           	if (exception instanceof FlexoAuthentificationException) {
            		getControllerActionInitializer().handleAuthenticationException((FlexoAuthentificationException) exception);
            	}
            	return false;
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
