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
package org.openflexo.wse.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wse.controller.WSEController;
import org.openflexo.wse.view.WSEView;


import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.ws.ExternalWSService;
import org.openflexo.foundation.ws.InternalWSService;
import org.openflexo.foundation.ws.WSObject;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.foundation.ws.action.WSDelete;


public class WSDeleteInitializer extends ActionInitializer { 

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	WSDeleteInitializer(WSEControllerActionInitializer actionInitializer)
	{
		super(WSDelete.actionType,actionInitializer);
	}
	
	@Override
	protected WSEControllerActionInitializer getControllerActionInitializer() 
	{
		return (WSEControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<WSDelete> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<WSDelete>() {
            @Override
			public boolean run(ActionEvent e, WSDelete action)
            {
                 // Selection is empty, nothing to delete, forget it
                if (action.getObjectsToDelete().size() == 0) {
                    if (logger.isLoggable(Level.INFO))
                        logger.info("no objects to delete");
                    return false;
                }
                // Remember parent of last deleted object, in order to
                // eventually reselect it after deletion performance
                WSObject lastDeletedParent = null;
                // System.out.println("action.getObjectsToDelete()"+
                // action.getObjectsToDelete());
                FlexoModelObject lastDeleted = action.getObjectsToDelete().lastElement();
                if (lastDeleted != null && lastDeleted instanceof WSObject) {
                    lastDeletedParent = (WSObject) ((WSObject) lastDeleted).getParent();
                    action.setContext(lastDeletedParent);
                } else if (lastDeleted != null && lastDeleted instanceof ServiceInterface) {
                    WSService ws = getProject().getFlexoWSLibrary().getParentOfServiceInterface((ServiceInterface) lastDeleted);
                    lastDeletedParent = ws;
                    action.setContext(lastDeletedParent);
                }

                if (action.getFocusedObject() instanceof ExternalWSService) {
                    return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_external_webservice"));
                } else if (action.getFocusedObject() instanceof InternalWSService) {
                    return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_this_internal_webservice"));
                } else if (action.getFocusedObject() instanceof ServiceInterface) {
                    return FlexoController.confirm(FlexoLocalization
                            .localizedForKey("would_you_like_to_remove_this_service_interface_from_this_webservice"));
                } else
                    return false;
             }
        };
	}

     @Override
	protected FlexoActionFinalizer<WSDelete> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<WSDelete>() {
            @Override
			public boolean run(ActionEvent e, WSDelete action)
            {
                getControllerActionInitializer().getWSESelectionManager().resetSelection();
                // getDMSelectionManager().processDeletionOfSelected();
                WSEView view = (WSEView)getControllerActionInitializer().getWSEController().getCurrentModuleView();
                WSObject lastDeletedParent = (WSObject)action.getContext();
                // System.out.println("Parent:"+ lastDeletedParent);
                if (!lastDeletedParent.isDeleted()) {
                    view.tryToSelect(lastDeletedParent);
                }
                return true;
           }
        };
	}

     @Override
 	protected FlexoExceptionHandler<WSDelete> getDefaultExceptionHandler() 
 	{
 		return new FlexoExceptionHandler<WSDelete>() {
 			@Override
			public boolean handleException(FlexoException exception, WSDelete action) {
                if (exception.getLocalizationKey() != null) {
                    FlexoController.showError(FlexoLocalization.localizedForKey("ws_delete_not_completed") + " : "
                            + FlexoLocalization.localizedForKey(exception.getLocalizationKey()));
                    return true;
                } else {
                    FlexoController.showError(FlexoLocalization.localizedForKey("ws_delete_not_completed"));
                    return true;
                }
			}
        };
 	}


 	@Override
	protected Icon getEnabledIcon() 
	{
		return IconLibrary.DELETE_ICON;
	}

	@Override
	protected KeyStroke getShortcut() 
	{
		return KeyStroke.getKeyStroke(FlexoCst.BACKSPACE_DELETE_KEY_CODE, 0);
	}


	@Override
	public void init()
	{
        super.init();
        getControllerActionInitializer().registerAction(WSDelete.actionType,KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0));
	}
	
}
