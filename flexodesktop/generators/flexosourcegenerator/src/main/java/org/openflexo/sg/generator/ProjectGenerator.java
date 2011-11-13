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
package org.openflexo.sg.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildListener;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.SGTemplates;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.sg.implmodel.enums.TechnologyLayer;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.Generator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.PermissionDeniedException;
import org.openflexo.generator.exception.TemplateNotFoundException;

/**
 * 
 * @author sguerin
 */
public class ProjectGenerator extends AbstractProjectGenerator<SourceRepository> {

	protected static final Logger logger = Logger.getLogger(ProjectGenerator.class.getPackage().getName());

	private static final String SG_MACRO_LIBRARY_NAME = "VM_global_library.vm";

	private boolean hasBeenInitialized = false;

	private Vector<BuildListener> buildListeners;

	private Map<TechnologyModuleDefinition, ModuleGenerator> generators = new HashMap<TechnologyModuleDefinition, ModuleGenerator>();

	// =============================================================
	// ======================== Constructor ========================
	// =============================================================

	/**
	 * Default constructor
	 * 
	 * @param workflowFile
	 * @throws Exception
	 */
	public ProjectGenerator(FlexoProject project, SourceRepository repository) throws GenerationException {
		super(project, repository);
		if (repository == null) {
			if (logger.isLoggable(Level.FINE))
				logger.fine("No target repository, this may happen during dynamic invocation of code generator within the context of the Data model edition");
		}

		if (getRootOutputDirectory() != null) {
			if (!getResourceOutputDirectory().exists())
				getRootOutputDirectory().mkdirs();
			if (!getRootOutputDirectory().canWrite())
				throw new PermissionDeniedException(getRootOutputDirectory(), this);
		}

		buildListeners = new Vector<BuildListener>();
	}

	public SourceRepository getSourceRepository() {
		return getRepository();
	}

	public ImplementationModel getImplementationModel() {
		return getRepository().getImplementationModel();
	}

	// private GenerationModule retrieveModule()

