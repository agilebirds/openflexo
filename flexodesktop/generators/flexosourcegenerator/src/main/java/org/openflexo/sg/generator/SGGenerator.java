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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.DataFlexoObserver;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleDefinition;
import org.openflexo.foundation.sg.implmodel.TechnologyModuleImplementation;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.generator.Generator;
import org.openflexo.generator.exception.TemplateNotFoundException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.formatter.Formatter;
import org.openflexo.sg.formatter.FormatterFactory;
import org.openflexo.sg.formatter.exception.FormattingException;
import org.openflexo.sg.generationdef.ContextEntry;
import org.openflexo.sg.generationdef.FileEntry;
import org.openflexo.toolbox.FileFormat;


public abstract class SGGenerator<T extends FlexoModelObject, CR extends GeneratedCodeResult> extends Generator<T, SourceRepository> implements DataFlexoObserver, IFlexoResourceGenerator {

	private static final Logger logger = FlexoLogger.getLogger(SGGenerator.class.getPackage().getName());

	protected CR generatedCode;
	private FileEntry fileEntry;
	protected ModuleGenerator moduleGenerator;

	protected FormattingException formattingException;

	public SGGenerator(ModuleGenerator moduleGenerator, FileEntry fileEntry) {
		this(moduleGenerator, fileEntry, null);
	}

