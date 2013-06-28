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
package org.openflexo.technologyadapter.xsd.metamodel;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.xml.model.IXMLMetaModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;

import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;


public class XSDMetaModel extends XSOntology implements FlexoMetaModel<XSDMetaModel>,IXMLMetaModel {



	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDMetaModel.class.getPackage()
			.getName());


	private XSOntClass thingClass;



	public XSDMetaModel(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI,xsdFile,adapter);

		this.thingClass = new XSOntClass(this,"Thing", XS_THING_URI, getTechnologyAdapter());

		addClass(thingClass);
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setIsReadOnly(boolean b) {
	}

	public void save() throws SaveResourceException {
		logger.warning("XSDMetaModels are not supposed to be saved !!!");
	}


	@Override
	public List<IFlexoOntologyMetaModel> getMetaModels() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public FlexoResource<XSDMetaModel> getResource() {
		return (XSDMetaModelResource) modelResource;
	}

	@Override
	public void setResource(FlexoResource<XSDMetaModel> resource) {
		this.modelResource =  (XSDMetaModelResource) resource;
	}




	//*****************************************************************************
	// IXMLMetaModel related Functions


	@Override
	public Type getTypeFromURI(String uri) {
		return this.getClass(uri);
	}

	@Override
	public Type createNewType(String uri, String localName, String qName) {
		// TODO Auto-generated method stub
		return null;
	}

	//*****************************************************************************
	// IFlexoOntology related Functions


	@Override
	public List<XSOntDataProperty> getDataProperties() {
		return new ArrayList<XSOntDataProperty>(dataProperties.values());
	}

	@Override
	public List<XSOntDataProperty> getAccessibleDataProperties() {
		Map<String, XSOntDataProperty> result = new HashMap<String, XSOntDataProperty>();
		for (XSOntology o : getImportedOntologies()) {
			for (XSOntDataProperty c : ((XSDMetaModel) o).getDataProperties()) {
				result.put(c.getURI(), c);
			}
		}
		return new ArrayList<XSOntDataProperty>(result.values());
	}

	@Override
	public XSOntDataProperty getDataProperty(String propertyURI) {
		return dataProperties.get(propertyURI);
	}


	@Override
	public XSOntClass getRootConcept() {
		return thingClass;
	}

	//*****************************************************************************
	// Utility Functions when building the model

	private XSDDataType computeDataType(XSSimpleType simpleType) {
		XSDDataType returned = dataTypes.get(simpleType);
		if (returned == null) {
			returned = new XSDDataType(simpleType, this, getTechnologyAdapter());
			dataTypes.put(simpleType, returned);
		}
		return returned;
	}


	private boolean addClass(XSOntClass c) {
		if (classes.containsKey(c.getURI()) == false) {
			classes.put(c.getURI(), c);
			return true;
		}
		return false;
	}

	public XSOntClass createOntologyClass(String name, String uri) throws DuplicateURIException {
		XSOntClass xsClass = new XSOntClass(this, name, uri, getTechnologyAdapter());
		xsClass.addToSuperClasses(getRootConcept());
		addClass(xsClass);
		return xsClass;
	}

	public XSOntClass createOntologyClass(String name, String uri, XSOntClass superClass) throws DuplicateURIException {
		XSOntClass xsClass = createOntologyClass(name,uri);
		xsClass.addToSuperClasses(superClass);
		return xsClass;
	}


	public XSOntClass createOntologyClass(String name, XSOntClass superClass) throws DuplicateURIException {
		String uri = this.getURI() + "#" + "name";
		XSOntClass xsClass = createOntologyClass(name,uri, superClass);
		return xsClass;
	}


	public XSOntDataProperty createDataProperty(String name, String uri, XSType aType) {
		XSOntDataProperty xsDataProperty = new XSOntDataProperty(this, name, uri, getTechnologyAdapter());
		xsDataProperty.setDataType(computeDataType(aType.asSimpleType()));
		dataProperties.put(uri, xsDataProperty);
		return xsDataProperty;
	}

	public XSOntObjectProperty createObjectProperty(String name, String uri, XSOntClass c) {
		XSOntObjectProperty property = new XSOntObjectProperty(this, name, uri, getTechnologyAdapter());
		objectProperties.put(property.getURI(), property);
		property.newRangeFound(c);
		return property;
	}



}
