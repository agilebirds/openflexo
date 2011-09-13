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
package org.openflexo.ced.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.action.SetPropertyAction;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class CEDSetPropertyInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

    public CEDSetPropertyInitializer(ControllerActionInitializer actionInitializer)
	{
		super(SetPropertyAction.actionType,actionInitializer);
	}

	@Override
	protected FlexoActionInitializer<SetPropertyAction> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<SetPropertyAction>() {
			@Override
			public boolean run(ActionEvent e, SetPropertyAction action)
			{
				return action.getFocusedObject()!=null;
			}
		};
	}

	@Override
	protected FlexoActionFinalizer<SetPropertyAction> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<SetPropertyAction>() {
			@Override
			public boolean run(ActionEvent e, SetPropertyAction action)
			{
				return true;
			}
		};
	}

	@Override
    protected FlexoExceptionHandler<SetPropertyAction> getDefaultExceptionHandler() 
    {
        return new FlexoExceptionHandler<SetPropertyAction>() {
            @Override
			public boolean handleException(FlexoException exception, SetPropertyAction action) 
            {
                exception.printStackTrace();
                FlexoController.notify(FlexoLocalization.localizedForKey("could_not_set_property")+" "+(action.getLocalizedPropertyName()!=null?"'"+action.getLocalizedPropertyName() +"' ":"")+ FlexoLocalization.localizedForKey("to")+" "+(action.getValue()==null||action.getValue().equals("")?FlexoLocalization.localizedForKey("empty_value"):action.getValue())
                        +(exception.getLocalizedMessage()!=null?"\n("+FlexoLocalization.localizedForKey("details: ")+ exception.getLocalizedMessage()+")":""));
                return true;
            }
        };
    }

}
