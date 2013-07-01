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
package org.openflexo.technologyadapter.xsd.model;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.technologyadapter.xml.model.IXMLModel;
import org.openflexo.technologyadapter.xml.model.IXMLIndividual;
import org.openflexo.technologyadapter.xml.model.XMLAttribute;
import org.openflexo.technologyadapter.xml.model.XMLIndividual;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.metamodel.XSDMetaModel;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntDataProperty;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.rm.XMLXSDFileResource;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLXSDModel extends XSOntology implements FlexoModel<XMLXSDModel, XSDMetaModel>, IXMLModel {

	private XSDMetaModel metaModel= null;
	private XSOntIndividual rootElem = null;

	protected static final Logger logger = Logger.getLogger(XMLXSDModel.class.getPackage().getName());

	public XMLXSDModel(String ontologyURI, File xmlFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI, xmlFile, adapter);
		uri = xmlFile.toURI().toString();
	}

	public XMLXSDModel(String ontologyURI, File xmlFile, XSDTechnologyAdapter adapter, XSDMetaModel mm) {
		super(ontologyURI, xmlFile, adapter);
		metaModel = mm;
		uri = xmlFile.toURI().toString();
	}


	public void setMetaModel(XSDMetaModel metaModelData) {
		this.metaModel = metaModelData;
		setChanged();

	}


	@Override
	public XSDMetaModel getMetaModel() {
		return metaModel;
	}

	@Override
	public List<IFlexoOntologyMetaModel> getMetaModels() {
		List<IFlexoOntologyMetaModel> list = new ArrayList<IFlexoOntologyMetaModel>();
		list.add((IFlexoOntologyMetaModel) metaModel);
		return list;
	}


	@Override
	public Object addNewIndividual(Type aType) {

		XSOntIndividual indiv = this.createOntologyIndividual((XSOntClass) aType);
		individuals.put(indiv.getUUID(),indiv);
		setChanged();
		return indiv;

	}

	@Override
	public void setRoot(IXMLIndividual<?,?> anIndividual) {
		rootElem = (XSOntIndividual) anIndividual;
		setChanged();

	}


	/**
	 * @return the rootNode
	 */
	public IXMLIndividual<XSOntIndividual,XSOntProperty> getRoot() {
		return (IXMLIndividual<XSOntIndividual,XSOntProperty>) rootElem;
	}


	@Override
	public FlexoResource<XMLXSDModel> getResource() {
		return (XMLXSDFileResource) modelResource;
	}



	public void setResource(FlexoResource<XMLXSDModel> resource) {
		this.modelResource = (XMLXSDFileResource) resource;
	}


	public void save() throws SaveResourceException {
		getResource().save(null);
	}

	@Override
	public List<? extends IFlexoOntologyDataProperty> getAccessibleDataProperties() {
		// Those should only be available for MetaModels
		return Collections.emptyList();
	}

	@Override
	public IFlexoOntologyDataProperty getDataProperty(String propertyURI) {
		return null;
	}

	@Override
	public List<? extends IFlexoOntologyDataProperty> getDataProperties() {
		// Those should only be available for MetaModels
		return Collections.emptyList();
	}


	public Document toXML() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		XSOntIndividual rootIndiv = (XSOntIndividual) getRoot();

		if (rootIndiv != null ){
			Element rootNode = rootIndiv.toXML(doc);
			doc.appendChild( rootNode );
		}
		return doc;
	}


}
