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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.UnexpectedExceptionOccuredException;
import org.openflexo.generator.rm.ProjectTextFileResource;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

public class DotClasspathGenerator extends MetaFileGenerator {

	private static final Logger logger = FlexoLogger.getLogger(DotClasspathGenerator.class.getPackage().getName());

	private static final String FILE_NAME = ".classpath";
	public static final String IDENTIFIER = "DOT_CLASSPATH";

	public DotClasspathGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, FileFormat.XML, ResourceType.SYSTEM_FILE, FILE_NAME, IDENTIFIER);
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
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + getIdentifier(), false);
			startGeneration();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Generating " + FILE_NAME);
			}
			VelocityContext velocityContext = defaultContext();
			velocityContext.put("subPaths", computeSubPaths());
			String javaCode = merge("dotClasspath.vm", velocityContext);
			generatedCode = new GeneratedTextResource(FILE_NAME, javaCode);
			stopGeneration();
		} catch (GenerationException e) {
			setGenerationException(e);
		} catch (Exception e) {
			setGenerationException(new UnexpectedExceptionOccuredException(e, getProjectGenerator()));
		}
	}

	/**
	 * @return
	 */
	private Vector<String> computeSubPaths() {
		Vector<String> v = new Vector<String>();
		Enumeration<CGFile> en = getRepository().getFiles().elements();
		Vector<CGSymbolicDirectory> javaSymbolicDirectories = new Vector<CGSymbolicDirectory>();

		while (en.hasMoreElements()) {
			CGFile file = en.nextElement();
			if (file instanceof CGJavaFile) {
				CGSymbolicDirectory symDir = getRepository().getSymbolicDirectory(file);
				if (symDir != null) {
					if (!symDir.getName().equals("JavaWebObjects") && !symDir.getName().equals("DataModel")) {
						if (!javaSymbolicDirectories.contains(symDir)) {
							javaSymbolicDirectories.add(symDir);
							v.add(symDir.getDirectory().getRelativePath().substring(1));
						}
					} else {

						String path = file
								.getResource()
								.getResourceFile()
								.getRelativePath()
								.substring(
										0,
										file.getResource().getResourceFile().getRelativePath().length()
												- file.getResource().getResourceFile().getFile().getName().length());
						if (path.startsWith("/")) {
							path = path.substring(1);
						}

						if (((CGJavaFile) file).getModelEntity() != null) {
							org.openflexo.foundation.dm.DMEntity entity = ((CGJavaFile) file).getModelEntity();
							if (entity.getPathForPackage().length() > 0) {
								path = path.substring(0, path.indexOf(entity.getPathForPackage()));
							}
						}

						if (path.endsWith("/")) {
							path = path.substring(0, path.length() - 1);
						}
						if (!v.contains(path)) {
							v.add(path);
						}

					}
				}
			}

		}
		return v;
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public CGSymbolicDirectory getSymbolicDirectory(CGRepository repository) {
		return repository.getProjectSymbolicDirectory();
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaFileGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.ProjectTextFileResource)
	 */
	@Override
	public void rebuildDependanciesForResource(ProjectTextFileResource resource) {
		resource.addToDependantResources(getProject().getFlexoComponentLibraryResource());
	}

}
