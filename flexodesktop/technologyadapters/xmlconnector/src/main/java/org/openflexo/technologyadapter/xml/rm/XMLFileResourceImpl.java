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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.resource.FlexoFileResourceImpl;
import org.openflexo.foundation.resource.ResourceLoadingCancelledException;
import org.openflexo.foundation.rm.ResourceDependencyLoopException;
import org.openflexo.foundation.technologyadapter.FlexoModelResource;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.technologyadapter.xml.model.XMLTechnologyContextManager;
import org.openflexo.technologyadapter.xml.model.XMLType;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLModel;
import org.openflexo.toolbox.IProgress;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author xtof
 *
 */
public abstract class XMLFileResourceImpl extends FlexoFileResourceImpl<XMLModel> implements XMLFileResource {

	private XMLModel myModel;


	private boolean isLoaded = false;
	private boolean isLoading = false;

	/**
	 * Creates a new {@link XMLFileResource} asserting this is an explicit creation: no file is present on file system<br>
	 * This method should not be used to retrieve the resource from a file in the file system, use
	 * {@link #retrieveXMLModelResource(File xmlFile, XMLTechnologyContextManager technologyContextManager)} instead
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
			returned.myModel = new XMLModel(technologyContextManager.getTechnologyAdapter());
			returned.myModel.setResource(returned);
			return returned;
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public XMLModel getModel() {
		return myModel;
	}

	@Override
	public XMLModel getModelData() {

		if (!isLoaded()){
			try {
				loadResourceData(null);
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
		return myModel;
	}

	@Override
	public XMLModel loadResourceData(IProgress progress)
			throws ResourceLoadingCancelledException,
			ResourceDependencyLoopException, FileNotFoundException,
			FlexoException {

		// TODO Parsing du fichier !!

		if (!isLoaded) {

			isLoading = true;
			try {

				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();

				XMLHandler handler = new XMLHandler(this); 
				saxParser.parse(this.getFile(), handler);

			} catch (Exception e) {
				e.printStackTrace();
			}


			isLoading = false;
			isLoaded = true;
		}

		return myModel;
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

	private class XMLHandler extends DefaultHandler {

		private XMLModel containerModel;
		
		private XMLIndividual currentContainer = null;

		private Stack<XMLIndividual> parents = new Stack<XMLIndividual>();
		
		public XMLHandler(XMLFileResourceImpl xmlFileResourceImpl) {
			super();
			containerModel = xmlFileResourceImpl.getModel();
		}


		public void startElement (String uri, String localName,String qName, 
				Attributes attributes) throws SAXException {

			try {
				XMLType currentType = containerModel.getTypeNamed(qName);
				if ( currentType == null){
					currentType = new XMLType(qName, containerModel);
					containerModel.addType(qName,currentType);
				}
				
				XMLIndividual anIndividual = new XMLIndividual(containerModel,qName, attributes);
				
				if ( currentContainer == null) {
					containerModel.setRoot(anIndividual);
					currentContainer = anIndividual;
				}
				else {
					currentContainer.addChild(anIndividual);
				}
				parents.push(anIndividual);
				currentContainer = anIndividual;
				containerModel.addIndividual(anIndividual);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}

		public void endElement(String uri, String localName,
				String qName) throws SAXException {
			parents.pop();
			if (!parents.isEmpty()){
				currentContainer = parents.lastElement();
			}
			else currentContainer = null;
		}

		public void characters(char ch[], int start, int length) throws SAXException {

			
			
		}

	}


}

