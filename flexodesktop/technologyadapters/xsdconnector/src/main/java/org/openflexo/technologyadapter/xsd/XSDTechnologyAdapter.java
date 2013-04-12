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
package org.openflexo.technologyadapter.xsd;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoFileResource;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.FlexoMetaModelResource;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterInitializationException;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xsd.model.XMLModel;
import org.openflexo.technologyadapter.xsd.model.XMLModelRepository;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.model.XSDMetaModelRepository;
import org.openflexo.technologyadapter.xsd.model.XSDTechnologyContextManager;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.rm.XMLModelResource;
import org.openflexo.technologyadapter.xsd.rm.XMLModelResourceImpl;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.technologyadapter.xsd.viewpoint.XSDBindingFactory;

/**
 * This class defines and implements the XSD/XML technology adapter
 * 
 * @author sylvain, luka, Christophe
 * 
 */

public class XSDTechnologyAdapter extends TechnologyAdapter<XMLModel, XSDMetaModel> {

	private static final String CatalogFileNames = "catalog-v0.ofcat";
	
	protected static final Logger logger = Logger.getLogger(XSDTechnologyAdapter.class.getPackage().getName());

	private static final XSDBindingFactory BINDING_FACTORY = new XSDBindingFactory();

	public XSDTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return "XSD/XML technology adapter";
	}

	@Override
	public XSDModelSlot createNewModelSlot(ViewPoint viewPoint) {
		return new XSDModelSlot(viewPoint, this);
	}

	@Override
	public XSDModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		return new XSDModelSlot(virtualModel, this);
	}

	/**
	 * Return flag indicating if supplied file represents a valid XSD schema
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		// TODO: also check that file is valid and maps a valid XSD schema
		return aMetaModelFile.isFile() && aMetaModelFile.getName().endsWith(".xsd");
	}

	/**
	 * Retrieve and return URI for supplied meta model file, if supplied file represents a valid XSD meta model
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		String s =  XSOntology.findOntologyURI(aMetaModelFile);
		
		return s;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelURI(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public String retrieveModelURI(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		
		// TODO Manage URIs properly
		
		logger.warning("xsdconnector: Have to deal properly with URIs");
		
		return aModelFile.toURI().toString();
	}

	/**
	 * Return flag indicating if supplied file represents a valid XML model conform to supplied meta-model
	 * 
	 * @param aModelFile
	 * @param metaModel
	 * @return
	 */
	@Override
	public boolean isValidModelFile(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		// TODO: also check that file is valid and maps the valid XSD represented by the metamodel
		return aModelFile.getName().endsWith(".xml");
	}

	@Override
	public boolean isValidModelFile(File aModelFile, TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		// TODO: also check that file is valid and maps a valid XSD
		return aModelFile.getName().endsWith(".xml");
	}

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public XSDMetaModelResource retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		XSDMetaModelResource xsdMetaModelResource = makeXSDMetaModelResource(aMetaModelFile,
				retrieveMetaModelURI(aMetaModelFile, technologyContextManager));
		xsdMetaModelResource.setName(aMetaModelFile.getName());
		xsdMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) technologyContextManager;
		xsdContextManager.registerMetaModel(xsdMetaModelResource);
		return xsdMetaModelResource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public XMLModelResource retrieveModelResource(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		
		XMLModelResource xmlModelResource = XMLModelResourceImpl.retrieveXMLModelResource(aModelFile,
				(XSDMetaModelResource) metaModelResource, getTechnologyContextManager());
		
		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) technologyContextManager;
		
		xsdContextManager.registerModel(xmlModelResource);
		
		return xmlModelResource;
	}

	@Override
	public XMLModelResource retrieveModelResource(File aModelFile,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		
		for (FlexoMetaModelResource<XMLModel, XSDMetaModel> mmRes : technologyContextManager.getAllMetaModels()) {
			if (isValidModelFile(aModelFile, mmRes, technologyContextManager)) {
				
				XMLModelResource xmlModelResource = (XMLModelResource) retrieveModelResource(aModelFile, mmRes, technologyContextManager);
				xmlModelResource.setMetaModelResource(mmRes);
				technologyContextManager.registerModel(xmlModelResource);
				
				return  xmlModelResource;
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
	public  XMLModelResource createEmptyModel(File modelFile, String modelUri, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {

		logger.warning("xsdconnector: URI not supported ");
		
		modelUri = modelFile.toURI().toString();

		XMLModelResource ModelResource = XMLModelResourceImpl.makeXMLModelResource(modelUri, modelFile, (XSDMetaModelResource) metaModelResource, (XSDTechnologyContextManager) technologyContextManager);
		technologyContextManager.registerModel(ModelResource);
		return ModelResource;

	}
		
	/**
	 * Creates new model conform to the supplied meta model
	 * 
	 * @param project
	 * @param metaModel
	 * @return
	 */
	@Override
	public XMLModelResource createEmptyModel(FlexoProject project, String filename, String modelUri, FlexoResource<XSDMetaModel> metaModel,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		
		
		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);
		
		return createEmptyModel(modelFile, modelUri, metaModel, technologyContextManager);
		
	}


	@Override
	public FlexoResource<XMLModel> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLModel, XSDMetaModel> technologyContextManager) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createEmptyModel(modelFile, modelUri, metaModelResource, technologyContextManager);
	}

	@Override
	public TechnologyContextManager<XMLModel, XSDMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
		return new XSDTechnologyContextManager(this, service);
	}

	@Override
	public XMLModelRepository createModelRepository(FlexoResourceCenter resourceCenter) {
		return new XMLModelRepository(this, resourceCenter);
	}

	@Override
	public XSDMetaModelRepository createMetaModelRepository(FlexoResourceCenter resourceCenter) {
		return new XSDMetaModelRepository(this, resourceCenter);
	}

	@Override
	public XSDTechnologyContextManager getTechnologyContextManager() {
		return (XSDTechnologyContextManager) super.getTechnologyContextManager();
	}

	protected XSDMetaModelResource makeXSDMetaModelResource(File xsdMetaModelFile, String uri) {
		try {
			ModelFactory factory = new ModelFactory(XSDMetaModelResource.class);
			XSDMetaModelResource returned = factory.newInstance(XSDMetaModelResource.class);
			returned.setTechnologyAdapter(this);
			returned.setURI(uri);
			returned.setName("Unnamed");
			returned.setFile(xsdMetaModelFile);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public XSDBindingFactory getTechnologyAdapterBindingFactory() {
		return BINDING_FACTORY;
	}

	@Override
	public String getExpectedMetaModelExtension() {
		return ".xsd";
	}

	@Override
	public String getExpectedModelExtension(FlexoResource<XSDMetaModel> metaModel) {
		return ".xml";
	}

}
