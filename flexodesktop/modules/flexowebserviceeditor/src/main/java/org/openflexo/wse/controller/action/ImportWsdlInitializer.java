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
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.ws.action.ImportWsdl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

public class ImportWsdlInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	ImportWsdlInitializer(WSEControllerActionInitializer actionInitializer)
	{
		super(ImportWsdl.actionType,actionInitializer);
	}
	
	@Override
	protected WSEControllerActionInitializer getControllerActionInitializer() 
	{
		return (WSEControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<ImportWsdl> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<ImportWsdl>() {
            @Override
			public boolean run(ActionEvent e, ImportWsdl action)
            {
                JFileChooser chooser = new JFileChooser("Please select an .wsdl file");
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION)
                    return false;

                action.setWsdlFile(chooser.getSelectedFile());
                String newWSName = FlexoController.askForString(FlexoLocalization.localizedForKey("enter_name_of_the_web_service"));
                action.setNewWebServiceName(newWSName);
                action.setProject(getProject());
                return true;
            }
        };
	}

     @Override
	protected FlexoActionFinalizer<ImportWsdl> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<ImportWsdl>() {
            @Override
			public boolean run(ActionEvent e, ImportWsdl action)
            {
                logger.info("finalize WS");
                return true;
          }
        };
	}

  }
