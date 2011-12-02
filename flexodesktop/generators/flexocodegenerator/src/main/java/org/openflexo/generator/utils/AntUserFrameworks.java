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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

public class AntUserFrameworks extends MetaFileGenerator {

	private static final Logger logger = FlexoLogger.getLogger(AntUserFrameworks.class.getPackage().getName());

	private static final String FILE_NAME = "ant.frameworks.user.home.manual";

	private static final String IDENTIFIER = "WO_PROJECT_ANT_FRAMEWORKS_USER";

	public AntUserFrameworks(ProjectGenerator projectGenerator) {
		super(projectGenerator, FileFormat.XML, ResourceType.TEXT_FILE, FILE_NAME, IDENTIFIER);
	}

	/**
	 * Overrides generate
	 * 
	 * @see org.openflexo.generator.CGGenerator#generate(boolean)
	 */
	@Override
	public void generate(boolean forceRegenerate) {
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Generating " + FILE_NAME);
		}
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			String generated = merge("AntUserFrameworks.vm", defaultContext());
			generatedCode = new GeneratedTextResource(FILE_NAME, generated);
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

	/**
	 * Overrides getRelativePath
	 * 
	 * @see org.openflexo.generator.FlexoTextResourceGenerator#getRelativePath()
	 */
	@Override
	public String getRelativePath() {
		return ResourceGenerator.WO_PROJECT_RELATIVE_PATH;
	}

	/**
	 * Overrides getSymbolicDirectory
	 * 
	 * @see org.openflexo.generator.FlexoTextResourceGenerator#getSymbolicDirectory(org.openflexo.foundation.cg.CGRepository)
	 */
	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getProjectSymbolicDirectory();
	}
}
