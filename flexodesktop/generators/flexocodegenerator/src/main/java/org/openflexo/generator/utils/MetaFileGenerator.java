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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.generator.GeneratedTextResource;
import org.openflexo.foundation.cg.generator.GeneratorUtils;
import org.openflexo.foundation.cg.generator.IFlexoTextResourceGenerator;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.FlexoResourceGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.rm.GeneratedFileResourceFactory;
import org.openflexo.generator.rm.ProjectTextFileResource;
import org.openflexo.toolbox.FileFormat;

/**
 * @author gpolet
 * 
 */
public abstract class MetaFileGenerator extends FlexoResourceGenerator<FlexoModelObject, GeneratedTextResource> implements
		IFlexoTextResourceGenerator {

	private final ResourceType _type;
	private final FileFormat _format;
	private final String _fileName;
	private final String _identifier;
	protected TextFileResource textResource;

	/**
	 * @param projectGenerator
	 */
	public MetaFileGenerator(ProjectGenerator projectGenerator, FileFormat format, ResourceType type, String fileName, String identifier) {
		super(projectGenerator);
		_type = type;
		_format = format;
		_fileName = fileName;
		_identifier = identifier;
		if (_identifier == null) {
			new Exception().printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public ProjectGenerator getProjectGenerator() {
		return (ProjectGenerator) super.getProjectGenerator();
	}

	/**
	 * Overrides rebuildDependanciesForResource
	 * 
	 * @see org.openflexo.generator.utils.MetaFileGenerator#rebuildDependanciesForResource(org.openflexo.generator.rm.ProjectTextFileResource)
	 */
	public void rebuildDependanciesForResource(ProjectTextFileResource resource) {
		// TODO Auto-generated method stub

	}

	/**
	 * Overrides getFileFormat
	 * 
	 * @see org.openflexo.generator.FlexoTextResourceGenerator#getFileFormat()
	 */
	@Override
	public final FileFormat getFileFormat() {
		return _format;
	}

	/**
	 * Overrides getFileType
	 * 
	 * @see org.openflexo.generator.FlexoTextResourceGenerator#getFileType()
	 */
	@Override
	public final ResourceType getFileType() {
		return _type;
	}

	/**
	 * Overrides getFileName
	 * 
	 * @see org.openflexo.generator.FlexoTextResourceGenerator#getFileName()
	 */
	@Override
	public final String getFileName() {
		return _fileName;
	}

	/**
	 * Overrides getIdentifier
	 * 
	 * @see org.openflexo.generator.FlexoResourceGenerator#getIdentifier()
	 */
	@Override
	public final String getIdentifier() {
		return _identifier;
	}

	/**
	 * Overrides buildResourcesAndSetGenerators
	 * 
	 * @see org.openflexo.generator.CGGenerator#buildResourcesAndSetGenerators(org.openflexo.foundation.cg.CGRepository, Vector)
	 */
	@Override
	public void buildResourcesAndSetGenerators(CGRepository repository, Vector<CGRepositoryFileResource> resources) {
		textResource = (ProjectTextFileResource) resourceForKeyWithCGFile(ResourceType.TEXT_FILE,
				GeneratorUtils.nameForRepositoryAndIdentifier(repository, getIdentifier()));
		if (textResource == null) {
			textResource = GeneratedFileResourceFactory.createNewProjectTextFileResource(repository, this);
		} else {
			textResource.setGenerator(this);
		}
		resources.add(textResource);
	}

	@Override
	public GeneratedTextResource getGeneratedCode() {
		if ((generatedCode == null) && (textResource != null) && (textResource.getASCIIFile() != null)
				&& textResource.getASCIIFile().hasLastAcceptedContent()) {
			generatedCode = new GeneratedTextResource(getFileName(), textResource.getASCIIFile().getLastAcceptedContent());
		}
		return generatedCode;
	}

}
