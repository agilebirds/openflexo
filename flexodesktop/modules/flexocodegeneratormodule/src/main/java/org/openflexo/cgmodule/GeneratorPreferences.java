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
package org.openflexo.cgmodule;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.module.GeneratedResourceModifiedChoice;
import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;


/**
 * Please comment this class
 *
 * @author sguerin
 *
 */
public class GeneratorPreferences extends ModulePreferences
{

    private static final Logger logger = Logger.getLogger(GeneratorPreferences.class.getPackage().getName());

    private static final Class CG_PREFERENCES = GeneratorPreferences.class;

    protected static final String HighlightSyntax = "HighlightSyntax";
    
    protected static final String validateBeforeGeneratingKey = "validateBeforeGenerating";
    protected static final String saveBeforeGeneratingKey = "saveBeforeGenerating";
    protected static final String choiceWhenGeneratedResourceModifiedKey = "generatedResourceModifiedChoice";
    protected static final String automaticallyDismissUnchangedFilesKey = "automaticallyDismissUnchangedFiles";

    private static GeneratorController _controller;

    public static void init(GeneratorController controller)
    {
        _controller = controller;
        preferences(CG_PREFERENCES);
    }

    public static void reset() {
        _controller = null;
    }

    public GeneratorPreferences()
    {
        super(Module.CG_MODULE);
    }

    @Override
    public File getInspectorFile()
    {
        return new FileResource("Config/Preferences/CGPrefs.inspector");
    }

    public static boolean getValidateBeforeGenerating()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getValidateBeforeGenerating");
        Boolean returned = preferences(CG_PREFERENCES).getBooleanProperty(validateBeforeGeneratingKey);
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
        preferences(CG_PREFERENCES).setBooleanProperty(validateBeforeGeneratingKey, aBoolean);

    }

    public static boolean getSaveBeforeGenerating()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getSaveBeforeGenerating");
        Boolean returned = preferences(CG_PREFERENCES).getBooleanProperty(saveBeforeGeneratingKey);
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
        preferences(CG_PREFERENCES).setBooleanProperty(saveBeforeGeneratingKey, aBoolean);

    }

    public static GeneratedResourceModifiedChoice getGeneratedResourceModifiedChoice()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getLanguage");
        return GeneratedResourceModifiedChoice.get(preferences(CG_PREFERENCES).getProperty(choiceWhenGeneratedResourceModifiedKey));
    }

    public static void setGeneratedResourceModifiedChoice(GeneratedResourceModifiedChoice choice)
    {
        if (choice != null) {
            preferences(CG_PREFERENCES).setProperty(choiceWhenGeneratedResourceModifiedKey, choice.getIdentifier());
            _controller.getCGGeneratedResourceModifiedHook().setDefaultGeneratedResourceModifiedChoice(choice);
        }
     }

    public static boolean getAutomaticallyDismissUnchangedFiles()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getAutomaticallyDismissUnchangedFiles");
        Boolean returned = preferences(CG_PREFERENCES).getBooleanProperty(automaticallyDismissUnchangedFilesKey);
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
        preferences(CG_PREFERENCES).setBooleanProperty(automaticallyDismissUnchangedFilesKey, aBoolean);

    }

    public static boolean getHighlightSyntax()
    {
    	if (logger.isLoggable(Level.FINE))
    		logger.fine("getHighlightSyntax");
    	Boolean returned = preferences(CG_PREFERENCES).getBooleanProperty(HighlightSyntax);
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
    	preferences(CG_PREFERENCES).setBooleanProperty(HighlightSyntax, aBoolean);
    	
    }
    

}
