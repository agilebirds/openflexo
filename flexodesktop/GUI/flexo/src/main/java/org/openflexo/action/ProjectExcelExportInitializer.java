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
package org.openflexo.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.view.FlexoFrame;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;

/**
 * @author gpolet
 * 
 */
public class ProjectExcelExportInitializer extends ActionInitializer
{

    /**
     * @param actionType
     * @param controllerActionInitializer
     */
    public ProjectExcelExportInitializer(ControllerActionInitializer controllerActionInitializer)
    {
        super(ProjectExcelExportAction.actionType, controllerActionInitializer);
    }

    protected static FlexoFrame getActiveModuleFrame()
    {
        if (FlexoModule.getActiveModule() != null) {
            return FlexoModule.getActiveModule().getFlexoFrame();
        } else {
            return null;
        }
    }

    /**
     * Overrides getDefaultInitializer
     * 
     * @see org.openflexo.view.controller.ActionInitializer#getDefaultInitializer()
     */
    @Override
    protected FlexoActionInitializer<ProjectExcelExportAction> getDefaultInitializer()
    {

        return new FlexoActionInitializer<ProjectExcelExportAction>() {

            @Override
            public boolean run(ActionEvent event, ProjectExcelExportAction action)
            {
                if (FlexoModule.getActiveModule()!=null) {
					FlexoModule.getActiveModule().getFlexoController().dismountWindowsOnTop(null);
				}
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setSelectedFile(new File(System.getProperty("user.home"), action.getFocusedObject()
                        .getProjectName()
                        + ".csv"));
                int ret = chooser.showSaveDialog(getActiveModuleFrame());
                if (FlexoModule.getActiveModule()!=null) {
					FlexoModule.getActiveModule().getFlexoController().remountWindowsOnTop();
				}
                if (ret == JFileChooser.APPROVE_OPTION) {
                    action.getFocusedObject().getStatistics().refresh();
                    String s = action.getFocusedObject().getStatistics().excel();
                    File out = chooser.getSelectedFile();
                    if (!out.getName().endsWith(".csv")) {
                        out = new File(out.getAbsolutePath() + ".csv");
                    }
                    boolean doIt = false;
                    if (out.exists()) {
                        if (FlexoController.confirm(FlexoLocalization.localizedForKey("the_file") + " " + out.getName() + " "
                                + FlexoLocalization.localizedForKey("already_exists") + "\n"
                                + FlexoLocalization.localizedForKey("do_you_want_to_replace_it?"))) {
                            doIt = true; // the file exists but the user has confirmed the replacement
                        }
                    } else {
						doIt = true; // the file does not exist
					}
                    if (doIt) {
                        try {
                            FileUtils.saveToFile(out, s);
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            FlexoController.showError(FlexoLocalization.localizedForKey("export_failed"));
                            return false;
                        }
                    }
                }
                return false;
            }

        };
    }
    
    @Override
    protected Icon getEnabledIcon() 
    {
    	return IconLibrary.BIG_EXCEL_ICON;
    }
    
}
