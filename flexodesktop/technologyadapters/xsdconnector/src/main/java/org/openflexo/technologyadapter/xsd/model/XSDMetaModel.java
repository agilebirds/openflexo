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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.foundation.ontology.IFlexoOntologyConcept;
import org.openflexo.foundation.ontology.IFlexoOntologyMetaModel;
import org.openflexo.foundation.rm.DuplicateResourceException;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.SaveResourceException;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.technologyadapter.xsd.XSDTechnologyAdapter;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.xmlcode.StringEncoder.Converter;

import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;


public class XSDMetaModel extends XSOntology implements FlexoMetaModel<XSDMetaModel> {


	
	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(XSDMetaModel.class.getPackage()
			.getName());


	public XSDMetaModel(String ontologyURI, File xsdFile, XSDTechnologyAdapter adapter) {
		super(ontologyURI,xsdFile,adapter);
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


	// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		public XSDMetaModelResource getFlexoResource() {
			return (XSDMetaModelResource) modelResource;
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		public void setFlexoResource(@SuppressWarnings("rawtypes") FlexoResource resource) throws DuplicateResourceException {
			if (resource instanceof XSDMetaModelResource) {
				this.modelResource = (XSDMetaModelResource) resource;
			}
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		@Override
		public org.openflexo.foundation.resource.FlexoResource<XSDMetaModel> getResource() {
			return getFlexoResource();
		}

		// TODO: we need to temporarily keep both pairs or methods getFlexoResource()/getResource() and setFlexoResource()/setResource() until
		// old implementation and new implementation of FlexoResource will be merged. To keep backward compatibility with former implementation
		// of ResourceManager, we have to deal with that. This should be fixed early 2013 (sylvain)
		@Override
		public void setResource(org.openflexo.foundation.resource.FlexoResource<XSDMetaModel> resource) {
			try {
				setFlexoResource((FlexoResource<?>) resource);
			} catch (DuplicateResourceException e) {
				e.printStackTrace();
			}
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
