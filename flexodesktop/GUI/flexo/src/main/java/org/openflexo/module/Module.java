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
package org.openflexo.module;

import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.openflexo.ApplicationContext;
import org.openflexo.GeneralPreferences;
import org.openflexo.ch.FCH;
import org.openflexo.components.ProgressWindow;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocResourceManager;
import org.openflexo.drm.Language;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.prefs.ModulePreferences;
import org.openflexo.swing.FlexoSwingUtils;

/**
 * Represents a Module in Openflexo intrastructure. A module is a software component.
 * 
 * An instance of {@link Module} is a {@link FlexoModule} instance which is retrieved through service management.<br>
 * A {@link Module} is not loaded by default, but need to be loaded by the {@link ModuleLoader}.
 * 
 * @author sguerin
 * 
 */
public abstract class Module<M extends FlexoModule> {

	private static final Logger logger = Logger.getLogger(Module.class.getPackage().getName());

	private final boolean notFoundNotified = false;

	private Constructor<M> constructor;

	private final String name;
	private final String shortName;
	private final Class<M> moduleClass;
	private final Class<? extends ModulePreferences<M>> preferencesClass;
	private final String relativeDirectory;
	private final String jiraComponentID;
	private final String helpTopic;
	private final ImageIcon smallIcon;
	private final ImageIcon mediumIcon;
	private final ImageIcon mediumIconWithHover;
	private final ImageIcon bigIcon;
	private final boolean requiresProject;

	private ModuleLoader moduleLoader;

	private M loadedModuleInstance;

	protected Module(String name, String shortName, Class<M> moduleClass, Class<? extends ModulePreferences<M>> preferencesClass,
			String relativeDirectory, String jiraComponentID, String helpTopic, ImageIcon smallIcon, ImageIcon mediumIcon,
			ImageIcon mediumIconWithHover, ImageIcon bigIcon, boolean requiresProject) {
		super();
		this.name = name;
		this.shortName = shortName;
		this.moduleClass = moduleClass;
		this.preferencesClass = preferencesClass;
		this.relativeDirectory = relativeDirectory;
		this.jiraComponentID = jiraComponentID;
		this.helpTopic = helpTopic;
		this.smallIcon = smallIcon;
		this.mediumIcon = mediumIcon;
		this.mediumIconWithHover = mediumIconWithHover;
		this.bigIcon = bigIcon;
		this.requiresProject = requiresProject;
	}

	public ModuleLoader getModuleLoader() {
		return moduleLoader;
	}

	public void setModuleLoader(ModuleLoader moduleLoader) {
		this.moduleLoader = moduleLoader;
	}

	public Constructor<M> getConstructor() {
		return constructor;
	}

	public Class<? extends ModulePreferences<M>> getPreferencesClass() {
		return preferencesClass;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public Class<M> getModuleClass() {
		return moduleClass;
	}

	protected String getRelativeDirectory() {
		return relativeDirectory;
	}

	public String getJiraComponentID() {
		return jiraComponentID;
	}

	public String getHelpTopic() {
		return helpTopic;
	}

	public ImageIcon getSmallIcon() {
		return smallIcon;
	}

	public ImageIcon getMediumIcon() {
		return mediumIcon;
	}

	public ImageIcon getMediumIconWithHover() {
		return mediumIconWithHover;
	}

	public ImageIcon getBigIcon() {
		return bigIcon;
	}

	public boolean requireProject() {
		return requiresProject;
	}

	public boolean isNotFoundNotified() {
		return notFoundNotified;
	}

	public String getLocalizedName() {
		return FlexoLocalization.localizedForKey(getName());
	}

	public final String getDescription() {
		return getName() + "_description";
	}

	public String getLocalizedDescription() {
		return FlexoLocalization.localizedForKey(getDescription());
	}

	public boolean register() {
		constructor = lookupConstructor();
		return constructor != null;
	}

	/**
	 * Internally used to lookup constructor
	 * 
	 */
	private Constructor<M> lookupConstructor() {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Registering module '" + getName() + "'");
		}
		Class[] constructorSigner;
		constructorSigner = new Class[1];
		constructorSigner[0] = ApplicationContext.class;
		try {
			Constructor<M> constructor = getModuleClass().getDeclaredConstructor(constructorSigner);
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Contructor:" + constructor);
			}
			return constructor;
		} catch (SecurityException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("SecurityException raised during module " + getName() + " registering. Aborting.");
			}
		} catch (NoSuchMethodException e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("NoSuchMethodException raised during module " + getName() + " registering. Aborting.");
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return getLocalizedName();
	}

	public String getHTMLDescription() {
		Language language = DocResourceManager.instance().getLanguage(GeneralPreferences.getLanguage());
		DocItem docItem = DocResourceManager.getDocItem(getHelpTopic());
		if (docItem != null) {
			if (docItem.getLastApprovedActionForLanguage(language) != null) {
				String returned = "<html>" + docItem.getLastApprovedActionForLanguage(language).getVersion().getFullHTMLDescription()
						+ "</html>";
				return returned;
			}
		}

		return "<html>No description available for <b>" + getLocalizedName() + "</b>" + "<br>"
				+ "Please submit documentation in documentation resource center" + "<br>" + "</html>";
	}

	/**
	 * Hook to initialize module<br>
	 * Default implementation does nothing
	 */
	public void initialize() {

	}

	/**
	 * Load the module if it is not already laoded
	 * 
	 * @return
	 * @throws Exception
	 */
	public M load() throws Exception {
		boolean createProgress = !ProgressWindow.hasInstance();
		if (createProgress) {
			ProgressWindow.showProgressWindow(FlexoLocalization.localizedForKey("loading_module") + " " + getLocalizedName(), 8);
		}
		ProgressWindow.setProgressInstance(FlexoLocalization.localizedForKey("loading_module") + " " + getLocalizedName());
		loadedModuleInstance = getConstructor().newInstance(new Object[] { (ApplicationContext) getModuleLoader().getServiceManager() });
		doInternalLoadModule();
		if (createProgress) {
			ProgressWindow.hideProgressWindow();
		}
		return loadedModuleInstance;
	}

	/**
	 * Unload the module (not implemented yet)
	 */
	public void unload() {
		// TODO
	}

	public boolean isLoaded() {
		return loadedModuleInstance != null;
	}

	public M getLoadedModuleInstance() {
		return loadedModuleInstance;
	}

	private FlexoModule doInternalLoadModule() throws Exception {
		ModuleLoaderCallable loader = new ModuleLoaderCallable(loadedModuleInstance);
		return FlexoSwingUtils.syncRunInEDT(loader);
	}

	private class ModuleLoaderCallable implements Callable<FlexoModule> {

		private final FlexoModule module;

		public ModuleLoaderCallable(FlexoModule module) {
			this.module = module;
		}

		@Override
		public FlexoModule call() throws Exception {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Loading module " + module.getName());
			}
			module.initModule();
			FCH.ensureHelpEntryForModuleHaveBeenCreated(module);
			return module;
		}
	}

}