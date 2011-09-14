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
package org.openflexo.generator.utils;

import java.util.Vector;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.dkv.Language;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.LocalizationFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;


public class LocalizedFileGenerator extends MetaFileGenerator {

	private static final Logger logger = FlexoLogger.getLogger(LocalizedFileGenerator.class.getPackage().getName());

	private final Language _langage;

	/**
	 * @param projectGenerator
	 * @param object
	 */
	public LocalizedFileGenerator(ProjectGenerator projectGenerator, Language language) {
		super(projectGenerator, FileFormat.TEXT, ResourceType.TEXT_FILE, language.getName(), "LOCALIZATION." + language.getFullyQualifiedName());
		_langage = language;
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.generator.CGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.CGRepository, Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// PList file
		textResource = (LocalizationFileResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE, GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (textResource == null) {
			textResource = GeneratedFileResourceFactory.createNewLocalizedFileResource(repository, this);
			textResource.setGenerator(this);
			logger.info("Created LOCALIZATION resource " + textResource.getName());
		} else {
			textResource.setGenerator(this);
			logger.info("Successfully retrieved LOCALIZATION FILE resource " + textResource.getName());
		}
		((LocalizationFileResource) textResource).registerObserverWhenRequired();
		resources.add(textResource);
	}

	/**
	 * @return
	 */
	public Language getLanguage() {
		return _langage;
	}

	/**
	 * Overrides generate
	 * 
	 * @see org.openflexo.generator.CGGenerator#generate(boolean)
	 */
	@Override
	public void generate(boolean forceRegenerate) {
		startGeneration();
		VelocityContext vc = defaultContext();
		try {
			String mergeResult = merge("LocalizedFile.vm", vc);
			generatedCode = new GeneratedTextResource(getLanguage().getName(), mergeResult);
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	/**
	 * Overrides getGeneratorLogger
	 * 
	 * @see org.openflexo.generator.CGGenerator#getGeneratorLogger()
	 */
	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getResourcesSymbolicDirectory();
	}

}
