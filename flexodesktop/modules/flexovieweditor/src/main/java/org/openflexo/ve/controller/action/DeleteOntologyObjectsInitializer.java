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
package org.openflexo.ve.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ontology.action.DeleteOntologyObjects;
import org.openflexo.icon.IconLibrary;
import org.openflexo.ve.VECst;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class DeleteOntologyObjectsInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteOntologyObjectsInitializer(OEControllerActionInitializer actionInitializer)
	{
		super(DeleteOntologyObjects.actionType,actionInitializer);
	}
	
	@Override
	protected OEControllerActionInitializer getControllerActionInitializer() 
	{
		return (OEControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<DeleteOntologyObjects> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DeleteOntologyObjects>() {
            @Override
			public boolean run(ActionEvent e, DeleteOntologyObjects action)
            {
				FIBDialog dialog = FIBDialog.instanciateComponent(
						VECst.DELETE_ONTOLOGY_OBJECTS_DIALOG_FIB,
						action, null, true);
				return (dialog.getStatus() == Status.VALIDATED);
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<DeleteOntologyObjects> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DeleteOntologyObjects>() {
            @Override
			public boolean run(ActionEvent e, DeleteOntologyObjects action)
            {
                if (getControllerActionInitializer().getOEController().getSelectionManager().getLastSelectedObject()!=null
                		&& getControllerActionInitializer().getOEController().getSelectionManager().getLastSelectedObject().isDeleted())
                	getControllerActionInitializer().getOEController().getSelectionManager().resetSelection();
                return true;
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
        getControllerActionInitializer().registerAction(DeleteOntologyObjects.actionType,KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0));
	}
	

}
