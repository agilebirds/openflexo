/*
 * (c) Copyright 2010-2012 AgileBirds
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
package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.DeclareModelSlot;
import org.openflexo.foundation.technologyadapter.DeclareModelSlots;
import org.openflexo.foundation.technologyadapter.DeclareRepositoryType;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XMLXSDModel;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.rm.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResourceImpl;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDNameSpaceFinder;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResourceImpl;
import org.openflexo.technologyadapter.xsd.viewpoint.binding.XSDBindingFactory;

/**
 * This class defines and implements the XSD/XML technology adapter
 * 
 * @author sylvain, luka, Christophe
 * 
 */


@DeclareModelSlots({ // ModelSlot(s) declaration
	@DeclareModelSlot(FML = "XSDModelSlot", modelSlotClass = XSDModelSlot.class) // Classical type-safe interpretation
})
@DeclareRepositoryType({ XSDMetaModelRepository.class, XMLModelRepository.class })

public class XSDTechnologyAdapter extends TechnologyAdapter {
	private static final String TAName = "XSD/XML technology adapter";

	protected static final Logger logger = Logger.getLogger(XSDTechnologyAdapter.class.getPackage().getName());

	private static final XSDBindingFactory BINDING_FACTORY = new XSDBindingFactory();

	public XSDTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return TAName;
	}

	/**
	 * Creates and return a new {@link ModelSlot} of supplied class.<br>
	 * This responsability is delegated to the {@link TechnologyAdapter} which manages with introspection its own {@link ModelSlot} types
	 * 
	 * @param modelSlotClass
	 * @return
	 */
	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass, VirtualModel<?> virtualModel) {
		if (XSDModelSlot.class.isAssignableFrom(modelSlotClass)) {
			return (MS) new XSDModelSlot(virtualModel, this);
		}
		logger.warning("Unexpected model slot: " + modelSlotClass.getName());
		return null;
	}

	/**
	 * Return the {@link TechnologyContextManager} for this technology shared by all {@link FlexoResourceCenter} declared in the scope of
	 * {@link FlexoResourceCenterService}
	 * 
	 * @return
	 */
	@Override
	public XSDTechnologyContextManager getTechnologyContextManager() {
		return (XSDTechnologyContextManager) super.getTechnologyContextManager();
	}

	/**
	 * Initialize the supplied resource center with the technology<br>
	 * ResourceCenter is scanned, ResourceRepositories are created and new technology-specific resources are build and registered.
	 * 
	 * @param resourceCenter
	 */
	@Override
	public <I> void initializeResourceCenter(FlexoResourceCenter<I> resourceCenter) {

		XSDTechnologyContextManager technologyContextManager = (XSDTechnologyContextManager) getTechnologyAdapterService()
				.getTechnologyContextManager(this);

		XSDMetaModelRepository mmRepository = resourceCenter.getRepository(XSDMetaModelRepository.class, this);

		if (mmRepository == null) {
			mmRepository = createMetaModelRepository(resourceCenter);
		}

		XMLModelRepository modelRepository = resourceCenter.getRepository(XMLModelRepository.class, this);
		if (modelRepository == null) {
			modelRepository = createModelRepository(resourceCenter);
		}

		// First pass on meta-models only
		Iterator<I> it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				XSDMetaModelResource mmRes = tryToLookupMetaModel(resourceCenter, candidateFile);
			}
		}

		// Second pass on models
		it = resourceCenter.iterator();

		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;
				XMLXSDFileResource mRes = tryToLookupModel(resourceCenter, candidateFile);
			}
		}
	}

	protected XSDMetaModelResource tryToLookupMetaModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		if (isValidMetaModelFile(candidateFile)) {
			XSDMetaModelResource mmRes = retrieveMetaModelResource(candidateFile);
			XSDMetaModelRepository mmRepository = resourceCenter.getRepository(XSDMetaModelRepository.class, this);
			if (mmRes != null) {
				RepositoryFolder<XSDMetaModelResource> folder;
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
				return mmRes;
			}
		}
		return null;
	}

	protected XMLXSDFileResource tryToLookupModel(FlexoResourceCenter<?> resourceCenter, File candidateFile) {
		XSDTechnologyContextManager technologyContextManager = getTechnologyContextManager();
		XSDMetaModelRepository mmRepository = resourceCenter.getRepository(XSDMetaModelRepository.class, this);
		XMLModelRepository modelRepository = resourceCenter.getRepository(XMLModelRepository.class, this);
		for (XSDMetaModelResource mmRes : mmRepository.getAllResources()) {
			if (isValidModelFile(candidateFile, mmRes, technologyContextManager)) {
				XMLXSDFileResource mRes = (XMLXSDFileResource) retrieveModelResource(candidateFile, mmRes);
				if (mRes != null) {
					RepositoryFolder<XMLXSDFileResource> folder;
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
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createMetaModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public XSDMetaModelRepository createMetaModelRepository(FlexoResourceCenter<?> resourceCenter) {
		XSDMetaModelRepository returned = new XSDMetaModelRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, XSDMetaModelRepository.class, this);
		return returned;
	}

	/**
	 * 
	 * Create a model repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#createModelRepository(org.openflexo.foundation.resource.FlexoResourceCenter)
	 */
	public XMLModelRepository createModelRepository(FlexoResourceCenter<?> resourceCenter) {
		XMLModelRepository returned = new XMLModelRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, XMLModelRepository.class, this);
		return returned;
	}


	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public boolean isValidMetaModelFile(File aMetaModelFile) {
		// TODO: also check that file is valid and maps a valid XSD schema

		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".xsd");
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public String retrieveMetaModelURI(File aMetaModelFile) {

		String s =  XSOntology.findNamespaceURI(aMetaModelFile);
		return s;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	public String retrieveModelURI(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {


		return aModelFile.toURI().toString();
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	public boolean isValidModelFile(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {

		if (aModelFile.getName().endsWith(".xml")){
			String schemaURI = XMLXSDNameSpaceFinder.findNameSpace(aModelFile);

			String mmURI = metaModelResource.getURI();

			if (schemaURI.equals(metaModelResource.getURI())) {
				logger.info("Found a conformant XML Model File [" + schemaURI+"]" + aModelFile.getAbsolutePath() );
				return  !schemaURI.isEmpty();
			}
		}

		return false;
	}

	public boolean isValidModelFile(File aModelFile, TechnologyContextManager technologyContextManager) {
		if (aModelFile.getName().endsWith(".xml")){

			String schemaURI = XMLXSDNameSpaceFinder.findNameSpace(aModelFile);

			// FIXME: Check if this is ok!
			XSDMetaModelResource mm = (XSDMetaModelResource) technologyContextManager.getResourceWithURI(schemaURI);

			if (mm!=null){
				return true;
			}
		}
		return false;
	}

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public XSDMetaModelResource retrieveMetaModelResource(File aMetaModelFile) {

		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) getTechnologyContextManager();

		String uri = retrieveMetaModelURI(aMetaModelFile);

		XSDMetaModelResource xsdMetaModelResource =  (XSDMetaModelResource) xsdContextManager.getResourceWithURI(uri);

		if (xsdMetaModelResource == null) {

			xsdMetaModelResource = XSDMetaModelResourceImpl.makeXSDMetaModelResource(aMetaModelFile,
					uri, xsdContextManager );

			xsdMetaModelResource.setName(aMetaModelFile.getName());

			xsdMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			xsdContextManager.registerResource(xsdMetaModelResource);
		}

		return xsdMetaModelResource;
	}

	/**
	 * Instantiate new model resource stored in supplied model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	public FlexoResource<XMLXSDModel>  retrieveModelResource(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource) {

		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) getTechnologyContextManager();

		String uri = retrieveMetaModelURI(aModelFile);

		XMLXSDFileResource XMLXSDFileResource = (XMLXSDFileResource) xsdContextManager.getResourceWithURI(uri) ;

		// If there is no metaModel Resource, we can not parse the file
		if (XMLXSDFileResource == null && metaModelResource != null) {

			XMLXSDFileResource = XMLXSDFileResourceImpl.makeXMLXSDFileResource(uri, aModelFile,
					(XSDMetaModelResource) metaModelResource, xsdContextManager);

			XMLXSDFileResource.setName(aModelFile.getName());

			XMLXSDFileResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			xsdContextManager.registerResource(XMLXSDFileResource);
		}

		return XMLXSDFileResource;
	}

	public XMLXSDFileResource retrieveModelResource(File aModelFile) {

		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) getTechnologyContextManager();

		for (FlexoResourceCenter rc : xsdContextManager.getResourceCenterService().getResourceCenters()) {


			ResourceRepository<FlexoFileResource<?>> mmRepository = rc.getRepository(XSDMetaModelRepository.class, xsdContextManager.getTechnologyAdapter());

			if (rc != null ){
				for (FlexoFileResource<?> mmRes : mmRepository.getAllResources()) {

					// If there is no metaModel Resource, we can not parse the file
					if (isValidModelFile(aModelFile, (FlexoResource<XSDMetaModel>) mmRes, xsdContextManager) && mmRes != null) {
						XMLXSDFileResource xmlXSDFileResource = (XMLXSDFileResource) retrieveModelResource(aModelFile, (FlexoResource<XSDMetaModel>) mmRes);
						xmlXSDFileResource.setMetaModelResource((XSDMetaModelResource) mmRes);
						xsdContextManager.registerResource(xmlXSDFileResource);

						return  xmlXSDFileResource;
					}
				}
			}
		}
		return null;

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
	public XMLXSDFileResource createNewXMLFile(File modelFile, String modelUri, FlexoResource<XSDMetaModel> metaModelResource) {

		modelUri = modelFile.toURI().toString();

		XMLXSDFileResource modelResource = XMLXSDFileResourceImpl.makeXMLXSDFileResource(modelUri, modelFile,
				(XSDMetaModelResource) metaModelResource, getTechnologyContextManager());


		getTechnologyContextManager().registerResource(modelResource);

		return modelResource;

	}

	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	public  XMLXSDFileResource createNewXMLFile(FlexoProject project, String filename, String modelUri, FlexoResource<XSDMetaModel> metaModel) {


		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);

		// TODO: modelURI is not used here!!!! => check the API, as it is processed by TA
		logger.warning("modelURI are not useful in this context");

		return createNewXMLFile(modelFile, modelUri, metaModel);

	}

	public FlexoResource<XMLXSDModel> createNewXMLFile(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			FlexoResource<XSDMetaModel> metaModelResource) {

		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);


		String modelUri = modelFile.toURI().toString();

		return createNewXMLFile(modelFile, modelUri,metaModelResource);
	}


	public FlexoResource<XMLXSDModel> createNewXMLFile(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<XSDMetaModel> metaModelResource) {

		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);

		return createNewXMLFile(modelFile,modelUri, metaModelResource);
	}

	@Override
	public XSDTechnologyContextManager createTechnologyContextManager(FlexoResourceCenterService service) {
		return new XSDTechnologyContextManager(this, service);
	}


	@Override
	public XSDBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	public String getExpectedMetaModelExtension() {
		return ".xsd";
	}

	public String getExpectedModelExtension(FlexoResource<XSDMetaModel> metaModel) {
		return ".xml";
	}

}
