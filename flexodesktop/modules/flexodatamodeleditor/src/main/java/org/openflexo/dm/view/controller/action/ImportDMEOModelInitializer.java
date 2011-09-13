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
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.icon.DMEIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.dm.view.DMEORepositoryView;
import org.openflexo.dm.view.DMEORepositoryView.OpenEOModelComponent;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.dm.action.ImportDMEOModel;
import org.openflexo.foundation.dm.eo.EOAccessException;
import org.openflexo.foundation.dm.eo.EOModelAlreadyRegisteredException;
import org.openflexo.foundation.dm.eo.InvalidEOModelFileException;
import org.openflexo.foundation.dm.eo.UnresovedEntitiesException;

public class ImportDMEOModelInitializer extends ActionInitializer {

	static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportDMEOModelInitializer(DMControllerActionInitializer actionInitializer)
	{
		super(ImportDMEOModel.actionType,actionInitializer);
	}
	
	@Override
	protected DMControllerActionInitializer getControllerActionInitializer() 
	{
		return (DMControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<ImportDMEOModel> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ImportDMEOModel>() {
            @Override
			public boolean run(ActionEvent e, ImportDMEOModel action)
            {
                File newEOModelFile = OpenEOModelComponent.getEOModelDirectory();
                if (newEOModelFile != null) {
                    if (logger.isLoggable(Level.INFO))
                        logger.info("Imports EOModel: " + newEOModelFile.getAbsolutePath());
                    action.setEOModelFile(newEOModelFile);
                    return true;
                }
                return false;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<ImportDMEOModel> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ImportDMEOModel>() {
            @Override
			public boolean run(ActionEvent e, ImportDMEOModel action)
            {
                if (getControllerActionInitializer().getDMController().getCurrentEditedObject() == action.getRepository()) {
                    if (logger.isLoggable(Level.FINE))
                        logger.fine("Finalizer for ImportDMEOModel in DMEORepository");
                    DMEORepositoryView repView = (DMEORepositoryView) getControllerActionInitializer().getDMController().getCurrentEditedObjectView();
                    repView.getEoModelTable().selectObject(action.getNewDMEOModel());
                }
                return true;
          }
        };
	}

     @Override
 	protected FlexoExceptionHandler<ImportDMEOModel> getDefaultExceptionHandler() 
 	{
 		return new FlexoExceptionHandler<ImportDMEOModel>() {
 			@Override
			public boolean handleException(FlexoException exception, ImportDMEOModel action) {
                if (exception instanceof InvalidEOModelFileException) {
                    FlexoController.showError(FlexoLocalization.localizedForKey("invalid_eo_model_file"));
                    return true;
                } else if (exception instanceof EOAccessException) {
                    FlexoController.showError(exception.getMessage());
                    return true;
                } else if (exception instanceof EOModelAlreadyRegisteredException) {
                    FlexoController.notify(FlexoLocalization.localizedForKey("eo_model_already_registered"));
                    return true;
                } else if (exception instanceof UnresovedEntitiesException) {
                    StringBuilder sb = new StringBuilder();
                    Iterator<String> i = ((UnresovedEntitiesException)exception).getMissingEntities().iterator();
                    while (i.hasNext()) {
                         sb.append("\n* ").append(i.next());
                    }
                    FlexoController.notify(FlexoLocalization.localizedForKey("the_following_entities_could_not_be_found:")+sb.toString());
                    return true;
                }
                return false;
			}
        };
 	}

  	@Override
	protected Icon getEnabledIcon() 
	{
		return DMEIconLibrary.DM_EOMODEL_ICON;
	}
 

}
