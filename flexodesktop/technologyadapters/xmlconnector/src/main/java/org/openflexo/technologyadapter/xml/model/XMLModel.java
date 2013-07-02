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

package org.openflexo.technologyadapter.xml.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author xtof
 *
 */
public class XMLModel extends FlexoObject implements FlexoModel<XMLModel, XMLModel>, FlexoMetaModel<XMLModel>, IXMLMetaModel, IXMLModel  {

	// Constants
	
	private static final String Version = "0";
	
	// Attributes
	

	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());
	private FlexoResource<?> xmlResource;
	private boolean isReadOnly = true;
	private XMLTechnologyAdapter technologyAdapter;

	private Map<String, XMLIndividual> individuals;
	private Map<String, XMLType> types;
	private IXMLIndividual<?, ?> root = null;
	
	private String namespaceURI = null;
	private String namespacePrefix = null;
	
	public XMLModel(TechnologyAdapter<?, ?> technologyAdapter) {
		super();
		individuals = new HashMap<String, XMLIndividual>();
		types = new HashMap<String, XMLType>();
		this.technologyAdapter = (XMLTechnologyAdapter) technologyAdapter;
	}
	
	@Override
	public void setNamespace(String uri, String prefix){
		this.namespacePrefix = prefix;
		this.namespaceURI = uri;
	}
	
	@Override
	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	@Override
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * @return the rootNode
	 */
	public IXMLIndividual<XMLIndividual, XMLAttribute> getRoot() {
		return (IXMLIndividual<XMLIndividual, XMLAttribute>) root;
	}


	/**
	 * @return the rootNode
	 */
	public void setRoot(IXMLIndividual<?,?> indiv) {
		root = indiv;
		setChanged();
	}

	@Override
	public XMLFileResource getResource() {
		return (XMLFileResource) xmlResource;
	}

	@Override
	public void setResource(FlexoResource<XMLModel> resource) {
		this.xmlResource = (XMLFileResource) resource;
	}

	@Override
	public boolean isReadOnly() {
		return isReadOnly;
	}

	@Override
	public void setIsReadOnly(boolean b) {
		isReadOnly = b;

	}

	@Override
	public XMLModel getMetaModel() {
		logger.info("Basic XML Files have no such thing as a MetaModel, resource plays the role of Model and MetaModel");
		return this;
	}

	@Override
	public String getURI() {
		return xmlResource.getURI();
	}

	@Override
	public Object getObject(String objectURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return technologyAdapter;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<? extends XMLIndividual> getIndividuals() {
		return new ArrayList<XMLIndividual>(individuals.values());
	}



	// TODO, TO BE OPTIMIZED
	public List<XMLIndividual> getIndividualsOfType(XMLType aType) {
		ArrayList<XMLIndividual> returned = new ArrayList<XMLIndividual>();
		for (XMLIndividual o : individuals.values()){
			if (o.getType() == aType) {
				returned.add(o);
			}
		}
		return returned;
	}

	@Override
	public Object addNewIndividual(Type aType) {
		XMLIndividual anIndividual = new XMLIndividual(this, (XMLType) aType);
		this.addIndividual(anIndividual);
		return anIndividual;
	}


	public void addIndividual(XMLIndividual anIndividual) {
		individuals.put(anIndividual.getUUID(), anIndividual);
		this.setChanged();

	}


	@Override
	public XMLType createNewType(String uri, String localName, String qName) {
		XMLType nType = new XMLType(uri,localName,qName, this);
		this.addType(nType);
		setChanged();
		return nType;
	}
	
	public XMLType getTypeFromURI(String uri) {
		return types.get(uri);
	}

	public void addType(XMLType aType) {
	types.put(aType.getURI(), aType);
	this.setChanged();
	}

	public List<? extends XMLType> getTypes() {
		return new ArrayList<XMLType>(types.values());
	}
	
	public Document toXML() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		XMLIndividual rootIndiv = (XMLIndividual) getRoot();

		if (rootIndiv != null ){
			Element rootNode = rootIndiv.toXML(doc);
			doc.appendChild( rootNode );
		}

		return doc;
	}



}
