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
package org.openflexo.sgmodule.controller.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import org.openflexo.components.MultipleObjectSelectorPopup;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.generator.action.MarkAsMergedAllTrivialMergableFiles;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.view.popup.SelectFilesPopup;
import org.openflexo.view.controller.ActionInitializer;
import org.openflexo.view.controller.ControllerActionInitializer;
import org.openflexo.view.controller.FlexoController;


public class MarkAsMergedAllTrivialMergableFilesInitializer extends ActionInitializer {

	private static final Logger logger = Logger.getLogger(ControllerActionInitializer.class.getPackage().getName());

	MarkAsMergedAllTrivialMergableFilesInitializer(SGControllerActionInitializer actionInitializer)
	{
		super(MarkAsMergedAllTrivialMergableFiles.actionType,actionInitializer);
	}
	
	@Override
	protected SGControllerActionInitializer getControllerActionInitializer() 
	{
		return (SGControllerActionInitializer)super.getControllerActionInitializer();
	}
	
	@Override
	protected FlexoActionInitializer<MarkAsMergedAllTrivialMergableFiles> getDefaultInitializer() 
	{
		return new FlexoActionInitializer<MarkAsMergedAllTrivialMergableFiles>() {
            @Override
			public boolean run(ActionEvent e, MarkAsMergedAllTrivialMergableFiles action)
            {
            	if (action.getTrivialMergableFiles().size() == 0) {
            		FlexoController.notify(FlexoLocalization.localizedForKey("no_files_found_as_trivially_mergable"));
            		return false;
            	}
               	else if (action.getTrivialMergableFiles().size() > 1 
            			|| (!(action.getFocusedObject() instanceof CGFile))) {
            		SelectFilesPopup popup = new SelectFilesPopup(FlexoLocalization
							.localizedForKey("mark_as_merged_all_trivially_mergable_files"), FlexoLocalization
							.localizedForKey("mark_as_merged_all_trivially_mergable_files_description"), "mark_as_merged", action
							.getTrivialMergableFiles(), action.getFocusedObject().getProject(), getControllerActionInitializer()
							.getSGController());
            		popup.setVisible(true);
            		if ((popup.getStatus() == MultipleObjectSelectorPopup.VALIDATE) 
            				&& (popup.getFileSet().getSelectedFiles().size() > 0)) {
            			action.setTrivialMergableFiles(popup.getFileSet().getSelectedFiles());
            		}
            		else {
            			return false;
            		}
            	}
            	else {
            		// 1 occurence, continue without confirmation
            	}
               action.getProjectGenerator().startHandleLogs();
                return true;
           }
        };
	}

     @Override
	protected FlexoActionFinalizer<MarkAsMergedAllTrivialMergableFiles> getDefaultFinalizer() 
	{
		return new FlexoActionFinalizer<MarkAsMergedAllTrivialMergableFiles>() {
            @Override
			public boolean run(ActionEvent e, MarkAsMergedAllTrivialMergableFiles action)
            {
                action.getProjectGenerator().stopHandleLogs();
                action.getProjectGenerator().flushLogs();
                return true;
           }
        };
	}

}
