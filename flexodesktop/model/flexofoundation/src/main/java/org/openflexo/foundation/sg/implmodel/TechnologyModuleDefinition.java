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
package org.openflexo.foundation.sg.implmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.sg.implmodel.enums.TechnologyLayer;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleCompatibilityCheckException;
import org.openflexo.foundation.sg.implmodel.exception.TechnologyModuleInitializationException;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.JavaResourceUtil;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

/**
 * Contains the content of the module.xml at the {@link TechnologyModuleImplementation} creation
 * 
 * @author ndaniels
 */
public abstract class TechnologyModuleDefinition {

	private static final Logger logger = Logger.getLogger(TechnologyModuleDefinition.class.getPackage().getName());

	public static File MODULES_DIR = new FileResource("Generator/TechnologyModules");
	private static XMLMapping MODULE_MODEL;

	private static Map<String, TechnologyModuleDefinition> technologyModuleDefinitionMap = null;

	private TechnologyLayer technologyLayer;
	private String name;
	private String version;
	private String description;
	private LinkedHashSet<String> requiredModuleNames;
	private LinkedHashSet<String> compatibleModuleNames;
	private LinkedHashSet<String> incompatibleModuleNames;

	public TechnologyModuleDefinition() throws TechnologyModuleInitializationException {
		loadModule();
	}

	/**
	 * Return the path to the resources needed by this module. <br>
	 * Can be a path to a java resource folder (ie. in a jar) or a path relative to MODULES_DIR in Flexo files. <br>
	 * The returned path must NOT ends with a '/'. <br>
	 * The denoted folder must contains a module.xml file. <br>
	 * By default, this method will return the first folder containing a module.xml file. <br>
	 * Pay attention that this method will be called BEFORE the module initialization (thus before having its name/version/... set)
	 * 
	 * @return the path to the resources needed by this module.
	 * @throws TechnologyModuleInitializationException
	 *             if resource path cannot be found
	 */
	public String getResourcePath() {
		for (String resourceName : JavaResourceUtil.getMatchingResources(this.getClass(), "module.xml")) {
			if (resourceName.endsWith("/module.xml")) {
				return resourceName.substring(0, resourceName.length() - "/module.xml".length());
			}
		}

		throw new TechnologyModuleInitializationException("module.xml not found for module '" + this.getClass() + "' !");
	}

	/**
	 * Create a new instance of the appropriate {@link TechnologyModuleImplementation}.
	 * 
	 * @param implementationModel
	 * @return the created {@link TechnologyModuleImplementation}
	 * @throws TechnologyModuleCompatibilityCheckException
	 *             if there is incompatibility with existing module.
	 */
	public abstract TechnologyModuleImplementation createNewImplementation(ImplementationModel implementationModel)
			throws TechnologyModuleCompatibilityCheckException;

