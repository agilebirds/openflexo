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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.dm.view.DMEOEntityView;
import org.openflexo.dm.view.DMEOModelView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateDMEOAttribute;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class CreateDMEOAttributeInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEOAttributeInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(CreateDMEOAttribute.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<CreateDMEOAttribute> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<CreateDMEOAttribute>() {
            @Override
			public boolean run(ActionEvent e, CreateDMEOAttribute action)
            {
            	return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<CreateDMEOAttribute> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<CreateDMEOAttribute>() {
            @Override
			public boolean run(ActionEvent e, CreateDMEOAttribute action)
            {
                if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity().getDMEOModel()) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Finalizer for CreateDMEOAttribute in DMEOModelView");
                    DMEOModelView dmEOModelView = (DMEOModelView)getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
                    dmEOModelView.getEoEntityTable().selectObject(action.getEntity());
                    dmEOModelView.getEoAttributeTable().selectObject(action.getNewEOAttribute());
                } else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getEntity()) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Finalizer for CreateDMEOAttribute in DMEOEntityView");
                    DMEOEntityView eoEntityView = (DMEOEntityView) getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
                    eoEntityView.getEoAttributeTable().selectObject(action.getNewEOAttribute());
                }
                return true;
           }
        };
	}

 	@Override
	protected Icon getEnabledIcon() 
	{
		return DMEIconLibrary.DM_EOATTRIBUTE_ICON;
	}
 
}
