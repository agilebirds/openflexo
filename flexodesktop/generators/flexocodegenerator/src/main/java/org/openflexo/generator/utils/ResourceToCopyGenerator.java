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

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.generator.GeneratedCopiedFile;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;

public class ResourceToCopyGenerator extends FlexoResourceGenerator<FlexoModelObject, GeneratedCopiedFile> {

	private static final Logger logger = Logger.getLogger(ResourceToCopyGenerator.class.getPackage().getName());

	private FlexoFileResource _source;
	private CGRepositoryFileResource generatedResource;
	private CGSymbolicDirectory _symbolicDir;
	private String relativePath;

	public ResourceToCopyGenerator(ProjectGenerator projectGenerator, FlexoFileResource source, CGSymbolicDirectory symbolicDir) {
		this(projectGenerator, source, symbolicDir, "");
	}

	public ResourceToCopyGenerator(ProjectGenerator projectGenerator, FlexoFileResource source, CGSymbolicDirectory symbolicDir,
			String relativePath) {
		super(projectGenerator);
		_symbolicDir = symbolicDir;
		_source = source;
		if (_source instanceof CGRepositoryFileResource) {
			generatedResource = (CGRepositoryFileResource) _source;
		}
		this.relativePath = relativePath;
		generatedCode = new GeneratedCopiedFile(_source.getFile());
	}

	@Override
	public void generate(boolean forceRegenerate) {

		if (generatedResource != null && generatedResource.getGenerator() != null) {
			generatedResource.getGenerator().generate(forceRegenerate);
		}
		try {
			if (forceRegenerate || _source.needsUpdate()) {
				if (logger.isLoggable(Level.INFO)) {
					logger.info("Called force generate on copied resource");
				}
				if (_source instanceof FlexoGeneratedResource) {
					try {
						_source.update();
					} catch (SaveResourceException e) {
						e.printStackTrace();
					} catch (FlexoException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (ResourceDependencyLoopException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (ResourceDependencyLoopException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.log(Level.SEVERE, "Resource dependency loop ", e);
			}
		}
	}

	/**
	 * Overrides getMemoryLastGenerationDate
	 * 
	 * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getMemoryLastGenerationDate()
	 */
	@Override
	public Date getMemoryLastGenerationDate() {
		if (generatedResource != null && generatedResource.getGenerator() != null) {
			return generatedResource.getGenerator().getMemoryLastGenerationDate();
		}
		return _source.getDiskLastModifiedDate();
	}

	/**
	 * Overrides getUsedTemplates
	 * 
	 * @see org.openflexo.foundation.cg.generator.IFlexoResourceGenerator#getUsedTemplates()
	 */
	@Override
	public Vector<CGTemplate> getUsedTemplates() {
		return new Vector<CGTemplate>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasFormattingException() {
		if (generatedResource != null && generatedResource.getGenerator() != null) {
			return generatedResource.getGenerator().hasFormattingException();
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAppendingException() {
		if (generatedResource != null && generatedResource.getGenerator() != null) {
			return generatedResource.getGenerator().hasAppendingException();
		}
		return false;
	}

	@Override
	public boolean isCodeAlreadyGenerated() {
		if (generatedResource != null && generatedResource.getGenerator() != null) {
			return generatedResource.getGenerator().isCodeAlreadyGenerated();
		}
		return true;
	}

	@Override
	public boolean needsGeneration() {
		if (generatedResource != null && generatedResource.getGenerator() != null) {
			return generatedResource.getGenerator().needsGeneration();
		}
		return false;
	}

	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated() {
		return false;
	}

	@Override
	public boolean needsRegenerationBecauseOfTemplateUpdated(Date diskLastGenerationDate) {
		return false;
	}

	@Override
	public TemplateLocator getTemplateLocator() {
		return null;
	}

	public FlexoFileResource getSource() {
		return _source;
	}

	public String getRelativePath() {
		return relativePath;
	}

	@Override
	public Logger getGeneratorLogger() {
		return logger;
	}

	public CGSymbolicDirectory getSymbolicDirectory() {
		return _symbolicDir;
	}

	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		resources.add(GeneratedFileResourceFactory.createNewFlexoCopyOfFlexoResource(repository, this, getSymbolicDirectory(), _source));
	}

	@Override
	public String getIdentifier() {
		return "COPY_OF_" + _source.getFullyQualifiedName();
	}

}
