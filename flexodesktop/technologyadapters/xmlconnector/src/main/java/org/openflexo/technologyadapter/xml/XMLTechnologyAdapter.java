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
import java.util.logging.Logger;

import org.openflexo.foundation.resource.FileSystemBasedResourceCenter;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.resource.FlexoResourceCenter;
import org.openflexo.foundation.resource.FlexoResourceCenterService;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.foundation.technologyadapter.MetaModelRepository;
import org.openflexo.foundation.technologyadapter.ModelRepository;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.technologyadapter.TechnologyContextManager;
import org.openflexo.foundation.viewpoint.ViewPoint;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.openflexo.technologyadapter.xml.rm.XMLFileResourceImpl;
import org.openflexo.technologyadapter.xml.rm.XMLMetaModelRepository;
import org.openflexo.technologyadapter.xml.rm.XMLModelRepository;
/**
 * @author xtof
 *
 */
public class XMLTechnologyAdapter extends TechnologyAdapter<XMLModel, XMLModel> {

	private static final String TAName = "XML technology adapter";
	private static final String XML_EXTENSION = ".xml";

	protected static final Logger logger = Logger.getLogger(XMLTechnologyAdapter.class.getPackage().getName());

	@Override
	public String getName() {
		return TAName;
	}

	@Override
	public XMLModelSlot createNewModelSlot(ViewPoint viewPoint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XMLModelSlot createNewModelSlot(VirtualModel<?> virtualModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidMetaModelFile(File aMetaModelFile,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return false;
	}

	@Override
	public String retrieveMetaModelURI(File aMetaModelFile,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return null;
	}

	@Override
	public FlexoResource<XMLModel> retrieveMetaModelResource(File aMetaModelFile,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {
		// No MetaModel in this connector
		// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
		return null;
	}

	@Override
	public boolean isValidModelFile(File aModelFile,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {

		return isValidModelFile(aModelFile, technologyContextManager);

	}

	@Override
	public boolean isValidModelFile(File aModelFile,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {

		if ( aModelFile.exists() &&  aModelFile.getName().endsWith(XML_EXTENSION)) return true;
		else return false;
	}

	@Override
	public String retrieveModelURI(File aModelFile,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {
		return aModelFile.toURI().toString();
	}

	@Override
	public XMLFileResource retrieveModelResource(File aModelFile,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {


		XMLFileResource xmlModelResource = XMLFileResourceImpl.makeXMLFileResource(aModelFile, (XMLTechnologyContextManager) getTechnologyContextManager());

		XMLTechnologyContextManager xmlContextManager = (XMLTechnologyContextManager) technologyContextManager;

		xmlContextManager.registerModel((FlexoModelResource<XMLModel, XMLModel>) xmlModelResource);

		return xmlModelResource;
	}

	@Override
	public XMLFileResource retrieveModelResource(File aModelFile,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {
		return  retrieveModelResource(aModelFile,technologyContextManager);
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
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {

		XMLFileResource ModelResource = XMLFileResourceImpl.makeXMLFileResource(modelFile, (XMLTechnologyContextManager) technologyContextManager);
		technologyContextManager.registerModel((FlexoModelResource<XMLModel, XMLModel>) ModelResource);
		return ModelResource;

	}

	@Override
	public XMLFileResource createEmptyModel(
			FileSystemBasedResourceCenter resourceCenter, String relativePath,
			String filename, String modelUri,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {		

		File modelDirectory = new File(resourceCenter.getRootDirectory(), relativePath);
		File modelFile = new File(modelDirectory, filename);
		return createEmptyModel(modelFile, technologyContextManager);
	}

	@Override
	public XMLFileResource createEmptyModel(FlexoProject project,
			String filename, String modelUri,
			FlexoResource<XMLModel> metaModelResource,
			TechnologyContextManager<XMLModel, XMLModel> technologyContextManager) {

		File modelFile = new File(FlexoProject.getProjectSpecificModelsDirectory(project), filename);

		return createEmptyModel(modelFile, technologyContextManager);
	}


	@Override
	public TechnologyContextManager<XMLModel, XMLModel> createTechnologyContextManager(
			FlexoResourceCenterService service) {

		return new XMLTechnologyContextManager(this, service);
	}

	@Override
	public TechnologyAdapterBindingFactory getTechnologyAdapterBindingFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedMetaModelExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpectedModelExtension(FlexoResource<XMLModel> metaModel) {
		return XML_EXTENSION;
	}

	@Override
	public <R extends FlexoResource<? extends XMLModel>> ModelRepository<R, XMLModel, XMLModel, ? extends TechnologyAdapter<XMLModel, XMLModel>> createModelRepository(
			FlexoResourceCenter resourceCenter) {
		return (ModelRepository<R, XMLModel, XMLModel, ? extends TechnologyAdapter<XMLModel, XMLModel>>) new XMLModelRepository(this, resourceCenter);
	}

	@Override
	public <R extends FlexoResource<? extends XMLModel>> MetaModelRepository<R, XMLModel, XMLModel, ? extends TechnologyAdapter<XMLModel, XMLModel>> createMetaModelRepository(
			FlexoResourceCenter resourceCenter) {
			// logger.warning("NO MetaModel exists for XMLTechnologyAdapter");
			return (MetaModelRepository<R, XMLModel, XMLModel, ? extends TechnologyAdapter<XMLModel, XMLModel>>) new XMLMetaModelRepository(this, resourceCenter);
	}


}
