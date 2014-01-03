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

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.owl.OWLTechnologyAdapter;
import org.openflexo.technologyadapter.owl.model.OWLOntology;
import org.openflexo.technologyadapter.owl.model.OWLOntologyLibrary;
import org.openflexo.toolbox.IProgress;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.RDFWriter;

/**
 * Represents the resource associated to a {@link OWLOntology}
 * 
 * @author sguerin
 * 
 */
public abstract class OWLOntologyResourceImpl extends FlexoFileResourceImpl<OWLOntology> implements OWLOntologyResource {

	private static final Logger logger = Logger.getLogger(OWLOntologyResourceImpl.class.getPackage().getName());

	/**
	 * Creates a new {@link OWLOntologyResource} asserting this is an explicit creation: no file is present on file system<br>
	 * This method should not be used to retrieve the resource from a file in the file system, use
	 * {@link #retrieveOWLOntologyResource(File, OWLOntologyLibrary)} instead
	 * 
	 * @param ontologyURI
	 * @param owlFile
	 * @param ontologyLibrary
	 * @return
	 */
	public static OWLOntologyResource makeOWLOntologyResource(String ontologyURI, File owlFile, OWLOntologyLibrary ontologyLibrary) {
		try {
			ModelFactory factory = new ModelFactory(OWLOntologyResource.class);
			OWLOntologyResourceImpl returned = (OWLOntologyResourceImpl) factory.newInstance(OWLOntologyResource.class);
			returned.setTechnologyAdapter(ontologyLibrary.getTechnologyAdapter());
			returned.setOntologyLibrary(ontologyLibrary);
			returned.setName(owlFile.getName());
			returned.setFile(owlFile);
			returned.setURI(ontologyURI);
			// Register the ontology
			ontologyLibrary.registerResource(returned);

			returned.setServiceManager(ontologyLibrary.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());

			// Creates the ontology
			returned.setResourceData(OWLOntology.createOWLEmptyOntology(ontologyURI, owlFile, ontologyLibrary,
					ontologyLibrary.getTechnologyAdapter()));
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Instanciates a new {@link OWLOntologyResource} asserting we are about to built a resource matching an existing file in the file
	 * system<br>
	 * This method should not be used to explicitely build a new ontology
	 * 
	 * @param owlFile
	 * @param ontologyLibrary
	 * @return
	 */
	public static OWLOntologyResource retrieveOWLOntologyResource(File owlFile, OWLOntologyLibrary ontologyLibrary) {
		try {
			ModelFactory factory = new ModelFactory(OWLOntologyResource.class);
			OWLOntologyResourceImpl returned = (OWLOntologyResourceImpl) factory.newInstance(OWLOntologyResource.class);
			returned.setTechnologyAdapter(ontologyLibrary.getTechnologyAdapter());
			returned.setOntologyLibrary(ontologyLibrary);
			returned.setName(OWLOntology.findOntologyName(owlFile));
			returned.setFile(owlFile);
			returned.setURI(OWLOntology.findOntologyURI(owlFile));
			returned.setServiceManager(ontologyLibrary.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			// Register the ontology
			ontologyLibrary.registerOntology(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load the &quot;real&quot; load resource data of this resource.
	 * 
	 * @param progress
	 *            a progress monitor in case the resource data is not immediately available.
	 * @return the resource data.
	 * @throws ResourceLoadingCancelledException
	 * @throws ResourceDependencyLoopException
	 * @throws FileNotFoundException
	 * @throws FlexoException
	 */
	@Override
	public OWLOntology loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FileNotFoundException, FlexoException {
		OWLOntology returned = new OWLOntology(getURI(), getFile(), getOntologyLibrary(), getTechnologyAdapter());
		returned.setResource(this);
		resourceData = returned;
		return returned;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public void save(IProgress progress) throws SaveResourceException {
		OWLOntology resourceData;
		try {
			resourceData = getResourceData(progress);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (FlexoException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		}

		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (resourceData != null) {
			FileWritingLock lock = willWriteOnDisk();
			_writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			resourceData.clearIsModified(false);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
			}
		}
	}

	private void _writeToFile() throws SaveResourceException {
		System.out.println("Saving OWL ontology to " + getFile().getAbsolutePath());
		FileOutputStream out = null;
		try {
			OWLOntology ontology = getResourceData(null);
			OntModel ontModel = ontology.getOntModel();
			ontModel.setNsPrefix("base", ontology.getURI());
			out = new FileOutputStream(getFile());
			RDFWriter writer = ontModel.getWriter("RDF/XML-ABBREV");
			writer.setProperty("xmlbase", ontology.getURI());
			writer.write(ontModel.getBaseModel(), out, ontology.getURI());
			// getOntModel().setNsPrefix("base", getOntologyURI());
			// getOntModel().write(out, "RDF/XML-ABBREV", getOntologyURI()); // "RDF/XML-ABBREV"
			clearIsModified(true);
			logger.info("Wrote " + getFile());
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (FlexoException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.warning("FileNotFoundException: " + e.getMessage());
			throw new SaveResourceException(this);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("IOException: " + e.getMessage());
				throw new SaveResourceException(this);
			}
		}

	}

	@Override
	public FlexoMetaModelResource<OWLOntology, OWLOntology, OWLTechnologyAdapter> getMetaModelResource() {
		logger.warning("FlexoMetaModelResource() not fully implemented in OWLOntologyResource");
		// TODO: implement this and extends cardinality

		return getModel().getImportedOntologies().get(0).getResource();
	}

	@Override
	public void setMetaModelResource(FlexoMetaModelResource<OWLOntology, OWLOntology, OWLTechnologyAdapter> aMetaModelResource) {
		// TODO: implement this and extends cardinality
	}

	@Override
	public OWLOntology getModelData() {
		try {
			return getResourceData(null);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (FlexoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public OWLOntology getMetaModelData() {
		return getModelData();
	}

	@Override
	public OWLOntology getModel() {
		return getModelData();
	}

	@Override
	public Class<OWLOntology> getResourceDataClass() {
		return OWLOntology.class;
	}

}
