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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * an XMLIndividual represents a single instance of XML Element in a XMLModel
 * 
 * 
 * @author xtof
 *
 */


public class XMLIndividual extends XMLObject implements IXMLIndividual<XMLIndividual,XMLAttribute> {


	/* Properties */

	private Map<XMLType,Set<XMLIndividual>> children = null;
	private Map<String, XMLAttribute> attributes = null;
	private IXMLIndividual<XMLIndividual,XMLAttribute> parent =null;
	private XMLModel containerModel = null;
	private XMLType myType = null;

	private String Name;
	private String uuid;


	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XMLIndividual.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * 
	 * @param adapter
	 */

	protected XMLIndividual(XMLModel containerModel) {
		super();
		this.containerModel = containerModel;
		uuid = UUID.randomUUID().toString();
		attributes = new HashMap<String, XMLAttribute>();
		children = new HashMap<XMLType, Set<XMLIndividual>>();
	}

	public XMLIndividual(XMLModel xmlModel, XMLType aType) {
		this.setName(aType.getName());
		this.containerModel = xmlModel;
		this.setType(aType);
		uuid = UUID.randomUUID().toString();
		attributes = new HashMap<String, XMLAttribute>();
		children = new HashMap<XMLType, Set<XMLIndividual>>();
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getContentDATA()
	 */
	@Override
	public String getContentDATA(){
		return (String) attributes.get(CDATA_ATTR_NAME).getValue();
	}

	//************ Accessors

	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getTechnologyAdapter()
	 */
	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		return containerModel.getTechnologyAdapter();
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.Name = name;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return Name;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getName()
	 */
	@Override
	public String getName() {
		return Name;
	}



	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getAttributeValue(java.lang.String)
	 */
	@Override
	public Object getAttributeValue(String attributeName) {

		XMLAttribute attr = attributes.get(attributeName);
		
		if (attr != null){
			return attr.getValue();
		}
		else return null;
	}

	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#addChild(org.openflexo.technologyadapter.xml.model.XMLIndividual)
	 */
	@Override
	public void addChild(IXMLIndividual<XMLIndividual,XMLAttribute> anIndividual) {
		XMLType aType = (XMLType) anIndividual.getType();
		Set<XMLIndividual> typedSet = children.get(aType);
		
		if (typedSet == null) {
			typedSet = new HashSet<XMLIndividual>();
			children.put(aType, typedSet);
		}
		typedSet.add((XMLIndividual) anIndividual);
		((XMLIndividual) anIndividual).setParent(this);
	}


	private void setParent(IXMLIndividual<XMLIndividual,XMLAttribute> xmlIndividual) {
		parent = xmlIndividual;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getChildren()
	 */
	@Override
	public Set<XMLIndividual> getChildren() {

		Set<XMLIndividual> returned = new HashSet<XMLIndividual>();
		
		for (Set<XMLIndividual> s : children.values()){
			returned.addAll(s);
		}
		return returned;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getParent()
	 */
	@Override
	public XMLIndividual getParent() {
		return (XMLIndividual) parent;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getType()
	 */
	@Override
	public XMLType getType() {
		return myType;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#setType(org.openflexo.technologyadapter.xml.model.XMLType)
	 */
	@Override
	public void setType(Type myClass) {
		this.myType = (XMLType) myClass;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getUUID()
	 */
	@Override
	public String getUUID() {
		return uuid;
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#getAttributes()
	 */
	@Override
	public Collection<? extends XMLAttribute> getAttributes() {
		return (Collection<? extends XMLAttribute>) attributes.values();
	}


	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#addAttribute(java.lang.String, org.openflexo.technologyadapter.xml.model.XMLAttribute)
	 */
	@Override
	public void addAttribute(String aName, XMLAttribute attr) {
		if (attributes == null){
			logger.warning("Attribute collection is null");
			attributes = new HashMap<String, XMLAttribute>();
		}
		attributes.put(aName,attr);
		
	}


	@Override
	public XMLAttribute getAttributeByName(String aName) {
		return attributes.get(aName);
	}
	
	/* (non-Javadoc)
	 * @see org.openflexo.technologyadapter.xml.model.IXMLIndividual#toXML(org.w3c.dom.Document)
	 */
	@Override
	public Element toXML(Document doc) {
		String nsURI = getType().getNameSpaceURI();
		Element element = null;
		if(nsURI != null){
			element = (Element) doc.createElementNS(nsURI, getType().getFullyQualifiedName());
		}
		else {
			element = (Element) doc.createElement(getType().getName());
		}
		
		for (IXMLIndividual<XMLIndividual,XMLAttribute> i : getChildren()){
			element.appendChild(i.toXML(doc));
		}

		// TODO dump attributes !!!
		
		return element;
	}


}
