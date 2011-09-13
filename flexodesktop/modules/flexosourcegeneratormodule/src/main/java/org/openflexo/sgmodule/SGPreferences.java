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
package org.openflexo.sgmodule;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.toolbox.FileResource;


/**
 * Contains preferences for this source generator module
 * 
 * @author sylvain
 * 
 */
public final class SGPreferences extends ModulePreferences
{
    
    private static final Logger logger = Logger.getLogger(SGPreferences.class.getPackage().getName());
    
    private static final Class SG_PREFERENCES = SGPreferences.class;
    
   protected static final String HighlightSyntax = "HighlightSyntax";
    
    protected static final String validateBeforeGeneratingKey = "validateBeforeGenerating";
    protected static final String saveBeforeGeneratingKey = "saveBeforeGenerating";
    protected static final String choiceWhenGeneratedResourceModifiedKey = "generatedResourceModifiedChoice";
    protected static final String automaticallyDismissUnchangedFilesKey = "automaticallyDismissUnchangedFiles";

    private static SGController _controller;
    
    public static void init(SGController controller)
    {
        _controller = controller;
        preferences(SG_PREFERENCES);
    }
    
    public SGPreferences()
    {
        super(Module.SG_MODULE);
    }
    
    @Override
	public File getInspectorFile()
    {
        return new FileResource("Config/Preferences/SGPrefs.inspector");
    }
    
    public static boolean getValidateBeforeGenerating()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getValidateBeforeGenerating");
        Boolean returned = preferences(SG_PREFERENCES).getBooleanProperty(validateBeforeGeneratingKey);
        if (returned == null) {
        	setValidateBeforeGenerating(true);
        	return true;
        }
        return returned;
    }

    public static void setValidateBeforeGenerating(boolean aBoolean)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getValidateBeforeGenerating");
        preferences(SG_PREFERENCES).setBooleanProperty(validateBeforeGeneratingKey, aBoolean);

    }

    public static boolean getSaveBeforeGenerating()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getSaveBeforeGenerating");
        Boolean returned = preferences(SG_PREFERENCES).getBooleanProperty(saveBeforeGeneratingKey);
        if (returned == null) {
        	setSaveBeforeGenerating(true);
        	return true;
        }
        return returned;
    }

    public static void setSaveBeforeGenerating(boolean aBoolean)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("setSaveBeforeGenerating");
        preferences(SG_PREFERENCES).setBooleanProperty(saveBeforeGeneratingKey, aBoolean);

    }

    public static GeneratedResourceModifiedChoice getGeneratedResourceModifiedChoice()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getLanguage");
        return GeneratedResourceModifiedChoice.get(preferences(SG_PREFERENCES).getProperty(choiceWhenGeneratedResourceModifiedKey));
    }

    public static void setGeneratedResourceModifiedChoice(GeneratedResourceModifiedChoice choice)
    {
        if (choice != null) {
            preferences(SG_PREFERENCES).setProperty(choiceWhenGeneratedResourceModifiedKey, choice.getIdentifier());
            _controller.getCGGeneratedResourceModifiedHook().setDefaultGeneratedResourceModifiedChoice(choice);
        }
     }

    public static boolean getAutomaticallyDismissUnchangedFiles()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getAutomaticallyDismissUnchangedFiles");
        Boolean returned = preferences(SG_PREFERENCES).getBooleanProperty(automaticallyDismissUnchangedFilesKey);
        if (returned == null) {
        	setAutomaticallyDismissUnchangedFiles(true);
        	return true;
        }
        return returned;
    }

    public static void setAutomaticallyDismissUnchangedFiles(boolean aBoolean)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("setAutomaticallyDismissUnchangedFiles");
        preferences(SG_PREFERENCES).setBooleanProperty(automaticallyDismissUnchangedFilesKey, aBoolean);

    }

    public static boolean getHighlightSyntax()
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("getHighlightSyntax");
    	Boolean returned = preferences(SG_PREFERENCES).getBooleanProperty(HighlightSyntax);
    	if (returned == null) {
    		setHighlightSyntax(true);
    		return true;
    	}
    	return returned;
    }
    
    public static void setHighlightSyntax(boolean aBoolean)
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("setHighlightSyntax");
    	preferences(SG_PREFERENCES).setBooleanProperty(HighlightSyntax, aBoolean);
    	
    }
    
    
}
