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
package org.openflexo.technologyadapter.emf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.InvalidFileNameException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.model.EMFModel;
import org.openflexo.technologyadapter.emf.model.EMFModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.viewpoint.binding.EMFBindingFactory;

/**
 * This class defines and implements the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
public class EMFTechnologyAdapter extends TechnologyAdapter<EMFModel, EMFMetaModel> {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyAdapter.class.getPackage().getName());

	private static final EMFBindingFactory BINDING_FACTORY = new EMFBindingFactory();

	/**
	 * 
	 * Constructor.
	 * 
	 * @throws TechnologyAdapterInitializationException
	 */
	public EMFTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getName()
	 */
	@Override
	public String getName() {
		return "EMF technology adapter";
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFMetaModelRepository(this, resourceCenter);
	}

	/**
	 * Return EMF Property file of MetaModel Directory.
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public Properties getEmfProperties(final File aMetaModelFile) {
		Properties emfProperties = null;
		if (aMetaModelFile.isDirectory()) {
			// Read emf.properties file.
			File[] emfPropertiesFiles = aMetaModelFile.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return dir == aMetaModelFile && name.equalsIgnoreCase("emf.properties");
				}
			});
			if (emfPropertiesFiles.length == 1) {
				try {
					emfProperties = new Properties();
					emfProperties.load(new FileReader(emfPropertiesFiles[0]));
				} catch (FileNotFoundException e) {
					emfProperties = null;
				} catch (IOException e) {
					emfProperties = null;
				}
			}
		}
		return emfProperties;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidMetaModelFile(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return getEmfProperties(aMetaModelFile) != null;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return getEmfProperties(aMetaModelFile).getProperty("URI");
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFMetaModelResource retrieveMetaModelResource(final File aMetaModelFile,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		EMFMetaModelResource metaModelResource = null;

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;

		Properties emfProperties = getEmfProperties(aMetaModelFile);
		if (emfProperties != null) {
			try {
				String uri = emfProperties.getProperty("URI");
				String extension = emfProperties.getProperty("EXTENSION");
				String ePackageClassName = emfProperties.getProperty("PACKAGE");
				String resourceFactoryClassName = emfProperties.getProperty("RESOURCE_FACTORY");
				if (uri != null && extension != null && ePackageClassName != null && resourceFactoryClassName != null) {
					ModelFactory factory = new ModelFactory(EMFMetaModelResource.class);
					metaModelResource = factory.newInstance(EMFMetaModelResource.class);
					metaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
					metaModelResource.setTechnologyAdapter(this);
					metaModelResource.setURI(uri);
					metaModelResource.setName(aMetaModelFile.getName());
					metaModelResource.setFile(aMetaModelFile);
					metaModelResource.setModelFileExtension(extension);
					metaModelResource.setPackageClassName(ePackageClassName);
					metaModelResource.setResourceFactoryClassName(resourceFactoryClassName);

					emfContextManager.registerMetaModel(metaModelResource);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return metaModelResource;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	@Override
	public EMFModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new EMFModelRepository(this, resourceCenter);
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#isValidModelFile(java.io.File,
	 *      org.openflexo.foundation.technologyadapter.FlexoMetaModel)
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		boolean valid = false;
		if (aModelFile.exists()) {
			EMFMetaModelResource emfMetaModelResource = (EMFMetaModelResource) metaModelResource;
			if (aModelFile.getName().endsWith("." + emfMetaModelResource.getModelFileExtension())) {
				if (aModelFile.isFile()) {
					valid = true;
				}
			}
		}
		return valid;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public String retrieveModelURI(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		return retrieveModelResource(aModelFile, metaModelResource, technologyContextManager).getURI();
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public EMFModelResource retrieveModelResource(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		EMFModelResource emfModelResource = null;

		EMFTechnologyContextManager emfContextManager = (EMFTechnologyContextManager) technologyContextManager;
		emfModelResource = emfContextManager.getModel(aModelFile);

		if (emfModelResource == null) {
			try {
				EMFMetaModelResource emfMetaModelResource = (EMFMetaModelResource) metaModelResource;
				emfModelResource = new EMFModelResource(aModelFile, emfMetaModelResource, this, URI.createFileURI(
						aModelFile.getAbsolutePath()).toString());
				emfModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());

				emfContextManager.registerModel(aModelFile, emfModelResource);
			} catch (InvalidFileNameException e) {
				e.printStackTrace();
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
		}

		return emfModelResource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createEmptyModel(org.openflexo.foundation.rm.FlexoProject,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public EMFModelResource createEmptyModel(FlexoProject project, String filename, String modelUri,
			FlexoResource<EMFMetaModel> metaModelResource, TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		return createEmptyModel(modelFile, modelUri, metaModelResource, technologyContextManager);
	}

	/**
	 * Create empty model.
	 * 
	 * @param modelFilePath
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	@Override
	public EMFModelResource createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createEmptyModel(modelFile, modelUri, metaModelResource, technologyContextManager);
	}

	/**
	 * Create empty model.
	 * 
	 * @param modelFile
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public EMFModelResource createEmptyModel(File modelFile, String modelUri, FlexoResource<EMFMetaModel> metaModelResource,
			TechnologyContextManager<EMFModel, EMFMetaModel> technologyContextManager) {
		EMFModelResource result = null;
		try {
			result = retrieveModelResource(modelFile, metaModelResource, technologyContextManager);
			result.getEMFResource().save(null);
			System.out.println("Create empty model " + modelFile.getAbsolutePath() + " as " + metaModelResource.getURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.ViewPoint)
	 */
	@Override
	public TechnologyContextManager<EMFModel, EMFMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
		return new EMFTechnologyContextManager();
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.ViewPoint)
	 */
	@Override
	public EMFModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new EMFModelSlot(viewPoint, this);
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.VirtualModel)
	 *      )
	 */
	@Override
	public EMFModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new EMFModelSlot(virtualModel, this);
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#getTechnologyContextManager()
	 */
	@Override
	public EMFTechnologyContextManager getTechnologyContextManager() {
		return (EMFTechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public EMFBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

}
