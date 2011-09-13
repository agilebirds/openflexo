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
package org.openflexo.doceditor;

import java.io.File;
import java.util.logging.Logger;

import org.openflexo.doceditor.controller.DEController;
import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;


/**
 * Please comment this class
 *
 * @author gpolet
 *
 */
public class DEPreferences extends ModulePreferences
{

    private static final Logger logger = Logger.getLogger(DEPreferences.class.getPackage().getName());

    private static final Class DE_PREFERENCES = DEPreferences.class;

    private static DEController _controller;

    public static void init(DEController c)
    {
        _controller = c;
        preferences(DE_PREFERENCES);
    }

    public static void reset() {
        _controller = null;
    }

    public DEPreferences()
    {
        super(Module.DE_MODULE);
    }

    protected DEPreferences(Module module)
    {
        super(module);
    }
    
    @Override
    public File getInspectorFile()
    {
        return new FileResource("Config/Preferences/DEPrefs.inspector");
    }

}
