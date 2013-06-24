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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.FlexoModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.openflexo.technologyadapter.xml.XMLTechnologyAdapter;
import org.openflexo.technologyadapter.xml.rm.XMLFileResource;

/**
 * @author xtof
 *
 */
public class XMLModel extends FlexoObject implements FlexoModel<XMLModel, XMLModel>, FlexoMetaModel<XMLModel>  {


	protected static final Logger logger = Logger.getLogger(XMLModel.class.getPackage().getName());
	private XMLFileResource xmlResource;
	private boolean isReadOnly = true;
	private XMLTechnologyAdapter technologyAdapter;

	private Map<String, XMLIndividual> individuals;
	private Map<String, XMLType> types;
	private XMLIndividual root = null;
	
	public XMLModel(TechnologyAdapter<?, ?> technologyAdapter) {
		super();
		individuals = new HashMap<String, XMLIndividual>();
		types = new HashMap<String, XMLType>();
		this.technologyAdapter = (XMLTechnologyAdapter) technologyAdapter;
	}

	/**
	 * @return the rootNode
	 */
	public XMLIndividual getRoot() {
		return root;
	}


	/**
	 * @return the rootNode
	 */
	public void setRoot(XMLIndividual indiv) {
		root = indiv;
	}

	@Override
	public XMLFileResource getResource() {
		return xmlResource;
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


	
	public Collection<XMLIndividual> getIndividuals() {
		return individuals.values();
	}

	public void addIndividual(XMLIndividual anIndividual) {
		individuals.put(anIndividual.getURI(), anIndividual);

	}

	public XMLType getTypeNamed(String qName) {
		return types.get(qName);
	}

	public void addType(String name, XMLType aType) {
	types.put(name, aType);
		
	}



}
