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

import java.awt.Frame;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.openflexo.AdvancedPrefs;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.prefs.FlexoPreferences;
import org.openflexo.view.FlexoFrame;


/**
 * Component allowing to choose an existing flexo project
 * 
 * @author sguerin
 */
public class OpenProjectComponent extends ProjectChooserComponent
{

    private static final Logger logger = Logger.getLogger(OpenProjectComponent.class.getPackage().getName());

    protected OpenProjectComponent(Frame owner)
    {
        super(owner);
        logger.info("Build OpenProjectComponent");
    }

    public static File getProjectDirectory() throws ProjectLoadingCancelledException
    {
    	return getProjectDirectory(FlexoFrame.getActiveFrame());
    }
    
     public static File getProjectDirectory(Frame owner) throws ProjectLoadingCancelledException
    {
        OpenProjectComponent chooser = new OpenProjectComponent(owner);
        File returned = null;
        int returnVal=-1;
        boolean ok = false;
        while (!ok) {
            try {
                returnVal = chooser.showOpenDialog();
                ok=true;
            } catch (ArrayIndexOutOfBoundsException e) {
                if (logger.isLoggable(Level.WARNING))
                    logger.warning("Caught ArrayIndexOutOfBoundsException, hope this will stop");
            }
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            returned = chooser.getSelectedFile();
            AdvancedPrefs.setLastVisitedDirectory(returned.getParentFile());
            FlexoPreferences.savePreferences(true);
        } else {
            if (logger.isLoggable(Level.FINE))
                logger.fine("No project supplied");
            throw new ProjectLoadingCancelledException();
        }
        return returned;
    }
}
