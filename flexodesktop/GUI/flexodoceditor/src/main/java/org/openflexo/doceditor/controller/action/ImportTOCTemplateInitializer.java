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
package org.openflexo.doceditor.controller.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.action.ImportTOCTemplate;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;

public class ImportTOCTemplateInitializer extends ActionInitializer
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

    ImportTOCTemplateInitializer(DEControllerActionInitializer actionInitializer)
    {
        super(ImportTOCTemplate.actionType, actionInitializer);
    }

    @Override
	protected DEControllerActionInitializer getControllerActionInitializer()
    {
        return (DEControllerActionInitializer) super.getControllerActionInitializer();
    }

    @Override
    protected FlexoActionInitializer<ImportTOCTemplate> getDefaultInitializer()
    {
        return new FlexoActionInitializer<ImportTOCTemplate>() {
            @Override
			public boolean run(ActionEvent e, ImportTOCTemplate action)
            {
            	
            	JFileChooser chooser = new JFileChooser();
            	chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            	chooser.setDialogTitle(FlexoLocalization.localizedForKey("select_toc_template", chooser));
            	
            	
            	File src = null;
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.CANCEL_OPTION)
                    return false;
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                     src = chooser.getSelectedFile();
                } else {
                    return false;
                }

                
                if (!src.exists()) return false;
                if (!src.isFile()) return false;
					
                action.setSourceFile(src);
                return true;
            }
            
        };
    }


}
