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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.generator.GeneratedResourceFileFactory;
import org.openflexo.generator.Generator;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.sg.file.SGFile;
import org.openflexo.sg.file.SGJavaFile;
import org.openflexo.sg.file.SGJavaFileResource;
import org.openflexo.sg.file.SGTextFile;
import org.openflexo.sg.file.SGTextFileResource;
import org.openflexo.sg.generationdef.FileEntry;
import org.openflexo.sg.generationdef.GenerationDefinition;
import org.openflexo.sg.generationdef.SymbolicPathEntry;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.ToolBox;

/**
 * @author sylvain
 * 
 */
public class ModuleGenerator extends MetaGenerator<FlexoModelObject, SourceRepository> {
	private static final Logger logger = Logger.getLogger(ModuleGenerator.class.getPackage().getName());

	private final TechnologyModuleImplementation module;
	/**
	 * This map allows data transfer between modules. <br>
	 * Key is a custom String allowing to classify the content. <br>
	 * Value is a sorted map with the 'from generator' and an object representing the data list. <br>
	 * This object can be either a List<Object>, a Map<Object, Object> or a Set<Object> depending on method used to perform the data
	 * insertion. <br>
	 * The 'from generator' is stored to allow clean of the transfered value from this generator only (as not all files are regenerated at
	 * each time, we must keep previous not regenerated content).
	 */
	private final Map<String, LinkedHashMap<Generator<?, ?>, Object>> crossModuleDataMap = new HashMap<String, LinkedHashMap<Generator<?, ?>, Object>>();

	private final Hashtable<CGRepositoryFileResource, SGGenerator> generators;

	public ModuleGenerator(ProjectGenerator projectGenerator, TechnologyModuleImplementation module) {
		super(projectGenerator, projectGenerator.getProject());
		this.module = module;
		generators = new Hashtable<CGRepositoryFileResource, SGGenerator>();
	}

