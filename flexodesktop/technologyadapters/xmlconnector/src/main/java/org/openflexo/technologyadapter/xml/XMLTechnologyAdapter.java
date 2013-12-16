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

package org.openflexo.technologyadapter.xml;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.resource.RepositoryFolder;
import org.openflexo.foundation.resource.ResourceRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.technologyadapter.xml.rm.XMLFileResourceImpl;
import org.openflexo.technologyadapter.xml.rm.XMLMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
/**
 * @author xtof
 *
 */
public class XMLTechnologyAdapter extends TechnologyAdapter {

	private static final String TAName = "XML technology adapter";
	private static final String XML_EXTENSION = ".xml";

	protected static final Logger logger = Logger.getLogger(XMLTechnologyAdapter.class.getPackage().getName());

	@Override
	public String getName() {
		return TAName;
	}

	public boolean isValidMetaModelFile(File aMetaModelFile,
			TechnologyContextManager technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return false;
	}

	public String retrieveMetaModelURI(File aMetaModelFile,
			TechnologyContextManager technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return null;
	}

	public FlexoResource<XMLModel> retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return null;
	}

	public boolean isValidModelFile(File aModelFile,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {

		return isValidModelFile(aModelFile);

	}

	public boolean isValidModelFile(File aModelFile) {
		if ( aModelFile.exists() &&  aModelFile.getName().endsWith(XML_EXTENSION)) return true;
		else return false;
	}

	public String retrieveModelURI(File aModelFile,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {
		return aModelFile.toURI().toString();
	}

	public XMLFileResource retrieveModelResource(File aModelFile) {

		XMLFileResource xmlModelResource = XMLFileResourceImpl.makeXMLFileResource(aModelFile, (XMLTechnologyContextManager) getTechnologyContextManager());

		return xmlModelResource;
	}

	/**
	 * Create empty model.
	 * 
	 * @param modelFile
	 * @param modelUri
	 * @param technologyContextManager
	 * @return
	 */
	public  XMLFileResource createEmptyModel(File modelFile, 
			TechnologyContextManager technologyContextManager) {

		XMLFileResource ModelResource = XMLFileResourceImpl.makeXMLFileResource(modelFile, (XMLTechnologyContextManager) technologyContextManager);
		technologyContextManager.registerResource((FlexoModelResource<XMLModel, XMLModel>) ModelResource);
		return ModelResource;

	}

	public XMLFileResource createEmptyModel(
			FileSystemBasedResourceCenter resourceCenter, String relativePath,
			String filename, String modelUri,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {		

		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createEmptyModel(modelFile, technologyContextManager);
	}

	public XMLFileResource createEmptyModel(FlexoProject project,
			String filename, String modelUri,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager technologyContextManager) {

		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);

		return createEmptyModel(modelFile, technologyContextManager);
	}


	@Override
	public TechnologyContextManager createTechnologyContextManager(
			FlexoResourceCenterService service) {

		return new XMLTechnologyContextManager(this, service);
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getExpectedModelExtension(FlexoResource<XMLModel> metaModel) {
		return XML_EXTENSION;
	}


	@Override
	public <I> void initializeResourceCenter(
			FlexoResourceCenter<I> resourceCenter) {

		XMLModelRepository mRepository = resourceCenter.getRepository(XMLModelRepository.class, this);
		if (mRepository == null) {
			mRepository = createXMLModelRepository(resourceCenter);
		}

		XMLMetaModelRepository mmRepository = resourceCenter.getRepository(XMLMetaModelRepository.class, this);
		if (mmRepository == null) {
			mmRepository = createXMLMetaModelRepository(resourceCenter);
		}

		Iterator<I> it = resourceCenter.iterator();
		
		while (it.hasNext()) {
			I item = it.next();
			if (item instanceof File) {
				File candidateFile = (File) item;

				if (isValidModelFile(candidateFile)) {
					XMLFileResource res = this.retrieveModelResource(candidateFile);
					XMLModelRepository repository = resourceCenter.getRepository(XMLModelRepository.class, this);

					if (res != null) {
						RepositoryFolder<XMLFileResource> folder;
						try {
							folder = repository.getRepositoryFolder(candidateFile, true);
							repository.registerResource(res, folder);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// Also register the resource in the ResourceCenter seen as a ResourceRepository
						if (resourceCenter instanceof ResourceRepository) {
							try {
								((ResourceRepository) resourceCenter).registerResource(res,
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



	@Override
	public <I> boolean isIgnorable(FlexoResourceCenter<I> resourceCenter,
			I contents) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <I> void contentsAdded(FlexoResourceCenter<I> resourceCenter,
			I contents) {
		// TODO Auto-generated method stub

	}

	@Override
	public <I> void contentsDeleted(FlexoResourceCenter<I> resourceCenter,
			I contents) {
		// TODO Auto-generated method stub

	}

	@Override
	public <MS extends ModelSlot<?>> MS makeModelSlot(Class<MS> modelSlotClass,
			VirtualModel virtualModel) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * Create a XMLModel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 */
	public XMLModelRepository createXMLModelRepository(FlexoResourceCenter<?> resourceCenter) {
		XMLModelRepository returned = new XMLModelRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, XMLModelRepository.class, this);
		return returned;
	}


	/**
	 * 
	 * Create a XMLModel repository for current {@link TechnologyAdapter} and supplied {@link FlexoResourceCenter}
	 * 
	 */
	public XMLMetaModelRepository createXMLMetaModelRepository(FlexoResourceCenter<?> resourceCenter) {
		XMLMetaModelRepository returned = new XMLMetaModelRepository(this, resourceCenter);
		resourceCenter.registerRepository(returned, XMLMetaModelRepository.class, this);
		return returned;
	}


}
