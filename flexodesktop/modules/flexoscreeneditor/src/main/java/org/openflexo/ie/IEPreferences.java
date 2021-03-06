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
package org.openflexo.ie;

import java.io.File;

import org.openflexo.module.Module;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.toolbox.FileResource;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public final class IEPreferences extends ModulePreferences {

	private static final Class<IEPreferences> IE_PREFERENCES = IEPreferences.class;

	public static final String SHOW_BINDINGVALUE_KEY = "showBindingValueInIE";

	public static void init() {
		getPreferences();
	}

	public static IEPreferences getPreferences() {
		return preferences(IE_PREFERENCES);
	}

	public IEPreferences() {
		super(Module.IE_MODULE);
	}

	@Override
	public File getInspectorFile() {
		return new FileResource("Config/Preferences/IEPrefs.inspector");
	}

	public static Boolean getDisplayBindingValue() {
		Boolean value = getPreferences().getBooleanProperty(SHOW_BINDINGVALUE_KEY);
		if (value == null) {
			return Boolean.FALSE;
		}
		return value;
	}

	public static void setDisplayBindingValue(Boolean displayBindingValue) {
		if (displayBindingValue.booleanValue() == getDisplayBindingValue().booleanValue()) {
			return;
		}
		getPreferences().setBooleanProperty(SHOW_BINDINGVALUE_KEY, displayBindingValue);
	}

}
