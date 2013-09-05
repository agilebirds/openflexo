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

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;


public class XMLType extends XMLObject implements Type {

	
	private XMLModel containerModel;
	
	/* Properties */
	
	private String Name;
	private String NameSpaceURI;
	private String NSPrefix;


	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XMLType.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * @param qName 
	 * 
	 * @param adapter
	 */

	public XMLType(String aName, XMLModel model) {
		super();
		this.containerModel = model;
			this.Name = aName;
			this.NameSpaceURI = null;
			this.NSPrefix = null;
	}


	public XMLType(String nsURI, String lName, String qName, XMLModel model) {
		super();
		this.containerModel = model;
			Name = lName;
			NameSpaceURI = nsURI; 
			NSPrefix = qName.replaceAll(":"+lName, "");
	}


	public void setName(String name) throws Exception {
			this.Name = name;
	}


	@Override
	public String getFullyQualifiedName() {
		if (NameSpaceURI != null && !NameSpaceURI.isEmpty())	return NSPrefix +":"+ Name;
		else return Name;
	}


	public String getName() {
		return Name;
	}


	public String getNameSpaceURI() {
		return NameSpaceURI;
	}

	public void setNameSpaceURI(String nameSpaceURI) {
		NameSpaceURI = nameSpaceURI;
	}



	public String getURI() {
		if (NameSpaceURI != null) {
		return NameSpaceURI + "#" + Name;
		}
		else {
			return Name;
		}
	}



	public TechnologyAdapter getTechnologyAdapter() {
		return containerModel.getTechnologyAdapter();
	}



}
