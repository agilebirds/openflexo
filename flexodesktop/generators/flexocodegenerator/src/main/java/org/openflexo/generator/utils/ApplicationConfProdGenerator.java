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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.ApplicationConfProdResource;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

/**
 * @author gpolet
 * 
 */
public class ApplicationConfProdGenerator extends MetaFileGenerator {
	private static final String TEMPLATE_NAME = "Application.conf.PROD.vm";

	private static final Logger logger = FlexoLogger.getLogger(ApplicationConfProdGenerator.class.getPackage().getName());

	public static final String IDENTIFIER = "APPLICATION_CONF_PROD";

	/**
	 * @param aProject
	 */
	public ApplicationConfProdGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, FileFormat.TEXT, ResourceType.TEXT_FILE, "Application.conf.PROD", IDENTIFIER);
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	/**
     *
     */
	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating " + getFileName());
			}
			VelocityContext velocityContext = defaultContext();
			String generated = merge(TEMPLATE_NAME, velocityContext);
			generatedCode = new GeneratedTextResource(getFileName(), generated);
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		// PList file
		textResource = (ApplicationConfProdResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (textResource == null) {
			textResource = GeneratedFileResourceFactory.createApplicationConfProdFileResource(repository, this);
			textResource.setGenerator(this);
			logger.info("Created HELP resource " + textResource.getName());
		} else {
			textResource.setGenerator(this);
			logger.info("Successfully retrieved HELP FILE resource " + textResource.getName());
		}
		((ApplicationConfProdResource) textResource).registerObserverWhenRequired();
		resources.add(textResource);
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getProjectSymbolicDirectory();
	}

}
