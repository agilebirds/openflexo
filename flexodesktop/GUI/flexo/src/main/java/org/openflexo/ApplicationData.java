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
package org.openflexo;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.icon.IconLibrary;
import org.openflexo.module.Module;

/**
 * Class storing general data for application
 * 
 * 
 * @author sguerin
 */
public class ApplicationData {
	private static final Logger logger = Logger.getLogger(ApplicationData.class.getPackage().getName());
	private final ApplicationContext applicationContext;

	public ApplicationData(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public Collection<Module<?>> getAvailableModules() {
		return applicationContext.getModuleLoader().getKnownModules();
	}

	public String getVersion() {
		return "Version " + FlexoCst.BUSINESS_APPLICATION_VERSION + " (build " + ApplicationVersion.BUILD_ID + ")";
	}

	public List<File> getLastOpenedProjects() {
		return applicationContext.getGeneralPreferences().getLastOpenedProjects();
	}

	public ImageIcon getProjectIcon() {
		return IconLibrary.OPENFLEXO_NOTEXT_16;
	}

	public ImageIcon getOpenflexoIcon() {
		return IconLibrary.OPENFLEXO_NOTEXT_64;
	}

	public ImageIcon getOpenflexoTextIcon() {
		return IconLibrary.OPENFLEXO_TEXT_SMALL_ICON;
	}

	public Module<?> getFavoriteModule() {
		Module<?> returned = applicationContext.getModuleLoader().getModuleNamed(
				applicationContext.getGeneralPreferences().getFavoriteModuleName());
		if (returned == null) {
			if (getAvailableModules().size() > 0) {
				return getAvailableModules().iterator().next();
			}
			logger.severe("No module found.");
			return null;
		} else {
			return returned;
		}
	}

	public void setFavoriteModule(Module<?> aModule) {
		if (aModule != null) {
			applicationContext.getGeneralPreferences().setFavoriteModuleName(aModule.getName());
		}
	}
}
