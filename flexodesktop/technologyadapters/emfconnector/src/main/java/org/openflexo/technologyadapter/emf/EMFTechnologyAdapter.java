/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
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
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
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
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelRepository;
import org.openflexo.technologyadapter.emf.rm.EMFMetaModelResource;
import org.openflexo.technologyadapter.emf.rm.EMFModelRepository;
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
@DeclareModelSlot(FML = "EMFModelSlot", modelSlotClass = EMFModelSlot.class), // Classical type-safe interpretation
// Removed because it is unusable
// @DeclareModelSlot(FML = "EMFMetaModelSlot", modelSlotClass = EMFMetaModelSlot.class) // Classical type-safe interpretation
})
@DeclareRepositoryType({ EMFMetaModelRepository.class, EMFModelRepository.class })
public class EMFTechnologyAdapter extends TechnologyAdapter {

	protected static final Logger logger = Logger.getLogger(EMFTechnologyAdapter.class.getPackage().getName());

	private static final EMFBindingFactory BINDING_FACTORY = new EMFBindingFactory();

	// Static references to ECORE properties

	private static String ECORE_MM_NAME = "Ecore Metamodel";
	private static String ECORE_MM_URI = "http://www.eclipse.org/emf/2002/Ecore";
	private static String ECORE_MM_EXT = "ecore";
	private static String ECORE_MM_PKGCLSNAME = EcorePackageImpl.class.getName();
	private static String ECORE_MM_FACTORYCLSNAME = EcoreResourceFactoryImpl.class.getName();
	private EMFMetaModelResource ecoreMetaModelResource = null;

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
	 * Creates and return a new {@link ModelSlot} of supplied class.<br>
	 * This responsability is delegated to the {@link TechnologyAdapter} which manages with introspection its own {@link ModelSlot} types
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass, VirtualModel virtualModel) {
		if (EMFModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new EMFModelSlot(virtualModel, this);
		} else if (EMFMetaModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new EMFMetaModelSlot(virtualModel, this);
		}
		logger.warning("Unexpected model slot: " + modelSlotClass.getName());
		return null;
	}

	/**
	 * Initialize the supplied resource center with the technology<br>
	 * ResourceCenter is scanned, ResourceRepositories are created and new technology-specific resources are build and registered.
	 * 
	 * @param resourceCenter
	 */
	@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

		EMFTechnologyContextManager technologyContextManager = (EMFTechnologyContextManager) getTechnologyAdapterService()
				.getTechnologyContextManager(this);

		// A single MM Repository for all ResourceCenters

		EMFMetaModelRepository mmRepository = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
		if (mmRepository == null) {
			try {
				mmRepository = createMetaModelRepository(resourceCenter);
			} catch (ModelDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		EMFModelRepository modelRepository = resourceCenter.getRepository(EMFModelRepository.class, this);
		if (modelRepository == null) {
			modelRepository = createModelRepository(resourceCenter);
		}

		// First pass on meta-models only
		Iterator<I> it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				EMFMetaModelResource mmRes = tryToLookupMetaModel(resourceCenter, candidateFile);
			}
		}

		// Second pass on models
		it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				EMFModelResource mRes = tryToLookupModel(resourceCenter, candidateFile);
			}
		}
	}

	protected EMFMetaModelResource tryToLookupMetaModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		EMFTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		if (isValidMetaModelFile(candidateFile)) {
			EMFMetaModelResource mmRes = retrieveMetaModelResource(candidateFile);
			EMFMetaModelRepository mmRepo = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
			if (mmRes != null) {
				RepositoryFolder<EMFMetaModelResource> folder;
				try {
					folder = mmRepo.getRepositoryFolder(candidateFile, true);
					mmRepo.registerResource(mmRes, folder);
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
				return mmRes;
			}
		}
		return null;
	}

	protected EMFModelResource tryToLookupModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		EMFTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		EMFMetaModelRepository mmRepository = resourceCenter.getRepository(EMFMetaModelRepository.class, this);
		EMFModelRepository modelRepository = resourceCenter.getRepository(EMFModelRepository.class, this);

		List<FlexoResourceCenter> rscCenters = technologyContextManager.getResourceCenterService().getResourceCenters();

		for (FlexoResourceCenter<?> rscCenter : rscCenters) {
			mmRepository = rscCenter.getRepository(EMFMetaModelRepository.class, this);
			if (mmRepository != null) {

				for (EMFMetaModelResource mmRes : mmRepository.getAllResources()) {
					if (isValidModelFile(candidateFile, mmRes)) {
						EMFModelResource mRes = retrieveModelResource(candidateFile, mmRes);
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
							return mRes;
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter, I contents) {
		return false;
	}

	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			File candidateFile = (File) contents;
			if (tryToLookupMetaModel(resourceCenter, candidateFile) != null) {
				// This is a meta-model, this one has just been registered
			} else {
				tryToLookupModel(resourceCenter, candidateFile);
			}
		}
	}

	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter, I contents) {
		if (contents instanceof File) {
			System.out
					.println("File DELETED " + ((File) contents).getName() + " in " + ((File) contents).getParentFile().getAbsolutePath());
		}
	}

	/**
	 * 
	 * Create a metamodel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @throws ModelDefinitionException
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public EMFMetaModelRepository createMetaModelRepository(FlexoResourceCenter<?> resourceCenter) throws ModelDefinitionException {

		EMFMetaModelRepository mmRepository = new EMFMetaModelRepository(this, resourceCenter);
		if (ecoreMetaModelResource == null) {
			// register ecore MM in every resource center
			ModelFactory factory = new ModelFactory(EMFMetaModelResource.class);
			ecoreMetaModelResource = factory.newInstance(EMFMetaModelResource.class);
			ecoreMetaModelResource.setTechnologyAdapter(this);
			ecoreMetaModelResource.setURI(ECORE_MM_URI);
			ecoreMetaModelResource.setName(ECORE_MM_NAME);
			ecoreMetaModelResource.setFile(null);
			ecoreMetaModelResource.setModelFileExtension(ECORE_MM_EXT);
			ecoreMetaModelResource.setPackageClassName(ECORE_MM_PKGCLSNAME);
			ecoreMetaModelResource.setResourceFactoryClassName(ECORE_MM_FACTORYCLSNAME);
			ecoreMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			getTechnologyContextManager().registerResource(ecoreMetaModelResource);
		} else {

			RepositoryFolder<EMFMetaModelResource> folder;
			folder = mmRepository.getRootFolder();
			mmRepository.registerResource(ecoreMetaModelResource, folder);
		}
		resourceCenter.registerRepository(mmRepository, EMFMetaModelRepository.class, this);

		return mmRepository;
	}

	/**
	 * 
	 * Create a model repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public EMFModelRepository createModelRepository(FlexoResourceCenter<?> resourceCenter) {
		EMFModelRepository returned = new EMFModelRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, EMFModelRepository.class, this);
		return returned;
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
	public boolean isValidMetaModelFile(File aMetaModelFile) {
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
	public EMFMetaModelResource retrieveMetaModelResource(final File aMetaModelFile) {
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
					getTechnologyContextManager().registerResource(metaModelResource);
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
	public boolean isValidModelFile(File aModelFile, EMFMetaModelResource metaModelResource) {
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
	public String retrieveModelURI(File aModelFile, EMFMetaModelResource metaModelResource) {
		return retrieveModelResource(aModelFile, metaModelResource).getURI();
	}

	/**
	 * Instantiate new model resource stored in supplied model file, given the conformant metamodel<br>
	 * We assert here that model resource is conform to supplied metamodel, ie we will not try to lookup the metamodel but take the one
	 * which was supplied
	 * 
	 */
	public EMFModelResource retrieveModelResource(File aModelFile, FlexoResource<EMFMetaModel> metaModelResource) {
		EMFModelResource emfModelResource = null;

		emfModelResource = getTechnologyContextManager().getModel(aModelFile);

		if (emfModelResource == null) {
			emfModelResource = EMFModelResourceImpl.retrieveEMFModelResource(aModelFile, (EMFMetaModelResource) metaModelResource,
					getTechnologyContextManager());
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
	public EMFModelResource createNewEMFModel(FlexoProject project, String filename, String modelUri, EMFMetaModelResource metaModelResource) {
		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		return createNewEMFModel(modelFile, modelUri, metaModelResource);
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
			String modelUri, EMFMetaModelResource metaModelResource) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createNewEMFModel(modelFile, modelUri, metaModelResource);
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
	public EMFModelResource createNewEMFModel(File modelFile, String modelUri, EMFMetaModelResource metaModelResource) {
		EMFMetaModelResource emfMetaModelResource = metaModelResource;
		EMFModelResource emfModelResource = EMFModelResourceImpl.makeEMFModelResource(modelUri, modelFile, emfMetaModelResource,
				getTechnologyContextManager());
		getTechnologyContextManager().registerResource(emfModelResource);
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
	public EMFTechnologyContextManager getTechnologyContextManager() {
		return (EMFTechnologyContextManager) super.getTechnologyContextManager();
	}

	@Override
	public EMFBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	public String getExpectedModelExtension(EMFMetaModelResource metaModelResource) {
		if (metaModelResource != null) {
			return "." + metaModelResource.getModelFileExtension();
		}
		return null;
	}

}
