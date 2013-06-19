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
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.DeclareModelSlot;
import org.openflexo.foundation.technologyadapter.DeclareModelSlots;
import org.openflexo.foundation.technologyadapter.DeclareRepositoryType;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModel;
import org.openflexo.technologyadapter.emf.metamodel.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.model.EMFModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelResourceImpl;
import org.openflexo.technologyadapter.emf.viewpoint.binding.EMFBindingFactory;

/**
 * This class defines and implements the EMF technology adapter
 * 
 * @author sylvain
 * 
 */
@DeclareModelSlots({ // ModelSlot(s) declaration
@DeclareModelSlot(FML = "EMFModelSlot", modelSlotClass = EMFModelSlot.class) // Classical type-safe interpretation
})
@DeclareRepositoryType({ EMFMetaModelRepository.class, EMFModelRepository.class })
public class EMFTechnologyAdapter extends TechnologyAdapter {

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

	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

		EMFTechnologyContextManager technologyContextManager = (EMFTechnologyContextManager) getTechnologyAdapterService()
				.getTechnologyContextManager(this);

		EMFMetaModelRepository mmRepository = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
		if (mmRepository == null) {
			mmRepository = createMetaModelRepository(resourceCenter);
		}

		EMFModelRepository modelRepository = resourceCenter.getRepository(EMFModelRepository.class, this);
		if (modelRepository == null) {
			modelRepository = createModelRepository(resourceCenter);
		}

		// First pass on meta-models only
		Iterator<I> it = resourceCenter.iterator();

