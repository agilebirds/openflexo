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

package org.openflexo.technologyadapter.xsd;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntClass;
import org.openflexo.technologyadapter.xsd.metamodel.XSOntProperty;
import org.openflexo.technologyadapter.xsd.model.AbstractXSOntObject;
import org.openflexo.technologyadapter.xsd.model.XSOntFeatureAssociation;
import org.openflexo.technologyadapter.xsd.model.XSOntIndividual;
import org.openflexo.technologyadapter.xsd.model.XSOntology;
import org.openflexo.technologyadapter.xsd.model.XSPropertyValue;
import org.openflexo.technologyadapter.xsd.rm.XSDMetaModelResource;
import org.openflexo.xmlcode.XMLSerializable;



/* Correct processing of XML Objects URIs needs to add an internal class to store
 * for each XSOntClass wich are the XML Elements (attributes or CDATA, or...) that will be 
 * used to calculate URIs
 */

// TODO Manage the fact that URI May Change

public class XSURIProcessor implements XMLSerializable {

	// mapping styles enumeration

	public static final String ATTRIBUTE_VALUE = "attribute";

	// Properties actually used to calculate URis

	private XSOntClass mappedClass;
	private XSOntProperty baseAttributeForURI;
	private XSDModelSlot modelSlot;
	// Cache des URis Pour aller plus vite ??
	// TODO some optimization required
	private Map<String, AbstractXSOntObject> uriCache = new HashMap<String, AbstractXSOntObject>();


	public void setModelSlot(XSDModelSlot xsdModelSlot) {
		modelSlot = xsdModelSlot;
	}

	// Serialized properties

	URI typeURI;
	private String mappingStyle;
	private String attributeName;

	public String _getTypeURI() {
		if (mappedClass != null) {
			return mappedClass.getURI();
		} else {
			this.bindtypeURIToMappedClass();
			return typeURI.toString();
		}
	}

	public void _setTypeURI(String name) {
		typeURI = URI.create(name);
		bindtypeURIToMappedClass();
	}

	public String _getMappingStyle() {
		return mappingStyle;
	}

	public void _setMappingStyle(String mappingStyle) {
		this.mappingStyle = mappingStyle;
	}

	public String _getAttributeName() {
		return attributeName;
	}

	public void _setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	// Lifecycle management methods
	public void reset() {
		modelSlot = null;
		mappedClass = null;
		mappingStyle = null;
		baseAttributeForURI = null;
	}

	public void bindtypeURIToMappedClass() {
		if (modelSlot != null) {
			String mmURI = modelSlot.getMetaModelURI();
			if (mmURI != null) {
				XSDMetaModelResource mmResource = (XSDMetaModelResource) modelSlot.getMetaModelResource();
				if (mmResource != null) {
					mappedClass = mmResource.getMetaModelData().getClass(typeURI.toString());
					if (attributeName != null) {
						baseAttributeForURI = (XSOntProperty) mappedClass.getPropertyByName(attributeName);
					}
				} else {
					XSDModelSlot.logger.warning("unable to map typeURI to an OntClass, as metaModelResource is Null ");
				}
			} else
				mappedClass = null;
		} else {
			XSDModelSlot.logger.warning("unable to map typeURI to an OntClass, as modelSlot is Null ");
		}
	}

	public XSURIProcessor() {
		super();
	}

	public XSURIProcessor(String typeURI) {
		super();
		this.typeURI = URI.create(typeURI);
	}

	// URI Calculation

	public String getURIForObject(ModelSlotInstance msInstance, AbstractXSOntObject xsO) {
		String builtURI = null;
		StringBuffer completeURIStr = new StringBuffer();

		// if processor not initialized
		if (mappedClass == null) {
			bindtypeURIToMappedClass();
		}
		// processor should be initialized
		if (mappedClass == null) {
			XSDModelSlot.logger.warning("Cannot process URI as URIProcessor is not initialized for that class: " + typeURI);
			return null;
		} else {
			if (mappingStyle.equals(ATTRIBUTE_VALUE) && attributeName != null) {

				XSOntProperty aProperty = mappedClass.getPropertyByName(attributeName);
				XSPropertyValue value = ((XSOntIndividual) xsO).getPropertyValue(aProperty);
				try {
					builtURI = URLEncoder.encode(value.toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					XSDModelSlot.logger.warning("Cannot process URI - Unexpected encoding error");
					e.printStackTrace();
				}
			} else{
				XSDModelSlot.logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
			}
		}

		if (builtURI != null){
			if(uriCache.get(builtURI) == null){
				// TODO Manage the fact that URI May Change
				uriCache.put(builtURI, xsO);
			}
		}
		completeURIStr.append(typeURI.getScheme()).append("://").append(typeURI.getHost() ).append(typeURI.getPath()).append("?").append(builtURI).append("#").append(typeURI.getFragment());
		return  completeURIStr.toString();
	}

	// get the Object given the URI
	public Object retrieveObjectWithURI(ModelSlotInstance msInstance, String objectURI) {

		AbstractXSOntObject o = uriCache.get(objectURI);

		// if processor not initialized
		if (mappedClass == null) {
			bindtypeURIToMappedClass();
		}

		// retrieve object
		if (o == null) {

			if (mappingStyle.equals(ATTRIBUTE_VALUE) && attributeName != null) {

				for (XSOntIndividual obj: ((XSOntology) msInstance.getModel()).getIndividualsOfClass(mappedClass)){

					XSOntProperty aProperty = mappedClass.getPropertyByName(attributeName);
					XSPropertyValue value = ((XSOntIndividual) obj).getPropertyValue(aProperty);
					try {
						if (value.equals(URLDecoder.decode(objectURI,"UTF-8"))){
							return obj;
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} else{
				XSDModelSlot.logger.warning("Cannot process URI - Unexpected or Unspecified mapping parameters");
			}
		}

		return o;
	}


	// get the right URIProcessor for URI

	static public String retrieveTypeURI(ModelSlotInstance msInstance, String objectURI){

		URI fullURI;
		StringBuffer typeURIStr = new StringBuffer();

		fullURI = URI.create(objectURI);
		typeURIStr.append(fullURI.getScheme()).append("://").append(fullURI.getHost() ).append(fullURI.getPath()).append("#").append(fullURI.getFragment());

		return typeURIStr.toString();
	}

}