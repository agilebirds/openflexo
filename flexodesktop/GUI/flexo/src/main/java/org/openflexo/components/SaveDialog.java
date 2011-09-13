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

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.module.FlexoModule;

/**
 * @author gpolet
 * 
 */
public class SaveDialog extends JOptionPane
{

    private static final Logger logger = FlexoLogger.getLogger(SaveDialog.class.getPackage().getName());

    private int retval = JOptionPane.CANCEL_OPTION;

    private FlexoProject project;
	
    /**
     * 
     */
    public SaveDialog(Component parent, FlexoProject project)
    {
        this.project = project;
        if(FlexoModule.getActiveModule()!=null)
            FlexoModule.getActiveModule().getFlexoController().dismountWindowsOnTop(null);
        retval = JOptionPane.showConfirmDialog(parent, FlexoLocalization.localizedForKey("project_has_unsaved_changes") + "\n"
                + FlexoLocalization.localizedForKey("would_you_like_to_save_the_changes?"), FlexoLocalization
                .localizedForKey("exiting_flexo"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(FlexoModule.getActiveModule()!=null)
            FlexoModule.getActiveModule().getFlexoController().remountWindowsOnTop();
    }

    public SaveDialog(Component parent, FlexoProject project, FlexoModule module)
    {
        this.project = project;
        if(FlexoModule.getActiveModule()!=null)
            FlexoModule.getActiveModule().getFlexoController().dismountWindowsOnTop(null);
        retval = JOptionPane.showConfirmDialog(parent, FlexoLocalization.localizedForKey("project_has_unsaved_changes") + "\n"
                + FlexoLocalization.localizedForKey("would_you_like_to_save_the_changes?"), FlexoLocalization.localizedForKey("exiting ")
                + module.getName(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(FlexoModule.getActiveModule()!=null)
            FlexoModule.getActiveModule().getFlexoController().remountWindowsOnTop();
    }

    public int getRetval()
    {
        return retval;
    }

    public void saveProject(FlexoProgress progress) throws SaveResourceException
    {
        if (project != null) {
            project.save(progress);
        } else {
            if (logger.isLoggable(Level.WARNING))
                logger.warning("Project is null");
        }
    }
}
