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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.html.ProjectDocHTMLGenerator;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;

public class FlexoReaderGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {
	private static final Logger logger = Logger.getLogger(FlexoReaderGenerator.class.getPackage().getName());

	private ProjectDocHTMLGenerator docProjectGenerator;

	public FlexoReaderGenerator(ProjectGenerator projectGenerator) throws GenerationException {
		super(projectGenerator, null);
		_generators = new Hashtable<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>, ResourceToCopyGenerator>();
		docProjectGenerator = new ProjectDocHTMLGenerator(projectGenerator.getProject(), projectGenerator.getRepository()
				.getReaderRepository());
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
	public void generate(boolean forceRegenerate) throws GenerationException {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called FlexoReaderGenerator.generate(forceRegenerate)");
		}
		resetSecondaryProgressWindow(_generators.values().size());
		startGeneration();
		for (ResourceToCopyGenerator generator : _generators.values()) {
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + generator.getIdentifier(), false);
			generator.generate(forceRegenerate);
		}
		stopGeneration();
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		docProjectGenerator.refreshConcernedResources();
		Vector<CGFile> cgFiles = repository.getReaderRepository().getFiles();
		resetSecondaryProgressWindow(cgFiles.size());
		for (CGFile file : cgFiles) {
			if (file.getResource() == null || file.getResource().getFile() == null) {
				continue;
			}
			ResourceToCopyGenerator generator = getGenerator(file.getResource());
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + file.getName(), false);
			if (generator != null) {
				generator.buildResourcesAndSetGenerators(repository, resources);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate ResourceToCopyGenerator for " + file);
				}
			}
		}

	}

	private Hashtable<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>, ResourceToCopyGenerator> _generators;

	protected ResourceToCopyGenerator getGenerator(
			CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile> resource) {
		ResourceToCopyGenerator returned = _generators.get(resource);
		if (returned == null) {
			String relativePath;
			try {
				File temp = resource.getFile().getParentFile().getCanonicalFile();
				File target = getRepository().getReaderRepository().getDirectory().getCanonicalFile();
				relativePath = "";
				while ((!temp.equals(target)) && (temp.getParentFile() != null)) {
					if (relativePath.length() == 0) {
						relativePath = temp.getName();
					} else {
						relativePath = temp.getName() + "/" + relativePath;
					}
					temp = temp.getParentFile();
				}
				if (temp == null || temp.getParentFile() == null) {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			_generators.put(resource, returned = new ResourceToCopyGenerator(getProjectGenerator(), resource, projectGenerator
					.getRepository().getReaderSymbolicDirectory(), relativePath));
		}
		return returned;
	}
}
