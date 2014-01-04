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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.AdvancedPrefs;
import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.foundation.FlexoService;
import org.openflexo.foundation.FlexoServiceImpl;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.model.ModelContext;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.module.Module;
import org.openflexo.prefs.FlexoPreferencesResource.FlexoPreferencesResourceImpl;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

/**
 * This service manages preferences<br>
 * 
 * Preferences are organized into logical {@link FlexoPreferences} units identified by a String identifier.
 * 
 * 
 * @author sguerin
 */
public class PreferencesService extends FlexoServiceImpl implements FlexoService, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(PreferencesService.class.getPackage().getName());

	private FlexoPreferencesFactory preferencesFactory;
	private FlexoPreferencesResource resource;

	@Override
	public ApplicationContext getServiceManager() {
		return (ApplicationContext) super.getServiceManager();
	}

	public FlexoPreferences getFlexoPreferences() {
		return resource.getFlexoPreferences();
	}

	private <P extends PreferencesContainer> P managePreferences(Class<P> prefClass, PreferencesContainer container) {
		P returned = getPreferences(prefClass);
		if (returned == null) {
			returned = getPreferencesFactory().newInstance(prefClass);
			initPreferences(returned);
			container.addToContainers(returned);
		}
		return returned;
	}

	private void initPreferences(PreferencesContainer p) {

	}

	@Override
	public void initialize() {
		try {
			initPreferencesFactory();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		resource = FlexoPreferencesResourceImpl.makePreferencesResource(getServiceManager(), getPreferencesFactory());
		managePreferences(GeneralPreferences.class, getFlexoPreferences());
		managePreferences(AdvancedPrefs.class, getFlexoPreferences());
		for (Module<?> m : getServiceManager().getModuleLoader().getKnownModules()) {
			managePreferences(m.getPreferencesClass(), getFlexoPreferences());
		}
	}

	@Override
	public void receiveNotification(FlexoService caller, ServiceNotification notification) {
		logger.fine("PreferencesService received notification " + notification + " from " + caller);
	}

	public void savePreferences() {
		try {
			resource.save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
	}

	public void revertToSaved() {
		// TODO: reimplement this
	}

	public FlexoPreferencesFactory getPreferencesFactory() {
		return preferencesFactory;
	}

	public File getLogDirectory() {
		File outputDir = new File(System.getProperty("user.home") + "/Library/Logs/OpenFlexo");
		if (ToolBox.getPLATFORM() == ToolBox.WINDOWS) {
			boolean ok = false;
			String appData = System.getenv("LOCALAPPDATA");
			if (appData != null) {
				File f = new File(appData);
				if (f.isDirectory() && f.canWrite()) {
					outputDir = new File(f, "OpenFlexo/Logs");
					ok = true;
				}
				if (!ok) {
					appData = System.getenv("APPDATA");
					if (appData != null) {
						f = new File(appData);
						if (f.isDirectory() && f.canWrite()) {
							outputDir = new File(f, "OpenFlexo/Logs");
							ok = true;
						}
					}
				}
			}
		} else if (ToolBox.getPLATFORM() == ToolBox.LINUX) {
			outputDir = new File("user.home", ".openflexo/logs");
		}
		return outputDir;
	}

	private void initPreferencesFactory() throws ModelDefinitionException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(FlexoPreferences.class);
		classes.add(GeneralPreferences.class);
		classes.add(AdvancedPrefs.class);
		for (Module<?> m : getServiceManager().getModuleLoader().getKnownModules()) {
			classes.add(m.getPreferencesClass());
		}
		preferencesFactory = new FlexoPreferencesFactory(ModelContextLibrary.getCompoundModelContext(classes.toArray(new Class<?>[classes
				.size()])));
	}

	public static class FlexoPreferencesFactory extends ModelFactory {

		public FlexoPreferencesFactory(ModelContext context) throws ModelDefinitionException {
			super(context);
		}
	}

	public <P extends PreferencesContainer> P getPreferences(Class<P> containerType) {
		return getFlexoPreferences().getPreferences(containerType);
	}
}
