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
package org.openflexo.dm;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class DMPreferences extends ModulePreferences {

	private static final Logger logger = Logger.getLogger(DMPreferences.class.getPackage().getName());

	private static final Class<DMPreferences> DM_PREFERENCES = DMPreferences.class;

	// Be carefull: this is the same value as the one in Code Generator Module
	// !!!
	protected static final String preferedOutputDirectoryKey = "preferedOutputDirectory";

	public static void init() {
		preferences(DM_PREFERENCES);
	}

	public static void reset() {

	}

	public DMPreferences() {
		super(Module.DM_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/DMPrefs.inspector");
	}

	public static File getPreferedOutputDirectory() {
		File returnedFile;
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getPreferedOutputDirectory");
		}
		returnedFile = preferences(DM_PREFERENCES).getDirectoryProperty(preferedOutputDirectoryKey, true);
		if (returnedFile != null) {
			return returnedFile;
		} else {
			return new File(System.getProperty("user.home"));
		}
	}

	public static void setPreferedOutputDirectory(File f) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setPreferedOutputDirectory");
		}
		preferences(DM_PREFERENCES).setDirectoryProperty(preferedOutputDirectoryKey, f, true);
	}
}
