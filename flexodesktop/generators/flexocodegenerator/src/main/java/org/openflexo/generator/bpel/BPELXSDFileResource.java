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
package org.openflexo.generator.bpel;

import java.util.Date;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.TextFileResource;
import org.openflexo.generator.TemplateLocator;
import org.openflexo.generator.cg.CGTextFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;

/**
 * @author sylvain
 * 
 */
public class BPELXSDFileResource extends TextFileResource<BPELXSDFileGenerator, CGTextFile> implements GenerationAvailableFileResource,
		FlexoObserver {
	public static Logger logger = FlexoLogger.getLogger(BPELXSDFileResource.class.getPackage().getName());

	/**
	 * @param builder
	 */
	public BPELXSDFileResource(FlexoProjectBuilder builder) {
		super(builder);
	}

	/**
	 * @param aProject
	 */
	public BPELXSDFileResource(FlexoProject aProject) {
		super(aProject);
	}

	public static String nameForRepositoryAndIdentifier(GenerationRepository repository, String identifier) {
		return repository.getName() + ".BPEL_XSD." + identifier;
	}

	protected String getIdentifier() {
		if (getGenerator() != null) {
			return getGenerator().getIdentifier();
		}
		return null;
	}

	@Override
	public String getFileName() {
		if (getGenerator() != null) {
			return getGenerator().getFileName();
		}
		return null;
	}

	@Override
	protected BPELXSDFile createGeneratedResourceData() {
		return new BPELXSDFile(getFile(), this);
	}

	@Override
	public BPELXSDFile getGeneratedResourceData() {
		return (BPELXSDFile) super.getGeneratedResourceData();
	}

	/**
	 * Overrides getFileFormat
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getFileFormat()
	 */
	@Override
	public FileFormat getResourceFormat() {
		return FileFormat.XSD;
	}

	/**
	 * Overrides getResourceType
	 * 
	 * @see org.openflexo.foundation.rm.cg.TextFileResource#getResourceType()
	 */
	@Override
	public ResourceType getResourceType() {
		return ResourceType.XSD;
	}

	/**
	 * Overrides update
	 * 
	 * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
	 *      org.openflexo.foundation.DataModification)
	 */
	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		// TODO: is it required for BPEL ???
		/*if (observable==getGenerator().getObject()) {
		    if (dataModification.propertyName()!=null && dataModification.equals("dontGenerate")) {
		        if (getGenerator().getObject().getDontGenerate()) {
		            logger.info("Handle dont generate for object");
		            setGenerator(null);
		            getCGFile().setMarkedForDeletion(true);
		            getCGFile().getRepository().refresh();
		        }
		    }
		}*/
	}

	private final boolean isObserverRegistered = false;

	public void registerObserverWhenRequired() {
		// TODO: is it required for BPEL ???
		/*if ((!isObserverRegistered) && (getEntity() != null)) {
			isObserverRegistered = true;
		    if (logger.isLoggable(Level.FINE))
		        logger.fine("*** addObserver "+getFileName()+" for "+getEntity());
			getEntity().addObserver(this);
		}*/
	}

	/**
	 * Implements the dependancy model of BPEL BPEL file depends of DataModel
	 */
	@Override
	public void rebuildDependancies() {
		super.rebuildDependancies();
		addToDependantResources(getProject().getFlexoDMResource());
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

}