	public SGGenerator(ModuleGenerator moduleGenerator, FileEntry fileEntry, T object) {
		super(moduleGenerator.getProjectGenerator(), object);
		this.fileEntry = fileEntry;
		this.moduleGenerator = moduleGenerator;
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	@Override
	public CR getGeneratedCode() {
		return generatedCode;
	}

	public FileEntry getFileEntry() {
		return fileEntry;
	}

	@Override
	protected final VelocityContext defaultContext() {
		VelocityContext returned = super.defaultContext();
		returned.put("implementationModel", getRepository().getImplementationModel());

		// Add the technology module and all its required modules in context
		for (TechnologyModuleDefinition moduleDefinition : moduleGenerator.getTechnologyModule().getTechnologyModuleDefinition().getAllRequiredModules()) {
			TechnologyModuleImplementation implementation = getRepository().getImplementationModel().getTechnologyModule(moduleDefinition);
			if (implementation != null)
				returned.put(ModuleGenerator.getTechnologyImplementationVelocityName(implementation), implementation);
		}
		// Add the compatible technology modules in context
		for (TechnologyModuleDefinition moduleDefinition : moduleGenerator.getTechnologyModule().getTechnologyModuleDefinition().getCompatibleModules()) {
			TechnologyModuleImplementation implementation = getRepository().getImplementationModel().getTechnologyModule(moduleDefinition);
			if (implementation != null)
				returned.put(ModuleGenerator.getTechnologyImplementationVelocityName(implementation), implementation);
		}

		for (ContextEntry ce : getFileEntry().template.contexts) {
			FlexoModelObjectReference<?> ref = new FlexoModelObjectReference<FlexoModelObject>(getProject(), ce.value);
			FlexoModelObject object = ref.getObject(true);
			if (object != null) {
				returned.put(ce.name, object);
			}
		}
		return returned;
	}

	/**
	 * Calculate if a generation is needed. It will also clean the cross module cache if the file is marked as deleted.
	 * 
	 * @param forceRegenerate
	 * @return true if a generation must proceed, false otherwise.
	 */
	public boolean needGeneration(boolean forceRegenerate) {
		if (isResourceMarkedAsDeleted())
		{
			formattingException = null;
			cleanCrossModuleDate();
			return false;
		}

		return forceRegenerate || needsGeneration();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startGeneration() {
		super.startGeneration();
		formattingException = null;
		cleanCrossModuleDate();
	}

	/**
	 * Clean the cross module data added by previous generation of this generator.
	 */
	protected void cleanCrossModuleDate() {
		// Clean cross module data
		for (ModuleGenerator moduleGenerator : getProjectGenerator().getAllModuleGenerators())
			moduleGenerator.cleanCrossModuleDataForGenerator(this);
	}

	/**
	 * Format the specified text according to the generator file format. If no formatter is found for this format the text is unchanged.
	 * 
	 * @param text
	 * @return the formatted text.
	 */
	protected String formatGeneration(String text) {
		Formatter formatter = FormatterFactory.getFormater(getFileFormat());
		if (formatter != null) {
			try {
				return formatter.format(text);
			} catch (FormattingException e) {
				formattingException = e;
			}
		}

		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		return formattingException != null;
	}

	public ModuleGenerator getModuleGenerator() {
		return moduleGenerator;
	}

	public String getTemplateName() {
		return moduleGenerator.getTemplatesFolder() + getFileEntry().template.templateFile;
	}

	public FileFormat getFileFormat() {
		return getFileEntry().getFormat();
	}

	/**
	 * @see #getVelocityMacroTemplates(Generator, ModuleGenerator)
	 */
	@Override
	public List<CGTemplate> getVelocityMacroTemplates() {
		return getVelocityMacroTemplates(this, getModuleGenerator());
	}

	/**
	 * Takes all macro from current module, all its required modules (recursively) and all its compatible modules (NOT recursively). <br>
	 * Order is as follow: deepest required modules to most direct one, then compatible modules then current module.
	 * 
	 * @return the taken macro templates.
	 */
	public static List<CGTemplate> getVelocityMacroTemplates(Generator<?, ?> currentGenerator, ModuleGenerator moduleGenerator) {
		List<CGTemplate> macros = new ArrayList<CGTemplate>();
		
		TechnologyModuleDefinition currentModuleDefinition = moduleGenerator.getTechnologyModule().getTechnologyModuleDefinition();

		// Add required modules
		Map<Integer, LinkedHashSet<TechnologyModuleDefinition>> requiredModuleByLevel = currentModuleDefinition.getAllRequiredModulesByLevel();
		List<Integer> orderedLevels = new ArrayList<Integer>(requiredModuleByLevel.keySet());
		Collections.sort(orderedLevels);
		Collections.reverse(orderedLevels);
		for (Integer key : orderedLevels) {
			for (TechnologyModuleDefinition definition : requiredModuleByLevel.get(key)) {
				if (definition != currentModuleDefinition) // Will add current one after compatible modules
				{
					ModuleGenerator definitionModuleGenerator = moduleGenerator.getProjectGenerator().getModuleGenerator(definition);
					if (definitionModuleGenerator != null) {
						try {
							macros.add(currentGenerator.templateWithName(definitionModuleGenerator.getTemplatesFolder() + "VM_global_library.vm"));
						} catch (TemplateNotFoundException e) {
							if (logger.isLoggable(Level.FINE)) {
								logger.fine("Specific velocity macro library '" + definitionModuleGenerator.getTemplatesFolder() + "VM_global_library.vm" + "' not found in module.");
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		// Add compatible modules
		for (TechnologyModuleDefinition definition : currentModuleDefinition.getCompatibleModules()) {
			ModuleGenerator definitionModuleGenerator = moduleGenerator.getProjectGenerator().getModuleGenerator(definition);
			if (definitionModuleGenerator != null) {
				try {
					macros.add(currentGenerator.templateWithName(definitionModuleGenerator.getTemplatesFolder() + "VM_global_library.vm"));
				} catch (TemplateNotFoundException e) {
					if (logger.isLoggable(Level.FINE)) {
						logger.fine("Specific velocity macro library '" + definitionModuleGenerator.getTemplatesFolder() + "VM_global_library.vm" + "' not found in module.");
						e.printStackTrace();
					}
				}
			}
		}

		// Add current module
		try {
			macros.add(currentGenerator.templateWithName(moduleGenerator.getTemplatesFolder() + "VM_global_library.vm"));
		} catch (TemplateNotFoundException e) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Specific velocity macro library '" + moduleGenerator.getTemplatesFolder() + "VM_global_library.vm" + "' not found in module.");
				e.printStackTrace();
			}
		}

		return macros;
	}

	protected CGRepositoryFileResource<?, ?, ? extends CGFile> getGeneratedResource() {
		if (getGeneratedResources().size() == 0)
			return null;
		if (getGeneratedResources().size() > 1)
			logger.warning("getGeneratedResource in SGGenerator retrieved more than 1 resource. A SGGenerator is supposed to generate only 1 resource ! Number of retrieved resources: "
					+ getGeneratedResources().size() + ". Returning the first one");
		return getGeneratedResources().get(0);
	}

	protected boolean isResourceMarkedAsDeleted() {
		CGRepositoryFileResource<?, ?, ? extends CGFile> resource = getGeneratedResource();
		if (resource == null)
			return false;

		CGFile cgFile = resource.getCGFile();
		return cgFile != null && cgFile.isMarkedForDeletion();
	}
}