	/**
	 * Load and parse the module.xml file associated to this {@link TechnologyModuleDefinition}. <br>
	 * The variable of this TechnologyModuleDefinition must be fully initialized after this method call. <br>
	 * This method is called at SG module loading, it can be used to perform any initialization needed. <br>
	 * For example it will record all inspectors available in this jar. <br>
	 * Override the method to load the needed GUI elements in the module using SGModule.recordTechnologyModuleGUIFactory.
	 * 
	 * @throws TechnologyModuleInitializationException
	 *             if the module.xml file is not found, if it has parsing error or if any other exception occurred during initialization
	 */
	protected void loadModule() throws TechnologyModuleInitializationException {

		InputStream inputStream = null;
		try {
			// Try to load from java resources
			inputStream = this.getClass().getResourceAsStream(getResourcePath() + "/module.xml");

			if (inputStream == null) {
				File xmlFile = new File(new File(MODULES_DIR, getResourcePath()), "module.xml");
				if (xmlFile.exists()) {
					inputStream = new FileInputStream(xmlFile);
				}
			}

			if (inputStream == null) {
				logger.severe("Cannot find module.xml using path '" + getResourcePath() + "' !");
				throw new TechnologyModuleInitializationException("Cannot find module.xml using path '" + getResourcePath() + "' !");
			}

			TechnologyModuleDefinitionDTO returned = (TechnologyModuleDefinitionDTO) XMLDecoder.decodeObjectWithMapping(inputStream,
					getModuleModel());
			fillFromDTO(returned);

			// Load inspectors
			SGJarInspectorGroup.INSTANCE.recordAllInspectors(getClass());
			logger.info("TechnologyModule " + name + " loaded");

		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot load module '" + getClass() + "' !", e);

			if (e instanceof TechnologyModuleInitializationException) {
				throw (TechnologyModuleInitializationException) e;
			}

			throw new TechnologyModuleInitializationException(e);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void fillFromDTO(TechnologyModuleDefinitionDTO dto) {
		this.technologyLayer = dto.technologyLayer;
		this.name = dto.name;
		this.description = dto.description;
		this.version = dto.version;
		this.requiredModuleNames = new LinkedHashSet<String>();
		this.compatibleModuleNames = new LinkedHashSet<String>();
		this.incompatibleModuleNames = new LinkedHashSet<String>();

		for (TechnologyModuleDefinitionDTO.CompatibilityModule module : dto.requiredModuleList) {
			this.requiredModuleNames.add(module.name);
		}
		for (TechnologyModuleDefinitionDTO.CompatibilityModule module : dto.compatibleModuleList) {
			if (!this.requiredModuleNames.contains(module.name)) {
				this.compatibleModuleNames.add(module.name);
			}
		}
		for (TechnologyModuleDefinitionDTO.CompatibilityModule module : dto.incompatibleModuleList) {
			if (!this.requiredModuleNames.contains(module.name) && !this.compatibleModuleNames.contains(module.name)) {
				this.incompatibleModuleNames.add(module.name);
			}
		}
	}

	/**
	 * Recursively retrieve all modules required by this module, including itself. <br>
	 * Return a map containing for each recursion level the list of required module. <br>
	 * Order is preserved in each level.
	 * 
	 * @return the retrieved required modules.
	 */
	public Map<Integer, LinkedHashSet<TechnologyModuleDefinition>> getAllRequiredModulesByLevel() {

		Map<Integer, LinkedHashSet<TechnologyModuleDefinition>> requiredModules = new HashMap<Integer, LinkedHashSet<TechnologyModuleDefinition>>();
		fillRequiredModules(requiredModules, 0);

		return requiredModules;
	}

	/**
	 * Recursively retrieve all modules required by this module, including itself.
	 * 
	 * @return the retrieved required modules.
	 */
	public Set<TechnologyModuleDefinition> getAllRequiredModules() {

		Map<Integer, LinkedHashSet<TechnologyModuleDefinition>> requiredModules = getAllRequiredModulesByLevel();

		LinkedHashSet<TechnologyModuleDefinition> result = new LinkedHashSet<TechnologyModuleDefinition>();
		for (LinkedHashSet<TechnologyModuleDefinition> set : requiredModules.values()) {
			result.addAll(set);
		}

		return result;
	}

	private void fillRequiredModules(Map<Integer, LinkedHashSet<TechnologyModuleDefinition>> requiredModules, int level) {

		// Avoid infinite loop (module1 requires module2 and module2 requires module1)
		for (LinkedHashSet<TechnologyModuleDefinition> set : requiredModules.values()) {
			if (set.contains(this)) {
				return;
			}
		}

		LinkedHashSet<TechnologyModuleDefinition> set = requiredModules.get(level);
		if (set == null) {
			set = new LinkedHashSet<TechnologyModuleDefinition>();
			requiredModules.put(level, set);
		}
		set.add(this);

		for (TechnologyModuleDefinition moduleDefinition : getRequiredModules()) {
			moduleDefinition.fillRequiredModules(requiredModules, level + 1);
		}
	}

	protected static XMLMapping getModuleModel() {
		if (MODULE_MODEL == null) {
			File moduleModelFile = new FileResource("Models/TechnologyModules/ModuleModel.xml");
			try {
				MODULE_MODEL = new XMLMapping(moduleModelFile);
			} catch (Exception e) {
				// Warns about the exception
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Exception raised: " + e.getClass().getName() + ". See console for details.");
				}
				e.printStackTrace();
			}
		}
		return MODULE_MODEL;
	}

	/**
	 * Retrieve all {@link TechnologyModuleDefinition} available from classpath. <br>
	 * Map contains the TechnologyModuleDefinition name as key and the TechnologyModuleDefinition itself as value.
	 * 
	 * @return the retrieved TechnologyModuleDefinition map.
	 */
	private static Map<String, TechnologyModuleDefinition> getTechnologyModuleDefinitionMap() {
		if (technologyModuleDefinitionMap == null) {
			technologyModuleDefinitionMap = new Hashtable<String, TechnologyModuleDefinition>();

			ServiceLoader<TechnologyModuleDefinition> loader = ServiceLoader.load(TechnologyModuleDefinition.class);
			Iterator<TechnologyModuleDefinition> iterator = loader.iterator();
			while (iterator.hasNext()) {
				TechnologyModuleDefinition technologyModuleDefinition = iterator.next();

				if (technologyModuleDefinitionMap.containsKey(technologyModuleDefinition.getName())) {
					logger.severe("Cannot include TechnologyModuleDefinition with name '" + technologyModuleDefinition.getName()
							+ "' because it already exists !!!! A Technology module name MUST be unique !");
				} else {
					technologyModuleDefinitionMap.put(technologyModuleDefinition.getName(), technologyModuleDefinition);
				}
			}
		}

		return technologyModuleDefinitionMap;
	}

	/**
	 * Return all available TechnologyModuleDefinition.
	 * 
	 * @return all available TechnologyModuleDefinition.
	 */
	public static Collection<TechnologyModuleDefinition> getAllTechnologyModuleDefinitions() {
		return getTechnologyModuleDefinitionMap().values();
	}

	/**
	 * Retrieve and return the TechnologyModuleDefinition with the specified name, null if it doesn't exist.
	 * 
	 * @param technologyModuleName
	 * @return the TechnologyModuleDefinition with the specified name.
	 */
	public static TechnologyModuleDefinition getTechnologyModuleDefinition(String technologyModuleName) {
		return getTechnologyModuleDefinitionMap().get(technologyModuleName);
	}

	/* ===================== */
	/* == Getter / Setter == */
	/* ===================== */

	public TechnologyLayer getTechnologyLayer() {
		return technologyLayer;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public Set<String> getRequiredModuleNames() {
		return requiredModuleNames;
	}

	public Set<String> getCompatibleModuleNames() {
		return compatibleModuleNames;
	}

	public Set<String> getIncompatibleModuleNames() {
		return incompatibleModuleNames;
	}

	public LinkedHashSet<TechnologyModuleDefinition> getRequiredModules() {
		LinkedHashSet<TechnologyModuleDefinition> result = new LinkedHashSet<TechnologyModuleDefinition>();
		for (String name : requiredModuleNames) {
			TechnologyModuleDefinition technologyModuleDefinition = getTechnologyModuleDefinition(name);
			if (technologyModuleDefinition != null) {
				result.add(technologyModuleDefinition);
			}
		}

		return result;
	}

	public LinkedHashSet<TechnologyModuleDefinition> getCompatibleModules() {
		LinkedHashSet<TechnologyModuleDefinition> result = new LinkedHashSet<TechnologyModuleDefinition>();
		for (String name : compatibleModuleNames) {
			TechnologyModuleDefinition technologyModuleDefinition = getTechnologyModuleDefinition(name);
			if (technologyModuleDefinition != null) {
				result.add(technologyModuleDefinition);
			}
		}

		return result;
	}

	public LinkedHashSet<TechnologyModuleDefinition> getIncompatibleModules() {
		LinkedHashSet<TechnologyModuleDefinition> result = new LinkedHashSet<TechnologyModuleDefinition>();
		for (String name : incompatibleModuleNames) {
			TechnologyModuleDefinition technologyModuleDefinition = getTechnologyModuleDefinition(name);
			if (technologyModuleDefinition != null) {
				result.add(technologyModuleDefinition);
			}
		}

		return result;
	}
}
