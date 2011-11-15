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

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.ie.util.GraphType;
import org.openflexo.foundation.rm.FlexoWebServerFileResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.MetaGenerator;
import org.openflexo.generator.PackagedResourceToCopyGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileResource;

public class WebServerResourcesGenerator extends MetaGenerator<FlexoModelObject, CGRepository> {
	private static final Logger logger = Logger.getLogger(WebServerResourcesGenerator.class.getPackage().getName());

	protected Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>> packagedResourceToCopyGenerator;

	public WebServerResourcesGenerator(ProjectGenerator projectGenerator) {
		super(projectGenerator, null);
		_generators = new Hashtable<FlexoWebServerFileResource, ResourceToCopyGenerator>();
		packagedResourceToCopyGenerator = new Hashtable<FileResource, PackagedResourceToCopyGenerator<CGRepository>>();
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
			logger.fine("Called WebServerResourcesGenerator.generate(forceRegenerate)");
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
		Hashtable<FlexoWebServerFileResource, ResourceToCopyGenerator> generators = new Hashtable<FlexoWebServerFileResource, ResourceToCopyGenerator>();
		List<FlexoWebServerFileResource> allWebResources = getProject().getResourcesOfClass(FlexoWebServerFileResource.class);
		resetSecondaryProgressWindow(allWebResources.size());
		for (FlexoWebServerFileResource wsRes : allWebResources) {
			ResourceToCopyGenerator generator = getGenerator(wsRes);
			if (wsRes.getFile() == null || !wsRes.getFile().exists()) {
				continue;
			}
			refreshSecondaryProgressWindow(FlexoLocalization.localizedForKey("generating") + " " + wsRes.getName(), false);
			if (generator != null) {
				generators.put(wsRes, generator);
				generator.buildResourcesAndSetGenerators(repository, resources);
			} else {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Could not instanciate WebResourceGenerator for " + wsRes);
				}
			}
		}
		for (GraphType type : GraphType.values()) {
			PackagedResourceToCopyGenerator<CGRepository> generator = getFileResourceGenerator(type.getFileResource());
			generator.buildResourcesAndSetGenerators(getRepository(), resources);
		}

		_generators.clear();// Frees memory!
		_generators = generators;
	}

	private Hashtable<FlexoWebServerFileResource, ResourceToCopyGenerator> _generators;

	public PackagedResourceToCopyGenerator<CGRepository> getFileResourceGenerator(FileResource r) {
		PackagedResourceToCopyGenerator<CGRepository> returned = packagedResourceToCopyGenerator.get(r);
		if (returned == null) {
			FileFormat format;
			if (r.getName().endsWith(".png")) {
				format = FileFormat.PNG;
			} else if (r.getName().endsWith(".jpg")) {
				format = FileFormat.JPG;
			} else if (r.getName().endsWith(".sty")) {
				format = FileFormat.LATEX;
			} else if (r.isDirectory()) {
				format = FileFormat.UNKNOWN_DIRECTORY;
			} else {
				format = FileFormat.UNKNOWN_BINARY_FILE;
			}
			packagedResourceToCopyGenerator.put(r, returned = new PackagedResourceToCopyGenerator<CGRepository>(getProjectGenerator(),
					format, ResourceType.COPIED_FILE, r, getRepository().getWebResourcesSymbolicDirectory(), ""));
		}
		return returned;
	}

	protected ResourceToCopyGenerator getGenerator(FlexoWebServerFileResource wsRes) {
		ResourceToCopyGenerator returned = _generators.get(wsRes);
		if (returned == null) {
			_generators.put(wsRes, returned = new ResourceToCopyGenerator(getProjectGenerator(), wsRes, projectGenerator.getRepository()
					.getWebResourcesSymbolicDirectory()));
		}
		return returned;
	}
}
