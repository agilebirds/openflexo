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
package org.openflexo.technologyadapter.excel.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.resource.SaveResourceException;
import org.openflexo.foundation.resource.SaveResourcePermissionDeniedException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.excel.ExcelTechnologyContextManager;
import org.openflexo.technologyadapter.excel.model.semantics.ExcelModel;
import org.openflexo.toolbox.IProgress;

/**
 * Represents the resource associated to a {@link ExcelModel}
 * 
 * @author sguerin
 * 
 */
public abstract class ExcelModelResourceImpl extends FlexoFileResourceImpl<ExcelModel> implements ExcelModelResource {

	private static final Logger logger = Logger.getLogger(ExcelModelResourceImpl.class.getPackage().getName());

	/**
	 * Creates a new {@link ExcelModelResource} asserting this is an explicit creation: no file is present on file system<br>
	 * This method should not be used to retrieve the resource from a file in the file system, use
	 * {@link #retrieveOWLOntologyResource(File, OWLOntologyLibrary)} instead
	 * 
	 * @param ontologyURI
	 * @param owlFile
	 * @param ontologyLibrary
	 * @return
	 */
	public static ExcelModelResource makeExcelModelResource(String modelURI, File modelFile, ExcelMetaModelResource excelMetaModelResource,
			ExcelTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(ExcelModelResource.class);
			ExcelModelResourceImpl returned = (ExcelModelResourceImpl) factory.newInstance(ExcelModelResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(modelFile.getName());
			returned.setFile(modelFile);
			returned.setURI(modelURI);
			returned.setMetaModelResource(excelMetaModelResource);
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerResource(returned);
			// Creates the Excel model from scratch
			ExcelModel resourceData = null; // Please insert creation code here
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
	public static ExcelModelResource retrieveExcelModelResource(File modelFile, ExcelMetaModelResource excelMetaModelResource,
			ExcelTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(ExcelModelResource.class);
			ExcelModelResourceImpl returned = (ExcelModelResourceImpl) factory.newInstance(ExcelModelResource.class);
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			returned.setName(modelFile.getName());
			returned.setFile(modelFile);
			returned.setURI(modelFile.toURI().toString());
			returned.setMetaModelResource(excelMetaModelResource);
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			technologyContextManager.registerResource(returned);
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
	public ExcelModel loadResourceData(IProgress progress) throws ResourceLoadingCancelledException, FileNotFoundException, FlexoException {
		ExcelModel resourceData = null; // TODO: insert loading code here
		setResourceData(resourceData);
		return resourceData;
	}

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public void save(IProgress progress) throws SaveResourceException {
		ExcelModel resourceData;
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
			writeToFile();
			hasWrittenOnDisk(lock);
			notifyResourceStatusChanged();
			resourceData.clearIsModified(false);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + getURI() + " : " + getFile().getName());
			}
		}
	}

	@Override
	public ExcelModel getModelData() {
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
	public ExcelModel getModel() {
		return getModelData();
	}

	/**
	 * Write file.
	 * 
	 * @throws SaveResourceException
	 */
	private void writeToFile() throws SaveResourceException {
		// TODO: insert saving code here
		logger.info("Wrote " + getFile());
	}

	@Override
	public Class<ExcelModel> getResourceDataClass() {
		return ExcelModel.class;
	}

}
