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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.openflexo.FlexoCst;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ontology.calc.action.DeleteCalcShemaElements;

public class DeleteCalcShemaElementsInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	DeleteCalcShemaElementsInitializer(CEDControllerActionInitializer actionInitializer)
	{
		super(DeleteCalcShemaElements.actionType,actionInitializer);
	}
	
	@Override
	protected CEDControllerActionInitializer getControllerActionInitializer() 
	{
		return (CEDControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<DeleteCalcShemaElements> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<DeleteCalcShemaElements>() {
            @Override
			public boolean run(ActionEvent e, DeleteCalcShemaElements action)
            {
            	return FlexoController.confirm(FlexoLocalization.localizedForKey("would_you_like_to_delete_those_objects"));
             }
        };
	}

     @Override
	protected FlexoActionFinalizer<DeleteCalcShemaElements> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<DeleteCalcShemaElements>() {
            @Override
			public boolean run(ActionEvent e, DeleteCalcShemaElements action)
            {
                if (getControllerActionInitializer().getCEDController().getSelectionManager().getLastSelectedObject()!=null
                		&& getControllerActionInitializer().getCEDController().getSelectionManager().getLastSelectedObject().isDeleted())
                	getControllerActionInitializer().getCEDController().getSelectionManager().resetSelection();
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
        getControllerActionInitializer().registerAction(DeleteCalcShemaElements.actionType,KeyStroke.getKeyStroke(FlexoCst.DELETE_KEY_CODE, 0));
	}
	

}