		while (it.hasNext()) {
			if (it instanceof File) {
				File candidateFile = (File) it;
				if (isValidMetaModelFile(candidateFile, technologyContextManager)) {
					EMFMetaModelResource mmRes = retrieveMetaModelResource(candidateFile, technologyContextManager);
					if (mmRes != null) {
						RepositoryFolder<EMFMetaModelResource> folder;
						try {
							folder = mmRepository.getRepositoryFolder(candidateFile, true);
							mmRepository.registerResource(mmRes, folder);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// Also register the resource in the ResourceCenter seen as a ResourceRepository
						if (resourceCenter instanceof ResourceRepository) {
							try {
								((ResourceRepository) resourceCenter).registerResource(mmRes,
										((ResourceRepository<?>) resourceCenter).getRepositoryFolder(candidateFile, true));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		// Second pass on models
		it = resourceCenter.iterator();

		while (it.hasNext()) {
			if (it instanceof File) {
				File candidateFile = (File) it;
				for (EMFMetaModelResource mmRes : mmRepository.getAllResources()) {
					if (isValidModelFile(candidateFile, mmRes, technologyContextManager)) {
						EMFModelResource mRes = retrieveModelResource(candidateFile, mmRes, technologyContextManager);
						if (mRes != null) {
							RepositoryFolder<EMFModelResource> folder;
							try {
								folder = modelRepository.getRepositoryFolder(candidateFile, true);
								modelRepository.registerResource(mRes, folder);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							// Also register the resource in the ResourceCenter seen as a ResourceRepository
							if (resourceCenter instanceof ResourceRepository) {
								try {
									((ResourceRepository) resourceCenter).registerResource(mmRes,
											((ResourceRepository<?>) resourceCenter).getRepositoryFolder(candidateFile, true));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Retrieve (creates it when not existant) folder containing supplied file
	 * 
	 * @param repository
	 * @param aFile
	 * @return
	 */
	protected <R extends FlexoResource<?>> RepositoryFolder<R> retrieveRepositoryFolder(ResourceRepository<R> repository, File aFile) {
		try {
			return repository.getRepositoryFolder(aFile, true);
		} catch (IOException e) {
			e.printStackTrace();
			return repository.getRootFolder();
		}
	}

	/**
	 * 
	 * Create a metamodel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter<?> resourceCenter) {
		return new EMFMetaModelRepository(this, resourceCenter);
	}

	/**
	 * 
	 * Create a model repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public EMFModelRepository createModelRepository(FlexoResourceCenter<?> resourceCenter) {
		return new EMFModelRepository(this, resourceCenter);
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
	 * Return flag indicating if supplied file represents a valid metamodel
	 * 
	 * @param aMetaModelFile
	 * @param technologyContextManager
	 * 
	 * @return
	 */
	public boolean isValidMetaModelFile(File aMetaModelFile, EMFTechnologyContextManager technologyContextManager) {
		return getEmfProperties(aMetaModelFile) != null;
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid meta model
	 * 
	 * @param aMetaModelFile
	 * @param technologyContextManager
	 * @return
	 */
	public String retrieveMetaModelURI(File aMetaModelFile, EMFTechnologyContextManager technologyContextManager) {
		return getEmfProperties(aMetaModelFile).getProperty("URI");
	}

	/**
	 * 
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveMetaModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public EMFMetaModelResource retrieveMetaModelResource(final File aMetaModelFile, EMFTechnologyContextManager emfContextManager) {
		EMFMetaModelResource metaModelResource = null;

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
					metaModelResource.setTechnologyAdapter(this);
					metaModelResource.setURI(uri);
					metaModelResource.setName(aMetaModelFile.getName());
					metaModelResource.setFile(aMetaModelFile);
					metaModelResource.setModelFileExtension(extension);
					metaModelResource.setPackageClassName(ePackageClassName);
					metaModelResource.setResourceFactoryClassName(resourceFactoryClassName);
					metaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
					emfContextManager.registerResource(metaModelResource);
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
	 * Return flag indicating if supplied file represents a valid model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public boolean isValidModelFile(File aModelFile, EMFMetaModelResource metaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		boolean valid = false;
		if (aModelFile.exists()) {
			// TODO syntaxic check and conformity to XMI
			if (aModelFile.getName().endsWith("." + metaModelResource.getModelFileExtension())) {
				if (aModelFile.isFile()) {
					valid = true;
				}
			}
		}
		return valid;
	}

	/**
	 * Retrieve and return URI for supplied model file
	 * 
	 * @param aModelFile
	 * @param technologyContextManager
	 * @return
	 */
	public String retrieveModelURI(File aModelFile, EMFMetaModelResource metaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		return retrieveModelResource(aModelFile, metaModelResource, technologyContextManager).getURI();
	}

	/**
	 * Instantiate new model resource stored in supplied model file, given the conformant metamodel<br>
	 * We assert here that model resource is conform to supplied metamodel, ie we will not try to lookup the metamodel but take the one
	 * which was supplied
	 * 
	 */
	public EMFModelResource retrieveModelResource(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		EMFModelResource emfModelResource = null;

		EMFTechnologyContextManager emfTechnologyContextManager = technologyContextManager;
		emfModelResource = emfTechnologyContextManager.getModel(aModelFile);

		if (emfModelResource == null) {
			emfModelResource = EMFModelResourceImpl.retrieveEMFModelResource(aModelFile, (EMFMetaModelResource) metaModelResource,
					technologyContextManager);
		}

		return emfModelResource;
	}

	/**
	 * Create Model Resource from file.
	 * 
	 * @param aModelFile
	 * @param emfMetaModelResource
	 * @param emfTechnologyContextManager
	 * @return
	 */
	/*protected EMFModelResource createModelResource(File aModelFile, EMFMetaModelResource emfMetaModelResource,
			EMFTechnologyContextManager emfTechnologyContextManager) {
		EMFModelResource emfModelResource = null;
		try {
			emfModelResource = new EMFModelResource(aModelFile, emfMetaModelResource, this, URI.createFileURI(aModelFile.getAbsolutePath())
					.toString());
			emfModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());

			emfTechnologyContextManager.registerModel(emfModelResource);
		} catch (InvalidFileNameException e) {
			e.printStackTrace();
		} catch (DuplicateResourceException e) {
			e.printStackTrace();
		}
		return emfModelResource;
	}*/

	/**
	 * Create empty EMFModel.
	 * 
	 * @param project
	 * @param filename
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public EMFModelResource createNewEMFModel(FlexoProject project, String filename, String modelUri,
			EMFMetaModelResource metaModelResource, EMFTechnologyContextManager technologyContextManager) {
		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		return createNewEMFModel(modelFile, modelUri, metaModelResource, technologyContextManager);
	}

	/**
	 * Create empty EMFModel.
	 * 
	 * @param modelFilePath
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public EMFModelResource createNewEMFModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, EMFMetaModelResource metaModelResource, EMFTechnologyContextManager technologyContextManager) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createNewEMFModel(modelFile, modelUri, metaModelResource, technologyContextManager);
	}

	/**
	 * Create empty EMFModel.
	 * 
	 * @param modelFile
	 * @param modelUri
	 * @param metaModelResource
	 * @param technologyContextManager
	 * @return
	 */
	public EMFModelResource createNewEMFModel(File modelFile, String modelUri, EMFMetaModelResource metaModelResource,
			EMFTechnologyContextManager technologyContextManager) {
		EMFMetaModelResource emfMetaModelResource = metaModelResource;
		EMFModelResource emfModelResource = EMFModelResourceImpl.makeEMFModelResource(modelUri, modelFile, emfMetaModelResource,
				technologyContextManager);
		technologyContextManager.registerResource(emfModelResource);
		try {
			emfModelResource.save(null);
		} catch (SaveResourceException e) {
			e.printStackTrace();
		}
		System.out.println("Created empty model " + modelFile.getAbsolutePath() + " as " + modelUri + " conform to "
				+ metaModelResource.getURI());
		return emfModelResource;
	}

	/**
	 * 
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createNewModelSlot(org.openflexo.foundation.viewpoint.ViewPoint)
	 */
	@Override
	public EMFTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new EMFTechnologyContextManager(this, service);
	}

	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass, VirtualModel<?> virtualModel) {
		if (EMFModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new EMFModelSlot(virtualModel, this);
		}
		logger.warning("Unexpected model slot: " + modelSlotClass.getName());
		return null;
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

	public String getExpectedModelExtension(EMFMetaModelResource metaModelResource) {
		return "." + metaModelResource.getModelFileExtension();
	}

}
