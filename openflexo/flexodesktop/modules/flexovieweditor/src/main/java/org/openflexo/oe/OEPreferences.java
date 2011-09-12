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
package org.openflexo.oe;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.module.Module;
import org.openflexo.oe.controller.OEController;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;


/**
 * Contains preferences for this module
 * 
 * @author yourname
 * 
 */
public final class OEPreferences extends ModulePreferences
{
    
    private static final Logger logger = Logger.getLogger(OEPreferences.class.getPackage().getName());
    
    private static final Class OE_PREFERENCES = OEPreferences.class;
    
	protected static final String SCREENSHOT_QUALITY = "screenshotQuality";

   protected static final String EXAMPLE_PREF_VALUE = "examplePrefValue";
    
    private static OEController _controller;
    
    public static void init(OEController controller)
    {
        _controller = controller;
        preferences(OE_PREFERENCES);
    }
    
    public OEPreferences()
    {
        super(Module.OE_MODULE);
    }
    
    @Override
	public File getInspectorFile()
    {
        return new FileResource("Config" + File.separator + "Preferences" + File.separator + "OEPrefs.inspector");
    }
    
    public static Boolean getExamplePrefValue()
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("getExamplePrefValue");
        Boolean value = preferences(OE_PREFERENCES).getBooleanProperty(EXAMPLE_PREF_VALUE);
        if (value == null) {
            setExamplePrefValue(Boolean.FALSE);
            return getExamplePrefValue();
        }
        return value;
    }
    
    public static void setExamplePrefValue(Boolean alignOnGrid)
    {
        if (logger.isLoggable(Level.FINE))
            logger.fine("setExamplePrefValue");
        preferences(OE_PREFERENCES).setBooleanProperty(EXAMPLE_PREF_VALUE, alignOnGrid);
    }
    
    public static int getScreenshotQuality(){
        Integer limit = preferences(OE_PREFERENCES).getIntegerProperty(SCREENSHOT_QUALITY);
        if(limit==null)limit=100;
        return limit;
    }

    public static void setScreenshotQuality(int limit) {
        preferences(OE_PREFERENCES).setIntegerProperty(SCREENSHOT_QUALITY, limit);
    }


    
}
