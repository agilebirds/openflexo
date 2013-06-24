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

import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.ontology.IFlexoOntologyObject;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;


public class XMLType extends FlexoObject implements IFlexoOntologyObject {

	
	private XMLModel containerModel;
	
	/* Properties */
	
	private String Name;
	

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XMLType.class
			.getPackage().getName());

	/**
	 * Default Constructor
	 * @param qName 
	 * 
	 * @param adapter
	 */

	public XMLType(String qName, XMLModel model) {
		super();
		this.containerModel = model;
		try {
			this.setName(qName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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



	@Override
	public TechnologyAdapter<?, ?> getTechnologyAdapter() {
		// TODO Auto-generated method stub
		return containerModel.getTechnologyAdapter();
	}


}
