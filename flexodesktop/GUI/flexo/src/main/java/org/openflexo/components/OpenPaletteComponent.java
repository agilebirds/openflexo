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
package org.openflexo.components;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

public class OpenPaletteComponent extends PaletteChooserComponent
{

    private static final Logger logger = Logger.getLogger(OpenProjectComponent.class.getPackage()
            .getName());

    protected OpenPaletteComponent()
    {
        super();

    }

    public static File getPaletteDirectory()
    {
    	OpenPaletteComponent chooser = new OpenPaletteComponent();
        File returned = null;
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            returned = chooser.getSelectedFile();
        } else {
            if (logger.isLoggable(Level.INFO))
                logger.info("No palette specified !");
        }
        return returned;
    }
}
