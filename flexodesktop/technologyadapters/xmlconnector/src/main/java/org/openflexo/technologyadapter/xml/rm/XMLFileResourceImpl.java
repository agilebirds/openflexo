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
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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
import org.openflexo.technologyadapter.xml.model.XMLType;
import org.openflexo.toolbox.IProgress;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 * @author xtof
 *
 */
public abstract class XMLFileResourceImpl extends FlexoFileResourceImpl<XMLModel> implements XMLFileResource {

	//Constants

	private static final String CDATA_TYPE_NAME = "CDATA";

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());

	// Properties

	private boolean isLoaded = false;
	private boolean isLoading = false;


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
			XMLFileResourceImpl returned = (XMLFileResourceImpl) factory.newInstance(XMLFileResource.class);
			returned.setName(xmlFile.getName());
			returned.setFile(xmlFile);
			returned.setURI(xmlFile.toURI().toString());
			returned.setServiceManager(technologyContextManager.getTechnologyAdapter().getTechnologyAdapterService().getServiceManager());
			returned.setTechnologyAdapter(technologyContextManager.getTechnologyAdapter());
			returned.setTechnologyContextManager(technologyContextManager);
			technologyContextManager.registerModel((FlexoModelResource<XMLModel, XMLModel>) returned);
			returned.setResourceData(new XMLModel(technologyContextManager.getTechnologyAdapter()));
			returned.getResourceData(null).setResource(returned);
			return returned;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.foundation.resource.FlexoResource#save(org.openflexo.toolbox.IProgress)
	 */

	@Override
	public void save(IProgress progress) throws SaveResourceException{

		File myFile = this.getFile();

		if (!myFile.exists()){
			//Creates a new file
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				throw new SaveResourceException(this);
			}
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

	private void writeToFile() throws SaveResourceException {

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			StreamResult result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance(
					"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(getModel().toXML());
			transformer.transform(source, result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} catch (TransformerException e) {
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
				return loadResourceData(null);
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
		return null;
	}

	@Override
	public XMLModel loadResourceData(IProgress progress)
			throws ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException,
			FlexoException {

		if (!isLoaded) {

			isLoading = true;
			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setNamespaceAware(true);
				SAXParser saxParser = factory.newSAXParser();


				XMLHandler handler = new XMLHandler(this); 
				saxParser.parse(this.getFile(), handler);

			} catch (Exception e) {
				e.printStackTrace();
			}


			isLoading = false;
			isLoaded = true;
		}

		return resourceData;
	}


	@Override
	public Class<XMLModel> getResourceDataClass() {
		return XMLModel.class;
	}


	// LifeCycle Management

	@Override
	public boolean isLoaded(){
		return isLoaded;
	}

	public boolean isLoading(){
		return isLoading;
	}

	// SAX Handler

	private class XMLHandler extends DefaultHandler2 {

		private XMLModel aXMLModel;

		private XMLIndividual currentContainer = null;
		private XMLIndividual currentIndividual = null;

		private StringBuffer cdataBuffer = new StringBuffer();

		private Stack<XMLIndividual> parents = new Stack<XMLIndividual>();

		public XMLHandler(XMLFileResourceImpl xmlFileResourceImpl) {
			super();
			aXMLModel = xmlFileResourceImpl.getModel();
		}


		public void startElement (String uri, String localName,String qName, 
				Attributes attributes) throws SAXException {


			try {
				// creates type on the Fly
				XMLType currentType = aXMLModel.getTypeFromURI(uri+"#"+"localName");
				if ( currentType == null){
					currentType = new XMLType(uri,localName,qName, aXMLModel);
					aXMLModel.addType(currentType);
				}

				// creates individual
				XMLIndividual anIndividual = aXMLModel.createIndividual(currentType);
				anIndividual.setType(currentType);
				currentIndividual = anIndividual;
				cdataBuffer.delete(0,cdataBuffer.length());

				// Attributes

				int len = attributes.getLength();

				for (int i=0 ; i<len ; i++ ) {

					Type aType = null;
					String typeName = attributes.getType(i);
					String attrQName = attributes.getQName(i);
					String attrLName = attributes.getLocalName(i);
					String attrURI= attributes.getURI(i);

					aType = aXMLModel.getTypeFromURI(attrURI+"#"+attrLName);

					if (typeName.equals(CDATA_TYPE_NAME)){
						aType = String.class;
						XMLAttribute attr = new XMLAttribute (attrLName, aType , attributes.getValue(i));
						anIndividual.addAttribute(attr.getName(),attr);
					}
					else {
						if (aType == null){
							logger.warning("XMLIndividual: cannot find a type for " + typeName + " - falling back to String");
							aType = String.class;
						}

						XMLAttribute attr = new XMLAttribute (attributes.getLocalName(i), aType , attributes.getValue(i));
						anIndividual.addAttribute(attr.getName(),attr);
					}

				}


				if ( currentContainer == null) {
					aXMLModel.setRoot(anIndividual);
					currentContainer = anIndividual;
				}
				else {
					currentContainer.addChild(anIndividual);
				}
				parents.push(anIndividual);
				currentContainer = anIndividual;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		public void endElement(String uri, String localName,
				String qName) throws SAXException {

			// CDATA allocation

			if (currentIndividual != null && cdataBuffer.length() >0){
				String str = cdataBuffer.toString().trim();

				XMLAttribute attr = new XMLAttribute (XMLIndividual.CDATA_ATTR_NAME, String.class ,str);
				currentIndividual.addAttribute(XMLIndividual.CDATA_ATTR_NAME, attr);
			}

			// node stack management

			parents.pop();
			if (!parents.isEmpty()){
				currentContainer = parents.lastElement();
			}
			else currentContainer = null;

		}



		public void characters(char ch[], int start, int length) throws SAXException {

			if (currentIndividual != null && length >0){
				cdataBuffer.append(ch,start,length);
			}
			else {
				logger.warning("No currentIndividual to allocate DATA to");
			}
		}

	}


}

