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

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.Module;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
@ModelEntity(isAbstract = true)
public interface ModulePreferences<M extends FlexoModule> extends PreferencesContainer {

	@PropertyIdentifier(type = Module.class)
	public static final String MODULE_KEY = "module";

	@Getter(value = MODULE_KEY)
	public Module<M> getModule();

	@Setter(MODULE_KEY)
	public void setModule(Module<M> module);

}
