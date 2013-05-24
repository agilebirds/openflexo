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
package org.openflexo.dre;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;

/**
 * Contains preferences for this module
 * 
 * @author yourname
 * 
 */
public final class DREPreferences extends ModulePreferences {

	private static final Logger logger = Logger.getLogger(DREPreferences.class.getPackage().getName());

	private static final Class<DREPreferences> DRE_PREFERENCES = DREPreferences.class;

	protected static final String EXAMPLE_PREF_VALUE = "examplePrefValue";

	public static void init() {
		preferences(DRE_PREFERENCES);
	}

	public static void reset() {
	}

	public DREPreferences() {
		super(Module.DRE_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/DREPrefs.inspector");
	}

	public static Boolean getExamplePrefValue() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("getExamplePrefValue");
		}
		Boolean value = preferences(DRE_PREFERENCES).getBooleanProperty(EXAMPLE_PREF_VALUE);
		if (value == null) {
			setExamplePrefValue(Boolean.FALSE);
			return getExamplePrefValue();
		}
		return value;
	}

	public static void setExamplePrefValue(Boolean alignOnGrid) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("setExamplePrefValue");
		}
		preferences(DRE_PREFERENCES).setBooleanProperty(EXAMPLE_PREF_VALUE, alignOnGrid);
	}

}
