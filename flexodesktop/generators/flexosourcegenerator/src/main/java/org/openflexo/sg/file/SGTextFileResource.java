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
package org.openflexo.sg.file;

import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sg.generator.SGTextFileGenerator;
import org.openflexo.toolbox.FileFormat;


/**
 * @author sylvain
 * 
 */
public class SGTextFileResource extends TextFileResource<SGTextFileGenerator, SGTextFile> implements GenerationAvailableFileResource, FlexoObserver {
	static final Logger logger = FlexoLogger.getLogger(SGTextFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public SGTextFileResource(FlexoProjectBuilder builder) {
		super(builder);
		setResourceFormat(FileFormat.TEXT);
	}

	/**
	 * @param aProject
	 */
	public SGTextFileResource(FlexoProject aProject) {
		super(aProject);
		setResourceFormat(FileFormat.TEXT);
	}

	@Override
	protected GeneratedTextFile createGeneratedResourceData() {
		return new GeneratedTextFile(getFile(), this);
	}

	/**
	 * Rebuild resource dependancies for this resource
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();

		if (getGenerator() != null) {
			getGenerator().rebuildDependanciesForResource(this);
		}
	}

	@Override
	public GeneratedTextFile getGeneratedResourceData() {
		return (GeneratedTextFile) super.getGeneratedResourceData();
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this resource's dependant resources
	 * 
	 * @param resource
	 * @param dependancyScheme
	 * @return
	 */
	@Override
	public boolean optimisticallyDependsOf(FlexoResource resource, Date requestDate) {
		if (resource instanceof TemplateLocator) {
			return ((TemplateLocator) resource).needsUpdateForResource(this);
		}
		return super.optimisticallyDependsOf(resource, requestDate);
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
	}

	@Override
	public SourceRepository getRepository() {
		return getGenerator().getProjectGenerator().getRepository();
	}

	public FileFormat getFileFormat() {
		if (getGenerator() instanceof SGTextFileGenerator) {
			return (getGenerator()).getFileFormat();
		}
		return FileFormat.UNKNOWN_ASCII_FILE;
	}

	@Override
	public FileFormat getResourceFormat() {
		return getFileFormat();
	}
}
