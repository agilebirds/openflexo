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
import org.openflexo.foundation.technologyadapter.TechnologyAdapterResource;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
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

public class XSDTechnologyAdapter extends TechnologyAdapter<XMLXSDModel, XSDMetaModel> {

	private static final String TAName = "XSD/XML technology adapter";

	protected static final Logger logger = Logger.getLogger(XSDTechnologyAdapter.class.getPackage().getName());

	private static final XSDBindingFactory BINDING_FACTORY = new XSDBindingFactory();

	public XSDTechnologyAdapter() throws TechnologyAdapterInitializationException {
	}

	@Override
	public String getName() {
		return TAName;
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
	public boolean isValidMetaModelFile(File aMetaModelFile, TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {
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
	public String retrieveMetaModelURI(File aMetaModelFile, TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		String s =  XSDMetaModel.findNamespaceURI(aMetaModelFile);

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
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

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
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		if (aModelFile.getName().endsWith(".xml")){
			String schemaURI = XMLXSDNameSpaceFinder.findNameSpace(aModelFile,false);

			String mmURI = metaModelResource.getURI();

			if (schemaURI.equals(metaModelResource.getURI())) {
				logger.info("Found a conformant XML Model File [" + schemaURI+"]" + aModelFile.getAbsolutePath() );
				return  !schemaURI.isEmpty();
			}
		}

		return false;

	}

	@Override
	public boolean isValidModelFile(File aModelFile, TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {
		if (aModelFile.getName().endsWith(".xml")){

			String schemaURI = XMLXSDNameSpaceFinder.findNameSpace(aModelFile,false);

			XSDMetaModelResource mm = (XSDMetaModelResource) technologyContextManager.getMetaModelWithURI(schemaURI);

			if (mm!=null){
				return true;
			}}
		return false;
	}

	/**
	 * Instantiate new meta model resource stored in supplied meta model file
	 * 
	 * @param aMetaModelFile
	 * @return
	 */
	@Override
	public XSDMetaModelResource retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) technologyContextManager;

		String uri = retrieveMetaModelURI(aMetaModelFile, xsdContextManager);

		XSDMetaModelResource xsdMetaModelResource =  (XSDMetaModelResource) xsdContextManager.getMetaModelWithURI(uri);

		if (xsdMetaModelResource == null) {

			xsdMetaModelResource = XSDMetaModelResourceImpl.makeXSDMetaModelResource(aMetaModelFile,
					retrieveMetaModelURI(aMetaModelFile, xsdContextManager), xsdContextManager );

			xsdMetaModelResource.setName(aMetaModelFile.getName());

			xsdMetaModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			xsdContextManager.registerMetaModel(xsdMetaModelResource);
		}

		return xsdMetaModelResource;
	}

	/**
	 * Follow the link.
	 * 
	 * @see org.openflexo.foundation.technologyadapter.TechnologyAdapter#retrieveModelResource(java.io.File,
	 *      org.openflexo.foundation.resource.FlexoResource, org.openflexo.foundation.technologyadapter.TechnologyContextManager)
	 */
	@Override
	public FlexoResource<XMLXSDModel>  retrieveModelResource(File aModelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		XSDTechnologyContextManager xsdContextManager = (XSDTechnologyContextManager) technologyContextManager;

		String uri = retrieveMetaModelURI(aModelFile, xsdContextManager);

		XMLXSDFileResource xmlModelResource = (XMLXSDFileResource) xsdContextManager.getModelWithURI(uri) ;

		// If there is no metaModel Resource, we can not parse the file
		if (xmlModelResource == null && metaModelResource != null) {

			xmlModelResource = XMLXSDFileResourceImpl.makeXMLXSDModelResource(uri, aModelFile,
					(XSDMetaModelResource) metaModelResource, xsdContextManager);

			xmlModelResource.setName(aModelFile.getName());

			xmlModelResource.setServiceManager(getTechnologyAdapterService().getServiceManager());
			xsdContextManager.registerModel(xmlModelResource);
		}

		return xmlModelResource;
	}

	@Override
	public XMLXSDFileResource retrieveModelResource(File aModelFile,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {


		for (TechnologyAdapterResource<XSDMetaModel> mmRes : technologyContextManager.getAllMetaModels()) {

			// If there is no metaModel Resource, we can not parse the file
			if (isValidModelFile(aModelFile, mmRes, technologyContextManager) && mmRes != null) {
				XMLXSDFileResource xmlModelResource = (XMLXSDFileResource) retrieveModelResource(aModelFile, mmRes, technologyContextManager);
				xmlModelResource.setMetaModelResource((XSDMetaModelResource) mmRes);
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
	public   XMLXSDFileResource  createEmptyModel(File modelFile, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		String modelUri = modelFile.toURI().toString();

		XMLXSDFileResource  ModelResource = XMLXSDFileResourceImpl.makeXMLXSDModelResource(modelUri, modelFile, (XSDMetaModelResource) metaModelResource, (XSDTechnologyContextManager) technologyContextManager);
		technologyContextManager.registerModel(ModelResource);

		return ModelResource;

	}

	@Override
	public  XMLXSDFileResource createEmptyModel(FlexoProject project, String filename, String modelUri, FlexoResource<XSDMetaModel> metaModel,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);

		// TODO: modelURI is not used here!!!! => check the API, as it is processed by TA
		logger.warning("modelURI are not useful in this context");

		return createEmptyModel(modelFile,  metaModel, technologyContextManager);

	}

	public FlexoResource<XMLXSDModel> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {
		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createEmptyModel(modelFile, metaModelResource, technologyContextManager);
	}


	@Override
	public FlexoResource<XMLXSDModel> createEmptyModel(FileSystemBasedResourceCenter resourceCenter, String relativePath, String filename,
			String modelUri, FlexoResource<XSDMetaModel> metaModelResource,
			TechnologyContextManager<XMLXSDModel, XSDMetaModel> technologyContextManager) {

		// TODO: modelURI is not used here!!!! => check the API, as it is processed by TA
		logger.warning("modelURI are not useful in this context");

		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);

		return createEmptyModel(modelFile, metaModelResource, technologyContextManager);
	}

	@Override
	public TechnologyContextManager<XMLXSDModel, XSDMetaModel> createTechnologyContextManager(FlexoResourceCenterService service) {
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
