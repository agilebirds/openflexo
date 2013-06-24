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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 * 
 * an XMLIndividual represents a single instance of XML Element
 * 
 * 
 * @author xtof
 *
 */


public class XMLIndividual extends FlexoObject implements IFlexoOntologyObject {

	
	// TODO : check if this is actually useful ?!?
	private Set<XMLIndividual> children = new HashSet<XMLIndividual>();
	private Map<String, XMLAttribute> values = new HashMap<String, XMLAttribute>();
	private XMLIndividual parent;
	private XMLModel containerModel;
	private XMLType myType;
	
	/* Properties */
	
	private String Name;
	

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XMLIndividual.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * 
	 * @param adapter
	 */

	protected XMLIndividual(XMLModel model) {
		super();
		this.containerModel = model;
	}


	public XMLIndividual(XMLModel containerModel, String name,
			Attributes attributes) throws Exception {

		this.setName(name);
		this.containerModel = containerModel;
		// TODO create AttributeProperties
		// TODO set Type
		// TODO calculate URIs
	}



	protected Element toXML(Document doc) {
		Element element = doc.createElement(Name);
		return element;
	}


	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return containerModel.getTechnologyAdapter();
	}


	public void setName(String name) throws Exception {
			this.Name = name;
	}


	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return Name;
	}


	@Override
	public String getName() {
		return Name;
	}


	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return Name;
	}


	public void addChild(XMLIndividual anIndividual) {
		children.add(anIndividual);
	}


	public Set<XMLIndividual> getChildren() {
		return children;
	}


	/**
	 * @return the Type of this element
	 */
	public XMLType getType() {
		return myType;
	}


	/**
	 * @param set the type of this Element
	 */
	public void setType(XMLType myClass) {
		this.myType = myClass;
	}

}
