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

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.fps.CVSFile.FileContentEditor;
import org.openflexo.fps.action.EditCVSFile;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;


public class EditCVSFileInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	EditCVSFileInitializer(FPSControllerActionInitializer actionInitializer)
	{
		super(EditCVSFile.actionType,actionInitializer);
	}
	
	@Override
	protected FPSControllerActionInitializer getControllerActionInitializer() 
	{
		return (FPSControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<EditCVSFile> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<EditCVSFile>() {
            @Override
			public boolean run(ActionEvent e, EditCVSFile action)
            {
    			FileContentEditor editor = (FileContentEditor)getControllerActionInitializer()
    			.getFPSController().moduleViewForObject(action.getFocusedObject());
    			if (editor != null) {
    				action.setFileContentEditor(editor);
    				return true;
    			}
    			return false;
           }
        };
	}

     @Override
	protected FlexoActionFinalizer<EditCVSFile> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<EditCVSFile>() {
            @Override
			public boolean run(ActionEvent e, EditCVSFile action)
            {
            	getControllerActionInitializer().getFPSController().setCurrentEditedObjectAsModuleView(action.getFocusedObject());
            	getControllerActionInitializer().getFPSController().selectAndFocusObject(action.getFocusedObject());
    			return true;            	
          }
        };
	}

   	@Override
	protected Icon getEnabledIcon() 
	{
		return GeneratorIconLibrary.EDIT_ICON;
	}
 
	@Override
	protected Icon getDisabledIcon() 
	{
		return GeneratorIconLibrary.EDIT_DISABLED_ICON;
	}

}
