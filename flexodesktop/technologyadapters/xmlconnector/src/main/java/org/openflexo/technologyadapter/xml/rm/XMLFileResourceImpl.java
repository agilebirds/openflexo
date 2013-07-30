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

package org.openflexo.technologyadapter.xml.rm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.io.IOUtils;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.FlexoFileResource.FileWritingLock;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.rm.SaveResourcePermissionDeniedException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xml.model.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.toolbox.IProgress;


/**
 * @author xtof
 * 
 */
public abstract class XMLFileResourceImpl extends
FlexoFileResourceImpl<XMLModel> implements XMLFileResource {

	// Constants

	static final String CDATA_TYPE_NAME = "CDATA";

	protected static final Logger logger = Logger.getLogger(XMLFileResourceImpl.class
			.getPackage().getName());

	// Properties 

	private boolean isLoaded = false;


	/**
	 * 
	 * @param modelURI
	 * @param xmlFile
	 * @param technologyContextManager
	 * @return
	 */
	public static XMLFileResource makeXMLFileResource(File xmlFile,
			XMLTechnologyContextManager technologyContextManager) {
		try {
			ModelFactory factory = new ModelFactory(XMLFileResource.class);
			XMLFileResourceImpl returned = (XMLFileResourceImpl) factory
					.newInstance(XMLFileResource.class);
			returned.setName(xmlFile.getName());
			returned.setFile(xmlFile);
			returned.setURI(xmlFile.toURI().toString());
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);

			technologyContextManager.registerResource((FlexoModelResource<XMLModel, XMLModel>) returned);

			// FIXME : comment ça marche le resource Manager?
			// test pour créer le fichier si jamais il n'existe pas

			if(!xmlFile.exists()){

				if (returned.resourceData == null){
					returned.resourceData = new XMLModel(technologyContextManager.getTechnologyAdapter());
					returned.resourceData.setResource(returned);
				}

				returned.save(null);
				returned.isLoaded = true;
			}

			return returned;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openflexo.foundation.resource.FlexoResource#save(org.openflexo.toolbox
	 * .IProgress)
	 */

	@Override
	public void save(IProgress progress) throws SaveResourceException {

		File myFile = this.getFile();

		if (!myFile.exists()) {
			// Creates a new file
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SaveResourceException(this);
			}
		}

		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : "
						+ getFile().getAbsolutePath());
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
				logger.info("Succeeding to save Resource " + getURI() + " : "
						+ getFile().getName());
			}
		}

	}

	/**
	 * URI here is the full path to the file
	 */
	@Override
	public String getURI() {
		return this.getFile().toURI().toString();
	}


	private void writeToFile() throws SaveResourceException {

		FileWriter out = null;
		try {
			out = new FileWriter(getFile());
			XMLWriter<XMLFileResource, XMLModel> writer = new XMLWriter<XMLFileResource, XMLModel>(this , out);

			writer.writeDocument();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			IOUtils.closeQuietly(out);
		}
		logger.info("Wrote " + getFile());
	}

	@Override
	public XMLModel getModel() {
		return resourceData;
	}

	@Override
	public XMLModel getModelData() {
		if (!isLoaded()){
			try {
				resourceData = loadResourceData(null);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceLoadingCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceDependencyLoopException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FlexoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resourceData;
	}

	@Override
	public XMLModel loadResourceData(IProgress progress)
			throws ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException,

			FlexoException {

		if (resourceData == null){
			resourceData = new XMLModel(this.getTechnologyAdapter());
			resourceData.setResource(this);
		}

		if (!isLoaded()) {

			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				factory.setXIncludeAware(true);
				factory.setFeature("http://xml.org/sax/features/namespace-prefixes",true);
				SAXParser saxParser = factory.newSAXParser();

				XMLReaderSAXHandler<XMLModel, XMLModel, XMLIndividual, XMLAttribute> handler = new XMLReaderSAXHandler<XMLModel, XMLModel, XMLIndividual, XMLAttribute>(
						this, true);
				saxParser.parse(this.getFile(), handler);

				isLoaded = true;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return resourceData;
	}

	@Override
	public Class<XMLModel> getResourceDataClass() {
		return XMLModel.class;

	}

	// Lifecycle Management


	public boolean isLoaded() {
		return isLoaded;
	}


}