	public TechnologyModuleImplementation getTechnologyModule() {
		return module;
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	protected VelocityContext defaultContext() {
		VelocityContext vc = super.defaultContext();
		vc.put("implementationModel", getRepository().getImplementationModel());

		// Add the technology module and all its required modules in context
		for (TechnologyModuleDefinition moduleDefinition : getTechnologyModule().getTechnologyModuleDefinition().getAllRequiredModules()) {
			TechnologyModuleImplementation implementation = getRepository().getImplementationModel().getTechnologyModule(moduleDefinition);
			if (implementation != null) {
				vc.put(ModuleGenerator.getTechnologyImplementationVelocityName(implementation), implementation);
			}
		}
		// Add the compatible technology modules in context
		for (TechnologyModuleDefinition moduleDefinition : getTechnologyModule().getTechnologyModuleDefinition().getCompatibleModules()) {
			TechnologyModuleImplementation implementation = getRepository().getImplementationModel().getTechnologyModule(moduleDefinition);
			if (implementation != null) {
				vc.put(ModuleGenerator.getTechnologyImplementationVelocityName(implementation), implementation);
			}
		}

		return vc;
	}

	public static String getTechnologyImplementationVelocityName(TechnologyModuleImplementation implementation) {
		return ToolBox.getJavaName(implementation.getTechnologyModuleDefinition().getName() + "Implementation");
	}

	public String getTemplatesFolder() {
		String resourcePath = getTechnologyModule().getTechnologyModuleDefinition().getResourcePath();
		if (resourcePath.startsWith("/")) {
			resourcePath = resourcePath.substring(1);
		}
		return getTechnologyModule().getTechnologyModuleDefinition().getTechnologyLayer().getFolderName() + "/" + resourcePath + "/";
	}

	public String getTemplateName() {
		return getTemplatesFolder() + "main.xml.vm";
	}

	public SourceRepository getSourceRepository() {
		return getProjectGenerator().getRepository();
	}

	/**
	 * Retrieve all data inserted by other modules for the specified classifier. <br>
	 * The returned Object can be either a List<Object>, a Map<Object, Object> or a Set<Object> depending on method used to perform the data
	 * insertion.
	 * 
	 * @param classifier
	 *            : the data classifier.
	 * @return all data inserted by other modules for the specified classifier.
	 */
	@SuppressWarnings({ "unchecked" })
	public Object getModuleData(String classifier) {
		Object result = null;

		if (crossModuleDataMap.get(classifier) == null) {
			return result;
		}

		List<Object> values = new ArrayList<Object>(crossModuleDataMap.get(classifier).values());
		Collections.reverse(values);
		// Reverse order to get the highest layer generator first

		for (Object object : values) {
			if (object instanceof List) {
				if (result == null) {
					result = new ArrayList<Object>();
				}
				((List<Object>) result).addAll((List<Object>) object);
			} else if (object instanceof Set) {
				if (result == null) {
					result = new LinkedHashSet<Object>();
				}
				((HashSet<Object>) result).addAll((HashSet<Object>) object);
			} else if (object instanceof Map) {
				if (result == null) {
					result = new LinkedHashMap<Object, Object>();
				}
				((Map<Object, Object>) result).putAll((Map<Object, Object>) object);
			}
		}

		return result;
	}

	/**
	 * Add a data object in this module cross data for the specified classifier. <br>
	 * The data container used for the specified classifier will be a List<Object>. <br>
	 * If this module already contains data for this classifier, it MUST have been inserted using the same method to ensure List<Object> is
	 * used everywhere. <br>
	 * If this module already contains data for this classifier which is not a List<Object>, a RuntimeException is thrown.
	 * 
	 * @param fromGenerator
	 *            the generator which adds this data
	 * @param classifier
	 *            the data classifier, used to allow different type of data for the same module.
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void addInModuleDataList(Generator<?, ?> fromGenerator, String classifier, Object data) {

		LinkedHashMap<Generator<?, ?>, Object> classifiedMap = crossModuleDataMap.get(classifier);

		if (classifiedMap == null) {
			classifiedMap = new LinkedHashMap<Generator<?, ?>, Object>();
			crossModuleDataMap.put(classifier, classifiedMap);
		}

		// Checks all data are in List<Object> for each generator
		for (Entry<Generator<?, ?>, Object> entry : classifiedMap.entrySet()) {
			if (entry.getValue() != null && !(entry.getValue() instanceof List)) {
				throw new RuntimeException("Cannot add cross module data in module '" + this.getTechnologyModule().getName()
						+ "' for classifier '" + classifier + "' because it already contains a non List value ("
						+ entry.getValue().getClass() + ") added by generator '" + entry.getKey() + "'");
			}
		}

		List<Object> dataList = (List<Object>) classifiedMap.get(fromGenerator);

		if (dataList == null) {
			dataList = new ArrayList<Object>();
			classifiedMap.put(fromGenerator, dataList);
		}

		dataList.add(data);
	}

	/**
	 * Add a data object in this module cross data for the specified classifier. <br>
	 * The data container used for the specified classifier will be a Set<Object>. <br>
	 * If this module already contains data for this classifier, it MUST have been inserted using the same method to ensure Set<Object> is
	 * used everywhere. <br>
	 * If this module already contains data for this classifier which is not a Set<Object>, a RuntimeException is thrown.
	 * 
	 * @param fromGenerator
	 *            the generator which adds this data
	 * @param classifier
	 *            the data classifier, used to allow different type of data for the same module.
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void addInModuleDataSet(Generator<?, ?> fromGenerator, String classifier, Object data) {

		LinkedHashMap<Generator<?, ?>, Object> classifiedMap = crossModuleDataMap.get(classifier);

		if (classifiedMap == null) {
			classifiedMap = new LinkedHashMap<Generator<?, ?>, Object>();
			crossModuleDataMap.put(classifier, classifiedMap);
		}

		// Checks all data are in List<Object> for each generator
		for (Entry<Generator<?, ?>, Object> entry : classifiedMap.entrySet()) {
			if (entry.getValue() != null && !(entry.getValue() instanceof Set)) {
				throw new RuntimeException("Cannot add cross module data in module '" + this.getTechnologyModule().getName()
						+ "' for classifier '" + classifier + "' because it already contains a non Set value ("
						+ entry.getValue().getClass() + ") added by generator '" + entry.getKey() + "'");
			}
		}

		Set<Object> dataSet = (Set<Object>) classifiedMap.get(fromGenerator);

		if (dataSet == null) {
			dataSet = new LinkedHashSet<Object>();
			classifiedMap.put(fromGenerator, dataSet);
		}

		dataSet.add(data);
	}

	/**
	 * Add a data object in this module cross data for the specified classifier. <br>
	 * The data container used for the specified classifier will be a Map<Object, Object>. <br>
	 * If this module already contains data for this classifier, it MUST have been inserted using the same method to ensure Map<Object,
	 * Object> is used everywhere. <br>
	 * If this module already contains data for this classifier which is not a Map<Object, Object>, a RuntimeException is thrown.
	 * 
	 * @param fromGenerator
	 *            the generator which adds this data
	 * @param classifier
	 *            the data classifier, used to allow different type of data for the same module.
	 * @param key
	 *            the key to use to insert the data into the map
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public void addInModuleDataMap(Generator<?, ?> fromGenerator, String classifier, Object key, Object data) {

		LinkedHashMap<Generator<?, ?>, Object> classifiedMap = crossModuleDataMap.get(classifier);

		if (classifiedMap == null) {
			classifiedMap = new LinkedHashMap<Generator<?, ?>, Object>();
			crossModuleDataMap.put(classifier, classifiedMap);
		}

		// Checks all data are in List<Object> for each generator
		for (Entry<Generator<?, ?>, Object> entry : classifiedMap.entrySet()) {
			if (entry.getValue() != null && !(entry.getValue() instanceof Map)) {
				throw new RuntimeException("Cannot add cross module data in module '" + this.getTechnologyModule().getName()
						+ "' for classifier '" + classifier + "' because it already contains a non Map value ("
						+ entry.getValue().getClass() + ") added by generator '" + entry.getKey() + "'");
			}
		}

		Map<Object, Object> dataMap = (Map<Object, Object>) classifiedMap.get(fromGenerator);

		if (dataMap == null) {
			dataMap = new LinkedHashMap<Object, Object>();
			classifiedMap.put(fromGenerator, dataMap);
		}

		dataMap.put(key, data);
	}

	protected void cleanCrossModuleDataForGenerator(Generator<?, ?> generator) {
		for (LinkedHashMap<Generator<?, ?>, Object> map : crossModuleDataMap.values()) {
			if (map.containsKey(generator)) {
				map.put(generator, null); // Don't remove it from map to avoid losing the order
			}
		}
	}

	@Override
	public void buildResourcesAndSetGenerators(SourceRepository repository, Vector<CGRepositoryFileResource> resources) {
		try {
			templateWithName(getTemplateName());
		} catch (TemplateNotFoundException e1) {
			logger.warning("Could not generate TechnologyModule " + getTechnologyModule().getTechnologyModuleDefinition().getName()
					+ " no main.xml.vm found");
			return;
		}

		String generationResult;
		try {

			// Clean cross module data
			for (ModuleGenerator moduleGenerator : getProjectGenerator().getAllModuleGenerators()) {
				moduleGenerator.cleanCrossModuleDataForGenerator(this);
			}

			generationResult = merge(getTemplateName(), defaultContext());

			if (logger.isLoggable(Level.INFO)) {
				logger.info("generationResult=" + generationResult);
			}

			GenerationDefinition generation = GenerationDefinition.retrieveGenerationDefinition(generationResult);

			for (SymbolicPathEntry symbolicPath : generation.symbolicPaths) {
				// Ensure declaration or creation of used symbolic dir
				if (getSourceRepository().getSymbolicDirectoryNamed(symbolicPath.name, false) == null) {
					FlexoProjectFile resourcesSymbDir = new FlexoProjectFile(getProject(), getSourceRepository().getSourceCodeRepository(),
							symbolicPath.path);
					getSourceRepository().setSymbolicDirectoryForKey(
							new CGSymbolicDirectory(getSourceRepository(), symbolicPath.name, resourcesSymbDir), symbolicPath.name);
					resourcesSymbDir.getFile().mkdirs();
				}
			}

			for (FileEntry file : generation.files) {
				logger.info("> File: " + file.name);
				String identifier = SGFile.makeIdentifier(file.name, file.symbolicPath, file.relativePath);
				CGSymbolicDirectory symbDir = getSourceRepository().getSymbolicDirectoryNamed(file.symbolicPath, true);
				if (file.getFormat().equals(FileFormat.JAVA)) {
					SGJavaFileResource javaResource = (SGJavaFileResource) resourceForKeyWithCGFile(ResourceType.JAVA_FILE,
							GeneratorUtils.nameForRepositoryAndIdentifier(repository, identifier));
					if (javaResource == null) {
						SGJavaClassGenerator generator = new SGJavaClassGenerator(this, file);
						javaResource = new SGJavaFileResource(getProject());
						javaResource.setGenerator(generator);
						javaResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, identifier));
						SGJavaFile sgJavaFile = new SGJavaFile(repository, javaResource);
						initSGFile(sgJavaFile, symbDir, javaResource);
						registerResource(javaResource, file.name, file.relativePath);
						logger.info("New java resource " + javaResource.getFile().getAbsolutePath());
					} else {
						logger.info("Retrieved java resource " + javaResource.getFile().getAbsolutePath());
						if (javaResource.getGenerator() == null) {
							SGJavaClassGenerator generator = new SGJavaClassGenerator(this, file);
							javaResource.setGenerator(generator);
						}
					}
					resources.add(javaResource);
					generators.put(javaResource, javaResource.getGenerator());
				} else {
					SGTextFileResource textResource = (SGTextFileResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE,
							GeneratorUtils.nameForRepositoryAndIdentifier(repository, identifier));
					if (textResource == null) {
						SGTextFileGenerator generator = new SGTextFileGenerator(this, file);
						textResource = new SGTextFileResource(getProject());
						textResource.setGenerator(generator);
						textResource.setName(GeneratorUtils.nameForRepositoryAndIdentifier(repository, identifier));
						SGTextFile sgTextFile = new SGTextFile(repository, textResource);
						initSGFile(sgTextFile, symbDir, textResource);
						registerResource(textResource, file.name, file.relativePath);
						logger.info("New text resource " + textResource.getFile().getAbsolutePath());
					} else {
						logger.info("Retrieved text resource " + textResource.getFile().getAbsolutePath());
						if (textResource.getGenerator() == null) {
							SGTextFileGenerator generator = new SGTextFileGenerator(this, file);
							textResource.setGenerator(generator);
						}
					}
					resources.add(textResource);
					generators.put(textResource, textResource.getGenerator());
				}
			}

		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*
		 * Hashtable<ComponentDefinition, ComponentGenerator> hash = new Hashtable<ComponentDefinition, ComponentGenerator>(); Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator> links =
		 * new Hashtable<PopupComponentDefinition, PopupLinkComponentGenerator>(); for(TabComponentDefinition tcd:getProject().getFlexoComponentLibrary().getTabComponentList()) { ComponentGenerator
		 * generator = getGenerator(tcd); if (generator != null) { hash.put(tcd,generator); generator.buildResourcesAndSetGenerators(repository,resources); } else { if
		 * (logger.isLoggable(Level.WARNING)) logger.warning("Could not instanciate ComponentGenerator for "+tcd); } } for(PopupComponentDefinition
		 * pcd:getProject().getFlexoComponentLibrary().getPopupsComponentList()) { if (pcd.isHelper()) continue; PopupLinkComponentGenerator linkGenerator = getPopupLinkGenerator(pcd); if
		 * (linkGenerator != null) { links.put(pcd,linkGenerator); linkGenerator.buildResourcesAndSetGenerators(repository,resources); } else { if (logger.isLoggable(Level.WARNING))
		 * logger.warning("Could not instanciate ComponentGenerator for "+pcd); } ComponentGenerator generator = getGenerator(pcd); if (generator != null) { hash.put(pcd,generator);
		 * generator.buildResourcesAndSetGenerators(repository,resources); } else { if (logger.isLoggable(Level.WARNING)) logger.warning("Could not instanciate ComponentGenerator for "+pcd); } }
		 * for(OperationComponentDefinition ocd:getProject().getFlexoComponentLibrary().getOperationsComponentList()) { ComponentGenerator generator = getGenerator(ocd); if (generator != null) {
		 * hash.put(ocd,generator); generator.buildResourcesAndSetGenerators(repository,resources); } else { if (logger.isLoggable(Level.WARNING))
		 * logger.warning("Could not instanciate ComponentGenerator for "+ocd); } }
		 * 
		 * if (getTarget() == CodeType.PROTOTYPE) { //Add page to manage samples StaticComponentGenerator generator = getStaticComponentGenerator("PrototypeSamplesAdminPage",
		 * "PrototypeSamplesAdminPage"); if (generator != null) generator.buildResourcesAndSetGenerators(repository,resources); else { if (logger.isLoggable(Level.WARNING))
		 * logger.warning("Could not instanciate StaticComponentGenerator for PrototypeSamplesAdminPage"); } }
		 * 
		 * generators.clear(); popupLinkGenerators.clear(); generators = hash; popupLinkGenerators = links;
		 */
	}

	private static void initSGFile(SGFile cgFile, CGSymbolicDirectory symbDir, CGRepositoryFileResource returned) {
		GeneratedResourceFileFactory.initCGFile(cgFile, symbDir, returned);
	}

	private static <FR extends CGRepositoryFileResource> FR registerResource(FR returned, String fileName, String folderPath) {
		return GeneratedResourceFileFactory.registerResource(returned, fileName, folderPath);
	}

	/**
	 * @see SGGenerator#getVelocityMacroTemplates(Generator, ModuleGenerator)
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		return SGGenerator.getVelocityMacroTemplates(this, this);
	}
}
