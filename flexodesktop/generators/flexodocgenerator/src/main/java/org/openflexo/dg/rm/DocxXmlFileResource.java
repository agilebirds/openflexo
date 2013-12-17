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
package org.openflexo.dg.rm;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.docx.DGDocxXMLGenerator;
import org.openflexo.dg.file.DGDocxXmlFile;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

public class DocxXmlFileResource<G extends DGDocxXMLGenerator<? extends FlexoModelObject>> extends TextFileResource<G, DGDocxXmlFile>
		implements GenerationAvailableFileResource, FlexoObserver {
	protected static final Logger logger = FlexoLogger.getLogger(HTMLFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public DocxXmlFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public DocxXmlFileResource(FlexoProject aProject) {
		super(aProject);
	}

	@Override
	public String getFileName() {
		try {
			String fileName = super.getFileName();
			if (fileName != null) {
				return fileName;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected DocxXmlFile createGeneratedResourceData() {
		return new DocxXmlFile(getFile(), this);
	}

	@Override
	public DocxXmlFile getGeneratedResourceData() {
		return (DocxXmlFile) super.getGeneratedResourceData();
	}

	/**
	 * Return dependancy computing between this resource, and an other resource, asserting that this resource is contained in this
	 * resource's dependant resources
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

	/**
	 * Overrides getFileFormat
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getFileFormat()
	 */
	@Override
	public FileFormat getResourceFormat() {
		return FileFormat.XML;
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.DOCXXML_FILE;
	}

	/**
	 * Overrides setFileFormat
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#setFileFormat(org.openflexo.foundation.rm.ResourceFormat)
	 */
	@Override
	public void setResourceFormat(FileFormat format) {
		if (format != FileFormat.XML) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Who tried that? This is strictly forbidden. Yes you can thank me for preventing this kind of problem...");
			}
		}
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (observable == getGenerator().getObject()) {
			if (dataModification.propertyName() != null && dataModification.propertyName().equals("dontGenerate")) {
				if (getGenerator().getObject().getDontGenerate()) {
					logger.info("Handle dont generate for object");
					setGenerator(null);
					getCGFile().setMarkedForDeletion(true);
					getCGFile().getRepository().refresh();
				}
			}
		}
	}
}
