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

import java.io.File;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

public class BuildAnaGenerator extends MetaFileGenerator {
	private final Logger logger = FlexoLogger.getLogger(BuildAnaGenerator.class.getPackage().getName());

	private static final String FILE_NAME = "build.ana";

	private File lastGeneratedFile;

	public BuildAnaGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, FileFormat.TEXT, ResourceType.TEXT_FILE, FILE_NAME, FILE_NAME);
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	@Override
	public void generate(boolean forceRegenerate) {
		if (!forceRegenerate && !needsGeneration()) {
			return;
		}
		try {
			startGeneration();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating " + FILE_NAME);
			}
			VelocityContext velocityContext = defaultContext();
			String javaCode = merge("build.ana.vm", velocityContext);
			generatedCode = new GeneratedTextResource(FILE_NAME, javaCode);
			lastGeneratedFile = generatedCode.saveToFile(getProjectGenerator().getRootOutputDirectory());
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Written " + FILE_NAME + " to directory " + getProjectGenerator().getRootOutputDirectory().getAbsolutePath());
			}
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	public File getFile() {
		return lastGeneratedFile != null ? lastGeneratedFile : new File(getProjectGenerator().getRootOutputDirectory(), FILE_NAME);
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		logger.warning("Not implemented");
	}

	@Override
	public String getRelativePath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		// TODO Auto-generated method stub
		return null;
	}

}
