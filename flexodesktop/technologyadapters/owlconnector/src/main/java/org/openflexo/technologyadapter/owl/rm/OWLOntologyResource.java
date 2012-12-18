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
package org.openflexo.technologyadapter.owl.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.ontology.dm.OntologyImported;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProjectBuilder;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.rm.LoadResourceException;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;

/**
 * Represents the resource associated to a {@link OWLOntology}
 * 
 * @author sguerin
 * 
 */
public class OWLOntologyResource extends FlexoStorageResource<OWLOntology> implements FlexoResource<OWLOntology>,
		FlexoModelResource<OWLOntology, OWLOntology>, FlexoMetaModelResource<OWLOntology, OWLOntology> {

	private static final Logger logger = Logger.getLogger(OWLOntologyResource.class.getPackage().getName());

	private OWLTechnologyAdapter technologyAdapter;
	private OWLOntologyLibrary ontologyLibrary = null;
	private String ontologyURI;
	private File absoluteFile;
	private OWLOntology metaModel;

	/**
	 * Constructor used for XML Serialization: never try to instanciate resource from this constructor
	 * 
	 * @param builder
	 */
	public OWLOntologyResource(FlexoProjectBuilder builder) {
		this(builder.project, null);
		builder.notifyResourceLoading(this);
	}

	public OWLOntologyResource(FlexoProject aProject, OWLTechnologyAdapter technologyAdapter) {
		super(aProject);
		this.technologyAdapter = technologyAdapter;
	}

	public OWLOntologyResource(File owlFile, OWLOntologyLibrary ontologyLibrary) {
		super((FlexoProject) null);
		this.ontologyURI = OWLOntology.findOntologyURI(owlFile);
		absoluteFile = owlFile;
		setOntologyLibrary(ontologyLibrary);
		this.technologyAdapter = ontologyLibrary.getTechnologyAdapter();
	}

	@Override
	public String getURI() {
		return ontologyURI;
	}

	@Override
	public File getFile() {
		return absoluteFile;
	}

	public OWLOntologyLibrary getOntologyLibrary() {
		return ontologyLibrary;
	}

	public void setOntologyLibrary(OWLOntologyLibrary ontologyLibrary) {
		this.ontologyLibrary = ontologyLibrary;
		ontologyLibrary.registerOntology(this);
	}

	/*public OWLOntologyResource(FlexoProject aProject, FlexoDMResource dmResource, FlexoProjectFile eoModelFile)
	        throws InvalidFileNameException
	{
	    this(aProject);
	    setResourceFile(eoModelFile);
	    addToSynchronizedResources(dmResource);
	    if (logger.isLoggable(Level.INFO))
	        logger.info("Build new FlexoEOModelResource");
	}*/

	public OWLOntologyResource(FlexoProject aProject, OWLOntology anOntology, FlexoProjectFile ontologyFile)
			throws InvalidFileNameException, DuplicateResourceException {
		this(aProject, anOntology.getTechnologyAdapter());
		_resourceData = anOntology;
		anOntology.setFlexoResource(this);
		setResourceFile(ontologyFile);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROJECT_ONTOLOGY;
	}

	@Override
	public String getName() {
		if (getProject() != null) {
			return getProject().getProjectName();
		}
		return getURI();
	}

	@Override
	public Class getResourceDataClass() {
		return OWLOntology.class;
	}

	@Override
	public void setName(String aName) {
		// Not allowed
	}

	@Override
	public OWLTechnologyAdapter getTechnologyAdapter() {
		return technologyAdapter;
	}

	@Override
	public void setTechnologyAdapter(TechnologyAdapter<?, ?> technologyAdapter) {
		this.technologyAdapter = (OWLTechnologyAdapter) technologyAdapter;
	}

	@Override
	public OWLOntology performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException {

		OWLOntology ontology = new OWLOntology(getURI(), getFile(), getOntologyLibrary(), getTechnologyAdapter());
		setChanged();
		notifyObservers(new OntologyImported(ontology));

		_resourceData = ontology;

		try {
			_resourceData.setFlexoResource(this);
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
			logger.warning("Should not happen");
		}
		notifyResourceStatusChanged();
		return _resourceData;
	}

	/**
	 * Implements
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
	 * @see org.openflexo.foundation.rm.FlexoResource#saveResourceData()
	 */
	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (_resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			_writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getResourceIdentifier() + " : " + getFile().getName());
			}
		}
		if (clearIsModified) {
			getResourceData().clearIsModified(false);
		}
	}

	public void _writeToFile() throws SaveResourceException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			_resourceData.getOntModel().write(out, null, _resourceData.getOntologyURI());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new SaveResourceException(this);
			}
		}

		logger.info("Wrote " + getFile());

	}

	@Override
	public OWLOntology getMetaModel() {
		return metaModel;
	}

	@Override
	public void setMetaModel(OWLOntology aMetaModel) {
		metaModel = aMetaModel;
	}

	@Override
	public OWLOntology getModel() {
		try {
			return getResourceData(null);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			return null;
		}
	}
}
