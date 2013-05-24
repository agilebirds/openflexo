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
package org.openflexo.prefs;

import java.io.File;
import java.util.Vector;

import org.openflexo.inspector.model.TabModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public abstract class ContextPreferences extends FlexoAbstractPreferences {

	private static FlexoPreferences preferences;

	protected static <CP extends ContextPreferences> CP preferences(Class<CP> contextPreferenceClass) {
		if (preferences == null) {
			preferences = FlexoPreferences.instance();
		}
		return preferences.get(contextPreferenceClass);
	}

	public ContextPreferences() {
		super(preferences);
	}

	public abstract String getName();

	public abstract File getInspectorFile();

	@Override
	public String getInspectorTitle() {
		return getName();
	}

	@Override
	public boolean isDeleted() {
		return false;
	}

	@Override
	public Vector<TabModel> inspectionExtraTabs() {
		return null;
	}

}
