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

import org.openflexo.dm.view.DMPackageView;
import org.openflexo.dm.view.DMRepositoryView;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.dm.action.CreateDMEntity;
import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class CreateDMEntityInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	CreateDMEntityInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(CreateDMEntity.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<CreateDMEntity> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<CreateDMEntity>() {
            @Override
			public boolean run(ActionEvent e, CreateDMEntity action)
            {
            	return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<CreateDMEntity> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<CreateDMEntity>() {
            @Override
			public boolean run(ActionEvent e, CreateDMEntity action)
            {
                if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Finalizer for CreateDMEntity in DMRepositoryView");
                    DMRepositoryView repView = (DMRepositoryView)getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
                    repView.getPackageTable().selectObject(action.getPackage());
                    repView.getEntityTable().selectObject(action.getNewEntity());
                } else if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getPackage()) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Finalizer for CreateDMEntity in DMPackageView");
                    DMPackageView packageView = (DMPackageView)getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
                    packageView.getEntityTable().selectObject(action.getNewEntity());
                }
                return true;
           }
        };
	}

	@Override
	protected Icon getEnabledIcon() 
	{
		return DMEIconLibrary.DM_ENTITY_ICON;
	}
 
}