	@Override
	public SGTemplates getDefaultTemplates() {
		return getProject().getGeneratedSources().getTemplates();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	/**
	 * This method is very important, because it is the way we must identify or build all resources involved in code generation. After this
	 * list has been built, we just let ResourceManager do the work.
	 * 
	 * @param repository
	 *            : repository where resources should be retrieved or built
	 * @param resources
	 *            : the list of resources we must retrieve or build
	 */
	@Override
	public void buildResourcesAndSetGenerators(SourceRepository repository, Vector<CGRepositoryFileResource> resources) {
		hasBeenInitialized = true;

		logger.info("We go for first pass on Project Generator");

		Map<TechnologyModuleDefinition, ModuleGenerator> newGenerators = new LinkedHashMap<TechnologyModuleDefinition, ModuleGenerator>();
		for (TechnologyModuleImplementation technologyModuleImplementation : repository.getImplementationModel().getTechnologyModules()) {
			ModuleGenerator moduleGenerator = generators.get(technologyModuleImplementation.getTechnologyModuleDefinition());
			if (moduleGenerator == null)
				moduleGenerator = new ModuleGenerator(this, technologyModuleImplementation);
			newGenerators.put(technologyModuleImplementation.getTechnologyModuleDefinition(), moduleGenerator);
		}

		generators = newGenerators;

		for (ModuleGenerator generator : newGenerators.values()) {
			refreshProgressWindow("Generate " + generator.getTechnologyModule().getTechnologyModuleDefinition().getName(), false);
			generator.buildResourcesAndSetGenerators(repository, resources);
		}

	}

	/**
	 * Calculate the appropriate order for module generation (as module can communicate). <br>
	 * The order is as follow (Inverse of declaration in TechnologyLayer):
	 * <ul>
	 * <li>Database layer modules</li>
	 * <li>DAO layer modules</li>
	 * <li>Business logic layer modules</li>
	 * <li>GUI layer modules</li>
	 * <li>Transversal layer modules</li>
	 * <li>Main layer modules</li>
	 * </ul>
	 * In the same layer, modules which are compatible with or require another module will be before this other module (except loop exists,
	 * in such case order is unpredictable)
	 * 
	 * @param filesToGenerate
	 *            the list of file which will be regenerated
	 */
	@Override
	public void sortResourcesForGeneration(
			List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> resources) {

		List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> notSGGeneratorResources = new ArrayList<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>();
		Map<TechnologyLayer, Map<TechnologyModuleDefinition, List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>>> map = new HashMap<TechnologyLayer, Map<TechnologyModuleDefinition, List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>>>();

		for (CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> resource : resources) {
			if (resource.getGenerator() instanceof SGGenerator<?, ?>) {
				TechnologyModuleDefinition moduleDefinition = ((SGGenerator<?, ?>) resource.getGenerator()).getModuleGenerator()
						.getTechnologyModule().getTechnologyModuleDefinition();
				Map<TechnologyModuleDefinition, List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>> mapForLayer = map
						.get(moduleDefinition.getTechnologyLayer());
				if (mapForLayer == null) {
					mapForLayer = new HashMap<TechnologyModuleDefinition, List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>>();
					map.put(moduleDefinition.getTechnologyLayer(), mapForLayer);
				}

				List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> resourcesForModule = mapForLayer
						.get(moduleDefinition);
				if (resourcesForModule == null) {
					resourcesForModule = new ArrayList<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>();
					mapForLayer.put(moduleDefinition, resourcesForModule);
				}
				resourcesForModule.add(resource);
			} else
				notSGGeneratorResources.add(resource);
		}

		resources.clear();

		List<TechnologyLayer> allLayers = Arrays.asList(TechnologyLayer.values());
		Collections.reverse(allLayers);

		for (TechnologyLayer layer : allLayers) {
			Map<TechnologyModuleDefinition, List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>>> mapForLayer = map
					.get(layer);

			if (mapForLayer == null)
				continue;

			/*
			 * The idea is to build a map containing, for each module, all the modules which are compatible or which request it. Once this map is built, we iterates over each key to takes empty
			 * modules and we remove those modules from the list of other modules. We do this again until no module is taken or until the map is empty.
			 */

			// 1. Build the map with empty lists
			Map<TechnologyModuleDefinition, Set<TechnologyModuleDefinition>> requiringModuleMap = new HashMap<TechnologyModuleDefinition, Set<TechnologyModuleDefinition>>();
			for (TechnologyModuleDefinition moduleDefinition : mapForLayer.keySet())
				requiringModuleMap.put(moduleDefinition, new HashSet<TechnologyModuleDefinition>());

			// 2. Fill the map
			for (TechnologyModuleDefinition definition : requiringModuleMap.keySet()) {
				Set<TechnologyModuleDefinition> requiredModules = definition.getRequiredModules();
				requiredModules.addAll(definition.getCompatibleModules());
				for (TechnologyModuleDefinition requiredModule : requiredModules) {
					if (requiringModuleMap.containsKey(requiredModule))
						requiringModuleMap.get(requiredModule).add(definition);
				}
			}

			// 3. Perform iteration
			boolean hasRemovedKey;
			do {
				hasRemovedKey = false;
				for (Map.Entry<TechnologyModuleDefinition, Set<TechnologyModuleDefinition>> entry : new HashMap<TechnologyModuleDefinition, Set<TechnologyModuleDefinition>>(
						requiringModuleMap).entrySet()) {
					if (entry.getValue().isEmpty()) {
						requiringModuleMap.remove(entry.getKey());
						for (Set<TechnologyModuleDefinition> set : requiringModuleMap.values())
							set.remove(entry.getKey());
						resources.addAll(mapForLayer.get(entry.getKey()));
						hasRemovedKey = true;
					}
				}
			} while (!requiringModuleMap.isEmpty() && hasRemovedKey);

			for (TechnologyModuleDefinition notRemovedModule : requiringModuleMap.keySet())
				resources.addAll(mapForLayer.get(notRemovedModule));
		}

		resources.addAll(notSGGeneratorResources);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasBeenInitialized() {
		return hasBeenInitialized;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public File getRootOutputDirectory() {
		if (getRepository() == null)
			return null;
		return getRepository().getDirectory();
	}

	public File getResourceOutputDirectory() {
		return getRootOutputDirectory();
	}

	public void addBuildListener(BuildListener listener) {
		buildListeners.add(listener);
	}

	public void removeBuildListener(BuildListener listener) {
		buildListeners.remove(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		List<CGTemplate> result = new ArrayList<CGTemplate>();
		try {
			result.add(templateWithName(SG_MACRO_LIBRARY_NAME));
		} catch (TemplateNotFoundException e) {
			logger.warning("Should include velocity macro template for project generator but template is not found '"
					+ SG_MACRO_LIBRARY_NAME + "'");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TargetType getTarget() {
		// No more used with new generator
		return null;
	}

	public String generateTimestamp() {
		return (new Date()).toString();
	}

	/**
	 * Retrieve the module generator associated to the specified TechnologyModuleDefinition.
	 * 
	 * @param technologyModuleDefinition
	 * @return the retrieved module generator if any, null otherwise.
	 */
	public ModuleGenerator getModuleGenerator(TechnologyModuleDefinition technologyModuleDefinition) {
		return generators.get(technologyModuleDefinition);
	}

	/**
	 * Return all ModuleGenerators recorded in this project generator.
	 * 
	 * @return all ModuleGenerators recorded in this project generator.
	 */
	public Collection<ModuleGenerator> getAllModuleGenerators() {
		return generators.values();
	}

	/**
	 * Add a cross module data in the specified targeted module. <br>
	 * Intended to be used in macro defined in the targeted module itself. <br>
	 * For those macro to be available in the from module, the targeted module must be in the "requireModule" or in the
	 * "compatibleWithModule".
	 * 
	 * @see ModuleGenerator#addInModuleDataList(Generator, String, Object)
	 */
	public void addCrossModuleDataInList(TechnologyModuleDefinition targetedModule, Generator<?, ?> fromGenerator, String classifier,
			Object data) {
		ModuleGenerator moduleGenerator = generators.get(targetedModule);

		if (moduleGenerator == null) {
			logger.info("Attempting to add cross module data in a non existent module '" + targetedModule.getName() + "'. Ignoring.");
			return;
		}

		moduleGenerator.addInModuleDataList(fromGenerator, classifier, data);
	}

	/**
	 * Add a cross module data in the specified targeted module. <br>
	 * Intended to be used in macro defined in the targeted module itself. <br>
	 * For those macro to be available in the from module, the targeted module must be in the "requireModule" or in the
	 * "compatibleWithModule".
	 * 
	 * @see ModuleGenerator#addInModuleDataSet(Generator, String, Object)
	 */
	public void addCrossModuleDataInSet(TechnologyModuleDefinition targetedModule, Generator<?, ?> fromGenerator, String classifier,
			Object data) {
		ModuleGenerator moduleGenerator = generators.get(targetedModule);

		if (moduleGenerator == null) {
			logger.info("Attempting to add cross module data in a non existent module '" + targetedModule.getName() + "'. Ignoring.");
			return;
		}

		moduleGenerator.addInModuleDataSet(fromGenerator, classifier, data);
	}

	/**
	 * Add a cross module data in the specified targeted module. <br>
	 * Intended to be used in macro defined in the targeted module itself. <br>
	 * For those macro to be available in the from module, the targeted module must be in the "requireModule" or in the
	 * "compatibleWithModule".
	 * 
	 * @see ModuleGenerator#addInModuleDataMap(Generator, String, Object, Object)
	 */
	public void addCrossModuleDataInMap(TechnologyModuleDefinition targetedModule, Generator<?, ?> fromGenerator, String classifier,
			Object key, Object data) {
		ModuleGenerator moduleGenerator = generators.get(targetedModule);

		if (moduleGenerator == null) {
			logger.info("Attempting to add cross module data in a non existent module '" + targetedModule.getName() + "'. Ignoring.");
			return;
		}

		moduleGenerator.addInModuleDataMap(fromGenerator, classifier, key, data);
	}
}