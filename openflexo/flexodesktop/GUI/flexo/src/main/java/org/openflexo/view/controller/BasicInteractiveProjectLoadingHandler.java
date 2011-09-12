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
package org.openflexo.view.controller;

import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.icon.CGIconLibrary;
import org.openflexo.localization.FlexoLocalization;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;

public class BasicInteractiveProjectLoadingHandler extends InteractiveProjectLoadingHandler {

    private static final Logger logger = Logger.getLogger(BasicInteractiveProjectLoadingHandler.class.getPackage().getName());
    
    private boolean alreadyAnswer = false;
    private boolean convertProject = false;
    private File _projectDirectory;
    
    public BasicInteractiveProjectLoadingHandler(File projectDirectory)
    {
    	super();
    	_projectDirectory = projectDirectory;
    }

    private boolean askForProjectConversion() throws ProjectLoadingCancelledException 
    {
    	String CONVERT = FlexoLocalization.localizedForKey("convert_project");
    	String DONT_CONVERT = FlexoLocalization.localizedForKey("don't_convert_project");
    	String CANCEL = FlexoLocalization.localizedForKey("cancel");
    	int choice = FlexoController.selectOption(
    			"<html><center>"+CGIconLibrary.UNFIXABLE_WARNING_ICON.getHTMLImg()+"<b>&nbsp;"+FlexoLocalization.localizedForKey("warning")+"</b></center><br>"
				+"<center>"+_projectDirectory.getName()+"</center><br>"
    			+FlexoLocalization.localizedForKey("this_project_seems_to_have_been_created_with_an_older_version_of_flexo")+"<br>"
				+FlexoLocalization.localizedForKey("would_you_like_to_convert_entire_project_to_new_version_of_flexo_(recommanded)")+"<br></html>",
    			CONVERT, CONVERT, DONT_CONVERT, CANCEL);


    	if (choice == 0) { // CONVERT
    		alreadyAnswer = true;
    		return true;
    	}
     	else if (choice == 1) { // DONT_CONVERT
       		alreadyAnswer = true;
       		return false;
    	}
    	else {			
    		throw new ProjectLoadingCancelledException();
    	}
    }

    @Override
	public boolean upgradeResourceToLatestVersion(FlexoXMLStorageResource resource)
    throws ProjectLoadingCancelledException 
    {
    	if (isPerformingAutomaticConversion()) return true;

    	if (!alreadyAnswer) {
    		convertProject = askForProjectConversion();
     	}
   		return convertProject;
   	    	
     }

    @Override
	public boolean useOlderMappingWhenLoadingFailure(FlexoXMLStorageResource resource)
    throws ProjectLoadingCancelledException
    {
    	return true;
    }

    @Override
	public boolean loadAndConvertAllOldResourcesToLatestVersion(FlexoProject project, FlexoProgress progress)
    throws ProjectLoadingCancelledException 
    {
    	Vector<ResourceToConvert> resourcesToConvert = searchResourcesToConvert(project);

    	if (resourcesToConvert.size() > 0) {
 
        	if (!alreadyAnswer) {
        		convertProject = askForProjectConversion();
         	}
 
        	if (convertProject) {
     			performConversion(project, resourcesToConvert, progress);
    			return true;
    		}
    	}

    	return false;
    }

 
}
