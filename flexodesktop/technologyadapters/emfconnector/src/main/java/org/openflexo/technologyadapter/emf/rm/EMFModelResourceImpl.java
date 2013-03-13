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
package org.openflexo.technologyadapter.emf.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.emf.EMFTechnologyContextManager;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.io.EMFModelConverter;
import org.openflexo.toolbox.IProgress;

/**
 * Represents the resource associated to a {@link OWLOntology}
 * 
 * @author sguerin
 * 
 */
public abstract class EMFModelResourceImpl extends FlexoFileResourceImpl<EMFModel> implements EMFModelResource {

	private static final Logger logger = Logger.getLogger(EMFModelResourceImpl.class.getPackage().getName());

	/** Model Resource. */
	protected Resource modelResource;

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
	public static EMFModelResource makeEMFModelResource(String modelURI, File modelFile, EMFMetaModelResource emfMetaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(EMFModelResource.class);
			EMFModelResourceImpl returned = (EMFModelResourceImpl) factory.newInstance(EMFModelResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(modelFile.getName());
			returned.setFile(modelFile);
			returned.setURI(modelURI);
			returned.setMetaModelResource(emfMetaModelResource);
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerModel(returned);
			// Creates the EMF model from scratch
			EMFModelConverter converter = new EMFModelConverter();
			EMFModel resourceData = converter.convertModel(returned.getMetaModelResource().getMetaModelData(), returned.getEMFResource());
			returned.setResourceData(resourceData);
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
	public static EMFModelResource retrieveEMFModelResource(File modelFile, EMFMetaModelResource emfMetaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(EMFModelResource.class);
			EMFModelResourceImpl returned = (EMFModelResourceImpl) factory.newInstance(EMFModelResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(modelFile.getName());
			returned.setFile(modelFile);
			returned.setURI(modelFile.toURI().toString());
			returned.setMetaModelResource(emfMetaModelResource);
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerModel(returned);
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
	public EMFModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, ResourceDependencyLoopException,
			FileNotFoundException, FlexoException {
		try {
			getEMFResource().load(null);
			EMFModelConverter converter = new EMFModelConverter();
			EMFModel resourceData = converter.convertModel(getMetaModelResource().getMetaModelData(), getEMFResource());
			setResourceData(resourceData);
			return resourceData;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public void save(IProgress progress) throws SaveResourceException {
		EMFModel resourceData;
		try {
			resourceData = getResourceData(progress);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ResourceDependencyLoopException e) {
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
			writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
			}
		}
	}

	@Override
	public EMFModel getModelData() {
		try {
			return getResourceData(null);
		} catch (ResourceLoadingCancelledException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (ResourceDependencyLoopException e) {
			e.printStackTrace();
			return null;
		} catch (FlexoException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public EMFModel getModel() {
		return getModelData();
	}

	/**
	 * Write file.
	 * 
	 * @throws SaveResourceException
	 */
	private void writeToFile() throws SaveResourceException {
		try {
			getEMFResource().save(null);
			logger.info("Wrote " + getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Getter of EMF Model Resource.
	 * 
	 * @return the modelResource value
	 */
	public Resource getEMFResource() {
		if (modelResource == null) {
			if (getMetaModelResource() == null) {
				logger.warning("EMFModel has no meta-model !!!");
				return null;
			}
			modelResource = getMetaModelResource().getMetaModelData().getResource().getResourceFactory()
					.createResource(org.eclipse.emf.common.util.URI.createFileURI(getFile().getAbsolutePath()));
		}
		return modelResource;
	}

	@Override
	public Class<EMFModel> getResourceDataClass() {
		return EMFModel.class;
	}

}
